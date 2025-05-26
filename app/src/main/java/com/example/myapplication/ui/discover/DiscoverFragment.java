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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        // 文章1点击事件 - 正念基础
        view.findViewById(R.id.article1_button).setOnClickListener(v -> {
            navigateToArticle(
                    "The Beginner's Guide",
                    R.drawable.discover_image1,
                    getString(R.string.article1_content)
            );
        });

        // 文章2点击事件 - 压力管理
        view.findViewById(R.id.article2_button).setOnClickListener(v -> {
            navigateToArticle(
                    "5 Techniques to Reduce Stress",
                    R.drawable.discover_image2,
                    getString(R.string.article2_content)
            );
        });

        // 文章3点击事件 - 睡眠改善
        view.findViewById(R.id.article3_button).setOnClickListener(v -> {
            navigateToArticle(
                    "Mindfulness for Sleep",
                    R.drawable.discover_image3,
                    getString(R.string.article3_content)
            );
        });

        // 文章4点击事件 - 情绪调节
        view.findViewById(R.id.article4_button).setOnClickListener(v -> {
            navigateToArticle(
                    "Mindfulness for Managing Moods",
                    R.drawable.discover_image4,
                    getString(R.string.article4_content)
            );
        });

        // 文章5点击事件 - 日常练习
        view.findViewById(R.id.article5_button).setOnClickListener(v -> {
            navigateToArticle(
                    "Integrate Mindfulness into Daily Life",
                    R.drawable.discover_image5,
                    getString(R.string.article5_content)
            );
        });

        return view;
    }

    private void navigateToArticle(String title, int imageRes, String content) {
        Bundle args = new Bundle();
        args.putString("articleTitle", title);
        args.putInt("articleImageRes", imageRes);
        args.putString("articleContent", content);

        Navigation.findNavController(requireView())
                .navigate(R.id.action_to_article, args);
    }
}