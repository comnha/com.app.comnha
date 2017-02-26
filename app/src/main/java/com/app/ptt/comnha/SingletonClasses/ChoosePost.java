package com.app.ptt.comnha.SingletonClasses;

/**
 * Created by PTT on 10/5/2016.
 */
public class ChoosePost {
    private static ChoosePost ourInstance;
    private String postID;
    private String tinh;
    private String huyen;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    String userID;
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    int type;
    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    private String foodID;

    public static ChoosePost getInstance() {
        if (ourInstance == null) {
            ourInstance = new ChoosePost();
        }
        return ourInstance;
    }

    private ChoosePost() {
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public void setTinh(String tinh) {
        this.tinh = tinh;
    }

    public String getHuyen() {
        return huyen;
    }

    public String getTinh() {
        return tinh;
    }

    public String getPostID() {
        return postID;
    }

    public void setHuyen(String huyen) {
        this.huyen = huyen;

    }
}
