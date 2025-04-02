package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends BasicActivity {

    TextView accountProg, levelProg, activityProg;

    String[] messages = {"You should take care of your dog more often!",
            "You're improving, keep going!",
            "Nice job! Your dog is getting better.",
            "Well done!",
            "Impressive records!"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        levelProg = findViewById(R.id.levelProg);
        activityProg = findViewById(R.id.activityProg);
        accountProg = findViewById(R.id.accountProg);




        StorageReference storageRef = storageReference.child("users/");

        setProgText();
        setActivityText();
        setAccountText(storageRef);

        setUpDrawer();
    }

    private void setProgText() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int level = snapshot.child("level").getValue(Integer.class);
                    int exp = snapshot.child("exp").getValue(Integer.class);
                    int requiredExp = snapshot.child("requiredExp").getValue(Integer.class);

                    int messageIndex = Math.min(level / 2, messages.length - 1);
                    String displayedMessage = messages[messageIndex];

                    String progressText = "Your current level is " + level + ". " + displayedMessage +
                            "\n\n You currently have " + exp + " points out of " + requiredExp +
                            ", which means you still need " + (requiredExp - exp) + " more points to reach level " + (level + 1) + ".";

                    levelProg.setText(progressText);
                } else {
                    levelProg.setText("Error: Could not retrieve data.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                levelProg.setText("Error: " + error.getMessage());
            }
        });
    }

    private void setActivityText() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int foodSuccess = snapshot.child("foodSuccess").getValue(Integer.class);
                    int spongeSuccess = snapshot.child("spongeSuccess").getValue(Integer.class);
                    int leashSuccess = snapshot.child("leashSuccess").getValue(Integer.class);

                    String activityText = "You have successfully given your dog a bath according to schedule " + spongeSuccess + " " +
                            "times. \n \n You have successfully fed your dog according to schedule " +foodSuccess + " times. " +
                            "\n \n You have successfully taken your dog on a trip " + leashSuccess +" times.";

                    activityProg.setText(activityText);
                } else {
                    activityProg.setText("Error: Could not retrieve data.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                activityProg.setText("Error: " + error.getMessage());
            }
        });
    }

    private void setAccountText(StorageReference storageRef) {
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<StorageReference> folders = listResult.getPrefixes();
                boolean hasProfile = false;
                for (StorageReference folder : folders) {
                    if (folder.getName().equals(uid)) {
                        Log.d("storage", "folder exists");
                        hasProfile = true;
                        break;
                    }}

                String ifHasProfile;
                if (hasProfile) {
                    ifHasProfile = "You have uploaded an image of your dog to the app. What a cute dog!";
                } else {
                    ifHasProfile = "You still haven't uploaded an image of your dog, what are you waiting for?";
                }
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            int loginCount = snapshot.child("loginCount").getValue(Integer.class);
                            long firstLoginTimestamp = snapshot.child("firstLoginTimestamp").getValue(Long.class);

                            String formattedFirstLogin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                    .format(new Date(firstLoginTimestamp));

                            int messageIndex = Math.min(loginCount / 3, messages.length - 1);
                            String message = messages[messageIndex];

                            String accountText = ifHasProfile + "\n\n Your first login to the application was on " + formattedFirstLogin +
                                    ". Since then, you have logged in to take care of your dog for " + loginCount + " days. " + message;
                            accountProg.setText(accountText);
                        } else {
                            accountProg.setText("Error");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        accountProg.setText("Error");
                    }
                });
            }
        });
    }


}