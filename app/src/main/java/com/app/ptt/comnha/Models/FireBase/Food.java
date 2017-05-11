package com.app.ptt.comnha.Models.FireBase;

import android.graphics.Bitmap;

import com.app.ptt.comnha.Modules.Times;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 10/29/2016.
 */

public class Food {
    //info
    String name,
            foodID,
            time, //giờ tạo
            date,
            comment, foodImg,
            district, province;//ngày tạo
    long price = 0,
            rating = 0,
            total = 0,
            type = 0;//loại món ăn
    boolean isHidden = false;
    //creator
    String userID;

    //food of store
    String storeID;
    //phép kết
    String dist_prov,
            isHidden_dist_prov,
            isHidden_storeID;//món ăn theo location


    private Bitmap imgBitmap = null;

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("comment", comment);
        result.put("foodImg", foodImg);
        result.put("time", time);
        result.put("date", date);
        result.put("price", price);
        result.put("rating", rating);
        result.put("total", total);
        result.put("type", type);
        result.put("isHidden", isHidden);
        result.put("userID", userID);
        result.put("storeID", storeID);
        result.put("isHidden_storeID", isHidden + "_" + storeID);
        result.put("dist_prov", district + "_" + province);
        result.put("isHidden_dist_prov", isHidden + "_"
                + district + "_" + province);
        return result;
    }

    public Food() {
    }

    public Food(String name, String comment,
                long price, long type,
                String userID, String storeID, String district,
                String province, String foodImg) {
        this.name = name;
        this.comment = comment;
        this.price = price;
        this.type = type;
        this.userID = userID;
        this.storeID = storeID;
        this.district = district;
        this.province = province;
        this.foodImg = foodImg;
        this.time = new Times().getTime();
        this.date = new Times().getDate();
    }

    public String getFoodImg() {
        return foodImg;
    }

    public void setFoodImg(String foodImg) {
        this.foodImg = foodImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getDist_prov() {
        return dist_prov;
    }

    public void setDist_prov(String dist_prov) {
        this.dist_prov = dist_prov;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Bitmap getImgBitmap() {
        return imgBitmap;
    }

    public void setImgBitmap(Bitmap imgBitmap) {
        this.imgBitmap = imgBitmap;
    }
}
