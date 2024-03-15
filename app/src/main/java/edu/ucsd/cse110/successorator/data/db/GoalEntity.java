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
    private Integer id = null;

    @ColumnInfo(name = "goalText")
    private String goalText;

    @ColumnInfo(name = "isChecked")
    private boolean isChecked;

    @ColumnInfo(name = "frequencyType")
    private String frequencyType;

    @ColumnInfo(name = "freqMonth")
    private Integer freqMonth;

    @ColumnInfo(name = "freqDayString")
    private String freqDayString;

    @ColumnInfo(name = "freqOccur")
    private Integer freqOccur;

    @ColumnInfo(name = "freqTimeInMilli")
    private long freqTimeInMilli;

    // New field for context
    @ColumnInfo(name = "context")
    private String context;

    @ColumnInfo(name = "listCategory")
    private String listCategory;

    public void setId(Integer id) { this.id = id; }

    public void setGoalText(String goalText) {
        this.goalText = goalText;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setFreqMonth(Integer freqMonth) {
        this.freqMonth = freqMonth;
    }

    public void setFrequencyType(String frequencyType) {
        this.frequencyType = frequencyType;
    }

    public void setFreqDayString(String freqDayString) {
        this.freqDayString = freqDayString;
    }
    public void setFreqOccur(Integer freqOccur) { this.freqOccur = freqOccur; }
    public void setFreqTimeInMilli(long freqTimeInMilli) { this.freqTimeInMilli = freqTimeInMilli; }

    public void setListCategory(String listCategory) { this.listCategory = listCategory; }

    public void setContext(String context) {
        this.context = context;
    }

    public Integer getId() {
        return id;
    }

    public String getGoalText() {
        return goalText;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public String getFreqDayString() { return freqDayString; }

    public Integer getFreqMonth() {
        return freqMonth;
    }

    public String getFrequencyType() {
        return frequencyType;
    }
    public Integer getFreqOccur() {
        return freqOccur;
    }
    public long getFreqTimeInMilli() {
        return freqTimeInMilli;
    }

    public String getContext() {
        return context;
    }

    public String getListCategory() {
        return listCategory;
    }

    public GoalEntity(String goalText, boolean isChecked, String context, String frequencyType, String listCategory, String freqDayString, long freqTimeInMilli, Integer freqOccur, Integer freqMonth) {
        this.goalText = goalText;
        this.isChecked = isChecked;
        this.context = context;
        this.frequencyType = frequencyType;
        this.listCategory = listCategory;
        this.freqDayString = freqDayString;
        this.freqTimeInMilli = freqTimeInMilli;
        this.freqOccur = freqOccur;
        this.freqMonth = freqMonth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoalEntity goal = (GoalEntity) o;
        return Objects.equals(goalText, goal.goalText) && Objects.equals(isChecked, goal.isChecked) ;
    }
    @Override
    public int hashCode() {
        return Objects.hash(goalText, isChecked);
    }
}
