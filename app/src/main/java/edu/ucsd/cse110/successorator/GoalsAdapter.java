package edu.ucsd.cse110.successorator;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
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
    private PopupWindow popupMenu1;



    public GoalsAdapter(List<GoalEntity> goalsList, GoalDao goalDao, PopupWindow popupMenu1) {
        this.goalsList = goalsList;
        this.goalDao = goalDao;
        this.popupMenu1 = popupMenu1;
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

            goalDao.update(goalEntity);
            notifyDataSetChanged();
        });
    }


    @Override
    public int getItemCount() {
        return goalsList.size();
    }

    // Provide a reference to the views for each data item
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView goalTextView;
        CheckBox goalCheckBox;
        Button recurButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            goalTextView = itemView.findViewById(R.id.goal_text_view);
            goalCheckBox = itemView.findViewById(R.id.goal_checkbox);
            recurButton = itemView.findViewById(R.id.recurButton);

            recurButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the click event for recurButton here
                    popupMenu1.showAtLocation(itemView, Gravity.CENTER, 0, 0);
                }
            });
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