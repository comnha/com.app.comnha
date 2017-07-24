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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.ptt.comnha.Interfaces.OnLoadMoreListener;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Modules.orderByPostHightLight;
import com.app.ptt.comnha.Modules.orderObjectByTime;
import com.app.ptt.comnha.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by PTT on 11/26/2016.
 */

public class Post_recycler_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Post> posts;
    Context context;
    OnItemClickLiestner onItemClickLiestner;
    OnLoadMoreListener onLoadMoreListener;

    boolean isLoading = false;

    public boolean isMoreDataAvailable() {
        return isMoreDataAvailable;
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    boolean isMoreDataAvailable = true;;
    StorageReference stRef;
    public static  final  int TYPE_LOADING=0,TYPE_ITEM=1;
    public interface OnItemClickLiestner {
        void onItemClick(Post post, View itemView);
    }
    public void setLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        this.onLoadMoreListener=onLoadMoreListener;
    }
    public void setOnItemClickLiestner(OnItemClickLiestner liestner) {
        onItemClickLiestner = liestner;
    }

    public Post_recycler_adapter( Context context, StorageReference stRef) {
        this.posts = posts;
        this.context = context;
        this.stRef = stRef;
    }
    public Post_recycler_adapter( ArrayList<Post> posts,Context context, StorageReference stRef) {
        this.posts = posts;
        this.context = context;
        this.stRef = stRef;
    }
    public void addToList(Post post){
        if(posts==null){
            posts=new ArrayList<>();
        }
        posts.add(post);
        notifyItemInserted(posts.size()-1);

     //   notifyDataSetChanged();
    }
    public void removeLastItemFromList(){
        if(posts==null){
            posts=new ArrayList<>();
        }
        if(posts.size()>0) {
            posts.remove(posts.size() - 1);
            notifyItemRemoved(posts.size()-1);
        }
    }
    public void clearList(){
        posts=new ArrayList<>();
        notifyDataSetChanged();
    }
    public void addList(List<Post> posts){
        if(this.posts==null){
            this.posts=new ArrayList<>();
        }
        this.posts.addAll(posts);
        notifyDataSetChanged();
    }
    public void setIsLoading(boolean isLoading){
        this.isLoading=isLoading;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case 0:
                return new LoaddingViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_loading, parent, false));

            case 1:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_post, parent, false));

        }
        return null;

    }

    @Override
    public int getItemViewType(int position) {
        return posts.get(position)==null ?TYPE_LOADING:TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder1, final int position) {
        if(position>=getItemCount()-1&&posts.size()>=3 && isMoreDataAvailable && !isLoading && onLoadMoreListener!=null){
            isLoading = true;
            onLoadMoreListener.loadMore();

        }
        if(holder1 instanceof LoaddingViewHolder){
            LoaddingViewHolder holderLoading=(LoaddingViewHolder) holder1;
            holderLoading.pb.setIndeterminate(true);
        }
        if(holder1 instanceof ItemViewHolder){
            final ItemViewHolder holder=(ItemViewHolder) holder1;
            Log.d("postAdapter", posts.size() + "");
            holder.txtv_date_un.setText(posts.get(position).getDate() + " - " +
                    posts.get(position).getUn());
            holder.txtv_storename.setText(posts.get(position).getStoreName());
            holder.txtv_title.setText(posts.get(position).getTitle());
            if (posts.get(holder.getAdapterPosition()).getImgBitmap() == null) {
                if (!posts.get(holder.getAdapterPosition()).getBanner().equals("")) {
                    StorageReference imgRef = stRef.child(posts.get(holder.getAdapterPosition())
                            .getBanner());
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(context)
                                    .load(uri).memoryPolicy(MemoryPolicy.NO_CACHE )
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .into(holder.imgv_banner);
                            try {
                                Bitmap imgBitmap = ((BitmapDrawable) holder.imgv_banner.getDrawable())
                                        .getBitmap();
                                posts.get(holder.getAdapterPosition()).setImgBitmap(imgBitmap);

                            } catch (NullPointerException e) {

                            }
                        }
                    });
                } else {
                    holder.imgv_banner.setBackgroundResource(R.drawable.img_banner);
//                    try {
//                        Bitmap imgBitmap = ((BitmapDrawable) holder.imgv_banner.getDrawable())
//                                .getBitmap();
//                        posts.get(holder.getAdapterPosition()).setImgBitmap(imgBitmap);
//                    } catch (NullPointerException e) {
//
//                    }
                }
            } else {
                holder.imgv_banner.setImageBitmap(posts.get(position).getImgBitmap());
            }
            if (posts.get(position).isHidden()) {
                holder.cardView.setBackgroundColor(
                        context.getResources().getColor(R.color.colorFabRipple));
            } else {
                holder.cardView.setBackgroundColor(
                        context.getResources().getColor(android.R.color.white));
            }
            if (onItemClickLiestner != null) {
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if (posts.get(position).getImgBitmap() == null) {
//                            try {
////                                Bitmap imgBitmap = ((BitmapDrawable) holder.imgv_banner.getDrawable())
////                                        .getBitmap();
////                                posts.get(holder.getAdapterPosition()).setImgBitmap(imgBitmap);
//                                onItemClickLiestner.onItemClick(posts.get(holder.getAdapterPosition()),
//                                        holder.itemView);
//                            } catch (NullPointerException e) {
//                                onItemClickLiestner.onItemClick(posts.get(holder.getAdapterPosition()),
//                                        holder.itemView);
//                            }
//                        } else {
                            onItemClickLiestner.onItemClick(posts.get(holder.getAdapterPosition()),
                                    holder.itemView);
                       // }
                    }
                });
            }
        }


    }
    public void sortByType(int type){
        switch (type){

            //newest
            case 1:
                Collections.sort(posts,new orderObjectByTime());
                break;
            //highlight
            case 2:
                Collections.sort(posts,new orderByPostHightLight());
                break;

        }
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return posts==null ?0: posts.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtv_date_un, txtv_storename,
                txtv_title;
        ImageView imgv_banner, imgv_share;

        public ItemViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardv_postitem);
            txtv_date_un = (TextView) itemView.findViewById(R.id.txtv_date_un_postitem);
            txtv_storename = (TextView) itemView.findViewById(R.id.txtv_storename_postitem);
            txtv_title = (TextView) itemView.findViewById(R.id.txtv_title_postitem);
            imgv_banner = (ImageView) itemView.findViewById(R.id.imgv_banner_postitem);
            imgv_share = (ImageView) itemView.findViewById(R.id.imgv_share_postitem);
        }
    }
    public static class LoaddingViewHolder extends RecyclerView.ViewHolder{
        ProgressBar pb;
        public LoaddingViewHolder(View itemView) {
            super(itemView);
            pb = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }
    public int getSize(){
        return posts==null?0: posts.size();
    }

}
