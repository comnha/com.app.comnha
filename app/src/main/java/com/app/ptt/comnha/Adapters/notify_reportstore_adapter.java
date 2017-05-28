package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.Models.FireBase.ReportstoreNotify;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Utils.AppUtils;

import java.util.ArrayList;

/**
 * Created by PTT on 4/7/2017.
 */

public class notify_reportstore_adapter extends BaseAdapter {
    Activity activity;
    ArrayList<ReportstoreNotify> items;
    private OnItemClickLiestner onItemClickLiestner;
    private OnOptionItemClickListener onOptionItemClickListener;

    public interface OnItemClickLiestner {
        void onItemClick(ReportstoreNotify notify, Activity activity);
    }

    public void setOnItemClickLiestner(OnItemClickLiestner liestner) {
        onItemClickLiestner = liestner;
    }

    public interface OnOptionItemClickListener {
        void onDelNotify(ReportstoreNotify notify);

        void onBlockUser(ReportstoreNotify notify);
    }

    public void setOnOptionItemClickListener(OnOptionItemClickListener onOptionItemClickListener) {
        this.onOptionItemClickListener = onOptionItemClickListener;
    }
    public notify_reportstore_adapter(Activity activity, ArrayList<ReportstoreNotify> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_listv_notify_reportstore, null);
            holder.name = (TextView) convertView.findViewById(R.id.textV_storename_item_notify_reportstore);
            holder.address = (TextView) convertView.findViewById(R.id.textV_address_item_notify_reportstore);
            holder.un = (TextView) convertView.findViewById(R.id.textV_un_item_notify_reportstore);
            holder.date = (TextView) convertView.findViewById(R.id.textV_date_item_notify_reportstore);
            holder.time = (TextView) convertView.findViewById(R.id.textV_time_item_notify_reportstore);
            holder.content = (TextView) convertView.findViewById(R.id.textV_content_item_notify_reportstore);
            holder.readestate = (TextView) convertView.findViewById(R.id.textV_readstate_item_notify_reportstore);
            holder.more = (ImageView) convertView.findViewById(R.id.imgV_option_item_notify_reportstore);
            holder.cardv = (CardView) convertView.findViewById(R.id.cardv_item_notify_reportstore);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText("Tên quán: "+items.get(position).getStoreName());
        holder.address.setText("Địa chỉ: "+items.get(position).getAddress());
        holder.date.setText("Ngày: " + items.get(position).getDate());
        holder.time.setText("Giờ: " + items.get(position).getTime());
        holder.un.setText("Từ: " + items.get(position).getUn());
        holder.content.setText(items.get(position).getContents());
        if (!items.get(position).isReadstate()) {
            holder.readestate.setText(activity.getString(R.string.txt_notread));
            holder.readestate.setTextColor(activity.getResources()
                    .getColor(R.color.admin_color_selection_news));
        } else {
            holder.readestate.setText(activity.getString(R.string.txt_read));
            holder.readestate.setTextColor(activity.getResources()
                    .getColor(android.R.color.darker_gray));
        }
        holder.cardv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickLiestner != null) {
                    if (!items.get(position).isReadstate()) {
                        holder.readestate.setText(activity.getString(R.string.txt_read));
                        holder.readestate.setTextColor(activity.getResources()
                                .getColor(android.R.color.darker_gray));
                    }
                    onItemClickLiestner.onItemClick(items.get(position),
                            activity);
                }
            }
        });
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(activity, holder.more, Gravity.TOP);
                Menu menu = popupMenu.getMenu();
                menu = AppUtils.createMenu(menu, new String[]{
                        activity.getResources().getString(R.string.text_delnotify),
                        activity.getResources().getString(R.string.text_block_reportpost)});
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case 0:
                                if (onOptionItemClickListener != null) {
                                    onOptionItemClickListener.onDelNotify(
                                            items.get(position));
                                }
                                break;
                            case 1:
                                if (onOptionItemClickListener != null) {
                                    onOptionItemClickListener.onBlockUser(
                                            items.get(position));
                                }
                        }
                        return true;
                    }
                });
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView name,address, content, date, time, readestate, un;
        ImageView more;
        CardView cardv;
    }
}
