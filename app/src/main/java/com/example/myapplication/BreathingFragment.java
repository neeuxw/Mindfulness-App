package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class BreathingFragment extends Fragment {

    private TextView tvTitle, tvDescription, tvTimer;
    private ImageView ivPracticeBackground;
    private AutoCompleteTextView dropdownTimer;
    private Button btnStart, btnReset;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 0;
    private boolean timerRunning = false;
    private final long DEFAULT_TIME = 5 * 60 * 1000; // 5 minutes in milliseconds

    private MediaPlayer instructionPlayer;
    private MediaPlayer loopPlayer;
    private boolean isInstructionCompleted = false;
    private int loopPosition = 0; // To track loop position when paused

    private static final int BREATHING_478 = 1;
    private static final int BREATHING_UJJAYI = 2;
    private static final int BREATHING_NADI = 3;
    private int currentBreathingType = BREATHING_478; // Default to 4-7-8
    private Button btnBreathing1, btnBreathing2, btnBreathing3;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_breathing, container, false);

        initViews(view);
        setupDropdownMenu();
        displayPracticeDetails();
        setupClickListeners();
        setupMediaPlayers(); // Initialize media player

        updateButtonStates();

        // Set default time
        timeLeftInMillis = DEFAULT_TIME;
        updateCountDownText();

        return view;
    }

    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tv_practice_title);
        tvDescription = view.findViewById(R.id.tv_practice_desc);
        ivPracticeBackground = view.findViewById(R.id.iv_practice_background);
        tvTimer = view.findViewById(R.id.tv_timer);
        dropdownTimer = view.findViewById(R.id.dropdown_timer);
        btnStart = view.findViewById(R.id.btn_start);
        btnReset = view.findViewById(R.id.btn_reset);

        btnBreathing1 = view.findViewById(R.id.btn_breathing1);
        btnBreathing2 = view.findViewById(R.id.btn_breathing2);
        btnBreathing3 = view.findViewById(R.id.btn_breathing3);
    }

    private void setupMediaPlayers() {
        // Release any existing players
        releaseMediaPlayers();

        try {
            // Set appropriate audio resources based on current technique
            int instructionRes = R.raw.breathing_instruction; // Default instruction
            int loopRes = R.raw.breathing_exercise; // Default loop

            switch (currentBreathingType) {
                case BREATHING_478:
                    instructionRes = R.raw.breathing_instruction;
                    loopRes = R.raw.breathing_exercise;
                    break;
                case BREATHING_UJJAYI:
                    instructionRes = R.raw.ujjayi_instruction;
                    loopRes = R.raw.ujjayi_exercise;
                    break;
                case BREATHING_NADI:
                    instructionRes = R.raw.nadi_shodhana_instruction;
                    loopRes = R.raw.nadi_shodhana_exercise;
                    break;
            }

            // Initialize instruction player
            instructionPlayer = MediaPlayer.create(getContext(), instructionRes);
            instructionPlayer.setOnCompletionListener(mp -> {
                isInstructionCompleted = true;
                startLoopMusic();
            });

            // Initialize loop player
            loopPlayer = MediaPlayer.create(getContext(), loopRes);
            loopPlayer.setLooping(true);
        } catch (Exception e) {
            Log.e("BreathingFragment", "Error initializing media players", e);
        }
    }

    private void startLoopMusic() {
        try {
            if (loopPlayer != null) {
                loopPlayer.seekTo(loopPosition); // Resume from saved position
                loopPlayer.start();
            }
        } catch (IllegalStateException e) {
            Log.e("BreathingFragment", "Error starting loop music", e);
        }
    }

    private void startMusic() {
        try {
            if (instructionPlayer != null && !isInstructionCompleted) {
                // Start with instruction if not completed yet
                instructionPlayer.start();
            } else if (loopPlayer != null) {
                // Otherwise start loop directly
                startLoopMusic();
            }
        } catch (IllegalStateException e) {
            setupMediaPlayers(); // Reinitialize if error occurs
            startMusic(); // Retry
        }
    }

    private void pauseMusic() {
        try {
            // Save loop position when pausing
            if (loopPlayer != null && loopPlayer.isPlaying()) {
                loopPosition = loopPlayer.getCurrentPosition();
                loopPlayer.pause();
            }
            if (instructionPlayer != null && instructionPlayer.isPlaying()) {
                instructionPlayer.pause();
            }
        } catch (IllegalStateException e) {
            // Handle error
        }
    }

    private void stopMusic() {
        try {
            if (instructionPlayer != null) {
                if (instructionPlayer.isPlaying()) {
                    instructionPlayer.stop();
                }
                instructionPlayer.release();
            }
            if (loopPlayer != null) {
                if (loopPlayer.isPlaying()) {
                    loopPlayer.stop();
                }
                loopPlayer.release();
            }
        } catch (IllegalStateException e) {
            // Handle error
        } finally {
            instructionPlayer = null;
            loopPlayer = null;
            isInstructionCompleted = false;
            loopPosition = 0;
        }
    }

    private void releaseMediaPlayers() {
        stopMusic();
    }

    private void setupDropdownMenu() {
        // Create options for dropdown
        String[] timeOptions = new String[]{
                "1 minute",
                "2 minutes",
                "5 minutes",
                "10 minutes",
                "15 minutes",
                "Custom time"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.dropdown_menu_item,
                timeOptions
        );

        dropdownTimer.setAdapter(adapter);
        dropdownTimer.setText("5 minutes", false);

        dropdownTimer.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0: // 1 minute
                    timeLeftInMillis = 60 * 1000;
                    break;
                case 1: // 2 minutes
                    timeLeftInMillis = 2 * 60 * 1000;
                    break;
                case 2: // 5 minutes (default)
                    timeLeftInMillis = 5 * 60 * 1000;
                    break;
                case 3: // 10 minutes
                    timeLeftInMillis = 10 * 60 * 1000;
                    break;
                case 4: // 15 minutes
                    timeLeftInMillis = 15 * 60 * 1000;
                    break;
                case 5: // Custom time
                    showCustomTimeDialog();
                    return; // Don't update timer yet
            }
            updateCountDownText();
        });
    }

    private void showCustomTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enter Custom Time");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Minutes");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String minutesStr = input.getText().toString();
            if (!minutesStr.isEmpty()) {
                try {
                    int minutes = Integer.parseInt(minutesStr);
                    if (minutes > 0) {
                        timeLeftInMillis = minutes * 60 * 1000;
                        dropdownTimer.setText(minutes + " minutes (custom)", false);
                        updateCountDownText();
                    } else {
                        Toast.makeText(getContext(), "Please enter a positive number", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid number", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please enter minutes", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            dropdownTimer.setText("5 minutes", false);
        });

        builder.show();
    }

    private void displayPracticeDetails() {
        if (getArguments() != null) {
            String title = getArguments().getString("practiceTitle");
            String description = getArguments().getString("practiceDescription");
            int backgroundRes = getArguments().getInt("backgroundResId");

            tvTitle.setText(title);
            tvDescription.setText(description);
            ivPracticeBackground.setImageResource(backgroundRes);
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

        btnBreathing1.setOnClickListener(v -> switchBreathingTechnique(BREATHING_478));
        btnBreathing2.setOnClickListener(v -> switchBreathingTechnique(BREATHING_UJJAYI));
        btnBreathing3.setOnClickListener(v -> switchBreathingTechnique(BREATHING_NADI));
    }

    private void switchBreathingTechnique(int technique) {
        if (currentBreathingType == technique) return;

        currentBreathingType = technique;
        updateButtonStates();
        resetTimer(); // Reset timer and audio when switching techniques

        // Update UI based on selected technique
        switch (technique) {
            case BREATHING_478:
                tvTitle.setText("4-7-8 Breathing");
                tvDescription.setText("Calming breathing pattern for relaxation");
                break;
            case BREATHING_UJJAYI:
                tvTitle.setText("Ujjayi Breathing");
                tvDescription.setText("Balanced breathing for mindfulness");
                break;
            case BREATHING_NADI:
                tvTitle.setText("Nadi Shodhana Breathing");
                tvDescription.setText("Military technique for focus and calm");
                break;
        }
    }

    private void updateButtonStates() {
        // Reset all buttons
        btnBreathing1.setBackgroundResource(R.drawable.button_outline);
        btnBreathing2.setBackgroundResource(R.drawable.button_outline);
        btnBreathing3.setBackgroundResource(R.drawable.button_outline);
        btnBreathing1.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_blue));
        btnBreathing2.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_blue));
        btnBreathing3.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_blue));

        // Set the selected button style
        switch (currentBreathingType) {
            case BREATHING_478:
                btnBreathing1.setBackgroundResource(R.drawable.button_filled);
                btnBreathing1.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                break;
            case BREATHING_UJJAYI:
                btnBreathing2.setBackgroundResource(R.drawable.button_filled);
                btnBreathing2.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                break;
            case BREATHING_NADI:
                btnBreathing3.setBackgroundResource(R.drawable.button_filled);
                btnBreathing3.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                break;
        }
    }

    private void startTimer() {
        startMusic(); // Start playing music when timer starts
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
                Toast.makeText(getContext(), "Practice completed!", Toast.LENGTH_SHORT).show();
                stopMusic();
            }
        }.start();

        timerRunning = true;
        btnStart.setText("Pause");
        btnReset.setVisibility(View.VISIBLE);
        dropdownTimer.setEnabled(false);
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        btnStart.setText("Start");
        pauseMusic();
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerRunning = false;
        timeLeftInMillis = DEFAULT_TIME;
        updateCountDownText();
        btnStart.setText("Start");
        btnReset.setVisibility(View.INVISIBLE);
        dropdownTimer.setEnabled(true);
        dropdownTimer.setText("5 minutes", false);

        isInstructionCompleted = false;
        loopPosition = 0;
        stopMusic();
        setupMediaPlayers(); // Reinitialize players for fresh start
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
        stopMusic();
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