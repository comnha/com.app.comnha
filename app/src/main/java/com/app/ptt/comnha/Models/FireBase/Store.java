package com.app.ptt.comnha.Models.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 9/26/2016.
 */

public class Store {
    //info
    String storeID;
    String name, address, phonenumb, opentime;
    //location
    String province,
            district;
    double lat, lng;
    //creator
    String userID;//người tạo
    String time;//giờ tạo
    String date;//ngày tạo
    //rating
    long priceSum = 0, healthySum = 0, serviceSum = 0;//điểm tổng giá-vệ sinh-phục vụ
    long size = 0; //số lượng đánh giá
    long priceAVG = 0, healthyAVG = 0, serviceAVG = 0,//điểm trung bình giá-vệ sinh-phục vụ
            rateAVG = 0;
    String storeimg = "";//ảnh đại diện của store
    //phép kết
    String pro_dist,//tìm theo tỉnh_huyện
            userID_pro_dist;//tìm theo user_tỉnh_huyện

    String distance;//khoảng cách hiển thị trên danh sách quán ăn
    Map<String, Comment> comments = null;//comment trong post

    public Store() {
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("address", address);
        result.put("phonenumb", phonenumb);
        result.put("opentime", opentime);
        result.put("province", province);
        result.put("district", district);
        result.put("lat", lat);
        result.put("lng", lng);
        result.put("userID", userID);
        result.put("date", date);
        result.put("time", time);
        result.put("priceSum", priceSum);
        result.put("healthySum", healthySum);
        result.put("serviceSum", serviceSum);
        result.put("size", size);
        result.put("priceAVG", priceAVG);
        result.put("healthyAVG", healthyAVG);
        result.put("serviceAVG", serviceAVG);
        result.put("storeimg", storeimg);
        result.put("pro_dist", district + "_" + province);
        result.put("userID_pro_dist", userID + "_" + district + "_" + province);
        return result;
    }

    public Store(String name, String address, String phonenumb,
                 String opentime, String province,
                 String district, double lat, double lng, String userID,
                 String time, String date, String storeimg) {
        this.name = name;
        this.address = address;
        this.phonenumb = phonenumb;
        this.opentime = opentime;
        this.province = province;
        this.district = district;
        this.lat = lat;
        this.lng = lng;
        this.userID = userID;
        this.time = time;
        this.date = date;
        this.storeimg = storeimg;
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

    public String getPhonenumb() {
        return phonenumb;
    }

    public void setPhonenumb(String phonenumb) {
        this.phonenumb = phonenumb;
    }

    public String getOpentime() {
        return opentime;
    }

    public void setOpentime(String opentime) {
        this.opentime = opentime;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public long getPriceSum() {
        return priceSum;
    }

    public void setPriceSum(long priceSum) {
        this.priceSum = priceSum;
    }

    public long getHealthySum() {
        return healthySum;
    }

    public void setHealthySum(long healthySum) {
        this.healthySum = healthySum;
    }

    public long getServiceSum() {
        return serviceSum;
    }

    public void setServiceSum(long serviceSum) {
        this.serviceSum = serviceSum;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getPriceAVG() {
        return priceAVG;
    }

    public void setPriceAVG(long priceAVG) {
        this.priceAVG = priceAVG;
    }

    public long getHealthyAVG() {
        return healthyAVG;
    }

    public void setHealthyAVG(long healthyAVG) {
        this.healthyAVG = healthyAVG;
    }

    public long getServiceAVG() {
        return serviceAVG;
    }

    public void setServiceAVG(long serviceAVG) {
        this.serviceAVG = serviceAVG;
    }

    public long getRateAVG() {
        return rateAVG;
    }

    public void setRateAVG(long rateAVG) {
        this.rateAVG = rateAVG;
    }

    public String getStoreimg() {
        return storeimg;
    }

    public void setStoreimg(String storeimg) {
        this.storeimg = storeimg;
    }

    public String getPro_dist() {
        return pro_dist;
    }

    public void setPro_dist(String pro_dist) {
        this.pro_dist = pro_dist;
    }

    public String getUserID_pro_dist() {
        return userID_pro_dist;
    }

    public void setUserID_pro_dist(String userID_pro_dist) {
        this.userID_pro_dist = userID_pro_dist;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Map<String, Comment> getComments() {
        return comments;
    }

    public void setComments(Map<String, Comment> comments) {
        this.comments = comments;
    }


}

