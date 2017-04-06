package com.app.ptt.comnha.Activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.Modules.MyTool;
import com.app.ptt.comnha.Service.MyService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NGUYEN VAN CUONG on 4/3/2017.
 */

public class BaseActivity extends AppCompatActivity {
    Dialog dialogLocationSetting;
    ProgressDialog progressDialog;

    public static final int MULTIPLE_PERMISSIONS = 10; // cod
    String[] permissions= new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION};
    IntentFilter mIntentFilter;
    boolean binded = false;
    MyService myService;
    boolean isConnected = false;
    MyLocation myLocation;
    MyTool myTool;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!checkPermissions()){

        }else{
            Intent intent = new Intent(this, MyService.class);
            this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }


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

    private boolean checkPermissions() {
        int result;
        List<String> listPermission = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermission.add(p);
            }
        }
        if (!listPermission.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermission.toArray(new String[listPermission.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, MyService.class);
                    this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                } else {

                }

                return;
            }
        }
    }

    public void showLoading(String tilte, String message) {
        progressDialog = ProgressDialog.show(this,
                tilte,
                message, true, false);
    }

    public void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }



    ServiceConnection serviceConnection = new ServiceConnection() {
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
}
