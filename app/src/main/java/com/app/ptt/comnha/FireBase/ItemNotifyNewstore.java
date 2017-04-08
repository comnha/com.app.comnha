package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 4/7/2017.
 */

public class ItemNotifyNewstore {
    String id, storeid, name, address, date, iduser, un, district_province;
    boolean readstate;

    public ItemNotifyNewstore() {
    }

    public ItemNotifyNewstore(String storeid, String name, String address, String date,
                              String iduser, String un, String district_province, boolean readstate) {
        this.storeid = storeid;
        this.name = name;
        this.address = address;
        this.date = date;
        this.iduser = iduser;
        this.un = un;
        this.district_province = district_province;
        this.readstate = readstate;
    }

    public String getDistrict_province() {
        return district_province;
    }

    public void setDistrict_province(String district_province) {
        this.district_province = district_province;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
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

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
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

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("storeid", storeid);
        result.put("name", name);
        result.put("address", address);
        result.put("date", date);
        result.put("iduser", iduser);
        result.put("un", un);
        result.put("readstate", readstate);
        result.put("district_province", district_province);
        return result;
    }
}
