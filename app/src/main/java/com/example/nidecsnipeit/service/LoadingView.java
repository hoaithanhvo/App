package com.example.nidecsnipeit.service;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.example.nidecsnipeit.R;

public class LoadingView extends FrameLayout {
    private LottieAnimationView loadingAnimation;

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.loading_lottie_view, this, true);
        loadingAnimation = findViewById(R.id.loadingAnimation);
    }

    public void show() {
        setVisibility(VISIBLE);
        loadingAnimation.playAnimation();
    }

    public void hide() {
        loadingAnimation.cancelAnimation();
        setVisibility(GONE);
    }
}
