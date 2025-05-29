package com.example.myapplication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {

    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final MutableLiveData<Integer> moodLevel = new MutableLiveData<>();

    public void setUsername(String username) {
        this.username.setValue(username);
    }

    public LiveData<String> getUsername() {
        return username;
    }

    public void setMoodLevel(int moodLevel) {
        this.moodLevel.setValue(moodLevel);
    }

    public LiveData<Integer> getMoodLevel() {
        return moodLevel;
    }
}
