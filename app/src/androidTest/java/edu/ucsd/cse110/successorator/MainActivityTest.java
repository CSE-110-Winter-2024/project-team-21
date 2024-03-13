package edu.ucsd.cse110.successorator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.recyclerview.widget.RecyclerView;
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
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ActivityScenario.launch;

import static junit.framework.TestCase.assertEquals;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import android.graphics.Paint;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;

import org.junit.runners.MethodSorters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
    //Test whether the app updates the day correctly
    @Test
    public void test5_dateCorrectlyUpdates() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        onView(withId(R.id.forwardButton)).perform(click());
        // Advance the date by one day
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        // Update the TextView with the new date
        String newDate = dateFormat.format(calendar.getTime());

        onView(withId(R.id.DateText)).check(matches(withText(newDate)));
    }

    //Test whether deleted goals are deleted on nextDay
    @Test
    public void test6_goalsCorrectlyDeleteAfterNextDay() throws InterruptedException {
        final String goalText = "Test Goal Delete";
        final String goalText2 = "Test Goal Delete2";

        //Add a goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.edit_text_goal_id)).perform(typeText(goalText), ViewActions.closeSoftKeyboard());
        onView(withText("Add")).perform(click());
        Thread.sleep(1000);


        //Add a goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.edit_text_goal_id)).perform(typeText(goalText2), ViewActions.closeSoftKeyboard());
        onView(withText("Add")).perform(click());

        Thread.sleep(1000);
        //Click the checkbox of goal
        onView(withId(R.id.goals_recycler_view)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(goalText)), clickChildViewWithId(R.id.goal_checkbox)));
        onView(withId(R.id.forwardButton)).perform(click());
        Thread.sleep(1000);


        //Check if goalText is deleted
        onView(withText(goalText)).check(doesNotExist());
        onView(withText(goalText2)).check(matches(withText(goalText2)));
        //Add a goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.edit_text_goal_id)).perform(typeText(goalText), ViewActions.closeSoftKeyboard());
        onView(withText("Add")).perform(click());
        Thread.sleep(1000);


        //Click the checkbox of goal
        onView(withId(R.id.goals_recycler_view)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(goalText)), clickChildViewWithId(R.id.goal_checkbox)));

        //Click the checkbox of goal
        onView(withId(R.id.goals_recycler_view)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(goalText2)), clickChildViewWithId(R.id.goal_checkbox)));
        Thread.sleep(1000);

        onView(withId(R.id.forwardButton)).perform(click());
        Thread.sleep(1000);


        onView(withText(goalText)).check(doesNotExist());
        onView(withText(goalText2)).check(doesNotExist());
    }

    //Check if goals not checked stay in RecyclerView
    @Test
    public void test7_goalsUncheckedStayAfterDay() throws InterruptedException {
        final String goalText = "Test Goal Stay";
        final String goalText2 = "Test Goal Stay2";

        //Add a goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.edit_text_goal_id)).perform(typeText(goalText), ViewActions.closeSoftKeyboard());
        onView(withText("Add")).perform(click());

        //Add a goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.edit_text_goal_id)).perform(typeText(goalText2), ViewActions.closeSoftKeyboard());
        onView(withText("Add")).perform(click());

        Thread.sleep(1000);
        //Click the checkbox of goal
        onView(withId(R.id.forwardButton)).perform(click());

        //Check if both goalTexts stay
        onView(withText(goalText)).check(matches(withText(goalText)));
        onView(withText(goalText2)).check(matches(withText(goalText2)));


        Thread.sleep(1000);

        onView(withId(R.id.forwardButton)).perform(click());
        onView(withId(R.id.forwardButton)).perform(click());
        onView(withId(R.id.forwardButton)).perform(click());
        onView(withId(R.id.forwardButton)).perform(click());


        //Check if both goalTexts stay
        onView(withText(goalText)).check(matches(withText(goalText)));
        onView(withText(goalText2)).check(matches(withText(goalText2)));
    }

    public void test8_goalMovesToListWhenPrompted() throws InterruptedException {
        final String newGoalText = "New Test Goal";

        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.edit_text_goal_id)).perform(typeText(newGoalText), ViewActions.closeSoftKeyboard());
        onView(withText("Add")).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.goals_recycler_view)).perform(
                RecyclerViewActions.actionOnItem(hasDescendant(withText(newGoalText)), longClick()));

        onView(withText("Tomorrow")).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.dropdown_menu)).perform(click());

        onView(withText("Tomorrow")).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.goals_recycler_view)).check(matches(hasDescendant(withText(newGoalText))));

    }
    @Test
    public void testOptionTomorrowAvailableWhenLongClickGoal() throws InterruptedException {
        final String goalText = "Goal for Tomorrow Option Test";

        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.edit_text_goal_id)).perform(typeText(goalText), ViewActions.closeSoftKeyboard());
        onView(withText("Add")).perform(click());
        Thread.sleep(1000); // Let the UI update

        onView(withId(R.id.goals_recycler_view))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(goalText)), longClick()));

        onView(withText("Tomorrow")).check(matches(isDisplayed()));
    }
    @Test
    public void testGoalNotInTodayAfterMovedToTomorrow() throws InterruptedException {
        final String goalText = "Goal to Move to Tomorrow";

        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.edit_text_goal_id)).perform(typeText(goalText), ViewActions.closeSoftKeyboard());
        onView(withText("Add")).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.goals_recycler_view))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(goalText)), longClick()));
        onView(withText("Tomorrow")).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.dropdown_menu)).perform(click());
        onView(withText("Today")).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.goals_recycler_view)).check(matches(not(hasDescendant(withText(goalText)))));
    }
}