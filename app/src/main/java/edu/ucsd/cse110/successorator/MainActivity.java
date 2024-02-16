package edu.ucsd.cse110.successorator;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView noGoalsTextView;
    private GoalsAdapter adapter;
    private List<String> goalsList = new ArrayList<>();
    private AppDatabase db;
    private GoalDao goalDao;
    private Consumer<Integer> onCompletionClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").allowMainThreadQueries().build();
        goalDao = db.goalDao();

        goalDao.getAllGoals().observe(this, goalEntities -> {
            Collections.sort(goalEntities, (o1, o2) -> Boolean.compare(o1.isChecked, o2.isChecked));
            List<String> sortedGoalTexts = goalEntities.stream()
                    .map(goalEntity -> goalEntity.goalText)
                    .collect(Collectors.toList());

            goalsList = new ArrayList<>(sortedGoalTexts);

            adapter.updateGoals(sortedGoalTexts);
            updateNoGoalsVisibility();
        });



        recyclerView = findViewById(R.id.goals_recycler_view);
        noGoalsTextView = findViewById(R.id.no_goals_text);
        adapter = new GoalsAdapter(goalsList, onCompletionClick, goalDao);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.add_goal_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddGoalDialog();
            }
        });
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
    }

    private void updateNoGoalsVisibility() {
        if (goalsList.isEmpty()) {
            noGoalsTextView.setVisibility(View.VISIBLE);
        } else {
            noGoalsTextView.setVisibility(View.GONE);
        }
    }
}
