package com.app.ptt.comnha.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.R;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    TextView dot1, dot2, dot3, dot4, dot5, dot6;
    ImageView imgLogo;
    public static final int MULTIPLE_PERMISSIONS = 10; // cod
    private static int SPLASH_TIME_OUT = 3000;
    String[] permissions= new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION};
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
        checkPermissions();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);


    }


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

                } else {

                }
                return;
            }
        }
    }

}
