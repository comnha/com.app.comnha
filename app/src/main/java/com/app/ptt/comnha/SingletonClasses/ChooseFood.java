package com.app.ptt.comnha.SingletonClasses;

import com.app.ptt.comnha.FireBase.Food;
import com.app.ptt.comnha.FireBase.MyLocation;

/**
 * Created by PTT on 10/5/2016.
 */
public class ChooseFood {
    private static ChooseFood ourInstance;

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    private Food food;

    public MyLocation getLocation() {
        return location;
    }

    public void setLocation(MyLocation location) {
        this.location = location;
    }

    private MyLocation location;
    public static ChooseFood getInstance() {
        if (ourInstance == null) {
            ourInstance = new ChooseFood();
        }
        return ourInstance;
    }

    private ChooseFood() {
    }

}
