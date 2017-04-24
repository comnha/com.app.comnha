package com.app.ptt.comnha.Classes;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.app.ptt.comnha.R;

/**
 * Created by PTT on 11/1/2016.
 */

public class AnimationUtils {
    private static AnimationUtils instance = null;

    public AnimationUtils() {
    }

    public static AnimationUtils getInstance() {
        if (instance == null) {
            instance = new AnimationUtils();
        }
        return instance;
    }

    public static void animateItemRcylerV(RecyclerView.ViewHolder holder, boolean goesDown) {
        ObjectAnimator animatorTraslateY = ObjectAnimator.ofFloat(holder.itemView,
                "translationY", goesDown == true ? -500 : 500, 0);
        animatorTraslateY.setDuration(500);
        animatorTraslateY.start();
    }

    public static void animatbtnRefreshIfChange(View view) {
        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(view,
                "translationY", -1000, 0);
        animatorTranslateY.setDuration(700);
        animatorTranslateY.start();
    }

    public static void animatbtnRefreshIfClick(View view) {
        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(view,
                "translationY", 0, -1000);
        animatorTranslateY.setDuration(1000);
        animatorTranslateY.start();
    }

    public static void animatfabMenuIn(View view) {
        ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(view,
                "translationX", 600, 0);
        animatorTranslateX.setDuration(1000);
        animatorTranslateX.start();
    }

    public static void animatfabMenuOut(View view) {
        ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(view,
                "translationX", 0, 600);
        animatorTranslateX.setDuration(1000);
        animatorTranslateX.start();
    }

    public static void animatShowTagMap(View view1, View view2) {
        final ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(view1, "translationX", -1050, 0),
                animatorTranslateX1 = ObjectAnimator.ofFloat(view2, "translationX", -1050, 0),
                animatorTranslateY = ObjectAnimator.ofFloat(view2, "translationY", 120, 0);
        animatorTranslateX.setDuration(500);
        animatorTranslateX1.setDuration(500);
        animatorTranslateY.setDuration(180);
        animatorTranslateX.start();
        animatorTranslateX1.start();
        animatorTranslateX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animatorTranslateY.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public static void animatHideTagMap(View view1, View view2) {
        final ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(view1, "translationX", 0, -1050),
                animatorTranslateY = ObjectAnimator.ofFloat(view2, "translationY", 0, 120),
                animatorTranslateX1 = ObjectAnimator.ofFloat(view2, "translationX", 0, -1050);
        animatorTranslateX.setDuration(500);
        animatorTranslateX1.setDuration(500);
        animatorTranslateY.setDuration(180);
        animatorTranslateY.start();
        animatorTranslateY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animatorTranslateX.start();
                animatorTranslateX1.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public static void animatShowTagMap2(View view1) {
        ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(view1, "translationX", -1050, 0);
        animatorTranslateX.setDuration(500);
        animatorTranslateX.start();
    }

    public static void animatHideTagMap2(View view1) {
        ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(view1, "translationX", 0, -1050);
        animatorTranslateX.setDuration(500);
        animatorTranslateX.start();
    }

    public static void animatMoveListForward(View proView, View disView) {
        ObjectAnimator animatorTranslateXDisList = ObjectAnimator.ofFloat(disView, "translationX", 1200, 0);
        ObjectAnimator animatorTranslateXProList = ObjectAnimator.ofFloat(proView, "translationX", 0, -1200);
        animatorTranslateXDisList.setDuration(400);
        animatorTranslateXProList.setDuration(400);
        animatorTranslateXProList.start();
        animatorTranslateXDisList.start();
    }

    public static void animatMoveListForback(View proView, View disView) {
        ObjectAnimator animatorTranslateXDisList = ObjectAnimator.ofFloat(disView, "translationX", 0, 1200);
        ObjectAnimator animatorTranslateXProList = ObjectAnimator.ofFloat(proView, "translationX", -1200, 0);
        animatorTranslateXDisList.setDuration(400);
        animatorTranslateXProList.setDuration(400);
        animatorTranslateXProList.start();
        animatorTranslateXDisList.start();
    }

    public static void animateTransAlpha(View view1) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view1, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view1, "scaleY", 0f, 1f);
        scaleX.setDuration(2000);
        scaleY.setDuration(2000);
        scaleY.start();
        scaleX.start();

    }

