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

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import edu.ucsd.cse110.successorator.data.db.GoalDao;
import edu.ucsd.cse110.successorator.data.db.GoalEntity;

import edu.ucsd.cse110.successorator.data.db.GoalDao;
import edu.ucsd.cse110.successorator.data.db.GoalEntity;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private List<GoalEntity> goalsList;
    private GoalDao goalDao;

    public GoalsAdapter(List<GoalEntity> goalsList, GoalDao goalDao) {
        this.goalsList = goalsList;
        this.goalDao = goalDao;
    }

    public void updateGoals(List<GoalEntity> newGoals) {
        Collections.sort(newGoals, (o1, o2) -> Boolean.compare(o1.isChecked(), o2.isChecked()));
        this.goalsList = newGoals;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GoalEntity goal = goalsList.get(position);

        if (goal != null) {
            holder.goalTextView.setText(goal.getGoalText());
            holder.goalCheckBox.setChecked(goal.isChecked());

            String context = goal.getContext();
            if (context != null && !context.isEmpty()) {
                holder.contextTextView.setText(context);
                holder.contextTextView.setVisibility(View.VISIBLE);
            } else {
                holder.contextTextView.setVisibility(View.GONE);
            }

            if (goal.isChecked()) {
                holder.goalTextView.setPaintFlags(holder.goalTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.goalTextView.setPaintFlags(holder.goalTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            holder.goalCheckBox.setOnClickListener(v -> {
                goal.setChecked(holder.goalCheckBox.isChecked());
                goalDao.update(goal);
                notifyDataSetChanged();
            });
        } else {

            holder.goalTextView.setText("");
            holder.goalCheckBox.setChecked(false);
            holder.contextTextView.setVisibility(View.GONE);
        }
    }



    @Override
    public int getItemCount() {
        return goalsList.size();
    }

    // Provide a reference to the views for each data item
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView goalTextView;
        CheckBox goalCheckBox;

        TextView contextTextView; // TextView for context display

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            goalTextView = itemView.findViewById(R.id.goal_text_view);
            goalCheckBox = itemView.findViewById(R.id.goal_checkbox);
            contextTextView = itemView.findViewById(R.id.context_text_view);
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
