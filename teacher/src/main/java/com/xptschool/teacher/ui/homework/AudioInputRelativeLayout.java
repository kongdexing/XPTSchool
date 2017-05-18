package com.xptschool.teacher.ui.homework;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


public abstract class AudioInputRelativeLayout extends RelativeLayout {
    private State state;

    public AudioInputRelativeLayout(Context context) {
        super(context);
    }

    public AudioInputRelativeLayout(Context context, AttributeSet attributeset) {
        super(context, attributeset);
    }

    public AudioInputRelativeLayout(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
    }

    public State getState() {
        return state;
    }

    protected abstract void onStateChanged();

    //显示监听
    public void showListening() {
        state = State.LISTENING;
        onStateChanged();
    }

    //显示不监听
    public void showNotListening() {
        state = State.NOT_LISTENING;
        onStateChanged();
    }

    //显示确认
    public void showRecognizing() {
        state = State.RECOGNIZING;
        onStateChanged();
    }

    //显示录音
    public void showRecording() {
        state = State.RECORDING;
        onStateChanged();
    }

    public enum State {
        NOT_LISTENING, MIC_INITIALIZING, LISTENING, RECORDING, RECOGNIZING    //未侦听，麦克初始化，监听，录音，确认
    }
}