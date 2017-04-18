package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 4/7/2017.
 */

public class ReportpostNotify {
    String id, postID, title, date, time, userID, un,
            contents, district_province;
    boolean readstate = false;
    //phép kết
    String readState_pro_dist;

    public ReportpostNotify() {
    }

    public ReportpostNotify(String postID, String title, String date, String time, String userID,
                            String un, String contents, String district_province,
                            boolean readstate, String readState_pro_dist) {
        this.postID = postID;
        this.title = title;
        this.date = date;
        this.time = time;
        this.userID = userID;
        this.un = un;
        this.contents = contents;
        this.district_province = district_province;
        this.readstate = readstate;
        this.readState_pro_dist = readState_pro_dist;
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

    public String getUn() {
        return un;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
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
        result.put("time", time);
        result.put("userID", userID);
        result.put("un", un);
        result.put("readstate", readstate);
        result.put("district_province", district_province);
        result.put("readState_pro_dist", readState_pro_dist);
        return result;
    }
}
