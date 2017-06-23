package com.app.ptt.comnha.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;

/**
 * Created by ADMIN on 6/15/2017.
 */

public class CustomDialog extends Dialog {
    private boolean isOne = true;
    OnPositiveListener onPositiveListener;
    OnNegativeListener onNegativeListener;
    String content;

    public interface OnPositiveListener {
        void onClick();
    }

    public interface OnNegativeListener {
        void onClick();
    }

    public void setOnPositiveListener(OnPositiveListener onPositiveListener) {
        this.onPositiveListener = onPositiveListener;
    }

    public void setOnNegativeListener(OnNegativeListener onNegativeListener) {
        this.onNegativeListener = onNegativeListener;
    }

    public CustomDialog(@NonNull Context context, @StyleRes int themeResId, String content, boolean isOne) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.isOne = isOne;
        this.content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isOne) {

        } else {

        }
    }

    private void initOne() {

    }

    private void initTwo() {

    }
}
