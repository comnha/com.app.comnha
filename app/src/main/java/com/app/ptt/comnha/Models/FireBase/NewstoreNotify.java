package com.app.ptt.comnha.Models.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 4/7/2017.
 */

public class NewstoreNotify {
    String id, storeID, name, address, date, userID, un, district, provinve;
    boolean readstate = false;
    String readState_pro_dist;

    public NewstoreNotify() {
    }

    public NewstoreNotify(String storeID, String name, String address, String date,
                          String userID, String un, String district, String provinve) {
        this.storeID = storeID;
        this.name = name;
        this.address = address;
        this.date = date;
        this.userID = userID;
        this.un = un;
        this.district = district;
        this.provinve = provinve;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("storeID", storeID);
        result.put("name", name);
        result.put("address", address);
        result.put("date", date);
        result.put("userID", userID);
        result.put("un", un);
        result.put("readstate", readstate);
        result.put("district_province", district + "_" + provinve);
        result.put("readState_pro_dist", String.valueOf(readstate) + "_" + district + "_" + provinve);
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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