package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 9/26/2016.
 */

public class Store {
    //info
    String locaID;
    String name, diachi, sdt, opentime, closetime;
    //location
    String province,
            district;
    double lat, lng;
    //creator
    String userId;//người tạo
    String time;//giờ tạo
    String date;//ngày tạo
    //rating
    long minprice, maxprice;
    long priceSum = 0, healthySum = 0, serviceSum = 0;//điểm tổng giá-vệ sinh-phục vụ
    long size = 0; //số lượng đánh giá
    long priceAVG, healthyAVG, serviceAVG,//điểm trung bình giá-vệ sinh-phục vụ
            rateAVG;
    String storeimg = "";//ảnh đại diện của store
    //phép kết
    String pro_dict,//tìm theo tỉnh_huyển
            userID_pro_dist,//tìm theo user_tỉnh_huyện
            userID_date;//tìm theo user_ngày tạo

    String distance;//khoảng cách hiển thị trên danh sách quán ăn
    Map<String, Comment> comments = null;//comment trong post

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        return result;
    }

}
