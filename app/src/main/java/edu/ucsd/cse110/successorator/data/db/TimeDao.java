package edu.ucsd.cse110.successorator.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface TimeDao {
    @Insert
    void insert(TimeEntity time);

    @Update
    void update(TimeEntity time);

    @Query("SELECT * FROM time")
    LiveData<TimeEntity> getTime();

}
