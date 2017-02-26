package com.app.ptt.comnha.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.app.ptt.comnha.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PickProvinceDialogFragment extends DialogFragment {

    private ListView listView;

    public PickProvinceDialogFragment() {
        // Required empty public constructor
    }


    public interface OnnPickProvinceListener {
        void onPickProvince(String province, int position);
    }

    private OnnPickProvinceListener onPickProvinceListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pick_province_dialog, container, false);
        anhxa(view);
        return view;
    }

    private void anhxa(View view) {
        listView = (ListView) view.findViewById(R.id.frg_pickProvince_listV);
        final ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getActivity(), R.array.tinhtp, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onPickProvinceListener.onPickProvince(adapter.getItem(position).toString(), position);
                dismiss();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(getResources().getString(R.string.text_pickProvince));
    }

    public void setOnPickProvinceListener(OnnPickProvinceListener listener) {
        onPickProvinceListener = listener;
    }
}
