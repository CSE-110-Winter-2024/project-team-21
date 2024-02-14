package edu.ucsd.cse110.successorator.data.db;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GoalDao {
    @Insert
    void insert(GoalEntity goal);

    @Query("SELECT * FROM goals")
    LiveData<List<GoalEntity>> getAllGoals();
}
