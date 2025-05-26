package com.example.myapplication.ui.home;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;

import java.util.Calendar;
import java.util.Random;

public class HomeFragment extends Fragment {

    private TextView quoteText;
    private Button refreshQuoteBtn;
    private Button quickStartBtn;
    private TextView greetingText;
    private TextView usernameText; // 新增用户名TextView
    private TextView moodSuggestionText;
    private ImageView[] moodIcons = new ImageView[5];
    private DatabaseHelper dbHelper; // 数据库帮助类

    // 名言数组
    private final String[] quotes = {
            "Mindfulness is a way of befriending ourselves and our experience.",
            "Inhale the future, exhale the past.",
            "Breathe in, calm. Breathe out, smile.",
            "Feel the moment before it slips away.",
            "The present moment is the only time over which we have dominion.",
            "Be where your feet are."
    };

    // 心情建议数组
    private final String[] moodSuggestions = {
            "You seem to be feeling down. Try deep breathing to ease your emotions.",
            "You seem a bit low. A 10-minute loving-kindness meditation might help.",
            "You're feeling okay. A 5-minute breathing session could lift your mood.",
            "You're in a good mood—keep it up! Try a 15-minute meditation.",
            "That's great! You're feeling wonderful—consider sharing this calm with others."
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化数据库帮助类
        dbHelper = new DatabaseHelper(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 初始化视图
        quoteText = view.findViewById(R.id.quote_text);
        refreshQuoteBtn = view.findViewById(R.id.refresh_quote_btn);
        quickStartBtn = view.findViewById(R.id.quick_start_btn1);
        greetingText = view.findViewById(R.id.greeting_text);
        usernameText = view.findViewById(R.id.username_text); // 初始化用户名TextView
        moodSuggestionText = view.findViewById(R.id.mood_suggestion_text);

        // 初始化心情图标
        moodIcons[0] = view.findViewById(R.id.mood_1);
        moodIcons[1] = view.findViewById(R.id.mood_2);
        moodIcons[2] = view.findViewById(R.id.mood_3);
        moodIcons[3] = view.findViewById(R.id.mood_4);
        moodIcons[4] = view.findViewById(R.id.mood_5);

        // 设置初始名言
        showRandomQuote();

        // 设置问候语和用户名
        updateGreeting();
        loadUsername(); // 加载用户名

        // 设置心情图标点击事件
        setupMoodIcons();

        // 刷新名言按钮点击事件
        refreshQuoteBtn.setOnClickListener(v -> showRandomQuote());

        // 快速开始按钮点击事件
        quickStartBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Start breathing exercise", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    // 从数据库加载用户名
    private void loadUsername() {
        new Thread(() -> {
            String username = getUsernameFromDatabase();

            // 回到主线程更新UI
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (username != null && !username.isEmpty()) {
                        usernameText.setText(username);
                    } else {
                        usernameText.setText("User"); // 默认值
                    }
                });
            }
        }).start();
    }

    // 从数据库获取用户名
    private String getUsernameFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String username = null;

        // 查询用户名（假设用户表中有username字段）
        Cursor cursor = db.query(
                "users",
                new String[]{"username"},
                null, null, null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            username = cursor.getString(0);
            cursor.close();
        }
        db.close();

        return username;
    }

    // 显示随机名言
    private void showRandomQuote() {
        Random random = new Random();
        int index = random.nextInt(quotes.length);
        quoteText.setText(quotes[index]);
    }

    // 根据时间更新问候语
    private void updateGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour >= 5 && hour < 12) {
            greeting = "Good morning!";
        } else if (hour >= 12 && hour < 18) {
            greeting = "Good afternoon!";
        } else {
            greeting = "Good evening!";
        }

        greetingText.setText(greeting);
    }

    // 设置心情图标点击事件
    private void setupMoodIcons() {
        for (int i = 0; i < moodIcons.length; i++) {
            final int moodLevel = i + 1;
            moodIcons[i].setOnClickListener(v -> {
                updateMoodSelection(moodLevel);
                showMoodSuggestion(moodLevel);
            });
        }
    }

    // 更新心情选择状态
    private void updateMoodSelection(int selectedMood) {
        for (int i = 0; i < moodIcons.length; i++) {
            if (i < selectedMood) {
                moodIcons[i].setAlpha(1.0f); // 选中状态
            } else {
                moodIcons[i].setAlpha(0.3f); // 未选中状态
            }
        }
    }

    // 显示心情建议
    private void showMoodSuggestion(int moodLevel) {
        if (moodLevel >= 1 && moodLevel <= 5) {
            moodSuggestionText.setText(moodSuggestions[moodLevel - 1]);
        }
    }

    @Override
    public void onDestroy() {
        dbHelper.close(); // 关闭数据库连接
        super.onDestroy();
    }
}