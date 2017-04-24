package com.app.ptt.comnha.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.app.ptt.comnha.Fragment.AdminNewPostFragment;
import com.app.ptt.comnha.Fragment.AdminNewStoreFragment;

/**
 * Created by PTT on 4/7/2017.
 */

public class NotifyNewsFragPagerAdapter extends FragmentStatePagerAdapter {
    public NotifyNewsFragPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AdminNewStoreFragment();
            case 1:
                return new AdminNewPostFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
