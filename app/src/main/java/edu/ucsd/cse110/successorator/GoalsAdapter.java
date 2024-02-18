package edu.ucsd.cse110.successorator;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.ucsd.cse110.successorator.data.db.GoalDao;
import edu.ucsd.cse110.successorator.data.db.GoalEntity;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private List<GoalEntity> goalsList;
    private GoalDao goalDao;

    // Constructor to initialize the adapter with a list of goals
    public GoalsAdapter(List<GoalEntity> goalsList, GoalDao goalDao) {
        this.goalsList = goalsList;
        this.goalDao = goalDao;
    }

    public void updateGoals(List<GoalEntity> newGoals) {
        Collections.sort(newGoals, (o1, o2) -> Boolean.compare(o1.isChecked(), o2.isChecked()));
        this.goalsList = newGoals;
        notifyDataSetChanged();
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
        String goalText = goalsList.get(position).getGoalText();
        GoalEntity goalEntity = goalDao.findByGoalText(goalText);
        boolean isChecked = goalEntity.isChecked();

        holder.goalTextView.setText(goalText);
        holder.goalCheckBox.setChecked(isChecked);

        if (isChecked) {
            holder.goalTextView.setPaintFlags(holder.goalTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.goalTextView.setPaintFlags(holder.goalTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.goalCheckBox.setOnClickListener(v -> {
            boolean checked = holder.goalCheckBox.isChecked();
            goalEntity.setChecked(checked);

            goalDao.update(goalEntity);
            notifyDataSetChanged();
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
    }

    public void removeCheckedOffGoals() {
        goalDao.removeCompletedFromDao();
        notifyDataSetChanged(); // Notify adapter about the changes
    }

    public void setGoalsList(List<GoalEntity> goalsList) {
        this.goalsList = goalsList;
        notifyDataSetChanged();
    }
}
