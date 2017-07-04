package com.app.ptt.comnha.Interfaces;

import android.graphics.Bitmap;

import com.app.ptt.comnha.Models.FireBase.Store;

/**
 * Created by cuong on 12/21/2016.
 */

public interface SendLocationListener {
    void notice(String tinh,String huyen);
    void notice1(String tinh,String huyen);
}
