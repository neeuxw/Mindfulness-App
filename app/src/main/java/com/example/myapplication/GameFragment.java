package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class GameFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        CardView focusTapCard = view.findViewById(R.id.card_focus_tap);
        CardView cloudGameCard = view.findViewById(R.id.card_cloud_game);
        CardView colorMindfulnessCard = view.findViewById(R.id.card_color_mindfulness);
        CardView zenDoodleCard = view.findViewById(R.id.card_zen_doodle);

        focusTapCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_gameFragment_to_focusTapFragment);
        });

        cloudGameCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_gameFragment_to_cloudGameFragment);
        });

        colorMindfulnessCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_gameFragment_to_colorMindfulnessFragment);
        });

        zenDoodleCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_gameFragment_to_zenDoodleFragment);
        });

        return view;
    }
}