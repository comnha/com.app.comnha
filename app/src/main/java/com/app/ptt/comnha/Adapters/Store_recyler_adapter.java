package com.app.ptt.comnha.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class Store_recyler_adapter extends RecyclerView.Adapter<Store_recyler_adapter.ViewHolder> {
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

    public Store_recyler_adapter(ArrayList<Store> stores, Context context) {
        this.stores = stores;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_store, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.txtv_storename.setText(stores.get(position).getName());
        holder.txtv_address.setText(stores.get(position).getAddress());
        holder.txtv_rate.setText(String.valueOf(stores.get(position).getRateAVG()));
//        holder.txtv_distance.setText(posts.get(position).getName());
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
                            .placeholder(R.drawable.ic_item_store)
                            .into(holder.imgv_avatar);
                }
            });
//            Glide.with(context)
//                    .using(new FirebaseImageLoader())
//                    .load(imgRef)
//                    .placeholder(R.drawable.ic_item_store)
//                    .into(holder.imgv_avatar).getRequest().begin();
//            notifyItemChanged(holder.getAdapterPosition());
        } else {
            holder.imgv_avatar.setImageResource(R.drawable.ic_item_store);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickLiestner.onItemClick(stores.get(holder.getAdapterPosition()), holder.itemView);
            }
        });
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
            cardView = (CardView) itemView.findViewById(R.id.item_list_cardV);
            txtv_address = (TextView) itemView.findViewById(R.id.item_list_txtvaddress);
            txtv_storename = (TextView) itemView.findViewById(R.id.item_list_txtvstorename);
            txtv_rate = (TextView) itemView.findViewById(R.id.item_list_txtvrate);
            txtv_distance = (TextView) itemView.findViewById(R.id.item_list_txtvdistance);
            txtv_opentime = (TextView) itemView.findViewById(R.id.item_list_txtvopentime);
            txtv_phonenumb = (TextView) itemView.findViewById(R.id.item_list_txtvphonenumb);
            imgv_avatar = (CircularImageView) itemView.findViewById(R.id.item_list_imgV);
        }
    }

}
