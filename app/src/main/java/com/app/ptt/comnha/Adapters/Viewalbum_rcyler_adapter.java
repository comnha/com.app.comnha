package com.app.ptt.comnha.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.ptt.comnha.FireBase.Image;
import com.app.ptt.comnha.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by PTT on 11/28/2016.
 */

public class Viewalbum_rcyler_adapter extends RecyclerView.Adapter<Viewalbum_rcyler_adapter.ViewHoler> {
    ArrayList<Image> images;

    public Viewalbum_rcyler_adapter(ArrayList<Image> images) {
        this.images = images;
    }

    @Override
    public ViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHoler(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcyler_viewalbum, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHoler holder, int position) {
//        Picasso.with(holder.img.getContext())
//                .load(images.get(position).getPath())
//                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ViewHoler extends RecyclerView.ViewHolder {
        ImageView img;

        public ViewHoler(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.item_rcyler_viewalbum_imgV);
        }
    }
}
