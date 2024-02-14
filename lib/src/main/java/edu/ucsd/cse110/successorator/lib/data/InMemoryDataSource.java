package edu.ucsd.cse110.successorator.lib.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.util.Subject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;

/**
 * Class used as a sort of "database" of goals that exist. This
 * will be replaced with a real database in the future, but can also be used
 * for testing.
 */
public class InMemoryDataSource {
    private final Map<Integer, Goal> goals
            = new HashMap<>();
    private final Map<Integer, SimpleSubject<Goal>> goalSubjects
            = new HashMap<>();
    private final SimpleSubject<List<Goal>> allGoalsSubject
            = new SimpleSubject<>();

    private static final Map<String, Boolean> checkedStates
            = new HashMap<>();

    public InMemoryDataSource() {
    }

    public List<Goal> getGoals() {
        return List.copyOf(goals.values());
    }

    public Goal getGoal(int id) {
        return goals.get(id);
    }

    public Subject<Goal> getGoalSubject(int id) {
        if (!goalSubjects.containsKey(id)) {
            var subject = new SimpleSubject<Goal>();
            subject.setValue(getGoal(id));
            goalSubjects.put(id, subject);
        }
        return goalSubjects.get(id);
    }

    public SimpleSubject<List<Goal>> getAllGoalsSubject() {
        return allGoalsSubject;
    }

    public void putGoal(Goal goal) {
        goals.put(goal.getId(), goal);
        if (goalSubjects.containsKey(goal.getId())) {
            goalSubjects.get(goal.getId()).setValue(goal);
        }
        allGoalsSubject.setValue(getGoals());
    }

/*    public final static List<Goal> DEFAULT_GOALS = List.of(
            new Goal(0, "ONE"),
            new Goal(1, "TWO"),
            new Goal(2, "THREE"),
            new Goal(3, "FOUR"),
            new Goal(4, "FIVE"),
            new Goal(5, "SIX")
    );*/

    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();
        /*for (Goal goal : DEFAULT_GOALS) {
            data.putGoal(goal);
        }
        // Initialize all goals as unchecked
        for (Goal goal : DEFAULT_GOALS) {
            checkedStates.put(goal.getGoalText(), false);
        }*/
        return data;
    }
}