package com.app.ptt.comnha.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.app.ptt.comnha.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainNotifyFragment extends Fragment {
    ListView listView;

    public MainNotifyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("MainNotifyFragment","createview");
        View view = inflater.inflate(R.layout.fragment_main_notify, container, false);
        listView = (ListView) view.findViewById(R.id.listV_notifyfrag);
        return view;
    }

}
