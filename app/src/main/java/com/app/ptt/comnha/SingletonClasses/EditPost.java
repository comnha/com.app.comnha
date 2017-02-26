package com.app.ptt.comnha.SingletonClasses;

import com.app.ptt.comnha.FireBase.Image;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.FireBase.Post;

import java.util.ArrayList;

/**
 * Created by PTT on 10/5/2016.
 */
public class EditPost {
    private static EditPost ourInstance;

    public MyLocation getLocation() {
        return location;
    }

    public void setLocation(MyLocation location) {
        this.location = location;
    }

    MyLocation location;
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
