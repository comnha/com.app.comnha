package com.app.ptt.comnha.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.app.ptt.comnha.Fragment.AdminReportFoodFragment;
import com.app.ptt.comnha.Fragment.AdminReportImgFragment;
import com.app.ptt.comnha.Fragment.AdminReportPostFragment;
import com.app.ptt.comnha.Fragment.AdminReportStoreFragment;

/**
 * Created by PTT on 4/7/2017.
 */

public class AdminNotifyReportsFragAdapter extends FragmentStatePagerAdapter {
    public AdminNotifyReportsFragAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AdminReportStoreFragment();
            case 1:
                return new AdminReportPostFragment();
            case 2:
                return new AdminReportImgFragment();
            case 3:
                return new AdminReportFoodFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
