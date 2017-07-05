package com.app.ptt.comnha.Models.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cuong on 12/22/2016.
 */

public class Notification {
    private String newStoreID;
    //type =1 new store added
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type;
    public String getNewStoreID() {
        return newStoreID;
    }

    public void setNewStoreID(String newStoreID) {
        this.newStoreID = newStoreID;
    }


    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("newStoreID",newStoreID);
        result.put("type",type);
        return result;
    }

}
