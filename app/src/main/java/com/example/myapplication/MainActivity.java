package com.example.myapplication;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navView = binding.navView;

        // Set up the ActionBar
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // Configure the ActionBar with the navigation graph
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_practice,
                R.id.navigation_discover,
                R.id.navigation_game,
                R.id.navigation_profile)
                .build();

        // Set up the ActionBar with the navigation controller and configuration
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Set up the BottomNavigationView with the navigation controller
        NavigationUI.setupWithNavController(navView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();

            // Define your destination IDs here
            boolean shouldHideBottomNav = (destinationId == R.id.practiceDetailFragment ||
                    destinationId == R.id.breathingFragment ||
                    destinationId == R.id.articleFragment ||
                    destinationId == R.id.cloudGameFragment ||
                    destinationId == R.id.colorMindfulnessFragment ||
                    destinationId == R.id.zenDoodleFragment ||
                    destinationId == R.id.focusTapFragment);

            if (shouldHideBottomNav) {
                // Hide bottom navigation
                navView.setVisibility(View.GONE);
                // Show back button
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            } else {
                // Show bottom navigation
                navView.setVisibility(View.VISIBLE);
                // Hide back button
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
        });

    }

    // Handle back button press
    @Override
    public boolean onSupportNavigateUp() {
        boolean handled = NavigationUI.navigateUp(navController, appBarConfiguration);
        if (!handled) {
            handled = super.onSupportNavigateUp();
        }
        return handled;
    }

    // Helper method to hide bottom navigation
    public void hideBottomNavigation() {
        navView.setVisibility(View.GONE);
    }

    public void showBottomNavigation() {
        navView.setVisibility(View.VISIBLE);
    }
}