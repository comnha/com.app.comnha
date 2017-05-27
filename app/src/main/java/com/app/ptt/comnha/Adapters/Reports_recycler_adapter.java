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
        void onCheck(ArrayList<Report> reports, int position);
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
    public void onBindViewHolder(final ViewHoler holder, int position) {
        holder.cb_report.setText(reports.get(position).getContent());
        if (reports.get(position).isChecked()) {
            holder.cb_report.setChecked(true);
        } else {
            holder.cb_report.setChecked(false);
        }
        if (reports.get(reports.size() - 1).isChecked()) {
            if (reports.size() - 1 == position) {
                holder.cb_report.setEnabled(true);
            } else {
                holder.cb_report.setEnabled(false);
            }
        } else {
            holder.cb_report.setEnabled(true);
        }
        holder.cb_report.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onItemCheckedListener != null) {
                    reports.get(holder.getAdapterPosition()).setChecked(isChecked);
                    onItemCheckedListener.onCheck(reports,
                            holder.getAdapterPosition());
                    if (holder.getAdapterPosition() == reports.size() - 1) {
                        notifyDataSetChanged();
                    }
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

    public String getContent() {
        String content = "";
        ArrayList<Report> rps = new ArrayList<>();
        for (Report item : reports) {
            if (reports.indexOf(item) != reports.size() - 1) {
                if (item.isChecked()) {
                    rps.add(item);
                }
            }
        }
        for (Report item : rps) {
            if (rps.indexOf(item) < rps.size() - 1) {
                content += item.getContent() + ", ";
            } else {
                content += item.getContent();
            }
        }
        return content;
    }
}
