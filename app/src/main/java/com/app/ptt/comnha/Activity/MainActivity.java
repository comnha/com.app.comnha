package com.app.ptt.comnha.Activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
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
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.Modules.MyTool;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.CoreManager;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.AppUtils;
import com.app.ptt.comnha.Utils.LocationController;
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
        stRef = FirebaseStorage.getInstance().getReferenceFromUrl(
                getString(R.string.firebaseStorage_path));
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebase_path));
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
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void getUserInfo(final FirebaseUser firebaseUser) {
        userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                String key = firebaseUser.getUid();
                user.setuID(key);
                if (user.getRole() == 1) {
                    itemAdmin.setVisible(true);
                } else {
                    itemAdmin.setVisible(false);
                }
                LoginSession.getInstance().setUser(user);
                LoginSession.getInstance().setFirebUser(firebaseUser);
                mAuth.removeAuthStateListener(mAuthListener);
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
            case R.id.action_changelocation_main:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                        closeDialog();
                        AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), getString(R.string.text_signout_success));
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
                        break;
                    case 1:
                        Intent intent_openAddstore = new Intent(this, AdapterActivity.class);
                        intent_openAddstore.putExtra(getString(R.string.fragment_CODE),
                                getString(R.string.frag_addstore_CODE));
                        startActivity(intent_openAddstore);
//                        FragmentTransaction transaction = getSupportFragmentManager()
//                                .beginTransaction()
//                                .add(R.id.frame_btmsheet, new AddstoreFragment(), "addloca_frag");
//                        transaction.commit();
//                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//                            @Override
//                            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                                switch (newState) {
//                                    case BottomSheetBehavior.STATE_EXPANDED:
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                            getWindow().setStatusBarColor(getResources()
//                                                    .getColor(R.color.color_selection_report));
//                                        }
//                                        break;
//                                    case BottomSheetBehavior.STATE_COLLAPSED:
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                            getWindow().setStatusBarColor(getResources()
//                                                    .getColor(R.color.colorPrimaryDark));
//                                        }
//                                    case BottomSheetBehavior.STATE_DRAGGING:
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                            getWindow().setStatusBarColor(getResources()
//                                                    .getColor(R.color.colorPrimaryDark));
//                                        }
//                                        break;
//                                }
                }
//
//                            @Override
//                            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                                Log.d("slideOffset", slideOffset + "");
//
//                                if (slideOffset == 0) {
//                                    dimbtmsheetView.setBackgroundColor(
//                                            getResources()
//                                                    .getColor(android.R.color.transparent));
//                                } else {
//                                    dimbtmsheetView.setBackgroundColor(
//                                            getResources().getColor(android.R.color.black));
//                                    dimbtmsheetView.setAlpha(slideOffset - 0.2f);
//                                }
//                            }
//                        });

//                        break;
//                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Const.PERMISSION_LOCATION_FLAG:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        break;
                    }
                    locationController.loadLocationService();
                }
                break;
        }
    }

    //
