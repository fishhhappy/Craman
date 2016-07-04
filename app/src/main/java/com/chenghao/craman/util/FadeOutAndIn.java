package com.chenghao.craman.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

/**
 * Created by Hao on 16/7/2.
 */
public class FadeOutAndIn {
    private View view;
    private ObjectAnimator fadeOut;
    private AnimatorSet animatorSet;

    public FadeOutAndIn(View view) {
        this.view = view;

        ObjectAnimator moveOut = ObjectAnimator.ofFloat(view, "translationX", 0f, 100f);
        moveOut.setInterpolator(new AccelerateDecelerateInterpolator());

        fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);

        ObjectAnimator moveIn = ObjectAnimator.ofFloat(view, "translationX", 100f, 0f);
        moveOut.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);

        animatorSet = new AnimatorSet();
        animatorSet.play(moveOut).with(fadeOut);
        animatorSet.play(moveIn).with(fadeIn).after(moveOut);
    }

    public void start() {
        animatorSet.start();
    }

    public void setText(final String text) {
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ((TextView) view).setText(text);
            }
        });
    }
}
