package com.app.ptt.comnha.Service;

import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.app.ptt.comnha.FireBase.Account;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.FireBase.Notification;
import com.app.ptt.comnha.Modules.MyTool;
import com.app.ptt.comnha.Modules.Storage;

import java.util.ArrayList;

import static com.app.ptt.comnha.Modules.ConnectionDetector.showSettingNetworkAlert;

public class MyService extends Service {
    private static String LOG_TAG = MyService.class.getSimpleName();
    private final IBinder binder = new MyServiceBinder();
    NetworkChangeReceiver mBroadcastReceiver;
    private IntentFilter mIntentFilter;
    private Dialog dialogLocationSetting;
    String className1 = null;
    static Context staticContext;
    int a = 0;
    static boolean finish = false;
    static int actionPost = -1;
    static int posOfReviewType = 1;

    public static boolean getDeleted() {
        boolean a = deleted;
        if (deleted)
            deleted = false;
        return a;
    }

    public static void setDeleted(boolean deleted) {
        MyService.deleted = deleted;
    }

    static boolean deleted;
    Intent broadcastIntent;

    public static int getPosNotification() {
        return posNotification;
    }

    public static void setPosNotification(int posNotification) {
        MyService.posNotification = posNotification;
        Intent mbroadcastIntent = new Intent();
        mbroadcastIntent.setAction(mBroadcastSendAddress);
        mbroadcastIntent.putExtra("pos", posNotification);
        staticContext.sendBroadcast(mbroadcastIntent);
    }

    static int posNotification;

    public static Context returnContext() {
        return staticContext;
    }

    public static ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public static void setNotifications(ArrayList<Notification> notifications) {
        MyService.notifications = notifications;
    }

    static ArrayList<Notification> notifications;

    public static Account getUserAccount() {
        return userAccount;
    }

    public static void setUserAccount(Account mUserAccount) {
        userAccount = mUserAccount;
    }

    private static Account userAccount;
    public boolean isConnected = false;
    static boolean isConnected1 = false, isSuccess = false;
    public boolean isSaved = false;
    MyTool myTool;
    static String changeContent = "";

    public static void setUriImg(ArrayList<Uri> UriImgs) {

        mUriImgs = UriImgs;
        if (mUriImgs != null) {
            for (Uri uri : mUriImgs) {
                Intent mbroadcastIntent = new Intent();
                Log.i("%%%%%%%%%%%%%%%%", uri.toString());
                mbroadcastIntent.setAction(mBroadcastSendAddress);
                mbroadcastIntent.putExtra("uriImg", String.valueOf(uri));
                mbroadcastIntent.putExtra("pos", mUriImgs.indexOf(uri));
                Log.i(LOG_TAG + ".Send OK", "");
                staticContext.sendBroadcast(mbroadcastIntent);
            }
        } else {
            Intent mbroadcastIntent = new Intent();

            mbroadcastIntent.setAction(mBroadcastSendAddress);
            mbroadcastIntent.putExtra("stt", -1);
            Log.i(LOG_TAG + ".Send OK", "");
            staticContext.sendBroadcast(mbroadcastIntent);
        }


    }

    static ArrayList<Uri> mUriImgs;
    static ArrayList<String> listSaved = new ArrayList<>();
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    public static final String mBroadcastSendAddress1 = "mBroadcastSendAddress1";

    public class MyServiceBinder extends Binder {

        public MyService getService() {
            Log.i(LOG_TAG, "isSaved=" + isSaved + "-" + a);
            return MyService.this;
        }
    }

    public MyService() {
    }

    public static void setFinish(boolean is) {
        finish = is;
        if (finish) {
            Intent mbroadcastIntent = new Intent();
            mbroadcastIntent.setAction(mBroadcastSendAddress);
            mbroadcastIntent.putExtra("finish", true);
            Log.i(LOG_TAG + ".Send OK", "");
            staticContext.sendBroadcast(mbroadcastIntent);
            finish = false;
        }
    }

    public static void setPosOfReviewType(int pos) {
        posOfReviewType = pos;
    }

    public static int getPosOfReviewType() {
        return posOfReviewType;
    }

    public static String getChangeContent() {
        return changeContent;
    }

