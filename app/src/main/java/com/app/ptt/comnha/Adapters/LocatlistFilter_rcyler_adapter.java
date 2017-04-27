package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;
import com.google.android.gms.maps.model.LatLng;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

/**
 * Created by PTT on 9/27/2016.
 */

public class LocatlistFilter_rcyler_adapter extends RecyclerView.Adapter<LocatlistFilter_rcyler_adapter.ViewHolder> {
    Activity activity;
    int type;

    ArrayList<Store> list;
    int previuosPosition = 0;
    LatLng yourLocation;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircularImageView imgV;
        public TextView txt_diachi, txt_tenquan, txt_km, txt_diem,txt_mon;

        public ViewHolder(View itemView) {
            super(itemView);
            imgV = (CircularImageView) itemView.findViewById(R.id.item_list_imgV);
            txt_diachi = (TextView) itemView.findViewById(R.id.item_list_txtvaddress);
            txt_tenquan = (TextView) itemView.findViewById(R.id.item_list_txtvstorename);
            txt_km = (TextView) itemView.findViewById(R.id.item_list_txtvdistance);
            txt_diem = (TextView) itemView.findViewById(R.id.item_list_txtvrate);
        }
    }

    public LocatlistFilter_rcyler_adapter(ArrayList<Store> list, int type) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_store, parent, false);
        return new ViewHolder(itemView);

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

//        holder.txtv_storename.setText(list.get(position).getName());
//        holder.txtv_address.setText(list.get(position).getDiachi());
//        holder.txt_diem.setText(String.valueOf(list.get(position).getTongAVG()));
        if (position > previuosPosition) {
            AnimationUtils.animateItemRcylerV(holder, false);
        } else {
            AnimationUtils.animateItemRcylerV(holder, true);
        }
        previuosPosition = position;
//        holder.txtv_distance.setText(
//                list.get(position).getKhoangcach());
//        holder.imgV.setBackground();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

}
