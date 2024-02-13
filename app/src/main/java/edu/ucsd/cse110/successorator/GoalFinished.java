package edu.ucsd.cse110.successorator;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Calendar;

public class GoalFinished {

    private static final String PREF_NAME = "GoalsPref";
    private SharedPreferences sharedPreferences;

    public GoalFinished(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void markGoalCompleted(String goalName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(goalName, true);
        editor.apply();
    }

    public void updateGoals() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar lastCheckedDate = Calendar.getInstance();
        lastCheckedDate.setTimeInMillis(sharedPreferences.getLong("last_checked_date", 0));

        if (lastCheckedDate.before(today)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            for (String key : sharedPreferences.getAll().keySet()) {
                boolean isGoalKey = !key.equals("last_checked_date");
                boolean isCompleted = sharedPreferences.getBoolean(key, false);
                if (isGoalKey && isCompleted) {
                    editor.remove(key);
                }
            }
            editor.putLong("last_checked_date", today.getTimeInMillis());
            editor.apply();
        }
    }
}
