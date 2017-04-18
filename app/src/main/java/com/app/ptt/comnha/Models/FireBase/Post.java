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
            userId;
    //store
    String storeID;
    String storeName;

    //attachedfood
    String foodID;
    //attachedimage
    String bannerName;//
    //rating
    long priceRate,
            healthyRate,
            serviceRate,
            commentCount;

    //phép kết
    String userID_dist_prov,//tìm post theo uid_tỉnh_huyện
            dist_pro;//tìm post theo tỉnh_huyện


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        return result;
    }
}
