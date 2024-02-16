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
    private List<Boolean> completionStatusList;
    private GoalsViewModel goalsViewModel;

    public GoalsAdapter(List<String> goalsList, List<Boolean> completionStatusList, GoalsViewModel viewModel) {
        this.goalsList = goalsList;
        this.completionStatusList = completionStatusList;
        this.goalsViewModel = viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String goal = goalsList.get(holder.getAdapterPosition()); // Get the current position using getAdapterPosition()
        boolean isChecked = completionStatusList.get(holder.getAdapterPosition()); // Get completion status using the current position

        holder.goalTextView.setText(goal);
        holder.goalCheckBox.setChecked(isChecked);

        holder.goalCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int adapterPosition = holder.getAdapterPosition(); // Get the current adapter position
                if (adapterPosition != RecyclerView.NO_POSITION) { // Check if position is valid
                    completionStatusList.set(adapterPosition, isChecked);
                    // Notify the ViewModel about the checkbox change
                    String goal = goalsList.get(adapterPosition);
                    if (isChecked) {
                        goalsViewModel.markGoalAsCheckedOff(goal);
                    } else {
                        goalsViewModel.markGoalAsNotCheckedOff(goal);
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return goalsList.size();
    }

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
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

}
