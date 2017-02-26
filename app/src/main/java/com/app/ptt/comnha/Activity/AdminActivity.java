package com.app.ptt.comnha.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Store_listview_adapter;

import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.FireBase.Notification;

import com.app.ptt.comnha.Fragment.PickDistrictDialogFragment;
import com.app.ptt.comnha.Fragment.PickProvinceDialogFragment;
import com.app.ptt.comnha.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener, PickProvinceDialogFragment.OnnPickProvinceListener, PickDistrictDialogFragment.OnPickDistricListener {
    TextView txt_prov, txt_dist;
    ListView lv_store, lv_reports;
    ArrayList<MyLocation> myLocations = new ArrayList<>();
    ChildEventListener storeChildEventListener, reportChildEventListener,notificatonChildEventListener;
    Store_listview_adapter store_adapter;

    String tinh = "", quan = "";
    int whatProvince;
    ProgressDialog mProgressDialog;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    RecyclerView.Adapter mAdapter;
    DatabaseReference dbRef;
    ArrayList<Notification> listNoti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
//        listNoti=new ArrayList<>();
//        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_notification);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
//        mRecyclerViewLayoutManager = linearLayoutManager;
//        mRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
//        mAdapter = new Notification_rcycler_adapter(listNoti, this);

        mRecyclerView.setAdapter(mAdapter);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.txt_plzwait));
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_path));
        txt_prov = (TextView) findViewById(R.id.act_admin_txttinh);
        txt_prov.setOnClickListener(this);
        txt_dist = (TextView) findViewById(R.id.act_admin_txtquan);
        txt_dist.setOnClickListener(this);
        lv_reports = (ListView) findViewById(R.id.act_admin_listvreports);
        lv_store = (ListView) findViewById(R.id.act_admin_listvstore);
        store_adapter = new Store_listview_adapter(myLocations);


        lv_store.setAdapter(store_adapter);

    }

    @Override
    public void onBackPressed() {

        if (lv_store.getTranslationX() < 0) {
            dbRef.removeEventListener(reportChildEventListener);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_admin_txttinh:
                PickProvinceDialogFragment pickProvinceFrg = new PickProvinceDialogFragment();
                pickProvinceFrg.show(getSupportFragmentManager(), "fragment_pickProvince");
                pickProvinceFrg.setOnPickProvinceListener(this);
                break;
            case R.id.act_admin_txtquan:
                if (tinh.equals("")) {
                    Toast.makeText(this, getString(R.string.txt_noChoseProvince), Toast.LENGTH_SHORT).show();
                } else {
                    PickDistrictDialogFragment pickDistrictFrg = new PickDistrictDialogFragment();
                    pickDistrictFrg.setWhatprovince(whatProvince);
                    pickDistrictFrg.show(getSupportFragmentManager(), "fragment_pickDistrict");
                    pickDistrictFrg.setOnPickDistricListener(this);
                }
                break;
        }
    }

    @Override
    public void onPickProvince(String province, int position) {
        txt_prov.setText(province);
        whatProvince = position;
        tinh = province;
    }

    @Override
    public void onPickDistrict(String district) {
        txt_dist.setText(district);
        quan = district;
        myLocations.clear();
        store_adapter.notifyDataSetChanged();
        dbRef.child(tinh + "/" + quan + "/"
                + getString(R.string.locations_CODE))
                .addChildEventListener(storeChildEventListener);
    }
}
