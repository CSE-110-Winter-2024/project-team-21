package edu.ucsd.cse110.successorator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
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
        return new ViewHolder(view); // Remove the second argument
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
                Log.d("GoalsAdapter", "Checkbox clicked, isChecked: " + isChecked);
                if (isChecked) {
                    holder.markGoalAsCheckedOff(); // Call the new method
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
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView goalTextView;
        CheckBox goalCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            goalTextView = itemView.findViewById(R.id.goal_text_view);
            goalCheckBox = itemView.findViewById(R.id.goal_checkbox);
        }

        private void markGoalAsCheckedOff() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                String goal = goalsList.get(position);
                goalsViewModel.markGoalAsCheckedOff(goal);
//                goalsViewModel.removeCheckedOffGoals(); // Remove checked-off goals from the list
//                goalsList.remove(position); // Remove the checked goal from the list
//                notifyItemRemoved(position); // Notify adapter about the item removal
            }
        }

    }
//    public void removeCheckedOffGoals() {
//        goalsViewModel.removeCheckedOffGoals();
//        notifyDataSetChanged(); // Notify adapter about the changes
//    }
    public void removeCheckedOffGoals() {
        for (String goal : goalsList) {
            int position = goalsList.indexOf(goal);
            // Add the goal to the updated list if it's not checked off
            if (goalsViewModel.isGoalCheckedOff(goal)) {
                goalsList.remove(position); // Remove the checked goal from the list
                notifyItemRemoved(position); // Notify adapter about the item removal

            }
        }
        notifyDataSetChanged(); // Notify adapter about the changes
    }

    public void setGoalsList(List<String> goalsList) {
        this.goalsList = goalsList;
        notifyDataSetChanged();
    }
}
