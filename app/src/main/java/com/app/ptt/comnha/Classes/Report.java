package com.app.ptt.comnha.Classes;

/**
 * Created by ADMIN on 5/26/2017.
 */

public class Report {
    private boolean isChecked = false;
    private String content = "";

    public Report(String content) {
        this.content = content;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getContent() {
        return content;
    }
}
