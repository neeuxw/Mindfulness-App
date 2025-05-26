package com.example.myapplication;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class PracticeDetailFragment extends Fragment {

    private TextView tvTitle, tvDescription, tvTimer, tvPracticeGuide;
    private ImageView ivPracticeBackground;
    private EditText etMinutes;
    private Button btnStart, btnReset;


    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 0;
    private boolean timerRunning = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_practice_detail, container, false);

        initViews(view);

        displayPracticeDetails();

        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tv_practice_title);
        tvDescription = view.findViewById(R.id.tv_practice_desc);
        ivPracticeBackground = view.findViewById(R.id.iv_practice_background);
        tvTimer = view.findViewById(R.id.tv_timer);
        etMinutes = view.findViewById(R.id.et_minutes);
        btnStart = view.findViewById(R.id.btn_start);
        btnReset = view.findViewById(R.id.btn_reset);
        tvPracticeGuide = view.findViewById(R.id.tv_practice_guide);
    }

    private void displayPracticeDetails() {
        if (getArguments() != null) {
            String title = getArguments().getString("practiceTitle");
            String description = getArguments().getString("practiceDescription");
            int backgroundRes = getArguments().getInt("backgroundResId");
            String guide = getArguments().getString("practiceGuide");

            tvTitle.setText(title);
            tvDescription.setText(description);
            ivPracticeBackground.setImageResource(backgroundRes);
            tvPracticeGuide.setText(guide);
        }
    }


    private void setupClickListeners() {
        btnStart.setOnClickListener(v -> {
            if (timerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        btnReset.setOnClickListener(v -> resetTimer());
    }

    private void navigateBack() {
        Navigation.findNavController(requireView()).navigateUp();
    }

    private void startTimer() {
        if (timeLeftInMillis == 0) {
            String input = etMinutes.getText().toString();
            if (input.isEmpty()) {
                Toast.makeText(getContext(), "Please enter practice time", Toast.LENGTH_SHORT).show();
                return;
            }
            long inputMinutes = Long.parseLong(input);
            timeLeftInMillis = inputMinutes * 60 * 1000;
            etMinutes.setEnabled(false);
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                btnStart.setText("Start");
                Toast.makeText(getContext(), "Finish!", Toast.LENGTH_SHORT).show();
            }
        }.start();

        timerRunning = true;
        btnStart.setText("Stop");
        btnReset.setVisibility(View.VISIBLE);
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        btnStart.setText("Start");
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timeLeftInMillis = 0;
        updateCountDownText();
        btnReset.setVisibility(View.INVISIBLE);
        btnStart.setText("Start");
        etMinutes.setEnabled(true);
        etMinutes.setText("");
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        tvTimer.setText(timeLeftFormatted);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideBottomNavigation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showBottomNavigation();
        }
    }
}

