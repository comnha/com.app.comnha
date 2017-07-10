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
    String tinh=null,huyen=null;
    public MainFragPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    public void setInfoForPage(String tinh, String huyen){
        this.tinh=tinh;
        this.huyen=huyen;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MainPostFragment m= new MainPostFragment();
                if(tinh!=null&&huyen!=null) {
                    m.setAttribute(tinh,huyen);
                    tinh=null;
                    huyen=null;
                }else{

                }
                return m;
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
