package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by PTT on 9/27/2016.
 */

public class Storeselection_rcyler_adapter extends RecyclerView.Adapter<Storeselection_rcyler_adapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgV;
        public TextView txt_address, txt_name, txt_opentime, txt_phonenumb;
        RelativeLayout relative_parent;

        public ViewHolder(View itemView) {
            super(itemView);
            imgV = (ImageView) itemView
                    .findViewById(R.id.item_storeselect_imgV);
            txt_address = (TextView) itemView
                    .findViewById(R.id.item_storeselect_txtvaddress);
            txt_name = (TextView) itemView
                    .findViewById(R.id.item_storeselect_txtvstorename);
            txt_opentime = (TextView) itemView.
                    findViewById(R.id.item_storeselect_txtvopentime);
            txt_phonenumb = (TextView) itemView.
                    findViewById(R.id.item_storeselect_txtvphonenumb);
            relative_parent = (RelativeLayout) itemView
                    .findViewById(R.id.item_storeselect_relative);
        }
    }
    OnItemClickLiestner onItemClickLiestner;

    Activity activity;
    ArrayList<Store> stores;
    StorageReference stRef;

    public Storeselection_rcyler_adapter(Activity activity,
                                         ArrayList<Store> stores, StorageReference stRef) {
        this.activity = activity;
        this.stores = stores;
        this.stRef = stRef;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcyler_storeselection, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.txt_name.setText(stores.get(position).getName());
        holder.txt_address.setText(stores.get(position).getAddress());
        holder.txt_opentime.setText(stores.get(position).getOpentime());
        holder.txt_phonenumb.setText(stores.get(position).getPhonenumb());
        if (!stores.get(holder.getAdapterPosition()).getStoreimg().equals("")) {
            StorageReference imgRef = stRef.child(stores.get(holder.getAdapterPosition())
                    .getStoreimg());
            Log.d("imgName", stores.get(holder.getAdapterPosition())
                    .getStoreimg());
            Log.d("Imgpath", imgRef.getDownloadUrl() + "");
            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d("getUrl().addOnSuccess", uri.toString() + "");
//                    holder.imgv_avatar.setImageURI(uri);
                    Picasso.with(activity)
                            .load(uri)
                            .into(holder.imgV);
                }
            });
        } else {
            holder.imgV.setImageResource(R.drawable.ic_item_store);
        }
        holder.relative_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickLiestner.onItemClick(stores.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    public interface OnItemClickLiestner {
        void onItemClick(Store store);
    }

    public void setOnItemClickLiestner(OnItemClickLiestner liestner) {
        onItemClickLiestner = liestner;
    }
}
