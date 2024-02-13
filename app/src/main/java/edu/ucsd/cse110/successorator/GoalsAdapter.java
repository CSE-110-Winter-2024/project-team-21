package edu.ucsd.cse110.successorator;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.function.Consumer;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private List<String> goalsList;
    Consumer<Integer> onCompletionClick;
    // Constructor to initialize the adapter with a list of goals
    public GoalsAdapter(List<String> goalsList, Consumer<Integer>onCompletionClick) {
        this.goalsList = goalsList;
        this.onCompletionClick = onCompletionClick;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view by inflating the layout for a single item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the goal at the specified position in the list
        String goal = goalsList.get(position);

        // Bind the goal text to the TextView in the ViewHolder
        holder.goalTextView.setText(goal); // Bind the goal text to the TextView, etc.

        // Set up the OnClickListener for the CheckBox
        // maybe simplify change later (dylan)
        holder.goalCheckBox.setOnClickListener(v -> {
            boolean isChecked = holder.goalCheckBox.isChecked();
            if (isChecked) {
                // Apply strikethrough effect
                holder.goalTextView.setPaintFlags(holder.goalTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                notifyItemMoved(position, goalsList.size()-1);
                String item = goalsList.remove(position); // Remove and capture the item
                goalsList.add(item); // Add it back at the end
            } else {
                // Remove strikethrough effect
                holder.goalTextView.setPaintFlags(holder.goalTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return goalsList.size();
    }

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Define the ViewHolder with member variables for any views that will be set as the rows are rendered.
        // In this case, there is a TextView to display the goal text
        TextView goalTextView;
        CheckBox goalCheckBox;

        // Constructor to initialize the ViewHolder with the provided view
        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views using findViewById
            goalTextView = itemView.findViewById(R.id.goal_text_view); // Replace R.id.goal_text_view with the actual ID of your TextView in item_goal.xml
            goalCheckBox = itemView.findViewById(R.id.goal_checkbox);
        }
    }
}
