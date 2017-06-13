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
import android.widget.TextView;

import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by PTT on 11/26/2016.
 */

public class Post_recycler_adapter extends RecyclerView.Adapter<Post_recycler_adapter.ViewHolder> {
    ArrayList<Post> posts;
    OnItemClickLiestner onItemClickLiestner;
    Context context;
    StorageReference stRef;

    public interface OnItemClickLiestner {
        void onItemClick(Post post, View itemView);
    }

    public void setOnItemClickLiestner(OnItemClickLiestner liestner) {
        onItemClickLiestner = liestner;
    }

    public Post_recycler_adapter(ArrayList<Post> posts, Context context, StorageReference stRef) {
        this.posts = posts;
        this.context = context;
        this.stRef = stRef;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_post, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d("postAdapter", posts.size() + "");
        holder.txtv_date_un.setText(posts.get(position).getDate() + " - " +
                posts.get(position).getUn());
        holder.txtv_storename.setText(posts.get(position).getStoreName());
        holder.txtv_title.setText(posts.get(position).getTitle());
        if (!posts.get(holder.getAdapterPosition()).getBanner().equals("")) {
            StorageReference imgRef = stRef.child(posts.get(holder.getAdapterPosition())
                    .getBanner());
            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(context)
                            .load(uri)
                            .into(holder.imgv_banner);
                }
            });
        } else {
            holder.imgv_banner.setVisibility(View.GONE);
        }
        if (onItemClickLiestner != null) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Bitmap imgBitmap = ((BitmapDrawable) holder.imgv_banner.getDrawable())
                                .getBitmap();
                        posts.get(holder.getAdapterPosition()).setImgBitmap(imgBitmap);
                        onItemClickLiestner.onItemClick(posts.get(holder.getAdapterPosition()),
                                holder.itemView);
                    } catch (NullPointerException e) {
                        onItemClickLiestner.onItemClick(posts.get(holder.getAdapterPosition()),
                                holder.itemView);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtv_date_un, txtv_storename,
                txtv_title;
        ImageView imgv_banner, imgv_share;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardv_postitem);
            txtv_date_un = (TextView) itemView.findViewById(R.id.txtv_date_un_postitem);
            txtv_storename = (TextView) itemView.findViewById(R.id.txtv_storename_postitem);
            txtv_title = (TextView) itemView.findViewById(R.id.txtv_title_postitem);
            imgv_banner = (ImageView) itemView.findViewById(R.id.imgv_banner_postitem);
            imgv_share = (ImageView) itemView.findViewById(R.id.imgv_share_postitem);
        }
    }


}
