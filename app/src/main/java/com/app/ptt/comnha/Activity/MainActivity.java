package com.app.ptt.comnha.Activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.FireBase.Account;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.Fragment.AboutBottomSheetDialogFragment;
import com.app.ptt.comnha.Fragment.ChangeLocationBottomSheetDialogFragment;
import com.app.ptt.comnha.Fragment.FilterFragment;
import com.app.ptt.comnha.Fragment.ReviewFragment;
import com.app.ptt.comnha.Fragment.StoreFragment;
import com.app.ptt.comnha.Modules.MyTool;
import com.app.ptt.comnha.Modules.Storage;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.firebase.client.Firebase;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , FloatingActionButton.OnClickListener {

    private static final String LOG = MainActivity.class.getSimpleName();
    private Bundle savedInstanceState;
    private ProgressDialog progressDialog;
    private IntentFilter mIntentFilter;
    private MyLocation myLocation;
    private ProgressDialog logoutDialog;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference dbRef;
    ValueEventListener profileValueEventListener;
    public String userID, username, email;
    private String tinh = "", huyen = "";
    private Toolbar mtoolbar;
    private DrawerLayout mdrawer;
    private ActionBarDrawerToggle mtoggle;
    private NavigationView mnavigationView;
    private TextView txt_email, txt_un;
    private FloatingActionMenu fabmenu;
    int a = 0;
    boolean role;
    FirebaseUser user;
    MenuItem menuItem, menuItem1, menuItem2, menuItem3, menuItem4;
    private FloatingActionButton fab_review, fab_addloca, fab_changloca;
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    public static final String mBroadcastSendAddress1 = "mBroadcastSendAddress1";
    private Firebase ref;
    boolean isConnected = false;
    private BottomBar bottomBar;
    private PopupMenu popupMenu;
    private MyTool myTool;

    private Dialog dialogLocationSetting;

    private ChangeLocationBottomSheetDialogFragment changeLccaBtmSheet;
    NetworkChangeReceiver mBroadcastReceiver;

    private boolean binded = false;
    private MyService myService;
    //public static boolean temp = false;


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

    public MyLocation returnLocationByLatLng(Double latitude, Double longitude, Geocoder geocoder) {
        MyLocation myLocation = new MyLocation();
        List<Address> addresses;
        Double lat = latitude;
        Double lon = longitude;
        try {
            if (lat != null && lon != null) {
                addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String a = address.getAddressLine(0);
                    String b = address.getSubLocality();
                    String c = address.getSubAdminArea();
                    String d = address.getAdminArea();
                    String e = "";
                    if (a != null) {
                        e += a;
                    }
                    if (b != null) {
                        if (a == null)
                            e += b;
                        else
                            e += ", " + b;

                    }

                    if (c != null) {
                        Scanner kb = new Scanner(c);
                        String name;
                        while (kb.hasNext()) {
                            name = kb.next();
                            if (name.equals("District") || name.equals("Quận")) {
                                c = "Quận";
                                while (kb.hasNext()) {
                                    c += " " + kb.next();
                                }
                            }
                        }
                        if (a == null && b == null)
                            e += c;
                        else
                            e += ", " + c;

                        myLocation.setQuanhuyen(c);
                    }
                    if (d != null) {
                        if (a == null && b == null && c == null)
                            e += d;
                        else
                            e += ", " + d;
                        myLocation.setTinhtp(d);
                    }
                    myLocation.setDiachi(e);
                    myLocation.setLat(lat);
                    myLocation.setLng(lon);
                    Log.i(LOG + ".returnLocationByLatLng", "Location can tim" + myLocation.getDiachi());
                    return myLocation;
                }
                return null;
            }
        } catch (IOException e) {
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginSession.getInstance().setTinh("");
        LoginSession.getInstance().setHuyen("");
        MyService.setUserAccount(null);
        Log.i(LOG, "onCreate");
        myTool = new MyTool(MainActivity.this, MainActivity.class.getSimpleName());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.txt_plzwait));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading");
        showDialogOpenLocationService();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
        mIntentFilter.addAction(mBroadcastSendAddress1);
        mBroadcastReceiver = new NetworkChangeReceiver();
        setContentView(R.layout.activity_main2);
        Intent intent = new Intent(this, MyService.class);
        this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        Firebase.setAndroidContext(this);
        ref = new Firebase(getResources().getString(R.string.firebase_path));
        anhXa();
        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        mdrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mtoggle = new ActionBarDrawerToggle(
                this, mdrawer, mtoolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mdrawer.setDrawerListener(mtoggle);
        mtoggle.syncState();
        mnavigationView = (NavigationView) findViewById(R.id.nav_view);
        mnavigationView.setNavigationItemSelectedListener(this);
        View header = mnavigationView.getHeaderView(0);
        txt_email = (TextView) header.findViewById(R.id.nav_head_email);
        txt_un = (TextView) header.findViewById(R.id.nav_head_username);
        this.savedInstanceState = savedInstanceState;
        Log.i(LOG, "onCreate");
        final Menu menu = mnavigationView.getMenu();
        menuItem = menu.findItem(R.id.nav_profile);
        menuItem1 = menu.findItem(R.id.nav_signin);
        menuItem2 = menu.findItem(R.id.nav_signout);
        menuItem3 = menu.findItem(R.id.nav_admin);
        menuItem4 = menu.findItem(R.id.nav_notification);
        menuItem4.setVisible(false);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    try {
                        if (user.getEmail() != null) {
                            userID = user.getUid();
                            menuItem.setEnabled(true);
                            menuItem1.setEnabled(false);
                            menuItem2.setEnabled(true);
                            menuItem4.setVisible(true);
                            Log.d("signed_in", "onAuthStateChanged:signed_in: " + user.getEmail());
                            LoginSession.getInstance().setUserID(userID);
                            LoginSession.getInstance().setEmail(user.getEmail());
                            LoginSession.getInstance().setUsername(user.getDisplayName());
                            txt_email.setText(user.getEmail());
                            txt_un.setText(user.getDisplayName());
                            getRole();
                        } else {
                            menuItem.setEnabled(false);
                            menuItem2.setEnabled(false);
                            menuItem1.setEnabled(true);
                            menuItem3.setVisible(false);
                            txt_email.setText(getResources().getString(R.string.text_hello));
                            txt_un.setText(getResources().getString(R.string.text_user));
                            userID = "";
                            LoginSession.getInstance().setUserID(null);
                            LoginSession.getInstance().setUsername(null);
                            LoginSession.getInstance().setEmail(null);
                        }
                    } catch (NullPointerException mess) {
                        txt_email.setText(getResources().getString(R.string.text_hello));
                        txt_un.setText(getResources().getString(R.string.text_user));
                        userID = "";
                        LoginSession.getInstance().setUserID(null);
                        LoginSession.getInstance().setUsername(null);
                        LoginSession.getInstance().setEmail(null);
                    }
                } else {
//                    txt_email.setText(getResources().getString(R.string.text_hello));
//                    txt_un.setText(getResources().getString(R.string.text_user));
//                    userID = "";
//                    LoginSession.getInstance().setUserID(null);
//                    LoginSession.getInstance().setUsername(null);
//                    LoginSession.getInstance().setEmail(null);
////                    Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_SHORT).show();
//                    Log.d("signed_out", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    public void getRole() {
        role = false;
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        profileValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Account account = dataSnapshot.getValue(Account.class);
                role = account.getRole();
                account.setId(dataSnapshot.getKey());
                account.setUsername(user.getDisplayName());
                LoginSession.getInstance().setRole(role);
                MyService.setUserAccount(account);
                if (role) {
                    menuItem3.setVisible(true);
                } else menuItem3.setVisible(false);
                Log.i("Role", "" + role);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getResources().getString(R.string.users_CODE) +//liệt kê tất cả
                LoginSession.getInstance().getUserID()).addListenerForSingleValueEvent(profileValueEventListener);
        dbRef.removeEventListener(profileValueEventListener);
        Log.i("Role1", "" + role);
    }


    void anhXa() {
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        fabmenu = (FloatingActionMenu) findViewById(R.id.main_fabMenu);
        fab_review = (FloatingActionButton) findViewById(R.id.main_fabitem3);
        fab_addloca = (FloatingActionButton) findViewById(R.id.main_fabitem2);
        fab_changloca = (FloatingActionButton) findViewById(R.id.main_fabitem1);
        // progressDialog.show();
        //request();
    }

    public void bottomBarEvent() {
        fab_review.setOnClickListener(this);
        fab_addloca.setOnClickListener(this);
        fab_changloca.setOnClickListener(this);
        fabmenu.setClosedOnTouchOutside(true);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                FragmentTransaction transaction;
                switch (tabId) {
                    case R.id.tab_reviews:
                        fabmenu.close(true);
                        ReviewFragment reviewFragment = new ReviewFragment();
                        reviewFragment.setContext(getApplicationContext());
                        reviewFragment.setSortType(1);
                        reviewFragment.setTinh(LoginSession.getInstance().getTinh());
                        reviewFragment.setHuyen(LoginSession.getInstance().getHuyen());
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame, reviewFragment);
                        transaction.commit();
                        AnimationUtils.animatfabMenuIn(fabmenu);
                        break;
                    case R.id.tab_stores:
                        fabmenu.close(true);
                        StoreFragment storeFragment = new StoreFragment();
                        storeFragment.setFilter(1);
                        storeFragment.setTinh(LoginSession.getInstance().getTinh());
                        storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                        storeFragment.setContext(getApplicationContext());
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame, storeFragment);
                        transaction.commit();
                        AnimationUtils.animatfabMenuIn(fabmenu);

                        break;
                    case R.id.tab_locations:
                        FilterFragment filterFragment = new FilterFragment();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame, filterFragment);
                        transaction.commit();
                        AnimationUtils.animatfabMenuOut(fabmenu);
                        fabmenu.close(true);
                        break;
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_reviews:
                        popupMenu = new PopupMenu(MainActivity.this, findViewById(R.id.tab_reviews), Gravity.END);
                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_viewpost, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.popup_viewpost_lastnews:
                                        ReviewFragment reviewFragment = new ReviewFragment();
                                        reviewFragment.setSortType(1);
                                        reviewFragment.setTinh(LoginSession.getInstance().getTinh());
                                        reviewFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                        reviewFragment.setContext(getApplicationContext());
                                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                        transaction.replace(R.id.frame, reviewFragment);
                                        transaction.commit();

                                        break;
                                    case R.id.popup_viewpost_mostcomment:

                                        ReviewFragment reviewFragment1 = new ReviewFragment();

                                        reviewFragment1.setSortType(2);
                                        reviewFragment1.setContext(getApplicationContext());
                                        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                                        transaction1.replace(R.id.frame, reviewFragment1);
                                        reviewFragment1.setTinh(LoginSession.getInstance().getTinh());
                                        reviewFragment1.setHuyen(LoginSession.getInstance().getHuyen());
                                        transaction1.commit();
                                        break;
                                    case R.id.popup_viewpost_mostlike:
                                        ReviewFragment reviewFragment2 = new ReviewFragment();
                                        reviewFragment2.setSortType(3);
                                        reviewFragment2.setTinh(LoginSession.getInstance().getTinh());
                                        reviewFragment2.setHuyen(LoginSession.getInstance().getHuyen());
                                        reviewFragment2.setContext(getApplicationContext());
                                        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                                        transaction2.replace(R.id.frame, reviewFragment2);
                                        transaction2.commit();

                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();

                        break;
                    case R.id.tab_stores:
                        popupMenu = new PopupMenu(MainActivity.this, findViewById(R.id.tab_stores), Gravity.CENTER);
                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_viewquan, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                FragmentTransaction transaction;
                                StoreFragment storeFragment;
                                switch (item.getItemId()) {
                                    case R.id.popup_viewquan_none:
                                        storeFragment = new StoreFragment();
                                        storeFragment.setFilter(1);
                                        storeFragment.setTinh(LoginSession.getInstance().getTinh());
                                        storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                        storeFragment.setContext(getApplicationContext());
                                        transaction = getSupportFragmentManager().beginTransaction();
                                        transaction.replace(R.id.frame, storeFragment);
                                        transaction.commit();

                                        break;
                                    case R.id.popup_viewquan_gia:

                                        storeFragment = new StoreFragment();

                                        storeFragment.setTinh(LoginSession.getInstance().getTinh());
                                        storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                        storeFragment.setContext(getApplicationContext());
                                        storeFragment.setFilter(2);
                                        transaction = getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.frame, storeFragment);
                                        transaction.commit();

                                        break;
                                    case R.id.popup_viewquan_pv:
                                        storeFragment = new StoreFragment();

                                        storeFragment.setTinh(LoginSession.getInstance().getTinh());
                                        storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                        storeFragment.setContext(getApplicationContext());
                                        storeFragment.setFilter(3);
                                        transaction = getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.frame, storeFragment);
                                        transaction.commit();
                                        break;
                                    case R.id.popup_viewquan_vs:
                                        storeFragment = new StoreFragment();
                                        storeFragment.setTinh(LoginSession.getInstance().getTinh());
                                        storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                        storeFragment.setContext(getApplicationContext());
                                        storeFragment.setFilter(4);
                                        transaction = getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.frame, storeFragment);
                                        transaction.commit();

                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                        break;
                    case R.id.tab_locations:
