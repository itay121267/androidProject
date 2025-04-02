package com.example.project;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends BasicActivity {
    TextView levelCount;
    String  userInputText = "None";
    ProgressBar progressBar;
    Button btnChange, btnDefault;
    ImageButton buttonLeash, buttonFood, buttonSponge;
    LottieAnimationView leashAnimation, foodAnimation, spongeAnimation;
    ImageView dog;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // השמה
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        buttonFood = findViewById(R.id.btnFood);
        buttonSponge = findViewById(R.id.btnSponge);
        buttonLeash = findViewById(R.id.btnLeash);
        levelCount = findViewById(R.id.levelCount);
        progressBar = findViewById(R.id.levelBar);
        dog = findViewById(R.id.dog);
        btnChange = findViewById(R.id.btnChange);
        btnDefault = findViewById(R.id.btnDefault);
        initializeProgressBar();

        StorageReference profileRef = storageReference.child("users/"+uid+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Picasso.get().load(uri).into(dog);
        });
        setUpDrawer();

        // *animations*

        leashAnimation = findViewById(R.id.leash_animation);
        foodAnimation = findViewById(R.id.food_animation);
        spongeAnimation = findViewById(R.id.bath_animation);


        // permissions

        // קבלת אישור לשימוש במצלמה
        checkAndRequestPermission(Manifest.permission.CAMERA, 102);
        // קבלת אישור לשלוח התראות למשתמש אם עוד לא אישר
        checkAndRequestPermission(Manifest.permission.POST_NOTIFICATIONS, 101);

        // *buttons & notifications*

        ActivityResultLauncher<Intent> startGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result != null && result.getResultCode() == RESULT_OK ){
                Intent data = result.getData();
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            }
        });
        ActivityResultLauncher<Intent> startCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        if (imageBitmap != null) {
                            Uri imageUri = getImageUriFromBitmap(imageBitmap);
                            uploadImageToFirebase(imageUri);
                        }
                    }
                }
        );
        btnChange.setOnClickListener(v -> {
            boolean isCameraPermissionGranted = ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
            // מגדיר את הטקסט של כפתורי התיבה לפי ההרשאה לשימוש במצלמה
            String[] options = isCameraPermissionGranted ? new String[]{"Gallery", "Camera"} : new String[]{"Gallery", "Enable Camera in Settings"};

            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("Choose Image Source")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            // פותח גלריה
                            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startGallery.launch(openGalleryIntent);
                        } else {
                            if (isCameraPermissionGranted) {
                                // פותח מצלמה
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startCamera.launch(takePictureIntent);
                            } else {
                                // במידה ואין אישור (כלומר המשתמש בחר לא לאשר אף פעם), הכפתור שולח להגדרות
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }
                    })
                    .show();
        });
        btnDefault.setOnClickListener(v -> {
            profileRef.delete();
            dog.setImageResource(R.drawable.dog);
        });

        // כפתור אוכל
        buttonFood.setOnClickListener(v -> {
             // outside of testing - maxTime = reqTime + 30 minutes
            long timeAtClick = System.currentTimeMillis();
            showInputDialog(new InputDialogListener() {
                @Override
                public void onDialogClosed(String userInput) {
                    checkButton("foodSuccess", (10 * 1000), 10, timeAtClick);
                    playAnimation(foodAnimation);

                    long timeToRemind = 10 * 1000;
                    logAction("food", timeAtClick, userInputText);
                    makeNotification(timeAtClick, timeToRemind, "Your dog is hungry! Remember to feed him!");

                }
            });
        });
        //  כפתור מקלחת
        buttonSponge.setOnClickListener(v -> {
            long timeAtClick = System.currentTimeMillis();
            showInputDialog(new InputDialogListener() {
                @Override
                public void onDialogClosed(String userInput) {
                    checkButton("spongeSuccess", (15 * 1000), 35, timeAtClick); // outside of testing - maxTime = reqTime + 30 minutes
                    playAnimation(spongeAnimation);

                    long timeToRemind = 15 * 1000;
                    logAction("sponge", timeAtClick, userInputText);
                    makeNotification(timeAtClick, timeToRemind, "Your dog is dirty! Remember to give him a bath");
                }
            });
        });
        //  כפתור יציאה לטיול
        buttonLeash.setOnClickListener(v -> {
            long timeAtClick = System.currentTimeMillis();
            showInputDialog(new InputDialogListener() {
                @Override
                public void onDialogClosed(String userInput) {
                    checkButton("leashSuccess", (5 * 1000), 15, timeAtClick); // outside of testing - maxTime = reqTime + 30 minutes

                    playAnimation(leashAnimation);
                    long timeToRemind = 5 * 1000;
                    logAction("leash", timeAtClick, userInputText);
                    makeNotification(timeAtClick, timeToRemind, "Your dog wants to go out! Remember to take him outside");
                }
            });
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (user != null) {
            dailyLogin();
            initializeProgressBar();
        }
    }
    // קבלת הרשאות
    private void checkAndRequestPermission(String permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(HomeActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{permission}, requestCode);
            }
        }
    }

    // העלאת תמונה לאחסון פיירבייס
    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference fileRef = storageReference.child("users/"+uid+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Picasso.get().load(uri).into(dog);
                });
                Toast.makeText(HomeActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // בדיקה האם זה כניסה יומית ועדכון אם כן
    private void dailyLogin() {
        if (user != null) {
            userRef.child("lastLoginTimestamp").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Long lastLoginTime = task.getResult().getValue(Long.class);
                    long currentTime = System.currentTimeMillis();
                    userRef.child("lastLoginTimestamp").setValue(currentTime);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String lastLoginDate = dateFormat.format(lastLoginTime);
                    String currentDate = dateFormat.format(currentTime);
                    if (lastLoginTime != null) {
                        long timeDifference = currentTime - lastLoginTime;
                        if (timeDifference >= (60 * 1000))  { // !lastLoginDate.equals(currentDate) במידה ולא בבדיקה:
                            userRef.child("loginCount").runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                    Integer currentLoginCount = mutableData.getValue(Integer.class);
                                    if (currentLoginCount == null) {
                                        mutableData.setValue(1);
                                    } else {
                                        mutableData.setValue(currentLoginCount + 1);
                                    }
                                    return Transaction.success(mutableData);
                                }
                                @Override
                                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                    if (committed) {
                                        Log.d("DailyLogin", "Transaction committed successfully.");
                                    } else {
                                        Log.d("DailyLogin", "Transaction was not committed.");
                                    }
                                }});
                            updateProgress(25);
                        }
                    }}});
        }
    }

    // בדיקה אם הפעולה נעשתה בזמן
    private void checkButton(String button, int minTime, int exp, long currentTime) {
        if (user != null) {
            String buttonTimestamp = button + "Timestamp";
            int maxTime = minTime + 10000; // outside of testing, maxTime = minTime + 30 minutes
            userRef.child(buttonTimestamp).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Long savedTimestamp = task.getResult().getValue(Long.class);

                    if (savedTimestamp != null) {
                        long timeDifference = currentTime - savedTimestamp;
                        if ((timeDifference >= minTime && timeDifference <= maxTime) || savedTimestamp == 0) {
                            // check if was pressed in the req time (between max and min) || first click
                            userRef.child(button).runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                    Integer currentButtonCount = mutableData.getValue(Integer.class);
                                    if (currentButtonCount == null) {
                                        mutableData.setValue(0);
                                    } else {
                                        mutableData.setValue(currentButtonCount + 1);
                                    }
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                    if (committed) {
                                        Log.d("DailyLogin", "Transaction committed ");
                                    } else {
                                        Log.d("DailyLogin", "Transactioמ not committed.");
                                    }
                                }
                            });
                            updateProgress(exp);
                        }
                    }
                    userRef.child(buttonTimestamp).setValue(currentTime);
                }});
        }
    }

    // הרצת גיף אנימציה
    private void playAnimation(LottieAnimationView lottieAnimation) {
        dog.setVisibility(View.GONE);
        buttonSponge.setVisibility(View.GONE);
        buttonLeash.setVisibility(View.GONE);
        buttonFood.setVisibility(View.GONE);

        lottieAnimation.setVisibility(View.VISIBLE);
        lottieAnimation.playAnimation();

        lottieAnimation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
            }
            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                lottieAnimation.setVisibility(View.GONE);
                dog.setVisibility(View.VISIBLE);
                buttonSponge.setVisibility(View.VISIBLE); // change to animation
                buttonLeash.setVisibility(View.VISIBLE);
                buttonFood.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {
            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {
            }
        });
    }

    // הוספת לוג לדאטאבאס
    private void logAction(String button, long timestamp, String input) {
        if (user != null)
        {
            String formattedTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
            LogEntry logEntry = new LogEntry(button, formattedTimestamp, input);

            userRef.child("logs").push().setValue(logEntry);
        }
    }

    // יצירת התראה
    private void makeNotification(long timeAtClick, long timeToRemind, String text) {
        createNotificationChannel();

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int requestCode = (int) System.currentTimeMillis();
        Intent intent = new Intent(getApplicationContext(), ReminderBroadcast.class);
        intent.putExtra("data", text);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_MUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // בודק הרשאה להתראה מדויקת
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeAtClick + timeToRemind, pendingIntent);
            } else if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
                Log.e("AlarmManager", "Permission to schedule exact alarms is not granted.");
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtClick + timeToRemind, pendingIntent);
                new AlertDialog.Builder(this)
                        .setTitle("Exact Alarm Permission Needed")
                        .setMessage("To ensure exact timely reminders, please allow exact alarms in your device settings.")
                        .setPositiveButton("Go to Settings", (dialog, which) -> startActivity( new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).setData(Uri.parse(
                                "package:" + getPackageName()))))
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeAtClick + timeToRemind, pendingIntent);
        }
    }
    //יצירת ערוץ
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String chanelID = "CHANNEL_ID_NOTIFICATION";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(chanelID,
                    "some desc", importance);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
    // עדכון נקודות
    private void updateProgress(int expGained) {
        DatabaseReference expRef = userRef.child("exp");
        DatabaseReference levelRef = userRef.child("level");
        DatabaseReference requiredExpRef = userRef.child("requiredExp");
        levelCount = findViewById(R.id.levelCount);
        progressBar = findViewById(R.id.levelBar);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DataSnapshot snapshot = task.getResult();

                int currentExp = snapshot.child("exp").getValue(Integer.class);
                int currentLevel = snapshot.child("level").getValue(Integer.class);
                int requiredExp = snapshot.child("requiredExp").getValue(Integer.class);

                int newExp = currentExp + expGained;
                // בודק אם הגעת לכמות הדרושה כדי לעלות שלב
                if (newExp >= requiredExp) {
                    int excessExp = newExp - requiredExp;
                    currentLevel++;
                    requiredExp += 50; // מעדכן כמה נקודות צריך לשלב הבא
                    newExp = excessExp;
                }

                // מעדכן את הdatabase
                expRef.setValue(newExp);
                levelRef.setValue(currentLevel);
                requiredExpRef.setValue(requiredExp).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        initializeProgressBar();
                    }});
            } else {
                Log.e("Firebase", "Failed to retrieve data", task.getException());
            }
        });

    }

    // אתחול התקדמות
    private void initializeProgressBar() {
        if (user != null) {
            levelCount = findViewById(R.id.levelCount);
            progressBar = findViewById(R.id.levelBar);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    DataSnapshot snapshot = task.getResult();


                    int currentExp = snapshot.child("exp").getValue(Integer.class);
                    int currentLevel = snapshot.child("level").getValue(Integer.class);
                    int requiredExp = snapshot.child("requiredExp").getValue(Integer.class);

                    progressBar.setMax(requiredExp);
                    progressBar.setProgress(currentExp);

                    levelCount.setText(String.valueOf(currentLevel));
                }
            });
        }
    }

    // יצירת תיבת דו-שיח להכנסת הערות מהמשתמש
    private void showInputDialog(final InputDialogListener listener) {

        final EditText input = new EditText(this);
        input.setHint("Enter your text");

        // בניית תיבה
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("הכנס הערה בנוגע לפעולה")
                .setView(input)
                .setPositiveButton("שמור הערה", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userInputText = input.getText().toString();
                        if (userInputText.isEmpty()) {
                            userInputText = "None"; // Default if empty
                        }
                        Toast.makeText(HomeActivity.this, "Saved: " + userInputText, Toast.LENGTH_SHORT).show();
                        listener.onDialogClosed(userInputText);
                    }
                })
                .setNegativeButton("ללא הערות", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userInputText = "None"; // Default value if canceled
                        Toast.makeText(HomeActivity.this, "No data saved", Toast.LENGTH_SHORT).show();
                        listener.onDialogClosed(userInputText);
                    }
                });

        // Show the dialog
        builder.create().show();
    }

    // העברת תמונה לuri - כדי להעלות לפיירבייס
    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "CapturedImage", null);
        return Uri.parse(path);
    }

}


