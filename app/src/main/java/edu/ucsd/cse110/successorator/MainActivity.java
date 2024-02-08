package edu.ucsd.cse110.successorator;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private GoalsAdapter adapter;
    private List<String> goalsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the list of goals
        goalsList = new ArrayList<>();
        // Add some sample goals (replace this with your actual list of goals)
        goalsList.add("Read 20 pages of a book");
        goalsList.add("Workout at the gym");
        goalsList.add("Call Mom");

        // Initialize the adapter with the list of goals
        adapter = new GoalsAdapter(goalsList);

        // Set the layout manager and adapter on the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.goalsRecyclerView.setLayoutManager(layoutManager);
        binding.goalsRecyclerView.setAdapter(adapter);

        binding.addGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a dialog for adding a new goal
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Add Goal");

                // Set up the input field in the dialog
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the positive button to add the goal
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String goal = input.getText().toString().trim();

                        // Check if the input is not empty
                        if (!TextUtils.isEmpty(goal)) {
                            // Add the goal to the list and update the RecyclerView
                            goalsList.add(goal);
                            adapter.notifyDataSetChanged();
                        } else {
                            // Display a toast if the input is empty
                            Toast.makeText(MainActivity.this, "Please enter a goal", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // Set up the negative button to cancel the dialog
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // Show the dialog
                builder.show();
            }
        });

    }
}
