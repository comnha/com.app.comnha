package com.app.ptt.comnha.Interfaces;

import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.Models.Search;

/**
 * Created by PTT on 9/26/2016.
 */

public interface Transactions {
    void setupFirebase();

    boolean createNew();

    void update();

    void delete();

    void disableUser(User user,boolean status);

    void changeRole(User user);
    void changeUserPermission(User user);
    void onSearchItemClick(Search search);

}
