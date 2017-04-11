package com.app.ptt.comnha.SingletonClasses;

import com.app.ptt.comnha.FireBase.Store;

/**
 * Created by PTT on 10/5/2016.
 */
public class EditLocal {
    private static EditLocal ourInstance;
    private Store store;

    public static EditLocal getInstance() {
        if (ourInstance == null) {
            ourInstance = new EditLocal();
        }
        return ourInstance;
    }

    private EditLocal() {
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Store getStore() {

        return store;
    }
}
