package com.app.ptt.comnha.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.ptt.comnha.Models.Page;
import com.app.ptt.comnha.R;

import java.util.ArrayList;

/**
 * Created by PTT on 5/13/2017.
 */

public class AlbumCateVPAdapter extends PagerAdapter {
    Context context;
    OnBtnItemClickListener onBtnItemClickListener;
    ArrayList<Page> pages;

    public void setOnBtnItemClickListener(
            AlbumCateVPAdapter.OnBtnItemClickListener onBtnItemClickListener) {
        this.onBtnItemClickListener = onBtnItemClickListener;
    }

    public interface OnBtnItemClickListener {
        void onClick(int position, ViewGroup viewGroup);
    }

    public AlbumCateVPAdapter(Context context, ArrayList<Page> pages) {
        this.context = context;
        this.pages = pages;
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, final int position) {
        Page page = pages.get(position);
        final ViewGroup layout = (ViewGroup) LayoutInflater
                .from(context)
                .inflate(page.getLayoutResId(), viewGroup, false);
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
                        if (onBtnItemClickListener != null) {
                            onBtnItemClickListener.onClick(position, layout);
                        }
                    }
                });
                text_des.setText(page.getTitle());
                break;
            case 1:
                text_des = (TextView) layout.findViewById(R.id.tv_profileimg_pager);
                text_des.setText(page.getTitle());
                btn_open = (Button) layout.findViewById(R.id.btn_openProfileimg_pager);
                btn_open.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onBtnItemClickListener != null) {
                            onBtnItemClickListener.onClick(position, layout);
                        }
                    }
                });
                break;
            case 2:
                text_des = (TextView) layout.findViewById(R.id.tv_bannerimg_pager);
                text_des.setText(page.getTitle());
                btn_open = (Button) layout.findViewById(R.id.btn_openBanner_pager);
                btn_open.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onBtnItemClickListener != null) {
                            onBtnItemClickListener.onClick(position, layout);
                        }
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

}
