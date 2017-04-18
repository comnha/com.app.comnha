package com.app.ptt.comnha;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by PTT on 4/16/2017.
 */

public class SystemControl {
    public static void hideSoftKeyboard(Activity activity) {
        View view=activity.getCurrentFocus();
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                view.getWindowToken(), 0);
    }
}
