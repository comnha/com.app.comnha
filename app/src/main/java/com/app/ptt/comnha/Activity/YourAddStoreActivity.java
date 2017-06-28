package com.app.ptt.comnha.Activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.app.ptt.comnha.Adapters.Store_recycler_adapter;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
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
public class YourAddStoreActivity extends AppCompatActivity {


    StorageReference stRef;
    DatabaseReference dbRef;
    RecyclerView rv_store;
    RecyclerView.LayoutManager storeLm;
    ArrayList<Store> stores;
    Store_recycler_adapter storeAdapter;
    ValueEventListener storeEventListener;
    User user = LoginSession.getInstance().getUser();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youraddstore);
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Const.DATABASE_PATH);
        stRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Const.STORAGE_PATH);
        Ref();
        getAllImg();
    }

    private void Ref() {
        rv_store = (RecyclerView) findViewById(R.id.rv_img_youraddstore);
        storeLm = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL,
                false);
        rv_store.setLayoutManager(storeLm);
        stores = new ArrayList<>();
        storeAdapter = new Store_recycler_adapter(stores, this, stRef);
        rv_store.setAdapter(storeAdapter);
        storeAdapter.setOnItemClickLiestner(new Store_recycler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(Store store, View itemView) {

            }
        });
    }

    private void getAllImg() {
        storeEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Store store = item.getValue(Store.class);
                    store.setStoreID(item.getKey());
                    stores.add(store);
                    storeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.store_CODE))
                .orderByChild("isHidden_uID")
                .equalTo(false + "_" + user.getuID())
                .addListenerForSingleValueEvent(storeEventListener);
    }
}
