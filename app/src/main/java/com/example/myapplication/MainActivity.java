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

        // 设置导航控制器
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // 配置AppBar (指定哪些是顶级目的地)
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_practice,
                R.id.navigation_discover,
                R.id.navigation_game,
                R.id.navigation_profile)
                .build();

        // 设置ActionBar与导航控制器的关联
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // 设置底部导航与导航控制器的关联
        NavigationUI.setupWithNavController(navView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();

            // 定义需要隐藏底部导航的页面
            boolean shouldHideBottomNav = (destinationId == R.id.practiceDetailFragment ||
                    destinationId == R.id.articleFragment ||
                    destinationId == R.id.cloudGameFragment ||
                    destinationId == R.id.colorMindfulnessFragment ||
                    destinationId == R.id.zenDoodleFragment ||
                    destinationId == R.id.focusTapFragment);

            if (shouldHideBottomNav) {
                // 隐藏底部导航
                navView.setVisibility(View.GONE);
                // 显示返回按钮
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            } else {
                // 显示底部导航
                navView.setVisibility(View.VISIBLE);
                // 隐藏返回按钮（因为这是顶级目的地）
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
        });

    }

    // 关键方法：处理ActionBar返回按钮点击
    @Override
    public boolean onSupportNavigateUp() {
        // 先尝试用导航控制器处理返回
        boolean handled = NavigationUI.navigateUp(navController, appBarConfiguration);
        if (!handled) {
            // 如果导航控制器无法处理，则调用父类方法
            handled = super.onSupportNavigateUp();
        }
        return handled;
    }

    // 可选：如果需要手动控制底部导航显示/隐藏
    public void hideBottomNavigation() {
        navView.setVisibility(View.GONE);
    }

    public void showBottomNavigation() {
        navView.setVisibility(View.VISIBLE);
    }
}