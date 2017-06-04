package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
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

import com.app.ptt.comnha.Models.FireBase.ReportfoodNotify;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PTT on 4/7/2017.
 */

public class notify_reportfood_adapter extends BaseAdapter {
    Activity activity;
    ArrayList<ReportfoodNotify> items;
    private OnItemClickLiestner onItemClickLiestner;
    private OnOptionItemClickListener onOptionItemClickListener;

    public interface OnItemClickLiestner {
        void onItemClick(ReportfoodNotify notify, Activity activity);
    }

    public void setOnItemClickLiestner(OnItemClickLiestner liestner) {
        onItemClickLiestner = liestner;
    }

    public interface OnOptionItemClickListener {
        void onDelNotify(ReportfoodNotify notify);

        void onBlockUser(ReportfoodNotify notify);
    }

    public void setOnOptionItemClickListener(OnOptionItemClickListener onOptionItemClickListener) {
        this.onOptionItemClickListener = onOptionItemClickListener;
    }

    public notify_reportfood_adapter(Activity activity, ArrayList<ReportfoodNotify> items) {
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
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_listv_notify_reportfood, null);
            holder.name = (TextView) convertView.findViewById(R.id.textV_foodname_item_notify_reportfood);
            holder.storename = (TextView) convertView.findViewById(R.id.textV_storename_item_notify_reportfood);
            holder.un = (TextView) convertView.findViewById(R.id.textV_un_item_notify_reportfood);
            holder.date = (TextView) convertView.findViewById(R.id.textV_date_item_notify_reportfood);
            holder.time = (TextView) convertView.findViewById(R.id.textV_time_item_notify_reportfood);
            holder.content = (TextView) convertView.findViewById(R.id.textV_content_item_notify_reportfood);
            holder.readestate = (TextView) convertView.findViewById(R.id.textV_readstate_item_notify_reportfood);
            holder.more = (ImageView) convertView.findViewById(R.id.imgV_option_item_notify_reportfood);
            holder.cardv = (CardView) convertView.findViewById(R.id.cardv_item_notify_reportfood);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(activity.getString(R.string.text_foodname) + ": " + items.get(position).getName());
        holder.date.setText(activity.getString(R.string.txt_date) + ": " + items.get(position).getDate());
        holder.time.setText(activity.getString(R.string.txt_time) + ": " + items.get(position).getTime());
        holder.un.setText(activity.getString(R.string.txt_from) + ": " + items.get(position).getUn());
        holder.content.setText(items.get(position).getContents());
        if (!items.get(position).isReadstate()) {
            holder.readestate.setText(activity.getString(R.string.txt_notread));
            holder.readestate.setTextColor(activity.getResources()
                    .getColor(R.color.color_notify_reportfood));
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
                List<Pair<Integer, String>> contents = new ArrayList<>();
                contents.add(new Pair<Integer, String>
                        (R.string.text_delnotify,
                                activity.getString(R.string.text_delnotify)));
                contents.add(new Pair<Integer, String>
                        (R.string.text_block_reportfood,
                                activity.getString(R.string.text_block_reportfood)));
                menu = AppUtils.createMenu(menu, contents);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.string.text_delnotify:
                                if (onOptionItemClickListener != null) {
                                    onOptionItemClickListener.onDelNotify(
                                            items.get(position));
                                }
                                break;
                            case R.string.text_block_reportfood:
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
        TextView name, storename, content, date, time, readestate, un;
        ImageView more;
        CardView cardv;
    }
}
