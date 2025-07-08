package com.example.myapplication.ui.home;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.UserViewModel;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;

import java.util.Calendar;
import java.util.Random;

public class HomeFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextView quoteText;
    private ImageButton refreshQuoteBtn;

    private Button quickStartBtn;
    private TextView greetingText;
    private TextView usernameText;
    private TextView moodSuggestionText;
    private ImageView[] moodIcons = new ImageView[5];
    private DatabaseHelper dbHelper;

    // Store current username for mood saving
    private String currentUsername;

    private final String[] quotes = {
            "Mindfulness is a way of befriending ourselves and our experience.",
            "Inhale the future, exhale the past.",
            "Breathe in, calm. Breathe out, smile.",
            "Feel the moment before it slips away.",
            "The present moment is the only time over which we have dominion.",
            "Be where your feet are."
    };

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
        dbHelper = new DatabaseHelper(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        quoteText = view.findViewById(R.id.quote_text);
        refreshQuoteBtn = view.findViewById(R.id.refresh_quote_btn);
        quickStartBtn = view.findViewById(R.id.quick_start_btn1);
        greetingText = view.findViewById(R.id.greeting_text);
        usernameText = view.findViewById(R.id.username_text);
        moodSuggestionText = view.findViewById(R.id.mood_suggestion_text);

        moodIcons[0] = view.findViewById(R.id.mood_1);
        moodIcons[1] = view.findViewById(R.id.mood_2);
        moodIcons[2] = view.findViewById(R.id.mood_3);
        moodIcons[3] = view.findViewById(R.id.mood_4);
        moodIcons[4] = view.findViewById(R.id.mood_5);

        showRandomQuote();

        updateGreeting();
        loadUsername();

        setupMoodIcons();

        refreshQuoteBtn.setOnClickListener(v -> showRandomQuote());

        quickStartBtn.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_to_cloudGameFragment);
        });

        return view;
    }

    private void loadUsername() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
        String username = prefs.getString("logged_in_username", null);

        if (username != null && !username.isEmpty()) {
            usernameText.setText(username);
            currentUsername = username; // Store username for mood saving
            userViewModel.setUsername(username);  // ViewModel update
            loadTodaysMood(); // Load today's mood if exists
        } else {
            usernameText.setText("User");
            currentUsername = "User"; // Default username
            userViewModel.setUsername("User"); // ViewModel update
        }
    }

    // Load today's mood from database
    private void loadTodaysMood() {
        new Thread(() -> {
            int todaysMood = dbHelper.getMoodForToday(currentUsername);

            if (getActivity() != null && todaysMood != -1) {
                getActivity().runOnUiThread(() -> {
                    updateMoodSelection(todaysMood);
                    showMoodSuggestion(todaysMood);
                    userViewModel.setMoodLevel(todaysMood);  // ViewModel update
                });
            }
        }).start();
    }

    private void showRandomQuote() {
        Random random = new Random();
        int index = random.nextInt(quotes.length);
        quoteText.setText(quotes[index]);
    }

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

    private void setupMoodIcons() {
        for (int i = 0; i < moodIcons.length; i++) {
            final int moodLevel = i + 1;
            moodIcons[i].setOnClickListener(v -> {
                updateMoodSelection(moodLevel);
                showMoodSuggestion(moodLevel);
                saveMoodToDatabase(moodLevel); // Save mood to database
            });
        }
    }

    // Save mood to database
    private void saveMoodToDatabase(int moodLevel) {
        if (currentUsername != null) {
            new Thread(() -> {
                boolean success = dbHelper.saveMood(currentUsername, moodLevel);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (success) {
                            Toast.makeText(getContext(), "Mood saved successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to save mood", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        }
    }

    private void updateMoodSelection(int selectedMood) {
        for (int i = 0; i < moodIcons.length; i++) {
            if (i < selectedMood) {
                moodIcons[i].setAlpha(1.0f);
            } else {
                moodIcons[i].setAlpha(0.3f);
            }
        }
    }

    private void showMoodSuggestion(int moodLevel) {
        if (moodLevel >= 1 && moodLevel <= 5) {
            moodSuggestionText.setText(moodSuggestions[moodLevel - 1]);
        }
    }

    @Override
    public void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}