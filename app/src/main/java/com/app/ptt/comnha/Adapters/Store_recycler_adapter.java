package com.app.ptt.comnha.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by PTT on 11/26/2016.
 */

public class Store_recycler_adapter extends RecyclerView.Adapter<Store_recycler_adapter.ViewHolder> {
    ArrayList<Store> stores;
    OnItemClickLiestner onItemClickLiestner;
    StorageReference stRef;
    Context context;

    public void setStorageRef(StorageReference stRef) {
        this.stRef = stRef;
    }

    public interface OnItemClickLiestner {
        void onItemClick(Store store, View itemView);
    }

    public void setOnItemClickLiestner(OnItemClickLiestner liestner) {
        onItemClickLiestner = liestner;
    }

    public Store_recycler_adapter(ArrayList<Store> stores, Context context) {
        this.stores = stores;
        this.context = context;
    }

    public Store_recycler_adapter(ArrayList<Store> stores, Context context,
                                  StorageReference stRef) {
        this.stores = stores;
        this.context = context;
        this.stRef = stRef;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_store, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        AnimationUtils.fadeAnimation(holder.itemView, 700, 0);
        holder.txtv_storename.setText(stores.get(position).getName());
        holder.txtv_address.setText(stores.get(position).getAddress());
        holder.txtv_rate.setText(String.valueOf(stores.get(position).getRateAVG()));
        holder.txtv_distance.setText(stores.get(position).getDistance()+" km");
        holder.txtv_opentime.setText(stores.get(position).getOpentime());
        holder.txtv_phonenumb.setText(stores.get(position).getPhonenumb());
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
                    Picasso.with(context)
                            .load(uri)
                            .into(holder.imgv_avatar);
                }
            });
        } else {
            holder.imgv_avatar.setImageResource(R.drawable.ic_item_store);
        }
        if (onItemClickLiestner != null) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Bitmap imgBitmap = ((BitmapDrawable) holder.imgv_avatar.getDrawable())
                                .getBitmap();
                        stores.get(holder.getAdapterPosition()).setImgBitmap(imgBitmap);
                        onItemClickLiestner.onItemClick(stores.get(holder.getAdapterPosition()),
                                holder.itemView);
                    } catch (NullPointerException e) {
                        onItemClickLiestner.onItemClick(stores.get(holder.getAdapterPosition()),
                                holder.itemView);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtv_address, txtv_storename, txtv_distance, txtv_rate,
                txtv_opentime, txtv_phonenumb;
        CircularImageView imgv_avatar;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardv_storeitem);
            txtv_address = (TextView) itemView.findViewById(R.id.txtv_address_storeitem);
            txtv_storename = (TextView) itemView.findViewById(R.id.txtv_storename_storeitem);
            txtv_rate = (TextView) itemView.findViewById(R.id.txtv_rate_storeitem);
            txtv_distance = (TextView) itemView.findViewById(R.id.txtv_distance_storeitem);
            txtv_opentime = (TextView) itemView.findViewById(R.id.txtv_opentime_storeitem);
            txtv_phonenumb = (TextView) itemView.findViewById(R.id.txtv_phonenumb_storeitem);
            imgv_avatar = (CircularImageView) itemView.findViewById(R.id.imgv_avatar_storeitem);
        }
    }

}
