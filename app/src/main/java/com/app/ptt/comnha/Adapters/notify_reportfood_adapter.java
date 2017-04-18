package com.app.ptt.comnha.Adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.Models.FireBase.ReportfoodNotify;
import com.app.ptt.comnha.R;

import java.util.ArrayList;

/**
 * Created by PTT on 4/7/2017.
 */

public class notify_reportfood_adapter extends BaseAdapter {
    Context context;
    ArrayList<ReportfoodNotify> items;

    public notify_reportfood_adapter(Context context, ArrayList<ReportfoodNotify> items) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_listv_notify_reportfood, null);
            holder.name = (TextView) convertView.findViewById(R.id.textV_foodname_item_notify_reportfood);
            holder.storename = (TextView) convertView.findViewById(R.id.textV_storename_item_notify_reportfood);
            holder.un = (TextView) convertView.findViewById(R.id.textV_un_item_notify_reportfood);
            holder.date = (TextView) convertView.findViewById(R.id.textV_date_item_notify_reportfood);
            holder.time = (TextView) convertView.findViewById(R.id.textV_time_item_notify_reportfood);
            holder.content = (TextView) convertView.findViewById(R.id.textV_content_item_notify_reportfood);
            holder.readestate = (TextView) convertView.findViewById(R.id.textV_readstate_item_notify_reportfood);
            holder.more = (ImageView) convertView.findViewById(R.id.imgV_option_item_notify_reportfood);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(items.get(i).getName());
        holder.name.setText(items.get(i).getStoreName());
        holder.date.setText("Ngày: " + items.get(i).getDate());
        holder.time.setText("Giờ: " + items.get(i).getTime());
        holder.un.setText("Từ: " + items.get(i).getUn());
        holder.content.setText("Nội dung: " + items.get(i).getContents());
        if (items.get(i).isReadstate()) {
            holder.readestate.setText("Chưa đọc");
            holder.readestate.setTextColor(context.getResources().getColor(R.color.color_notify_reportfood));
        } else {
            holder.readestate.setText("Đã đọc");
            holder.readestate.setTextColor(context.getResources().getColor(R.color.color_read));
        }
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(context, holder.more, Gravity.TOP);
                menu.inflate(R.menu.menu_item_notify);
                menu.getMenu().add(Menu.NONE, 0, 0, context.getResources().getString(R.string.text_del));
                menu.getMenu().add(Menu.NONE, 1, 1, context.getResources().getString(R.string.text_block_reportfood));
                menu.getMenu().add(Menu.NONE, 2, 2, context.getResources().getString(R.string.text_del));
                menu.show();
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case 0:
                                Log.d("setOnMenuItemClick", "item " + 0);
                                break;
                            case 1:
                                break;
                            case 2:
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
        TextView name, storename, content, date, time, readestate, un;
        ImageView more;
    }
}
