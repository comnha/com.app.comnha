package com.app.ptt.comnha.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.app.ptt.comnha.Classes.Report;
import com.app.ptt.comnha.R;

import java.util.ArrayList;

/**
 * Created by ADMIN on 5/26/2017.
 */

public class Reports_recycler_adapter extends RecyclerView.Adapter<Reports_recycler_adapter.ViewHoler> {
    ArrayList<Report> reports;
    Context context;
    OnItemCheckedListener onItemCheckedListener;


    public void setOnItemCheckedListener(OnItemCheckedListener onItemCheckedListener) {
        this.onItemCheckedListener = onItemCheckedListener;
    }

    public interface OnItemCheckedListener {
        void onCheck(Report report);
    }

    public Reports_recycler_adapter(ArrayList<Report> reports, Context context) {
        this.reports = reports;
        this.context = context;
    }

    @Override
    public ViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHoler(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_rcyler_report, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHoler holder, final int position) {
        holder.cb_report.setText(reports.get(position).getContent());
        if (reports.get(position).isChecked()) {
            holder.cb_report.setChecked(true);
        } else {
            holder.cb_report.setChecked(false);
        }
        holder.cb_report.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onItemCheckedListener != null) {
                    onItemCheckedListener.onCheck(reports.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ViewHoler extends RecyclerView.ViewHolder {
        CheckBox cb_report;

        public ViewHoler(View itemView) {
            super(itemView);
            cb_report = (CheckBox) itemView.findViewById(R.id.item_report_cb);
        }
    }
}
