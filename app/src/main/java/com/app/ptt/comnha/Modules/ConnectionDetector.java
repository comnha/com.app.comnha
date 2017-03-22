package com.app.ptt.comnha.Modules;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * Created by cuong on 10/16/2016.
 */

public class ConnectionDetector {
    public static boolean isMobileNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static boolean isWifiAvailable(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }


    public static boolean networkStatus(Context mContext) {
        return (ConnectionDetector.isMobileNetworkAvailable(mContext) || ConnectionDetector.isWifiAvailable(mContext));
    }

    public static boolean canGetLocation(Context mContext) {
        try {
            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!isGPSEnabled) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean showSettingGPSAlert(final Context mContext) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS settings");
        alertDialog.setMessage("GPS is not enabled.Do you want to go to setting menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
        return true;
    }
    public static boolean showSettingNetworkAlert(final Context mContext) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Internet settings");
        alertDialog.setMessage("Internet is not enabled. Do you want to go to setting menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
        return true;
    }
    public static boolean showSettingAlertFirstTime(Context mContext) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Alert!!");
        alertDialog.setMessage("Your location has not been saved. Please turn on your GPS and Internet");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
               // mContext.startActivity(intent);
                dialog.cancel();
            }
        }).show();
        return true;
    }

    public static void showNetworkAlert(final Context mContext){
        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setTitle("Network Connection");
        builder.setMessage("Network is not enabled.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    public static void showNoConnectAlert(final Context mContext){
        final AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setTitle("No Connection");
        builder.setMessage("Network and GPS is not enabled.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    public static void showLoadingAlert(final Context mContext){
        final AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setTitle("Loading");
        builder.setMessage("Taking data in the internet");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    public static void showGetLocationError(final Context mContext){
        final AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setTitle("Error!!");
        builder.setMessage("Can't get your location. Try again");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
