package edu.ucsd.cse110.successorator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
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
    private LiveData<List<GoalEntity>> goalsList; // = new ArrayList<>();
    private String currListCategory;
    private String todayDate;
    private String tomorrowDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "SuccessListDatabase").allowMainThreadQueries().build();
        goalDao = db.goalDao();

        currListCategory = "Today";
        dateTextView = findViewById(R.id.DateText);
        recyclerView = findViewById(R.id.goals_recycler_view);
        noGoalsTextView = findViewById(R.id.no_goals_text);
        goalsList = goalDao.getGoalsByListCategory(currListCategory); //.getValue();
        adapter = new GoalsAdapter(goalsList.getValue(), goalDao);
        forwardButton = findViewById(R.id.forwardButton); // Find the forward button
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        goalDao.getGoalsByListCategory("Today").observe(this, goalEntities ->  {
            adapter.updateGoals(goalEntities);
            updateNoGoalsVisibility();
        });

        todayDate = updateDate();
        dateTextView.setText(todayDate);
        tomorrowDate = getTomorrowDate(); // so i dont have to call function every time list is selected

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

    // BUG: Skips day by one too much, so 2 days in the future instead of 1
    private void advanceTimeByOneDay() {
        // Define the date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());

        // Initialize a calendar instance
        Calendar calendar = Calendar.getInstance();

      //   Try to parse the date from the TextView, if it exists
        try {
            Date displayedDate = dateFormat.parse(todayDate);
            calendar.setTime(displayedDate);

        } catch (ParseException e) {
            // If parsing fails, the current date is used
            e.printStackTrace();
        }
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        todayDate = dateFormat.format(calendar.getTime());

        // Get tomorrow's date given we advanced the day by 1 and assign it to var
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        tomorrowDate = dateFormat.format(calendar.getTime());
        if (currListCategory.equals("Today")) {dateTextView.setText(todayDate);}
        else if (currListCategory.equals("Tomorrow")) {dateTextView.setText(tomorrowDate);}

        // Your method to remove checked-off goals
        adapter.removeCheckedOffGoals();

        // Switch goals marked 'Tomorrow' which are not checked off to 'Today' view
        goalDao.rolloverTomorrowToToday();
        adapter.notifyDataSetChanged();

    }

    private void showAddGoalDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Goal");

        final EditText input = new EditText(this);
        input.setId(R.id.edit_text_goal_id);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String goalText = input.getText().toString().trim();
                GoalEntity findSameGoal = goalDao.findByGoalText(goalText);
                if (!TextUtils.isEmpty(goalText) && findSameGoal == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            goalDao.insert(new GoalEntity(goalText, false, currListCategory));
                        }
                    }).start();
                } else if (findSameGoal != null) {
                    Toast.makeText(MainActivity.this, "You already have this goal added.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a goal", Toast.LENGTH_SHORT).show();
                }
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

    private String updateDate() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());

        // Update the TextView with the current date
        //dateTextView.setText(currentDate);
    }
    private String getTomorrowDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1); // add one day to get tmrw date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
        // i used the same pattern as seen before
        return dateFormat.format(calendar.getTime());
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

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    String selectedOption = menuItem.getTitle().toString();
                    currListCategory = selectedOption;
                    LiveData<List<GoalEntity>> goalsLiveData = goalDao.getGoalsByListCategory(selectedOption);

                    switch (selectedOption) {
                        case "Today":
                            dateTextView.setText(todayDate);
                            break;
                        case "Tomorrow":
                            dateTextView.setText(tomorrowDate);
                            break;
                        case "Pending":
                            dateTextView.setText("Pending");
                            break;
                        case "Recurring":
                            dateTextView.setText("Recurring");
                            break;
                    }

                    goalsLiveData.observe(MainActivity.this, new Observer<List<GoalEntity>>() {
                        @Override
                        public void onChanged(List<GoalEntity> goalEntities) {
                            adapter.setGoalsList(goalEntities);
                        }
                    });

                    return true;
                }
            });

            popupMenu.show();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}