package edu.ucsd.cse110.successorator;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import edu.ucsd.cse110.successorator.data.db.AppDatabase;
import edu.ucsd.cse110.successorator.data.db.GoalDao;
import edu.ucsd.cse110.successorator.data.db.GoalEntity;
import android.util.Log;


import java.util.Objects;
import java.util.stream.Collectors;



public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private GoalDao goalDao;

    // UI references
    Button forwardButton;
    private TextView dateTextView;
    private RecyclerView recyclerView;
    private TextView noGoalsTextView;
    private GoalsAdapter adapter;
    private List<GoalEntity> goalsList = new ArrayList<>();
    private Spinner contextSpinner; // Spinner for selecting context

    // Fields to manage focus mode
    private boolean inFocusMode = false;
    private String focusContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Database and DAO initialization
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "SuccessListDatabase").allowMainThreadQueries().build();
        goalDao = db.goalDao();

        // UI initialization
        dateTextView = findViewById(R.id.DateText);
        recyclerView = findViewById(R.id.goals_recycler_view);
        noGoalsTextView = findViewById(R.id.no_goals_text);
        adapter = new GoalsAdapter(goalsList, goalDao);
        forwardButton = findViewById(R.id.forwardButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Observer for goal entities
        goalDao.getAllGoals().observe(this, goalEntities -> {
            adapter.updateGoals(goalEntities);
            updateNoGoalsVisibility();
        });

        // Setting the date in the UI
        updateDate();

        // Add goal button listener
        findViewById(R.id.add_goal_button).setOnClickListener(v -> showAddGoalDialog());

        // Forward button listener to advance the day
        forwardButton.setOnClickListener(v -> {
            advanceTimeByOneDay();
            updateNoGoalsVisibility();
        });


    }

    // Advances the time by one day and updates the UI
    private void advanceTimeByOneDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            Date displayedDate = dateFormat.parse(dateTextView.getText().toString());
            calendar.setTime(displayedDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            String currentDate = dateFormat.format(calendar.getTime());
            dateTextView.setText(currentDate);
            adapter.removeCheckedOffGoals(); // Remove completed goals
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // method for when the focus mode button is clicked
    public void toggleFocusMode(View view) {
        final String[] contexts = getContexts().toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Focus Context")
                .setItems(contexts, (dialog, which) -> {
                    // Apply the focus mode with the selected context
                    focusContext = contexts[which];
                    inFocusMode = true;
                    // Use the LiveData observer pattern
                    goalDao.getAllGoals().observe(this, this::filterGoalsByContext);
                })
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Clear Focus", (dialog, which) -> clearFocusMode());
        builder.create().show();
    }

    private void filterGoalsByContext(List<GoalEntity> goals) {
        if (inFocusMode && focusContext != null) {
            // If not null, proceed with filtering
            List<GoalEntity> filteredGoals = goals.stream()
                    .filter(goal -> goal.getContext().equals(focusContext))
                    .collect(Collectors.toList());
            adapter.setGoalsList(filteredGoals);
        } else {
            // If not in focus mode or focus context is null, show all goals
            adapter.setGoalsList(goals);
        }
    }

    private void clearFocusMode() {
        inFocusMode = false;
        focusContext = null;
        // Reset to observe all goals without filtering
        goalDao.getAllGoals().observe(this, adapter::setGoalsList);
    }


    // Displays a dialog for adding a new goal with context tagging
    private void showAddGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Goal");

        // EditText for goal input
        final EditText input = new EditText(this);
        input.setId(R.id.edit_text_goal_id);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        // Spinner for context selection
        contextSpinner = new Spinner(this);
        ArrayAdapter<String> contextAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getContexts());
        contextSpinner.setAdapter(contextAdapter);

        // Layout to contain input and spinner
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(input); // Add input
        layout.addView(contextSpinner); // Add spinner
        builder.setView(layout);

        // Positive button for adding the goal
        builder.setPositiveButton("Add", (dialog, which) -> {
            final String goalText = input.getText().toString().trim();
            final String selectedContext = contextSpinner.getSelectedItem().toString();
            GoalEntity findSameGoal = goalDao.findByGoalText(goalText);
            if (!TextUtils.isEmpty(goalText) && findSameGoal == null) {
                // Insert new goal with context into the database
                new Thread(() -> {
                    GoalEntity goalEntity = new GoalEntity(goalText, false);
                    goalEntity.setContext(selectedContext); // Set the context
                    goalDao.insert(goalEntity);
                }).start();
            } else if (findSameGoal != null) {
                Toast.makeText(MainActivity.this, "You already have this goal added.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Please enter a goal", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Updates visibility of 'no goals' text based on goal presence
    private void updateNoGoalsVisibility() {
        if (goalDao.isItEmpty() == null) {
            noGoalsTextView.setVisibility(View.VISIBLE);
        } else {
            noGoalsTextView.setVisibility(View.GONE);
        }
    }

    // Updates the date displayed at the top of the app
    private void updateDate() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        // Update the TextView with the current date
        dateTextView.setText(currentDate);
    }

    // Helper method to provide context options for the spinner
    private List<String> getContexts() {
        // Define the list of contexts to choose from
        return Arrays.asList("Home", "Work", "School", "Errands");
    }
}

