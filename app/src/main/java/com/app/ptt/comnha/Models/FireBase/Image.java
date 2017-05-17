package com.app.ptt.comnha.Models.FireBase;

import android.graphics.Bitmap;
import android.net.Uri;

import com.app.ptt.comnha.Utils.Times;

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
    int type = 4;//banner of post, profile hoặc images of post
    // (1:banner, 2:profile, 3:post's image, 4:normal image)
    String postID = "",//tìm theo post
            storeID = "";//tìm theo quán ăn
    String type_uID;//Phân loại theo banner, profile, images of user
    Uri path;
    boolean isHidden = false;
    String isHidden_postID, isHidden_storeID, isHidden_uID;
    Bitmap imgBitmap;

    public Image() {
    }

    public Image(String name, String userID,
                 int type, String postID, String storeID) {
        this.name = name;
        this.date = new Times().getDate();
        this.time = new Times().getTime();
        this.userID = userID;
        this.type = type;
        this.postID = postID;
        this.storeID = storeID;
        this.type_uID = this.type + "_" + userID;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("caption", caption);
        result.put("date", date);
        result.put("time", time);
        result.put("type", type);
        result.put("userID", userID);
        result.put("postID", postID);
        result.put("storeID", storeID);
        result.put("storeID", storeID);
        result.put("type_uID", type_uID);
        result.put("isHidden", isHidden);
        result.put("isHidden_postID", isHidden + "_" + postID);
        result.put("isHidden_storeID", isHidden + "_" + storeID);
        result.put("isHidden_uID", isHidden + "_" + userID);

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

    public String getType_uID() {
        return type_uID;
    }

    public void setType_uID(String type_uID) {
        this.type_uID = type_uID;
    }

    public Uri getPath() {
        return path;
    }

    public void setPath(Uri path) {
        this.path = path;
    }

    public Bitmap getImgBitmap() {
        return imgBitmap;
    }

    public void setImgBitmap(Bitmap imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
}
