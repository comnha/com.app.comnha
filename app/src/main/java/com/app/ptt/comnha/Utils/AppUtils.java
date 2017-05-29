package com.app.ptt.comnha.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.app.ptt.comnha.Const.Const;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NguyenVanCuong on 4/12/2017.
 */

public class AppUtils {
    //show snack bar have a button and title to do some thing
    public static void showSnackbar(final Context c, View view, final String title, String actionTitle, final String type, final int showTime) {
        final Snackbar snackbar = Snackbar.make(view, title, showTime);
        snackbar.setAction(actionTitle, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent snackBarIntent = new Intent();
                snackBarIntent.setAction(type);
                snackBarIntent.putExtra(type, type);
                c.sendBroadcast(snackBarIntent);
                if(showTime==Snackbar.LENGTH_INDEFINITE){
                    snackbar.dismiss();
                }
            }
        });
        snackbar.show();
    }


    public static void showSnackbarWithoutButton(View view, final String title) {

        final Snackbar snackbar = Snackbar.make(view, title, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public static boolean isEqualsNull(EditText editText) {
        return editText.getText().toString().trim().length() <= 0;
    }

    public static boolean isTextEqualsNull(String text) {
        return text.trim().equals("");
    }

    public static String getText(EditText editText) {
        return editText.getText().toString().trim();
    }

    public static void requestPermission(Context context, List<String> listPermission, int type) {
        ActivityCompat.requestPermissions((Activity) context, listPermission.toArray(new String[listPermission.size()]), type);
    }

    public static void showKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static List<String> checkPermissions(Context context, String[] permissions) {
        int result;
        List<String> listPermission = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(context, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermission.add(p);
            }
        }
        return listPermission;
    }

    public static boolean canGetLocation(Context context) {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            return isGPSEnabled;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean isMobileNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return mNetworkInfo.isConnected();
    }

    public static boolean isWifiAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mNetworkInfo.isConnected();
    }

    public static void showToast(Context c, String text, int type) {
        if (type == 1) {
            Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(c, text, Toast.LENGTH_LONG).show();
    }

    public static boolean checkEmptyEdt(EditText edt) {
        return edt.getText().toString().trim().equals("") || edt.getText().toString().isEmpty();
    }

    public static Menu createMenu(Menu menu,
                                  String[] menuitems) {
        for (int i = 0; i < menuitems.length; i++) {
            menu.add(Menu.NONE, i, Menu.NONE, menuitems[i]);
        }
        return menu;
    }

    public static ProgressDialog SetupProgressDialog(Context context, String message,
                                                     String title, boolean cancelable,
                                                     boolean ontouchoutside,
                                                     int progressstyle, int max) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(cancelable);
        progressDialog.setCanceledOnTouchOutside(ontouchoutside);
        progressDialog.setProgressStyle(progressstyle);
        if (title != null) {
            progressDialog.setTitle(title);
        }
        if (message != null) {
            progressDialog.setMessage(message);
        }
        if (progressstyle == ProgressDialog.STYLE_HORIZONTAL) {
            progressDialog.setMax(max);
        }
        return progressDialog;
    }

    public static int getStatusBarHeight(Activity activity) {
        Rect rectangle = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static void setStatusBarTranslucent(Activity activity) {
//        View v = findViewById(R.id.bellow_actionbar);
//        if (v != null) {
//            int paddingTop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ?
//                    MyScreenUtils.getStatusBarHeight(activity) : 0;
//            TypedValue tv = new TypedValue();
//            getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, tv, true);
//            paddingTop += TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
//            v.setPadding(0, makeTranslucent ? paddingTop : 0, 0, 0);
//        }
//
//        if (makeTranslucent) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        } else {
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
    }
//    public  boolean showSettingGPSAlert() {
//
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//        alertDialog.setTitle("GPS settings");
//        alertDialog.setMessage("GPS is not enabled.Do you want to go to setting menu?");
//        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//            }
//        });
//        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        alertDialog.show();
//        return true;
//    }
//    public boolean showSettingNetworkAlert(final Context c) {
//
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);
//        alertDialog.setTitle("Kết nối ");
//        alertDialog.setMessage("Internet is not enabled. Do you want to go to setting menu?");
//        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(Settings.ACTION_SETTINGS);
//                c.startActivity(intent);
//            }
//        });
//        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        alertDialog.show();
//        return true;
//    }
//    public  boolean showSettingAlertFirstTime() {
//
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//        alertDialog.setTitle("Alert!!");
//        alertDialog.setMessage("Your location has not been saved. Please turn on your GPS and Internet");
//        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                // mContext.startActivity(intent);
//                dialog.cancel();
//            }
//        }).show();
//        return true;
//    }
//
//    public  void showNetworkAlert(){
//        AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        builder.setTitle("Network Connection");
//        builder.setMessage("Network is not enabled.");
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        builder.show();
//    }
//    public  void showNoConnectAlert(){
//        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        builder.setTitle("No Connection");
//        builder.setMessage("Network and GPS is not enabled.");
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        builder.show();
//    }
//    public  void showLoadingAlert(){
//        AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        builder.setTitle("Loading");
//        builder.setMessage("Taking data in the internet");
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        builder.show();
//    }
//    public  void showGetLocationError(){
//        AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        builder.setTitle("Error!!");
//        builder.setMessage("Can't get your location. Try again");
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        builder.show();
//    }
}
