package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;

import java.util.ArrayList;

/**
 * Created by PTT on 9/27/2016.
 */

public class Chooseloca_rcyler_adapter extends RecyclerView.Adapter<Chooseloca_rcyler_adapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgV;
        public TextView txt_diachi, txt_tenquan, txt_km, txt_diem;

        public ViewHolder(View itemView) {
            super(itemView);
            imgV = (ImageView) itemView.findViewById(R.id.item_rcyler_chooseloca_imgV);
            txt_diachi = (TextView) itemView.findViewById(R.id.item_rcyler_chooseloca_txtdiachi);
            txt_tenquan = (TextView) itemView.findViewById(R.id.item_rcyler_chooseloca_txtTenquan);
            txt_km = (TextView) itemView.findViewById(R.id.item_rcyler_chooseloca_txtkm);
            txt_diem = (TextView) itemView.findViewById(R.id.item_rcyler_chooseloca_txtscore);
        }
    }

    Activity activity;
    ArrayList<Store> list;
    int previuosPosition = 0;

    public Chooseloca_rcyler_adapter(ArrayList<Store> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcyler_chooseloca, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.txtv_storename.setText(list.get(position).getName());
//        holder.txtv_address.setText(list.get(position).getDiachi());
        if (position > previuosPosition) {
            AnimationUtils.animateItemRcylerV(holder, false);

        } else {
            AnimationUtils.animateItemRcylerV(holder, true);

        }
        previuosPosition = position;
//        holder.diem.setText(list.get(position).getName());
//        holder.txtv_distance.setText(list.get(position).getName());
//        holder.imgV.setBackground();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
