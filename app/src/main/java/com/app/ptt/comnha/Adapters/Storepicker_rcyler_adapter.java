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
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by PTT on 9/27/2016.
 */

public class Storepicker_rcyler_adapter extends RecyclerView.Adapter<Storepicker_rcyler_adapter.ViewHolder> {
    Activity activity;


    ArrayList<Store> list;
    int previuosPosition = 0;
    LatLng yourLocation;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgV;
        public TextView txt_diachi, txt_tenquan, txt_km, txt_diem;

        public ViewHolder(View itemView) {
            super(itemView);
            imgV = (ImageView) itemView.findViewById(R.id.item_list_imgV);
            txt_diachi = (TextView) itemView.findViewById(R.id.item_list_txtvaddress);
            txt_tenquan = (TextView) itemView.findViewById(R.id.item_list_txtvstorename);
            txt_km = (TextView) itemView.findViewById(R.id.item_list_txtvdistance);
            txt_diem = (TextView) itemView.findViewById(R.id.item_list_txtvrate);
        }
    }

    public Storepicker_rcyler_adapter(ArrayList<Store> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_store, parent, false);
        return new ViewHolder(itemView);

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

//        holder.txtv_storename.setText(stores.get(position).getName());
//        holder.txtv_address.setText(stores.get(position).getDiachi());
//        holder.txt_diem.setText(String.valueOf(stores.get(position).getTongAVG()));
        if (position > previuosPosition) {
            AnimationUtils.animateItemRcylerV(holder, true);
        } else {
            AnimationUtils.animateItemRcylerV(holder, false);
        }
        previuosPosition = position;
//        holder.txtv_distance.setText(
//                stores.get(position).getKhoangcach());
//        holder.imgV.setBackground();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

}
