package edu.ucsd.cse110.successorator.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.app.ui.list.GoalsAdapter;
import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;


public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView noGoalsTextView;
    private GoalsAdapter adapter;
    private List<Goal> goalsList;

    private ActivityMainBinding view;
    private MainViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //ADDED
        setTitle(R.string.app_title);

        this.view = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());

        //END ADDED
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        var itemId = item.getItemId();

        if (itemId == R.id.action_bar_menu_add_goal) {
            showAddGoalDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateNoGoalsVisibility() {
        if (goalsList.isEmpty()) {
            noGoalsTextView.setVisibility(View.VISIBLE);
        } else {
            noGoalsTextView.setVisibility(View.GONE);
        }
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
                    goalsList.add(0, new Goal(model.getGoalCount()+1,goal));
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
}
