package edu.ucsd.cse110.successorator.data.db;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GoalDao {
    @Insert
    void insert(GoalEntity goal);

    @Delete
    void delete(GoalEntity goal);

    @Update
    void update(GoalEntity goal);

    @Query("SELECT * FROM goals")
    LiveData<List<GoalEntity>> getAllGoals();

    @Query("SELECT * FROM goals WHERE goalText = :goalText LIMIT 1")
    GoalEntity findByGoalText(String goalText);

    @Query("SELECT * FROM goals LIMIT 1")
    GoalEntity isItEmpty();

    @Query("DELETE FROM goals WHERE isChecked = 1 AND frequencyType = 'One-time' ")
    void removeCompletedFromDao();

    @Query("UPDATE goals SET isChecked = 0 WHERE isChecked = 1 AND frequencyType != 'One-time'")
    void uncheckRecurringGoals();

    @Query("SELECT * FROM goals")
    LiveData<GoalEntity> getGoals();

    @Query("SELECT COUNT(*) FROM goals")
    int size();

    @Query("SELECT * FROM goals WHERE listCategory = :listCategory")
    LiveData<List<GoalEntity>> getGoalsByListCategory(String listCategory);

    @Query("UPDATE goals SET listCategory = 'Today' WHERE listCategory = 'Tomorrow'")
    void rolloverTomorrowToToday();

    @Query("UPDATE goals SET listCategory = :newCategory WHERE id = :goalId")
    void updateCategoryById(int goalId, String newCategory);

    @Query("DELETE FROM goals")
    void deleteAllGoals();
}
