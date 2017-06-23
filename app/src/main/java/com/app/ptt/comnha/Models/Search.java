package com.app.ptt.comnha.Models;

import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Store;

/**
 * Created by ciqaz on 22/06/2017.
 */

public class Search {
    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private Store store;
    private Food food;
    private int type;
}
