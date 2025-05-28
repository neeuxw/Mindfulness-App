package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class CloudGameFragment extends Fragment {

    private EditText worryEditText;
    private Button releaseButton;
    private TextView mindfulnessMessage;
    private RelativeLayout cloudContainer;

    public CloudGameFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cloud_game, container, false);

        worryEditText = view.findViewById(R.id.worryEditText);
        releaseButton = view.findViewById(R.id.releaseButton);
        mindfulnessMessage = view.findViewById(R.id.mindfulnessMessage);
        cloudContainer = view.findViewById(R.id.cloudContainer);

        releaseButton.setOnClickListener(v -> releaseWorry());

        return view;
    }

    private void releaseWorry() {
        String worry = worryEditText.getText().toString().trim();
        if (worry.isEmpty()) {
            worryEditText.setError("Please enter your worry");
            return;
        }

        mindfulnessMessage.setVisibility(View.INVISIBLE);

        FrameLayout cloudWithText = new FrameLayout(requireContext());

        ImageView cloud = new ImageView(requireContext());
        cloud.setImageResource(R.drawable.cloud_icon);

        TextView worryText = new TextView(requireContext());
        worryText.setText(worry);
        worryText.setTextColor(getResources().getColor(android.R.color.black, requireContext().getTheme()));
        worryText.setTextSize(30);
        worryText.setGravity(android.view.Gravity.CENTER);

        // Set container, cloud, and text layout parameters
        RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(
                300, 300);
        containerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        containerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        containerParams.bottomMargin = 50;

        FrameLayout.LayoutParams cloudParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        textParams.gravity = android.view.Gravity.CENTER;

        // Apply layout parameters to the views
        cloud.setLayoutParams(cloudParams);
        worryText.setLayoutParams(textParams);

        cloudWithText.addView(cloud);
        cloudWithText.addView(worryText);

        cloudContainer.addView(cloudWithText, containerParams);

        // Create animation - cloud moves up
        ObjectAnimator moveAnimator = ObjectAnimator.ofFloat(
                cloudWithText, "translationY", -1000f);
        moveAnimator.setDuration(3000);
        moveAnimator.setInterpolator(new AccelerateInterpolator());

        // Create animation - text fades out
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(3000);
        fadeOut.setFillAfter(true);

        // Start animations
        cloudWithText.startAnimation(fadeOut);
        moveAnimator.start();

        // After 3 seconds, remove the cloud and show the message
        cloudWithText.postDelayed(() -> {
            mindfulnessMessage.setVisibility(View.VISIBLE);
            cloudContainer.removeView(cloudWithText);
            worryEditText.setText("");
        }, 3000);
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