package com.example.myapplication;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Random;

public class CloudGameFragment extends Fragment {

    private EditText worryEditText;
    private Button releaseButton;
    private TextView mindfulnessMessage;
    private RelativeLayout cloudContainer;
    private Random random = new Random();

    public CloudGameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cloud_game, container, false);

        worryEditText = view.findViewById(R.id.worryEditText);
        releaseButton = view.findViewById(R.id.releaseButton);
        mindfulnessMessage = view.findViewById(R.id.mindfulnessMessage);
        cloudContainer = view.findViewById(R.id.cloudContainer);

        if (releaseButton != null) {
            releaseButton.setOnClickListener(v -> releaseWorry());
        }

        return view;
    }

    private void releaseWorry() {
        if (worryEditText == null) {
            return;
        }

        String worry = worryEditText.getText().toString().trim();
        if (worry.isEmpty()) {
            worryEditText.setError("Please enter your worry");
            return;
        }

        if (mindfulnessMessage != null) {
            mindfulnessMessage.setVisibility(View.INVISIBLE);
        }

        worryEditText.setText("");
        createCloudWithText(worry);
    }

    private void createCloudWithText(String worry) {
        if (cloudContainer == null) {
            return;
        }

        // Create main cloud with text
        FrameLayout mainCloud = createCloud(worry, 400, 400);
        if (mainCloud == null) {
            return;
        }

        RelativeLayout.LayoutParams mainParams = new RelativeLayout.LayoutParams(400, 400);
        mainParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mainParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mainParams.bottomMargin = 50;

        try {
            cloudContainer.addView(mainCloud, mainParams);
        } catch (Exception e) {
            return;
        }

        // Create blank clouds in various positions
        int numBlankClouds = 3 + random.nextInt(3); // 3-5 blank clouds
        FrameLayout[] blankClouds = new FrameLayout[numBlankClouds];

        // Get main cloud's initial position
        int mainCloudCenterX = cloudContainer.getWidth() / 2;
        int mainCloudBottom = cloudContainer.getHeight() - 50;

        for (int i = 0; i < numBlankClouds; i++) {
            // Random size for each cloud (80-180px)
            int cloudSize = 160 + random.nextInt(100);
            blankClouds[i] = createCloud("", cloudSize, cloudSize);

            if (blankClouds[i] != null) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cloudSize, cloudSize);

                // Position clouds in a semi-circle around the main cloud
                float angle = (float) (2 * Math.PI * i / numBlankClouds); // 360° 散布
                int distance = 120 + random.nextInt(80); // 120-200px from center

                // Calculate position
                int offsetX = (int) (distance * Math.cos(angle));
                int offsetY = (int) (distance * Math.sin(angle)) - 30; // Slightly upward
                int leftMargin = mainCloudCenterX - (cloudSize / 2) + offsetX;
                int topMargin = mainCloudBottom - cloudSize + offsetY;

                params.leftMargin = leftMargin;
                params.topMargin = topMargin;

                try {
                    cloudContainer.addView(blankClouds[i], params);
                } catch (Exception e) {
                    // Silently handle error
                }
            }
        }

        // Animation sequence for all clouds
        AnimatorSet animatorSet = new AnimatorSet();

        // Main cloud rises straight up
        ObjectAnimator mainRiseAnim = ObjectAnimator.ofFloat(mainCloud, "translationY", -800f);
        mainRiseAnim.setDuration(1500);
        mainRiseAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        // Blank clouds rise with slight variations
        AnimatorSet[] blankCloudAnims = new AnimatorSet[numBlankClouds];
        for (int i = 0; i < numBlankClouds; i++) {
            if (blankClouds[i] != null) {
                // Each blank cloud rises with slight horizontal movement
                float xVariation = -50 + random.nextInt(100); // -50 to 50px horizontal movement
                float yDistance = -700f - random.nextInt(300); // -400 to -600px vertical

                ObjectAnimator xAnim = ObjectAnimator.ofFloat(blankClouds[i], "translationX", xVariation);
                ObjectAnimator yAnim = ObjectAnimator.ofFloat(blankClouds[i], "translationY", yDistance);

                AnimatorSet cloudAnimSet = new AnimatorSet();
                cloudAnimSet.playTogether(xAnim, yAnim);
                cloudAnimSet.setDuration(1200 + random.nextInt(600)); // 1200-1800ms
                cloudAnimSet.setInterpolator(new AccelerateDecelerateInterpolator());

                blankCloudAnims[i] = cloudAnimSet;
            }
        }

        // Play all animations together
        AnimatorSet.Builder builder = animatorSet.play(mainRiseAnim);
        for (AnimatorSet anim : blankCloudAnims) {
            if (anim != null) {
                builder.with(anim);
            }
        }

        // After rising, create scattering effect
        mainRiseAnim.addListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {}

            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                createScatteringClouds(mainCloud);

                // Fade out all clouds
                fadeOutView(mainCloud);
                for (FrameLayout cloud : blankClouds) {
                    if (cloud != null) {
                        fadeOutView(cloud);
                    }
                }
            }

            @Override
            public void onAnimationCancel(android.animation.Animator animation) {}

            @Override
            public void onAnimationRepeat(android.animation.Animator animation) {}
        });

        animatorSet.start();

        // Clean up after animation
        cloudContainer.postDelayed(() -> {
            if (mindfulnessMessage != null) {
                mindfulnessMessage.setVisibility(View.VISIBLE);
            }
            removeViewSafely(mainCloud);
            for (FrameLayout cloud : blankClouds) {
                removeViewSafely(cloud);
            }
        }, 3500);
    }

    private FrameLayout createCloud(String text, int width, int height) {
        if (getContext() == null) {
            return null;
        }

        FrameLayout cloudWithText = new FrameLayout(requireContext());

        ImageView cloud = new ImageView(requireContext());
        try {
            cloud.setImageResource(R.drawable.cloud_icon);
        } catch (Exception e) {
            cloud.setBackgroundColor(0xFFE0E0E0); // Light gray fallback
        }

        FrameLayout.LayoutParams cloudParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        cloud.setLayoutParams(cloudParams);
        cloudWithText.addView(cloud);

        // Only add text if it's not empty
        if (!text.isEmpty()) {
            TextView worryText = new TextView(requireContext());
            worryText.setText(text);

            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    worryText.setTextColor(getResources().getColor(android.R.color.black, requireContext().getTheme()));
                } else {
                    worryText.setTextColor(getResources().getColor(android.R.color.black));
                }
            } catch (Exception e) {
                worryText.setTextColor(0xFF000000); // Black as fallback
            }

            worryText.setTextSize(20);
            worryText.setGravity(android.view.Gravity.CENTER);

            FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            textParams.gravity = android.view.Gravity.CENTER;

            worryText.setLayoutParams(textParams);
            cloudWithText.addView(worryText);
        }

        return cloudWithText;
    }

    private void createScatteringClouds(View mainCloud) {
        if (cloudContainer == null || mainCloud == null) {
            return;
        }

        int numClouds = 5 + random.nextInt(5); // 5-9 clouds

        for (int i = 0; i < numClouds; i++) {
            FrameLayout smallCloud = createCloud("", 150 + random.nextInt(100), 150 + random.nextInt(100));
            if (smallCloud == null) {
                continue;
            }

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    100 + random.nextInt(100),
                    100 + random.nextInt(100)
            );

            int mainCloudX = (int) mainCloud.getX();
            int mainCloudY = (int) mainCloud.getY();

            params.leftMargin = mainCloudX + random.nextInt(50) - 25;
            params.topMargin = mainCloudY + random.nextInt(50) - 25;

            try {
                cloudContainer.addView(smallCloud, params);
                animateScatteringCloud(smallCloud);
            } catch (Exception e) {
                // Silently handle error
            }
        }
    }

    private void animateScatteringCloud(FrameLayout cloud) {
        if (cloud == null) {
            return;
        }

        AnimatorSet set = new AnimatorSet();

        float angle = random.nextFloat() * 360;
        float distance = 200 + random.nextInt(300);
        float xDist = (float) (distance * Math.cos(Math.toRadians(angle)));
        float yDist = (float) (distance * Math.sin(Math.toRadians(angle)));

        ObjectAnimator xAnim = ObjectAnimator.ofFloat(cloud, "translationX", xDist);
        ObjectAnimator yAnim = ObjectAnimator.ofFloat(cloud, "translationY", yDist);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(cloud, "alpha", 1f, 0f);

        set.playTogether(xAnim, yAnim, alphaAnim);
        set.setDuration(1500 + random.nextInt(1000));
        set.setInterpolator(new OvershootInterpolator());

        try {
            set.start();
        } catch (Exception e) {
            // Silently handle error
        }

        cloud.postDelayed(() -> {
            if (cloudContainer != null && cloud != null) {
                try {
                    cloudContainer.removeView(cloud);
                } catch (Exception e) {
                    // Silently handle error
                }
            }
        }, 2500);
    }

    private void fadeOutView(View view) {
        if (view != null) {
            AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            fadeOut.setDuration(1000);
            fadeOut.setFillAfter(true);
            view.startAnimation(fadeOut);
        }
    }

    private void removeViewSafely(View view) {
        if (cloudContainer != null && view != null) {
            try {
                cloudContainer.removeView(view);
            } catch (Exception e) {
                // Silently handle error
            }
        }
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