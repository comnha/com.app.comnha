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

import com.app.ptt.comnha.Adapters.AlbumCateVPAdapter;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Utils.AppUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumActivity extends AppCompatActivity {
    AlbumCateVPAdapter pagerAdapter;
    ViewPager viewPager;
    Toolbar toolbar;

    public AlbumActivity() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        Ref();
    }

    private void Ref() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(
                    getResources().getColor(R.color.colorPrimary));
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar_album);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setTitle(getString(R.string.txt_yourphoto));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        viewPager = (ViewPager) findViewById(R.id.viewpager_album);
        pagerAdapter = new AlbumCateVPAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setClipToPadding(false);
        viewPager.setPadding((int) AppUtils.dipToPixels(this, 32),
                (int) AppUtils.dipToPixels(this, 16),
                (int) AppUtils.dipToPixels(this, 32),
                (int) AppUtils.dipToPixels(this, 16));
        viewPager.setPageMargin((int) AppUtils.dipToPixels(this, 32));
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
        pagerAdapter.setOnBtnItemClickListener(new AlbumCateVPAdapter.OnBtnItemClickListener() {
            @Override
            public void onClick(int position, ViewGroup viewGroup) {
                switch (position) {
                    case 0:
                        Intent open_allimg = new Intent(AlbumActivity.this,
                                AllPhotoActivity.class);
                        startActivity(open_allimg);
                        break;
                    case 1:
                        Intent open_profile = new Intent(AlbumActivity.this,
                                ProfilePhotoActivity.class);
                        startActivity(open_profile);
                        break;
                    case 2:
                        Intent open_banner = new Intent(AlbumActivity.this,
                                BannerPhotoActivity.class);

                        startActivity(open_banner);
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

}
