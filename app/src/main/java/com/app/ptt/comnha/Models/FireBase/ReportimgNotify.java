package com.app.ptt.comnha.Models.FireBase;

import com.app.ptt.comnha.Modules.Times;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 4/7/2017.
 */

public class ReportimgNotify {
    String id, imgID, name, date, time, userID, un,
            contents;
    boolean readstate = false;
    //phép kết

    public ReportimgNotify() {
    }

    public ReportimgNotify(String imgID, String name,
                           String userID, String un,
                           String contents) {
        this.imgID = imgID;
        this.name = name;
        this.date = new Times().getDate();
        this.time = new Times().getTimeNoSecond();
        this.userID = userID;
        this.un = un;
        this.contents = contents;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("imgID", imgID);
        result.put("name", name);
        result.put("date", date);
        result.put("time", time);
        result.put("userID", userID);
        result.put("un", un);
        result.put("contents", contents);
        result.put("readstate", readstate);
        return result;
    }
    public String getImgID() {
        return imgID;
    }

    public void setImgID(String imgID) {
        this.imgID = imgID;
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

    public boolean isReadstate() {
        return readstate;
    }

    public void setReadstate(boolean readstate) {
        this.readstate = readstate;
    }
}
