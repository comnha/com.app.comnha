package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 10/5/2016.
 */

public class Comment {
    String content;
    String time;
    String date;
    String username;
    String userID;
    String postID;

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    String commentID;

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostID() {

        return postID;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public String getUserID() {
        return userID;
    }

    public Comment() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("content", content);
        result.put("time", time);
        result.put("date", date);
        result.put("userID", userID);
        result.put("username", username);
        result.put("postID", postID);
        return result;
    }
}
