package com.app.ptt.comnha.Activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.app.ptt.comnha.Adapters.MainFragPagerAdapter;
import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Fragment.AboutBottomSheetDialogFragment;
import com.app.ptt.comnha.Interfaces.Comunication;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.Models.MyLocation;
import com.app.ptt.comnha.Modules.ConnectionDetector;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.CoreManager;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.AppUtils;
import com.app.ptt.comnha.Utils.LocationController;
import com.app.ptt.comnha.Utils.MyTool;
import com.app.ptt.comnha.Utils.Storage;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, LocationController.LocationControllerListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String tinh = "", huyen = "";
    private Toolbar mtoolbar;
    private DrawerLayout mdrawer;
    private ActionBarDrawerToggle mtoggle;
    private NavigationView mnavigationView;
    private TextView txt_email, txt_un;
    private CircularImageView imgv_avatar;
    private FloatingActionMenu fabmenu;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MainFragPagerAdapter pagerAdapter;
    private FloatingActionButton fab;
    private int connectionStatus = -1;
    private Snackbar snackbar;
    private MenuItem itemProfile, itemAdmin, itemSignIn, itemSignOut, itemMap, itemSetting;
    NestedScrollView nestedScrollView;
    AlertDialog.Builder alertDialog;
    private MyTool myTool;
    View guideView;

    private View posttabview,
            storetabview,
            notifytabview;
    boolean isPostRefreshed = true,
            isStoretRefreshed = true,
            isNotitRefreshed = true;
    private StorageReference stRef;
    private DatabaseReference dbRef;
    private ValueEventListener userValueListener;
    private User user;
    private LocationController locationController;
    private NetworkChangeReceiver mBroadcastReceiver;
    private IntentFilter mIntentFilter;
    private Intent broadcastIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Const.DATABASE_PATH);
        stRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Const.STORAGE_PATH);
        CoreManager.getInstance().initData(this);
        myTool = new MyTool(this);
        ref();
        startMyService();
        initMenu();

    }


    private void getUser() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    txt_email.setText(user.getEmail());
                    txt_un.setText(user.getDisplayName());
                    Picasso.with(getApplicationContext())
                            .load(user.getPhotoUrl())
                            .placeholder(R.drawable.ic_logo)
                            .into(imgv_avatar);
                    getUserInfo(user);
                    itemSignIn.setVisible(false);
                    itemSignOut.setVisible(true);
                    itemProfile.setVisible(true);
                    itemAdmin.setVisible(false);
                    Log.d("onAuthStateChanged", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("onAuthStateChanged", "onAuthStateChanged:signed_out");
                    LoginSession.getInstance().setFirebUser(null);
                    LoginSession.getInstance().setUser(null);
                    txt_email.setText(null);
                    txt_un.setText(null);
                    itemSignIn.setVisible(true);
                    itemSignOut.setVisible(false);
                    itemProfile.setVisible(false);
                    itemAdmin.setVisible(false);
                    imgv_avatar.setImageResource(R.drawable.ic_logo);
                    closeDialog();
                    AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), getString(R.string.text_signout_success));

                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void getUserInfo(final FirebaseUser firebaseUser) {
        userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    user = dataSnapshot.getValue(User.class);
                    String key = firebaseUser.getUid();
                    user.setuID(key);
                    if (user.getRole() > 0) {
                        itemAdmin.setVisible(true);
                    } else {
                        itemAdmin.setVisible(false);
                    }
                    LoginSession.getInstance().setUser(user);
                    LoginSession.getInstance().setFirebUser(firebaseUser);
                    mAuth.removeAuthStateListener(mAuthListener);
                } catch (Exception e) {
                    mAuth.signOut();
                    getUser();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef.child(getString(R.string.users_CODE)
                + firebaseUser.getUid())
                .addListenerForSingleValueEvent(userValueListener);
    }

    private void ref() {

        mtoolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mtoolbar);
        mdrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mtoggle = new ActionBarDrawerToggle(
                this, mdrawer, mtoolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mdrawer.setDrawerListener(mtoggle);
        mtoggle.syncState();
        mnavigationView = (NavigationView) findViewById(R.id.nav_view);
        mnavigationView.setNavigationItemSelectedListener(this);
        fab = (FloatingActionButton) findViewById(R.id.fab_main);
        View header = mnavigationView.getHeaderView(0);
        txt_email = (TextView) header.findViewById(R.id.txtv_email_nav_head);
        txt_un = (TextView) header.findViewById(R.id.txtv_un_nav_head);
        imgv_avatar = (CircularImageView) header.findViewById(R.id.imgv_avatar_nav_head);

        pagerAdapter = new MainFragPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewpager_main);
        viewPager.setAdapter(pagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_main);
        tabLayout.setupWithViewPager(viewPager);
        posttabview = LayoutInflater.from(this).inflate(R.layout.tabs_main, null);
        posttabview.findViewById(R.id.imgV_tabs_main).setBackgroundResource(R.drawable.ic_tab_post);
