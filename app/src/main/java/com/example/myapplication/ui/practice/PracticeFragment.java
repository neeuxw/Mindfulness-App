package com.example.myapplication.ui.practice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;

public class PracticeFragment extends Fragment {

    private Button btnBreathing, btnMeditation, btnBodyScan;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_practice, container, false);

        initViews(view);

        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        btnBreathing = view.findViewById(R.id.btn_breathing);
        btnMeditation = view.findViewById(R.id.btn_meditation);
        btnBodyScan = view.findViewById(R.id.btn_body_scan);
    }

    private void setupClickListeners() {
        // Breathing Exercise button
        btnBreathing.setOnClickListener(v -> {
            String title = "Breathing Exercise";
            String description = "Focus on your breath and feel its flow. Be aware when inhaling and aware when exhaling.";
            int backgroundRes = R.drawable.practice_breathing;

            Bundle args = new Bundle();
            args.putString("practiceTitle", title);
            args.putString("practiceDescription", description);
            args.putInt("backgroundResId", backgroundRes);

            Navigation.findNavController(v).navigate(R.id.action_to_breathingFragment, args);
        });

        // Mindfulness Meditation button
        btnMeditation.setOnClickListener(v -> navigateToDetail(
                "Mindfulness Meditation",
                "Observe your thoughts without judgment, maintaining awareness and an attitude of acceptance.",
                R.drawable.practice_meditation
        ));

        // Body Scan button
        btnBodyScan.setOnClickListener(v -> navigateToDetail(
                "Body Scan",
                "Gradually bring awareness to each part of your body from head to toe, releasing tension and stress.",
                R.drawable.practice_body
        ));
    }


    private void navigateToDetail(String title, String description, int backgroundRes) {

        // Create Bundle to pass data to PracticeDetailFragment
        Bundle args = new Bundle();
        args.putString("practiceTitle", title);
        args.putString("practiceDescription", description);
        args.putInt("backgroundResId", backgroundRes);

        Navigation.findNavController(requireView())
                .navigate(R.id.action_to_practiceDetail, args);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}