package edu.ucsd.cse110.successorator.app.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.databinding.FragmentGoalListBinding;

public class GoalsFragment extends Fragment {
    private TextView noGoalsTextView;
    private RecyclerView recyclerView;
    private FragmentGoalListBinding view;
    private MainViewModel activityModel;
    private GoalsAdapter adapter;
    public GoalsFragment() {

    }

    public static GoalsFragment newInstance() {
        GoalsFragment fragment = new GoalsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        this.adapter = new GoalsAdapter(List.of());

        activityModel.getOrderedGoals().observe(newGoals -> {
            if(newGoals == null) return;
            adapter.notifyDataSetChanged();;
        });
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        this.view = FragmentGoalListBinding.inflate(inflater, container, false);

        // Find views using View Binding
        recyclerView = view.goalsRecyclerView;
        noGoalsTextView = view.noGoalsText;

        // Set the layout manager and adapter on the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return view.getRoot();
    }
}