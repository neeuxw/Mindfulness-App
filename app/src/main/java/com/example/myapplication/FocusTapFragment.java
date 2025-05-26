package com.example.myapplication;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Objects;
import java.util.Random;

public class FocusTapFragment extends Fragment {

    private TextView timerText;
    private TextView scoreText;
    private Button targetButton;
    private Button startButton;

    private int score = 0;
    private boolean isGameRunning = false;
    private CountDownTimer gameTimer;
    private CountDownTimer buttonTimer;
    private Random random = new Random();

    public FocusTapFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_focus_tap, container, false);

        timerText = view.findViewById(R.id.timerText);
        scoreText = view.findViewById(R.id.scoreText);
        targetButton = view.findViewById(R.id.targetButton);
        startButton = view.findViewById(R.id.startButton);

        // Hide target button initially
        targetButton.setVisibility(View.INVISIBLE);

        startButton.setOnClickListener(v -> startGame());
        targetButton.setOnClickListener(v ->{
            // 添加点击动画
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.button_click_animation);
            v.startAnimation(anim);
            onTargetClicked();
        });

        return view;
    }

    private void startGame() {
        if (isGameRunning) return;

        isGameRunning = true;
        score = 0;
        scoreText.setText("Score: 0");
        startButton.setVisibility(View.INVISIBLE);

        // show random button
        showRandomButton();

        // 30 seconds timer
        gameTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                endGame();
            }
        }.start();
    }

    private void showRandomButton() {
        // Cancel previous timer
        if (buttonTimer != null) {
            buttonTimer.cancel();
        }

        moveButtonToRandomPosition();
        targetButton.setVisibility(View.VISIBLE);

        // 1.5s later hide button
        buttonTimer = new CountDownTimer(1500, 1500) { // 只在结束时触发
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (isGameRunning) {
                    targetButton.setVisibility(View.INVISIBLE);
                    showRandomButton();
                }
            }
        }.start();
    }

    private void moveButtonToRandomPosition() {
        View rootView = getView();
        if (rootView == null) return;

        rootView.post(() -> {
            int width = rootView.getWidth() - targetButton.getWidth();
            int height = rootView.getHeight() - targetButton.getHeight();

            if (width <= 0 || height <= 0) return;

            int x = random.nextInt(width);
            int y = random.nextInt(height);

            targetButton.setX(x);
            targetButton.setY(y);
        });
    }

    private void onTargetClicked() {
        if (!isGameRunning) return;

        score++;
        scoreText.setText("Score: " + score);

        targetButton.setVisibility(View.INVISIBLE);
        showRandomButton();
    }

    private void endGame() {
        isGameRunning = false;
        targetButton.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);

        timerText.setText("Game finish!");
        startButton.setText("Play again");

        if (gameTimer != null) {
            gameTimer.cancel();
        }
        if (buttonTimer != null) {
            buttonTimer.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gameTimer != null) {
            gameTimer.cancel();
        }
        if (buttonTimer != null) {
            buttonTimer.cancel();
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