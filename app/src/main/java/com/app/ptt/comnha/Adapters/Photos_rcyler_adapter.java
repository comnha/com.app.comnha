package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.ptt.comnha.FireBase.Image;
import com.app.ptt.comnha.R;

import java.util.ArrayList;

/**
 * Created by PTT on 10/24/2016.
 */

public class Photos_rcyler_adapter extends RecyclerView.Adapter<Photos_rcyler_adapter.Viewholder> {
    private ArrayList<Image> photoList;

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcyler_photos, parent, false));
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {
        Log.i("Add photo","Add");
//        Picasso.with(activity)
//                .load(photoList.get(position).getPath())
//                .into(holder.imageView);
    }

    private Activity activity;

    public Photos_rcyler_adapter(ArrayList<Image> photoList, Activity activity) {
        Log.i("LIST SIZE:",photoList.size()+"");
        this.photoList = photoList;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public Viewholder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_rcyler_photo_imgV);
        }
    }


}
