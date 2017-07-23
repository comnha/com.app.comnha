package com.app.ptt.comnha.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.Utils.MyTool;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by NGUYEN VAN CUONG on 4/3/2017.
 */

public class BaseActivity extends AppCompatActivity {
    Dialog dialogLocationSetting;
    ProgressDialog progressDialog;

    public static final int MULTIPLE_PERMISSIONS = 10; // cod
    DatabaseReference dbRef;
    IntentFilter mIntentFilter;
    boolean binded = false;
    MyService myService;
    boolean isConnected = false;
    Store store;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onStart() {
        super.onStart();


    }
//    public void showDialogOpenLocationService() {
//        if (dialogLocationSetting != null && dialogLocationSetting.isShowing()) {
//            return;
//        }
//
//        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        boolean gpsEnabled = false;
//        boolean networkEnabled = false;
//        try {
//            gpsEnabled = lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
//            networkEnabled =
//                    lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
//            if (gpsEnabled == false) {
//                // notify user
//                android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(this);
//                alertBuilder.setCancelable(false);
//                alertBuilder.setTitle("GPS");
//                alertBuilder.setMessage("Bật GPS để sử dụng ứng dụng");
//                alertBuilder.setPositiveButton("Đến GPS Setting", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivity(myIntent);
//                    }
//                });
//
//                dialogLocationSetting = alertBuilder.create();
//                dialogLocationSetting.show();
//            }
//            if (networkEnabled == false) {
//                // notify user
//                android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(this);
//                alertBuilder.setCancelable(false);
//                alertBuilder.setTitle("Internet");
//                alertBuilder.setMessage("Bật Internet để sử dụng ứng dụng");
//                alertBuilder.setPositiveButton("Đến Internet Setting", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        Intent myIntent = new Intent(Settings.ACTION_SETTINGS);
//                        startActivity(myIntent);
//                    }
//                });
//
//                dialogLocationSetting = alertBuilder.create();
//                dialogLocationSetting.show();
//            }
//        } catch (Exception ex) {
//        }
//
//
//    }

    protected void closeDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    protected void showProgressDialog(String tilte, String message) {
        if (progressDialog == null) {
            if (tilte.equals("")) {
                tilte = getString(R.string.txt_plzwait);
            }
            progressDialog = ProgressDialog.show(this,
                    tilte,
                    message, true, false);
        }
    }

    protected void handleProgressDialog() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isShowProgress()) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getApplicationContext(), "try again", Toast.LENGTH_SHORT).show();
                }
            }
        }, 15000);
    }

    protected boolean isShowProgress() {
        return progressDialog.isShowing();
    }
    public void startMyService(){
        Intent intent = new Intent(this, MyService.class);
        this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

   public  ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService = new MyService();

            MyService.MyServiceBinder binder = (MyService.MyServiceBinder) service;
            binder.getService();
            binded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binded = false;
        }
    };




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                }

                return;
            }
        }
    }
}
