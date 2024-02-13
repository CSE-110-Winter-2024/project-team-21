package edu.ucsd.cse110.successorator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.Calendar;

public class GoalFinished {
    private Context context;

    public GoalFinished(Context context) {
        this.context = context;
    }

    public void markGoalCompleted(String goalName) {
        // Save the completion status of the goal
        saveGoalCompletionStatus(goalName);

        // Schedule deletion of the goal if it's marked completed today
        scheduleGoalDeletionIfCompletedToday(goalName);
    }

    private void saveGoalCompletionStatus(String goalName) {
        // Use SharedPreferences to store the completion status of the goal
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(goalName, true); // Assuming boolean key-value pair for simplicity
        editor.apply();
    }

    private void scheduleGoalDeletionIfCompletedToday(String goalName) {
        // Get the current date
        Calendar today = Calendar.getInstance();

        // Get the completion date of the goal
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long completionTime = sharedPreferences.getLong(goalName + "_completion_time", 0);

        // If the goal was completed today, schedule its deletion for the next day
        if (completionTime != 0 && isSameDay(today.getTimeInMillis(), completionTime)) {
            scheduleGoalDeletion(goalName, today);
        }
    }

    private void scheduleGoalDeletion(String goalName, Calendar today) {
        // Set the deletion time to the next day
        today.add(Calendar.DAY_OF_MONTH, 1);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        // Schedule deletion using AlarmManager or any other scheduling mechanism
        // Here, we'll just save the deletion time in SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(goalName + "_deletion_time", today.getTimeInMillis());
        editor.apply();
    }

    public boolean shouldDeleteGoal(String goalName) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long deletionTime = sharedPreferences.getLong(goalName + "_deletion_time", 0);
        if (deletionTime != 0) {
            Calendar now = Calendar.getInstance();
            return now.getTimeInMillis() >= deletionTime;
        }
        return false;
    }

    public void deleteGoal(String goalName) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(goalName); // Remove the completion status
        editor.remove(goalName + "_deletion_time"); // Remove the deletion time
        editor.apply();
    }

    private boolean isSameDay(long timestamp1, long timestamp2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(timestamp1);
        cal2.setTimeInMillis(timestamp2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}

