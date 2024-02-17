package edu.ucsd.cse110.successorator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoalsViewModel extends ViewModel {
    private MutableLiveData<List<String>> goalsList;
    private List<String> checkedOffGoals; // Map to track checked off goals

    public GoalsViewModel() {
        goalsList = new MutableLiveData<>();
        goalsList.setValue(new ArrayList<>()); // Initialize with an empty list

        checkedOffGoals = new ArrayList<>();
        // Initialize the map to track checked off goals
    }

    public LiveData<List<String>> getGoalsList() {
        return goalsList;
    }

    public void addGoal(String goal) {
        List<String> currentList = goalsList.getValue();
        currentList.add(goal);
        goalsList.setValue(currentList);
    }

    public void markGoalAsCheckedOff(String goal) {
        if (!checkedOffGoals.contains(goal)) {
            checkedOffGoals.add(goal);
        }
    }

    public void markGoalAsNotCheckedOff(String goal) {
        checkedOffGoals.remove(goal);    }

//    public void removeCheckedOffGoals() {
//        List<String> currentList = goalsList.getValue();
//        List<String> updatedList = new ArrayList<>();
//        for (String goal : currentList) {
//            // Add the goal to the updated list if it's not checked off
//            if (!isCheckedOff(goal)) {
//                updatedList.add(goal);
//            }
//        }
//        goalsList.setValue(updatedList);
//    }


    public boolean isGoalCheckedOff(String goal) {
        return checkedOffGoals.contains(goal);
    }

    private boolean isCheckedOff(String goal) {
        // Check if the goal exists in the map of checked off goals
        return checkedOffGoals.contains(goal);
    }
}
