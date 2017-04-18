package com.app.ptt.comnha.Adapters;

import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;

import java.util.ArrayList;

/**
 * Created by PTT on 11/26/2016.
 */

public class Store_listview_adapter extends BaseAdapter {
    ArrayList<Store> stores;
    OnItemClickLiestner onItemClickLiestner;

    public interface OnItemClickLiestner {
        void onItemClick(Store store);
    }

    public void setOnItemClickLiestner(OnItemClickLiestner liestner) {
        onItemClickLiestner = liestner;
    }

    public Store_listview_adapter(ArrayList<Store> stores) {
        this.stores = stores;
    }

    @Override
    public int getCount() {
        return stores.size();
    }

    @Override
    public Object getItem(int position) {
        return stores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcyler_localist, null);
            holder = new ViewHolder();
            holder.cardView = (CardView) convertView.findViewById(R.id.item_rcyler_cardV);
            holder.txt_diachi = (TextView) convertView.findViewById(R.id.item_rcyler_txtdiachi);
            holder.txt_tenquan = (TextView) convertView.findViewById(R.id.item_rcyler_txtTenquan);
            holder.txt_rate = (TextView) convertView.findViewById(R.id.item_rcyler_txtscore);
            holder.txt_km = (TextView) convertView.findViewById(R.id.item_rcyler_txtkm);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txt_rate.setVisibility(View.GONE);
        holder.txt_km.setVisibility(View.GONE);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickLiestner.onItemClick(stores.get(position));
            }
        });
//        holder.txt_tenquan.setText(stores.get(position).getName());
//        holder.txt_diachi.setText(stores.get(position).getDiachi());
        return convertView;
    }

    static class ViewHolder {
        CardView cardView;
        TextView txt_diachi, txt_tenquan, txt_km, txt_rate;
    }
}
