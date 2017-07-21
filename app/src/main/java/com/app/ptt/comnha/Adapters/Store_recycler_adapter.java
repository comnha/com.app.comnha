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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Interfaces.OnLoadMoreListener;

import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Modules.orderByDistance;
import com.app.ptt.comnha.Modules.orderByHealthy;
import com.app.ptt.comnha.Modules.orderByPrice;
import com.app.ptt.comnha.Modules.orderByService;
import com.app.ptt.comnha.Modules.orderByStoreHightLight;
import com.app.ptt.comnha.Modules.orderObjectByTime;
import com.app.ptt.comnha.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by PTT on 11/26/2016.
 */

public class Store_recycler_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Store> stores;
    OnItemClickLiestner onItemClickLiestner;
    StorageReference stRef;
    Context context;
    boolean isMoreDataAvailable = true;
    OnLoadMoreListener onLoadMoreListener;
    boolean isLoading;
    public static  final  int TYPE_LOADING=0,TYPE_ITEM=1;
    public OnLoadMoreListener getOnLoadMoreListener() {
        return onLoadMoreListener;
    }

    public void setLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }



    public boolean isMoreDataAvailable() {
        return isMoreDataAvailable;
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    @Override
    public int getItemViewType(int position) {
        return stores.get(position)==null ?TYPE_LOADING:TYPE_ITEM;
    }

    public void setStorageRef(StorageReference stRef) {
        this.stRef = stRef;
    }

    public interface OnItemClickLiestner {
        void onItemClick(Store store, View itemView);
    }

    public void setOnItemClickLiestner(OnItemClickLiestner liestner) {
        onItemClickLiestner = liestner;
    }

    public Store_recycler_adapter(Context context) {
        this.stores = stores;
        this.context = context;
    }

    public void addToList(Store store) {
        if (stores == null) {
            stores = new ArrayList<>();
        }
        stores.add(store);
        notifyItemInserted(stores.size() - 1);
    }

    public void removeLastItemFromList() {
        if (stores == null) {
            stores = new ArrayList<>();
        }
        if (stores.size() > 0) {
            stores.remove(stores.size() - 1);
            notifyItemRemoved(stores.size() - 1);
        }
    }
    public void setIsLoading(boolean isLoading){
        this.isLoading=isLoading;
    }
    public Store_recycler_adapter(ArrayList<Store> stores, Context context,
                                  StorageReference stRef) {
        this.stores = stores;
        this.context = context;
        this.stRef = stRef;
    }

    public Store_recycler_adapter(Context context,
                                  StorageReference stRef) {
        this.context = context;
        this.stRef = stRef;
    }

    public void addList(List<Store> stores) {
        if (this.stores == null) {
            this.stores = new ArrayList<>();
        }
        this.stores.addAll(stores);
        notifyDataSetChanged();
    }

    public void clearList() {
        stores = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new LoaddingViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_loading, parent, false));

            case 1:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_store, parent, false));

        }
        return null;

    }

    public static class LoaddingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar pb;

        public LoaddingViewHolder(View itemView) {
            super(itemView);
            pb = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder1, final int position) {
        if (position >= getItemCount() - 1 && stores.size() >= 3 && isMoreDataAvailable && !isLoading && onLoadMoreListener != null) {
            isLoading = true;
            onLoadMoreListener.loadMore();
        }
        if (holder1 instanceof LoaddingViewHolder) {
            LoaddingViewHolder holderLoading = (LoaddingViewHolder) holder1;
            holderLoading.pb.setIndeterminate(true);
        }
        if (holder1 instanceof ItemViewHolder) {
            final ItemViewHolder holder = (ItemViewHolder) holder1;
            if (null != stores.get(position).getDistance()) {
                holder.txtv_distance.setText(stores.get(position).getDistance() + " km");
            } else {
                AnimationUtils.fadeAnimation(holder.itemView, 700, true, 0);
            }
            holder.txtv_storename.setText(stores.get(position).getName());
            holder.txtv_address.setText(stores.get(position).getAddress());
            holder.txtv_rate.setText(String.format(Locale.getDefault(), Const.DECIMAL_1F, stores.get(position).getRateAVG()));

            holder.txtv_opentime.setText(stores.get(position).getOpentime());
            holder.txtv_phonenumb.setText(stores.get(position).getPhonenumb());
            if (stores.get(holder.getAdapterPosition()).getImgBitmap() == null) {
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
                            try {
                                Bitmap imgBitmap = ((BitmapDrawable) holder.imgv_avatar.getDrawable())
                                        .getBitmap();
                                if (stores.size() > position) {
                                    stores.get(position).setImgBitmap(imgBitmap);
                                }
                            } catch (NullPointerException e) {

                            }
                        }
                    });
                } else {
                    holder.imgv_avatar.setImageResource(R.drawable.ic_item_store);
                }
            } else {
                holder.imgv_avatar.setImageBitmap(stores.get(position).getImgBitmap());
            }
            if (stores.get(position).isHidden()) {
                holder.cardView.setBackgroundColor(
                        context.getResources().getColor(R.color.colorFabRipple));
            } else {
                holder.cardView.setBackgroundColor(
                        context.getResources().getColor(android.R.color.white));
            }
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (stores.get(position).getImgBitmap() == null) {
                        try {
                            Bitmap imgBitmap = ((BitmapDrawable) holder.imgv_avatar.getDrawable())
                                    .getBitmap();
                            stores.get(holder.getAdapterPosition()).setImgBitmap(imgBitmap);
                            onItemClickLiestner.onItemClick(stores.get(position),
                                    holder.itemView);
                        } catch (NullPointerException e) {
                            onItemClickLiestner.onItemClick(stores.get(position),
                                    holder.itemView);
                        }
                    } else {
                        onItemClickLiestner.onItemClick(stores.get(position),
                                holder.itemView);
                    }
                }
            });
        }
    }

    public void sortByType(int type) {
        switch (type) {

            //newest
            case 1:
                Collections.sort(stores,new orderObjectByTime());
                break;
            //highlight
            case 2:
                Collections.sort(stores, new orderByStoreHightLight());
                break;
            //gia
            case 3:
                Collections.sort(stores, new orderByPrice());
                break;
            //ve sinh
            case 4:
                Collections.sort(stores, new orderByHealthy());
                break;
            //phuc vu
            case 5:
                Collections.sort(stores, new orderByService());
                break;
            //kc
            case 6:
                Collections.sort(stores, new orderByDistance());
                break;

        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return stores == null ? 0 :
                stores.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtv_address, txtv_storename, txtv_distance, txtv_rate,
                txtv_opentime, txtv_phonenumb;
        CircularImageView imgv_avatar;

        public ItemViewHolder(View itemView) {
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
    public int getSize(){
        return stores==null?0: stores.size();
    }
}
