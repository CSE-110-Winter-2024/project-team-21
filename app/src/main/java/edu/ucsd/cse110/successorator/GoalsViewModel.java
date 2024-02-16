package edu.ucsd.cse110.successorator;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class GoalsViewModel extends ViewModel {
    private MutableLiveData<List<String>> goalsList;
    private List<String> checkedOffGoals; // Assuming you're storing checked off goals

    public GoalsViewModel() {
        goalsList = new MutableLiveData<>();
        goalsList.setValue(new ArrayList<>()); // Initialize with an empty list
        checkedOffGoals = new ArrayList<>(); // Initialize the list of checked off goals
    }

    public LiveData<List<String>> getGoalsList() {
        return goalsList;
    }

    public void addGoal(String goal) {
        List<String> currentList = goalsList.getValue();
        currentList.add(goal);
        goalsList.setValue(currentList);
    }

    public void clearCheckedMITs() {
        List<String> currentList = goalsList.getValue();
        List<String> updatedList = new ArrayList<>();

        for (String goal : currentList) {
            // Add the goal to the updated list if it's not checked off
            if (!isGoalCheckedOff(goal)) {
                updatedList.add(goal);
            }
        }

        goalsList.setValue(updatedList);
    }

    private boolean isGoalCheckedOff(String goal) {
        // Check if the goal exists in the list of checked off goals
        return checkedOffGoals.contains(goal);
    }

    // This method allows other classes to mark a goal as checked off
    public void markGoalAsCheckedOff(String goal) {
        checkedOffGoals.add(goal);
    }

    // This method allows other classes to mark a goal as not checked off
    public void markGoalAsNotCheckedOff(String goal) {
        checkedOffGoals.remove(goal);
    }
}

