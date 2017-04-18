package com.app.ptt.comnha.Activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.app.ptt.comnha.Adapters.MainFragPagerAdapter;
import com.app.ptt.comnha.Fragment.AboutBottomSheetDialogFragment;
import com.app.ptt.comnha.Fragment.AddlocaFragment;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String LOG = MainActivity.class.getSimpleName();
    private Bundle savedInstanceState;
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
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MainFragPagerAdapter pagerAdapter;
    private FloatingActionButton fab;
    NestedScrollView nestedScrollView;
    BottomSheetBehavior bottomSheetBehavior;
    FrameLayout frameLayout;
    View dimbtmsheetView;
    private View posttabview,
            storetabview,
            notifytabview;
    boolean isPostRefresh = true,
            isStoretRefresh = true,
            isNotitRefresh = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        anhXa();
        LoginSession.getInstance().setTinh("");
        LoginSession.getInstance().setHuyen("");
        startMyService();

    }

    private void anhXa() {

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
        txt_email = (TextView) header.findViewById(R.id.nav_head_email);
        txt_un = (TextView) header.findViewById(R.id.nav_head_username);
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
                if (!isPostRefresh) {
                    View postview = posttabview.findViewById(R.id.imgV_news_tabs_main);
                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(postview, "scaleX", 1, 0),
                            scaleY = ObjectAnimator.ofFloat(postview, "scaleY", 1, 0);
                    scaleX.setDuration(100);
                    scaleY.setDuration(100);
//                    tabLayout.getTabAt(0).setCustomView(posttabview);
                    scaleX.start();
                    scaleY.start();
                    scaleX.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            posttabview.findViewById(R.id.imgV_news_tabs_main).setBackgroundResource(android.R.color.transparent);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                }
            }
        });
        storetabview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabLayout.getTabAt(1).select();
                if (!isStoretRefresh) {
                    View storeview = storetabview.findViewById(R.id.imgV_news_tabs_main);
                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(storeview, "scaleX", 1, 0),
                            scaleY = ObjectAnimator.ofFloat(storeview, "scaleY", 1, 0);
                    scaleX.setDuration(100);
                    scaleY.setDuration(100);
//                    tabLayout.getTabAt(1).setCustomView(storetabview);
                    scaleX.start();
                    scaleY.start();
                    scaleX.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            storetabview.findViewById(R.id.imgV_news_tabs_main).setBackgroundResource(android.R.color.transparent);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                }
            }
        });
        notifytabview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabLayout.getTabAt(2).select();
                if (isNotitRefresh) {
                    View notifyview = storetabview.findViewById(R.id.imgV_news_tabs_main);
                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(notifyview, "scaleX", 1, 0),
                            scaleY = ObjectAnimator.ofFloat(notifyview, "scaleY", 1, 0);
                    scaleX.setDuration(100);
                    scaleY.setDuration(100);
//                    tabLayout.getTabAt(2).setCustomView(notifytabview);
                    scaleX.start();
                    scaleY.start();
                    scaleX.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            notifytabview.findViewById(R.id.imgV_news_tabs_main).setBackgroundResource(android.R.color.transparent);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                }
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("onTabSelected", tab.getPosition() + "");

                switch (tab.getPosition()) {
                    case 0:


                        scalefabX.start();
                        scalefabY.start();
                        fab.setColorNormal(getResources()
                                .getColor(R.color.admin_color_selection_news));
                        fab.setColorPressed(getResources()
                                .getColor(R.color.admin_color_selection_news));
                        fab.setImageResource(R.drawable.ic_write_color_white_24dp);
                        break;
                    case 1:
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

        dimbtmsheetView = findViewById(R.id.view_dimbtmsheet_main);

        frameLayout = (FrameLayout) findViewById(R.id.frame_btmsheet);
        bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_main:
                posttabview.findViewById(R.id.imgV_news_tabs_main)
                        .setBackgroundResource(R.drawable.ic_notify_new_yellow_18dp);
                View postview = posttabview.findViewById(R.id.imgV_news_tabs_main);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(postview, "scaleX", 0, 1),
                        scaleY = ObjectAnimator.ofFloat(postview, "scaleY", 0, 1);
                scaleX.setDuration(100);
                scaleY.setDuration(100);
                tabLayout.getTabAt(0).setCustomView(posttabview);
                scaleX.start();
                scaleY.start();
                isPostRefresh = false;
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                Intent intent1 = new Intent(MainActivity.this, Adapter2Activity.class);
                intent1.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frg_signin_CODE));

                startActivity(intent1);
                break;
            case R.id.nav_signout:
