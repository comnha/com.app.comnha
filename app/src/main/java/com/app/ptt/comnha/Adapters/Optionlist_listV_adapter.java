package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.R;

import java.util.ArrayList;

/**
 * Created by PTT on 10/3/2016.
 */

public class Optionlist_listV_adapter extends ArrayAdapter<String> {
    ArrayList<String> list;
    private Activity activity;

    public Optionlist_listV_adapter(Activity context, int resource, ArrayList<String> list) {
        super(context, resource, list);
        this.list = list;
        this.activity = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_listv_optionlist, null, false);
        } else {
            convertView.getTag();
        }

        TextView name = (TextView) convertView.findViewById(R.id.listv_txt_option);
        ImageView img = (ImageView) convertView.findViewById(R.id.listv_img_option);

        name.setText(list.get(position));

        return convertView;

    }
}
