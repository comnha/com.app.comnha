package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.R;

import java.util.ArrayList;

/**
 * Created by PTT on 10/30/2016.
 */

public class Thucdon_rcyler_adapter extends RecyclerView.Adapter<Thucdon_rcyler_adapter.ViewHolder> {
    ArrayList<Food> foodList;
    Activity activity;

    public Thucdon_rcyler_adapter(ArrayList<Food> foodList, Activity activity) {
        this.foodList = foodList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcyler_food, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        Log.i("Visible",foodList.get(position).getVisible()+"");
//        if(foodList.get(position).getVisible()) {
//            holder.txt_gia.setText(foodList.get(position).getGia() + "đ");
//            holder.txt_tenMon.setText(foodList.get(position).getTenmon());
//            holder.ratingBar.setRating(foodList.get(position).getDanhGia());
//            holder.ratingBar.setIsIndicator(true);
//        }
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_tenMon, txt_gia;
        public RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_gia = (TextView) itemView.findViewById(R.id.item_rcyler_thucdon_txtGia);
            txt_tenMon = (TextView) itemView.findViewById(R.id.item_rcyler_thucdon_txttenMon);
            ratingBar=(RatingBar) itemView.findViewById(R.id.rb_danhGiaMon);
            ratingBar.setNumStars(3);
            ratingBar.setStepSize(1);

        }
    }
}
