package com.example.myapplication.ui.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;

public class DiscoverFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupArticleButton(R.id.article1_button, "The Beginner's Guide", R.drawable.discover_image1, getString(R.string.article1_content), view);
        setupArticleButton(R.id.article2_button, "5 Techniques to Reduce Stress", R.drawable.discover_image2, getString(R.string.article2_content), view);
        setupArticleButton(R.id.article3_button, "Mindful Breathing", R.drawable.discover_image3, getString(R.string.article3_content), view);
        setupArticleButton(R.id.article4_button, "Creating a Calm Space", R.drawable.discover_image4, getString(R.string.article4_content), view);
        setupArticleButton(R.id.article5_button, "Daily Mindfulness Routine", R.drawable.discover_image5, getString(R.string.article5_content), view);
    }

    private void setupArticleButton(int buttonId, String title, int imageRes, String content, View rootView) {
        rootView.findViewById(buttonId).setOnClickListener(v -> {
            navigateToArticle(title, imageRes, content, v);
        });
    }

    private void navigateToArticle(String title, int imageRes, String content, View clickedView) {
        Bundle args = new Bundle();
        args.putString("articleTitle", title);
        args.putInt("articleImageRes", imageRes);
        args.putString("articleContent", content);

        Navigation.findNavController(clickedView).navigate(R.id.action_to_article, args);
    }
}