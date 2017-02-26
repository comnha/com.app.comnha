package com.app.ptt.comnha.Interfaces;

/**
 * Created by PTT on 9/26/2016.
 */

public interface Transactions {
    void setupFirebase();

    boolean createNew();

    void update();

    void delete();
}
