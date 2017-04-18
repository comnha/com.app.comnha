package com.app.ptt.comnha.Models.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 4/7/2017.
 */

public class NewpostNotify {
    String id, postID, title, date, userID, un, district_province;
    boolean readstate = false;
    //phép kết
    String readState_pro_dist;

    public NewpostNotify() {
    }

    public NewpostNotify(String postID, String title, String date, String userID, String un,
                         String district_province, boolean readstate, String readState_pro_dist) {
        this.postID = postID;
        this.title = title;
        this.date = date;
        this.userID = userID;
        this.un = un;
        this.district_province = district_province;
        this.readstate = readstate;
        this.readState_pro_dist = readState_pro_dist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUn() {
        return un;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public String getDistrict_province() {
        return district_province;
    }

    public void setDistrict_province(String district_province) {
        this.district_province = district_province;
    }

    public boolean isReadstate() {
        return readstate;
    }

    public void setReadstate(boolean readstate) {
        this.readstate = readstate;
    }

    public String getReadState_pro_dist() {
        return readState_pro_dist;
    }

    public void setReadState_pro_dist(String readState_pro_dist) {
        this.readState_pro_dist = readState_pro_dist;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("postID", postID);
        result.put("title", title);
        result.put("date", date);
        result.put("userID", userID);
        result.put("un", un);
        result.put("readstate", readstate);
        result.put("district_province", district_province);
        result.put("readState_pro_dist", readState_pro_dist);
        return result;
    }
}
