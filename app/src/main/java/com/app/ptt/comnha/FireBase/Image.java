package com.app.ptt.comnha.FireBase;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 11/8/2016.
 */

public class Image {
    String name;
    String postID;
    String locaID;
    String userID;
    public String getImage() {
        return image;
    }

    public Uri getPath() {
        return path;
    }

    public void setPath(Uri path) {
        this.path = path;
    }

    Uri path;
    public void setImage(String image) {
        this.image = image;
    }

    String image;
    String imageID;
    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getImageID() {

        return imageID;
    }

    public String getName() {
        return name;
    }

    public String getPostID() {
        return postID;
    }

    public String getLocaID() {
        return locaID;
    }

    public String getUserID() {
        return userID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public void setLocaID(String locaID) {
        this.locaID = locaID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Image() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("postID", postID);
        result.put("locaID", locaID);
        result.put("userID", userID);
        result.put("image",image);
        return result;
    }
}