//        tabLayout.getTabAt(0).setIcon(R.drawable.ic_main_posts);
        tabLayout.getTabAt(0).setCustomView(posttabview);
        storetabview = LayoutInflater.from(this).inflate(R.layout.tabs_main, null);
        storetabview.findViewById(R.id.imgV_tabs_main).setBackgroundResource(R.drawable.ic_tab_store);
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_main_stores);
        tabLayout.getTabAt(1).setCustomView(storetabview);
        notifytabview = LayoutInflater.from(this).inflate(R.layout.tabs_main, null);
        notifytabview.findViewById(R.id.imgV_tabs_main).setBackgroundResource(R.drawable.ic_tab_notify);
//        tabLayout.getTabAt(2).setIcon(R.drawable.ic_main_notify);
        tabLayout.getTabAt(2).setCustomView(notifytabview);

        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_main);
        nestedScrollView.setFillViewport(true);
        long scaletime = 200;
//        fab.setPressed(false);
        fab.setImageResource(R.drawable.ic_write_color_white_24dp);
        fab.setColorNormal(getResources().getColor(R.color.admin_color_selection_news));
        fab.setColorPressed(getResources().getColor(R.color.admin_color_selection_news));

        final ObjectAnimator scalefabX = ObjectAnimator.ofFloat(fab, "scaleX", 1, 0, 1),
                scalefabY = ObjectAnimator.ofFloat(fab, "scaleY", 1, 0, 1),
                collapsefabX = ObjectAnimator.ofFloat(fab, "scaleX", 1, 0),
                collapsefabY = ObjectAnimator.ofFloat(fab, "scaleY", 1, 0),
                expandfabX = ObjectAnimator.ofFloat(fab, "scaleX", 0, 1),
                expandfabY = ObjectAnimator.ofFloat(fab, "scaleY", 0, 1);
        scalefabX.setDuration(scaletime);
        scalefabY.setDuration(scaletime);
        collapsefabX.setDuration(scaletime / 2);
        collapsefabY.setDuration(scaletime / 2);
        expandfabX.setDuration(scaletime / 2);
        expandfabY.setDuration(scaletime / 2);

        posttabview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabLayout.getTabAt(0).select();
                if (!isPostRefreshed) {
                    AnimationUtils.getInstance()
                            .animateHideNotify(posttabview, 100);
                    isPostRefreshed = true;
                }
            }
        });
        storetabview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabLayout.getTabAt(1).select();
                if (!isStoretRefreshed) {
                    AnimationUtils.getInstance()
                            .animateHideNotify(storetabview, 100);
                    isStoretRefreshed = true;
                }
            }
        });
        notifytabview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabLayout.getTabAt(2).select();
                if (!isNotitRefreshed) {
                    AnimationUtils.getInstance()
                            .animateHideNotify(notifytabview, 100);
                    isNotitRefreshed = true;
                }
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("onTabSelected", tab.getPosition() + "");

                int cx = 0, cy = viewPager.getTop();
                int r = viewPager.getRight(), l = viewPager.getLeft();
                long duration = 400;
                switch (tab.getPosition()) {
                    case 0:
                        cx = l;
//                        AnimationUtils.getInstance().createOpenCR(viewPager, duration,
//                                cx, cy);
                        scalefabX.start();
                        scalefabY.start();
                        fab.setColorNormal(getResources()
                                .getColor(R.color.admin_color_selection_news));
                        fab.setColorPressed(getResources()
                                .getColor(R.color.admin_color_selection_news));
                        fab.setImageResource(R.drawable.ic_write_color_white_24dp);
                        break;
                    case 1:
                        cx = (l + r) / 2;
//                        AnimationUtils.getInstance().createOpenCR(viewPager, duration,
//                                cx, cy);
                        if (fab.getScaleX() == 0) {
                            fab.setVisibility(View.VISIBLE);
//                            fab.setScaleX(0);
//                            fab.setScaleY(0);
                            expandfabX.start();
                            expandfabY.start();
                            fab.setColorNormal(getResources()
                                    .getColor(R.color.color_selection_report));
                            fab.setColorPressed(getResources()
                                    .getColor(R.color.color_selection_report));
                            fab.setImageResource(R.drawable.ic_add_location_white_24dp);
                        } else {
                            scalefabX.start();
                            scalefabY.start();
                            fab.setColorNormal(getResources()
                                    .getColor(R.color.color_selection_report));
                            fab.setColorPressed(getResources()
                                    .getColor(R.color.color_selection_report));
                            fab.setImageResource(R.drawable.ic_add_location_white_24dp);
                        }
                        break;
                    case 2:
                        cx = r;
//                        AnimationUtils.getInstance().createOpenCR(viewPager, duration,
//                                cx, cy);
                        collapsefabX.start();
                        collapsefabY.start();
                        collapsefabX.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                fab.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d("onTabUnselected", tab.getPosition() + "");
                switch (tab.getPosition()) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        fab.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        fab.setOnClickListener(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.action_search_main:
                Intent intent_openSearch = new Intent(this, SearchActivity.class);
                startActivity(intent_openSearch);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_profile:
                Intent intent = new Intent(MainActivity.this, ProfiledetailActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_about:
                AboutBottomSheetDialogFragment aboutBottomSheetDialogFragment
                        = new AboutBottomSheetDialogFragment();
                aboutBottomSheetDialogFragment.show(getSupportFragmentManager(),
                        getString(R.string.frag_about_CODE));
                break;
            case R.id.nav_admin:
                Intent intent_admin = new Intent(this, AdminActivity.class);
                startActivity(intent_admin);
                break;
            case R.id.nav_signin:
                Intent intent1 = new Intent(MainActivity.this, AdapterActivity.class);
                intent1.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frg_signin_CODE));
                startActivity(intent1);
                break;
            case R.id.nav_signout:
                showProgressDialog(getString(R.string.txt_plzwait), getString(R.string.txt_logginout));
                CountDownTimer countDownTimer = new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }
                    @Override
                    public void onFinish() {
                        mAuth.signOut();
                        txt_email.setText(null);
                        txt_un.setText(null);
                        itemSignIn.setVisible(true);
                        itemSignOut.setVisible(false);
                        itemProfile.setVisible(false);
                        itemAdmin.setVisible(false);
                        imgv_avatar.setImageResource(R.drawable.ic_logo);
                        getUser();
                    }
                };
                countDownTimer.start();


                break;
            case R.id.nav_map:
                if (!MyService.canGetLocation(this)) {
                    AppUtils.showSnackbar(this, getWindow().getDecorView(), "Bật GPS để sử dụng chức năng này", "Bật GPS", Const.SNACKBAR_TURN_ON_GPS, Snackbar.LENGTH_SHORT);
                } else {
                    Intent intent2 = new Intent(MainActivity.this, MapActivity.class);
                    intent2.putExtra(getString(R.string.fragment_CODE),
                            getString(R.string.frag_map_CODE));
                    intent2.putExtra("type", 0);
                    startActivity(intent2);
                }

                break;
        }
        mdrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mdrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_main:
                switch (tabLayout.getSelectedTabPosition()) {
                    case 0:
                        Intent intent_openWritepost = new Intent(this, AdapterActivity.class);
                        intent_openWritepost.putExtra(getString(R.string.fragment_CODE),
                                getString(R.string.frag_writepost_CODE));
                        startActivity(intent_openWritepost);

                    case 1:
                        Intent intent_openAddstore = new Intent(this, AdapterActivity.class);
                        intent_openAddstore.putExtra(getString(R.string.fragment_CODE),
                                getString(R.string.frag_addstore_CODE));
                        startActivity(intent_openAddstore);
                        break;
                }

                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void initMenu() {
        final Menu menu = mnavigationView.getMenu();
        itemMap = menu.findItem(R.id.nav_map);
        itemAdmin = menu.findItem(R.id.nav_admin);
        itemProfile = menu.findItem(R.id.nav_profile);
        itemSignIn = menu.findItem(R.id.nav_signin);
        itemSignOut = menu.findItem(R.id.nav_signout);
        itemSetting = menu.findItem(R.id.nav_setting);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != LoginSession.getInstance().getUser() && null != LoginSession.getInstance().getFirebUser()) {
            User user = LoginSession.getInstance().getUser();
            txt_email.setText(user.getEmail());
            txt_un.setText(LoginSession.getInstance().getFirebUser().getDisplayName());
            Picasso.with(getApplicationContext())
                    .load(LoginSession.getInstance().getFirebUser().getPhotoUrl())
                    .placeholder(R.drawable.ic_logo)
                    .into(imgv_avatar);

            itemSignIn.setVisible(false);
            itemSignOut.setVisible(true);
            itemProfile.setVisible(true);
            itemAdmin.setVisible(false);
            if (user.getRole() > 0) {
                itemAdmin.setVisible(true);
            } else {
                itemAdmin.setVisible(false);
            }
        }else{
            Log.d("onAuthStateChanged", "onAuthStateChanged:signed_out");
            LoginSession.getInstance().setFirebUser(null);
            LoginSession.getInstance().setUser(null);
            txt_email.setText(null);
            txt_un.setText(null);
            itemSignIn.setVisible(true);
            itemSignOut.setVisible(false);
            itemProfile.setVisible(false);
            itemAdmin.setVisible(false);
            imgv_avatar.setImageResource(R.drawable.ic_logo);
        }
        Log.i(TAG, "onResume");
        mIntentFilter = new IntentFilter(Const.BROADCAST_SEND_STATUS_INTERNET);
        mIntentFilter.addAction(Const.BROADCAST_SEND_STATUS_GET_LOCATION);
        mIntentFilter.addAction(Const.SNACKBAR_GO_ONLINE);
        mIntentFilter.addAction(Const.SNACKBAR_TURN_ON_GPS);
        broadcastIntent = new Intent();
        mBroadcastReceiver = new NetworkChangeReceiver();
        registerReceiver(mBroadcastReceiver, mIntentFilter);
        startGetLocation();
        if (!MyService.isNetworkAvailable(this)) {
            showSnackbar(MainActivity.this, getWindow().getDecorView(), getString(R.string.text_not_internet), getString(R.string.text_connect), Const.SNACKBAR_GO_ONLINE, Snackbar.LENGTH_INDEFINITE);
        } else {
            if (!MyService.canGetLocation(this)) {
                if (null == CoreManager.getInstance().getMyLocation()) {
                    showSnackbar(MainActivity.this, getWindow().getDecorView(), "Bật GPS để sử dụng chức năng này", "Bật GPS", Const.SNACKBAR_TURN_ON_GPS, Snackbar.LENGTH_INDEFINITE);
                }
            } else {
                if (null != snackbar && snackbar.isShown()) {
                    snackbar.dismiss();
                }

            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
//        if (mAuthListener != null) {
//        }
    }

    //
    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if (binded) {
            this.unbindService(serviceConnection);
            binded = false;
        }
    }

    @Override
    public void onFail() {

    }

    @Override
    public void requestPermisson(List<String> strings) {
        AppUtils.requestPermission(this, strings, Const.PERMISSION_LOCATION_FLAG);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Const.PERMISSION_LOCATION_FLAG:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        break;
                    }

                }
                locationController.loadLocationService();
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i(TAG, "Location:" + location.getLatitude() + "-long:" + location.getLongitude());
        MyLocation savedLocation = CoreManager.getInstance().getMyLocation();
        if (savedLocation != null) {
            if (myTool.distanceFrom_in_Km(savedLocation.getLat(), savedLocation.getLng(), location.getLatitude(), location.getLongitude()) > 2000) {

                MyLocation myLocation = myTool.returnMyLocation(location.getLatitude(), location.getLongitude());
                List<MyLocation> listLocation = new ArrayList<>();
                listLocation.add(myLocation);
                CoreManager.getInstance().setMyLocation(this, Storage.parseMyLocationToJson(listLocation));
                Comunication.sendLocationListener.notice();
            }
        } else {
            MyLocation myLocation = myTool.returnMyLocation(location.getLatitude(), location.getLongitude());
            List<MyLocation> listLocation = new ArrayList<>();
            listLocation.add(myLocation);
            CoreManager.getInstance().setMyLocation(this, Storage.parseMyLocationToJson(listLocation));
            Comunication.sendLocationListener.notice();
        }

        locationController.disconnect();
        if (null != snackbar && snackbar.isShown()) {
            snackbar.dismiss();
        }

    }


    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            switch (intent.getAction()) {
                case Const.BROADCAST_SEND_STATUS_INTERNET:
                    if (intent.getBooleanExtra(Const.BROADCAST_SEND_STATUS_INTERNET, false)) {
                        if (intent.getBooleanExtra(Const.BROADCAST_SEND_STATUS_GET_LOCATION, false)) {
                        } else {
                            if (null == CoreManager.getInstance().getMyLocation()) {
                                ConnectionDetector.showSettingGPSAlert(alertDialog, context);
                            }
                        }
                        if (null != snackbar && snackbar.isShown()) {
                            snackbar.dismiss();
                        }
                    } else {
                        showSnackbar(MainActivity.this, getWindow().getDecorView(), getString(R.string.text_not_internet), getString(R.string.text_connect), Const.SNACKBAR_GO_ONLINE, Snackbar.LENGTH_INDEFINITE);
                    }
                    break;
                case Const.BROADCAST_SEND_STATUS_GET_LOCATION:
                    if (intent.getBooleanExtra(Const.BROADCAST_SEND_STATUS_GET_LOCATION, false)) {

                    } else {

                    }
                    break;
                case Const.SNACKBAR_GO_ONLINE:
                    if (null != intent.getStringExtra(Const.SNACKBAR_GO_ONLINE)) {
                        Intent intentSetting = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intentSetting);
                    }

                case Const.SNACKBAR_TURN_ON_GPS:
                    if (null != intent.getStringExtra(Const.SNACKBAR_TURN_ON_GPS)) {
                        Intent intentGpsSetting = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intentGpsSetting);
                    }

                    break;
            }

        }
    }

    public void showSnackbar(final Context c, View view, final String title, final String actionTitle, final String type, final int showTime) {
        if (snackbar == null) {
            snackbar = Snackbar.make(view, title, showTime);
            snackbar.setAction(actionTitle, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent snackBarIntent = new Intent();
                    snackBarIntent.setAction(type);
                    snackBarIntent.putExtra(type, type);
                    c.sendBroadcast(snackBarIntent);
                    if (showTime == Snackbar.LENGTH_INDEFINITE) {
                        snackbar.dismiss();
                    }
                }
            });

        }
        if (!snackbar.isShown()) {
            snackbar.show();
        }
    }
    public void startGetLocation() {
        locationController = new LocationController(this);
        locationController.initController();
        locationController.connect();
        locationController.setLocationListener(this);

    }

}
