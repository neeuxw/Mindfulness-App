package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;

public class ProfileFragment extends Fragment {

    private TextView tvUsername;
    private TextView tvCheckInDays;
    private Button btnCheckIn;
    private Button btnLogout;

    private DatabaseHelper databaseHelper;
    private String currentUsername;

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

        // Initialize database helper
        databaseHelper = new DatabaseHelper(getContext());

        // Get username from arguments
        if (getArguments() != null) {
            currentUsername = getArguments().getString("username", "");
        } else {
            currentUsername = "User";
        }

        // Set username
        tvUsername.setText("Welcome, " + currentUsername + "!");

        // Update check-in status and days
        updateCheckInStatus();
        updateCheckInDays();

        // Set click listeners
        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performCheckIn();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        return view;
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
    }
}