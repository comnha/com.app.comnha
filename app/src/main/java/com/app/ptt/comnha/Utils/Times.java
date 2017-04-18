package com.app.ptt.comnha.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by NguyenVanCuong on 4/13/2017.
 */

public class Times {
    public Times() {
    }
    public String getDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        return df.format(c.getTime());
    }

    public String getTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        return df.format(c.getTime());
    }
    public String getTimeNoSecond() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        return df.format(c.getTime());
    }
    public Date getCustomDate(String datetime){
        SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            Date date=format.parse(datetime);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;

        }
    }
}