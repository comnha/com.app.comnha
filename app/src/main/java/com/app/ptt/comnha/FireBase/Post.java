package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 9/19/2016.
 */
public class Post {
    //không nên để là private
    String title;
    String content;
    String date;
    String time;
    String postID;
    String userId;
    String userName;
    String locaID;
    String locaName;

    String diachi;
    String hinh;
    long vesinh, phucvu, gia;
    int likeCount;
    int commentCount;
    Food food;
    int type;
    boolean visible;
    String index;

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    String reporter;
    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }



    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }





    public String getHinh() {
        return hinh;
    }

    public void setHinh(String hinh) {
        this.hinh = hinh;
    }



    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    //Map<String, Boolean> likes;

    public Post() {
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getDiachi() {

        return diachi;
    }



    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getCommentCount() {

        return commentCount;
    }

    public void setLocaID(String locaID) {
        this.locaID = locaID;
    }

    public void setLocaName(String locaName) {
        this.locaName = locaName;
    }

    public String getLocaID() {
        return locaID;
    }

    public String getLocaName() {
        return locaName;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setVesinh(long vesinh) {
        this.vesinh = vesinh;
    }

    public void setPhucvu(long phucvu) {
        this.phucvu = phucvu;
    }

    public void setGia(long gia) {
        this.gia = gia;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getPostID() {
        return postID;
    }

    public long getVesinh() {
        return vesinh;
    }

    public long getPhucvu() {
        return phucvu;
    }

    public long getGia() {
        return gia;
    }

    public boolean getIsReported() {
        return isReported;
    }

    public void setReported(boolean reported) {
        isReported = reported;
    }

    boolean isReported;
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("content", content);
        result.put("date", date);
        result.put("time", time);
        result.put("userName", userName);
        result.put("userId", userId);
        result.put("vesinh", vesinh);
        result.put("phucvu", phucvu);
        result.put("gia", gia);
        result.put("commentCount", commentCount);
        result.put("likeCount", likeCount);
        result.put("locaID", locaID);
        result.put("locaName", locaName);
        result.put("diachi", diachi);
        result.put("hinh",hinh);
        result.put("type", type);
        result.put("food",food) ;
        result.put("index",index);
        result.put("visible",visible);
        result.put("isreported",isReported);
        result.put("reporter",reporter);
        return result;
    }
}
