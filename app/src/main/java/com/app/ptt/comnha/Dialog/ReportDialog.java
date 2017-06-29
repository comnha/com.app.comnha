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
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Reports_recycler_adapter;
import com.app.ptt.comnha.Classes.Report;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Image;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.ReportfoodNotify;
import com.app.ptt.comnha.Models.FireBase.ReportimgNotify;
import com.app.ptt.comnha.Models.FireBase.ReportpostNotify;
import com.app.ptt.comnha.Models.FireBase.ReportstoreNotify;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.AppUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    Reports_recycler_adapter reportAdapter = null;
    Const.REPORTS type;
    Object report = null;
    DatabaseReference dbRef;

    OnPosNegListener onPosNegListener;
    Button btn_neg, btn_pos;
    Map<String, Object> childUpdate = null;

    public interface OnPosNegListener {
        void onPositive(boolean isClicked, Map<String, Object> childUpdate, Dialog dialog);

        void onNegative(boolean isClicked, Dialog dialog);
    }

    public void setOnPosNegListener(OnPosNegListener
                                            onPosNegListener) {
        this.onPosNegListener = onPosNegListener;
    }

    public void setReport(Const.REPORTS type, Object report) {
        this.report = report;
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
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebaseDB_path));
        init(view);
        return view;
    }

    private void init(View view) {
        edt_content = (EditText) view.findViewById(R.id.edt_content_report);
        edt_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
        edt_content.setEnabled(false);
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
        reportAdapter.setOnItemCheckedListener(new Reports_recycler_adapter.OnItemCheckedListener() {
            @Override
            public void onCheck(ArrayList<Report> reports, int position) {
                if (position == reports.size() - 1) {
                    if (!reports.get(reports.size() - 1).isChecked()) {
                        edt_content.setEnabled(false);
                    } else {
                        edt_content.setEnabled(true);
                    }
                }
//                Log.i("reportDialog_checked",
//                        ReportDialog.this.reports.get(position).isChecked() + "");
            }
        });
        btn_neg = (Button) view.findViewById(R.id.btn_negative_report);
        btn_pos = (Button) view.findViewById(R.id.btn_positive_report);
        btn_neg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPosNegListener != null) {
                    onPosNegListener.onNegative(true, getDialog());
                }
            }
        });
        btn_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPosNegListener != null) {
                    if (edt_content.isEnabled()) {
                        content = AppUtils.getText(edt_content);
                    } else {
                        content = reportAdapter.getContent();
                    }
                    if (content.equals("")) {
                        Toast.makeText(getContext(),
                                getString(R.string.txt_reportnocontent),
                                Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        switch (type) {
                            case REPORT_STORE:
                                childUpdate = setStoreReport();
                                break;
                            case REPORT_IMG:
                                childUpdate = setImgReport();
                                break;
                            case REPORT_FOOD:
                                childUpdate = setFoodReport();
                                break;
                            case REPORT_POST:
                                childUpdate = setPostReport();
                                break;
                        }
                        if (childUpdate != null) {

                            onPosNegListener.onPositive(true, childUpdate, getDialog());
                        }
                    }
                }
            }
        });
    }

    Map<String, Object> setStoreReport() {
        try {
            String key = dbRef
                    .child(getString(R.string.reportStore_CODE))
                    .push()
                    .getKey();
            Store store = (Store) report;
            ReportstoreNotify storeReport = new ReportstoreNotify(
                    store.getStoreID(), store.getName(),
                    store.getAddress(),
                    LoginSession.getInstance().getUser().getuID(),
                    LoginSession.getInstance().getUser().getUn(),
                    content, store.getPro_dist());
            Map<String, Object> rpValue = storeReport.toMap();
            Map<String, Object> childupdate = new HashMap<>();
            childupdate.put(getString(R.string.reportStore_CODE)
                    + key, rpValue);
            Log.d("reportDialog_key", store.getStoreID());
            return childupdate;
        } catch (NullPointerException mess) {
            Toast.makeText(getContext(),
                    mess.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
        return null;
    }

    Map<String, Object> setPostReport() {
        try {
            String key = dbRef
                    .child(getString(R.string.reportPost_CODE))
                    .push()
                    .getKey();
            Post post = (Post) report;
            ReportpostNotify postReport = new ReportpostNotify(
                    post.getPostID(), post.getTitle(),
                    LoginSession.getInstance().getUser().getuID(),
                    LoginSession.getInstance().getUser().getUn(),
                    content, post.getDist_pro());
            Map<String, Object> rpValue = postReport.toMap();
            Map<String, Object> childupdate = new HashMap<>();
            childupdate.put(getString(R.string.reportPost_CODE)
                    + key, rpValue);
            Log.d("reportDialog_key", post.getPostID());
            return childupdate;
        } catch (NullPointerException mess) {
            Toast.makeText(getContext(),
                    mess.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
        return null;
    }

    Map<String, Object> setFoodReport() {
        try {
            String key = dbRef
                    .child(getString(R.string.reportFood_CODE))
                    .push()
                    .getKey();
            Food food = (Food) report;
            ReportfoodNotify foodReport = new ReportfoodNotify(
                    food.getName(), food.getFoodID(),
                    LoginSession.getInstance().getUser().getuID(),
                    LoginSession.getInstance().getUser().getUn(),
                    content, food.getDist_prov());
            Map<String, Object> rpValue = foodReport.toMap();
            Map<String, Object> childupdate = new HashMap<>();
            childupdate.put(getString(R.string.reportFood_CODE)
                    + key, rpValue);
            Log.d("reportDialog_key", food.getStoreID());
            return childupdate;
        } catch (NullPointerException mess) {
            Toast.makeText(getContext(),
                    mess.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
        return null;
    }

    Map<String, Object> setImgReport() {
        try {
            String key = dbRef
                    .child(getString(R.string.reportImg_CODE))
                    .push()
                    .getKey();
            Image image = (Image) report;
            ReportimgNotify storeReport = new ReportimgNotify(
                    image.getImageID(), image.getName(),
                    LoginSession.getInstance().getUser().getuID(),
                    LoginSession.getInstance().getUser().getUn(),
                    content);
            Map<String, Object> rpValue = storeReport.toMap();
            Map<String, Object> childupdate = new HashMap<>();
            childupdate.put(getString(R.string.reportImg_CODE)
                    + key, rpValue);
            Log.d("reportDialog_key", image.getImageID());
            return childupdate;
        } catch (NullPointerException mess) {
            Toast.makeText(getContext(),
                    mess.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
        return null;
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
        reports.add(new Report(getString(R.string.txt_report_spam)));
        reports.add(new Report(getString(R.string.txt_report_impolite)));
        reports.add(new Report(getString(R.string.text_other)));
    }

    private void getFoodReports() {
        reports.add(new Report(getString(R.string.txt_report_wronfoodname)));
        reports.add(new Report(getString(R.string.txt_report_wrongfoodprice)));
        reports.add(new Report(getString(R.string.txt_report_samefoodname)));
        reports.add(new Report(getString(R.string.txt_report_spam)));
        reports.add(new Report(getString(R.string.text_other)));

    }

    private void getImgReports() {
        reports.add(new Report(getString(R.string.txt_report_nude)));
        reports.add(new Report(getString(R.string.txt_report_vilence)));
        reports.add(new Report(getString(R.string.txt_report_spam)));
        reports.add(new Report(getString(R.string.text_other)));
    }

    private void getStoreReports() {
        reports.add(new Report(getString(R.string.txt_report_wrongstorename)));
        reports.add(new Report(getString(R.string.txt_report_wrongaddress)));
        reports.add(new Report(getString(R.string.txt_report_wrongphonenumb)));
        reports.add(new Report(getString(R.string.txt_report_wrongtimeopen)));
        reports.add(new Report(getString(R.string.txt_report_samestorename)));
        reports.add(new Report(getString(R.string.txt_report_samestore)));
        reports.add(new Report(getString(R.string.txt_report_spam)));
        reports.add(new Report(getString(R.string.text_other)));
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
        switch (type) {
            case REPORT_STORE:
                dialog.setTitle(getString(R.string.txt_reportStore));
                break;
            case REPORT_IMG:
                dialog.setTitle(getString(R.string.txt_reportImg));
                break;
            case REPORT_FOOD:
                dialog.setTitle(getString(R.string.txt_reportFood));
                break;
            case REPORT_POST:
                dialog.setTitle(getString(R.string.txt_reportPost));
                break;
        }
        return dialog;
    }
}
