package edu.ucsd.cse110.successorator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.*;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.core.app.ActivityScenario.launch;

import static junit.framework.TestCase.assertEquals;

import static org.hamcrest.CoreMatchers.not;

import android.content.Context;
import android.view.View;
import android.widget.Checkable;

import static org.junit.Assert.*;

import edu.ucsd.cse110.successorator.data.db.AppDatabase;

import edu.ucsd.cse110.successorator.data.db.GoalDao;
import edu.ucsd.cse110.successorator.data.db.GoalEntity;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DatabaseTest {
    private AppDatabase db;
    private GoalDao goalDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries() // Only for testing
                .build();
        goalDao = db.goalDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void testAllGoalDaoFunctionality() throws InterruptedException {
        final String goalText = "Test Goal Checkbox";
        //goalDao should be initially empty/null because nothing is in it.
        assertNull("Dao should be empty.", goalDao.isItEmpty());

        // Insert a test goal into the database
        GoalEntity testGoal = new GoalEntity("Test Goal Checkbox", false, "NONE", 0,0, 0, "HEL");
        goalDao.insert(testGoal);

        //NEED to assign an entity in the Dao to a variable because of PrimaryKey
        GoalEntity updatedGoal = goalDao.findByGoalText(goalText);
        assertFalse("goalIsChecked should be false in database to begin with.", updatedGoal.isChecked());

        // Essentially, simulating the button is "checkmarked" so it should be true for isChecked
        updatedGoal.setChecked(true);

        goalDao.update(updatedGoal);

        // Dao updates properly so isChecked is true.
        assertTrue("Simulated checkmark should return true. ", updatedGoal.isChecked());

        //assertTrue("Same goal should be true.", goalDao.findByGoalText(goalText).equals(testGoal));
        //goalDao should be NonNull because there is one item.
        assertNotNull(goalDao.isItEmpty());

        //For "next day" behavior implementation, check if delete from database works correctly
        goalDao.delete(updatedGoal);
        assertNull("Dao should be empty from this deletion.", goalDao.isItEmpty());
    }
}