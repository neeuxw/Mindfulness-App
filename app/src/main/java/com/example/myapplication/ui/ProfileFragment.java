package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.UserViewModel;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;

public class ProfileFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextView tvUsername;
    private TextView tvCheckInDays;
    private Button btnCheckIn;
    private Button btnLogout;
    private ImageView ivMoodIcon;
    private TextView tvMoodStatus;

    private DatabaseHelper databaseHelper;
    private String currentUsername;

    //Mood status messages
    private final String[] moodStatusMessages = {
            "Having a tough day - remember, this too shall pass",
            "Feeling a bit low - take some time for self-care",
            "You're doing okay - keep moving forward",
            "Feeling good today - embrace the positive energy",
            "Amazing mood! You're radiating positivity"
    };

    private final int[] moodIcons = {
            R.drawable.ic_mood1,     // Mood level 1
            R.drawable.ic_mood2,          // Mood level 2
            R.drawable.ic_mood3,      // Mood level 3
            R.drawable.ic_mood4,        // Mood level 4
            R.drawable.ic_mood5    // Mood level 5
    };

    // Static method to create fragment with username
    public static ProfileFragment newInstance(String username) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        tvUsername = view.findViewById(R.id.tv_username);
        tvCheckInDays = view.findViewById(R.id.tv_checkin_days);
        btnCheckIn = view.findViewById(R.id.btn_checkin);
        btnLogout = view.findViewById(R.id.btn_logout);
        ivMoodIcon = view.findViewById(R.id.iv_mood_icon);
        tvMoodStatus = view.findViewById(R.id.tv_mood_status);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(getContext());

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        userViewModel.getUsername().observe(getViewLifecycleOwner(), username -> {
            currentUsername = username;
            tvUsername.setText(username);
            updateCheckInStatus();
            updateCheckInDays();
        });

        userViewModel.getMoodLevel().observe(getViewLifecycleOwner(), moodLevel -> {
            if (moodLevel != null && moodLevel >= 1 && moodLevel <= 5) {
                setMoodDisplay(moodLevel);
            } else {
                setDefaultMoodDisplay();
            }
        });

        // Set click listeners
        btnCheckIn.setOnClickListener(v -> performCheckIn());

        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    // Update mood icon and status text based on today's mood
    private void updateMoodDisplay() {
        new Thread(() -> {
            int todaysMood = databaseHelper.getMoodForToday(currentUsername);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (todaysMood != -1) {
                        // User has set a mood today
                        setMoodDisplay(todaysMood);
                    } else {
                        // No mood set today
                        setDefaultMoodDisplay();
                    }
                });
            }
        }).start();
    }

    // Set mood icon and text for a specific mood level
    private void setMoodDisplay(int moodLevel) {
        if (moodLevel >= 1 && moodLevel <= 5) {
            // Set mood icon
            ivMoodIcon.setImageResource(moodIcons[moodLevel - 1]);
            ivMoodIcon.setVisibility(View.VISIBLE);

            // Set mood status text
            tvMoodStatus.setText(moodStatusMessages[moodLevel - 1]);
            tvMoodStatus.setVisibility(View.VISIBLE);

        }
    }

    // Set default display when no mood is recorded
    private void setDefaultMoodDisplay() {
        ivMoodIcon.setImageResource(R.drawable.ic_mood3);
        ivMoodIcon.setVisibility(View.VISIBLE);

        tvMoodStatus.setText("No mood recorded today - how are you feeling?");
        tvMoodStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvMoodStatus.setVisibility(View.VISIBLE);
    }

    private void updateCheckInStatus() {
        boolean hasCheckedInToday = databaseHelper.checkInToday(currentUsername);

        if (hasCheckedInToday) {
            btnCheckIn.setText("Already Checked In Today");
            btnCheckIn.setEnabled(false);
            btnCheckIn.setBackgroundResource(android.R.drawable.btn_default);
        } else {
            btnCheckIn.setText("Check In");
            btnCheckIn.setEnabled(true);
            btnCheckIn.setBackgroundResource(android.R.drawable.btn_default);
        }
    }

    private void updateCheckInDays() {
        int totalDays = databaseHelper.getTotalCheckInDays(currentUsername);
        tvCheckInDays.setText("Total Check-in Days: " + totalDays);
    }

    private void performCheckIn() {
        boolean success = databaseHelper.performCheckIn(currentUsername);

        if (success) {
            Toast.makeText(getContext(), "Check-in successful!", Toast.LENGTH_SHORT).show();
            updateCheckInStatus();
            updateCheckInDays();
        } else {
            Toast.makeText(getContext(), "You have already checked in today!", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        // Navigate back to LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Finish current activity
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Update status when fragment becomes visible again
        updateCheckInStatus();
        updateCheckInDays();
        updateMoodDisplay();
    }
}