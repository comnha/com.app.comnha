package com.app.ptt.comnha.SingletonClasses;

import com.app.ptt.comnha.Models.FireBase.Store;

/**
 * Created by PTT on 10/5/2016.
 */
public class ChooseStore {
    private static ChooseStore ourInstance;

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;

    }

    private ChooseStore() {
    }

    Class aClass = getClass();

    private Store store;

    public static ChooseStore getInstance() {
        if (ourInstance == null) {
            ourInstance = new ChooseStore();
        }

        return ourInstance;
    }


}
