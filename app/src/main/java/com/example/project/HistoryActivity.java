package com.example.project;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BasicActivity {
    List<LogEntry> logs = new ArrayList<LogEntry>();
    List<LogEntry> filteredLogs = new ArrayList<>();
    String userInputText = "None";
    LogAdapter logAdapter;
    RecyclerView actionLog;
    DatabaseReference logsRef;
    TextView foodSort , spongeSort, leashSort, showAll, logCount, inputSort;
    Boolean foodFilter = false, spongeFilter = false, leashFilter = false, inputFilter= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // אתחול והשמה
        setContentView(R.layout.activity_history);
        foodSort = findViewById(R.id.foodSort);
        spongeSort = findViewById(R.id.spongeSort);
        showAll = findViewById(R.id.showAll);
        leashSort = findViewById(R.id.leashSort);
        inputSort = findViewById(R.id.inputSort);
        updateFilterHighlight(foodSort, foodFilter);
        updateFilterHighlight(spongeSort, spongeFilter);
        updateFilterHighlight(leashSort, leashFilter);
        updateFilterHighlight(inputSort, inputFilter);

        // אתחול האדפטר
        logsRef = userRef.child("logs");
        actionLog = findViewById(R.id.actionlog);
        logAdapter = new LogAdapter(this,filteredLogs);
        actionLog.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
        actionLog.setAdapter(logAdapter);

        fetchLogs();

        // ממיינים
        inputSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click", "inputSort clicked");
                showInputDialog(new InputDialogListener() {
                    @Override
                    public void onDialogClosed(String userInput) {
                        if (!userInputText.equals("None")) {
                            inputFilter = !inputFilter;
                            updateFilterHighlight(inputSort, inputFilter);
                            applyFilters();
                        }
                    }
                });
            }
        });
        foodSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DrawerClick", "FoodSort clicked");
                foodFilter = !foodFilter;
                Log.d("food", "food: " + foodFilter);
                updateFilterHighlight(foodSort, foodFilter);
                applyFilters();
            }
        });
        spongeSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DrawerClick", "spongeSort clicked");
                spongeFilter = !spongeFilter;
                Log.d("sponge", "sponge: " + spongeFilter);
                updateFilterHighlight(spongeSort, spongeFilter);

                applyFilters();
            }
        });
        leashSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DrawerClick", "leashSort clicked");
                leashFilter = !leashFilter;
                Log.d("leash", "leash: " + leashFilter);
                updateFilterHighlight(leashSort, leashFilter);
                applyFilters();
            }
        });
        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("show all", "show all clicked");
                foodFilter = false;
                spongeFilter = false;
                leashFilter = false;
                inputFilter = false;
                updateFilterHighlight(foodSort, false);
                updateFilterHighlight(spongeSort, false);
                updateFilterHighlight(leashSort, false);
                updateFilterHighlight(inputSort, false);
                applyFilters();
            }
        });

        setUpDrawer();

    }

    // קריאת נתונים ממסד הנתונים לרשימה
    private void fetchLogs() {
        logsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                logs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String buttonName = snapshot.child("buttonName").getValue(String.class);
                    String input = snapshot.child("input").getValue(String.class);
                    String formattedTimestamp = snapshot.child("formattedTimestamp").getValue(String.class);
                    logs.add(new LogEntry(buttonName, formattedTimestamp, input));
                }
                Log.d("FirebaseLogs", "Fetched logs: " + logs.size());
                applyFilters(); 
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HistoryActivity.this, "Failed to load logs", Toast.LENGTH_SHORT).show();
            }
        });
    }



// עדכון סופי של רשימת האיברים לפי ממיינים לפני שליחה לאדפטר
    private void applyFilters() {
        filteredLogs.clear();
        Log.d("filters", "food, sponge, leash" + foodFilter + spongeFilter + leashFilter);
        if (!foodFilter && !spongeFilter && !leashFilter && !inputFilter) {
            // אין ממיינים פעילים, הראה הכל
            filteredLogs.addAll(logs);
        } else {
            // הראה ממיין בהתאם
            for (LogEntry log : logs) {
                if (foodFilter && log.getButtonName().equals("food")) {
                    filteredLogs.add(log);
                }
                if (spongeFilter && log.getButtonName().equals("sponge")) {
                    filteredLogs.add(log);
                }
                if (leashFilter && log.getButtonName().equals("leash")) {
                    filteredLogs.add(log);
                }
                if (inputFilter && !isInLogs(filteredLogs,log) &&!userInputText.equals("None") && (log.getButtonName().contains(userInputText) ||
                        log.getFormattedTimestamp().contains(userInputText) ||
                        log.getInput().contains(userInputText))) {
                    filteredLogs.add(log);
                }
            }
        }
        logCount = findViewById(R.id.logCount);
        if (logAdapter != null) {
            logAdapter.notifyDataSetChanged();
            logCount.setText(String.valueOf(filteredLogs.size()));
            Log.d("AdapterUpdate", "Adapter updated with " + filteredLogs.size() + " logs");
        } else {
            Log.d("AdapterError", "logAdapter is null!");
        }
    }

    // עדכון הדגשה לממיינים
    private void updateFilterHighlight(TextView textView, boolean isHighlighted) {
        if (isHighlighted) {
            // Set the highlight background color
            textView.setBackgroundColor(getResources().getColor(R.color.highlight_color));
        } else {
            // Reset the background to its default state
            textView.setBackground(null);
        }
    }
    // תיבת דו-שיח לבדוק לפי מה למיין
    private void showInputDialog(final InputDialogListener listener) {
        final EditText input = new EditText(this);
        input.setHint("Enter your text");


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("מיין לפי טקסט")
                .setView(input)
                .setPositiveButton("מיין", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userInputText = input.getText().toString();
                        if (userInputText.isEmpty()) {
                            userInputText = "None";
                        }
                        Toast.makeText(HistoryActivity.this, "Sorting by:  " + userInputText, Toast.LENGTH_SHORT).show();
                        listener.onDialogClosed(userInputText);
                    }
                })
                .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userInputText = "None";
                        listener.onDialogClosed(userInputText);
                    }
                });

        // Show the dialog
        builder.create().show();
    }
    // פעולת עזר לוודא שאין כפילויות
    private boolean isInLogs(List<LogEntry> check, LogEntry in) {
        for (LogEntry log : check) {
            if (log.equals(in))
                return true;
        }
        return false;
    }

}



