package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.ptt.comnha.Models.FireBase.Image;
import com.app.ptt.comnha.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by PTT on 11/28/2016.
 */

public class ViewPhotoVPadapter extends PagerAdapter {
    ArrayList<Image> images;
    Activity activity;
    StorageReference stRef;
    OnImageClick onImageClick;

    public interface OnImageClick {
        void onClick();
    }

    public void setOnImageClick(OnImageClick onImageClick) {
        this.onImageClick = onImageClick;
    }

    public ViewPhotoVPadapter(ArrayList<Image> images, Activity activity, StorageReference stRef) {
        this.images = images;
        this.activity = activity;
        this.stRef = stRef;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewGroup layout = (ViewGroup) LayoutInflater.from(activity)
                .inflate(R.layout.layout_pager_viewphoto, container, false);
        container.addView(layout);
        final ImageView imgv = (ImageView) layout.findViewById(R.id.imgv_viewphoto);
        StorageReference imgRef = stRef.child(images.get(position).getName());
        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(activity)
                        .load(uri)
                        .into(imgv);
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onImageClick != null) {
                    onImageClick.onClick();
                }
            }
        });
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
}
