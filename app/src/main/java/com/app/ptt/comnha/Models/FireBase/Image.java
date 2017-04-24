package com.app.ptt.comnha.Models.FireBase;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 11/8/2016.
 */

public class Image {
    //info
    String imageID,
            name,
            caption = "",
            date,
            time;
    //creator
    String userID;//tìm theo tài khoản

    //type of image
    int type = 4;//banner of post, profile hoặc images of post (1:banner, 2:profile, 3:post's image, 4:normal image)
    String postID = "",//tìm theo post
            storeID = "";//tìm theo quán ăn
    String type_imageID;//Phân loại theo banner, profile, images of post
    Uri path;

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("caption", caption);
        result.put("date", date);
        result.put("time", time);
        result.put("type", type);
        result.put("postID", postID);
        result.put("storeID", storeID);
        result.put("storeID", storeID);
        result.put("type_imageID", type_imageID);
        return result;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getType_imageID() {
        return type_imageID;
    }

    public void setType_imageID(String type_imageID) {
        this.type_imageID = type_imageID;
    }

    public Uri getPath() {
        return path;
    }

    public void setPath(Uri path) {
        this.path = path;
    }
}
