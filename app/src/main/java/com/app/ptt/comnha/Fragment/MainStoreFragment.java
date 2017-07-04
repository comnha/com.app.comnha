package com.app.ptt.comnha.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.ptt.comnha.Activity.StoreDeatailActivity;
import com.app.ptt.comnha.Adapters.Store_recycler_adapter;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Interfaces.Comunication;
import com.app.ptt.comnha.Interfaces.SendLocationListener;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
import com.app.ptt.comnha.SingletonClasses.CoreManager;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.AppUtils;
import com.app.ptt.comnha.Utils.MyTool;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainStoreFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    Store_recycler_adapter itemadapter;
    ArrayList<Store> stores;
    DatabaseReference dbRef;
    ValueEventListener storeEventListener;
    String dist_pro;
    StorageReference stRef;
    SwipeRefreshLayout swipeRefresh;
    MyTool myTool;
    private final static String TAG = "MainStoreFragment";

    public MainStoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MainFragmentPage", "createviewStore");
        View view = inflater.inflate(R.layout.fragment_main_store, container, false);
        dbRef = FirebaseDatabase.getInstance().
                getReferenceFromUrl(Const.DATABASE_PATH);
        stRef = FirebaseStorage.getInstance().getReferenceFromUrl(
                getString(R.string.firebaseStorage_path));
        ref(view);
        if (null != CoreManager.getInstance().getMyLocation()) {
            dist_pro = CoreManager.getInstance().getMyLocation().getDistrict()
                    + "_" + CoreManager.getInstance().getMyLocation().getProvince();
            getStoreList(dist_pro);
        } else {
            if (getView() != null) {

                AppUtils.showSnackbarWithoutButton(getView(), "Không tìm thấy vị trí của bạn");
            }
        }
        myTool = new MyTool(getActivity());

        return view;
    }






    public class calculateDistance extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            for (Store store : stores) {
                double distance = 0;

                try {
                    distance = myTool.distanceFrom_in_Km(store.getLat(), store.getLng(),
                            CoreManager.getInstance().getMyLocation().getLat(), CoreManager.getInstance().getMyLocation().getLng());
                    int c = (int) Math.round(distance);
                    int d = c / 1000;
                    int e = c % 1000;
                    int f = e / 100;
                    String a=String.format(Locale.US,"%*.1f",f);
                    store.setDistance(d + "," + a);
                } catch (Exception e) {

                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            itemadapter.notifyDataSetChanged();
        }
    }


    private void getStoreList(final String dist_pro) {
        stores.clear();
        int role = 0;
        if (LoginSession.getInstance().getUser() != null) {
            role = LoginSession.getInstance().getUser().getRole();
        }
        storeEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Store store = item.getValue(Store.class);
                    store.setStoreID(item.getKey());
                    int pos = -1;
                    for (Store mStore : stores) {
                        if (mStore.getStoreID().equals(store.getStoreID())) {
                            pos=stores.indexOf(mStore);
                        }
                    }
                    if (pos != -1) {
                        stores.set(pos, store);
                    } else {
                        stores.add(store);
                    }
                    Log.d("added", "added");
                }
                itemadapter.notifyDataSetChanged();
                new calculateDistance().execute();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        if (role != 0) {
            dbRef.child(getString(R.string.store_CODE))
                    .orderByChild("pro_dist")
                    .equalTo(dist_pro)
                    .addValueEventListener(storeEventListener);
        } else {
            dbRef.child(getString(R.string.store_CODE))
                    .orderByChild("isHidden_dis_pro")
                    .equalTo(String.valueOf(false) + "_" + dist_pro)
                    .addValueEventListener(storeEventListener);
        }
    }

    private void ref(final View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerV_storefrag);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        stores = new ArrayList<>();
        itemadapter = new Store_recycler_adapter(stores, getContext(), stRef);
        itemadapter.setOnItemClickLiestner(new Store_recycler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(Store store, View itemView) {
                Intent intent_storedetail = new Intent(getContext(),
                        StoreDeatailActivity.class);
                intent_storedetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ChooseStore.getInstance().setStore(store);
                ActivityOptionsCompat optionsCompat
                        = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(), itemView.findViewById(R.id.imgv_avatar_storeitem),
                        "avatarStore");
//                Toast.makeText(getContext(),
//                        selected_store.getName() + "", Toast.LENGTH_SHORT).show();
                startActivity(intent_storedetail, optionsCompat.toBundle());
            }
        });
        mRecyclerView.setAdapter(itemadapter);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_storefrag);
        swipeRefresh.setColorSchemeResources(R.color.admin_color_selection_news,
                R.color.color_selection_report);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getStoreList(dist_pro);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    @Override
    public void onResume() {
        super.onResume();
        mIntentFilter = new IntentFilter(Const.INTENT_KEY_RECEIVE_LOCATION);
        mBroadcastReceiver = new LocationChange();
        broadcastIntent = new Intent();
        getActivity().registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mBroadcastReceiver!=null) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }
    }
    IntentFilter mIntentFilter;
    Intent broadcastIntent;
    LocationChange mBroadcastReceiver;
    class LocationChange extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Const.INTENT_KEY_RECEIVE_LOCATION)){
                if(intent.getStringExtra(Const.KEY_TINH).toString()!=null){
                    if(intent.getStringExtra(Const.KEY_HUYEN).toString()!=null){
                        dist_pro = intent.getStringExtra(Const.KEY_HUYEN).toString()
                                + "_" +intent.getStringExtra(Const.KEY_TINH).toString();
                        getStoreList(dist_pro);
                    }
                }
            }
        }
    }
}
