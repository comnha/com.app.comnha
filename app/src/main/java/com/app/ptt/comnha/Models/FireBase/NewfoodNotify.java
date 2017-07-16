package com.app.ptt.comnha.Models.FireBase;

import com.app.ptt.comnha.Modules.Times;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 4/7/2017.
 */

public class NewfoodNotify {
    String id;
    String foodID;

    String date;
    String userID;
    String un;
    String district_province;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;
    boolean readstate = false;
    //phép kết
    String readState_pro_dist;

    public NewfoodNotify(String id, String foodID, String date, String userID, String un, String district_province, String name) {
        this.id = id;
        this.foodID = foodID;
        this.date = date;
        this.userID = userID;
        this.un = un;
        this.district_province = district_province;
        this.name = name;
        this.readstate = readstate;
        this.readState_pro_dist = readState_pro_dist;
    }

    public NewfoodNotify() {
    }


    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("foodID", foodID);
        result.put("date", date);
        result.put("userID", userID);
        result.put("un", un);
        result.put("readstate", readstate);
        result.put("district_province", district_province);
        result.put("readState_pro_dist",  String.valueOf(readstate) + "_"
                + district_province);
        result.put("name",name);
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


}
