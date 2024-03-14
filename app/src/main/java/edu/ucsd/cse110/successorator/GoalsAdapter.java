package edu.ucsd.cse110.successorator;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    //sort goals by checked first and then by context
    public void updateGoals(List<GoalEntity> newGoals) {
        Map<String, Integer> contextOrder = Map.of(
                "Home", 1,
                "Work", 2,
                "School", 3,
                "Errands", 4
        );

        // Custom sort: First by isChecked status, then by context order
        Collections.sort(newGoals, (o1, o2) -> {
            // First compare by isChecked status
            int checkedCompare = Boolean.compare(o1.isChecked(), o2.isChecked());
            if (checkedCompare == 0) {
                // If isChecked status is the same, sort by context
                return Integer.compare(
                        contextOrder.getOrDefault(o1.getContext(), 5),
                        contextOrder.getOrDefault(o2.getContext(), 5)
                );
            }
            return checkedCompare;
        });

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
        String goalText = goalsList.get(position).getGoalText();
        GoalEntity goalEntity = goalDao.findByGoalText(goalText);

        GoalEntity goal = goalsList.get(position);
        holder.goalTextView.setText(goal.getGoalText());
        holder.goalCheckBox.setChecked(goal.isChecked());

        String context = goal.getContext();
        holder.contextTextView.setText(context.substring(0,1));

        switch(context) {
            case "Home": holder.contextTextView.setBackground(ContextCompat.getDrawable(holder.contextTextView.getContext(), R.drawable.context_home));
                break;
            case "Work": holder.contextTextView.setBackground(ContextCompat.getDrawable(holder.contextTextView.getContext(), R.drawable.context_work));
                break;
            case "School":holder.contextTextView.setBackground(ContextCompat.getDrawable(holder.contextTextView.getContext(), R.drawable.context_school));
                break;
            case "Errands":holder.contextTextView.setBackground(ContextCompat.getDrawable(holder.contextTextView.getContext(), R.drawable.context_errands));
                break;
        }

        //fixed issue if goalEntity would not be found aka null
        if (goalEntity == null) {

        }
        else {
            boolean isChecked = goalEntity.isChecked();
            holder.goalTextView.setText(goalText);
            holder.goalCheckBox.setChecked(isChecked);

            if (isChecked) {
                holder.goalTextView.setPaintFlags(holder.goalTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.goalTextView.setPaintFlags(holder.goalTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }

        holder.goalCheckBox.setOnClickListener(v -> {
            boolean checked = holder.goalCheckBox.isChecked();
            goalEntity.setChecked(checked);
            if (goalEntity.getListCategory().equals("Tomorrow") || goalEntity.getListCategory().equals("Today")) {
                goalEntity.setListCategory("Today");
            }
            goalDao.update(goalEntity);
            notifyDataSetChanged();
        });

        // Set long click listener on goalTextView
        holder.goalTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                GoalEntity clickedGoal = goalsList.get(holder.getAdapterPosition());

                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);

                popupMenu.getMenuInflater().inflate(R.menu.popup_pending_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(item -> {
                    switch(item.getTitle().toString()) {
                        case "Move to Today":
                            updateListCategory(clickedGoal, "Today");
                            return true;
                        case "Move to Tomorrow":
                            updateListCategory(clickedGoal, "Tomorrow");
                            return true;
                        case "Finish":
                            clickedGoal.setChecked(true);
                            clickedGoal.setListCategory("Today");
                            goalDao.update(clickedGoal);
                            notifyDataSetChanged();
                            return true;
                        case "Delete":
                            goalDao.delete(clickedGoal);
                            notifyDataSetChanged();
                            return true;
                    }
                    return false;
                });
                popupMenu.show();
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        if (goalsList != null) {return goalsList.size();}
        return 0;
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

    //remove checked goals if they are one-time
    public void removeCheckedOffGoals() {
        goalDao.removeCompletedFromDao();
        goalDao.uncheckRecurringGoals();
        notifyDataSetChanged(); // Notify adapter about the changes
    }

    //update the list category of a goal from long click
    private void updateListCategory(GoalEntity goal, String category) {
        goal.setListCategory(category);
        goalDao.update(goal);
        goalsList.remove(goal);
        notifyDataSetChanged();
    }
}
