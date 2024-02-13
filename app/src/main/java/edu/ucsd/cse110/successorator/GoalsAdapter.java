package edu.ucsd.cse110.successorator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import edu.ucsd.cse110.successorator.GoalFinished;
import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private List<String> goalsList;
    private Context context;
    private GoalFinished goalFinished;

    // Constructor to initialize the adapter with a list of goals
    public GoalsAdapter(List<String> goalsList, Context context, GoalFinished goalFinished) {
        this.goalsList = goalsList;
        this.context = context;
        this.goalFinished = goalFinished;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view by inflating the layout for a single item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String goal = goalsList.get(position);
        holder.goalTextView.setText(goal);

        holder.goalCheckBox.setOnCheckedChangeListener(null); // Avoid triggering listener during binding
        holder.goalCheckBox.setChecked(isGoalCompleted(goal));

        holder.goalCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    goalFinished.markGoalCompleted(goal); // Mark goal as completed
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return goalsList.size();
    }

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Define the ViewHolder with member variables for any views that will be set as the rows are rendered.
        // In this case, there is a TextView to display the goal text
        TextView goalTextView;
        CheckBox goalCheckBox;


        // Constructor to initialize the ViewHolder with the provided view
        public ViewHolder(View itemView) {
            super(itemView);
            goalTextView = itemView.findViewById(R.id.goal_text_view);
            goalCheckBox = itemView.findViewById(R.id.goal_checkbox);
        }

    }
    private boolean isGoalCompleted(String goalName) {
        // Use GoalFinished to check if the goal is completed
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(goalName, false);
    }

}