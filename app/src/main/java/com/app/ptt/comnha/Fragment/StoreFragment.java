package com.app.ptt.comnha.Fragment;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.app.ptt.comnha.Activity.Adapter2Activity;
import com.app.ptt.comnha.Adapters.Locatlist_rcyler_adapter;
import com.app.ptt.comnha.Classes.RecyclerItemClickListener1;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.Modules.Storage;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.Modules.MyTool;
import com.app.ptt.comnha.SingletonClasses.ChooseLoca;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.StringWriter;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends Fragment implements View.OnClickListener {
    private static final String LOG = StoreFragment.class.getSimpleName();
    ProgressDialog mProgressDialog;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private Boolean isBound = false;
    private MyService myService;

    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference dbRef;

    private ArrayList<MyLocation> listLocation,customLis;
    ChildEventListener locaListChildEventListener;
    MyLocation myLocation;
    MyTool myTool;
    View mView;
    int filter;
    boolean first=false;
    Button btn_refresh;
    private static int STATUS_START = 0;

    public void setFilter(int filter) {
        this.filter = filter;
    }

    Handler handler;
    int listSize = 0, count;
    String tinh, huyen;
    Context mContext;
    boolean isConnected=false;
    IntentFilter mIntentFilter;
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(mBroadcastSendAddress)) {
                Log.i(LOG+".onReceive form Service","isConnected= "+ intent.getBooleanExtra("isConnected", false));
                if (intent.getBooleanExtra("isConnected", false)) {
                    isConnected = true;
                } else
                    isConnected = false;
            }
        }
    };




    public void setTinh(String tinh) {
        this.tinh = tinh;
    }
    public void setHuyen(String huyen) {
        this.huyen = huyen;
    }

    public void setContext(Context context){
        mContext=context;
    }
    public StoreFragment() {
    }
    @Override
    public void onStart() {
        Log.i(LOG, "onStart");
        super.onStart();
        String a= Storage.readFile(getContext(), "myLocation");
        if(a!=null){
            ArrayList<MyLocation> list=new ArrayList<>();
            list=Storage.readJSONMyLocation(a);
            myLocation=list.get(0);
        }
        isConnected= MyService.returnIsConnected();
        if(!isConnected){
            Toast.makeText(getContext(),"Offline mode",Toast.LENGTH_SHORT).show();
        }
        myTool = new MyTool(getActivity());
        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
        getContext().registerReceiver(broadcastReceiver,mIntentFilter);
    }
    @Override
    public void onStop() {
        Log.i(LOG, "onStop");
        super.onStop();
        //myTool.stopGoogleApi();
        getContext().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        mView = view;
        listSize = 0;
        isConnected= MyService.returnIsConnected();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        anhxa(view);
//        if (!isConnected){
//           // Toast.makeText(mContext,"Offline mode",Toast.LENGTH_SHORT).show();
//           getDataOffline();
//        }else
//        {
//            if(MyService.saveToListSaved("listLocation" + filter+"_"+tinh+"_"+huyen)==1)
//                getDataOffline();
            getData();
       // }
        return view;
    }
    public void getDataOffline(){
        Log.i(LOG + ".getDataOffline", "OK");
        if (Storage.readFile(getContext(), "listLocation" + filter+"_"+tinh+"_"+huyen) != null) {
            ArrayList<MyLocation> locations;
            String a = Storage.readFile(getContext(), "listLocation" + filter+"_"+tinh+"_"+huyen);
            if(a!=null) {
                locations = Storage.readJSONMyLocation(a);
                if(locations.size()>0) {
                    for (MyLocation location : locations) {
                        listLocation.add(location);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }else{
                if(isConnected)
                    getData();
            }
        }
    }
    protected void closeDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    protected void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(getContext(), null, "Loading...");
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }
    }

    protected boolean isShowProgress() {
        return mProgressDialog.isShowing();
    }
    public void getData(){
        showProgressDialog();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isShowProgress()){
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    Toast.makeText(getContext(), "Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }
        }, 15000);
        locaListChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                MyLocation newLocation = dataSnapshot.getValue(MyLocation.class);
                Log.i("Dia chi", "RUN:" + newLocation.getDiachi());
                newLocation.setLocaID(dataSnapshot.getKey());
                if (myLocation != null) {
                    float kc = (float) myTool.getDistance(new LatLng(myLocation.getLat(), myLocation.getLng()), new LatLng(newLocation.getLat(), newLocation.getLng()));
                    int c = Math.round(kc);
                    Log.i(LOG + ".tinh khoang cach", c + "");
                    int d = c / 1000;
                    int e = c % 1000;
                    int f = e / 100;
                    newLocation.setKhoangcach(d + "," + f + " km");
                    Log.i("Dia chi", "CC:" + newLocation.getKhoangcach());
                } else
                    Log.i(LOG + ".getDataInFireBase", "my Location==null");
                if(tinh!=null&&huyen!=null){
                    Log.i(LOG + ".getDataInFireBase", "tinh!=null&&huyen!=null and index="+newLocation.getIndex());
                    if(newLocation.getIndex()!=null) {
                        if (newLocation.getIndex().equals(tinh + "_" + huyen)) {
                            if (newLocation
                                    .getVisible() != null && newLocation.getVisible()) {
                                long vs=newLocation.getVsTong()/newLocation.getSize();
                                long pv=newLocation.getPvTong()/newLocation.getSize();
                                long gia=newLocation.getGiaTong()/newLocation.getSize();
                                newLocation.setVsAVG(vs);
                                newLocation.setPvAVG(pv);
                                newLocation.setGiaAVG(gia);
                                long tong=(vs+pv+gia)/3;
                                newLocation.setTongAVG(tong);
                                listLocation.add(newLocation);

                            }

                        }
                    }
                }else{
                    Log.i(LOG + ".getDataInFireBase", "visible="+newLocation.getVisible());
                    if(newLocation
                            .getVisible()!=null &&newLocation.getVisible()){
                        long vs=newLocation.getVsTong()/newLocation.getSize();
                        long pv=newLocation.getPvTong()/newLocation.getSize();
                        long gia=newLocation.getGiaTong()/newLocation.getSize();
                        newLocation.setVsAVG(vs);
                        newLocation.setPvAVG(pv);
                        newLocation.setGiaAVG(gia);
                        long tong=(vs+pv+gia)/3;
                        newLocation.setTongAVG(tong);
                        listLocation.add(newLocation);

                    }

                }
                //Storage.writeFile(mContext,  Storage.parseMyLocationToJson( listLocation).toString(), "listLocation" + filter+"_"+tinh+"_"+huyen);
                //MyService.saveToListSaved("listLocation" + filter+"_"+tinh+"_"+huyen);
                Log.i(LOG + ".getData ","size = "+ listLocation.size());
