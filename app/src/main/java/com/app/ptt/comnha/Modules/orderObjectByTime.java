package com.app.ptt.comnha.Modules;

import com.app.ptt.comnha.Models.FireBase.Comment;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.Store;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * Created by ciqaz on 09/07/2017.
 */

public class orderObjectByTime implements Comparator<Object> {
    public int checkType(Object ob){
        if(ob instanceof Post){
            return 0;
        }
        if(ob instanceof Store){
            return 1;
        }
        if(ob instanceof Comment){
            return 2;
        }
        return -1;
    }
    SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat timeFormat=new SimpleDateFormat("hh:mm"); //
    SimpleDateFormat timeFormat_full=new SimpleDateFormat("hh:mm:ss");
    @Override
    public int compare(Object o_1, Object o_2) {
        //0 post
        //1 store
        //3 comment
        switch (checkType(o_1)){
            case 0:
                Post objPost1 =(Post) o_1;
                Post objPost2=(Post) o_2;
                try {
                    if (dateFormat.parse(objPost1.getDate()).compareTo(dateFormat.parse(objPost2.getDate())) == -1) {
                        return 1;
                    }
                    if (dateFormat.parse(objPost1.getDate()).compareTo(dateFormat.parse(objPost2.getDate())) == 1) {
                        return -1;
                    }
                    if (dateFormat.parse(objPost1.getDate()).compareTo(dateFormat.parse(objPost2.getDate())) == 0) {
                        if (timeFormat_full.parse(objPost1.getTime()).compareTo(timeFormat_full.parse(objPost2.getTime())) == -1) {
                            return 1;
                        }
                        if (timeFormat_full.parse(objPost1.getTime()).compareTo(timeFormat_full.parse(objPost2.getTime())) == 1) {
                            return -1;
                        }
                        return 0;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

            case 1:
                Store objStore1 =(Store) o_1;
                Store objStore2=(Store) o_2;
                try {
                    if (dateFormat.parse(objStore1.getDate()).compareTo(dateFormat.parse(objStore2.getDate())) == -1) {
                        return 1;
                    }
                    if (dateFormat.parse(objStore1.getDate()).compareTo(dateFormat.parse(objStore2.getDate())) == 1) {
                        return -1;
                    }
                    if (dateFormat.parse(objStore1.getDate()).compareTo(dateFormat.parse(objStore2.getDate())) == 0) {
                        if (timeFormat_full.parse(objStore1.getTime()).compareTo(timeFormat_full.parse(objStore2.getTime())) == -1) {
                            return 1;
                        }
                        if (timeFormat_full.parse(objStore1.getTime()).compareTo(timeFormat_full.parse(objStore2.getTime())) == 1) {
                            return -1;
                        }
                        return 0;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                Comment objComment1 =(Comment) o_1;
                Comment objComment2=(Comment) o_2;
                try {
                    if (dateFormat.parse(objComment1.getDate()).compareTo(dateFormat.parse(objComment2.getDate())) == -1) {
                        return -1;
                    }
                    if (dateFormat.parse(objComment1.getDate()).compareTo(dateFormat.parse(objComment2.getDate())) == 1) {
                        return 1;
                    }
                    if (dateFormat.parse(objComment1.getDate()).compareTo(dateFormat.parse(objComment2.getDate())) == 0) {
                        if (timeFormat_full.parse(objComment1.getTime()).compareTo(timeFormat_full.parse(objComment2.getTime())) == -1) {
                            return -1;
                        }
                        if (timeFormat_full.parse(objComment1.getTime()).compareTo(timeFormat_full.parse(objComment2.getTime())) == 1) {
                            return 1;
                        }
                        return 0;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

        }
        return -1;
    }
}