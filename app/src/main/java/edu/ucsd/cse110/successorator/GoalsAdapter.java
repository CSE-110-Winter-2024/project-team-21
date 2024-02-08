package edu.ucsd.cse110.successorator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private List<String> goalsList;

    public GoalsAdapter(List<String> goalsList) {
        this.goalsList = goalsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String goal = goalsList.get(position);
        // Bind the goal text to the TextView, etc.
    }

    @Override
    public int getItemCount() {
        return goalsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Define your ViewHolder with member variables for any views that will be set as you render a row.

        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views using itemView.findViewById, etc.
        }
    }
}
