package com.app.ptt.comnha.Models.FireBase;

import android.graphics.Bitmap;

import com.app.ptt.comnha.Modules.Times;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            userID;
    //store

    String storeID;
    String storeName;

    //attachedfood
    String foodID = "";
    long foodRate=0;

    //attachedimage
    String banner = "";//
    //rating
    long priceRate = 0,
            healthyRate = 0,
            serviceRate = 0;
    boolean isHidden = false;
    //post type =0 chua dc duyet, isHidden=true;
    //1: accept
    //-1: reject
    //3: was reported
    //2: reject report
    //-2: accept report
    int postType;
    List<String> userComment = new ArrayList<>();


    Map<String, Comment> comments=new HashMap<>();
    //phép kết
    String userID_dist_prov,
            isHidden_dist_prov,//tìm post theo uid_tỉnh_huyện
            dist_pro,//tìm post theo tỉnh_huyện
            isHidden_storeID,
            isHidden_foodID, isHidden_uID_postID;
    Bitmap imgBitmap = null;

    public boolean checkExist(String user){
        if(this.userComment==null){
            userComment=new ArrayList<>();
        }
        for(String mUser:userComment){
            if(mUser.toLowerCase().equals(user.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    public String getIsHidden_uID_postID() {
        return isHidden_uID_postID;
    }
    public int getPostType() {
        return postType;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

    public Map<String, Comment> getComments() {
        return comments;
    }

    public void setComments(Map<String, Comment> comments) {
        this.comments = comments;
    }

    public void setIsHidden_uID_postID(String isHidden_uID_postID) {
        this.isHidden_uID_postID = isHidden_uID_postID;
    }

    public List<String> getUserComment() {
        return userComment;
    }

    public void setUserComment(List<String> userComment) {
        this.userComment = userComment;
    }

    public boolean addUsertoList(String user) {

        try {
            if (!userComment.contains(user)) {
                userComment.add(user);
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public boolean removeUser(String user) {
        try {
            if (userComment.contains(user)) {
                userComment.remove(user);
                return true;
            }
        } catch (Exception e) {

        }
        return false;

    }

    public Post() {

    }

    public Post(String title, String content,
                String un, String userID, String storeID,
                String storeName, String foodID, long foodRate,
                String banner, long priceRate, long healthyRate,
                long serviceRate, String dist_pro) {
        this.title = title;
        this.content = content;
        this.date = new Times().getDate();
        this.time = new Times().getTime();
        this.un = un;
        this.userID = userID;
        this.storeID = storeID;
        this.storeName = storeName;
        this.foodID = foodID;
        this.foodRate = foodRate;
        this.banner = banner;
        this.priceRate = priceRate;
        this.healthyRate = healthyRate;
        this.serviceRate = serviceRate;
        this.dist_pro = dist_pro;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("content", content);
        result.put("date", date);
        result.put("time", time);
        result.put("userID", userID);
        result.put("un", un);
        result.put("storeID", storeID);
        result.put("storeName", storeName);
        result.put("foodID", foodID);
        result.put("foodRate", foodRate);
        result.put("banner", banner);
        result.put("priceRate", priceRate);
        result.put("healthyRate", healthyRate);
        result.put("serviceRate", serviceRate);
        result.put("comments", comments);
        result.put("userComment", userComment);
        result.put("userID_dist_prov", userID+"_"+dist_pro);
        result.put("dist_pro", dist_pro);
        result.put("isHidden", isHidden);
        result.put("userID_dist_prov", userID+"_"+dist_pro);
        result.put("dist_pro", dist_pro);
        result.put("isHidden", isHidden);

        result.put("isHidden_dist_prov", String.valueOf(isHidden)
                + "_" + dist_pro);
        result.put("isHidden_storeID", String.valueOf(isHidden)
                + "_" + storeID);
        result.put("isHidden_foodID", String.valueOf(isHidden)
                + "_" + foodID);
        result.put("isHidden_uID", String.valueOf(isHidden)
                + "_" + userID);
        result.put("postType",postType);
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

//    public Map<String, Comment> getComments() {
//        return Comments;
//    }
//
//    public void setComments(Map<String, Comment> comments) {
//        this.Comments = comments;
//    }

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

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public String getIsHidden_dist_prov() {
        return isHidden_dist_prov;
    }

    public void setIsHidden_dist_prov(String isHidden_dist_prov) {
        this.isHidden_dist_prov = isHidden_dist_prov;
    }

    public String getIsHidden_storeID() {
        return isHidden_storeID;
    }

    public void setIsHidden_storeID(String isHidden_storeID) {
        this.isHidden_storeID = isHidden_storeID;
    }

    public String getIsHidden_foodID() {
        return isHidden_foodID;
    }

    public void setIsHidden_foodID(String isHidden_foodID) {
        this.isHidden_foodID = isHidden_foodID;
    }

    public Bitmap getImgBitmap() {
        return imgBitmap;
    }

    public void setImgBitmap(Bitmap imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    public long getFoodRate() {
        return foodRate;
    }

    public void setFoodRate(long foodRate) {
        this.foodRate = foodRate;
    }
}
