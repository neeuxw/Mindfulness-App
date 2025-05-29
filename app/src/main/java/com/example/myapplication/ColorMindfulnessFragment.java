package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ColorMindfulnessFragment extends Fragment {

    private TextView colorInstruction, feedbackText;
    private LinearLayout colorOptionsLayout;
    private Button nextColorButton;

    private final String[] colors = {"Red", "Blue", "Green", "Yellow", "Purple"};
    private final int[] colorValues = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA
    };

    private int correctColorIndex;

    public ColorMindfulnessFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_color_mindfulness, container, false);

        colorInstruction = view.findViewById(R.id.colorInstruction);
        feedbackText = view.findViewById(R.id.feedbackText);
        colorOptionsLayout = view.findViewById(R.id.colorOptionsLayout);
        nextColorButton = view.findViewById(R.id.nextColorButton);

        nextColorButton.setOnClickListener(v -> startNewColorChallenge());

        startNewColorChallenge();

        return view;
    }

    private void startNewColorChallenge() {
        feedbackText.setText("");
        nextColorButton.setVisibility(View.GONE);
        colorOptionsLayout.removeAllViews();

        correctColorIndex = new Random().nextInt(colors.length);
        String targetColor = colors[correctColorIndex];
        colorInstruction.setText("Tap the color: " + targetColor);

        // 先把正确答案放进列表
        List<Integer> options = new ArrayList<>();
        options.add(correctColorIndex);

        // 剩余颜色索引
        List<Integer> remaining = new ArrayList<>();
        for (int i = 0; i < colors.length; i++) {
            if (i != correctColorIndex) {
                remaining.add(i);
            }
        }
        // 随机打乱剩余颜色
        Collections.shuffle(remaining);

        // 加入2个随机干扰颜色，确保一共3个选项
        options.add(remaining.get(0));
        options.add(remaining.get(1));

        // 打乱顺序，使正确答案位置随机
        Collections.shuffle(options);

        for (int colorIndex : options) {
            View colorView = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
            params.setMargins(16, 0, 16, 0);
            colorView.setLayoutParams(params);
            colorView.setBackgroundColor(colorValues[colorIndex]);

            int finalColorIndex = colorIndex;
            colorView.setOnClickListener(v -> {
                if (finalColorIndex == correctColorIndex) {
                    feedbackText.setText("Great job. You're aware of the present.");
                    nextColorButton.setVisibility(View.VISIBLE);
                } else {
                    feedbackText.setText("Try again. Look closely.");
                }
            });

            colorOptionsLayout.addView(colorView);
        }
    }
}
