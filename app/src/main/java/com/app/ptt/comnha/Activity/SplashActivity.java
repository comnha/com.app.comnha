package com.app.ptt.comnha.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.Modules.ConnectionDetector;
import com.app.ptt.comnha.Modules.Storage;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Modules.MyTool;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    TextView dot1, dot2, dot3, dot4, dot5, dot6;
    ImageView imgLogo;
    private static final String LOG = SplashActivity.class.getSimpleName();

    public static final String mBroadcastSendAddress1 = "mBroadcastSendAddress1";
    private static int SPLASH_TIME_OUT = 3000;


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // myLocation=new MyLocation();
        setContentView(R.layout.activity_splash);
        dot1 = (TextView) findViewById(R.id.act_splash_dot1);
        dot2 = (TextView) findViewById(R.id.act_splash_dot2);
        dot3 = (TextView) findViewById(R.id.act_splash_dot3);
        dot4 = (TextView) findViewById(R.id.act_splash_dot4);
        dot5 = (TextView) findViewById(R.id.act_splash_dot5);
        dot6 = (TextView) findViewById(R.id.act_splash_dot6);
        imgLogo = (ImageView) findViewById(R.id.act_splash_imglogo);
        AnimationUtils.animateTransTrip(dot1, dot2, dot3, dot4, dot5, dot6);
        AnimationUtils.animateTransAlpha(imgLogo);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                i.putExtra("isConnected", true);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
