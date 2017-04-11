package com.app.ptt.comnha.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.app.ptt.comnha.R;


public class MainPostFragment extends Fragment {
    ListView listView;
    public MainPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MainPostFragment","createview");
        View view=inflater.inflate(R.layout.fragment_main_post, container, false);;
        listView = (ListView) view.findViewById(R.id.listV_postfrag);
        return view;
    }


}
