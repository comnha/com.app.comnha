package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 4/7/2017.
 */

public class ItemNotifyNewpost {
    String id, postid, title, date, iduser, un, district_province;
    boolean readstate;

    public ItemNotifyNewpost() {
    }

    public ItemNotifyNewpost(String postid, String title, String date, String iduser,
                             String un, String district_province, boolean readstate) {
        this.postid = postid;
        this.title = title;
        this.date = date;
        this.iduser = iduser;
        this.un = un;
        this.district_province = district_province;
        this.readstate = readstate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
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

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("postid", postid);
        result.put("title", title);
        result.put("date", date);
        result.put("iduser", iduser);
        result.put("un", un);
        result.put("readstate", readstate);
        result.put("district_province", district_province);
        return result;
    }
}
