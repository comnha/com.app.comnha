package com.app.ptt.comnha.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.app.ptt.comnha.Interfaces.LocationFinderListener;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Utils.MyTool;
import com.app.ptt.comnha.Utils.PlaceAPI;
import com.app.ptt.comnha.Utils.PlaceAttribute;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.EditLocal;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditStoreDialogFragment extends DialogFragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, LocationFinderListener {

    EditText edt_tenquan, edt_sdt,
            edt_giamin, edt_giamax;
    AutoCompleteTextView edt_diachi;
    Button btn_timestart, btn_timeend, btn_cancel, btn_ok;
    private TimePickerDialog tpd;
    private Calendar now;
    int edtID;
    int hour, min;
    Map<String, Object> childUpdates;
    ArrayList<String> resultList;
    private DatabaseReference dbRef;
    String prov, dist;
    Store location;
    ProgressDialog mProgressDialog;

    MyTool myTool;
    int pos = -1;
    PlaceAPI placeAPI;
    Store newLocation;
    ArrayList<PlaceAttribute> placeAttributes;

    public EditStoreDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_store_dialog, container, false);
        location = EditLocal.getInstance().getStore();
        now = Calendar.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        childUpdates=new HashMap<>();
        anhxa(view);
        return view;
    }

    private void anhxa(View view) {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getString(R.string.txt_plzwait));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        edt_diachi = (AutoCompleteTextView) view.findViewById(R.id.frg_reportstore_autocomplete);
        edt_tenquan = (EditText) view.findViewById(R.id.frg_reportstore_edt_tenquan);
        edt_sdt = (EditText) view.findViewById(R.id.frg_reportstore_edt_sdt);
        edt_giamin = (EditText) view.findViewById(R.id.frg_reportstore_edt_giamin);
        edt_giamax = (EditText) view.findViewById(R.id.frg_reportstore_edt_giamax);
        btn_cancel = (Button) view.findViewById(R.id.frg_reportstore_btncancel);
        btn_ok = (Button) view.findViewById(R.id.frg_reportstore_btnok);
        btn_timestart = (Button) view.findViewById(R.id.frg_reportstore_btn_giomo);
        btn_timeend = (Button) view.findViewById(R.id.frg_reportstore_btn_giodong);
        tpd = TimePickerDialog.newInstance(this, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), now.get(Calendar.SECOND), true);
        tpd.setOnDismissListener(this);
        tpd.setOnCancelListener(this);
        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_timestart.setOnClickListener(this);
        btn_timeend.setOnClickListener(this);
        edt_diachi.setAdapter(new PlaceAutoCompleteAdapter(getContext(), R.layout.autocomplete_list_item));
        edt_diachi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(getResources().getString(R.string.txt_reportStore));
    }

    @Override
    public void onStart() {
        super.onStart();
//        edt_tenquan.setText(EditLocal.getInstance().getStore().getName());
//        edt_diachi.setText(EditLocal.getInstance().getStore().getDiachi());
//        edt_sdt.setText(EditLocal.getInstance().getStore().getSdt());
//        edt_giamax.setText(EditLocal.getInstance().getStore().getGiamax()+"");
//        edt_giamin.setText(EditLocal.getInstance().getStore().getGiamin()+"");
//        btn_opentime.setText(EditLocal.getInstance().getStore().getTimestart());
//        btn_closetime.setText(EditLocal.getInstance().getStore().getTimeend());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_reportstore_btnok:
//                if (edt_tenquan.getText().toString().equals(EditLocal.getInstance().getStore().getName())
//                        && edt_diachi.getText().toString().equals(EditLocal.getInstance().getStore().getDiachi())
//                        && edt_sdt.getText().toString().equals(EditLocal.getInstance().getStore().getSdt())
//                        && edt_giamax.getText().toString().equals(EditLocal.getInstance().getStore().getGiamax()+"")
//                        && edt_giamin.getText().toString().equals(EditLocal.getInstance().getStore().getGiamin()+"")
//                        && btn_closetime.getText().toString().equals(EditLocal.getInstance().getStore().getTimeend())
//                        && btn_opentime.getText().toString().equals(EditLocal.getInstance().getStore().getTimestart())) {
//                    new AlertDialog.Builder(getContext())
//                            .setMessage("Bạn không có thay đổi nào cả!!!")
//                            .setPositiveButton("Trở lại", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            })
//                            .show();
//                } else {
//                    location.setName(edt_tenquan.getText().toString());
//                    location.setSdt(edt_sdt.getText().toString());
//                    location.setGiamax(Long.parseLong(edt_giamax.getText().toString()));
//                    location.setGiamin(Long.parseLong(edt_giamin.getText().toString()));
//                    location.setTimestart(btn_opentime.getText().toString());
//                    location.setTimeend(btn_closetime.getText().toString());
//                    if(!edt_diachi.getText().toString().equals(EditLocal.getInstance().getStore().getDiachi())){
//                            placeAPI = new PlaceAPI(edt_diachi.getText().toString(), this);
//                    }else{
//                        Map<String, Object> updateLocal = location.toMap();
//                        childUpdates.put(
//                                getResources().getString(R.string.locations_CODE)
//                                        + location.getLocaID(), updateLocal);
//                        dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isComplete()) {
//                                    if(!location.getVisible()){
//                                        Notification notification = new Notification();
//                                        String key1 = dbRef.child(getResources().getString(R.string.notification_CODE) + "admin").push().getKey();
//                                        notification.setAccount(MyService.getUserAccount());
//                                        notification.setDate(new Times().getDate());
//                                        notification.setTime(new Times().getTime());
//                                        notification.setType(4);
//                                        notification.setLocation(location);
//                                        notification.setReaded(false);
//                                        notification.setTo("admin");
//                                        Map<String, Object> notificationValue = notification.toMap();
//                                        childUpdates.put(getResources().getString(R.string.notification_CODE) + "admin/" + key1, notificationValue);
//                                        dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isComplete()) {
//                                                    Toast.makeText(getContext(), "Đã sửa. Bạn vui lòng chờ admin phản hồi", Toast.LENGTH_SHORT).show();
//                                                    mProgressDialog.dismiss();
//                                                    getActivity().finish();
//                                                } else {
//                                                    mProgressDialog.dismiss();
//                                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                        });
//                                    }else{
//                                        mProgressDialog.dismiss();
//                                        Toast.makeText(getActivity(), "Sửa thành công", Toast.LENGTH_SHORT).show();
//                                        getActivity().finish();
//                                    }
//
//
//
//
//                                } else {
//                                    mProgressDialog.dismiss();
//                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    }
//                }

                break;
            case R.id.frg_reportstore_btncancel:
                new AlertDialog.Builder(getContext())
                        .setMessage("Bạn có muốn hủy???")
                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismiss();
                            }
                        })
                        .setNegativeButton("Trở lại", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.frg_reportstore_btn_giomo:
//                edtID = R.id.frg_addloction_btn_giomo;
//                tpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.frg_reportstore_btn_giodong:
//                edtID = R.id.frg_addloction_btn_giodong;
//                tpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                break;
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        switch (edtID) {
//            case R.id.frg_addloction_btn_giomo:
//                hour = hourOfDay;
//                min = minute;
//                break;
//            case R.id.frg_addloction_btn_giodong:
//                hour = hourOfDay;
//                min = minute;
//                break;
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        hour = -1;
        Log.d("cancel" + String.valueOf(edtID), String.valueOf(hour));
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        Log.d("dismiss" + String.valueOf(edtID), String.valueOf(hour));
        switch (edtID) {
//            case R.id.frg_addloction_btn_giomo:
//                if (hour > -1) {
//                    btn_opentime.setText(hour + "h" + min);
//                }
//                break;
//            case R.id.frg_addloction_btn_giodong:
//                if (hour > -1) {
//                    btn_closetime.setText(hour + "h" + min);
//                }
//                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onLocationFinderStart() {

    }

    @Override
    public void onLocationFinderSuccess(final PlaceAttribute placeAttribute) {
        if (placeAttribute != null && placeAttribute.getDistrict() != null && placeAttribute.getState() != null) {
            // newReport.setAddress(placeAttribute.getFullname());
            final LatLng newLatLng = myTool.returnLatLngByName(placeAttribute.getFullname());
            final PlaceAttribute myPlaceAttribute = placeAttribute;
            mProgressDialog.dismiss();
            placeAttribute.setPlaceLatLng(myTool.returnLatLngByName(placeAttribute.getFullname()));
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//            builder.setMessage("Địa chỉ: " + placeAttribute.getFullname()).setTitle("Xác nhận")
//                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            location.setLat(newLatLng.latitude);
//                            location.setLng(newLatLng.longitude);
//                            location.setTinhtp(myPlaceAttribute.getState());
//                            location.setQuanhuyen(myPlaceAttribute.getDistrict());
//                            location.setTime(new Times().getTime());
//                            location.setDate(new Times().getDate());
//                            location.setIndex(myPlaceAttribute.getState()+"_"+myPlaceAttribute.getDistrict());
//                            Map<String, Object> updateLocal = location.toMap();
////                            childUpdates.put(
////                                    getResources().getString(R.string.locations_CODE)
////                                            + location.getLocaID(), updateLocal);
//                            dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isComplete()) {
//                                        mProgressDialog.dismiss();
//                                        Toast.makeText(getActivity(), "Sửa thành công", Toast.LENGTH_SHORT).show();
//                                        getActivity().finish();
//                                    } else {
//                                        mProgressDialog.dismiss();
//                                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//
//                            dialog.dismiss();
//                        }
//                    })
//                    .setNegativeButton("Thử lại", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            AlertDialog dialog = builder.create();
//            dialog.show();
        } else {
            Toast.makeText(getActivity(), "Lỗi! Kiểm tra dữ liệu nhập vàp ", Toast.LENGTH_LONG).show();
        }
        pos = -1;
    }

    @Override
    public void onGeocodingFinderSuccess(String address) {

    }

    class PlaceAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {


        public PlaceAutoCompleteAdapter(Context context, int resource) {
            super(context, resource);
            myTool = new MyTool(getContext());
        }

        @Override
        public int getCount() {
            if (resultList != null) {
                return resultList.size();
            } else return 0;
        }

        @Nullable
        @Override
        public String getItem(int position) {
            return resultList.get(position);
        }

        @Override
        public Filter getFilter() {
            final Filter filter = new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        placeAttributes = new ArrayList<>();
                        if (constraint != null) {
                            placeAttributes = myTool.returnPlaceAttributeByName(constraint.toString().trim());
                            if (placeAttributes.size() > 0) {
                                resultList = new ArrayList<>();
                                for (PlaceAttribute placeAttribute : placeAttributes) {
                                    resultList.add(placeAttribute.getFullname());
                                }
                                filterResults.count = resultList.size();
                                filterResults.values = resultList;
                                if (resultList.size() > 0)
                                    return filterResults;
                            }
                        }

                    }
                    return null;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

}
