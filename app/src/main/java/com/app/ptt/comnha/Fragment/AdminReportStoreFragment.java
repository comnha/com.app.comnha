package com.app.ptt.comnha.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.app.ptt.comnha.Adapters.notify_reportstore_adapter;
import com.app.ptt.comnha.Models.FireBase.ReportstoreNotify;
import com.app.ptt.comnha.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminReportStoreFragment extends Fragment {

    ListView listView;
    notify_reportstore_adapter itemadapter;
    ArrayList<ReportstoreNotify> items;
    DatabaseReference dbRef;
    ChildEventListener childEventListener;
    String dist_pro = "";

    public AdminReportStoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_report_store, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_path));
        listView = (ListView) view.findViewById(R.id.listview_reportstore_frag);
        items = new ArrayList<>();
        itemadapter = new notify_reportstore_adapter(getContext(), items);
        listView.setAdapter(itemadapter);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ReportstoreNotify item = dataSnapshot.getValue(ReportstoreNotify.class);
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
        dbRef.child(getString(R.string.notify_reportstore_CODE)).orderByChild("district_province")
                .equalTo(dist_pro).addChildEventListener(childEventListener);
        return view;
    }

}
