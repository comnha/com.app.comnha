package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.ptt.comnha.Models.FireBase.Image;
import com.app.ptt.comnha.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by PTT on 10/24/2016.
 */

public class Photo_recycler_adapter extends RecyclerView.Adapter<Photo_recycler_adapter.Viewholder> {
    private ArrayList<Image> photos;
    private StorageReference stRef;
    private OnItemClickLiestner onItemClickLiestner;

    public interface OnItemClickLiestner {
        void onItemClick(Image post);
    }

    public void setOnItemClickLiestner(OnItemClickLiestner liestner) {
        onItemClickLiestner = liestner;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcyler_photos, parent, false));
    }

    @Override
    public void onBindViewHolder(final Viewholder holder, int position) {
        Log.i("Add photo", "Add");
        StorageReference imgRef = stRef.child(photos.get(position)
                .getName());
        Glide.with(activity)
                .using(new FirebaseImageLoader())
                .load(imgRef)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickLiestner.onItemClick(photos.get(holder.getAdapterPosition()));
            }
        });

    }

    private Activity activity;

    public Photo_recycler_adapter(ArrayList<Image> photos, StorageReference stRef, Activity activity) {
        this.photos = photos;
        this.stRef = stRef;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public Viewholder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_rcyler_photo_imgV);
        }
    }


}
