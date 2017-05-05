package com.app.ptt.comnha.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.app.ptt.comnha.Activity.StoreDeatailActivity;
import com.app.ptt.comnha.Adapters.notify_newstore_adapter;
import com.app.ptt.comnha.Models.FireBase.NewstoreNotify;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminNewStoreFragment extends Fragment {

    ListView listView;
    notify_newstore_adapter itemadapter;
    ArrayList<NewstoreNotify> items;
    DatabaseReference dbRef;
    ChildEventListener childEventListener;
    ValueEventListener storeValueListener;
    String dist_pro = "Quận 9_HCM";
    Store store = null;

    public AdminNewStoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_newstore, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_path));
        ref(view);
        getdata();
        Log.d("notinewstore_createView", "createview");
        return view;
    }

    private void getdata() {
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                NewstoreNotify item = dataSnapshot.getValue(NewstoreNotify.class);
                item.setId(key);
                items.add(item);
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
        dbRef.child(getString(R.string.notify_newstore_CODE))
                .orderByChild("district_province")
                .equalTo(dist_pro)
                .addChildEventListener(childEventListener);
    }

    private void ref(View view) {
        listView = (ListView) view.findViewById(R.id.listview_newstore_frag);
        items = new ArrayList<>();
        itemadapter = new notify_newstore_adapter(getActivity(), items);
        listView.setAdapter(itemadapter);
        itemadapter.setOnItemClickLiestner(new notify_newstore_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(final NewstoreNotify notify, Activity activity) {
                if (!notify.isReadstate()) {
                    String key = notify.getId();
                    NewstoreNotify childNotify = notify;
                    notify.setReadstate(true);
                    childNotify.setId(null);
                    childNotify.setReadstate(true);
                    Log.d("district_province", childNotify.getDistrict_province());
                    childNotify.setReadState_pro_dist(String.valueOf(childNotify.isReadstate())
                            + "_" + childNotify.getDistrict_province());
                    Map<String, Object> notifyValue = childNotify.toMap();
                    Map<String, Object> updateChild = new HashMap<>();
                    updateChild.put(getString(R.string.notify_newstore_CODE) + key,
                            notifyValue);
                    dbRef.updateChildren(updateChild).addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dbRef.child(getString(R.string.store_CODE) +
                                            notify.getStoreID())
                                            .addValueEventListener(storeValueListener);
                                }
                            }
                    );
                } else {
                    dbRef.child(getString(R.string.store_CODE) +
                            notify.getStoreID())
                            .addValueEventListener(storeValueListener);
                }

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("notifynewstore_onAttach", "attached");
        storeValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                store = dataSnapshot.getValue(Store.class);
                String key = dataSnapshot.getKey();
                store.setStoreID(key);
                ChooseStore.getInstance().setStore(store);
                Intent intent_openstore = new Intent(getActivity(),
                        StoreDeatailActivity.class);
                intent_openstore.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_openstore);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("notifynewstore_onResume", "resume");
    }

    @Override
    public void onDetach() {
        Log.d("adminnewstore", "Detached");
        dbRef.removeEventListener(childEventListener);
        dbRef.removeEventListener(storeValueListener);
        super.onDetach();
    }
}
