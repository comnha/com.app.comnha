package com.app.ptt.comnha.SingletonClasses;

import com.app.ptt.comnha.FireBase.Store;

/**
 * Created by PTT on 10/5/2016.
 */
public class ChooseLoca {
    private static ChooseLoca ourInstance;

    public Store getLocation() {
        return location;
    }

    public void setLocation(Store location) {
        this.location = location;
    }

    private Store location;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    private String info;

    public static ChooseLoca getInstance() {
        if (ourInstance == null) {
            ourInstance = new ChooseLoca();
        }
        return ourInstance;
    }

    private ChooseLoca() {
    }

}
