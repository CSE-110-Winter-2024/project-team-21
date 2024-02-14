package edu.ucsd.cse110.successorator.lib.domain;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Just a dummy domain model that does nothing in particular. Delete me.
 */
public class Goal {
    private final @Nullable Integer id;
    private final @Nullable String goalText;

    public Goal(
        @Nullable Integer id,
        @Nullable String goalText
    ) {
        this.id = id;
        this.goalText = goalText;
    }

    @Nullable
    public Integer getId() {
        return id;
    }

    @Nullable
    public String getGoalText() {
        return goalText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return Objects.equals(id, goal.id) && Objects.equals(goalText, goal.goalText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, goalText);
    }
}
