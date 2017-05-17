package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.Models.FireBase.Comment;
import com.app.ptt.comnha.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by PTT on 10/5/2016.
 */

public class Comment_rcyler_adapter extends RecyclerView.Adapter<Comment_rcyler_adapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgv_avatar;
        public TextView txt_un, txt_content, txt_time;

        public ViewHolder(View itemView) {
            super(itemView);
            imgv_avatar = (ImageView) itemView.findViewById(R.id.avatar_itemcomt);
            txt_un = (TextView) itemView.findViewById(R.id.un_itemcomt);
            txt_content = (TextView) itemView.findViewById(R.id.content_itemcomt);
            txt_time = (TextView) itemView.findViewById(R.id.time_itemcomt);
        }
    }

    ArrayList<Comment> comments;
    StorageReference stRef;
    Activity activity;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcyler_comment, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.txt_content.setText(comments.get(position).getContent());
        holder.txt_time.setText(comments.get(position).getTime());
        holder.txt_un.setText(comments.get(position).getUn());
        if (!comments.get(holder.getAdapterPosition()).getAvatar().equals("")) {
            StorageReference imgRef = stRef.child(comments.get(holder.getAdapterPosition())
                    .getAvatar());
            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d("getUrl().addOnSuccess", uri.toString() + "");
//                    holder.imgv_avatar.setImageURI(uri);
                    Picasso.with(activity)
                            .load(uri)
                            .into(holder.imgv_avatar);
                }
            });
        } else {
            holder.imgv_avatar.setImageResource(R.drawable.ic_item_store);
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public Comment_rcyler_adapter(Activity activity, ArrayList<Comment> comments,
                                  StorageReference stRef) {
        this.comments = comments;
        this.stRef = stRef;
        this.activity = activity;
    }
}
