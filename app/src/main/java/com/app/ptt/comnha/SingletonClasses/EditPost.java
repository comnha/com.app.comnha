package com.app.ptt.comnha.SingletonClasses;

import com.app.ptt.comnha.FireBase.Image;
import com.app.ptt.comnha.FireBase.Store;
import com.app.ptt.comnha.FireBase.Post;

import java.util.ArrayList;

/**
 * Created by PTT on 10/5/2016.
 */
public class EditPost {
    private static EditPost ourInstance;

    public Store getLocation() {
        return location;
    }

    public void setLocation(Store location) {
        this.location = location;
    }

    Store location;
    public ArrayList<Image> getAlbumList() {
        return albumList;
    }

    public void setAlbumList(ArrayList<Image> albumList) {
        this.albumList = albumList;
    }

    ArrayList<Image> albumList;

    public float getDanhgia() {
        return danhgia;
    }

    public void setDanhgia(float danhgia) {
        this.danhgia = danhgia;
    }

    float danhgia;
    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    private Post post;

    public static EditPost getInstance() {
        if (ourInstance == null) {
            ourInstance = new EditPost();
        }
        return ourInstance;
    }

    private EditPost() {
    }


}
