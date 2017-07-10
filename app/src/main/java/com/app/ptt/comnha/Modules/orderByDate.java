package com.app.ptt.comnha.Modules;

import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.UserNotification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * Created by ciqaz on 09/07/2017.
 */

public class orderByDate implements Comparator<UserNotification> {
    SimpleDateFormat dateFormat=new SimpleDateFormat(Const.DATE_TIME_FORMAT);
    @Override
    public int compare(UserNotification o1, UserNotification o2) {
        try {
            if( dateFormat.parse(o1.getDate()).compareTo(dateFormat.parse(o2.getDate()))==-1){
                return 1;
            }
            if( dateFormat.parse(o1.getDate()).compareTo(dateFormat.parse(o2.getDate()))==1){
                return -1;
            }
            if( dateFormat.parse(o1.getDate()).compareTo(dateFormat.parse(o2.getDate()))==0){
                return 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  0;
    }
}