package edu.ucsd.cse110.successorator.lib.domain;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class GoalRepository {
    private final InMemoryDataSource dataSource;

    public GoalRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Integer count() {
        return dataSource.getGoals().size();
    }

    public Subject<Goal> find(int id) {
        return dataSource.getGoalSubject(id);
    }

    public Subject<List<Goal>> findAll() {
        return dataSource.getAllGoalsSubject();
    }

    public void save(Goal goal) {
        dataSource.putGoal(goal);
    }
}