//    private ChangeLocationBottomSheetDialogFragment changeLccaBtmSheet;
//
//    public void initUI() {
//        if (LoginSession.getInstance().getHuyen() == "" && LoginSession.getInstance().getTinh() == "") {
//            tinh = myLocation.getTinhtp();
//            huyen = myLocation.getQuanhuyen();
//            LoginSession.getInstance().setTinh(myLocation.getTinhtp());
//            LoginSession.getInstance().setHuyen(myLocation.getQuanhuyen());
//            fab_changloca.setLabelText(
//                    LoginSession.getInstance().getHuyen() + ", "
//                            + LoginSession.getInstance().getTinh()
//            );
//        }
//        bottomBarEvent();
//    }
//    public void initFirebase() {
//
//        mAuth.addAuthStateListener(mAuthListener);
//        try {
//            if (mAuth.getCurrentUser() == null) {
//                mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d("signInAnonymously", "signInAnonymously:onComplete:" + task.isSuccessful());
//
//                        // If sign in fails, display a message to the user. If sign in succeeds
//                        // the auth state listener will be notified and logic to handle the
//                        // signed in user can be handled in the listener.
//                        if (!task.isSuccessful()) {
//                            Log.w("signInAnonymouslyError", "signInAnonymously", task.getException());
////                            Toast.makeText(MainActivity.this, "Authentication failed.",
////                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//            if (mAuth.getCurrentUser().getEmail() == null) {
//                mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d("signInAnonymously", "signInAnonymously:onComplete:" + task.isSuccessful());
//
//                        // If sign in fails, display a message to the user. If sign in succeeds
//                        // the auth state listener will be notified and logic to handle the
//                        // signed in user can be handled in the listener.
//                        if (!task.isSuccessful()) {
//                            Log.w("signInAnonymouslyError", "signInAnonymously", task.getException());
//                        }
//                    }
//                });
//            }
//        } catch (NullPointerException mess) {
//        }
//    }
//
    public void initMenu() {
        final Menu menu = mnavigationView.getMenu();
        itemMap = menu.findItem(R.id.nav_map);
        itemAdmin = menu.findItem(R.id.nav_admin);
        itemProfile = menu.findItem(R.id.nav_profile);
        itemSignIn = menu.findItem(R.id.nav_signin);
        itemSignOut = menu.findItem(R.id.nav_signout);
        itemSetting = menu.findItem(R.id.nav_setting);
    }

    //
