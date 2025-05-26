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
        btnBreathing.setOnClickListener(v -> navigateToDetail(
                "Breathing Exercise",
                "Focus on your breath and feel its flow. Be aware when inhaling and aware when exhaling.",
                R.drawable.practice_breathing
        ));

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

        String guideContent = "";
        switch (title) {
            case "Breathing Exercise":
                guideContent = "1. Find a quiet and comfortable place to sit\n" +
                        "2. Keep your back straight and shoulders relaxed\n" +
                        "3. Close your eyes and focus on your breath\n" +
                        "4. Inhale for 4 seconds, hold for 2 seconds, exhale for 6 seconds\n" +
                        "5. Repeat the process and feel the flow of your breath";
                break;
            case "Mindfulness Meditation":
                guideContent = "1. Sit in a comfortable posture\n" +
                        "2. Focus on the present moment without judgment\n" +
                        "3. When your mind wanders, gently bring it back to your breath\n" +
                        "4. Start with 5 minutes and gradually increase the duration\n" +
                        "5. Practicing at a fixed time daily yields the best results";
                break;
            case "Body Scan":
                guideContent = "1. Lie down or sit comfortably\n" +
                        "2. Start from your toes and slowly scan upwards\n" +
                        "3. Pay attention to the sensations in each part\n" +
                        "4. Take deep breaths to relax any tense areas\n" +
                        "5. A full body scan takes around 15-20 minutes";
                break;
        }

        // Create Bundle to pass data to PracticeDetailFragment
        Bundle args = new Bundle();
        args.putString("practiceTitle", title);
        args.putString("practiceDescription", description);
        args.putInt("backgroundResId", backgroundRes);
        args.putString("practiceGuide", guideContent);

        Navigation.findNavController(requireView())
                .navigate(R.id.action_to_practiceDetail, args);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}