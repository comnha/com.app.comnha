package com.app.ptt.comnha.Models.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 10/29/2016.
 */

public class Food {
    //info
    String tenmon,
            monID,
            time, //giờ tạo
            date;//ngày tạo
    long gia;
    float danhGia;
    long type;//loại món ăn
    //creator
    String userID;

    //food of store
    String storeID;

    //phép kết
    String dist_prov,//món ăn theo location
            foodID_storeID;//món ăn theo quán


    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        return result;
    }
}
