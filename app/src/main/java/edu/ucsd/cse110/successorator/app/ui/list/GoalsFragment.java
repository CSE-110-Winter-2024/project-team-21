package edu.ucsd.cse110.successorator.app.ui.study;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.databinding.FragmentGoalsBinding;

public class GoalsFragment extends Fragment {
    private MainViewModel activityModel; // NEW FIELD
    private FragmentGoalsBinding view;

    public GoalsFragment() {
        // Required empty public constructor
    }

    public static edu.ucsd.cse110.successorator.app.ui.study.GoalsFragment newInstance() {
        edu.ucsd.cse110.successorator.app.ui.study.GoalsFragment fragment = new edu.ucsd.cse110.successorator.app.ui.study.GoalsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the Model
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize the View
        view = FragmentGoalsBinding.inflate(inflater, container, false);

        //setupMvp();

        return view.getRoot();
    }

    /*private void setupMvp() {
        // Observe Model -> call View
        activityModel.getDisplayedText().observe(text -> view.cardText.setText(text));

        // Observe View -> call Model
        view.card.setOnClickListener(v -> activityModel.flipTopCard());
        view.nextButton.setOnClickListener(v -> activityModel.stepForward());
        view.prevButton.setOnClickListener(v -> activityModel.stepBackward());
        view.shuffleButton.setOnClickListener(v -> activityModel.shuffle());
    }*/
}