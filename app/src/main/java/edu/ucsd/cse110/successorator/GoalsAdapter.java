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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
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
        String goalText = goalsList.get(position).getGoalText();
        GoalEntity goalEntity = goalDao.findByGoalText(goalText);

        //fixed issue if goalEntity would not be found aka null
        if (goalEntity == null) {

        } else {
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

            goalDao.update(goalEntity);
            notifyDataSetChanged();
        });

        // Set long click listener on goalTextView
        holder.goalTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String selectedGoalText = holder.goalTextView.getText().toString();
                GoalEntity clickedGoal = goalsList.get(holder.getAdapterPosition());
                GoalEntity alsoclicked = goalDao.findByGoalText(selectedGoalText);
                int clickedGoalId = clickedGoal.getId();

                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                      //  if (item.getItemId() == R.id.goal_text_view) {
                        Log.d("Test", "Were here");
                        switch(item.getTitle().toString()) {
                            case "Today":
                                Log.d("PopupMenu", "Option Today clicked");
                                updateListCategory(clickedGoal, "Today");
                                return true;
                            case "Tomorrow":
                                updateListCategory(clickedGoal, "Tomorrow");
                                return true;
                            case "Pending":
                                updateListCategory(clickedGoal, "Pending");
                                return true;
                            case "Recurring":
                                updateListCategory(clickedGoal, "Recurring");
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return true;
            }
        });
    }

    private void updateListCategory(GoalEntity goal, String category) {
        goal.setListCategory(category);
        goalDao.update(goal);
        goalsList.remove(goal);
        notifyDataSetChanged();
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
