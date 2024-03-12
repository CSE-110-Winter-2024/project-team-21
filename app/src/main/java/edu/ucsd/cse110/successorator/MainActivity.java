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
import android.widget.RadioButton;
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

    private List<RadioButton> radioButtons;

    //constants
    final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());

    private int shownGoalsCount;
    Calendar today = Calendar.getInstance();

    String allFormattedToday;

    final String[] daysOfWeek = {"Sun","Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

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
        shownGoalsCount = 0;
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 0, 0, 0); // Set the calendar to 12:00 AM on the current day with hour, minute, second
        allFormattedToday = dateFormat.format(today.getTime());

        goalDao.getAllGoals().observe(this, goalEntities -> {
            List<GoalEntity> filteredGoals = goalEntities.stream()
                    .filter(goal ->
                            goal.getFrequencyType().equals("One-time")
                            || (goal.getFrequencyType().equals("Daily") && today.getTimeInMillis() >= goal.getFreqTimeInMilli())
                            || (goal.getFrequencyType().equals("Weekly") && daysOfWeek[today.get(Calendar.DAY_OF_WEEK) - 1].equals(goal.getFreqDayString()))
                            || (goal.getFrequencyType().equals("Monthly") && isCorrectMonthlyOccurrence(goal))
                            || (goal.getFrequencyType().equals("Yearly") && isCorrectYearlyOccurrence(goal)))
                    .collect(Collectors.toList());
            shownGoalsCount = filteredGoals.size();
            adapter.updateGoals(filteredGoals);
            updateNoGoalsVisibility(shownGoalsCount);
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
                updateNoGoalsVisibility(shownGoalsCount);
            }
        });
    }

    private void advanceTimeByOneDay() {
        // Try to parse the date from the TextView, if it exists
        try {
            Date displayedDate = dateFormat.parse(dateTextView.getText().toString());
            today.setTime(displayedDate);
        } catch (ParseException e) {
            // If parsing fails, the current date is used
            e.printStackTrace();
        }

        // Advance the date by one day
        today.add(Calendar.DAY_OF_MONTH, 1);

        // Update the TextView with the new date
        String currentDate = dateFormat.format(today.getTime());
        dateTextView.setText(currentDate);

        // Your method to remove checked-off goals
        adapter.removeCheckedOffGoals();
        goalDao.getAllGoals().observe(this, goalEntities -> {
            List<GoalEntity> filteredGoals = goalEntities.stream()
                    .filter(goal ->
                            goal.getFrequencyType().equals("One-time")
                            || (goal.getFrequencyType().equals("Daily") && today.getTimeInMillis() >= goal.getFreqTimeInMilli())
                            || (goal.getFrequencyType().equals("Weekly") && daysOfWeek[today.get(Calendar.DAY_OF_WEEK) - 1].equals(goal.getFreqDayString()))
                            || (goal.getFrequencyType().equals("Monthly") && isCorrectMonthlyOccurrence(goal))
                            || (goal.getFrequencyType().equals("Yearly") && isCorrectYearlyOccurrence(goal)))
                    .collect(Collectors.toList());
            shownGoalsCount = filteredGoals.size();
            adapter.updateGoals(filteredGoals);
            updateNoGoalsVisibility(shownGoalsCount);
        });
    }

    private void showAddGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Goal");

        // Inflate the custom layout
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_goal, null);
        builder.setView(dialogView);

        final EditText editTextGoal = dialogView.findViewById(R.id.edit_text_goal_id);
        final Spinner spinDay = dialogView.findViewById(R.id.spin_day);
        final Spinner spinFreq = dialogView.findViewById(R.id.spin_recurring);
        final Spinner spinWeek = dialogView.findViewById(R.id.spin_weekly);
        final Button yearlyButton = dialogView.findViewById(R.id.button_select_start_date);
        final Button oneTimeButton = dialogView.findViewById(R.id.btn_onetime);
        final Button dailyButton = dialogView.findViewById(R.id.btn_daily);
        final RadioButton radioBtnOneTime = dialogView.findViewById(R.id.radio_btn_onetime);
        final RadioButton radioBtnDaily = dialogView.findViewById(R.id.radio_btn_daily);
        final RadioButton radioBtnWeekly = dialogView.findViewById(R.id.radio_btn_weekly);
        final RadioButton radioBtnMonthly = dialogView.findViewById(R.id.radio_btn_monthly);
        final RadioButton radioBtnYearly = dialogView.findViewById(R.id.radio_btn_yearly);


        // Setup the spinner with frequencies
        ArrayAdapter<CharSequence> adapterDay = ArrayAdapter.createFromResource(this,
                R.array.day_frequencies, android.R.layout.simple_spinner_item);
        adapterDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDay.setAdapter(adapterDay);
        spinWeek.setAdapter(adapterDay);

        // Setup the spinner with frequencies
        ArrayAdapter<CharSequence> adapterRecur = ArrayAdapter.createFromResource(this,
                R.array.recur_frequencies, android.R.layout.simple_spinner_item);
        adapterRecur.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinFreq.setAdapter(adapterRecur);

        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.getDefault());
        final String formattedToday = sdf.format(today.getTime());
        // Initially set the button text to "Select Starting Date"
        yearlyButton.setText(formattedToday);
        oneTimeButton.setText(allFormattedToday);
        dailyButton.setText(allFormattedToday);

        Calendar calendar = Calendar.getInstance();

        // DatePickerDialog Yearly logic
        DatePickerDialog.OnDateSetListener dateYear = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Update the button text with the selected date
            yearlyButton.setText(sdf.format(calendar.getTime()));
        };

        // DatePickerDialog Daily logic
        DatePickerDialog.OnDateSetListener dateDaily = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Update the button text with the selected date
            dailyButton.setText(dateFormat.format(calendar.getTime()));
        };

        // DatePickerDialog One-time logic
        DatePickerDialog.OnDateSetListener dateOneTime = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Update the button text with the selected date
            oneTimeButton.setText(dateFormat.format(calendar.getTime()));
        };

        yearlyButton.setOnClickListener(view -> new DatePickerDialog(MainActivity.this, dateYear,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());

        dailyButton.setOnClickListener(view -> new DatePickerDialog(MainActivity.this, dateDaily,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());

        oneTimeButton.setOnClickListener(view -> new DatePickerDialog(MainActivity.this, dateOneTime,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());

        View.OnClickListener radioButtonClickListener = v -> {
            RadioButton rb = (RadioButton) v;
            clearRadioGroupSelection(rb);
            rb.setChecked(true);
        };

        radioBtnOneTime.setOnClickListener(radioButtonClickListener);
        radioBtnDaily.setOnClickListener(radioButtonClickListener);
        radioBtnWeekly.setOnClickListener(radioButtonClickListener);
        radioBtnMonthly.setOnClickListener(radioButtonClickListener);
        radioBtnYearly.setOnClickListener(radioButtonClickListener);

        radioButtons = new ArrayList<>(List.of(radioBtnOneTime, radioBtnDaily, radioBtnWeekly, radioBtnMonthly, radioBtnYearly));

        // Handle the "Add" button
        builder.setPositiveButton("Save", (dialog, which) -> {
            final String goalText = editTextGoal.getText().toString().trim();
            Integer freqMonth = -1, freqOccur = -1;
            long freqTimeInMilli = -1;
            String freqType = "", freqDayString = "";
            if (radioBtnOneTime.isChecked()) {
                freqType = "One-time";
                freqTimeInMilli = calendar.getTimeInMillis();
            } else if (radioBtnDaily.isChecked()) {
                freqType = "Daily";
                freqDayString = daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK)-1];
                freqTimeInMilli = calendar.getTimeInMillis();
            } else if (radioBtnWeekly.isChecked()) {
                freqType = "Weekly";
                freqDayString = spinWeek.getSelectedItem().toString();
            } else if (radioBtnMonthly.isChecked()) {
                freqType = "Monthly";
                freqMonth = Calendar.MONTH;
                freqOccur = Integer.valueOf(spinFreq.getSelectedItem().toString().substring(0,1));
                freqDayString = spinDay.getSelectedItem().toString();
            } else if (radioBtnYearly.isChecked()) {
                freqType = "Yearly";
                freqTimeInMilli = calendar.getTimeInMillis();
            }

            GoalEntity complete = new GoalEntity(goalText, false, freqType, freqDayString, freqMonth, freqTimeInMilli, freqOccur);
            //final Integer freqOccur = ((Integer.valueOf(calendar.get(Calendar.DAY_OF_MONTH) - 1))/7) + 1;
            final long startTime = calendar.getTimeInMillis();
            final long currentTime = today.getTimeInMillis();
            boolean radioBtnsAllUnchecked = true;

            GoalEntity findSameGoal = goalDao.findByGoalText(goalText); //Check if same goalText is in goals
            for (RadioButton checkChecked : radioButtons) {
                if (checkChecked.isChecked()) {
                    radioBtnsAllUnchecked = false;
                }
            }
            if (TextUtils.isEmpty(goalText)) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Please enter a goal.", Toast.LENGTH_SHORT).show());
            } else if (radioBtnsAllUnchecked) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Please check a goal frequency.", Toast.LENGTH_SHORT).show());
            } else if (findSameGoal != null) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "You already have this goal added.", Toast.LENGTH_SHORT).show());
            } else if (yearlyButton.getText().equals(formattedToday) && radioBtnYearly.isChecked()) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Please select a date.", Toast.LENGTH_SHORT).show());
            } else if (startTime < currentTime) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Please select a valid time.", Toast.LENGTH_SHORT).show());
            }
            else {
                new Thread(() -> goalDao.insert(complete)).start();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }




    private void updateNoGoalsVisibility(int numberOfGoals) {
        if (numberOfGoals == 0) {
            noGoalsTextView.setVisibility(View.VISIBLE);
        } else {
            noGoalsTextView.setVisibility(View.GONE);
        }
    }

    private void updateDate() {
        String currentDate = dateFormat.format(today.getTime());

        // Update the TextView with the current date
        dateTextView.setText(currentDate);
    }

    private boolean isCorrectMonthlyOccurrence(GoalEntity goal) {
        String targetDayOfWeek = goal.getFreqDayString();
        String todayDay = daysOfWeek[today.get(Calendar.DAY_OF_WEEK) - 1];

        int freqOccur = goal.getFreqOccur();
        int dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
        int occurrence = ((dayOfMonth - 1) / 7) + 1;
        int goalMonth = goal.getFreqMonth();

        int todayMonth = today.get(Calendar.MONTH) + 1;
        int todayLastMonth = (todayMonth == 1) ? 12: todayMonth - 1;
        int goalNextMonth = (goalMonth == 12) ? 1 : goalMonth + 1;

        //Move forward
        if (isThirtyDayMonth(todayMonth) && isThirtyDayMonth(todayLastMonth) && todayMonth != goalNextMonth) {
            freqOccur = 1;
        }

        return todayDay.equals(targetDayOfWeek) && occurrence == freqOccur;
    }

    private boolean isCorrectYearlyOccurrence(GoalEntity goal) {

        int goalDayOfMonth = goal.getFreqMonth();
        int goalMonth = goal.getFreqMonth();

        int dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
        int currMonth = today.get(Calendar.MONTH);

        //Move forward
        if (dayOfMonth == 1 && goalDayOfMonth == 31 && currMonth > goalMonth + 1) {
            goalDayOfMonth = 1;
            goalMonth = currMonth;
        }

        return dayOfMonth == goalDayOfMonth && goalMonth == currMonth;
    }

    private boolean isThirtyDayMonth(int month) {
        switch (month) {
            case 4:
            case 6:
            case 9:
            case 11:
                return true;
            default:
                return false;
        }
    }

    private void clearRadioGroupSelection(RadioButton selectedRadioButton) {
        for (RadioButton rb : radioButtons) {
            if (!rb.equals(selectedRadioButton)) {
                rb.setChecked(false);
            }
        }
    }
}
