package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.Models.FireBase.NewstoreNotify;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PTT on 4/7/2017.
 */

public class notify_newstore_adapter extends BaseAdapter {
    Activity activity;
    ArrayList<NewstoreNotify> items;
    private OnItemClickLiestner onItemClickLiestner;
    private OnOptionItemClickListener onOptionItemClickListener;

    public interface OnItemClickLiestner {
        void onItemClick(NewstoreNotify notify, Activity activity);
    }

    public void setOnItemClickLiestner(OnItemClickLiestner liestner) {
        onItemClickLiestner = liestner;
    }

    public interface OnOptionItemClickListener {
        void onDelNotify(NewstoreNotify notify);

        void onBlockUser(NewstoreNotify notify);
    }

    public void setOnOptionItemClickListener(OnOptionItemClickListener onOptionItemClickListener) {
        this.onOptionItemClickListener = onOptionItemClickListener;
    }

    public notify_newstore_adapter(Activity activity, ArrayList<NewstoreNotify> items) {
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
            convertView = LayoutInflater.from(activity).
                    inflate(R.layout.item_listv_notify_newstore, null);
            holder.name = (TextView) convertView.
                    findViewById(R.id.textV_storename_item_notify_newstore);
            holder.address = (TextView) convertView.
                    findViewById(R.id.textV_storeaddress_item_notify_newstore);
            holder.date = (TextView) convertView
                    .findViewById(R.id.textV_createday_item_notify_newstore);
            holder.createby = (TextView) convertView
                    .findViewById(R.id.textV_createby_item_notify_newstore);
            holder.readestate = (TextView) convertView
                    .findViewById(R.id.textV_readstate_item_notify_newstore);
            holder.more = (ImageView) convertView
                    .findViewById(R.id.imgV_option_item_notify_newstore);
            holder.cardv = (CardView) convertView
                    .findViewById(R.id.cardv_item_notify_newstore);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(activity.getString(R.string.txt_storename) + ": " + items.get(position).getName());
        holder.address.setText(activity.getString(R.string.text_address) + ": " + items.get(position).getAddress());
        holder.date.setText(activity.getString(R.string.txt_createdate) + items.get(position).getDate());
        holder.createby.setText(activity.getString(R.string.txt_storeaddby) + items.get(position).getUn());
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
                if (!items.get(position).isReadstate()) {
                    holder.readestate.setText(activity.getString(R.string.txt_read));
                    holder.readestate.setTextColor(activity.getResources()
                            .getColor(android.R.color.darker_gray));
                }
                onItemClickLiestner.onItemClick(items.get(position),
                        activity);
            }
        });
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(activity, holder.more, Gravity.END);
//                popupMenu.inflate(R.popupMenu.menu_item_notify);
                Menu menu = popupMenu.getMenu();
                List<Pair<Integer, String>> contents = new ArrayList<>();
                contents.add(new Pair<Integer, String>
                        (R.string.text_delnotify,
                                activity.getString(R.string.text_delnotify)));
                contents.add(new Pair<Integer, String>
                        (R.string.text_block_addstore,
                                activity.getString(R.string.text_block_addstore)));
                menu = AppUtils.createMenu(menu, contents);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.string.text_delnotify:
                                onOptionItemClickListener.onDelNotify(
                                        items.get(position));
                                Log.d("setOnMenuItemClick", "item " + 0);
                                break;
                            case R.string.text_block_addstore:
                                onOptionItemClickListener.onBlockUser(
                                        items.get(position));
                                Log.d("setOnMenuItemClick", "item " + 1);
                                break;
                        }
                        return true;
                    }
                });
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView name, address, date, createby, readestate;
        ImageView more;
        CardView cardv;
    }
}
