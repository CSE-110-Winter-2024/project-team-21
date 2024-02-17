package edu.ucsd.cse110.successorator;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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


public class MainActivity extends AppCompatActivity {
    private TextView dateTextView;
    private RecyclerView recyclerView;
    private TextView noGoalsTextView;
    private GoalsAdapter adapter;
    private List<String> goalsList;
    private GoalsViewModel goalsViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the GoalsViewModel
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
        recyclerView.setAdapter(adapter);

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

    private void advanceTimeByOneDay() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1); // Advance the date by one day

        // Update the TextView with the new date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        dateTextView.setText(currentDate);
        goalsViewModel.removeCheckedOffGoals();
    }

    private void showAddGoalDialog() {
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
        if (goalsList.isEmpty()) {
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





    /* BroadcastReceiver to detect date changes
    private BroadcastReceiver dateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Clear checked off MITs when the date changes
            goalsViewModel.clearCheckedMITs();
        }
    };*/
}