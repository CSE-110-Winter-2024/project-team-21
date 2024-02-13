package edu.ucsd.cse110.successorator;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.ImageButton;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView noGoalsTextView;
    private GoalsAdapter adapter;
    private List<String> goalsList;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private static final int SPEECH_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the list of goals
        goalsList = new ArrayList<>();

        // Find views
        recyclerView = findViewById(R.id.goals_recycler_view);
        noGoalsTextView = findViewById(R.id.no_goals_text);

        // Initialize the adapter with the list of goals
        adapter = new GoalsAdapter(goalsList);

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

        // Initialize SpeechRecognizer and Intent
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
    }

    private AlertDialog dialog;
    private void showAddGoalDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Goal");

        // Inflate the custom layout for the dialog
        View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_add_goal, null);
        final EditText input = dialogLayout.findViewById(R.id.edit_text_goal);
        ImageButton micButton = dialogLayout.findViewById(R.id.button_mic);
        ImageButton closeButton = dialogLayout.findViewById(R.id.button_close);

        // Set OnClickListener for the mic button to start speech recognition
        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });

        // Set OnClickListener for the close button to dismiss the dialog
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        builder.setView(dialogLayout);

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

        dialog = builder.create(); // Create the dialog
        dialog.show(); // Show the dialog
    }

    private void updateNoGoalsVisibility() {
        if (goalsList.isEmpty()) {
            noGoalsTextView.setVisibility(View.VISIBLE);
        } else {
            noGoalsTextView.setVisibility(View.GONE);
        }
    }

    // Method to start speech recognition
    private void startSpeechToText() {
        try {
            startActivityForResult(speechRecognizerIntent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(MainActivity.this, "Speech recognition not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the result from speech recognition
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = result.get(0);
            // Now spoken text can be used as the goal and added to the list
            addGoal(spokenText);
        }
    }

    private void addGoal(String goal) {
        if (!TextUtils.isEmpty(goal)) {
            goalsList.add(goal);
            adapter.notifyDataSetChanged();
            updateNoGoalsVisibility();
        } else {
            Toast.makeText(MainActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
        }
    }

}
