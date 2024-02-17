package edu.ucsd.cse110.successorator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private List<String> goalsList;
    private GoalsViewModel goalsViewModel;


    // Constructor to initialize the adapter with a list of goals
    public GoalsAdapter(List<String> goalsList, GoalsViewModel viewModel) {
        this.goalsList = goalsList;
        this.goalsViewModel = viewModel;
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
        // Get the goal at the specified position in the list
        String goal = goalsList.get(position);
        // Bind the goal text to the TextView in the ViewHolder
        holder.goalTextView.setText(goal); // Bind the goal text to the TextView, etc.
        // Set the checkbox state based on the checked off status
        holder.goalCheckBox.setChecked(goalsViewModel.isGoalCheckedOff(goal));

        // Set a listener to mark the goal as checked off when the checkbox is checked
        holder.goalCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    goalsViewModel.markGoalAsCheckedOff(goal);
                } else {
                    goalsViewModel.markGoalAsNotCheckedOff(goal);
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
        TextView goalTextView;
        CheckBox goalCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            goalTextView = itemView.findViewById(R.id.goal_text_view);
            goalCheckBox = itemView.findViewById(R.id.goal_checkbox);
        }
    }

    public void setGoalsList(List<String> goalsList) {
        this.goalsList = goalsList;
        notifyDataSetChanged();
    }
}