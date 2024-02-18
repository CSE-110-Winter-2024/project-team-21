package edu.ucsd.cse110.successorator;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.room.Room;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import edu.ucsd.cse110.successorator.data.db.AppDatabase;
import edu.ucsd.cse110.successorator.data.db.GoalDao;
import edu.ucsd.cse110.successorator.data.db.GoalEntity;

public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private GoalDao goalDao;

    Button forwardButton;

    //////////
    private TextView dateTextView;
    private RecyclerView recyclerView;
    private TextView noGoalsTextView;
    private GoalsAdapter adapter;
    private List<GoalEntity> goalsList;
    private GoalsViewModel goalsViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "SuccessListDatabase").allowMainThreadQueries().build();
        goalDao = db.goalDao();

        goalsViewModel = new ViewModelProvider(this).get(GoalsViewModel.class);

        dateTextView = findViewById(R.id.DateText);
        recyclerView = findViewById(R.id.goals_recycler_view);
        noGoalsTextView = findViewById(R.id.no_goals_text);
        adapter = new GoalsAdapter(goalsList,goalsViewModel, goalDao);
        forwardButton = findViewById(R.id.forwardButton); // Find the forward button


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        goalDao.getAllGoals().observe(this, goalEntities -> {
            adapter.updateGoals(goalEntities);
            updateNoGoalsVisibility();
            updateDate();
        });

        ////////////////////////////

        /*// Initialize the GoalsViewModel
        goalsViewModel = new ViewModelProvider(this).get(GoalsViewModel.class);

        // Find views
        dateTextView = findViewById(R.id.DateText);
        recyclerView = findViewById(R.id.goals_recycler_view);
        noGoalsTextView = findViewById(R.id.no_goals_text);
        Button forwardButton = findViewById(R.id.forwardButton); // Find the forward button

        // Initialize the list of goals
        goalsList = new ArrayList<>();

        // Initialize the adapter with the list of goals and the GoalsViewModel
        adapter = new GoalsAdapter(goalsList, goalsViewModel);

        updateDate();

        // Set the layout manager and adapter on the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);*/

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
            }
        });


    }


    /*
    LOOOOOK AT THIS





     */
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

        final EditText input = new EditText(this);
        input.setId(R.id.edit_text_goal_id);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String goalText = input.getText().toString().trim();
                if (!TextUtils.isEmpty(goalText)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            goalDao.insert(new GoalEntity(goalText, false));
                        }
                    }).start();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a goal", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();



        ////////////////////////////
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Goal");

        final EditText input = new EditText(this);

        // Assign the ID to the EditText
        input.setId(R.id.edit_text_goal_id);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String goal = input.getText().toString().trim();
                if (!TextUtils.isEmpty(goal)) {
                    goalsList.add(goal);
                    adapter.notifyDataSetChanged();
                    updateNoGoalsVisibility();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a goal", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

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

    private final BroadcastReceiver dateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_DATE_CHANGED.equals(intent.getAction())) {
                // Date has changed, update the UI
                updateDate();
                adapter.removeCheckedOffGoals();
            }
        }
    };



}
