package com.app.ptt.comnha.Modules;

import com.app.ptt.comnha.Models.FireBase.Comment;
import com.app.ptt.comnha.Models.FireBase.UserNotification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * Created by ciqaz on 09/07/2017.
 */

public class orderByTime implements Comparator<Comment> {
    SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat timeFormat=new SimpleDateFormat("hh:mm");
    @Override
    public int compare(Comment o1, Comment o2) {

        try {
            if( dateFormat.parse(o1.getDate()).compareTo(dateFormat.parse(o2.getDate()))==-1){
                return -1;
            }
            if( dateFormat.parse(o1.getDate()).compareTo(dateFormat.parse(o2.getDate()))==1){
                return 1;
            }
            if( dateFormat.parse(o1.getDate()).compareTo(dateFormat.parse(o2.getDate()))==0){
                if(timeFormat.parse(o1.getTime()).compareTo(timeFormat.parse(o2.getTime()))==-1){
                    return -1;
                }
                if(timeFormat.parse(o1.getTime()).compareTo(timeFormat.parse(o2.getTime()))==1){
                    return 1;
                }
                return 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  0;
    }
}