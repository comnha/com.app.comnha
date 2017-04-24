package com.app.ptt.comnha.Activity;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.app.ptt.comnha.Adapters.NotifyReportsFragPagerAdapter;
import com.app.ptt.comnha.R;

public class ReportsNotifyAdminActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    NotifyReportsFragPagerAdapter adapter;
    ImageView imgv_bg_1, imgv_bg_2, imgv_icon_1, imgv_icon_2;
    int firstcolor, firstIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_notify_admin);
        firstcolor
                = getResources().getColor(R.color.color_selection_report);
        firstIcon = R.drawable.ic_notify_reportstore;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.color_selection_report));
        }
        imgv_bg_1 = (ImageView) findViewById(R.id.img_bg_1_admin_notify_reports_act);
        imgv_bg_1.setBackgroundColor(getResources().getColor(R.color.color_selection_report));
        imgv_bg_2 = (ImageView) findViewById(R.id.img_bg_2_admin_notify_reports_act);
        imgv_icon_1 = (ImageView) findViewById(R.id.imgV_1_notify_repots_act);
        imgv_icon_1.setImageResource(R.drawable.ic_notify_reportstore);
        imgv_icon_2 = (ImageView) findViewById(R.id.imgV_2_notify_repots_act);

        toolbar = (Toolbar) findViewById(R.id.toolbar_admin_notify_reports_act);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        viewPager = (ViewPager) findViewById(R.id.viewpager_notify_reports_act);

        adapter = new NotifyReportsFragPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_notify_reports_act);
        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.getTabAt(0).setIcon(R.drawable.ic_admin_newstore);
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_admin_newpost);
        View tab_store = getLayoutInflater().inflate(R.layout.tab_notify, null);
        tab_store.findViewById(R.id.imgV_tab_notify).setBackgroundResource(R.drawable.ic_tab_store);
        tabLayout.getTabAt(0).setCustomView(tab_store);
        View tab_post = getLayoutInflater().inflate(R.layout.tab_notify, null);
        tab_post.findViewById(R.id.imgV_tab_notify).setBackgroundResource(R.drawable.ic_tab_post);
        tabLayout.getTabAt(1).setCustomView(tab_post);
        View tab_img = getLayoutInflater().inflate(R.layout.tab_notify, null);
        tab_img.findViewById(R.id.imgV_tab_notify).setBackgroundResource(R.drawable.ic_tab_img);
        tabLayout.getTabAt(2).setCustomView(tab_img);
        View tab_food = getLayoutInflater().inflate(R.layout.tab_notify, null);
        tab_food.findViewById(R.id.imgV_tab_notify).setBackgroundResource(R.drawable.ic_tab_food);
        tabLayout.getTabAt(3).setCustomView(tab_food);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(getResources().getColor(R.color.color_selection_report));
                        }
                        int lastColor = getResources()
                                .getColor(R.color.color_selection_report),
                                lastIcon = R.drawable.ic_notify_reportstore;
                        imgv_bg_2.setBackgroundColor(firstcolor);
                        imgv_icon_2.setImageResource(firstIcon);
                        imgv_bg_1.setBackgroundColor(lastColor);
                        imgv_icon_1.setImageResource(lastIcon);
                        firstcolor = lastColor;
                        firstIcon = lastIcon;
                        changeTabAnimate(imgv_bg_2);
                        changeTabAnimate(imgv_icon_2);
                        break;
                    case 1:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
                        }
                        int lastColor1 = getResources()
                                .getColor(R.color.colorPrimary),
                                lastIcon1 = R.drawable.ic_notify_reportpost;
                        imgv_bg_2.setBackgroundColor(firstcolor);
                        imgv_icon_2.setImageResource(firstIcon);
                        imgv_bg_1.setBackgroundColor(lastColor1);
                        imgv_icon_1.
                                setImageResource(lastIcon1);
                        firstcolor = lastColor1;
                        firstIcon = lastIcon1;
                        changeTabAnimate(imgv_bg_2);
                        changeTabAnimate(imgv_icon_2);
                        break;
                    case 2:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(getResources().getColor(R.color.color_notify_reportimg));
                        }
                        int lastColor2 = getResources()
                                .getColor(R.color.color_notify_reportimg),
                                lastIcon2 = R.drawable.ic_notify_reportimg;
                        imgv_bg_2.setBackgroundColor(firstcolor);
                        imgv_icon_2.setImageResource(firstIcon);
                        imgv_bg_1.setBackgroundColor(lastColor2);
                        imgv_icon_1.
                                setImageResource(lastIcon2);
                        firstcolor = lastColor2;
                        firstIcon = lastIcon2;
                        changeTabAnimate(imgv_bg_2);
                        changeTabAnimate(imgv_icon_2);
                        break;
                    case 3:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(getResources().getColor(R.color.color_notify_reportfood));
                        }
                        int lastColor3 = getResources()
                                .getColor(R.color.color_notify_reportfood),
                                lastIcon3 = R.drawable.ic_notify_reportfood;
                        imgv_bg_2.setBackgroundColor(firstcolor);
                        imgv_icon_2.setImageResource(firstIcon);
                        imgv_bg_1.setBackgroundColor(lastColor3);
                        imgv_icon_1.
                                setImageResource(lastIcon3);
                        firstcolor = lastColor3;
                        firstIcon = lastIcon3;
                        changeTabAnimate(imgv_bg_2);
                        changeTabAnimate(imgv_icon_2);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void changeTabAnimate(View view1) {
        ObjectAnimator alphaTo0 =
                ObjectAnimator.ofFloat(view1, "alpha", 1, 0);
        alphaTo0.setDuration(200);
        alphaTo0.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }
}
