package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class ArticleFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        ImageView articleImage = view.findViewById(R.id.article_image);
        TextView articleTitle = view.findViewById(R.id.article_title);
        TextView articleContent = view.findViewById(R.id.article_content);

        if (getArguments() != null) {
            String title = getArguments().getString("articleTitle");
            int imageRes = getArguments().getInt("articleImageRes");
            String content = getArguments().getString("articleContent");

            articleImage.setImageResource(imageRes);
            articleTitle.setText(title);
            articleContent.setText(content);
        }

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