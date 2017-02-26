package com.app.ptt.comnha.SingletonClasses;

import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.FireBase.Post;

/**
 * Created by PTT on 10/5/2016.
 */
public class EditLocal {
    private static EditLocal ourInstance;
    private MyLocation myLocation;

    public static EditLocal getInstance() {
        if (ourInstance == null) {
            ourInstance = new EditLocal();
        }
        return ourInstance;
    }

    private EditLocal() {
    }

    public void setMyLocation(MyLocation myLocation) {
        this.myLocation = myLocation;
    }

    public MyLocation getMyLocation() {

        return myLocation;
    }
}
