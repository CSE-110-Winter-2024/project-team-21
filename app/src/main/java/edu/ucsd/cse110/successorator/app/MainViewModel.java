package edu.ucsd.cse110.successorator.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class MainViewModel extends ViewModel{
    // Domain state (true "Model" state)
    private final GoalRepository goalRepository;

    // UI state
    private final SimpleSubject<List<Integer>> goalOrdering;
    private final SimpleSubject<List<Goal>> orderedGoals;
    private final SimpleSubject<Goal> goal;
    private final SimpleSubject<Boolean> isCompleted;
    private final SimpleSubject<String> goalText;

    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (SuccessoratorApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getGoalRepository());
                    });

    public MainViewModel(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;

        // Create the observable subjects.
        this.goalOrdering = new SimpleSubject<>();
        this.orderedGoals = new SimpleSubject<>();
        this.goal = new SimpleSubject<>();
        this.isCompleted = new SimpleSubject<>();
        this.goalText = new SimpleSubject<>();

        // Initialize...
        isCompleted.setValue(false);

        // When the list of cards changes (or is first loaded), reset the ordering.
        goalRepository.findAll().observe(cards -> {
            if (cards == null) return; // not ready yet, ignore

            var ordering = new ArrayList<Integer>();
            for (int i = 0; i < cards.size(); i++) {
                ordering.add(i);
            }
            goalOrdering.setValue(ordering);
        });

        // When the ordering changes, update the top card.
        goalOrdering.observe(ordering -> {
            if (ordering == null) return;

            var card = goalRepository.find(ordering.get(0)).getValue();
            if (card == null) return;
            this.goal.setValue(card);
        });

        goalOrdering.observe(ordering -> {
            if (ordering == null) return;

            var cards = new ArrayList<Goal>();
            for (var id : ordering) {
                var card = goalRepository.find(id).getValue();
                if (card == null) return;
                cards.add(card);
            }
            this.orderedGoals.setValue(cards);
        });
    }

    public Subject<String> getGoalText() {
        return goalText;


    }

    /*public void flipTopCard() {
        var isShowingFront = this.isCompleted.getValue();
        if (isShowingFront == null) return;
        this.isCompleted.setValue(!isShowingFront);
    }

    public void stepForward() {
        var ordering = this.goalOrdering.getValue();
        if (ordering == null) return;

        var newOrdering = new ArrayList<>(ordering);
        Collections.rotate(newOrdering, -1);
        this.goalOrdering.setValue(newOrdering);
    }

    public void stepBackward() {
        var ordering = this.goalOrdering.getValue();
        if (ordering == null) return;

        var newOrdering = new ArrayList<>(ordering);
        Collections.rotate(newOrdering, 1);
        this.goalOrdering.setValue(newOrdering);
    }

    public void shuffle() {
        var ordering = this.goalOrdering.getValue();
        if (ordering == null) return;

        var newOrdering = new ArrayList<>(ordering);
        Collections.shuffle(newOrdering);
        this.goalOrdering.setValue(newOrdering);
    }*/

    public Subject<List<Goal>> getOrderedGoals() {
        return orderedGoals;
    }
}
