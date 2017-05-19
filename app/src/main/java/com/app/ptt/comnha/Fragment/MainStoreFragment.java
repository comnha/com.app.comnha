package com.app.ptt.comnha.Fragment;


import android.content.Context;
import android.content.Intent;
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

import com.app.ptt.comnha.Activity.StoreDeatailActivity;
import com.app.ptt.comnha.Adapters.Store_recycler_adapter;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
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
public class MainStoreFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    Store_recycler_adapter itemadapter;
    ArrayList<Store> items;
    DatabaseReference dbRef;
    ValueEventListener childEventListener;
    String pro_dist = "Quận 9_HCM";
    StorageReference stRef;
    SwipeRefreshLayout swipeRefresh;

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
        getStoreList();
        return view;
    }

    private void getStoreList() {
        childEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Store store = item.getValue(Store.class);
                    store.setStoreID(item.getKey());
                    items.add(store);
                    itemadapter.setStorageRef(
                            FirebaseStorage.getInstance().getReferenceFromUrl(
                                    getString(R.string.firebaseStorage_path)));
                    Log.d("added", "added");
                }
                itemadapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
                dbRef.child(getString(R.string.store_CODE))
                        .orderByChild("isHidden_dis_pro")
                        .equalTo(String.valueOf(false) + "_" + pro_dist)
                        .removeEventListener(childEventListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.store_CODE))
                .orderByChild("isHidden_dis_pro")
                .equalTo(String.valueOf(false) + "_" + pro_dist)
                .addValueEventListener(childEventListener);
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
                items.clear();
                getStoreList();
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
