package com.app.ptt.comnha.Const;

import android.Manifest;

/**
 * Created by NGUYEN VAN CUONG on 4/3/2017.
 */

public class Const {
    public static final String DATABASE_PATH="https://comnha-e4dbe.firebaseio.com/";
    public static final String STORAGE_PATH="gs://comnha-e4dbe.appspot.com/";
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
    public static final String SNACKBAR_GO_ONLINE = "5";
    public static final String SNACKBAR_TURN_ON_GPS= "6";

    public static final boolean CONNECTED = true;
    public static final boolean NOTCONNECTED = false;
    public static final String DATE_TIME_FORMAT="dd/MM/yyyy HH:mm:ss";
    public static final String INTENT_KEY_EMAIL = "EMAIL";
    public static final String INTENT_KEY_PASSWORD = "PASSWORD";
    public static final String PREF_FIRST_LAUNCH = "PREF_FIRST_LAUNCH";
    public static final String PREF_FIRST_PROFILE_LAUNCH = "PREF_FIRST_PROFILE_LAUNCH";
    public static final String PREF_MY_LOCATION = "PREF_MY_LOCATION";
    public static final int INTENT_KEY_SIGN_UP = 29;
    public static final String INTENT_KEY_SIGN_IN_STORE = "INTENT_KEY_SIGN_IN_STORE";
    public static final String INTENT_KEY_SIGN_IN_POST = "INTENT_KEY_SIGN_IN_POST";
    public static final String INTENT_KEY_RECEIVE_LOCATION = "INTENT_KEY_RECEIVE_LOCATION";
    public static final String INTENT_KEY_USER_CHANGE = "INTENT_KEY_USER_CHANGE";
    public static final String INTENT_KEY_SORT_STORE = "INTENT_KEY_SORT_STORE";
    public static final String INTENT_KEY_SORT_POST = "INTENT_KEY_SORT_POST";
    public static final String INTENT_KEY_SORT_STORE_TYPE = "INTENT_KEY_SORT_STORE_TYPE";
    public static final String INTENT_KEY_SORT_POST_TYPE= "INTENT_KEY_SORT_POST_TYPE";
    public static final String INTENT_KEY_RECEIVE_LOCATION_TAB = "INTENT_KEY_RECEIVE_LOCATION_TAB";
    public static final String INTENT_KEY_NOTI_NEW_NOTI = "INTENT_KEY_NOTI_NEW_NOTI";
    public static final String INTENT_KEY_RELOAD_DATA= "INTENT_KEY_RELOAD_DATA";
    public static final String KEY_HUYEN = "KEY_HUYEN";
    public static final String KEY_TINH = "KEY_TINH";
    public static final String KEY_SORT = "KEY_SORT";
    public static final String KEY_FOOD= "KEY_FOOD";
    public static final String KEY_STORE= "KEY_STORE";
    //
    public static final int PERMISSION_LOCATION_FLAG = 2;
    public static final String DECIMAL_1F = "%.1f";
    public enum BLOCK_TYPE {

        BLOCK_ADDSTORE, BLOCK_WRITEPOST,
        BLOCK_ADDFOOD, BLOCK_REPRTFOOD,
        BLOCK_REPRTIMG, BLOCK_REPRTSTORE,
        BLOCK_REPRTPOST,BLOCK_COMMENT
    }

    public enum REPORTS {
        REPORT_STORE, REPORT_POST,
        REPORT_IMG,REPORT_FOOD;
    }
}
