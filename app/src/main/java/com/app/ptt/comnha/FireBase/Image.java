package com.app.ptt.comnha.FireBase;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 11/8/2016.
 */

public class Image {
    //info
    String imageID,
            name,
            caption="",
            date,
            time;
    //creator
    String userID;//tìm theo tài khoản

    //type of image
    int type=4;//banner of post, profile hoặc images of post (1:banner, 2:profile, 3:post's image, 4:normal image)
    String postID="",//tìm theo post
            storeID="";//tìm theo quán ăn
    String type_imageID;//Phân loại theo banner, profile, images of post
    Uri path;
    String image;

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        return result;
    }
}
