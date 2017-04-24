package com.app.ptt.comnha.Activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.R;

public class SearchActivity extends AppCompatActivity {
    ImageView imgv_back, imgv_reveal;
    TabLayout tabLayout;
    RelativeLayout relative_temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ref();
    }

    private void ref() {
        imgv_back = (ImageView) findViewById(R.id.imgv_back_search);
        imgv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        imgv_reveal = (ImageView) findViewById(R.id.imgv_reveal_search);
        relative_temp = (RelativeLayout) findViewById(R.id.relative_search);
        relative_temp.setBackgroundColor(
                getResources().getColor(R.color.color_notify_reportfood));
        tabLayout = (TabLayout) findViewById(R.id.tablayout_search);
        View foodtabview = LayoutInflater.from(this).inflate(R.layout.tab_search, null);
        foodtabview.findViewById(R.id.imgv_icon_tabsearch)
                .setBackgroundResource(R.drawable.ic_tab_food);
        TabLayout.Tab foodtab = tabLayout.newTab().setCustomView(foodtabview);
        View storetabview = LayoutInflater.from(this).inflate(R.layout.tab_search, null);
        storetabview.findViewById(R.id.imgv_icon_tabsearch)
                .setBackgroundResource(R.drawable.ic_tab_store);
        TabLayout.Tab storetab = tabLayout.newTab().setCustomView(storetabview);
        tabLayout.addTab(foodtab, 0);
        tabLayout.addTab(storetab, 1);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int top = tabLayout.getTop(), btm = imgv_reveal.getBottom(),
                        left = tabLayout.getLeft(),
                        right = tabLayout.getRight(), h = tabLayout.getHeight(),
                        w = tabLayout.getWidth();
                int cx = 0,
                        cy = 0;
                long duration = 250;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    switch (tab.getPosition()) {
                        case 0:
                            cx = left;
                            cy = btm;
                            relative_temp.setBackgroundColor(getResources()
                                    .getColor(R.color.admin_color_selection_news));
                            imgv_reveal.setBackgroundColor(
                                    getResources()
                                            .getColor(R.color.color_notify_reportfood));
                            AnimationUtils.getInstance()
                                    .createCircularReveal(imgv_reveal, duration, cx, cy);
                            getWindow().setStatusBarColor(
                                    getResources().getColor(R.color.color_notify_reportfood));
                            break;
                        case 1:
                            cx = right;
                            cy = btm;
                            relative_temp.setBackgroundColor(getResources()
                                    .getColor(R.color.color_notify_reportfood));
                            imgv_reveal.setBackgroundColor(
                                    getResources()
                                            .getColor(R.color.admin_color_selection_news));
                            AnimationUtils.getInstance()
                                    .createCircularReveal(imgv_reveal, duration, cx, cy);
                            getWindow().setStatusBarColor(
                                    getResources().getColor(R.color.admin_color_selection_news));
                            break;
                    }
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
