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

import com.app.ptt.comnha.Models.FireBase.ReportimgNotify;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PTT on 4/7/2017.
 */

public class notify_reportimg_adapter extends BaseAdapter {
    Activity activity;
    ArrayList<ReportimgNotify> items;
    private OnItemClickLiestner onItemClickLiestner;
    private OnOptionItemClickListener onOptionItemClickListener;

    public interface OnItemClickLiestner {
        void onItemClick(ReportimgNotify notify, Activity activity);
    }

    public void setOnItemClickLiestner(OnItemClickLiestner liestner) {
        onItemClickLiestner = liestner;
    }

    public interface OnOptionItemClickListener {
        void onDelNotify(ReportimgNotify notify);

        void onBlockUser(ReportimgNotify notify);
    }

    public void setOnOptionItemClickListener(OnOptionItemClickListener onOptionItemClickListener) {
        this.onOptionItemClickListener = onOptionItemClickListener;
    }

    public notify_reportimg_adapter(Activity activity, ArrayList<ReportimgNotify> items) {
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
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_listv_notify_reportimg, null);
            holder.name = (TextView) convertView.findViewById(R.id.textV_imgname_item_notify_reportimg);
            holder.un = (TextView) convertView.findViewById(R.id.textV_un_item_notify_reportimg);
            holder.date = (TextView) convertView.findViewById(R.id.textV_date_item_notify_reportimg);
            holder.time = (TextView) convertView.findViewById(R.id.textV_time_item_notify_reportimg);
            holder.content = (TextView) convertView.findViewById(R.id.textV_content_item_notify_reportimg);
            holder.readestate = (TextView) convertView.findViewById(R.id.textV_readstate_item_notify_reportimg);
            holder.more = (ImageView) convertView.findViewById(R.id.imgV_option_item_notify_reportimg);
            holder.cardv = (CardView) convertView.findViewById(R.id.cardv_item_notify_reportimg);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(activity.getString(R.string.txt_imgname) + ": " + items.get(position).getName());
        holder.date.setText(activity.getString(R.string.txt_date) + ": " + items.get(position).getDate());
        holder.time.setText(activity.getString(R.string.txt_time) + ": " + items.get(position).getTime());
        holder.un.setText(activity.getString(R.string.txt_from) + ": " + items.get(position).getUn());
        holder.content.setText(activity.getString(R.string.text_content) + ": " + items.get(position).getContents());
        if (!items.get(position).isReadstate()) {
            holder.readestate.setText(activity.getString(R.string.txt_notread));
            holder.readestate.setTextColor(activity.getResources()
                    .getColor(R.color.color_notify_reportimg));
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
                        (R.string.text_block_reportimg,
                                activity.getString(R.string.text_block_reportimg)));
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
                            case R.string.text_block_reportimg:
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
        TextView name, content, date, time, readestate, un;
        ImageView more;
        CardView cardv;
    }
}
