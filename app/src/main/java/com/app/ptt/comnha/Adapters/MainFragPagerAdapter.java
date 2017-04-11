package com.app.ptt.comnha.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.app.ptt.comnha.Fragment.MainNotifyFragment;
import com.app.ptt.comnha.Fragment.MainPostFragment;
import com.app.ptt.comnha.Fragment.MainStoreFragment;

/**
 * Created by PTT on 4/8/2017.
 */

public class MainFragPagerAdapter extends FragmentStatePagerAdapter {
    public MainFragPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MainPostFragment();
            case 1:
                return new MainStoreFragment();
            case 2:
                return new MainNotifyFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
