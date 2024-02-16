package edu.ucsd.cse110.successorator.data.db;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GoalDao {
    @Insert
    void insert(GoalEntity goal);




    @Update
    void update(GoalEntity goal);

    @Query("SELECT * FROM goals")
    LiveData<List<GoalEntity>> getAllGoals();

    @Query("SELECT * FROM goals WHERE goalText = :goalText LIMIT 1")
    GoalEntity findByGoalText(String goalText);
}
