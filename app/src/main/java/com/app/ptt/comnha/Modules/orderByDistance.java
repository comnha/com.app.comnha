package com.app.ptt.comnha.Modules;

import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Models.FireBase.UserNotification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * Created by ciqaz on 09/07/2017.
 */

public class orderByDistance implements Comparator<Store> {

    @Override
    public int compare(Store o1, Store o2) {
        return ((Double.parseDouble(o1.getDistance()) < Double.parseDouble(o2.getDistance())) ? 1 :
                (Double.parseDouble(o1.getDistance()) > Double.parseDouble(o2.getDistance())) ? -1 : 0);

    }
}