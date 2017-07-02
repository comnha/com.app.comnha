package com.app.ptt.comnha.Models.FireBase;

import com.app.ptt.comnha.Modules.Times;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 10/5/2016.
 */
public class Comment {
    String commentID,
            content,
            time,
            date;
    //creator
    String un,
            avatar = "",
            userID;
    //post
    String postID;
    boolean isHidden = false;
    //phép kết
    String isHidden_postID;//

    public Comment(String content, String un, String avatar, String userID, String postID) {
        this.content = content;
        this.time = new Times().getTimeNoSecond();
        this.date = new Times().getDate();
        this.un = un;
        this.avatar = avatar;
        this.userID = userID;
        this.postID = postID;
    }

    public Comment() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("content", content);
        result.put("time", time);
        result.put("date", date);
        result.put("un", un);
        result.put("avatar", avatar);
        result.put("userID", userID);
        result.put("postID", postID);
        result.put("isHidden", isHidden);
        result.put("isHidden_postID", isHidden + "_" + postID);
        return result;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getUn() {
        return un;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

}
