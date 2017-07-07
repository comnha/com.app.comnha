package com.app.ptt.comnha.Models.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cuong on 12/22/2016.
 */

public class UserNotification {
    private String storeID;


    private String userOwnID;
    private String postID;
    private String foodId;



    private String userEffectId;

    private boolean isReaded=false;
    private String Id;
    //type =1 new store added
    //type=2 new post added
    //type=3 comment
    //type=4 new food add
    private int type;

    public String getUserOwnID() {
        return userOwnID;
    }

    public void setUserOwnID(String userOwnID) {
        this.userOwnID = userOwnID;
    }

    public String getUserEffectId() {
        return userEffectId;
    }

    public void setUserEffectId(String userEffectId) {
        this.userEffectId = userEffectId;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String newPostID) {
        this.postID = newPostID;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public boolean isReaded() {
        return isReaded;
    }

    public void setReaded(boolean readed) {
        isReaded = readed;
    }


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }


    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("storeID", storeID);
        result.put("foodId", foodId);
        result.put("postID", postID);
        result.put("type",type);
        result.put("userEffectId", userEffectId);
        result.put("isReaded",isReaded);
        return result;
    }

}
