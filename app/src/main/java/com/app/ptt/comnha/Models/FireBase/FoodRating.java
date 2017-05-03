package com.app.ptt.comnha.Models.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 5/3/2017.
 */

public class FoodRating {
    String title,
            ratingID,
            time, //giờ tạo
            date,
            comment;//ngày tạo
    String userID, un;
    double rate;

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("", title);
        result.put("", comment);
        result.put("", time);
        result.put("", date);
        result.put("", userID);
        result.put("", un);
        result.put("", rate);
        return result;
    }

    public FoodRating(String title, String comment,
                      String userID, String un, double rate) {
        this.title = title;
        this.comment = comment;
        this.userID = userID;
        this.un = un;
        this.rate = rate;
    }
}
