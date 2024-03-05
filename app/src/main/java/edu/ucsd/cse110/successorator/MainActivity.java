package edu.ucsd.cse110.successorator;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import androidx.lifecycle.ViewModelProvider;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import edu.ucsd.cse110.successorator.data.db.AppDatabase;
import edu.ucsd.cse110.successorator.data.db.GoalDao;
import edu.ucsd.cse110.successorator.data.db.GoalEntity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.ucsd.cse110.successorator.data.db.AppDatabase;
import edu.ucsd.cse110.successorator.data.db.GoalDao;
import edu.ucsd.cse110.successorator.data.db.GoalEntity;

public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private GoalDao goalDao;

    Button forwardButton;

    private TextView dateTextView;
    private RecyclerView recyclerView;
    private TextView noGoalsTextView;
    private GoalsAdapter adapter;
    private List<GoalEntity> goalsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "SuccessListDatabase").allowMainThreadQueries().build();
        goalDao = db.goalDao();

        dateTextView = findViewById(R.id.DateText);
        recyclerView = findViewById(R.id.goals_recycler_view);
        noGoalsTextView = findViewById(R.id.no_goals_text);
        adapter = new GoalsAdapter(goalsList, goalDao);
        forwardButton = findViewById(R.id.forwardButton); // Find the forward button
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        goalDao.getAllGoals().observe(this, goalEntities -> {
            adapter.updateGoals(goalEntities);
            updateNoGoalsVisibility();
        });

        updateDate();

        // Set OnClickListener for FloatingActionButton to add new goals
        findViewById(R.id.add_goal_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddGoalDialog();
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                advanceTimeByOneDay();
                updateNoGoalsVisibility();
            }
        });


    }

    private void advanceTimeByOneDay() {
        // Define the date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());

        // Initialize a calendar instance
        Calendar calendar = Calendar.getInstance();

        // Try to parse the date from the TextView, if it exists
        try {
            Date displayedDate = dateFormat.parse(dateTextView.getText().toString());
            calendar.setTime(displayedDate);
        } catch (ParseException e) {
            // If parsing fails, the current date is used
            e.printStackTrace();
        }

        // Advance the date by one day
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        // Update the TextView with the new date
        String currentDate = dateFormat.format(calendar.getTime());
        dateTextView.setText(currentDate);

        // Your method to remove checked-off goals
        adapter.removeCheckedOffGoals();
    }

    private void showAddGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Goal");

        // Inflate the custom layout
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_goal, null);

        final EditText editTextGoal = dialogView.findViewById(R.id.edit_text_goal_id);
        final Spinner frequencySpinner = dialogView.findViewById(R.id.frequency_spinner);
        final Button selectStartDateButton = dialogView.findViewById(R.id.button_select_start_date);

        // Setup the spinner with frequencies
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.goal_frequencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(adapter);

        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        // Initially set the button text to "Select Starting Date"
        selectStartDateButton.setText("Select Starting Date");

        // DatePickerDialog logic
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            // Update the button text with the selected date
            selectStartDateButton.setText(sdf.format(calendar.getTime()));
        };

        selectStartDateButton.setOnClickListener(view -> new DatePickerDialog(MainActivity.this, date,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());

        builder.setView(dialogView);

        // Handle the "Add" button
        builder.setPositiveButton("Add", (dialog, which) -> {
            final String goalText = editTextGoal.getText().toString().trim();
            final String frequency = frequencySpinner.getSelectedItem().toString();
            final long startDate = calendar.getTimeInMillis(); // Get the start date in millis

            GoalEntity findSameGoal = goalDao.findByGoalText(goalText);
            if (!TextUtils.isEmpty(goalText) && findSameGoal == null) {
                new Thread(() -> goalDao.insert(new GoalEntity(goalText, false))).start();
            } else if (findSameGoal != null) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "You already have this goal added.", Toast.LENGTH_SHORT).show());
            } else {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Please enter a goal", Toast.LENGTH_SHORT).show());
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }




    private void updateNoGoalsVisibility() {
        if (goalDao.isItEmpty() == null) {
            noGoalsTextView.setVisibility(View.VISIBLE);
        } else {
            noGoalsTextView.setVisibility(View.GONE);
        }
    }

    private void updateDate() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        // Update the TextView with the current date
        dateTextView.setText(currentDate);
    }
}