//    public void getRole() {
//        role = false;
//        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
//        profileValueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Account account = dataSnapshot.getValue(Account.class);
//                role = account.getRole();
//                account.setId(dataSnapshot.getKey());
//                account.setUsername(user.getDisplayName());
//                LoginSession.getInstance().setRole(role);
//                if (role) {
//                    menuItem3.setVisible(true);
//                } else {
////                    menuItem3.setVisible(false);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//        dbRef.child(getResources().getString(R.string.users_CODE) +//liệt kê tất cả
//                LoginSession.getInstance().getUserID()).addListenerForSingleValueEvent(profileValueEventListener);
//        dbRef.removeEventListener(profileValueEventListener);
//    }
//
//
//    void ref() {
//    }
//
//    public void bottomBarEvent() {
//        fab_review.setOnClickListener(this);
//        fab_addloca.setOnClickListener(this);
//        fab_changloca.setOnClickListener(this);
//        fabmenu.setClosedOnTouchOutside(true);
//        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
//            @Override
//            public void onTabSelected(@IdRes int tabId) {
//                FragmentTransaction transaction;
//                switch (tabId) {
//                    case R.id.tab_reviews:
//                        fabmenu.close(true);
//                        ReviewFragment reviewFragment = new ReviewFragment();
//                        reviewFragment.setContext(getApplicationContext());
//                        reviewFragment.setSortType(1);
//                        reviewFragment.setTinh(LoginSession.getInstance().getTinh());
//                        reviewFragment.setHuyen(LoginSession.getInstance().getHuyen());
//                        transaction = getSupportFragmentManager().beginTransaction();
//                        transaction.replace(R.id.frame, reviewFragment);
//                        transaction.commit();
//                        AnimationUtils.animatfabMenuIn(fabmenu);
//                        break;
//                    case R.id.tab_stores:
//                        fabmenu.close(true);
//                        StoreFragment storeFragment = new StoreFragment();
//                        storeFragment.setFilter(1);
//                        storeFragment.setTinh(LoginSession.getInstance().getTinh());
//                        storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
//                        storeFragment.setContext(getApplicationContext());
//                        transaction = getSupportFragmentManager().beginTransaction();
//                        transaction.replace(R.id.frame, storeFragment);
//                        transaction.commit();
//                        AnimationUtils.animatfabMenuIn(fabmenu);
//
//                        break;
//                    case R.id.tab_locations:
//                        FilterFragment filterFragment = new FilterFragment();
//                        transaction = getSupportFragmentManager().beginTransaction();
//                        transaction.replace(R.id.frame, filterFragment);
//                        transaction.commit();
//                        AnimationUtils.animatfabMenuOut(fabmenu);
//                        fabmenu.close(true);
//                        break;
//                }
//            }
//        });
//
//        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
//            @Override
//            public void onTabReSelected(@IdRes int tabId) {
//                switch (tabId) {
//                    case R.id.tab_reviews:
//                        popupMenu = new PopupMenu(MainActivity.this, findViewById(R.id.tab_reviews), Gravity.END);
//                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_viewpost, popupMenu.getMenu());
//                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                            @Override
//                            public boolean onMenuItemClick(MenuItem item) {
//                                switch (item.getItemId()) {
//                                    case R.id.popup_viewpost_lastnews:
//                                        ReviewFragment reviewFragment = new ReviewFragment();
//                                        reviewFragment.setSortType(1);
//                                        reviewFragment.setTinh(LoginSession.getInstance().getTinh());
//                                        reviewFragment.setHuyen(LoginSession.getInstance().getHuyen());
//                                        reviewFragment.setContext(getApplicationContext());
//                                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                                        transaction.replace(R.id.frame, reviewFragment);
//                                        transaction.commit();
//
//                                        break;
//                                    case R.id.popup_viewpost_mostcomment:
//
//                                        ReviewFragment reviewFragment1 = new ReviewFragment();
//
//                                        reviewFragment1.setSortType(2);
//                                        reviewFragment1.setContext(getApplicationContext());
//                                        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
//                                        transaction1.replace(R.id.frame, reviewFragment1);
//                                        reviewFragment1.setTinh(LoginSession.getInstance().getTinh());
//                                        reviewFragment1.setHuyen(LoginSession.getInstance().getHuyen());
//                                        transaction1.commit();
//                                        break;
//                                    case R.id.popup_viewpost_mostlike:
//                                        ReviewFragment reviewFragment2 = new ReviewFragment();
//                                        reviewFragment2.setSortType(3);
//                                        reviewFragment2.setTinh(LoginSession.getInstance().getTinh());
//                                        reviewFragment2.setHuyen(LoginSession.getInstance().getHuyen());
//                                        reviewFragment2.setContext(getApplicationContext());
//                                        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
//                                        transaction2.replace(R.id.frame, reviewFragment2);
//                                        transaction2.commit();
//
//                                        break;
//                                }
//                                return true;
//                            }
//                        });
//                        popupMenu.show();
//
//                        break;
//                    case R.id.tab_stores:
//                        popupMenu = new PopupMenu(MainActivity.this, findViewById(R.id.tab_stores), Gravity.CENTER);
//                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_viewquan, popupMenu.getMenu());
//                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                            @Override
//                            public boolean onMenuItemClick(MenuItem item) {
//                                FragmentTransaction transaction;
//                                StoreFragment storeFragment;
//                                switch (item.getItemId()) {
//                                    case R.id.popup_viewquan_none:
//                                        storeFragment = new StoreFragment();
//                                        storeFragment.setFilter(1);
//                                        storeFragment.setTinh(LoginSession.getInstance().getTinh());
//                                        storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
//                                        storeFragment.setContext(getApplicationContext());
//                                        transaction = getSupportFragmentManager().beginTransaction();
//                                        transaction.replace(R.id.frame, storeFragment);
//                                        transaction.commit();
//
//                                        break;
//                                    case R.id.popup_viewquan_gia:
//
//                                        storeFragment = new StoreFragment();
//
//                                        storeFragment.setTinh(LoginSession.getInstance().getTinh());
//                                        storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
//                                        storeFragment.setContext(getApplicationContext());
//                                        storeFragment.setFilter(2);
//                                        transaction = getSupportFragmentManager()
//                                                .beginTransaction()
//                                                .replace(R.id.frame, storeFragment);
//                                        transaction.commit();
//
//                                        break;
//                                    case R.id.popup_viewquan_pv:
//                                        storeFragment = new StoreFragment();
//
//                                        storeFragment.setTinh(LoginSession.getInstance().getTinh());
//                                        storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
//                                        storeFragment.setContext(getApplicationContext());
//                                        storeFragment.setFilter(3);
//                                        transaction = getSupportFragmentManager()
//                                                .beginTransaction()
//                                                .replace(R.id.frame, storeFragment);
//                                        transaction.commit();
//                                        break;
//                                    case R.id.popup_viewquan_vs:
//                                        storeFragment = new StoreFragment();
//                                        storeFragment.setTinh(LoginSession.getInstance().getTinh());
//                                        storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
//                                        storeFragment.setContext(getApplicationContext());
//                                        storeFragment.setFilter(4);
//                                        transaction = getSupportFragmentManager()
//                                                .beginTransaction()
//                                                .replace(R.id.frame, storeFragment);
//                                        transaction.commit();
//
//                                        break;
//                                }
//                                return true;
//                            }
//                        });
//                        popupMenu.show();
//                        break;
//                    case R.id.tab_locations:
////                        popupMenu = new PopupMenu(MainActivity.this, findViewById(R.id.tab_locations), Gravity.START);
////                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_locafilter, popupMenu.getMenu());
////                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
////                            @Override
////                            public boolean onMenuItemClick(MenuItem item) {
////                                switch (item.getItemId()) {
////                                    case R.id.popup_locafilter_myloca:
////                                        break;
////                                    case R.id.popup_locafilter_choseloca:
////                                        break;
////                                }
////                                return true;
////                            }
////                        });
////                        popupMenu.show();
//                        break;
//                }
//            }
//        });
//    }
//
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
        getUser();
        Log.i(TAG, "onResume");
        mIntentFilter = new IntentFilter(Const.BROADCAST_SEND_STATUS_INTERNET);
        mIntentFilter.addAction(Const.BROADCAST_SEND_STATUS_GET_LOCATION);
        mIntentFilter.addAction(Const.SNACKBAR_GO_ONLINE);
        mIntentFilter.addAction(Const.SNACKBAR_TURN_ON_GPS);
        broadcastIntent = new Intent();
        mBroadcastReceiver = new NetworkChangeReceiver();
        registerReceiver(mBroadcastReceiver, mIntentFilter);
        if (!MyService.isNetworkAvailable(this)) {
            connectionStatus = -2;
            showSnackbar(MainActivity.this, getWindow().getDecorView(), getString(R.string.text_not_internet), getString(R.string.text_connect), Const.SNACKBAR_GO_ONLINE, Snackbar.LENGTH_INDEFINITE, false);
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
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Location:" + location.getLatitude() + "-long:" + location.getLongitude());
        Store savedLocation = CoreManager.getInstance().getMyLocation();
        if (savedLocation != null) {
            if (savedLocation.getLat() != location.getLatitude()
                    && savedLocation.getLng() != location.getLongitude()) {
                Store myLocation = myTool.returnLocationByLatLng(location.getLatitude(), location.getLongitude());
                List<Store> listLocation = new ArrayList<>();
                listLocation.add(myLocation);
                CoreManager.getInstance().setMyLocation(this, Storage.parseMyLocationToJson(listLocation));
            }
        } else {
            Store myLocation = myTool.returnLocationByLatLng(location.getLatitude(), location.getLongitude());
            List<Store> listLocation = new ArrayList<>();
            listLocation.add(myLocation);
            CoreManager.getInstance().setMyLocation(this, Storage.parseMyLocationToJson(listLocation));
        }
        locationController.disconnect();
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            switch (intent.getAction()) {
                case Const.BROADCAST_SEND_STATUS_INTERNET:
                    if (intent.getBooleanExtra(Const.BROADCAST_SEND_STATUS_INTERNET, false)) {
                        if (intent.getBooleanExtra(Const.BROADCAST_SEND_STATUS_GET_LOCATION, false)) {
                            startGetLocation();
                        }
                        if (connectionStatus == -2 || connectionStatus == 0) {
                            showSnackbar(MainActivity.this, getWindow().getDecorView(), getString(R.string.text_not_internet), getString(R.string.text_connect), Const.SNACKBAR_GO_ONLINE, Snackbar.LENGTH_INDEFINITE, true);
                        }
                        connectionStatus = 1;
                    } else {
                        if (connectionStatus != -2) {
                            showSnackbar(MainActivity.this, getWindow().getDecorView(), getString(R.string.text_not_internet), getString(R.string.text_connect), Const.SNACKBAR_GO_ONLINE, Snackbar.LENGTH_INDEFINITE, false);
                        }
                        connectionStatus = 0;
                    }
                    break;
                case Const.BROADCAST_SEND_STATUS_GET_LOCATION:
                    if (intent.getBooleanExtra(Const.BROADCAST_SEND_STATUS_GET_LOCATION, false)) {
                        startGetLocation();
                    } else {

                    }
                    break;
                case Const.SNACKBAR_GO_ONLINE:
                    connectionStatus = 2;
                    Intent intentSetting = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intentSetting);
                case Const.SNACKBAR_TURN_ON_GPS:
                    connectionStatus = 3;
                    Intent intentGpsSetting = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intentGpsSetting);
                    break;
            }

        }
    }

    public void showSnackbar(final Context c, View view, final String title, final String actionTitle, final String type, final int showTime, boolean action) {
        if (action) {
            snackbar.dismiss();
        } else {
            snackbar = Snackbar.make(view, title, showTime);
            snackbar.setAction(actionTitle, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent snackBarIntent = new Intent();
                    snackBarIntent.setAction(Const.SNACKBAR_GO_ONLINE);
                    snackBarIntent.putExtra(Const.SNACKBAR_GO_ONLINE, type);
                    c.sendBroadcast(snackBarIntent);
                    if (showTime == Snackbar.LENGTH_INDEFINITE) {
                        snackbar.dismiss();
                    }
                }
            });

            snackbar.show();
        }
    }

    public void startGetLocation() {
        locationController = new LocationController(this);
        locationController.initController();
        locationController.setLocationListener(this);
        locationController.connect();
    }
