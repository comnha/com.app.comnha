package com.app.ptt.comnha.SingletonClasses;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.MyLocation;
import com.app.ptt.comnha.Utils.Storage;
import com.app.ptt.comnha.preference.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ciqaz on 24/05/2017.
 */

public class CoreManager {
    private static CoreManager ourInstance;
    private String tinh;

    public String getTinh() {
        return tinh;
    }

    public void setTinh(String tinh) {
        this.tinh = tinh;
    }

    public String getHuyen() {
        return huyen;
    }

    public void setHuyen(String huyen) {
        this.huyen = huyen;
    }

    private String huyen;
    private boolean isPostAdded;


    public static CoreManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new CoreManager();
        }
        return ourInstance;
    }

    public void initData(Context context) {
        this.isLaunchFistTime = PreferenceUtils.getBoolPref(context, Const.PREF_FIRST_LAUNCH, true);
        this.myLocation = PreferenceUtils.getStringPref(context, Const.PREF_MY_LOCATION, "");
        this.isCheckProfileFirstTime=PreferenceUtils.getBoolPref(context,Const.PREF_FIRST_PROFILE_LAUNCH,true);
    }

    public boolean isCheckProfileFirstTime() {
        return isCheckProfileFirstTime;
    }

    public void setCheckProfileFirstTime(Context context,boolean checkProfileFirstTime) {
        isCheckProfileFirstTime = checkProfileFirstTime;
        PreferenceUtils.saveBoolPref(context, Const.PREF_FIRST_PROFILE_LAUNCH, checkProfileFirstTime);
    }

    boolean isCheckProfileFirstTime;

    boolean isLaunchFistTime;


    public boolean isLaunchFistTime() {
        return isLaunchFistTime;
    }

    public void setLaunchFistTime(Context context, boolean launchFistTime) {
        isLaunchFistTime = launchFistTime;
        PreferenceUtils.saveBoolPref(context, Const.PREF_FIRST_LAUNCH, isLaunchFistTime);
    }


    private String myLocation;



    public MyLocation getMyLocation() {
        List<MyLocation> list = new ArrayList<>();
        if (!TextUtils.isEmpty(myLocation)) {
            list = Storage.readJSONMyLocation(myLocation);
        } else {
            return null;
        }
        return list.get(0);
    }

    public void setMyLocation(Context context, String myLocation) {
        this.myLocation = myLocation;
        PreferenceUtils.saveStringPref(context, Const.PREF_MY_LOCATION, myLocation);
    }

}
