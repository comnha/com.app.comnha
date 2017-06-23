package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by PTT on 10/30/2016.
 */

public class Food_recycler_adapter extends RecyclerView.Adapter<Food_recycler_adapter.ViewHolder> {
    ArrayList<Food> foods;
    Activity activity;
    StorageReference stRef;
    private OnItemClickLiestner onItemClickLiestner;

    public interface OnItemClickLiestner {
        void onItemClick(Food food, Activity activity, View itemView);
    }

    public void setOnItemClickLiestner(OnItemClickLiestner liestner) {
        onItemClickLiestner = liestner;
    }

    public Food_recycler_adapter(ArrayList<Food> foods,
                                 Activity activity, StorageReference stRef) {
        this.foods = foods;
        this.activity = activity;
        this.stRef = stRef;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcyler_food, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.txt_name.setTextColor(activity.getResources()
                .getColor(android.R.color.white));
        holder.txt_price.setTextColor(activity.getResources()
                .getColor(android.R.color.white));
        holder.txt_price.setText(foods.get(position).getPrice() + "đ");
        holder.txt_name.setText(foods.get(position).getName());
        if (foods.get(position).getTotal() == 0) {
            holder.ratingBar.setRating(0);
        } else {
            holder.ratingBar.setRating(foods.get(position).getRating() /
                    foods.get(position)
                            .getTotal());
        }
        holder.ratingBar.setIsIndicator(true);
        holder.cardv.setCardBackgroundColor(activity.getResources()
                .getColor(R.color.color_notify_reportfood));
        StorageReference imgRef = stRef.child(foods.get(position).getFoodImg());
        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(activity)
                        .load(uri)
                        .into(holder.imgv_photo);

            }
        });
        if (onItemClickLiestner != null) {
            holder.cardv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Bitmap imgBitmap = ((BitmapDrawable) holder.imgv_photo.getDrawable())
                                .getBitmap();
                        foods.get(holder.getAdapterPosition()).setImgBitmap(imgBitmap);
                        onItemClickLiestner.onItemClick(
                                foods.get(holder.getAdapterPosition()),
                                activity, holder.itemView);
                    } catch (NullPointerException e) {
                        onItemClickLiestner.onItemClick(
                                foods.get(holder.getAdapterPosition()),
                                activity, holder.itemView);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_name, txt_price;
        public RatingBar ratingBar;
        ImageView imgv_photo;
        CardView cardv;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_price = (TextView) itemView.findViewById(R.id.item_rcyler_food_price);
            txt_name = (TextView) itemView.findViewById(R.id.item_rcyler_food_txtvname);
            ratingBar = (RatingBar) itemView.findViewById(R.id.item_rcyler_food_rb_rating);
            imgv_photo = (ImageView) itemView.findViewById(R.id.item_rcyler_food_imgV);
            cardv = (CardView) itemView.findViewById(R.id.item_rcyler_food_cardv);
        }
    }
}
