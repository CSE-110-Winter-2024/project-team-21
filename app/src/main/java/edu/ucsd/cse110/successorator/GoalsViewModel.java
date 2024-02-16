package edu.ucsd.cse110.successorator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoalsViewModel extends ViewModel {
    private MutableLiveData<Map<String, Boolean>> goalsMap; // Map to store goals with their completion status

    public GoalsViewModel() {
        goalsMap = new MutableLiveData<>();
        goalsMap.setValue(new HashMap<>()); // Initialize with an empty map
    }

    // Method to get the LiveData object containing the map of goals
    public LiveData<Map<String, Boolean>> getGoalsMap() {
        return goalsMap;
    }

    // Method to add a new goal with completion status to the map
    public void addGoal(String goal) {
        Map<String, Boolean> currentMap = goalsMap.getValue();
        if (currentMap == null) {
            currentMap = new HashMap<>();
        }
        // Add the goal with initial completion status (false)
        currentMap.put(goal, false);
        goalsMap.setValue(currentMap);
    }

    // Method to mark a goal as checked off
    public void markGoalAsCheckedOff(String goal) {
        Map<String, Boolean> currentMap = goalsMap.getValue();
        if (currentMap != null && currentMap.containsKey(goal)) {
            currentMap.put(goal, true); // Update completion status to true
            goalsMap.setValue(currentMap);
        }
    }

    // Method to clear checked off goals
    public void clearCheckedGoals() {
        Map<String, Boolean> currentMap = goalsMap.getValue();
        if (currentMap != null) {
            // Create a new map to store unchecked goals
            Map<String, Boolean> uncheckedGoals = new HashMap<>();
            for (Map.Entry<String, Boolean> entry : currentMap.entrySet()) {
                // Copy unchecked goals to the new map
                if (!entry.getValue()) {
                    uncheckedGoals.put(entry.getKey(), false);
                }
            }
            // Update the LiveData with the map of unchecked goals
            goalsMap.setValue(uncheckedGoals);
        }
    }
}
