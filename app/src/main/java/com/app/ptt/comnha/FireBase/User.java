package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 9/16/2016.
 */
public class User {
    String userID,
            ho,
            ten,
            tenlot,
            birth,
            un,
            email, password,
            confirmPass,

    //more detail
            street = "",
            ward = "",
            district = "",
            province = "",
            phonenumb = "",
            work = "";
    //profile image
    String pro_imgName;//tên ảnh đại diện

    boolean sexual = false,
            permission = false;

    //phép kết
    String dist_prov;//tìm user theo tỉnh_huyện

    public Map<Object, String> toMap() {
        Map<Object, String> result = new HashMap<>();

        return result;
    }
}
