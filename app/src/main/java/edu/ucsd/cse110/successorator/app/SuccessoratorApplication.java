package edu.ucsd.cse110.successorator.app;

import android.app.Application;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
public class SuccessoratorApplication extends Application {
    private InMemoryDataSource dataSource;
    private GoalRepository goalRepository;

    @Override
    public void onCreate(){
        super.onCreate();

        this.dataSource = InMemoryDataSource.fromDefault();
        this.goalRepository = new GoalRepository(dataSource);
    }

    public GoalRepository getGoalRepository() {
        return goalRepository;
    }
}
