package com.app.ptt.comnha.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.app.ptt.comnha.Adapters.notify_newstore_adapter;
import com.app.ptt.comnha.Models.FireBase.NewstoreNotify;
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
public class AdminNewStoreFragment extends Fragment {

    ListView listView;
    notify_newstore_adapter itemadapter;
    ArrayList<NewstoreNotify> items;
    DatabaseReference dbRef;
    ChildEventListener childEventListener;
    String dist_pro = "Quận 9_HCM";

    public AdminNewStoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_newstore, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_path));
        listView = (ListView) view.findViewById(R.id.listview_newstore_frag);
        items = new ArrayList<>();
        itemadapter = new notify_newstore_adapter(getContext(), items);
        listView.setAdapter(itemadapter);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                NewstoreNotify item = dataSnapshot.getValue(NewstoreNotify.class);
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
        dbRef.child(getString(R.string.notify_newstore_CODE)).orderByChild("district_province")
                .equalTo(dist_pro).addChildEventListener(childEventListener);
        return view;
    }

    @Override
    public void onDetach() {
        Log.d("adminnewstore", "Detached");
        dbRef.removeEventListener(childEventListener);
        super.onDetach();
    }
}
