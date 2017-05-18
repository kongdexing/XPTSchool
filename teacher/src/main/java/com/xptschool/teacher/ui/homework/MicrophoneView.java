// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.xptschool.teacher.ui.homework;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.xptschool.teacher.R;

public class MicrophoneView extends AudioInputRelativeLayout {
    private static final int OPENING_TIME_MS = 218;
    private static final int RED_MIC_FADE_IN_TIME = 536;
    private static final String TAG = MicrophoneView.class.getSimpleName();
    private ImageView grayCircle;
    private ImageView microphone;
    private Animation pulseAnim;
    private ImageView pulsingCircle;
    private ImageView redCircle;

    public MicrophoneView(Context context) {
        super(context);
    }

    public MicrophoneView(Context context, AttributeSet attributeset) {
        super(context, attributeset);
    }

    public MicrophoneView(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
    }

    private void setupPulsingAnimation() {
        pulseAnim = AnimationUtils.loadAnimation(getContext(),
                R.anim.pulsing_circle_anim);

        pulseAnim
                .setAnimationListener(new Animation.AnimationListener() {

                    public void onAnimationEnd(Animation animation) {
                        pulsingCircle.setAlpha(0.0F);
                        startPulsingAnimation();
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationStart(Animation animation) {
                        pulsingCircle.setAlpha(1.0F);
                    }
                });
    }

    private void startFadeInAnimation(View view, int i) {
        view.setAlpha(0.0F);
        view.animate().alpha(1.0F).setDuration(i).start();
    }

    private void startFadeOutAnimation(View view, int i) {
        view.setAlpha(1.0F);
        view.animate().alpha(0.0F).setDuration(i).start();
    }

    private void startOpeningAnimation() {
        startFadeInAnimation(microphone, OPENING_TIME_MS);
        startFadeInAnimation(grayCircle, OPENING_TIME_MS);
    }

    private void startPulsingAnimation() {
        //Log.i(TAG, "start startPulsingAnimation");
        if (pulsingCircle.getVisibility() == View.GONE) {
            return;
        } else {
            pulsingCircle.setAnimation(pulseAnim);
            pulseAnim.start();
            return;
        }
    }

    private void startRedCircleAnimation() {
        redCircle.setVisibility(VISIBLE);
        startFadeOutAnimation(grayCircle, RED_MIC_FADE_IN_TIME);
        startFadeInAnimation(redCircle, RED_MIC_FADE_IN_TIME);
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        Log.i(TAG, "on finish inflate");
        microphone = (ImageView) findViewById(R.id.microphone);
        redCircle = (ImageView) findViewById(R.id.red_circle);
        grayCircle = (ImageView) findViewById(R.id.gray_circle);
        pulsingCircle = (ImageView) findViewById(R.id.pulsing_circle);
        setupPulsingAnimation();
    }

    @Override
    protected void onStateChanged() {
        if (microphone == null) {
            Log.d(TAG, "microphone is null");
        }

        switch (cls.SwitchMap[getState().ordinal()]) {
            default:
                return;
            case 1:
                microphone.setVisibility(View.GONE);
                grayCircle.setVisibility(View.GONE);
                redCircle.setVisibility(View.GONE);
                pulsingCircle.setVisibility(View.GONE);
                return;
            case 2:
                microphone.setVisibility(View.VISIBLE);
                microphone.setImageResource(R.drawable.icon_voice_input);
                grayCircle.setVisibility(View.VISIBLE);
                pulsingCircle.setVisibility(View.VISIBLE);
                startPulsingAnimation();
                startOpeningAnimation();
                return;
            case 3:
                microphone.setVisibility(View.VISIBLE);
                grayCircle.setVisibility(View.VISIBLE);
                pulsingCircle.setVisibility(View.VISIBLE);
                startRedCircleAnimation();
                return;
            case 4:
                microphone.setVisibility(View.GONE);
                redCircle.setVisibility(View.GONE);
                grayCircle.setVisibility(View.GONE);
                pulsingCircle.setVisibility(View.GONE);
                if (pulseAnim != null) {
                    pulseAnim.cancel();
                    pulseAnim.reset();
                    return;
                }
                return;
            case 5:
                microphone.setVisibility(View.VISIBLE);
                microphone.setImageResource(R.drawable.icon_voice_input_b);
                grayCircle.setVisibility(View.VISIBLE);
                pulsingCircle.setVisibility(View.VISIBLE);
                return;
        }
    }

    static class cls {
        static final int SwitchMap[];

        static {
            SwitchMap = new int[AudioInputRelativeLayout.State.values().length];
            try {
                SwitchMap[AudioInputRelativeLayout.State.MIC_INITIALIZING
                        .ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror) {
            }
            try {
                SwitchMap[AudioInputRelativeLayout.State.LISTENING.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror1) {
            }
            try {
                SwitchMap[AudioInputRelativeLayout.State.RECORDING.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror2) {
            }
            try {
                SwitchMap[AudioInputRelativeLayout.State.NOT_LISTENING
                        .ordinal()] = 4;
            } catch (NoSuchFieldError nosuchfielderror3) {
            }
            try {
                SwitchMap[AudioInputRelativeLayout.State.RECOGNIZING.ordinal()] = 5;
            } catch (NoSuchFieldError nosuchfielderror4) {
            }
        }
    }
}
