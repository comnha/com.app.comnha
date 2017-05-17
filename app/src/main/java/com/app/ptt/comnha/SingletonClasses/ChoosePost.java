package com.app.ptt.comnha.SingletonClasses;

import com.app.ptt.comnha.Models.FireBase.Post;

/**
 * Created by PTT on 10/5/2016.
 */
public class ChoosePost {
    private static ChoosePost ourInstance;
    Post post;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public static ChoosePost getInstance() {
        if (ourInstance == null) {
            ourInstance = new ChoosePost();
        }
        return ourInstance;
    }
}