    public static void setChangeContent(String isChanged) {
        changeContent = isChanged;
        if (changeContent.equals("justCommend") || changeContent.equals("justDelete")) {
            Log.i(LOG_TAG + ".setChangeContent", "before remove=" + listSaved.size());
            for (int a = 0; a < listSaved.size(); a++) {
                Log.i(LOG_TAG + ".setChangeContent- " + listSaved.get(a).toString(), "");
                if (listSaved.get(a).toString().toLowerCase().contains("postlist".toLowerCase())) {
                    listSaved.remove(a);
                    Log.i(LOG_TAG + ".setChangeContent- ", "after remove remove=" + listSaved.size());
                }
            }

        }
    }

    public static int saveToListSaved(String file) {
        for (String myFile : listSaved)
            if (myFile.equals(file)) {
                Log.i(LOG_TAG + ".saveToListSaved=1", file);
                return 1;
            }
        listSaved.add(file);
        Log.i(LOG_TAG + ".saveToListSaved=2", file);
        return 2;
    }

    public static void setActionAddPost(int i) {
        actionPost = i;
    }

    public static int getActionAddPost() {
        return actionPost;
    }

    public static void setStatusChooseImg(boolean sc) {
        isSuccess = sc;
    }

    public static boolean getStatusChooseImg() {
        return isSuccess;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mIntentFilter.addAction("android.location.PROVIDERS_CHANGED");
        mIntentFilter.addAction(mBroadcastSendAddress1);
        mBroadcastReceiver = new NetworkChangeReceiver();
        broadcastIntent = new Intent();
        registerReceiver(mBroadcastReceiver, mIntentFilter);
        staticContext = getApplicationContext();
        // TODO: Return the communication channel to the service.
        return this.binder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(LOG_TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unregisterReceiver(mBroadcastReceiver);
        Log.i(LOG_TAG, "onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    public static boolean returnIsConnected() {
        return isConnected1;
    }

    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastSendAddress1) && intent.getIntExtra("STT", 0) == 2) {
                Log.i(LOG_TAG + ".onReceive form MyTool", "Save location");
                Log.i(LOG_TAG, "save location=" + isSaved + "-" + a);
                if (myTool.getYourLocation() != null) {
                    Storage.deleteFile(getApplicationContext(), "myLocation");
                    ArrayList<MyLocation> list = new ArrayList<>();
                    list.add(myTool.getYourLocation());
                    Storage.writeFile(getApplicationContext(), Storage.parseMyLocationToJson(list).toString(), "myLocation");
                    isSaved = true;
                    myTool.stopLocationUpdate();
                    broadcastIntent.setAction(mBroadcastSendAddress);
                    broadcastIntent.putExtra("isConnected", true);
                    context.sendBroadcast(broadcastIntent);
                }
            }

            Log.i(LOG_TAG, "isSaved=" + isSaved + "-" + a);
            if (isNetworkAvailable(context) && canGetLocation(context)) {
                if (!isSaved && a == 0) {
                    myTool = new MyTool(context, MyService.class.getSimpleName());
                    myTool.startGoogleApi();
                    a++;
                } else {
                    broadcastIntent.setAction(mBroadcastSendAddress);
                    broadcastIntent.putExtra("isConnected", true);
                    context.sendBroadcast(broadcastIntent);
                }
                isConnected = true;
                isConnected1 = true;
                Log.i(LOG_TAG + ".sendStatus", "isConnected=" + isConnected);

            } else {
                broadcastIntent.setAction(mBroadcastSendAddress);
                broadcastIntent.putExtra("isConnected", false);
                context.sendBroadcast(broadcastIntent);
                isConnected = false;
                isConnected1 = false;
                Log.i(LOG_TAG + ".sendStatus", "isConnected=" + isConnected);
            }
        }

        private boolean canGetLocation(Context mContext) {

            int a = 0;
            try {
                a = Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE);
                //Log.i(LOG_TAG + ".canGetLocation", a + "");
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            if (a >= 2) return true;
            return false;
        }

        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivity = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();

                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        //Toast.makeText(getApplicationContext(),"Ten:"+info[i].getTypeName()+"--TrangThai:"+info[i].getState().toString(),Toast.LENGTH_LONG).show();
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            Log.v(LOG_TAG, "Now you are connected to Internet!");
                            return true;
                        }
                    }
                }
            }
            Log.v(LOG_TAG, "You are not connected to Internet!");
            return false;
        }
    }

}
