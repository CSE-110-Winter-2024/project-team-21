package edu.ucsd.cse110.successorator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ActivityScenario.launch;

import static junit.framework.TestCase.assertEquals;

import static org.hamcrest.CoreMatchers.not;

import org.junit.Assert;

import edu.ucsd.cse110.successorator.app.MainActivity;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Test
    public void addition_isCorrect() {
        Assert.assertEquals(4, 2 + 2);
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);
    @Test
    public void addGoal_IsDisplayedInRecyclerView() {
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.edit_text_goal_id)).perform(typeText("Test Goal"), ViewActions.closeSoftKeyboard());
        onView(withText("Add")).perform(click());
        onView(withText("Test Goal")).check(matches(isDisplayed()));
    }

    @Test
    public void goalAdditionCancelled_NoGoalsTextStillDisplayed() {
        onView(withId(R.id.no_goals_text)).check(matches(isDisplayed()));
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.edit_text_goal_id)).perform(typeText("New Goal"), ViewActions.closeSoftKeyboard());
        onView(withText("Cancel")).perform(click());
        onView(withId(R.id.no_goals_text)).check(matches(isDisplayed()));
    }

    @Test
    public void noGoalsText_VisibilityChangesAfterAddingGoal() {
        onView(withId(R.id.no_goals_text)).check(matches(isDisplayed()));
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.edit_text_goal_id)).perform(typeText("Test Goal"), ViewActions.closeSoftKeyboard());
        onView(withText("Add")).perform(click());
        onView(withId(R.id.no_goals_text)).check(matches(not(isDisplayed())));
    }
}