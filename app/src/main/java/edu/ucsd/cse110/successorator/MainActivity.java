package edu.ucsd.cse110.successorator;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import java.util.Map;
import java.util.stream.Collectors;

import android.app.AlertDialog;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import edu.ucsd.cse110.successorator.data.db.AppDatabase;
import edu.ucsd.cse110.successorator.data.db.GoalDao;
import edu.ucsd.cse110.successorator.data.db.GoalEntity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private GoalDao goalDao;

    Button forwardButton;
    FloatingActionButton addGoalButton;

    private TextView dateTextView;
    private RecyclerView recyclerView;
    private TextView noGoalsTextView;
    private GoalsAdapter adapter;
    private List<GoalEntity> goalsList = new ArrayList<>();

    private List<RadioButton> radioButtons;

    // Fields to manage focus mode
    private boolean inFocusMode = false;
    private String focusContext = "";

    private int shownGoalsCount;
    private TextView contextTextView; // Spinner for selecting context
    Calendar today;

    String allFormattedToday;

    private String currListCategory;

    private Calendar tomorrow;

    final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());

    final String[] daysOfWeek = {"Sun","Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    final String[] freqTypes = {"One-time","Daily", "Weekly", "Monthly", "Yearly"};
    final Map<String, String> contextStrings = Map.of(
            "H","Home",
            "W", "Work",
            "S", "School",
            "E", "Errands"
    );

    final Map<String, String> contextColors = Map.of(
            "Home","#F8FB85",
            "Work", "#C8FFFE",
            "School", "#ECC8FF",
            "Errands", "#C8FFD0"
    );

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
        addGoalButton = findViewById(R.id.add_goal_button);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        shownGoalsCount = 0;

        today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 0, 0, 0); // Set the calendar to 12:00 AM on the current day with hour, minute, second

        tomorrow = (Calendar) today.clone();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        allFormattedToday = "Today: " + dateFormat.format(today.getTime());
        currListCategory = "Today";

        filterChanges();

        updateDate();

        // Set OnClickListener for FloatingActionButton to add new goals
        addGoalButton.setOnClickListener(v -> showAddGoalDialog());

        // Forward button listener to advance the day
        forwardButton.setOnClickListener(v -> {
            advanceTimeByOneDay();
            updateNoGoalsVisibility();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        today = Calendar.getInstance();
        tomorrow = (Calendar) today.clone();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        updateDate();
        filterChanges();
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

        tomorrow = (Calendar) today.clone();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        // Update the TextView with the new date
        updateDate();

        // Your method to remove checked-off goals
        adapter.removeCheckedOffGoals();
        filterChanges();
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
        final TextView homeContext = dialogView.findViewById(R.id.homeContext);
        final TextView workContext = dialogView.findViewById(R.id.workContext);
        final TextView schoolContext = dialogView.findViewById(R.id.schoolContext);
        final TextView errandsContext = dialogView.findViewById(R.id.errandsContext);
        final Button yearlyButton = dialogView.findViewById(R.id.button_select_start_date);
        final Button oneTimeButton = dialogView.findViewById(R.id.btn_onetime);
        final Button dailyButton = dialogView.findViewById(R.id.btn_daily);
        final RadioButton radioBtnOneTime = dialogView.findViewById(R.id.radio_btn_onetime);
        final RadioButton radioBtnDaily = dialogView.findViewById(R.id.radio_btn_daily);
        final RadioButton radioBtnWeekly = dialogView.findViewById(R.id.radio_btn_weekly);
        final RadioButton radioBtnMonthly = dialogView.findViewById(R.id.radio_btn_monthly);
        final RadioButton radioBtnYearly = dialogView.findViewById(R.id.radio_btn_yearly);


        final TextView[] contextCircles = {homeContext, workContext, schoolContext, errandsContext};

        for (TextView contextView : contextCircles) {
            contextView.setOnClickListener(v -> {
                TextView chosen = (TextView) v;
                updateContextSelection(chosen , contextCircles);
            });
        }

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
        allFormattedToday = dateFormat.format(today.getTime());
        // Initially set the button text to "Select Starting Date"
        yearlyButton.setText(formattedToday);
        oneTimeButton.setText(allFormattedToday);
        dailyButton.setText(allFormattedToday);

        Calendar calendar = today;

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
            long freqTimeInMilli = calendar.getTimeInMillis();
            String freqType = "", freqDayString = "", selectedContext = contextStrings.get(getSelectedContext(contextCircles).getText().toString());
            String listCategory = currListCategory;
            if (radioBtnOneTime.isChecked()) {
                freqType = freqTypes[0];
            } else if (radioBtnDaily.isChecked()) {
                freqType = freqTypes[1];
                freqDayString = daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK)-1];
                freqTimeInMilli = calendar.getTimeInMillis();
                listCategory = "Recurring";
            } else if (radioBtnWeekly.isChecked()) {
                freqType = freqTypes[2];
                freqDayString = spinWeek.getSelectedItem().toString();
                listCategory = "Recurring";
            } else if (radioBtnMonthly.isChecked()) {
                freqType = freqTypes[3];
                freqMonth = Calendar.MONTH +1;
                freqOccur = Integer.valueOf(spinFreq.getSelectedItem().toString().substring(0,1));
                freqDayString = spinDay.getSelectedItem().toString();
                listCategory = "Recurring";
            } else if (radioBtnYearly.isChecked()) {
                freqType = freqTypes[4];
                listCategory = "Recurring";
            }

            GoalEntity complete = new GoalEntity(goalText, false, selectedContext, freqType, listCategory, freqDayString, freqTimeInMilli, freqOccur, freqMonth);
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
            } else if (startTime < currentTime && !radioBtnYearly.isChecked()) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Please select a valid time.", Toast.LENGTH_SHORT).show());
            }
            else {
                new Thread(() -> goalDao.insert(complete)).start();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    //HELPER FUNCTIONS

    private void updateNoGoalsVisibility() {
        if (shownGoalsCount == 0 && dateTextView.getText().toString().equals(allFormattedToday)) {
            noGoalsTextView.setVisibility(View.VISIBLE);
        } else {
            noGoalsTextView.setVisibility(View.GONE);
        }
    }

    // Update the TextView with the current date
    private void updateDate() {
        switch(currListCategory) {
            case "Today":   dateTextView.setText("Today: " + dateFormat.format(today.getTime()));
                break;
            case "Tomorrow":    dateTextView.setText("Tomorrow: " + dateFormat.format(tomorrow.getTime()));
                break;
            case "Pending": dateTextView.setText("Pending");
                break;
            case "Recurring":   dateTextView.setText("Recurring");
                break;
        }
    }

    private boolean isCorrectMonthlyOccurrence(GoalEntity goal) {
        String targetDayOfWeek = goal.getFreqDayString();
        String todayDay = daysOfWeek[today.get(Calendar.DAY_OF_WEEK) - 1];

        int freqOccur = goal.getFreqOccur();
        int dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
        int occurrence = ((dayOfMonth - 1) / 7) + 1;

        int todayMonth = today.get(Calendar.MONTH) + 1;
        int todayLastMonth = todayMonth - 1;

        int occurForThirtyDay = (((dayOfMonth - 7 + 30) - 1) / 7) + 1;
        int thirtyOneDayOccur = (((dayOfMonth - 7 + 31) - 1) / 7) + 1;
        //No fifth day of this month
        if (todayDay.equals(targetDayOfWeek) && occurrence == 1) {
            if (isThirtyDayMonth(todayLastMonth) && occurForThirtyDay == 4) {
                freqOccur = 1;
            } else {
                if (thirtyOneDayOccur == 4) {
                    freqOccur = 1;
                }
            }
        }

        return todayDay.equals(targetDayOfWeek) && occurrence == freqOccur;
    }

    private boolean isCorrectYearlyOccurrence(GoalEntity goal) {
        Calendar targetDate = Calendar.getInstance();
        targetDate.setTimeInMillis(goal.getFreqTimeInMilli());

        int goalDay = targetDate.get(Calendar.DAY_OF_MONTH);
        int goalMonth = targetDate.get(Calendar.MONTH);

        int dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
        int currMonth = today.get(Calendar.MONTH);
        int currYear = today.get(Calendar.YEAR);

        // Handle Leap Day
        if (goalMonth == Calendar.FEBRUARY && goalDay == 29) {
            if (!isLeapYear(currYear) && currMonth == Calendar.FEBRUARY && dayOfMonth == 28) {
                return true;
            }
        }

        return currMonth == goalMonth && goalDay == dayOfMonth;
    }

    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
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

    private void filterChanges() {
        final List<GoalEntity>[] filteredGoals = new List[]{new ArrayList<>()};
        goalDao.getAllGoals().observe(this, goalEntities -> {
            filteredGoals[0] = goalEntities.stream().filter(goal ->
                            goal.getFrequencyType().equals(freqTypes[0])
                                    || (goal.getFrequencyType().equals(freqTypes[1]) && today.getTimeInMillis() >= goal.getFreqTimeInMilli())
                                    || (goal.getFrequencyType().equals(freqTypes[2]) && daysOfWeek[today.get(Calendar.DAY_OF_WEEK) - 1].equals(goal.getFreqDayString()))
                                    || (goal.getFrequencyType().equals(freqTypes[3]) && isCorrectMonthlyOccurrence(goal))
                                    || (goal.getFrequencyType().equals(freqTypes[4]) && isCorrectYearlyOccurrence(goal))
                                    || (goal.getListCategory().equals(currListCategory)))
                            .collect(Collectors.toList());
            //temporary fix for handling tomorrow and pending
            if (currListCategory == "Tomorrow") {
                filteredGoals[0] = filteredGoals[0].stream()
                        .filter(goal -> isItTomorrow(goal))
                        .collect(Collectors.toList());
            }
            if (inFocusMode) {
                filteredGoals[0] = filteredGoals[0].stream()
                            .filter(goal -> goal.getContext().equals(focusContext))
                            .collect(Collectors.toList());
            }
            shownGoalsCount = filteredGoals[0].size();
            adapter.updateGoals(filteredGoals[0]);
            updateNoGoalsVisibility();
        });
    }

    // method for when the focus mode button is clicked
    public void toggleFocusMode(View view) {
        final String[] contexts = getResources().getStringArray(R.array.contexts);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Focus Context")
                .setItems(contexts, (dialog, which) -> {
                    // Apply the focus mode with the selected context
                    focusContext = contexts[which];
                    inFocusMode = true;
                    filterChanges();
                })
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Clear Focus", (dialog, which) -> clearFocusMode());
        builder.create().show();
    }

    private void clearFocusMode() {
        inFocusMode = false;
        focusContext = "N/A";
        // Reset to observe all goals without filtering
        filterChanges();
        updateNoGoalsVisibility();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dropdown_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.dropdown_menu) {
            PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.dropdown_menu));
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                String selectedOption = menuItem.getTitle().toString();
                currListCategory = selectedOption;
                updateDate();
                filterChanges();
                updateNoGoalsVisibility();
                return true;
            });
            popupMenu.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Make selected context circle in add dialog appear chosen and others not
    private void updateContextSelection(TextView selected, TextView[] contextCircles) {
        // Iterate over all context TextViews
        for (TextView contextView : contextCircles) {
            if (contextView == selected) {
                // Apply the drawable to the selected TextView
                contextView.setForeground(ContextCompat.getDrawable(this, R.drawable.chosen_context));
            } else {
                // Remove the background from all other TextViews
                contextView.setForeground(null); // Or set to a default background if necessary
            }
        }
    }

    private TextView getSelectedContext(TextView[] contextCircles) {
        TextView chosen = null;
        for (TextView contextView : contextCircles) {
            if (contextView.getForeground() != null) {
                chosen = contextView;
            }
        }
        return chosen;
    }

    private boolean isItTomorrow(GoalEntity goal) {
        String tomorrowDate = dateFormat.format(goal.getFreqTimeInMilli());
        System.out.println("***"+tomorrowDate+"*"+dateTextView.getText().toString().substring(10)+"***");
        if (tomorrowDate.equals(dateTextView.getText().toString().substring(10))) {
            return true;
        }
        else {
            return false;
        }
    }
}

