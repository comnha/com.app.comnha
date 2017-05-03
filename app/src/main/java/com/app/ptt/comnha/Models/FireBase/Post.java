package com.app.ptt.comnha.Models.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 9/19/2016.
 */
public class Post {
    //info
    String title,
            content,
            date,
            time,
            postID;
    //creator
    String un,
            userID,
            avatar;
    //store
    String storeID;
    String storeName;

    //attachedfood
    String foodID;
    //attachedimage
    String banner;//
    //rating
    long priceRate,
            healthyRate,
            serviceRate;
    Map<String, Comment> comments = null;
    //phép kết
    String userID_dist_prov,//tìm post theo uid_tỉnh_huyện
            dist_pro;//tìm post theo tỉnh_huyện

    public Post(String title, String content,
                String date, String time,
                String un, String userID, String avatar, String storeID,
                String storeName, String foodID, String banner,
                long priceRate, long healthyRate,
                long serviceRate, String dist_pro) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.time = time;
        this.un = un;
        this.userID = userID;
        this.avatar = avatar;
        this.storeID = storeID;
        this.storeName = storeName;
        this.foodID = foodID;
        this.banner = banner;
        this.priceRate = priceRate;
        this.healthyRate = healthyRate;
        this.serviceRate = serviceRate;
        this.userID_dist_prov = userID + "_" + dist_pro;
        this.dist_pro = dist_pro;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("content", content);
        result.put("date", date);
        result.put("time", time);
        result.put("userID", userID);
        result.put("avatar", avatar);
        result.put("un", un);
        result.put("storeID", storeID);
        result.put("storeName", storeName);
        result.put("foodID", foodID);
        result.put("banner", banner);
        result.put("priceRate", priceRate);
        result.put("healthyRate", healthyRate);
        result.put("serviceRate", serviceRate);
        result.put("comments", comments);
        result.put("userID_dist_prov", userID_dist_prov);
        result.put("dist_pro", dist_pro);
        return result;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getUn() {
        return un;
    }

    public void setUn(String un) {
        this.un = un;
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

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public long getPriceRate() {
        return priceRate;
    }

    public void setPriceRate(long priceRate) {
        this.priceRate = priceRate;
    }

    public long getHealthyRate() {
        return healthyRate;
    }

    public void setHealthyRate(long healthyRate) {
        this.healthyRate = healthyRate;
    }

    public long getServiceRate() {
        return serviceRate;
    }

    public void setServiceRate(long serviceRate) {
        this.serviceRate = serviceRate;
    }

    public Map<String, Comment> getComments() {
        return comments;
    }

    public void setComments(Map<String, Comment> comments) {
        this.comments = comments;
    }

    public String getUserID_dist_prov() {
        return userID_dist_prov;
    }

    public void setUserID_dist_prov(String userID_dist_prov) {
        this.userID_dist_prov = userID_dist_prov;
    }

    public String getDist_pro() {
        return dist_pro;
    }

    public void setDist_pro(String dist_pro) {
        this.dist_pro = dist_pro;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
