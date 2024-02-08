package edu.ucsd.cse110.successorator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private List<String> goalsList;

    // Constructor to initialize the adapter with a list of goals
    public GoalsAdapter(List<String> goalsList) {
        this.goalsList = goalsList;
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

        // Constructor to initialize the ViewHolder with the provided view
        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views using findViewById
            goalTextView = itemView.findViewById(R.id.goal_text_view); // Replace R.id.goal_text_view with the actual ID of your TextView in item_goal.xml
        }
    }
}
