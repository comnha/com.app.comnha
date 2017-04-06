package com.app.ptt.comnha.FireBase;

import org.json.simple.JSONAware;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 9/26/2016.
 */

public class MyLocation implements JSONAware,Serializable {
    String name, diachi, sdt, timestart, timeend, tinhtp, quanhuyen;
    String khoangcach;

    public boolean getIsReported() {
        return isReported;
    }

    public void setReported(boolean reported) {
        isReported = reported;
    }

    boolean isReported;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    String userId;
    String time;
    String date;
    String index;

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    Boolean visible;
    public String getKhoangcach() {
        return khoangcach;
    }

    public void setKhoangcach(String khoangcach) {
        this.khoangcach = khoangcach;
    }


    public String getTinhtp() {
        return tinhtp;
    }

    public String getQuanhuyen() {
        return quanhuyen;
    }

    public void setTinhtp(String tinhtp) {

        this.tinhtp = tinhtp;
    }

    public void setQuanhuyen(String quanhuyen) {
        this.quanhuyen = quanhuyen;
    }

    double lat, lng;
    long giamin, giamax;
    String locaID;
    long giaTong = 0, vsTong = 0, pvTong = 0;
    long size = 0;
    long giaAVG, vsAVG, pvAVG, tongAVG;

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    String reporter;

    public String getHinh() {
        return hinh;
    }

    public void setHinh(String hinh) {
        this.hinh = hinh;
    }

    String hinh;
    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {

        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setGiaAVG(long giaAVG) {
        this.giaAVG = giaAVG;
    }

    public void setVsAVG(long vsAVG) {
        this.vsAVG = vsAVG;
    }

    public void setPvAVG(long pvAVG) {
        this.pvAVG = pvAVG;
    }

    public void setTongAVG(long tongAVG) {
        this.tongAVG = tongAVG;
    }

    public long getGiaAVG() {
        return giaAVG;
    }

    public long getVsAVG() {
        return vsAVG;
    }

    public long getPvAVG() {
        return pvAVG;
    }

    public long getTongAVG() {
        return tongAVG;
    }

    public MyLocation() {
    }

    public void setLocaID(String locaID) {
        this.locaID = locaID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public void setTimestart(String timestart) {
        this.timestart = timestart;
    }

    public void setTimeend(String timeend) {
        this.timeend = timeend;
    }

    public void setGiamin(long giamin) {
        this.giamin = giamin;
    }

    public void setGiamax(long giamax) {
        this.giamax = giamax;
    }

    public String getName() {
        return name;
    }

    public String getDiachi() {
        return diachi;
    }

    public String getSdt() {
        return sdt;
    }

    public String getTimestart() {
        return timestart;
    }

    public String getTimeend() {
        return timeend;
    }

    public long getGiamin() {
        return giamin;
    }

    public long getGiamax() {
        return giamax;
    }

    public String getLocaID() {
        return locaID;
    }

    public void setGiaTong(long giaTong) {
        this.giaTong = giaTong;
    }

    public void setVsTong(long vsTong) {
        this.vsTong = vsTong;
    }

    public void setPvTong(long pvTong) {
        this.pvTong = pvTong;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public long getPvTong() {
        return pvTong;
    }

    public long getVsTong() {
        return vsTong;
    }

    public long getGiaTong() {
        return giaTong;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("diachi", diachi);
        result.put("sdt", sdt);
        result.put("timeend", timeend);
        result.put("timestart", timestart);
        result.put("giamax", giamax);
        result.put("giamin", giamin);
        result.put("giaTong", giaTong);
        result.put("vsTong", vsTong);
        result.put("pvTong", pvTong);
        result.put("size", size);
        result.put("tinhtp", tinhtp);
        result.put("quanhuyen", quanhuyen);
        result.put("lat", lat);
        result.put("lng", lng);
        result.put("userId", userId);
        result.put("time",time);
        result.put("date",date);
        result.put("index",index);
        result.put("visible",visible);
        result.put("hinh",hinh);
        result.put("isreported",isReported);
        result.put("reporter",reporter);
        return result;
    }

    @Override
    public String toJSONString() {
        StringBuffer sb=new StringBuffer();
        sb.append("{");//Bat dau doi tuong json
        sb.append("\"id\":\""+getLocaID()+"\"");
        sb.append("\"id\":\""+getLocaID()+"\"");
        return null;
    }
}
