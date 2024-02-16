package edu.ucsd.cse110.successorator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.*;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ActivityScenario.launch;

import static junit.framework.TestCase.assertEquals;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import org.junit.Assert;
import org.junit.runners.MethodSorters;

import edu.ucsd.cse110.successorator.data.db.AppDatabase;
import edu.ucsd.cse110.successorator.data.db.GoalDao;
import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    // Utility method to wait for a specific duration
    public static ViewAction waitFor(long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for " + millis + " milliseconds";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    // Utility method to click on a child view within a RecyclerView item
    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a specific child view with given ID.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }

    //Text is strikethrough matcher
    public static Matcher<View> withStrikeThroughText(final String text, final boolean withStrikeThrough) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (item instanceof TextView) {
                    TextView textView = (TextView) item;
                    int paintFlags = textView.getPaintFlags();
                    boolean hasStrikeThrough = (paintFlags & Paint.STRIKE_THRU_TEXT_FLAG) > 0;
                    return textView.getText().toString().equals(text) && (withStrikeThrough == hasStrikeThrough);
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                if (withStrikeThrough) {
                    description.appendText("with text: " + text + " and strike through applied");
                } else {
                    description.appendText("with text: " + text + " and without strike through");
                }
            }
        };
    }

    //"No goals for the day" should be displayed if no goals are shown
    @Test
    public void test1_goalAdditionCancelled_NoGoalsTextStillDisplayed() {
        onView(withId(R.id.no_goals_text)).check(matches(isDisplayed()));
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.edit_text_goal_id)).perform(typeText("New Goal"), ViewActions.closeSoftKeyboard());
        onView(withText("Cancel")).perform(click());
        onView(withId(R.id.no_goals_text)).check(matches(isDisplayed()));
    }

    //Checks whether the "No goals for the day" can be seen after add
    @Test
    public void test2_noGoalsText_VisibilityChangesAfterAddingGoal() {
        onView(withId(R.id.no_goals_text)).check(matches(isDisplayed()));
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.edit_text_goal_id)).perform(typeText("Test Goal"), ViewActions.closeSoftKeyboard());
        onView(withText("Add")).perform(click());
        onView(withId(R.id.no_goals_text)).check(matches(not(isDisplayed())));
    }

    //Checks whether the added goal is displayed in recyclerview
    @Test
    public void test3_addGoal_IsDisplayedInRecyclerView() {

        final int[] testCountAddTest = new int[1];
        onView(withId(R.id.goals_recycler_view)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(RecyclerView.class);
            }

            @Override
            public String getDescription() {
                return "Get RecyclerView item count";
            }

            @Override
            public void perform(UiController uiController, View view) {
                RecyclerView recyclerView = (RecyclerView) view;
                testCountAddTest[0] = recyclerView.getAdapter().getItemCount();
            }
        });
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.edit_text_goal_id)).perform(typeText("Test Goal"), ViewActions.closeSoftKeyboard());
        onView(withText("Add")).perform(click());
        onView(withId(R.id.goals_recycler_view)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                RecyclerView recyclerView = (RecyclerView) view;
                int itemCountAfterAdd = recyclerView.getAdapter().getItemCount();
                assertEquals(itemCountAfterAdd, testCountAddTest[0] + 1);
            }
        });
    }

    //Test whether the app correctly strikes through the text and unstrikes text
    @Test
    public void test4_goalsCorrectlyStrikeThroughFuncationality() {
        final String goalText = "Test Goal Strike";

        //Add a goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.edit_text_goal_id)).perform(typeText(goalText), ViewActions.closeSoftKeyboard());
        onView(withText("Add")).perform(click());

        //Click the checkbox of goal
        onView(withId(R.id.goals_recycler_view)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(goalText)), clickChildViewWithId(R.id.goal_checkbox)));

        //Check that it is crossed out
        onView(withText(goalText)).check(matches(withStrikeThroughText(goalText, true)));

        //Click the checkbox of goal
        onView(withId(R.id.goals_recycler_view)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(goalText)), clickChildViewWithId(R.id.goal_checkbox)));

        //Check that it is not crossed out after unchecking it
        onView(withText(goalText)).check(matches(withStrikeThroughText(goalText, false)));
    }
}