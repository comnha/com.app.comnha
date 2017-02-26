package com.app.ptt.comnha.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.app.ptt.comnha.Fragment.NotificationFullFragment;
import com.app.ptt.comnha.Fragment.PhotoFragment;
import com.app.ptt.comnha.Fragment.ProfileFragment;

/**
 * Created by PTT on 10/22/2016.
 */

public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    final int PAGE_COUNT1= 1;
    private String tabtitles[] = new String[]{"Profile", "Activities", "Photos"};
    private String tab1titles[] = new String[]{ "Thông báo"};
    private Context context;
    int a;
    public FragmentPagerAdapter(FragmentManager fm, Context context, int a) {
        super(fm);
        this.context = context;
        this.a=a;
    }

    @Override
    public Fragment getItem(int position) {
        if(a==1) {
            switch (position) {
                case 0:
                    return new ProfileFragment();
                case 1:
                    //return new ActivityFragment();
                case 2:
                    return new PhotoFragment();
            }
        }
        if(a==2) {
            switch (position) {
                case 0:
                    return new NotificationFullFragment();
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        if(a==1)
        return PAGE_COUNT;
        else return PAGE_COUNT1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(a==1)
        return tabtitles[position];
        else
            return tab1titles[position];
    }
}
