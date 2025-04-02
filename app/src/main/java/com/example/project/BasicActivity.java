package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BasicActivity extends AppCompatActivity {
    protected LinearLayout home, history, statistics, logout;
    protected TextView userEmail;
    protected DrawerLayout drawerLayout;
    protected FirebaseAuth mAuth;
    protected FirebaseUser user;
    protected String uid;
    protected DatabaseReference userRef;
    protected StorageReference storageReference;
    protected ImageView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // אתחול הפניות לפיירבייס
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            Log.e("AuthCheck", "User is null, redirecting to login.");
            finish();
            return;
        } else {
            uid = user.getUid();
            userRef = FirebaseDatabase.getInstance("//").getReference("userActivities").child(uid); // ללא הלינק
            storageReference = FirebaseStorage.getInstance("//").getReference(); // ללא הלינק
        }

    }

    protected void setUpDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        history = findViewById(R.id.history);
        statistics = findViewById(R.id.statistics);
        logout = findViewById(R.id.logout);
        userEmail = findViewById(R.id.userEmail);
        userEmail.setText(user.getEmail());
        menu.setOnClickListener(v -> {
            openDrawer(drawerLayout);
            Log.d("Drawer", "Drawer opened");
        });
        // מאזינים לתפריט הניווט
        home.setOnClickListener(v -> {
            Log.d("DrawerClick", "Home clicked");
            if (!(this instanceof HomeActivity)) {
                redirectActivity(this, HomeActivity.class);
            } else {
                recreate();
            }
        });

        history.setOnClickListener(v -> {
            Log.d("DrawerClick", "History clicked");
            if (!(this instanceof HistoryActivity)) {
                redirectActivity(this, HistoryActivity.class);
            } else {
                recreate();
            }
        });

        statistics.setOnClickListener(v -> {
            Log.d("DrawerClick", "Statistics clicked");
            if (!(this instanceof StatisticsActivity)) {
                redirectActivity(this, StatisticsActivity.class);
            } else {
                recreate();
            }
        });

        logout.setOnClickListener(v -> {
            Log.d("DrawerClick", "Logout clicked");
            FirebaseAuth.getInstance().signOut();
            redirectActivity(this, LoginActivity.class);
        });
    }

    // פעולות לטיפול בתפריט הניווט
    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    protected static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    protected static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    protected static void redirectActivity(Activity activity, Class<?> secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
    // ממשק לתיבות דו-שיח
    interface InputDialogListener {
        void onDialogClosed(String userInput);
    }
}
