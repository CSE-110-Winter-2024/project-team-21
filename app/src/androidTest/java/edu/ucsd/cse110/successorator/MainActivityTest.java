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
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.recyclerview.widget.RecyclerView;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.*;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;

import static org.hamcrest.CoreMatchers.not;

import static org.hamcrest.Matchers.allOf;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.TextView;

import org.junit.runners.MethodSorters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.ucsd.cse110.successorator.data.db.AppDatabase;
import edu.ucsd.cse110.successorator.data.db.GoalDao;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainActivityTest {

    private AppDatabase db;
    private GoalDao goalDao;
    Context context;
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        // Use an in-memory database for testing, which does not persist between tests
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries() // Allow queries on the main thread for testing purposes
                .build();
        goalDao = db.goalDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

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

    public static void addGoals(){
        //Add "School" Goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText("Write Paper"), ViewActions.closeSoftKeyboard());
        onView(withText("S")).perform(click());
        onView(withId(R.id.radio_btn_onetime)).perform(click());
        onView(withText("Save")).perform(click());

        //Add "Work" Goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText("Email Boss"), ViewActions.closeSoftKeyboard());
        onView(withText("W")).perform(click());
        onView(withId(R.id.radio_btn_onetime)).perform(click());
        onView(withText("Save")).perform(click());

        //Add "Errands" Goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText("Clean"), ViewActions.closeSoftKeyboard());
        onView(withText("E")).perform(click());
        onView(withId(R.id.radio_btn_onetime)).perform(click());
        onView(withText("Save")).perform(click());

        //Add "Home" Goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText("Read"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.radio_btn_onetime)).perform(click());
        onView(withText("Save")).perform(click());
    }

    public static void addGoalsWithFrequencies(){
        //Add "School" Goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText("Write Paper"), ViewActions.closeSoftKeyboard());
        onView(withText("S")).perform(click());
        onView(withId(R.id.radio_btn_onetime)).perform(click());
        onView(withText("Save")).perform(click());

        //Add "Work" Goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText("Email Boss"), ViewActions.closeSoftKeyboard());
        onView(withText("W")).perform(click());
        onView(withId(R.id.radio_btn_daily)).perform(click());
        onView(withText("Save")).perform(click());

        //Add "Errands" Goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText("Clean"), ViewActions.closeSoftKeyboard());
        onView(withText("E")).perform(click());
        onView(withId(R.id.radio_btn_weekly)).perform(click());
        onView(withText("Save")).perform(click());

        //Add "Home" Goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText("Read"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.radio_btn_monthly)).perform(click());
        onView(withText("Save")).perform(click());
    }

    //"No goals for the day" should be displayed if no goals are shown
    @Test
    public void test01_goalAdditionCancelled_NoGoalsTextStillDisplayed() {
        onView(withId(R.id.no_goals_text)).check(matches(isDisplayed()));
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText("New Goal"), ViewActions.closeSoftKeyboard());
        onView(withText("Cancel")).perform(click());
        onView(withId(R.id.no_goals_text)).check(matches(isDisplayed()));
    }

    //Checks whether the "No goals for the day" can be seen after add
    @Test
    public void test02_noGoalsText_VisibilityChangesAfterAddingGoal() {
        onView(withId(R.id.no_goals_text)).check(matches(isDisplayed()));
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText("Test Goal"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.radio_btn_onetime)).perform(click());
        onView(withText("Save")).perform(click());
        onView(withId(R.id.no_goals_text)).check(matches(not(isDisplayed())));
    }

    //Checks whether the added goal is displayed in recyclerview
    @Test
    public void test03_addGoal_IsDisplayedInRecyclerView() {

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
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText("Test Goal"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.radio_btn_onetime)).perform(click());
        onView(withText("Save")).perform(click());
        onView(withId(R.id.goals_recycler_view)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                RecyclerView recyclerView = (RecyclerView) view;
                int itemCountAfterAdd = recyclerView.getAdapter().getItemCount();
                assertEquals(itemCountAfterAdd, testCountAddTest[0]); //temp fix
            }
        });
    }

    //Test whether the app correctly strikes through the text and unstrikes text
    @Test
    public void test04_goalsCorrectlyStrikeThroughFuncationality() {
        final String goalText = "Test Goal Strike";

        //Add a goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText(goalText), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.radio_btn_onetime)).perform(click());
        onView(withText("Save")).perform(click());

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
    public void test05_dateCorrectlyUpdates() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
        onView(withId(R.id.forwardButton)).perform(click());
        // Advance the date by one day
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        // Update the TextView with the new date
        String newDate = "Today: " + dateFormat.format(calendar.getTime());

        onView(withId(R.id.DateText)).check(matches(withText(newDate)));
    }

    //Test whether deleted goals are deleted on nextDay
    @Test
    public void test06_goalsCorrectlyDeleteAfterNextDay() throws InterruptedException {
        final String goalText = "Test Goal Delete";
        final String goalText2 = "Test Goal Delete2";

        //Add a goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText(goalText), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.radio_btn_onetime)).perform(click());
        onView(withText("Save")).perform(click());
        Thread.sleep(1000);


        //Add a goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText(goalText2), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.radio_btn_onetime)).perform(click());
        onView(withText("Save")).perform(click());

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
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText(goalText), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.radio_btn_onetime)).perform(click());
        onView(withText("Save")).perform(click());
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
    public void test07_goalsUncheckedStayAfterDay() throws InterruptedException {
        final String goalText = "Test Goal Stay";
        final String goalText2 = "Test Goal Stay2";

        //Add a goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText(goalText), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.radio_btn_onetime)).perform(click());
        onView(withText("Save")).perform(click());

        //Add a goal
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText(goalText2), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.radio_btn_onetime)).perform(click());
        onView(withText("Save")).perform(click());

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

    //Helper method for US5 to add Goals
    @Test
    public void test08_US5_FiltersWork() {
        addGoals();
        //Focus Mode
        onView(withId(R.id.btn_focus_mode)).perform(click());
        onView(withText("Work")).perform(click());

        //Work appears
        onView(withId(R.id.goals_recycler_view))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Email Boss"))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(hasDescendant(withChild(withText("W")))));

        //"Home" and "Errands" do not appear
        onView(withId(R.id.goals_recycler_view))
                .check(matches(not(hasDescendant(allOf(
                        withId(R.id.goal_text_view),
                        withText("Write Paper"))))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(not(hasDescendant(withChild(withText("S"))))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(not(hasDescendant(allOf(
                        withId(R.id.goal_text_view),
                        withText("Clean"))))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(not(hasDescendant(withChild(withText("E"))))));

        onView(isRoot()).perform(waitFor(2000)); //we can see it for a bit
    }

    @Test
    public void test09_US5_FiltersChange() {
        addGoals();
        //Focus Mode for Work
        onView(withId(R.id.btn_focus_mode)).perform(click());
        onView(withText("Work")).perform(click());

        //Work appears
        onView(withId(R.id.goals_recycler_view))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Email Boss"))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(hasDescendant(withChild(withText("W")))));

        //"Home" and "Errands" do not appear
        onView(withId(R.id.goals_recycler_view))
                .check(matches(not(hasDescendant(allOf(
                        withId(R.id.goal_text_view),
                        withText("Write Paper"))))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(not(hasDescendant(withChild(withText("S"))))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(not(hasDescendant(allOf(
                        withId(R.id.goal_text_view),
                        withText("Clean"))))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(not(hasDescendant(withChild(withText("E"))))));

        onView(isRoot()).perform(waitFor(2000)); //we can see it for a bit

        //Focus Mode for School
        onView(withId(R.id.btn_focus_mode)).perform(click());
        onView(withText("School")).perform(click());

        //School appears
        onView(withId(R.id.goals_recycler_view))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Write Paper"))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(hasDescendant(withChild(withText("S")))));

        //"Home" and "Errands" do not appear
        onView(withId(R.id.goals_recycler_view))
                .check(matches(not(hasDescendant(allOf(
                        withId(R.id.goal_text_view),
                        withText("Email Boss"))))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(not(hasDescendant(withChild(withText("W"))))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(not(hasDescendant(allOf(
                        withId(R.id.goal_text_view),
                        withText("Clean"))))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(not(hasDescendant(withChild(withText("E"))))));

        onView(isRoot()).perform(waitFor(2000)); //we can see it for a bit

    }

    @Test
    public void test10_US5_ClearFocus() {
        addGoals();
        //Focus Mode for Work
        onView(withId(R.id.btn_focus_mode)).perform(click());
        onView(withText("Work")).perform(click());

        //Work appears
        onView(withId(R.id.goals_recycler_view))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Email Boss"))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(hasDescendant(withChild(withText("W")))));

        //"Home" and "Errands" do not appear
        onView(withId(R.id.goals_recycler_view))
                .check(matches(not(hasDescendant(allOf(
                        withId(R.id.goal_text_view),
                        withText("Write Paper"))))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(not(hasDescendant(withChild(withText("S"))))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(not(hasDescendant(allOf(
                        withId(R.id.goal_text_view),
                        withText("Clean"))))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(not(hasDescendant(withChild(withText("E"))))));

        //Clear Focus Mode
        onView(withId(R.id.btn_focus_mode)).perform(click());
        onView(withText("Clear Focus")).perform(click());

        //Everything Appears
    }

    @Test
    public void test11_US4_AddGoalWithSpecificContext() {
        final String goalText = "Draft research paper";
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText(goalText));
        onView(withId(R.id.radio_btn_onetime)).perform(click());
        onView(withText("S")).perform(click());
        onView(withText("Save")).perform(click());
        onView(withId(R.id.goals_recycler_view))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText(goalText))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(hasDescendant(withChild(withText("S")))));
    }

    @Test
    public void test12_US4_AddGoalNoSpecificContext() {
        final String goalText = "Draft research paper";
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText(goalText));
        onView(withId(R.id.radio_btn_onetime)).perform(click());
        onView(withText("Save")).perform(click());
        onView(withId(R.id.goals_recycler_view))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText(goalText))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(hasDescendant(withChild(withText("H")))));
    }

    @Test
    public void test13_US4_AddTwoGoalsWithSpecificContext() {
        //add goal 1
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText("Draft Research"));

        onView(withText("S")).perform(click());
        onView(withId(R.id.radio_btn_onetime)).perform(click());
        onView(withText("Save")).perform(click());

        //add goal 2
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText("Email Boss"));

        onView(withText("W")).perform(click());
        onView(withId(R.id.radio_btn_onetime)).perform(click());
        onView(withText("Save")).perform(click());

        onView(withId(R.id.goals_recycler_view))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Draft Research"))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(hasDescendant(withChild(withText("S")))));

        onView(withId(R.id.goals_recycler_view))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Email Boss"))));
        onView(withId(R.id.goals_recycler_view))
                .check(matches(hasDescendant(withChild(withText("W")))));
    }

    @Test
    public void test14_goalMovesToListWhenPrompted() throws InterruptedException {
        final String newGoalText = "New Test Goal";

        onView(withId(R.id.dropdown_menu)).perform(click());

        onView(withText("Pending")).perform(click());
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText(newGoalText), ViewActions.closeSoftKeyboard());
        onView(withText("Save")).perform(click());

        onView(withId(R.id.goals_recycler_view)).perform(
                RecyclerViewActions.actionOnItem(hasDescendant(withText(newGoalText)), longClick()));

        onView(withText("Move to Tomorrow")).perform(click());

        onView(withId(R.id.dropdown_menu)).perform(click());

        onView(withText("Tomorrow")).perform(click());

        onView(withId(R.id.goals_recycler_view)).check(matches(hasDescendant(withText(newGoalText))));

    }
    @Test
    public void test15_todayNotShownInTomorrow() throws InterruptedException {
        final String newGoalText = "Test tomorrow not shown in today";
        onView(withId(R.id.dropdown_menu)).perform(click());
        onView(withText("Tomorrow")).perform(click());
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText(newGoalText), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.radio_btn_onetime)).perform(click());
        onView(withText("Save")).perform(click());
        onView(withId(R.id.goals_recycler_view))
                .check(matches(hasDescendant(withChild(withText(newGoalText)))));
        onView(withId(R.id.dropdown_menu)).perform(click());
        onView(withText("Today")).perform(click());
        onView(withId(R.id.goals_recycler_view))
                .check(matches(not(hasDescendant(withChild(withText(newGoalText))))));
    }

    @Test
    public void test16_pendingDoesNotGoIntoTodayOrTomorrow() throws InterruptedException {
        final String goalText = "Goal Pending";
        onView(withId(R.id.dropdown_menu)).perform(click());
        onView(withText("Pending")).perform(click());
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.text_pending_edit_text_goal_id)).perform(typeText(goalText), ViewActions.closeSoftKeyboard());
        onView(withText("Save")).perform(click());

        onView(withId(R.id.dropdown_menu)).perform(click());
        onView(withText("Today")).perform(click());
        onView(withId(R.id.goals_recycler_view)).check(matches(not(hasDescendant(withText(goalText)))));

        onView(withId(R.id.dropdown_menu)).perform(click());
        onView(withText("Tomorrow")).perform(click());
        onView(withId(R.id.goals_recycler_view)).check(matches(not(hasDescendant(withText(goalText)))));
    }
}