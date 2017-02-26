package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 10/29/2016.
 */

public class Food {
    String tenmon;
    String monID;
    String locaID;
    float danhGia;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    String index;

    String userID;
    long gia;
    boolean visible;
    public float getDanhGia() {
        return danhGia;
    }

    public void setDanhGia(float danhGia) {
        this.danhGia = danhGia;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }



    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }



    public void setLocaID(String locaID) {
        this.locaID = locaID;
    }



    public String getLocaID() {

        return locaID;
    }


    public void setMonID(String monID) {
        this.monID = monID;
    }

    public String getMonID() {

        return monID;
    }

    public String getTenmon() {
        return tenmon;
    }

    public long getGia() {
        return gia;
    }

    public void setTenmon(String tenmon) {
        this.tenmon = tenmon;
    }

    public void setGia(long gia) {
        this.gia = gia;
    }

    public Food() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("tenmon", tenmon);
        result.put("gia", gia);
        result.put("locaID", locaID);
        result.put("visible",visible);
        result.put("userID",userID);
        result.put("danhGia",danhGia);
        result.put("index",index);
        return result;
    }
}