//                        popupMenu = new PopupMenu(MainActivity.this, findViewById(R.id.tab_locations), Gravity.START);
//                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_locafilter, popupMenu.getMenu());
//                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                            @Override
//                            public boolean onMenuItemClick(MenuItem item) {
//                                switch (item.getItemId()) {
//                                    case R.id.popup_locafilter_myloca:
//                                        break;
//                                    case R.id.popup_locafilter_choseloca:
//                                        break;
//                                }
//                                return true;
//                            }
//                        });
//                        popupMenu.show();
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        isConnected = MyService.returnIsConnected();
        Log.i(LOG, "onStart= " + isConnected);
        registerReceiver(mBroadcastReceiver, mIntentFilter);
        mAuth.addAuthStateListener(mAuthListener);
        try {
            if (mAuth.getCurrentUser() == null) {
                mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("signInAnonymously", "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("signInAnonymouslyError", "signInAnonymously", task.getException());
//                            Toast.makeText(MainActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            if (mAuth.getCurrentUser().getEmail() == null) {
                mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("signInAnonymously", "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("signInAnonymouslyError", "signInAnonymously", task.getException());
                        }
                    }
                });
            }
        } catch (NullPointerException mess) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(LOG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mBroadcastReceiver);
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        Log.i(LOG, "onStop");

    }

    @Override
    protected void onDestroy() {
        Log.i(LOG, "onDestroy");
        super.onDestroy();
        if (binded) {
            this.unbindService(serviceConnection);
            binded = false;
        }
        // Storage.deleteFile(getApplicationContext(),"myLocation");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG, "Pause");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Bạn có muốn thoát?")
                    .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_profile:
                Intent intent = new Intent(MainActivity.this, Adapter2Activity.class);
                intent.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frg_prodetail_CODE));
                startActivity(intent);
                break;
            case R.id.nav_homepage:
                if (bottomBar.getCurrentTabPosition() != 0) {
                    bottomBar.selectTabAtPosition(0);
                }
                break;
            case R.id.nav_notification:
                Intent intent_notification = new Intent(MainActivity.this, Adapter2Activity.class);
                intent_notification.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frg_notification_CODE));
                startActivity(intent_notification);
                break;
            case R.id.nav_about:
                AboutBottomSheetDialogFragment aboutBottomSheetDialogFragment
                        = new AboutBottomSheetDialogFragment();
                aboutBottomSheetDialogFragment.show(getSupportFragmentManager(),
                        getString(R.string.frag_about_CODE));
                break;
            case R.id.nav_admin:
                try {
                    if (LoginSession.getInstance().getRole()) {
                        Intent intent3 = new Intent(this, AdminActivity.class);
                        startActivity(intent3);
                    } else {
                        Toast.makeText(this, "Bạn không thể thực hiện chức năng này", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException mess) {
                    Toast.makeText(this, "Bạn không thể thực hiện chức năng này", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_signin:
                Intent intent1 = new Intent(MainActivity.this, Adapter2Activity.class);
                intent1.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frg_signin_CODE));
                intent1.putExtra("isConnected", isConnected);
                startActivity(intent1);
                break;
            case R.id.nav_signout:
                if (isConnected) {
                    logoutDialog = ProgressDialog.show(this,
                            getResources().getString(R.string.txt_plzwait),
                            getResources().getString(R.string.txt_logginout), true, false);
                    mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("signInAnonymously", "signInAnonymously:onComplete:" + task.isSuccessful());
                            logoutDialog.dismiss();
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w("signInAnonymouslyError", "signInAnonymously", task.getException());
                            } else {
                                MyService.setUserAccount(null);
                                LoginSession.getInstance().setTen(null);
                                LoginSession.getInstance().setHo(null);
                                LoginSession.getInstance().setTenlot(null);
                                LoginSession.getInstance().setNgaysinh(null);
                                LoginSession.getInstance().setPassword(null);
//                                menuItem3.setVisible(false);
//                                menuItem1.setEnabled(true);
//                                menuItem.setEnabled(false);
//                                menuItem2.setEnabled(false);
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "You are offline", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_map:
                // if (isConnected) {
                Intent intent2 = new Intent(MainActivity.this, AdapterActivity.class);
                intent2.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frag_map_CODE));
                intent2.putExtra("isConnected", isConnected);
                startActivity(intent2);
                //   }
                break;
        }
        mdrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mdrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_fabitem1:
                changeLccaBtmSheet = new ChangeLocationBottomSheetDialogFragment();
                changeLccaBtmSheet.show(getSupportFragmentManager(), "fragment_changeLocal");
                changeLccaBtmSheet.setOnChangeLocationListenner(new ChangeLocationBottomSheetDialogFragment.OnChangeLocationListenner() {
                    @Override
                    public void onChangeLocation(String Province, String District) {
                        LoginSession.getInstance().setTinh(Province);
                        fab_changloca.setLabelText(
                                Province + ", " + District
                        );
                        LoginSession.getInstance().setHuyen(District);
                        fabmenu.close(true);
                        FragmentTransaction transaction;
                        switch (bottomBar.getCurrentTabPosition()) {
                            case 0:
                                ReviewFragment reviewFragment = new ReviewFragment();
                                reviewFragment.setTinh(LoginSession.getInstance().getTinh());
                                reviewFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                reviewFragment.setSortType(1);
                                reviewFragment.setContext(getApplicationContext());
                                transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.frame, reviewFragment);
                                transaction.commit();
                                break;
                            case 1:
                                StoreFragment storeFragment = new StoreFragment();
                                storeFragment.setFilter(1);
                                storeFragment.setTinh(LoginSession.getInstance().getTinh());
                                storeFragment.setContext(getApplicationContext());
                                storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.frame, storeFragment);
                                transaction.commit();
                                break;
                            case 2:
                                break;
                        }
                    }

                    @Override
                    public void onChangetoMylocation(boolean isMylocation) {
                        if (isMylocation) {
                            LoginSession.getInstance().setHuyen(myLocation.getQuanhuyen());
                            LoginSession.getInstance().setTinh(myLocation.getTinhtp());
                            fab_changloca.setLabelText(LoginSession.getInstance().getHuyen() + ", " +
                                    LoginSession.getInstance().getTinh());
                            fabmenu.close(true);
                            FragmentTransaction transaction;
                            switch (bottomBar.getCurrentTabPosition()) {
                                case 0:
                                    ReviewFragment reviewFragment = new ReviewFragment();
                                    reviewFragment.setTinh(LoginSession.getInstance().getTinh());
                                    reviewFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                    reviewFragment.setSortType(1);
                                    reviewFragment.setContext(getApplicationContext());
                                    transaction = getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame, reviewFragment);
                                    transaction.commit();
                                    break;
                                case 1:
                                    // if (isConnected) {
                                    StoreFragment storeFragment = new StoreFragment();
                                    storeFragment.setFilter(1);
                                    storeFragment.setTinh(LoginSession.getInstance().getTinh());
                                    storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                    storeFragment.setContext(getApplicationContext());
                                    transaction = getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame, storeFragment);
                                    transaction.commit();
                                    //  } //else startGetLocation();
                                    break;
                                case 2:
                                    break;
                            }
                        }
                    }
                });
                break;
            case R.id.main_fabitem2:
                if (isConnected) {
                    if (LoginSession.getInstance().getUserID() == null) {
                        Toast.makeText(this, getString(R.string.txt_needlogin),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(MainActivity.this, Adapter2Activity.class);
                        intent.putExtra(getString(R.string.fragment_CODE),
                                getString(R.string.frag_addloca_CODE));
                        intent.putExtra("isConnected", isConnected);
                        startActivity(intent);
                    }
                } else
                    Toast.makeText(this, "You are offline", Toast.LENGTH_LONG).show();
                break;
            case R.id.main_fabitem3:
                if (isConnected) {
                    if (LoginSession.getInstance().getUserID() == null) {
                        Toast.makeText(this, getString(R.string.txt_needlogin),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent1 = new Intent(MainActivity.this, Adapter2Activity.class);
                        intent1.putExtra(getString(R.string.fragment_CODE),
                                getString(R.string.frag_addpost_CODE));
                        intent1.putExtra("isConnected", isConnected);
                        startActivity(intent1);
                    }
                } else
                    Toast.makeText(this, "You are offline", Toast.LENGTH_LONG).show();
                break;

        }
    }


    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            // Log.i(LOG + ".NetworkChangeReceiver", "isConnected splash " + temp);
            if (intent.getAction().equals(mBroadcastSendAddress)) {
                if (intent.getBooleanExtra("isConnected", false)) {
                    isConnected = true;
                } else {
                    isConnected = false;
                    //showDialogOpenLocationService();
                }
                if(myLocation==null) {
                    ArrayList<MyLocation> locations;
                    String a = Storage.readFile(getApplicationContext(), "myLocation");
                    if (a != null) {
                        locations = Storage.readJSONMyLocation(a);
                        if (locations.size() > 0)
                            myLocation = locations.get(0);
                        else {
                            myLocation = null;
                        }
                        if (LoginSession.getInstance().getHuyen() == "" && LoginSession.getInstance().getTinh() == "") {
                            tinh = myLocation.getTinhtp();
                            huyen = myLocation.getQuanhuyen();
                            LoginSession.getInstance().setTinh(myLocation.getTinhtp());
                            LoginSession.getInstance().setHuyen(myLocation.getQuanhuyen());
                            fab_changloca.setLabelText(
                                    LoginSession.getInstance().getHuyen() + ", "
                                            + LoginSession.getInstance().getTinh()
                            );
                            Log.i(LOG + ".NetworkChangeReceiver", "myLocation != null");
                        }
                        bottomBarEvent();
                    } else {
                        //myTool.startGoogleApi();
                    }
                }
                if (intent.getIntExtra("STT", 0) == 2) {
                    Log.i(LOG + ".NetworkChangeReceiver", "Nhan vi tri cua ban:");
                    try {
                        myLocation = myTool.getYourLocation();
                        Storage.deleteFile(getApplicationContext(), "myLocation");
                    } catch (Exception e) {
                    }
                    if (myLocation != null) {
                        ArrayList<MyLocation> list = new ArrayList<>();
                        list.add(myLocation);
                        if (Storage.parseMyLocationToJson(list).toString() != null) {
                            Storage.writeFile(getApplicationContext(), Storage.parseMyLocationToJson(list).toString(), "myLocation");
                        }
                        if (myTool.isGoogleApiConnected())
                            myTool.stopLocationUpdate();
                    } else {
                        Toast.makeText(context, "Không lấy được vị trí của bạn. Vui lòng kiểm tra lại mạng, gps và khởi đô", Toast.LENGTH_SHORT).show();
                    }


                }

//                if (isNetworkAvailable(context) && canGetLocation(context)) {
//                    Log.i(LOG + ".NetworkChangeReceiver", "isNetworkAvailable(context) && canGetLocation(context) ");
//                    if (myLocation ==null) {
//                        Log.i(LOG + ".NetworkChangeReceiver", "myLocation == null");
//                        myTool.startGoogleApi();
//                    }
//                    isConnected = true;
//                } else {
//                    Log.i(LOG + ".NetworkChangeReceiver","!(isNetworkAvailable(context) && canGetLocation(context)) ");
//                    showDialogOpenLocationService();
//
//                }
            }

        }
    }

    public void showDialogOpenLocationService() {
        if (dialogLocationSetting != null && dialogLocationSetting.isShowing()) {
            return;
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        try {
            gpsEnabled = lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
            networkEnabled =
                    lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
            if (gpsEnabled==false) {
                // notify user
                android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(this);
                alertBuilder.setCancelable(false);
                alertBuilder.setTitle("GPS");
                alertBuilder.setMessage("Bật GPS để sử dụng ứng dụng");
                alertBuilder.setPositiveButton("Đến GPS Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });

                dialogLocationSetting = alertBuilder.create();
                dialogLocationSetting.show();
            }
            if (networkEnabled==false) {
                // notify user
                android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(this);
                alertBuilder.setCancelable(false);
                alertBuilder.setTitle("Internet");
                alertBuilder.setMessage("Bật Internet để sử dụng ứng dụng");
                alertBuilder.setPositiveButton("Đến Internet Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(myIntent);
                    }
                });

                dialogLocationSetting = alertBuilder.create();
                dialogLocationSetting.show();
            }
        } catch (Exception ex) {
        }


    }

}
