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

import com.app.ptt.comnha.Models.FireBase.NewpostNotify;
import com.app.ptt.comnha.R;

import java.util.ArrayList;

/**
 * Created by PTT on 4/7/2017.
 */

public class notify_newpost_adapter extends BaseAdapter {
    Context context;
    ArrayList<NewpostNotify> items;

    public notify_newpost_adapter(Context context, ArrayList<NewpostNotify> items) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_listv_notify_newpost, null);
            holder.title = (TextView) convertView.findViewById(R.id.textV_title_item_notify_newpost);
            holder.date = (TextView) convertView.findViewById(R.id.textV_createday_item_notify_newpost);
            holder.createby = (TextView) convertView.findViewById(R.id.textV_craeteby_item_notify_newpost);
            holder.readestate = (TextView) convertView.findViewById(R.id.textV_readstate_item_notify_newpost);
            holder.more = (ImageView) convertView.findViewById(R.id.imgV_option_item_notify_newpost);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(items.get(i).getTitle());
        holder.date.setText("Ngày đăng: " + items.get(i).getDate());
        holder.createby.setText("Đăng bởi: " + items.get(i).getUn());
        if (items.get(i).isReadstate()) {
            holder.readestate.setText("Chưa đọc");
            holder.readestate.setTextColor(context.getResources().getColor(R.color.color_notify_newpost));
        } else {
            holder.readestate.setText("Đã đọc");
            holder.readestate.setTextColor(context.getResources().getColor(R.color.color_read));

        }
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(context, holder.more, Gravity.TOP);
                menu.inflate(R.menu.menu_item_notify_newpost);
                menu.show();
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_del_notify_newpost:
                                break;
                            case R.id.action_block_post_notify_newpost:
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
        TextView title, date, createby, readestate;
        ImageView more;
    }
}
