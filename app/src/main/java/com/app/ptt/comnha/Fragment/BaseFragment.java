package com.app.ptt.comnha.Fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import static com.app.ptt.comnha.Utils.AppUtils.showSnackbar;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by NguyenVanCuong on 4/12/2017.
 */

public class BaseFragment extends Fragment {
    public static final int MULTIPLE_PERMISSIONS = 10; // cod
    protected boolean isGrantedAll = true;
    protected boolean isNetworkConnected = false, isLocationConnected = false;
    protected DatabaseReference dbRef;
    protected StorageReference storeRef;
    protected FirebaseAuth auth;
    IntentFilter mIntentFilter;
    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION};

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
    public boolean passWordLenght(String pass){
        return pass.length() >= 8;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);


    }

    ProgressDialog progressDialog;

    protected void closeDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    protected void showProgressDialog(Context context, String tilte, String message) {
        if (progressDialog == null) {
            if (tilte.equals("")) {
                tilte = getString(R.string.txt_plzwait);
            }
            progressDialog = ProgressDialog.show(context,
                    tilte,
                    message, true, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(broadcastReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        isNetworkConnected = MyService.returnIsNetworkConnected();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Const.BROADCAST_SEND_STATUS_INTERNET);
        mIntentFilter.addAction(Const.SNACKBAR_GO_ONLINE);

        auth=FirebaseAuth.getInstance();
        Firebase.setAndroidContext(getContext());
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_path));
    }
    @Override
    public void onStop() {
        super.onStop();

    }
    protected void handleProgressDialog(final String alertText) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && isShowProgress()) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), alertText, Toast.LENGTH_SHORT).show();
                }
            }
        }, 15000);
    }


    protected boolean isShowProgress() {
        return progressDialog.isShowing();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Const.BROADCAST_SEND_STATUS_INTERNET)) {
                if (intent.getExtras().containsKey(Const.BROADCAST_SEND_STATUS_INTERNET)) {
                    if (intent.getBooleanExtra(Const.BROADCAST_SEND_STATUS_INTERNET, false)) {
                        isNetworkConnected = true;
                    }
                    if (!intent.getBooleanExtra(Const.BROADCAST_SEND_STATUS_INTERNET, false)) {
                        isNetworkConnected = false;
                        showSnackbar(getActivity(), getView(), getString(R.string.text_not_internet), getString(R.string.text_connect), Const.SNACKBAR_GO_ONLINE, Snackbar.LENGTH_SHORT);

                    }
                }
                if (intent.getExtras().containsKey(Const.BROADCAST_SEND_STATUS_GET_LOCATION)) {
                    if (intent.getBooleanExtra(Const.BROADCAST_SEND_STATUS_GET_LOCATION, false)) {
                        isLocationConnected = true;
                    }
                    if (!intent.getBooleanExtra(Const.BROADCAST_SEND_STATUS_GET_LOCATION, false)) {
                        isLocationConnected = false;


                    }
                }


            }

            if (intent.getAction().equals(Const.SNACKBAR_GO_ONLINE)) {
                Intent intentSetting = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intentSetting);
            }
        }
    };
}
