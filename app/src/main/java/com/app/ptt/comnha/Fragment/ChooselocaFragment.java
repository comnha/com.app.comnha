package com.app.ptt.comnha.Fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.ptt.comnha.Adapters.Chooseloca_rcyler_adapter;
import com.app.ptt.comnha.Classes.RecyclerItemClickListener1;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.DoPost;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChooselocaFragment extends Fragment implements View.OnClickListener, PickProvinceDialogFragment.OnnPickProvinceListener, PickDistrictDialogFragment.OnPickDistricListener {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mlayoutManager;
    ArrayList<MyLocation> list;
    DatabaseReference dbRef;
    ChildEventListener locaChildEventListener;
    PickDistrictDialogFragment pickDistrictDialog;
    PickProvinceDialogFragment pickProvinceDialog;
    FragmentManager fmDialog;
    TextView txt_huyen, txt_tinh;
    int whatProcince = 0;

    public ChooselocaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chooseloca, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_path));
        anhxa(view);
        locaChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                MyLocation newLocation = dataSnapshot.getValue(MyLocation.class);
                newLocation.setTinhtp(txt_tinh.getText().toString());
                newLocation.setQuanhuyen(txt_huyen.getText().toString());
                newLocation.setLocaID(dataSnapshot.getKey());
                list.add(newLocation);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        return view;
    }

    private void anhxa(View view) {
        txt_huyen = (TextView) view.findViewById(R.id.frg_chooseloca_txthuyen);
        txt_tinh = (TextView) view.findViewById(R.id.frg_chooseloca_txttinh);
        fmDialog = getActivity().getSupportFragmentManager();
        pickDistrictDialog = new PickDistrictDialogFragment();
        pickProvinceDialog = new PickProvinceDialogFragment();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_chooseloca);
        mRecyclerView.setHasFixedSize(true);
        mlayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mlayoutManager);
        list = new ArrayList<>();
        mAdapter = new Chooseloca_rcyler_adapter(list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener1(getActivity().getApplicationContext(), new RecyclerItemClickListener1.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DoPost.getInstance().setMyLocation(list.get(position));
                getActivity().finish();
            }
        }));
        txt_huyen.setOnClickListener(this);
        txt_tinh.setOnClickListener(this);
        pickProvinceDialog.setOnPickProvinceListener(this);
        pickDistrictDialog.setOnPickDistricListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("destroy", "destroy");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_chooseloca_txttinh:
                pickProvinceDialog.show(fmDialog, "fragment_pickProvince");
                break;
            case R.id.frg_chooseloca_txthuyen:
                if (whatProcince < 1) {
                    Snackbar.make(v, getString(R.string.txt_noChoseProvince), Snackbar.LENGTH_SHORT).show();
                } else {
                    pickDistrictDialog.setWhatprovince(whatProcince);
                    pickDistrictDialog.show(fmDialog, "fragment_pickDistrict");
                }
                break;
        }
    }

    @Override
    public void onPickProvince(String province, int position) {
        whatProcince = position;
        txt_tinh.setText(province);
    }

    @Override
    public void onPickDistrict(String district) {
        txt_huyen.setText(district);
        list.clear();
        mAdapter.notifyDataSetChanged();
        dbRef.child(
                getString(R.string.locations_CODE)).orderByChild("index").equalTo(txt_tinh.getText()+"_"+txt_huyen.getText())
                .addChildEventListener(locaChildEventListener);
    }
}
