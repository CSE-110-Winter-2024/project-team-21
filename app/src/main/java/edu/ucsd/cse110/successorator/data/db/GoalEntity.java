package edu.ucsd.cse110.successorator.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

import edu.ucsd.cse110.successorator.*;

@Entity(tableName = "goals")
public class GoalEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id = null;

    @ColumnInfo(name = "goalText")
    public String goalText;

    @ColumnInfo(name = "isChecked")
    public boolean isChecked;

    @ColumnInfo(name = "Sort Number")
    public Integer sortNumber;

    public GoalEntity(String goalText, boolean isChecked) {
        this.goalText = goalText;
        this.isChecked = isChecked;
        this.sortNumber = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoalEntity goal = (GoalEntity) o;
        return Objects.equals(goalText, goal.goalText) && Objects.equals(isChecked, goal.isChecked);
    }
}
