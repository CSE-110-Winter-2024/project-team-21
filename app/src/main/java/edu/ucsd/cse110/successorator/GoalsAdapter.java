package edu.ucsd.cse110.successorator;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import edu.ucsd.cse110.successorator.data.db.GoalDao;
import edu.ucsd.cse110.successorator.data.db.GoalEntity;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private List<String> goalsList;
    Consumer<Integer> onCompletionClick;

    GoalDao goalDao;



    public GoalsAdapter(List<String> goalsList, Consumer<Integer> onCompletionClick, GoalDao goalDao) {
        this.goalsList = goalsList;
        this.onCompletionClick = onCompletionClick;
        this.goalDao = goalDao;

    }

    public void updateGoals(List<String> newGoals) {
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
        String goalText = goalsList.get(position);
        GoalEntity goalEntity = goalDao.findByGoalText(goalText);
        boolean isChecked = goalEntity.isChecked;

        holder.goalTextView.setText(goalText);
        holder.goalCheckBox.setChecked(isChecked);

        if (isChecked) {
            holder.goalTextView.setPaintFlags(holder.goalTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.goalTextView.setPaintFlags(holder.goalTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.goalCheckBox.setOnClickListener(v -> {
            boolean checked = holder.goalCheckBox.isChecked();
            goalEntity.isChecked = checked;

            new Thread(() -> {
                goalDao.update(goalEntity);
            }).start();

            notifyDataSetChanged();
        });
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
