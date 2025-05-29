package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ZenDoodleFragment extends Fragment {

    private ZenDoodleView zenDoodleView;
    private TextView scoreTextView;
    private Button resetButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zen_doodle, container, false);

        zenDoodleView = view.findViewById(R.id.zenDoodleView);
        scoreTextView = view.findViewById(R.id.scoreTextView);
        resetButton = view.findViewById(R.id.resetButton);

        zenDoodleView.setScoreUpdateListener((score, isDrawing) -> {
            if (isDrawing) {
                scoreTextView.setText("Score: " + score);
            } else {
                scoreTextView.setText("Drawing stopped." + "\n" + "Final score: " + score);
            }
        });

        resetButton.setOnClickListener(v -> zenDoodleView.reset());

        return view;
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
