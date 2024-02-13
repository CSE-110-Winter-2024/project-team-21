package edu.ucsd.cse110.successorator;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Calendar;
import static org.junit.Assert.*;

import edu.ucsd.cse110.successorator.GoalFinished;

@RunWith(AndroidJUnit4.class)
public class GoalFinishedTest {

    private SharedPreferences sharedPreferences;
    private GoalFinished goalFinished;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        sharedPreferences = context.getSharedPreferences("test_prefs", Context.MODE_PRIVATE);
        goalFinished = new GoalFinished(context);
    }

    @Test
    public void testMarkGoalCompleted() {
        // Given
        String goalName = "Example Goal";
        boolean expectedValue = true;

        // When
        goalFinished.markGoalCompleted(goalName);

        // Then
        assertTrue(sharedPreferences.getBoolean(goalName, false) == expectedValue);
    }

    @Test
    public void testUpdateGoals() {
        // Given
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);

        // Mock last checked date to be yesterday
        sharedPreferences.edit().putLong("last_checked_date", yesterday.getTimeInMillis()).apply();
        sharedPreferences.edit().putBoolean("goal_1", true).apply(); // Mark a goal as completed yesterday

        // When
        goalFinished.updateGoals();

        // Then
        assertFalse(sharedPreferences.getBoolean("goal_1", false)); // Goal should be removed
        assertEquals(today.getTimeInMillis(), sharedPreferences.getLong("last_checked_date", 0)); // Last checked date should be updated to today
    }
}
