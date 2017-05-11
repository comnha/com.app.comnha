package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by PTT on 9/27/2016.
 */

public class Foodselection_rcyler_adapter extends RecyclerView.Adapter<Foodselection_rcyler_adapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgV;
        TextView txt_price, txt_name;
        RatingBar rb_rating;
        RelativeLayout relative_parent;

        public ViewHolder(View itemView) {
            super(itemView);
            imgV = (CircularImageView) itemView
                    .findViewById(R.id.item_foodselect_imgV);
            txt_price = (TextView) itemView
                    .findViewById(R.id.item_foodselect_txtvprice);
            txt_name = (TextView) itemView
                    .findViewById(R.id.item_foodselect_txtvname);
            rb_rating = (RatingBar) itemView
                    .findViewById(R.id.item_foodselect_rb_rating);
            relative_parent = (RelativeLayout) itemView
                    .findViewById(R.id.item_foodselect_relative);
        }
    }

    OnItemClickLiestner onItemClickLiestner;

    Activity activity;
    ArrayList<Food> foods;
    StorageReference stRef;

    public Foodselection_rcyler_adapter(Activity activity,
                                        ArrayList<Food> foods, StorageReference stRef) {
        this.activity = activity;
        this.foods = foods;
        this.stRef = stRef;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcyler_foodselection, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.txt_name.setText(foods.get(position).getName());
        holder.txt_price.setText(foods.get(position).getPrice() + "đ");
        if (foods.get(position).getTotal() == 0) {
            holder.rb_rating.setRating(0);
        } else {
            holder.rb_rating.setRating(foods.get(position).getRating() / foods.get(position)
                    .getTotal());
        }
        holder.rb_rating.setIsIndicator(true);
        if (!foods.get(holder.getAdapterPosition()).getFoodImg().equals("")) {
            StorageReference imgRef = stRef.child(foods.get(holder.getAdapterPosition())
                    .getFoodImg());
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
                try {
                    Bitmap imgBitmap = ((BitmapDrawable) holder.imgV.getDrawable())
                            .getBitmap();
                    foods.get(holder.getAdapterPosition()).setImgBitmap(imgBitmap);
                    onItemClickLiestner.onItemClick(
                            foods.get(holder.getAdapterPosition()));
                } catch (NullPointerException e) {
                    onItemClickLiestner.onItemClick(
                            foods.get(holder.getAdapterPosition()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public interface OnItemClickLiestner {
        void onItemClick(Food food);
    }

    public void setOnItemClickLiestner(OnItemClickLiestner liestner) {
        onItemClickLiestner = liestner;
    }
}
