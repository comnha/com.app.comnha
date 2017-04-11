package com.app.ptt.comnha.Adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.FireBase.NewstoreNotify;
import com.app.ptt.comnha.R;

import java.util.ArrayList;

/**
 * Created by PTT on 4/7/2017.
 */

public class item_notify_newstore_adapter extends BaseAdapter {
    Context context;
    ArrayList<NewstoreNotify> items;

    public item_notify_newstore_adapter(Context context, ArrayList<NewstoreNotify> items) {
        this.context = context;
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_listv_notify_newstore, null);
            holder.name = (TextView) convertView.findViewById(R.id.textV_storename_item_notify_newstore);
            holder.address = (TextView) convertView.findViewById(R.id.textV_storeaddress_item_notify_newstore);
            holder.date = (TextView) convertView.findViewById(R.id.textV_createday_item_notify_newstore);
            holder.createby = (TextView) convertView.findViewById(R.id.textV_craeteby_item_notify_newstore);
            holder.readestate = (TextView) convertView.findViewById(R.id.textV_readstate_item_notify_newstore);
            holder.more = (ImageView) convertView.findViewById(R.id.imgV_option_item_notify_newstore);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(items.get(i).getName());
        holder.address.setText(items.get(i).getAddress());
        holder.date.setText("Ngày tạo: " + items.get(i).getDate());
        holder.createby.setText("Thêm bởi: " + items.get(i).getUn());
        if (items.get(i).isReadstate()) {
            holder.readestate.setText("Chưa đọc");
            holder.readestate.setTextColor(context.getResources().getColor(R.color.admin_color_selection_news));
        } else {
            holder.readestate.setText("Đã đọc");
        }
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(context, holder.more, Gravity.TOP);
                menu.inflate(R.menu.menu_item_notify_newstore);
                menu.show();
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_del_notify_newstore:
                                break;
                            case R.id.action_block_addstore_notify_newstore:
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
    }
}
