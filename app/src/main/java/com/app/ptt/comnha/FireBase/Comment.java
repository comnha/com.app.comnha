package com.app.ptt.comnha.FireBase;

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
            userID;
    //post
    String postID;

    //phép kết
    String dist_prov;//
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        return result;
    }
}
