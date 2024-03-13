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
    public Integer getId() {
        return id;
    }

    public String getGoalText() {
        return goalText;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public String getListCategory() {return listCategory; }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Integer id = null;

    @ColumnInfo(name = "goalText")
    private String goalText;

    @ColumnInfo(name = "isChecked")
    private boolean isChecked;

    @ColumnInfo(name = "listCategory")
    private String listCategory;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setGoalText(String goalText) {
        this.goalText = goalText;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setListCategory(String listCategory) { this.listCategory = listCategory; }

    public GoalEntity(String goalText, boolean isChecked, String listCategory) {
        this.goalText = goalText;
        this.isChecked = isChecked;
        this.listCategory = listCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoalEntity goal = (GoalEntity) o;
        return Objects.equals(goalText, goal.goalText) && Objects.equals(isChecked, goal.isChecked) && Objects.equals(listCategory, goal.listCategory);
    }
    @Override
    public int hashCode() {
        return Objects.hash(goalText, isChecked);
    }
}