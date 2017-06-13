package com.app.ptt.comnha.SingletonClasses;

import com.app.ptt.comnha.Models.FireBase.Image;

/**
 * Created by PTT on 10/5/2016.
 */
public class ChooseImg {
    private static ChooseImg ourInstance;
    private Image image;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public static ChooseImg getInstance() {
        if (ourInstance == null) {
            ourInstance = new ChooseImg();
        }
        return ourInstance;
    }
}
