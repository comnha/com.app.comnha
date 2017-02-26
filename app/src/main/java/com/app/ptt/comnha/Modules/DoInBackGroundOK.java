package com.app.ptt.comnha.Modules;

import android.graphics.Bitmap;
import android.net.Uri;

import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.FireBase.Post;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by cuong on 12/21/2016.
 */

public interface DoInBackGroundOK {
    void DoInBackGroundStart();
    void DoInBackGroundOK( Boolean isSuccess,int type);
    void DoInBackGroundImg(Bitmap bitmap);
    void DoInBackGroundLocation(MyLocation location);
}
