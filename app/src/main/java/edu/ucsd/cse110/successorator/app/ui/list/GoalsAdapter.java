package edu.ucsd.cse110.successorator.app.ui.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ArrayAdapter;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private List<Goal> goalsList;

    public GoalsAdapter(List<Goal> goalsList) {
        this.goalsList = goalsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String goal = goalsList.get(position).getGoalText();
        holder.goalTextView.setText(goal);
        /*boolean isChecked = checkedStates.getOrDefault(goal, false);
        holder.goalCheckBox.setChecked(isChecked);

        if (isChecked) {
            holder.goalTextView.setPaintFlags(holder.goalTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.goalTextView.setPaintFlags(holder.goalTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.goalCheckBox.setOnClickListener(v -> {
            boolean checked = holder.goalCheckBox.isChecked();
            checkedStates.put(goal, checked);
            if (checked) {
                // Move checked goal to the bottom
                goalsList.remove(goal);
                goalsList.add(goalsList.size()-completed++, goal);
            } else {
                // Move unchecked goal to the top
                goalsList.remove(goal);
                goalsList.add(0, goal);
                completed--;
            }
            notifyDataSetChanged();

        });*/
    }

    @Override
    public int getItemCount() {
        return goalsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView goalTextView;
        CheckBox goalCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            goalTextView = itemView.findViewById(R.id.goal_text_view);
            goalCheckBox = itemView.findViewById(R.id.goal_checkbox);
        }
    }
}
