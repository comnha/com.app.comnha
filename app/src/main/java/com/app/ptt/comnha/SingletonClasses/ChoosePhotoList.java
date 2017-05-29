package com.app.ptt.comnha.SingletonClasses;

import com.app.ptt.comnha.Models.FireBase.Image;

import java.util.ArrayList;

/**
 * Created by PTT on 10/5/2016.
 */
public class ChoosePhotoList {
    private static ChoosePhotoList ourInstance;
    private ArrayList<Image> images;

    public ArrayList<Image> getImage() {
        return images;
    }

    public void setImage(ArrayList<Image> images) {
        this.images = images;
    }

    public static ChoosePhotoList getInstance() {
        if (ourInstance == null) {
            ourInstance = new ChoosePhotoList();
        }
        return ourInstance;
    }
}
