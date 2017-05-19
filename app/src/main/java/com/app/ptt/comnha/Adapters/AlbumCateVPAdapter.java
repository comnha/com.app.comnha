package com.app.ptt.comnha.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.ptt.comnha.R;

/**
 * Created by PTT on 5/13/2017.
 */

public class AlbumCateVPAdapter extends PagerAdapter {
    Context context;
    OnBtnItemClickListener onBtnItemClickListener;

    public void setOnBtnItemClickListener(
            AlbumCateVPAdapter.OnBtnItemClickListener onBtnItemClickListener) {
        this.onBtnItemClickListener = onBtnItemClickListener;
    }

    public interface OnBtnItemClickListener {
        void onClick(int position, ViewGroup viewGroup);
    }

    public AlbumCateVPAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return CustomPagerEnum.values().length;
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, final int position) {
        CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];
        final ViewGroup layout = (ViewGroup) LayoutInflater
                .from(context)
                .inflate(customPagerEnum.getLayoutResId(), viewGroup, false);
        viewGroup.addView(layout);
        TextView text_des;
        Button btn_open;
        switch (position) {
            case 0:
                text_des = (TextView) layout.findViewById(R.id.tv_allimg_pager);
                btn_open = (Button) layout.findViewById(R.id.btn_openallimg_pager);
                btn_open.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBtnItemClickListener.onClick(position, layout);
                    }
                });
                text_des.setText(customPagerEnum.getTitle());
                break;
            case 1:
                text_des = (TextView) layout.findViewById(R.id.tv_profileimg_pager);
                text_des.setText(customPagerEnum.getTitle());
                btn_open = (Button) layout.findViewById(R.id.btn_openProfileimg_pager);
                btn_open.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBtnItemClickListener.onClick(position, layout);
                    }
                });
                break;
            case 2:
                text_des = (TextView) layout.findViewById(R.id.tv_banner_pager);
                text_des.setText(customPagerEnum.getTitle());
                btn_open = (Button) layout.findViewById(R.id.btn_openBanner_pager);
                btn_open.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBtnItemClickListener.onClick(position, layout);
                    }
                });
                break;
        }
        return layout;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public float getPageWidth(int position) {
        return 0.85f;
    }

    public enum CustomPagerEnum {

        ALLIMG("tất cả hình ảnh", R.layout.layout_pager_allimg),
        PROFILE("ảnh cá nhân", R.layout.layout_pager_profileimg),
        BANNER("ảnh banner", R.layout.layout_pager_bannerimg);
//        BLUE(R.layout.view_blue),
//        ORANGE(R.layout.view_orange);

        private int mLayoutResId;
        private String title;

        CustomPagerEnum(String title, int layoutResId) {
            this.title = title;
            mLayoutResId = layoutResId;
        }

        public int getLayoutResId() {
            return mLayoutResId;
        }

        public String getTitle() {
            return title;
        }

    }
}
