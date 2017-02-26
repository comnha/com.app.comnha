package com.app.ptt.comnha.SingletonClasses;

import com.app.ptt.comnha.FireBase.Food;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.FireBase.Notification;

/**
 * Created by PTT on 10/5/2016.
 */
public class ChooseNoti {
    private static ChooseNoti ourInstance;


    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    Notification notification;
    public static ChooseNoti getInstance() {
        if (ourInstance == null) {
            ourInstance = new ChooseNoti();
        }
        return ourInstance;
    }

    private ChooseNoti() {
    }

}
