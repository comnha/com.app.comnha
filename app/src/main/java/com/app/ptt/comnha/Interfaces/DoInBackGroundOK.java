package com.app.ptt.comnha.Interfaces;

import android.graphics.Bitmap;

import com.app.ptt.comnha.Models.FireBase.Store;

/**
 * Created by cuong on 12/21/2016.
 */

public interface DoInBackGroundOK {
    void DoInBackGroundStart();
    void DoInBackGroundOK( Boolean isSuccess,int type);
    void DoInBackGroundImg(Bitmap bitmap);
    void DoInBackGroundLocation(Store location);
}
