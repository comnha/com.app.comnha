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

import com.app.ptt.comnha.Models.FireBase.NewpostNotify;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PTT on 4/7/2017.
 */

public class notify_newpost_adapter extends BaseAdapter {
    Activity activity;
    ArrayList<NewpostNotify> items;

    private OnItemClickLiestner onItemClickLiestner;
    private OnOptionItemClickListener onOptionItemClickListener;

    public interface OnItemClickLiestner {
        void onItemClick(NewpostNotify notify, Activity activity);
    }

    public void setOnItemClickLiestner(OnItemClickLiestner liestner) {
        onItemClickLiestner = liestner;
    }

    public interface OnOptionItemClickListener {
        void onDelNotify(NewpostNotify notify);

        void onBlockUser(NewpostNotify notify);
    }

    public void setOnOptionItemClickListener(OnOptionItemClickListener onOptionItemClickListener) {
        this.onOptionItemClickListener = onOptionItemClickListener;
    }

    public notify_newpost_adapter(Activity activity, ArrayList<NewpostNotify> items) {
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
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_listv_notify_newpost, null);
            holder.title = (TextView) convertView.findViewById(R.id.textV_title_item_notify_newpost);
            holder.date = (TextView) convertView.findViewById(R.id.textV_createday_item_notify_newpost);
            holder.createby = (TextView) convertView.findViewById(R.id.textV_craeteby_item_notify_newpost);
            holder.readestate = (TextView) convertView.findViewById(R.id.textV_readstate_item_notify_newpost);
            holder.more = (ImageView) convertView.findViewById(R.id.imgV_option_item_notify_newpost);
            holder.cardv = (CardView) convertView
                    .findViewById(R.id.cardv_item_notify_newpost);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(activity.getString(R.string.text_title) + ": " + items.get(position).getTitle());
        holder.date.setText(activity.getString(R.string.txt_postdate) + items.get(position).getDate());
        holder.createby.setText(activity.getString(R.string.txt_postby) + items.get(position).getUn());
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
                PopupMenu popupMenu = new PopupMenu(activity, holder.more, Gravity.END);
//                popupMenu.inflate(R.popupMenu.menu_item_notify);
                Menu menu = popupMenu.getMenu();
                List<Pair<Integer, String>> contents = new ArrayList<>();
                contents.add(new Pair<Integer, String>
                        (R.string.text_delnotify,
                                activity.getString(R.string.text_delnotify)));
                contents.add(new Pair<Integer, String>
                        (R.string.text_block_writepost,
                                activity.getString(R.string.text_block_writepost)));
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
                            case R.string.text_block_writepost:
                                if (onOptionItemClickListener != null) {
                                    onOptionItemClickListener.onBlockUser(
                                            items.get(position));
                                }
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
        CardView cardv;
    }
}
