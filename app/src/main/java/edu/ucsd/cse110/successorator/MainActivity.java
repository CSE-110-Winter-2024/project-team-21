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
    final String[] occurrences = {"1st", "2nd", "3rd", "4th", "5th"};

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
        addGoalButton.setOnClickListener(v ->
                showAddGoalDialogGeneral()
        );

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
                switch(currListCategory) {
                    case "Today":
                    case "Tomorrow": addGoalButton.setOnClickListener(v ->
                            showAddGoalDialogGeneral()
                    );
                        break;
                    case "Pending": addGoalButton.setOnClickListener(v ->
                            showAddGoalDialogPending()
                    );
                        break;
                    case "Recurring": addGoalButton.setOnClickListener(v ->
                            showAddGoalDialogRecurring()
                    );
                        break;
                }
                // Set OnClickListener for FloatingActionButton to add new goals

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
        // Switch goals marked 'Tomorrow' which are not checked off to 'Today' view
        adapter.rolloverTomorrowToToday();
        filterChanges();
    }

    //Today or Tomorrow Add Dialog
    private void showAddGoalDialogGeneral() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Goal");

        // Inflate the custom layout
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_todaytmr, null);
        builder.setView(dialogView);

        //initialize components of add dialog
        final EditText editTextGoal = dialogView.findViewById(R.id.text_pending_edit_text_goal_id);
        final TextView homeContext = dialogView.findViewById(R.id.homeContext);
        final TextView workContext = dialogView.findViewById(R.id.workContext);
        final TextView schoolContext = dialogView.findViewById(R.id.schoolContext);
        final TextView errandsContext = dialogView.findViewById(R.id.errandsContext);
        final TextView textWeekly = dialogView.findViewById(R.id.text_today_weekly);
        final TextView textMonthly = dialogView.findViewById(R.id.text_today_monthly);
        final TextView textYearly = dialogView.findViewById(R.id.text_today_yearly);
        final RadioButton radioBtnOneTime = dialogView.findViewById(R.id.radio_btn_onetime);
        final RadioButton radioBtnDaily = dialogView.findViewById(R.id.radio_btn_daily);
        final RadioButton radioBtnWeekly = dialogView.findViewById(R.id.radio_btn_weekly);
        final RadioButton radioBtnMonthly = dialogView.findViewById(R.id.radio_btn_monthly);
        final RadioButton radioBtnYearly = dialogView.findViewById(R.id.radio_btn_yearly);

        //initialize onClick for contextCircles
        final TextView[] contextCircles = {homeContext, workContext, schoolContext, errandsContext};

        for (TextView contextView : contextCircles) {
            contextView.setOnClickListener(v -> {
                TextView chosen = (TextView) v;
                updateContextSelection(chosen , contextCircles);
            });
        }

        Calendar calendar = (currListCategory.equals("Today")) ? (Calendar) today.clone() : (Calendar) tomorrow.clone();
        String theDay = daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK)-1];
        String occurred = findOccurrence(calendar.get(Calendar.DAY_OF_MONTH));

        //initialize texts of right side buttons
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.getDefault());

        // Initially set the button text to "Select Starting Date"
        textWeekly.setText(daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK)-1]);
        textMonthly.setText(occurred + " " + theDay);
        textYearly.setText(sdf.format(calendar.getTime()));

        View.OnClickListener radioButtonClickListener = v -> {
            RadioButton rb = (RadioButton) v;
            clearRadioGroupSelection(rb);
            rb.setChecked(true);
        };

        //initialize radioButtons and onClickListeners
        radioButtons = new ArrayList<>(List.of(radioBtnOneTime, radioBtnDaily, radioBtnWeekly, radioBtnMonthly, radioBtnYearly));
        for (RadioButton radio : radioButtons) {
            radio.setOnClickListener(radioButtonClickListener);
        }

        // Handle the "Add" button
        builder.setPositiveButton("Save", (dialog, which) -> {

            //initialize initial values
            final String goalText = editTextGoal.getText().toString().trim();
            Integer freqMonth = calendar.get(Calendar.MONTH)+1, freqOccur = Integer.valueOf(occurred.substring(0,1));
            long freqTimeInMilli = calendar.getTimeInMillis();
            String freqType = "", selectedContext = contextStrings.get(getSelectedContext(contextCircles).getText().toString());


            //Defining internal variables of goal based on what frequency type it is
            if (radioBtnOneTime.isChecked()) {
                freqType = freqTypes[0];
            } else if (radioBtnDaily.isChecked()) {
                freqType = freqTypes[1];
            } else if (radioBtnWeekly.isChecked()) {
                freqType = freqTypes[2];
            } else if (radioBtnMonthly.isChecked()) {
                freqType = freqTypes[3];
            } else if (radioBtnYearly.isChecked()) {
                freqType = freqTypes[4];
            }

            //creating the goal to be inserted and variables for if statement
            GoalEntity complete = new GoalEntity(goalText, false, selectedContext, freqType, currListCategory, theDay, freqTimeInMilli, freqOccur, freqMonth);
            final long startTime = calendar.getTimeInMillis();
            final long currentTime = today.getTimeInMillis();
            boolean radioBtnsAllUnchecked = true;

            GoalEntity findSameGoal = goalDao.findByGoalText(goalText); //Check if same goalText is in goals

            for (RadioButton checkChecked : radioButtons) {
                if (checkChecked.isChecked()) {
                    radioBtnsAllUnchecked = false;
                }
            }

            //if statement checking what we do not have based through writeup/piazza (fix)
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

    //Recurring Tab Add Dialog
    private void showAddGoalDialogRecurring() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Goal");

        // Inflate the custom layout
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_recurring, null);
        builder.setView(dialogView);

        //initialize components of add dialog
        final EditText editTextGoal = dialogView.findViewById(R.id.text_pending_edit_text_goal_id);
        final TextView homeContext = dialogView.findViewById(R.id.homeContext);
        final TextView workContext = dialogView.findViewById(R.id.workContext);
        final TextView schoolContext = dialogView.findViewById(R.id.schoolContext);
        final TextView errandsContext = dialogView.findViewById(R.id.errandsContext);
        final TextView textDaily = dialogView.findViewById(R.id.text_recur_daily);
        final TextView textWeekly = dialogView.findViewById(R.id.text_recur_weekly);
        final TextView textMonthly = dialogView.findViewById(R.id.text_recur_monthly);
        final TextView textYearly = dialogView.findViewById(R.id.text_recur_yearly);
        final RadioButton radioBtnDaily = dialogView.findViewById(R.id.radio_btn_daily);
        final RadioButton radioBtnWeekly = dialogView.findViewById(R.id.radio_btn_weekly);
        final RadioButton radioBtnMonthly = dialogView.findViewById(R.id.radio_btn_monthly);
        final RadioButton radioBtnYearly = dialogView.findViewById(R.id.radio_btn_yearly);
        final Button dateButton = dialogView.findViewById(R.id.btn_selectDate);

        //initialize onClick for contextCircles
        final TextView[] contextCircles = {homeContext, workContext, schoolContext, errandsContext};

        for (TextView contextView : contextCircles) {
            contextView.setOnClickListener(v -> {
                TextView chosen = (TextView) v;
                updateContextSelection(chosen , contextCircles);
            });
        }

        Calendar calendar = (Calendar) today.clone();
        String theDay = daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK)-1];
        String occurred = findOccurrence(calendar.get(Calendar.DAY_OF_MONTH));

        //initialize texts of right side buttons
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.getDefault());

        //initialize textViews initial
        textWeekly.setText(daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK)-1]);
        textMonthly.setText(occurred + " " + theDay);
        textYearly.setText(sdf.format(calendar.getTime()));

        View.OnClickListener radioButtonClickListener = v -> {
            RadioButton rb = (RadioButton) v;
            clearRadioGroupSelection(rb);
            rb.setChecked(true);
        };

        // DatePickerDialog One-time logic
        DatePickerDialog.OnDateSetListener dateRecur = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Update the button text with the selected date
            dateButton.setText(dateFormat.format(calendar.getTime()));

            //initialize textViews based on choice
            textWeekly.setText(daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK)-1]);
            textMonthly.setText(findOccurrence(calendar.get(Calendar.DAY_OF_MONTH)) + " " + daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK)-1]);
            textYearly.setText(sdf.format(calendar.getTime()));
        };

        //initialize date picker onclicks
        dateButton.setOnClickListener(view -> new DatePickerDialog(MainActivity.this, dateRecur,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());

        //initialize radioButtons and onClickListeners
        radioButtons = new ArrayList<>(List.of(radioBtnDaily, radioBtnWeekly, radioBtnMonthly, radioBtnYearly));

        for (RadioButton radio : radioButtons) {
            radio.setOnClickListener(radioButtonClickListener);
        }

        // Handle the "Add" button
        builder.setPositiveButton("Save", (dialog, which) -> {

            //initialize initial values
            final String goalText = editTextGoal.getText().toString().trim();
            Integer freqMonth = calendar.get(Calendar.MONTH)+1, freqOccur = Integer.valueOf(occurred.substring(0,1));
            long freqTimeInMilli = calendar.getTimeInMillis();
            String freqType = "", selectedContext = contextStrings.get(getSelectedContext(contextCircles).getText().toString());


            //Defining internal variables of goal based on what frequency type it is
            if (radioBtnDaily.isChecked()) {
                freqType = freqTypes[1];
            } else if (radioBtnWeekly.isChecked()) {
                freqType = freqTypes[2];
            } else if (radioBtnMonthly.isChecked()) {
                freqType = freqTypes[3];
            } else if (radioBtnYearly.isChecked()) {
                freqType = freqTypes[4];
            }

            //creating the goal to be inserted and variables for if statement
            GoalEntity complete = new GoalEntity(goalText, false, selectedContext, freqType, currListCategory, theDay, freqTimeInMilli, freqOccur, freqMonth);
            boolean radioBtnsAllUnchecked = true;

            GoalEntity findSameGoal = goalDao.findByGoalText(goalText); //Check if same goalText is in goals

            for (RadioButton checkChecked : radioButtons) {
                if (checkChecked.isChecked()) {
                    radioBtnsAllUnchecked = false;
                }
            }

            //if statement checking what we do not have based through writeup/piazza (fix)
            if (TextUtils.isEmpty(goalText)) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Please enter a goal.", Toast.LENGTH_SHORT).show());
            } else if (radioBtnsAllUnchecked) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Please check a goal frequency.", Toast.LENGTH_SHORT).show());
            } else if (findSameGoal != null) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "You already have this goal added.", Toast.LENGTH_SHORT).show());
            }
            else {
                new Thread(() -> goalDao.insert(complete)).start();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    //Pending Tab Add Dialog
    private void showAddGoalDialogPending() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Goal");

        // Inflate the custom layout
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_pending, null);
        builder.setView(dialogView);

        //initialize components of add dialog
        final EditText editTextGoal = dialogView.findViewById(R.id.text_pending_edit_text_goal_id);
        final TextView homeContext = dialogView.findViewById(R.id.homeContext);
        final TextView workContext = dialogView.findViewById(R.id.workContext);
        final TextView schoolContext = dialogView.findViewById(R.id.schoolContext);
        final TextView errandsContext = dialogView.findViewById(R.id.errandsContext);

        //initialize onClick for contextCircles
        final TextView[] contextCircles = {homeContext, workContext, schoolContext, errandsContext};

        for (TextView contextView : contextCircles) {
            contextView.setOnClickListener(v -> {
                TextView chosen = (TextView) v;
                updateContextSelection(chosen , contextCircles);
            });
        }

        // Handle the "Add" button
        builder.setPositiveButton("Save", (dialog, which) -> {

            //initialize initial values
            final String goalText = editTextGoal.getText().toString().trim();
            String selectedContext = contextStrings.get(getSelectedContext(contextCircles).getText().toString());


            //creating the goal to be inserted and variables for if statement
            GoalEntity complete = new GoalEntity(goalText, false, selectedContext, "", currListCategory, "", 0, 0, 0);

            boolean radioBtnsAllUnchecked = true;

            GoalEntity findSameGoal = goalDao.findByGoalText(goalText); //Check if same goalText is in goals

            for (RadioButton checkChecked : radioButtons) {
                if (checkChecked.isChecked()) {
                    radioBtnsAllUnchecked = false;
                }
            }

            //if statement checking what we do not have based through writeup/piazza (fix)
            if (TextUtils.isEmpty(goalText)) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Please enter a goal.", Toast.LENGTH_SHORT).show());
            } else if (radioBtnsAllUnchecked) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Please check a goal frequency.", Toast.LENGTH_SHORT).show());
            } else if (findSameGoal != null) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "You already have this goal added.", Toast.LENGTH_SHORT).show());
            }
            else {
                new Thread(() -> goalDao.insert(complete)).start();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    //HELPER FUNCTIONS

    //update "No goals for the day" visibility
    private void updateNoGoalsVisibility() {
        if (shownGoalsCount == 0 && dateTextView.getText().toString().equals(allFormattedToday)) {
            noGoalsTextView.setVisibility(View.VISIBLE);
        } else {
            noGoalsTextView.setVisibility(View.GONE);
        }
    }

    // Update the dateTextView with the current date
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

    //Check whether a given goal should appear based on monthly definition
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

    //Return occurrence index for a Calendar DayOfMonth
    private String findOccurrence(int dayOfMonth) {
        return occurrences[((dayOfMonth - 1) / 7)];
    }

    //Check whether goal should appear based on yearly definition
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

    //Check leap year
    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    //Check if last month was a thirty day month
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

    //Logic for selecting only one frequency type
    private void clearRadioGroupSelection(RadioButton selectedRadioButton) {
        for (RadioButton rb : radioButtons) {
            if (!rb.equals(selectedRadioButton)) {
                rb.setChecked(false);
            }
        }
    }

    //Dynamically change the goals that appear based upon what user wants (fix listCategory based)
    private void filterChanges() {
        final List<GoalEntity>[] filteredGoals = new List[]{new ArrayList<>()};
        goalDao.getAllGoals().observe(this, goalEntities -> {
            filteredGoals[0] = goalEntities.stream().filter(goal ->
                            goal.getFrequencyType().equals(freqTypes[0])
                                    || (goal.getFrequencyType().equals(freqTypes[1]) && today.getTimeInMillis() >= goal.getFreqTimeInMilli())
                                    || (goal.getFrequencyType().equals(freqTypes[2]) && daysOfWeek[today.get(Calendar.DAY_OF_WEEK) - 1].equals(goal.getFreqDayString()))
                                    || (goal.getFrequencyType().equals(freqTypes[3]) && isCorrectMonthlyOccurrence(goal))
                                    || (goal.getFrequencyType().equals(freqTypes[4]) && isCorrectYearlyOccurrence(goal))
                                    || (goal.getListCategory().equals(currListCategory) ))
                            .collect(Collectors.toList());
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

    //Turn off Focus Mode
    private void clearFocusMode() {
        inFocusMode = false;
        focusContext = "N/A";
        // Reset to observe all goals without filtering
        filterChanges();
        updateNoGoalsVisibility();
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

    //Select only one context Circle
    private TextView getSelectedContext(TextView[] contextCircles) {
        TextView chosen = null;
        for (TextView contextView : contextCircles) {
            if (contextView.getForeground() != null) {
                chosen = contextView;
            }
        }
        return chosen;
    }

    //Does the goal appear tomorrow
    private boolean isItTomorrow(GoalEntity goal) {
        String tomorrowDate = dateFormat.format(goal.getFreqTimeInMilli());
        if (tomorrowDate.equals(dateTextView.getText().toString().substring(10))) {
            return true;
        }
        else {
            return false;
        }
    }

}

