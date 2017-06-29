package com.app.ptt.comnha.Interfaces;

import com.app.ptt.comnha.Models.FireBase.User;

/**
 * Created by PTT on 9/26/2016.
 */

public interface Transactions {
    void setupFirebase();

    boolean createNew();

    void update();

    void delete();

    void deleteUser(User user);

    void changeRole(User user);
}
