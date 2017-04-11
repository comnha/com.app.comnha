package com.app.ptt.comnha.Activity;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.app.ptt.comnha.Adapters.AdminNotifyNewsFragAdapter;
import com.app.ptt.comnha.R;

public class NewsNotifyAdminActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    AdminNotifyNewsFragAdapter adapter;
    ImageView imgv_bg_store, imgv_bg_post, imgv_icon_store, imgv_icon_post;
    boolean isShowPost = false, isShowStore = false, isFirstTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_notify_admin);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.admin_color_selection_news));
        }
        imgv_bg_store = (ImageView) findViewById(R.id.img_bg_store_admin_notify_news_act);
        imgv_bg_post = (ImageView) findViewById(R.id.img_bg_post_admin_notify_news_act);
        imgv_icon_store = (ImageView) findViewById(R.id.imgV_store_notify_act);
        imgv_icon_post = (ImageView) findViewById(R.id.imgV_post_notify_act);
        toolbar = (Toolbar) findViewById(R.id.toolbar_admin_notify_news_act);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        viewPager = (ViewPager) findViewById(R.id.viewpager_notify_act);

        adapter = new AdminNotifyNewsFragAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_notify_act);
        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.getTabAt(0).setIcon(R.drawable.ic_admin_newstore);
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_admin_newpost);
        View tab_store = getLayoutInflater().inflate(R.layout.tab_notify_newstore, null);
        tab_store.findViewById(R.id.imgV_tab_notify_news).setBackgroundResource(R.drawable.ic_admin_newstore);
        tabLayout.getTabAt(0).setCustomView(tab_store);
        View tab_post = getLayoutInflater().inflate(R.layout.tab_notify_newstore, null);
        tab_post.findViewById(R.id.imgV_tab_notify_news).setBackgroundResource(R.drawable.ic_admin_newpost);
        tabLayout.getTabAt(1).setCustomView(tab_post);
//        tabLayout.getTabAt(0).setText("1");
//        tabLayout.getTabAt(1).setText("2");
        final ObjectAnimator alpha_show_bg_post = ObjectAnimator.ofFloat(imgv_bg_post, "alpha", 0, 1),
                alpha_show_icon_post = ObjectAnimator.ofFloat(imgv_icon_post, "alpha", 0, 1),
                alpha_show_icon_store = ObjectAnimator.ofFloat(imgv_icon_store, "alpha", 0, 1),
                alpha_hide_bg_post = ObjectAnimator.ofFloat(imgv_bg_post, "alpha", 1, 0),
                alpha_hide_icon_post = ObjectAnimator.ofFloat(imgv_icon_post, "alpha", 1, 0),
                alpha_hide_icon_store = ObjectAnimator.ofFloat(imgv_icon_store, "alpha", 1, 0);
        alpha_show_bg_post.setDuration(100);
        alpha_show_icon_post.setDuration(100);
        alpha_show_icon_store.setDuration(100);
        alpha_hide_bg_post.setDuration(100);
        alpha_hide_icon_post.setDuration(100);
        alpha_hide_icon_store.setDuration(100);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("tabselect", tab.getPosition() + "");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("positionOffset", positionOffset + "");
                Log.d("position", position + "");
                if (isFirstTime) {
                    isFirstTime = false;
                } else {
                    if (position == 0 && positionOffset == 0) {
                        if (!isShowStore) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                getWindow().setStatusBarColor(getResources().getColor(R.color.admin_color_selection_news));
                            }
                            alpha_hide_bg_post.start();
                            alpha_hide_icon_post.start();
                            alpha_show_icon_store.start();
                            isShowStore = true;
                            isShowPost = false;
                        }
                    } else if (position == 1 && positionOffset == 0) {
                        if (!isShowPost) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                getWindow().setStatusBarColor(getResources().getColor(R.color.admin_color_notify_newpost));
                            }
                            alpha_show_bg_post.start();
                            alpha_show_icon_post.start();
                            alpha_hide_icon_store.start();
                            isShowPost = true;
                            isShowStore = false;
                        }
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }
}
