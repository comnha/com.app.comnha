package com.app.ptt.comnha.SingletonClasses;

import com.app.ptt.comnha.Models.FireBase.Image;

import java.util.ArrayList;

/**
 * Created by PTT on 10/5/2016.
 */
public class OpenAlbum {
    private static OpenAlbum ourInstance;
    private ArrayList<Image> images;
    String postID;

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostID() {
        return postID;
    }

    public static OpenAlbum getInstance() {
        if (ourInstance == null) {
            ourInstance = new OpenAlbum();
        }
        return ourInstance;
    }

    public ArrayList<Image> getImage() {
        return images;
    }

    public void setImage(ArrayList<Image> image) {

        this.images = image;
    }

    private OpenAlbum() {
    }
}