    public void animateTransTrip(View view1, View view2, View view3, View view4, View view5, View view6) {
        ObjectAnimator traslateX1 = ObjectAnimator.ofFloat(view1, "translationX", -630, 400, 1260);
        ObjectAnimator traslateX2 = ObjectAnimator.ofFloat(view2, "translationX", -630, 370, 1260);
        ObjectAnimator traslateX3 = ObjectAnimator.ofFloat(view3, "translationX", -630, 330, 1260);
        ObjectAnimator traslateX4 = ObjectAnimator.ofFloat(view4, "translationX", -630, 280, 1260);
        ObjectAnimator traslateX5 = ObjectAnimator.ofFloat(view5, "translationX", -630, 220, 1260);
        ObjectAnimator traslateX6 = ObjectAnimator.ofFloat(view6, "translationX", -630, 150, 1260);
        traslateX1.setDuration(2500);
        traslateX1.setStartDelay(100);

        traslateX2.setDuration(2500);
        traslateX2.setStartDelay(200);

        traslateX3.setDuration(2500);
        traslateX3.setStartDelay(300);

        traslateX4.setDuration(2500);
        traslateX4.setStartDelay(400);

        traslateX5.setDuration(2500);
        traslateX5.setStartDelay(500);

        traslateX6.setDuration(2500);
        traslateX6.setStartDelay(600);

        traslateX1.setRepeatCount(ValueAnimator.INFINITE);
        traslateX2.setRepeatCount(ValueAnimator.INFINITE);
        traslateX3.setRepeatCount(ValueAnimator.INFINITE);
        traslateX4.setRepeatCount(ValueAnimator.INFINITE);
        traslateX5.setRepeatCount(ValueAnimator.INFINITE);
        traslateX6.setRepeatCount(ValueAnimator.INFINITE);
        traslateX1.start();
        traslateX2.start();
        traslateX3.start();
        traslateX4.start();
        traslateX5.start();
        traslateX6.start();
    }

    public static void rotate90postoption(View view) {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", 0, 180);
        rotate.setDuration(300);
        rotate.start();
    }

    public static void rotate90postoptionBack(View view) {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", 180, 0);
        rotate.setDuration(300);
        rotate.start();
    }

    public void createCircularReveal(View view, long duration, int cx, int cy) {
        int w = view.getWidth(), h = view.getHeight();
        int endRadius = Math.max(w, h);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Animator animator = ViewAnimationUtils.createCircularReveal(view,
                    cx, cy, 0, endRadius);
            animator.setDuration(duration);
            animator.start();

        }
    }

    public View animateShowNotify(View firstview, long duration) {
        firstview.findViewById(R.id.imgV_news_tabs_main)
                .setBackgroundResource(R.drawable.ic_notify_new_yellow_18dp);
        View secondview = firstview.findViewById(R.id.imgV_news_tabs_main);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(secondview, "scaleX", 0, 1),
                scaleY = ObjectAnimator.ofFloat(secondview, "scaleY", 0, 1);
        scaleX.setDuration(duration);
        scaleY.setDuration(duration);
        scaleX.start();
        scaleY.start();
        return firstview;
    }

    public void animateHideNotify(final View firstview, long duration) {
        View secondview = firstview.findViewById(R.id.imgV_news_tabs_main);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(secondview, "scaleX", 1, 0),
                scaleY = ObjectAnimator.ofFloat(secondview, "scaleY", 1, 0);
        scaleX.setDuration(duration);
        scaleY.setDuration(duration);
        scaleX.start();
        scaleY.start();
        scaleX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                firstview.findViewById(R.id.imgV_news_tabs_main)
                        .setBackgroundResource(android.R.color.transparent);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    AnimationNotifyListener animationNotifyListener;

    public void setAnimationNotifyListener(AnimationNotifyListener animationNotifyListener) {
        this.animationNotifyListener = animationNotifyListener;
    }

    public interface AnimationNotifyListener {
        void Hiden(boolean b);
    }
}
