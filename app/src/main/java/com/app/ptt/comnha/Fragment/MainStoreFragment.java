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
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainStoreFragment extends Fragment {
    ListView listView;
    notify_newstore_adapter itemadapter;
    ArrayList<NewstoreNotify> items;
    DatabaseReference dbRef;
    ChildEventListener childEventListener;
    String prio_dist="Quận 9_HCM";

    public MainStoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MainStoreFragment","createview");
        View view = inflater.inflate(R.layout.fragment_main_store, container, false);
        listView = (ListView) view.findViewById(R.id.listV_storefrag);
        return view;
    }

}
