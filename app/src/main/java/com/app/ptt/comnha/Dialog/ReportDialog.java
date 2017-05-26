package com.app.ptt.comnha.Dialog;


import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.app.ptt.comnha.Adapters.Reports_recycler_adapter;
import com.app.ptt.comnha.Classes.Report;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportDialog extends DialogFragment {

    EditText edt_content;
    TextInputLayout ilayout_content;
    RecyclerView rv_content_report;
    String content = "";
    RecyclerView.LayoutManager reportLm;
    ArrayList<Report> reports;
    Reports_recycler_adapter reportAdapter;
    Const.REPORTS type;

    public void setType(Const.REPORTS type) {
        this.type = type;
    }

    public ReportDialog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_dialog, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        edt_content = (EditText) view.findViewById(R.id.edt_content_report);
        edt_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
        ilayout_content = (TextInputLayout) view.findViewById(R.id.ilayout_other_report);
        edt_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                content = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    ilayout_content.setError(null);
                } else {
                    ilayout_content.setError(
                            getResources().getString(R.string.txt_nocontent));
                }
            }
        });
        rv_content_report = (RecyclerView) view.findViewById(R.id.rv_content_report);
        reportLm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_content_report.setLayoutManager(reportLm);
        getReports();
        reportAdapter = new Reports_recycler_adapter(reports, getContext());
        rv_content_report.setAdapter(reportAdapter);
    }

    private void getReports() {
        reports = new ArrayList<>();
        switch (type) {
            case REPORT_STORE:
                getStoreReports();
                break;
            case REPORT_IMG:
                getImgReports();
                break;
            case REPORT_FOOD:
                getFoodReports();
                break;
            case REPORT_POST:
                getPostReports();
                break;
        }

    }

    private void getPostReports() {
    }

    private void getFoodReports() {
    }

    private void getImgReports() {
    }

    private void getStoreReports() {
        reports.add(new Report(getString(R.string.txt_report_wrongstorename)));
        reports.add(new Report(getString(R.string.txt_report_wrongaddress)));
        reports.add(new Report(getString(R.string.txt_report_wrongphonenumb)));
        reports.add(new Report(getString(R.string.txt_report_wrongtimeopen)));
        reports.add(new Report(getString(R.string.txt_report_samestorename)));
        reports.add(new Report(getString(R.string.txt_report_samestore)));
    }
    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getString(R.string.txt_report));
        return dialog;
    }
}