//
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            new AlertDialog.Builder(this)
//                    .setMessage("Bạn có muốn thoát?")
//                    .setNegativeButton("Không", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    })
//                    .setPositiveButton("có", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    }).show();
//        }
//    }
//
//
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.main_fabitem1:
//                changeLccaBtmSheet = new ChangeLocationBottomSheetDialogFragment();
//                changeLccaBtmSheet.show(getSupportFragmentManager(), "fragment_changeLocal");
//                changeLccaBtmSheet.setOnChangeLocationListenner(new ChangeLocationBottomSheetDialogFragment.OnChangeLocationListenner() {
//                    @Override
//                    public void onChangeLocation(String Province, String District) {
//                        LoginSession.getInstance().setTinh(Province);
//                        fab_changloca.setLabelText(
//                                Province + ", " + District
//                        );
//                        LoginSession.getInstance().setHuyen(District);
//                        fabmenu.close(true);
//                        FragmentTransaction transaction;
//                        switch (bottomBar.getCurrentTabPosition()) {
//                            case 0:
//                                ReviewFragment reviewFragment = new ReviewFragment();
//                                reviewFragment.setTinh(LoginSession.getInstance().getTinh());
//                                reviewFragment.setHuyen(LoginSession.getInstance().getHuyen());
//                                reviewFragment.setSortType(1);
//                                reviewFragment.setContext(getApplicationContext());
//                                transaction = getSupportFragmentManager().beginTransaction();
//                                transaction.replace(R.id.frame, reviewFragment);
//                                transaction.commit();
//                                break;
//                            case 1:
//                                StoreFragment storeFragment = new StoreFragment();
//                                storeFragment.setFilter(1);
//                                storeFragment.setTinh(LoginSession.getInstance().getTinh());
//                                storeFragment.setContext(getApplicationContext());
//                                storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
//                                transaction = getSupportFragmentManager().beginTransaction();
//                                transaction.replace(R.id.frame, storeFragment);
//                                transaction.commit();
//                                break;
//                            case 2:
//                                break;
//                        }
//                    }
//
//                    @Override
//                    public void onChangetoMylocation(boolean isMylocation) {
//                        if (isMylocation) {
//                            LoginSession.getInstance().setHuyen(myLocation.getQuanhuyen());
//                            LoginSession.getInstance().setTinh(myLocation.getTinhtp());
//                            fab_changloca.setLabelText(LoginSession.getInstance().getHuyen() + ", " +
//                                    LoginSession.getInstance().getTinh());
//                            fabmenu.close(true);
//                            FragmentTransaction transaction;
//                            switch (bottomBar.getCurrentTabPosition()) {
//                                case 0:
//                                    ReviewFragment reviewFragment = new ReviewFragment();
//                                    reviewFragment.setTinh(LoginSession.getInstance().getTinh());
//                                    reviewFragment.setHuyen(LoginSession.getInstance().getHuyen());
//                                    reviewFragment.setSortType(1);
//                                    reviewFragment.setContext(getApplicationContext());
//                                    transaction = getSupportFragmentManager().beginTransaction();
//                                    transaction.replace(R.id.frame, reviewFragment);
//                                    transaction.commit();
//                                    break;
//                                case 1:
//                                    StoreFragment storeFragment = new StoreFragment();
//                                    storeFragment.setFilter(1);
//                                    storeFragment.setTinh(LoginSession.getInstance().getTinh());
//                                    storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
//                                    storeFragment.setContext(getApplicationContext());
//                                    transaction = getSupportFragmentManager().beginTransaction();
//                                    transaction.replace(R.id.frame, storeFragment);
//                                    transaction.commit();
//                                    break;
//                                case 2:
//                                    break;
//                            }
//                        }
//                    }
//                });
//                break;
//            case R.id.main_fabitem2:
//                if (isConnected) {
//                    if (LoginSession.getInstance().getUserID() == null) {
//                        Toast.makeText(this, getString(R.string.txt_needlogin),
//                                Toast.LENGTH_SHORT).show();
//                    } else {
//                        Intent intent = new Intent(MainActivity.this, Adapter2Activity.class);
//                        intent.putExtra(getString(R.string.fragment_CODE),
//                                getString(R.string.frag_addloca_CODE));
//                        intent.putExtra("isConnected", isConnected);
//                        startActivity(intent);
//                    }
//                } else
//                    Toast.makeText(this, "You are offline", Toast.LENGTH_LONG).show();
//                break;
//            case R.id.main_fabitem3:
//                if (isConnected) {
//                    if (LoginSession.getInstance().getUserID() == null) {
//                        Toast.makeText(this, getString(R.string.txt_needlogin),
//                                Toast.LENGTH_SHORT).show();
//                    } else {
//                        Intent intent1 = new Intent(MainActivity.this, Adapter2Activity.class);
//                        intent1.putExtra(getString(R.string.fragment_CODE),
//                                getString(R.string.frag_addpost_CODE));
//                        intent1.putExtra("isConnected", isConnected);
//                        startActivity(intent1);
//                    }
//                } else
//                    Toast.makeText(this, "You are offline", Toast.LENGTH_LONG).show();
//                break;
//
//        }
//    }


}
