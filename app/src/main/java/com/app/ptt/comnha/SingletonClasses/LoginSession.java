package com.app.ptt.comnha.SingletonClasses;

import com.app.ptt.comnha.Models.FireBase.User;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by PTT on 9/27/2016.
 */
public class LoginSession {
    private static LoginSession ourInstance;
    private User user;
    private FirebaseUser firebUser;

    public FirebaseUser getFirebUser() {
        return firebUser;
    }

    public void setFirebUser(FirebaseUser firebUser) {
        this.firebUser = firebUser;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static LoginSession getInstance() {
        if (ourInstance == null) {
            ourInstance = new LoginSession();
        }
        return ourInstance;
    }

}