//                if (STATUS_START > 0) {
//                    btn_refresh.setVisibility(View.VISIBLE);
//                    AnimationUtils.animatbtnRefreshIfChange(btn_refresh);
//                }
//                STATUS_START = 1;

            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                MyLocation newLocation = dataSnapshot.getValue(MyLocation.class);
                newLocation.setLocaID(dataSnapshot.getKey());
                for (MyLocation lc : listLocation) {
                    if (lc.getLocaID().equals(newLocation.getLocaID())) {
                        listLocation.set(listLocation.indexOf(lc),newLocation);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {
                MyLocation newLocation = dataSnapshot.getValue(MyLocation.class);
                newLocation.setLocaID(dataSnapshot.getKey());
                for (MyLocation lc : listLocation) {
                    if (lc.getLocaID().equals(newLocation.getLocaID())) {
                        listLocation.remove(lc);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(listLocation.size()>0){
                    mAdapter = new Locatlist_rcyler_adapter(listLocation);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getContext(), "Không có quán ăn mới", Toast.LENGTH_SHORT).show();
                }
                closeDialog();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        switch (filter) {
            case 1:
                dbRef.child(
                        getResources().getString(R.string.locations_CODE)).addChildEventListener(locaListChildEventListener);
                break;
            case 2:
                dbRef.child(
                        getResources().getString(R.string.locations_CODE))
                        .orderByChild("giaAVG")
                        .limitToLast(200)
                        .addChildEventListener(locaListChildEventListener);
                break;
            case 3:
                dbRef.child(
                        getResources().getString(R.string.locations_CODE))
                        .orderByChild("pvAVG")
                        .limitToLast(200)
                        .addChildEventListener(locaListChildEventListener);
                break;
            case 4:
                dbRef.child(
                        getResources().getString(R.string.locations_CODE))
                        .orderByChild("vsAVG")
                        .limitToLast(200)
                        .addChildEventListener(locaListChildEventListener);
                break;
        }
    }
    private void anhxa(View view) {
        Log.i(LOG, "anhxa");
        listLocation = new ArrayList<>();
        btn_refresh = (Button) view.findViewById(R.id.frg_store_btn_refresh);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_store_recyclerView_localist);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mLayoutManager = linearLayoutManager;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener1(getActivity().getApplicationContext(),
                new RecyclerItemClickListener1.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                      //  if(isConnected) {
                            String key = listLocation.get(position).getLocaID();
                            Intent intent = new Intent(getActivity().getApplicationContext(), Adapter2Activity.class);
                            intent.putExtra(getResources().getString(R.string.fragment_CODE),
                                    getResources().getString(R.string.frag_locadetail_CODE));
                            MyLocation location=listLocation.get(position);
                            //location.setLocaID(key);
                            //location.setTinhtp(listLocation.get(position).getTinhtp());
                            //location.setQuanhuyen(listLocation.get(position).getQuanhuyen());
                            ChooseLoca.getInstance().setLocation(location);
                            if(!isConnected) {
//                                ChooseLoca.getInstance().setInfo("listLocation" + filter + "_" + tinh + "_" + huyen);
//                                Log.i(LOG + ".anhxa", "listLocation" + filter + "_" + tinh + "_" + huyen);
                            }
                            else
                                ChooseLoca.getInstance().setInfo("");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
//                        }else{
//                            Toast.makeText(mContext,"You are offline",Toast.LENGTH_LONG).show();
//                        }
                    }
                }));
        //btn_refresh.setVisibility(View.GONE);
        btn_refresh.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_store_btn_refresh:
                listLocation = new ArrayList<>();
                getData();
                //mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);

                //btn_refresh.setVisibility(View.GONE);
                break;
        }
    }
}
