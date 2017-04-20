package com.app.ptt.comnha.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by PTT on 11/26/2016.
 */

public class Post_recycler_adapter extends RecyclerView.Adapter<Post_recycler_adapter.ViewHolder> {
    ArrayList<Post> posts;
    OnItemClickLiestner onItemClickLiestner;
    Context context;
    StorageReference stRef;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_post, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.txtv_un.setText(posts.get(position).getUn());
        holder.txtv_storename.setText(posts.get(position).getStoreName());
        holder.txtv_time.setText(posts.get(position).getTime());
        holder.txtv_date.setText(posts.get(position).getDate());
        holder.txtv_title.setText(posts.get(position).getTime());
        holder.txtv_content.setText(posts.get(position).getContent());
        if (posts.get(position).getComments() != null) {
            holder.txtv_comcount.setText(posts.get(position).getComments().size()
                    + " " + context.getString(R.string.txt_comment));
        } else {
            holder.txtv_comcount.setText("0" + context.getString(R.string.txt_comment));
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickLiestner.onItemClick(posts.get(position));
            }
        });
        StorageReference avatarRef = stRef.child(posts.get(position)
                .getAvatar()),
                bannerRef = stRef.child(posts.get(position).getBanner());
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(avatarRef)
                .into(holder.imgv_avatar);
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(bannerRef)
                .into(holder.imgv_banner);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtv_un, txtv_time, txtv_date, txtv_storename,
                txtv_title, txtv_content, txtv_comcount, txtv_likecount;
        ImageView imgv_avatar, imgv_banner;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.item_list_cardV);
            txtv_un = (TextView) itemView.findViewById(R.id.post_txtv_un);
            txtv_storename = (TextView) itemView.findViewById(R.id.post_txtv_storename);
            txtv_time = (TextView) itemView.findViewById(R.id.post_txtv_time);
            txtv_date = (TextView) itemView.findViewById(R.id.post_txtv_postdate);
            txtv_title = (TextView) itemView.findViewById(R.id.post_txtv_title);
            txtv_content = (TextView) itemView.findViewById(R.id.post_txtv_content);
            txtv_comcount = (TextView) itemView.findViewById(R.id.post_txtv_commentnumb);
            txtv_likecount = (TextView) itemView.findViewById(R.id.post_txtv_likenumb);
            imgv_avatar = (ImageView) itemView.findViewById(R.id.post_img_user);
            imgv_banner = (ImageView) itemView.findViewById(R.id.post_imgv_banner);
        }
    }

    public interface OnItemClickLiestner {
        void onItemClick(Post post);
    }

    public void setOnItemClickLiestner(OnItemClickLiestner liestner) {
        onItemClickLiestner = liestner;
    }

    public Post_recycler_adapter(ArrayList<Post> posts, Context context, StorageReference stRef) {
        this.posts = posts;
        this.context = context;
        this.stRef = stRef;
    }


}
