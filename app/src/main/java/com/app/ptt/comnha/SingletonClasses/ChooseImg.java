package com.app.ptt.comnha.SingletonClasses;

import com.app.ptt.comnha.Models.FireBase.Food;

/**
 * Created by PTT on 10/5/2016.
 */
public class ChooseFood {
    private static ChooseFood ourInstance;
    private Food food;

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public static ChooseFood getInstance() {
        if (ourInstance == null) {
            ourInstance = new ChooseFood();
        }
        return ourInstance;
    }
}