//                if (isConnected) {
//                    showLoading(getString(R.string.txt_plzwait), getString(R.string.txt_logginout));
//                    mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            hideLoading();
//                            Log.d("signInAnonymously", "signInAnonymously:onComplete:" + task.isSuccessful());
//                            // If sign in fails, display a message to the user. If sign in succeeds
//                            // the auth state listener will be notified and logic to handle the
//                            // signed in user can be handled in the listener.
//                            if (!task.isSuccessful()) {
//                                Log.w("signInAnonymouslyError", "signInAnonymously", task.getException());
//                            } else {
//                                LoginSession.getInstance().setTen(null);
//                                LoginSession.getInstance().setHo(null);
//                                LoginSession.getInstance().setTenlot(null);
//                                LoginSession.getInstance().setNgaysinh(null);
//                                LoginSession.getInstance().setPassword(null);
//                            }
//                        }
//                    });
//                } else {
//                    Toast.makeText(getApplicationContext(), "You are offline", Toast.LENGTH_SHORT).show();
//                }
                break;
            case R.id.nav_map:
                Intent intent2 = new Intent(MainActivity.this, AdapterActivity.class);
                intent2.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frag_map_CODE));
                startActivity(intent2);
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
                        Intent intent_openWritepost = new Intent(this, Adapter2Activity.class);
                        intent_openWritepost.putExtra(getString(R.string.fragment_CODE),
                                getString(R.string.frag_addpost_CODE));
                        startActivity(intent_openWritepost);
                        break;
                    case 1:
                        FragmentTransaction transaction = getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.frame_btmsheet, new AddlocaFragment(), "addloca_frag");
                        transaction.commit();
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                            @Override
                            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                                switch (newState) {
                                    case BottomSheetBehavior.STATE_EXPANDED:
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            getWindow().setStatusBarColor(getResources()
                                                    .getColor(R.color.color_selection_report));
                                        }
                                        break;
                                    case BottomSheetBehavior.STATE_COLLAPSED:
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            getWindow().setStatusBarColor(getResources()
                                                    .getColor(R.color.colorPrimaryDark));
                                        }
                                    case BottomSheetBehavior.STATE_DRAGGING:
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            getWindow().setStatusBarColor(getResources()
                                                    .getColor(R.color.colorPrimaryDark));
                                        }
                                        break;
                                }
                            }

                            @Override
                            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                                Log.d("slideOffset", slideOffset + "");

                                if (slideOffset == 0) {
                                    dimbtmsheetView.setBackgroundColor(
                                            getResources()
                                                    .getColor(android.R.color.transparent));
                                } else {
                                    dimbtmsheetView.setBackgroundColor(
                                            getResources().getColor(android.R.color.black));
                                    dimbtmsheetView.setAlpha(slideOffset - 0.2f);
                                }
                            }
                        });

                        break;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
    //
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
//    public void initMenu() {
//        final Menu menu = mnavigationView.getMenu();
//        menuItem = menu.findItem(R.id.nav_profile);
//        menuItem1 = menu.findItem(R.id.nav_signin);
//        menuItem2 = menu.findItem(R.id.nav_signout);
//        menuItem3 = menu.findItem(R.id.nav_admin);
//        menuItem4 = menu.findItem(R.id.nav_notification);
//        menuItem4.setVisible(false);
//        mAuth = FirebaseAuth.getInstance();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                user = firebaseAuth.getCurrentUser();
//
//                if (user != null) {
//                    try {
//                        if (user.getEmail() != null) {
//                            userID = user.getUid();
//                            menuItem.setEnabled(true);
//                            menuItem1.setEnabled(false);
//                            menuItem2.setEnabled(true);
//                            menuItem4.setVisible(true);
//                            Log.d("signed_in", "onAuthStateChanged:signed_in: " + user.getEmail());
//                            LoginSession.getInstance().setUserID(userID);
//                            LoginSession.getInstance().setEmail(user.getEmail());
//                            LoginSession.getInstance().setUsername(user.getDisplayName());
//                            txt_email.setText(user.getEmail());
//                            txt_un.setText(user.getDisplayName());
//                            getRole();
//                        } else {
//                            menuItem.setEnabled(false);
//                            menuItem2.setEnabled(false);
//                            menuItem1.setEnabled(true);
////                            menuItem3.setVisible(false);
//                            menuItem4.setVisible(false);
//                            txt_email.setText(getResources().getString(R.string.text_hello));
//                            txt_un.setText(getResources().getString(R.string.text_user));
//                            userID = "";
//                            LoginSession.getInstance().setUserID(null);
//                            LoginSession.getInstance().setUsername(null);
//                            LoginSession.getInstance().setEmail(null);
//                        }
//                    } catch (NullPointerException mess) {
//                        txt_email.setText(getResources().getString(R.string.text_hello));
//                        txt_un.setText(getResources().getString(R.string.text_user));
//                        userID = "";
//                        LoginSession.getInstance().setUserID(null);
//                        LoginSession.getInstance().setUsername(null);
//                        LoginSession.getInstance().setEmail(null);
//                    }
//                } else {
//                    menuItem.setEnabled(false);
//                    menuItem2.setEnabled(false);
//                    menuItem1.setEnabled(true);
////                    menuItem3.setVisible(false);
//                    menuItem4.setEnabled(false);
//                    txt_email.setText(getResources().getString(R.string.text_hello));
//                    txt_un.setText(getResources().getString(R.string.text_user));
//                    userID = "";
//                    LoginSession.getInstance().setUserID(null);
//                    LoginSession.getInstance().setUsername(null);
//                    LoginSession.getInstance().setEmail(null);
//                }
//            }
//        };
//    }
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
//    void anhXa() {
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
//    @Override
//    protected void onStart() {
//        super.onStart();
//        isConnected = MyService.returnIsConnected();
//        mIntentFilter = new IntentFilter();
//        mIntentFilter.addAction(Const.BROADCAST_SEND_STATUS_GET_LOCATION);
//        mBroadcastReceiver = new NetworkChangeReceiver();
//        registerReceiver(mBroadcastReceiver, mIntentFilter);
//        Log.i(LOG, "onStart= " + isConnected);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        Log.i(LOG, "onResume");
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        unregisterReceiver(mBroadcastReceiver);
//
//        Log.i(LOG, "onStop");
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        Log.i(LOG, "onDestroy");
//        super.onDestroy();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//        if (binded) {
//            this.unbindService(serviceConnection);
//            binded = false;
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.i(LOG, "Pause");
//    }
//
//    public class NetworkChangeReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(final Context context, final Intent intent) {
//            // Log.i(LOG + ".NetworkChangeReceiver", "isConnected splash " + temp);
//            if (intent.getAction().equals(Const.BROADCAST_SEND_STATUS_GET_LOCATION)) {
//                if (intent.getBooleanExtra("isConnected", false)) {
//                    isConnected = true;
//                    if (intent.getSerializableExtra("myLocation") != null) {
//                        myLocation = (MyLocation) intent.getSerializableExtra("myLocation");
//                        initUI();
//                    }
//                } else {
//                    isConnected = false;
//                    if (intent.getIntExtra("checkCodition", 0) == 1) {
//                        ConnectionDetector.showSettingNetworkAlert(getApplicationContext());
//                    }
//                    if (intent.getIntExtra("checkCondition", 0) == 2) {
//                        ConnectionDetector.showSettingGPSAlert(getApplicationContext());
//                    }
//                    if (intent.getIntExtra("checkCondition", 0) == 3) {
//                        ConnectionDetector.showSettingNetworkAlert(getApplicationContext());
//                        ConnectionDetector.showSettingGPSAlert(getApplicationContext());
//                    }
//
//                }
//            }
//
//        }
//    }
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
//

}
