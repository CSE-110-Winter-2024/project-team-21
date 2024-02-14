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

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private List<String> goalsList;
    private Map<String, Boolean> checkedStates = new HashMap<>();
    private int completed;
    Consumer<Integer> onCompletionClick;



    public GoalsAdapter(List<String> goalsList, Consumer<Integer> onCompletionClick) {
        this.goalsList = goalsList;
        this.onCompletionClick = onCompletionClick;
        // Initialize all goals as unchecked
        for (String goal : goalsList) {
            checkedStates.put(goal, false);
        }
        completed = 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String goal = goalsList.get(position);
        holder.goalTextView.setText(goal);
        boolean isChecked = checkedStates.getOrDefault(goal, false);
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
