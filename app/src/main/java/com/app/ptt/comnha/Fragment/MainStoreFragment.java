package com.app.ptt.comnha.Fragment;


import android.content.Context;
import android.content.Intent;
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
import com.app.ptt.comnha.Interfaces.Comunication;
import com.app.ptt.comnha.Interfaces.SendLocationListener;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
import com.app.ptt.comnha.SingletonClasses.CoreManager;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class MainStoreFragment extends Fragment implements SendLocationListener {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    Store_recycler_adapter itemadapter;
    ArrayList<Store> items;
    DatabaseReference dbRef;
    ValueEventListener childEventListener;
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
                getReferenceFromUrl(getString(R.string.firebase_path));
        stRef = FirebaseStorage.getInstance().getReferenceFromUrl(
                getString(R.string.firebaseStorage_path));
        ref(view);
        if(null!=CoreManager.getInstance().getMyLocation()) {
            dist_pro =CoreManager.getInstance().getMyLocation().getDistrict()+"_"+CoreManager.getInstance().getMyLocation().getProvince();
            getStoreList(dist_pro);
        }else{
            if(getView()!=null) {

                AppUtils.showSnackbarWithoutButton(getView(), "Không tìm thấy vị trí của bạn");
            }
        }
        myTool = new MyTool(getActivity());
        Comunication.sendLocationListener = this;
        return view;
    }

    @Override
    public void notice() {

       if(null!=CoreManager.getInstance().getMyLocation()) {
           dist_pro =CoreManager.getInstance().getMyLocation().getDistrict()+"_"+CoreManager.getInstance().getMyLocation().getProvince();
           getStoreList(dist_pro);
       }

    }

    public class calculateDistance extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            for (Store store : items) {
                double distance = 0;

                try {
                    distance = myTool.distanceFrom_in_Km(store.getLat(), store.getLng(),
                            CoreManager.getInstance().getMyLocation().getLat(), CoreManager.getInstance().getMyLocation().getLng());
                    int c = (int) Math.round(distance);
                    int d = c / 1000;
                    int e = c % 1000;
                    int f = e / 100;
                    store.setDistance(d + "," + f);
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
        items.clear();
        childEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Store store = item.getValue(Store.class);
                    store.setStoreID(item.getKey());
                    if(!checkExist(store.getStoreID())) {
                        items.add(store);
                        itemadapter.notifyDataSetChanged();
                    }
                    itemadapter.setStorageRef(
                            FirebaseStorage.getInstance().getReferenceFromUrl(
                                    getActivity().getResources().getString(R.string.firebaseStorage_path)));
                    Log.d("added", "added");

                }
                swipeRefresh.setRefreshing(false);
                itemadapter.notifyDataSetChanged();
                if (null != CoreManager.getInstance().getMyLocation()) {
                    new calculateDistance().execute();
                }
//                dbRef.child(getString(R.string.store_CODE))
//                        .orderByChild("isHidden_dis_pro")
//                        .equalTo(String.valueOf(false) + "_" + dist_pro)
//                        .removeEventListener(childEventListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.store_CODE))
                .orderByChild("isHidden_dis_pro")
                .equalTo(String.valueOf(false) + "_" + dist_pro)
                .addValueEventListener(childEventListener);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(null!=CoreManager.getInstance().getMyLocation()) {
                    new calculateDistance().execute();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public boolean checkExist(String id){
        for (Store store:items){
            if(store.getStoreID().equals(id)){
                return true;
            }
        }
        return false;
    }
    private void ref(final View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerV_storefrag);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        items = new ArrayList<>();
        itemadapter = new Store_recycler_adapter(items, getContext());
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
}
