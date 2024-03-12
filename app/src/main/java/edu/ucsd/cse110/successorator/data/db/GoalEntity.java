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

    @ColumnInfo(name = "freqDay")
    private Integer freqDay;

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
    public void setFreqDay(Integer freqDay) { this.freqDay = freqDay; }


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
    public Integer getFreqDay() {
        return freqDay;
    }

    public GoalEntity(String goalText, boolean isChecked, String frequencyType, Integer freqMonth, Integer freqDay, Integer freqOccur, String freqDayString) {
        this.goalText = goalText;
        this.isChecked = isChecked;
        this.frequencyType = frequencyType;
        this.freqMonth = freqMonth;
        this.freqDayString = freqDayString;
        this.freqOccur = freqOccur;
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