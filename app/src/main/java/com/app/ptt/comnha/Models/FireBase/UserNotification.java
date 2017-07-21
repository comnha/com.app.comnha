package com.app.ptt.comnha.Models.FireBase;

import com.app.ptt.comnha.Const.Const;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cuong on 12/22/2016.
 */


public class UserNotification {


    public UserNotification(){
        SimpleDateFormat sdf = new SimpleDateFormat(Const.DATE_TIME_FORMAT);
        String currentDateandTime = sdf.format(new Date());
        date = currentDateandTime;
    }
    private String storeID;
    private String userOwnID;
    private String postID;
    private String foodId;
    private String userEffectId;
    private boolean ơwner;
    private String userEffectName;
    private String foodName;
    private boolean readed;
    private String Id;

    public boolean isShown() {
        return isShown;
    }

    public void setShown(boolean shown) {
        isShown = shown;
    }

    private boolean isShown;
    //type =1 new store added
    //type=2 new post added
    //type=3 comment
    //type=4 new food add


    //status
    //0: notify to other people
    //1: accept
    //-1: reject
    //3: was reported
    //2: reject report
    //-2: accept report
    private int type,status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }


    private String reportId;
    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public boolean isƠwner() {
        return ơwner;
    }

    public void setƠwner(boolean ơwner) {
        this.ơwner = ơwner;
    }

    public String getUserEffectName() {
        return userEffectName;
    }

    public void setUserEffectName(String userEffectName) {
        this.userEffectName = userEffectName;
    }

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
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
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
        result.put("type", type);
        result.put("userEffectId", userEffectId);
        result.put("readed", readed);
        result.put("ơwner", ơwner);
        result.put("userEffectName", userEffectName);
        result.put("foodName",foodName);

        result.put("date", date);
        result.put("isShown",isShown);
        result.put("reportId",reportId);
        result.put("status",status);
        return result;
    }

}
