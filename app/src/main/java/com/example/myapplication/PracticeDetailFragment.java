package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
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
import androidx.fragment.app.Fragment;

public class PracticeDetailFragment extends Fragment {

    private TextView tvTitle, tvDescription, tvTimer, tvPracticeGuide;
    private ImageView ivPracticeBackground;
    private AutoCompleteTextView dropdownTimer;
    private Button btnStart, btnReset;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 0;
    private boolean timerRunning = false;
    private final long DEFAULT_TIME = 5 * 60 * 1000;

    private MediaPlayer instructionPlayer;
    private MediaPlayer loopPlayer;
    private int instructionPosition = 0;
    private int loopPosition = 0;
    private boolean wasInstructionCompleted = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_practice_detail, container, false);

        initViews(view);
        setupDropdownMenu();
        displayPracticeDetails();
        setupClickListeners();

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
    }

    private void setupAudio() {
        if (getArguments() != null) {
            String title = getArguments().getString("practiceTitle");
            int instructionResource = 0;
            int loopResource = 0;

            // Set appropriate audio resources based on practice type
            if (title != null) {
                switch (title) {
                    case "Mindfulness Meditation":
                        instructionResource = R.raw.meditation_instruction;
                        loopResource = R.raw.mindfulness_meditation;
                        break;
                    case "Body Scan":
                        instructionResource = R.raw.body_scan_instruction;
                        loopResource = R.raw.body_scan;
                        break;
                }
            }

            // Initialize MediaPlayers
            instructionPlayer = MediaPlayer.create(requireContext(), instructionResource);
            loopPlayer = MediaPlayer.create(requireContext(), loopResource);
            loopPlayer.setLooping(true);

            // Set completion listener for instruction
            instructionPlayer.setOnCompletionListener(mp -> {
                wasInstructionCompleted = true;
                instructionPosition = 0; // Reset instruction position

                // Start the loop when instruction finishes
                if (loopPlayer != null && !loopPlayer.isPlaying()) {
                    loopPlayer.seekTo(loopPosition); // Resume loop from saved position
                    loopPlayer.start();
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
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

    @SuppressLint("SetTextI18n")
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
                        timeLeftInMillis = (long) minutes * 60 * 1000;
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
    }

    @SuppressLint("SetTextI18n")
    private void startTimer() {
        if (!timerRunning) {
            // If timer wasn't running, we might need to setup audio
            if (instructionPlayer == null || loopPlayer == null) {
                setupAudio();
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
                    Toast.makeText(getContext(), "Practice completed!", Toast.LENGTH_SHORT).show();
                    stopAllAudio();
                }
            }.start();

            timerRunning = true;
            btnStart.setText("Pause");
            btnReset.setVisibility(View.VISIBLE);
            dropdownTimer.setEnabled(false);

            // Resume audio playback
            if (wasInstructionCompleted) {
                // If instruction was completed before, just resume the loop
                if (loopPlayer != null && !loopPlayer.isPlaying()) {
                    loopPlayer.seekTo(loopPosition);
                    loopPlayer.start();
                }
            } else {
                // Resume instruction if it wasn't completed
                if (instructionPlayer != null && !instructionPlayer.isPlaying()) {
                    instructionPlayer.seekTo(instructionPosition);
                    instructionPlayer.start();
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        btnStart.setText("Start");

        // Save current positions and pause both players
        if (instructionPlayer != null && instructionPlayer.isPlaying()) {
            instructionPosition = instructionPlayer.getCurrentPosition();
            instructionPlayer.pause();
        }
        if (loopPlayer != null && loopPlayer.isPlaying()) {
            loopPosition = loopPlayer.getCurrentPosition();
            loopPlayer.pause();
        }
    }

    @SuppressLint("SetTextI18n")
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

        // Reset audio state
        instructionPosition = 0;
        loopPosition = 0;
        wasInstructionCompleted = false;
        stopAllAudio();
    }

    private void stopAllAudio() {
        if (instructionPlayer != null) {
            if (instructionPlayer.isPlaying()) {
                instructionPlayer.stop();
            }
            instructionPlayer.release();
            instructionPlayer = null;
        }
        if (loopPlayer != null) {
            if (loopPlayer.isPlaying()) {
                loopPlayer.stop();
            }
            loopPlayer.release();
            loopPlayer = null;
        }
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        @SuppressLint("DefaultLocale")
        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        tvTimer.setText(timeLeftFormatted);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        stopAllAudio();
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

