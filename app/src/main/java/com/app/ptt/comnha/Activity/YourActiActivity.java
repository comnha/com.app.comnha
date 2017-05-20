package com.app.ptt.comnha.Activity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.app.ptt.comnha.Adapters.YourActiCateVPAdapter;
import com.app.ptt.comnha.Models.Page;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Utils.AppUtils;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourActiActivity extends AppCompatActivity {
    YourActiCateVPAdapter pagerAdapter;
    ViewPager viewPager;
    Toolbar toolbar;
    ArrayList<Page> pages;

    public YourActiActivity() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youracti);
        Ref();
    }

    private void Ref() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(
                    getResources().getColor(R.color.colorPrimary));
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar_youracti);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setTitle(getString(R.string.txt_youracti));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        viewPager = (ViewPager) findViewById(R.id.viewpager_youracti);
        getPage();
        pagerAdapter = new YourActiCateVPAdapter(this, pages);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setClipToPadding(false);
        viewPager.setPadding((int) AppUtils.dipToPixels(this, 32),
                (int) AppUtils.dipToPixels(this, 16),
                (int) AppUtils.dipToPixels(this, 32),
                (int) AppUtils.dipToPixels(this, 16));
        viewPager.setPageMargin((int) AppUtils.dipToPixels(this, 32));
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
        pagerAdapter.setOnBtnItemClickListener(new YourActiCateVPAdapter.OnBtnItemClickListener() {
            @Override
            public void onClick(int position, ViewGroup viewGroup) {
                switch (position) {
                    case 0:
                        Intent open_post = new Intent(YourActiActivity.this,
                                YourPostActivity.class);
                        startActivity(open_post);
                        break;
                    case 1:
                        Intent open_store = new Intent(YourActiActivity.this,
                                YourAddStoreActivity.class);
                        startActivity(open_store);
                        break;
                    case 2:
                        Intent open_food = new Intent(YourActiActivity.this,
                                YourAddFoodActivity.class);
                        startActivity(open_food);
                        break;

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void getPage() {
        pages = new ArrayList<>();
        pages.add(new Page(R.layout.layout_pager_yourpost,
                getString(R.string.txt_yourpost)));
        pages.add(new Page(R.layout.layout_pager_uraddstore,
                getString(R.string.txt_uraddstore)));
        pages.add(new Page(R.layout.layout_pager_uraddfood,
                getString(R.string.txt_uraddfood)));
    }
}
