package com.app.ptt.comnha.Models.FireBase;

import com.app.ptt.comnha.Modules.Times;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 4/7/2017.
 */

public class ReportfoodNotify {
    String id, foodID, name, date, time, userID, un,
            contents, district_province;
    boolean readstate = false;
    //phép kết
    String readState_pro_dist;

    public ReportfoodNotify() {
    }

    public ReportfoodNotify(String name, String foodID,
                            String userID, String un,
                            String contents, String district_province) {
        this.name = name;
        this.foodID = foodID;
        this.date = new Times().getDate();
        this.time = new Times().getTimeNoSecond();
        this.userID = userID;
        this.un = un;
        this.contents = contents;
        this.district_province = district_province;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("foodID", foodID);
        result.put("name", name);
        result.put("date", date);
        result.put("time", time);
        result.put("userID", userID);
        result.put("un", un);
        result.put("contents", contents);
        result.put("readstate", readstate);
        result.put("district_province", district_province);
        result.put("readState_pro_dist", false + "_" + district_province);
        return result;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
