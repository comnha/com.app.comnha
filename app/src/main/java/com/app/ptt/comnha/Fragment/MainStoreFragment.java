package com.app.ptt.comnha.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ptt.comnha.Activity.StoreDeatailActivity;
import com.app.ptt.comnha.Adapters.Store_recyler_adapter;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainStoreFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    Store_recyler_adapter itemadapter;
    ArrayList<Store> items;
    DatabaseReference dbRef;
    ChildEventListener childEventListener;
    String pro_dist = "Quận 9_HCM";
    StorageReference stRef;

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
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Store store = dataSnapshot.getValue(Store.class);
                store.setStoreID(dataSnapshot.getKey());
                items.add(store);
                itemadapter.setStorageRef(
                        FirebaseStorage.getInstance().getReferenceFromUrl(
                                getString(R.string.firebaseStorage_path)));
                Log.d("added", "added");
                itemadapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.store_CODE))
                .orderByChild("pro_dist")
                .equalTo(pro_dist)
                .addChildEventListener(childEventListener);
        return view;
    }

    private void ref(final View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerV_storefrag);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        items = new ArrayList<>();
        itemadapter = new Store_recyler_adapter(items, getContext());
        itemadapter.setOnItemClickLiestner(new Store_recyler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(Store store,View itemView) {
                Intent intent_storedetail = new Intent(getContext(), StoreDeatailActivity.class);
                intent_storedetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityOptionsCompat optionsCompat
                        = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(), itemView.findViewById(R.id.item_list_imgV), "avatarStore");

                intent_storedetail.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frag_storedetail_CODE));
                ChooseStore.getInstance().setStore(store);
                startActivity(intent_storedetail,optionsCompat.toBundle());
            }
        });
        mRecyclerView.setAdapter(itemadapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dbRef.removeEventListener(childEventListener);
    }
}
