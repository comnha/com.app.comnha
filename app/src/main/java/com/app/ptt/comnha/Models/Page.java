package com.app.ptt.comnha.Models;

/**
 * Created by PTT on 5/20/2017.
 */

public class Page {
    private int mLayoutResId;
    private String title;

    public Page(int mLayoutResId, String title) {
        this.mLayoutResId = mLayoutResId;
        this.title = title;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

    public String getTitle() {
        return title;
    }
}
