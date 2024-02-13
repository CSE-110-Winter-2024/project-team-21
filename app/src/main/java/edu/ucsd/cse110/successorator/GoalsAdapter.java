package edu.ucsd.cse110.successorator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CheckBox;
import android.content.Context;




import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private List<String> goalsList;
    private Context context;
    private GoalFinished goalFinished;


    // Constructor to initialize the adapter with a list of goals
    public GoalsAdapter(List<String> goalsList, Context context ) {
        this.goalsList = goalsList;
        this.context = context;
        goalFinished = new GoalFinished(context);
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

        holder.goalCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String goalName = goalsList.get(holder.getAdapterPosition()); // Get the goal name
                if (isChecked) {
                    // Mark goal as completed when checkbox is checked
                    goalFinished.markGoalCompleted(goalName);
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
        CheckBox goalCheckBox; // Add reference to the checkbox view

        public ViewHolder(View itemView) {
            super(itemView);
            goalTextView = itemView.findViewById(R.id.goal_text_view);
            goalCheckBox = itemView.findViewById(R.id.goal_checkbox); // Initialize the checkbox view
        }
    }}