package com.app.ptt.comnha.Const;

import android.Manifest;

/**
 * Created by NGUYEN VAN CUONG on 4/3/2017.
 */

public class Const {
    public static final String BROADCAST_SEND_INFO = "1";
    public static final String BROADCAST_SEND_STATUS_GET_LOCATION = "2";
    public static final String BROADCAST_SEND_STATUS_INTERNET = "3";
    public static final String BROADCAST_PROVIDER_CHANGED = "android.location.PROVIDERS_CHANGED";
    public static final String BROADCAST_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String BROADCAST_SET_URI_IMG = "3";
    public static String[] mListPermissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION};
    public static final String SNACKBAR_GO_ONLINE = "SNACKBAR_GO_ONLINE";
    public static final String SNACKBAR_TURN_ON_GPS= "SNACKBAR_TURN_ON_GPS";

    public static final boolean CONNECTED = true;
    public static final boolean NOTCONNECTED = false;

    public static final int INTENT_KEY_SIGN_UP = 1000;
    public static final String INTENT_KEY_EMAIL = "EMAIL";
    public static final String INTENT_KEY_PASSWORD = "PASSWORD";
    public static final String PREF_FIRST_LAUNCH = "PREF_FIRST_LAUNCH";
    public static final String PREF_MY_LOCATION = "PREF_MY_LOCATION";
    //
    public static final int PERMISSION_LOCATION_FLAG = 2;

    public enum BLOCK_TYPE {

        BLOCK_ADDSTORE, BLOCK_WRITEPOST,
        BLOCK_ADDFOOD, BLOCK_REPRTFOOD,
        BLOCK_REPRTIMG, BLOCK_REPRTSTORE,
        BLOCK_REPRTPOST
    }

}
