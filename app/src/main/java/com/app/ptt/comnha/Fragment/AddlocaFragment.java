package com.app.ptt.comnha.Fragment;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.Adapters.SingleImageImportRvAdapter;
import com.app.ptt.comnha.Classes.SelectedImage;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.Utils.MyTool;
import com.app.ptt.comnha.Utils.PlaceAPI;
import com.app.ptt.comnha.Utils.Times;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddlocaFragment extends Fragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener,
        DialogInterface.OnDismissListener, DialogInterface.OnCancelListener, PlaceSelectionListener {
    FloatingActionButton fab_save;
    public static final String LOG = AddlocaFragment.class.getSimpleName();

    EditText edt_storeName, edt_phoneNumb, edt_address;
    PlaceAutocompleteFragment _autocompleteFragment;
    Button btn_timestart, btn_timeend;
    ProgressDialog mProgressDialog;
    DatabaseReference dbRef;
    Calendar now;
    String tinh, huyen, diachi;
    TimePickerDialog tpd;
    int edtID, pos = -1;
    Store newLocation;
    int hour, min;
    String key;
    Geocoder gc;
    PlaceAPI placeAPI;
    AutoCompleteTextView autoCompleteText;
    String a = "";
    MyTool myTool;
    boolean isConnected = true;
    IntentFilter mIntentFilter;
    String mString;
    Toolbar toolbar;
    private TextView txtv_avatar, txtv_save;
    ImageView imgv_avatar;
    SingleImageImportRvAdapter singleImageImportRvAdapter;

    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastSendAddress)) {
                Log.i(LOG + ".onReceive form Service", "isConnected= " + intent.getBooleanExtra("isConnected", false));
                if (intent.getBooleanExtra("isConnected", false)) {
                    isConnected = true;
                } else
                    isConnected = false;
            }
        }
    };
    private RecyclerView imagesrv;
    private GridLayoutManager imageslm;
    private SelectedImage selectedImage;
    private BottomSheetDialog imgsDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        isConnected = MyService.returnIsNetworkConnected();
        if (!isConnected) {
//            Toast.makeText(getContext(), "Offline mode", Toast.LENGTH_SHORT).show();
        }
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
        getContext().registerReceiver(broadcastReceiver, mIntentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(broadcastReceiver);
    }

    public AddlocaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addloca, container, false);
        isConnected = MyService.returnIsNetworkConnected();
        now = Calendar.getInstance();
        gc = new Geocoder(getContext(), Locale.getDefault());
        anhXa(view);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        return view;
    }


    void anhXa(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_addloca);
        toolbar.setTitle(getString(R.string.text_addloca));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        toolbar.setBackgroundColor(getResources()
                .getColor(R.color.admin_color_selection_reports));


        edt_storeName = (EditText) view.findViewById(R.id.frg_addloction_edt_storename);
        edt_address = (EditText) view.findViewById(R.id.frg_addloction_edt_address);
        edt_phoneNumb = (EditText) view.findViewById(R.id.frg_addloction_edt_phonenumb);
        tpd = TimePickerDialog.newInstance(this, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), now.get(Calendar.SECOND), true);
        tpd.setAccentColor(getResources()
                .getColor(R.color.addlocal_color_opentime));
        tpd.setOnDismissListener(this);
        tpd.setOnCancelListener(this);

        btn_timeend = (Button) view.findViewById(R.id.frg_addloction_btn_closetime);
        btn_timestart = (Button) view.findViewById(R.id.frg_addloction_btn_opentime);
        btn_timeend.setOnClickListener(this);
        btn_timestart.setOnClickListener(this);
        btn_timestart.setText(new Times().getTimeNoSecond());
        btn_timeend.setText(new Times().getTimeNoSecond());
//        _autocompleteFragment = (PlaceAutocompleteFragment)
//                getActivity()
//                        .getFragmentManager()
//                        .findFragmentById(R.id.place_autocomplete_fragment);
//        _autocompleteFragment.setOnPlaceSelectedListener(this);
        txtv_avatar = (TextView) view.findViewById(R.id.frg_addloction_txtv_avatar);
        txtv_avatar.setOnClickListener(this);

        txtv_save = (TextView) view.findViewById(R.id.frg_addloction_txtv_save);
        txtv_save.setOnClickListener(this);

        imgv_avatar = (ImageView) view.findViewById(R.id.frg_addloction_imgv_avatar);
        imgv_avatar.setOnClickListener(this);
        imgv_avatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                imgv_avatar.setImageURI(null);
                selectedImage = null;
                return true;
            }
        });
        imgsDialog = new BottomSheetDialog(getContext());
        imgsDialog.setContentView(R.layout.layout_writepost_imgimporting);
        imgsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                selectedImage = singleImageImportRvAdapter.getSelectedImage();
                imgv_avatar.setImageURI(selectedImage.getUri());
            }
        });

        imagesrv = (RecyclerView) imgsDialog.findViewById(R.id.rv_images_imgimporting);
        imageslm = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
        imagesrv.setLayoutManager(imageslm);
        singleImageImportRvAdapter = new SingleImageImportRvAdapter(getContext(), getContext().getContentResolver());
        imagesrv.setAdapter(singleImageImportRvAdapter);
        singleImageImportRvAdapter.setOnSingleClickListener(new SingleImageImportRvAdapter.OnSingleClickListener() {
            @Override
            public void onClick(boolean isDismiss) {
                if (isDismiss) {
                    imgsDialog.dismiss();
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//                if (edt_tenquan.getText().toString().trim().equals("")) {
//                    Snackbar.make(view, getResources().getText(R.string.txt_notenquan), Snackbar.LENGTH_SHORT).show();
//                } else if (autoCompleteText.getText().toString().trim().equals("")) {
//                    Snackbar.make(view, getResources().getText(R.string.txt_nodiachi), Snackbar.LENGTH_SHORT).show();
//                } else if (edt_giamin.getText().toString().trim().equals("")) {
//                    Snackbar.make(view, getResources().getText(R.string.txt_nogia), Snackbar.LENGTH_SHORT).show();
//                } else if (edt_giamax.getText().toString().trim().equals("")) {
//                    Snackbar.make(view, getResources().getText(R.string.txt_nogia), Snackbar.LENGTH_SHORT).show();
////                } else if (edt_sdt.getText().toString().trim().equals("")) {
////                    Snackbar.make(view, getResources().getText(R.string.txt_nosdt), Snackbar.LENGTH_SHORT).show();
////                } else if (edt_timestart.getText().toString().trim().equals("")) {
////                    Snackbar.make(view, getResources().getText(R.string.txt_noopentime), Snackbar.LENGTH_SHORT).show();
////                } else if (edt_timeend.getText().toString().trim().equals("")) {
////                    Snackbar.make(view, getResources().getText(R.string.txt_noopentime), Snackbar.LENGTH_SHORT).show();
//                } else if (Long.valueOf(edt_giamax.getText().toString()) <= Long.valueOf(edt_giamin.getText().toString())) {
//                    Snackbar.make(view, getResources().getText(R.string.txt_giawarn), Snackbar.LENGTH_SHORT).show();
//                } else {
//                    if (isConnected) {
//                        if (MyService.getUserAccount() != null)
//                            addNewLoca();
//                        else
//                            Toast.makeText(getContext(), "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
//                    } else
//                        Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
//
//                }
//                break;
            case R.id.frg_addloction_txtv_avatar:

                break;
            case R.id.frg_addloction_btn_opentime:
                edtID = R.id.frg_addloction_btn_opentime;
                tpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.frg_addloction_btn_closetime:
                edtID = R.id.frg_addloction_btn_closetime;
                tpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.frg_addloction_imgv_avatar:
                singleImageImportRvAdapter.readthentranstoarray();
                imagesrv.scrollToPosition(0);
                imgsDialog.show();
                break;
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        switch (edtID) {
            case R.id.frg_addloction_btn_opentime:
                hour = hourOfDay;
                min = minute;
                break;
            case R.id.frg_addloction_btn_closetime:
                hour = hourOfDay;
                min = minute;
                break;
        }
    }


    @Override
    public void onCancel(DialogInterface dialogInterface) {
        hour = -1;
        Log.d("cancel" + String.valueOf(edtID), String.valueOf(hour));
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        switch (edtID) {
            case R.id.frg_addloction_btn_opentime:
                if (hour > -1) {
                    btn_timestart.setText(hour + ":" + min);
                }
                break;
            case R.id.frg_addloction_btn_closetime:
                if (hour > -1) {
                    btn_timeend.setText(hour + ":" + min);
                }
                break;
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        diachi = place.getAddress().toString();
        _autocompleteFragment.setText(place.getAddress());
    }

    @Override
    public void onError(Status status) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    //    class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
//        ArrayList<String> resultList;
//        Context mContext;
//        int mResource;
//
//        public PlacesAutoCompleteAdapter(Context context, int resource) {
//            super(context, resource);
//            mContext = context;
//            mPlaceAttribute = new ArrayList<>();
//            mResource = resource;
//            myTool = new MyTool(getContext(), AddlocaFragment.class.getSimpleName());
//        }
//
//        @Override
//        public int getCount() {
//            if (resultList != null) {
//                return resultList.size();
//            } else return 0;
//        }
//
//        @Nullable
//        @Override
//        public String getItem(int position) {
//            return resultList.get(position);
//        }
//
//        @NonNull
//        @Override
//        public Filter getFilter() {
//            final Filter filter = new Filter() {
//                @Override
//                protected FilterResults performFiltering(CharSequence constraint) {
//                    FilterResults filterResults = new FilterResults();
//                    if (constraint != null) {
//                        mPlaceAttribute = new ArrayList<>();
//                        mPlaceAttribute = myTool.returnPlaceAttributeByName(constraint.toString());
//                        if (mPlaceAttribute != null) {
//                            a = "OK";
//                            resultList = new ArrayList<>();
//                            for (PlaceAttribute placeAttribute : mPlaceAttribute)
//                                resultList.add(placeAttribute.getFullname());
//                            filterResults.values = resultList;
//                            filterResults.count = resultList.size();
//                        } else {
//                            a = null;
//                            PlaceAttribute a1 =new PlaceAttribute();
//                            a1.setFullname(constraint.toString());
//                            mPlaceAttribute.add(a1);
//                        }
//                    }
//                    return filterResults;
//                }
//
//                @Override
//                protected void publishResults(CharSequence constraint, FilterResults results) {
//                    if (results != null && results.count > 0) {
//                        notifyDataSetChanged();
//                    } else {
//                        notifyDataSetInvalidated();
//                    }
//                }
//            };
//            return filter;
//        }
//    }


//    @Override
//    public void onLocationFinderStart() {
//
//    }
//
//    @Override
//    public void onLocationFinderSuccess(PlaceAttribute placeAttribute) {
////        if (placeAttribute != null) {
////            final PlaceAttribute myPlaceAttribute = placeAttribute;
////            newLocation.setDiachi(placeAttribute.getFullname());
////            Log.i(LOG + ".onLocationFinder", placeAttribute.getState() + "-" + placeAttribute.getDistrict());
////            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
////            builder.setMessage("Địa chỉ: " + placeAttribute.getFullname()).setTitle("Xác nhận")
////                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
////                        @Override
////                        public void onClick(DialogInterface dialog, int which) {
////                            dialog.dismiss();
////                            mProgressDialog = ProgressDialog.show(getActivity(),
////                                    getResources().getString(R.string.txt_plzwait),
////                                    getResources().getString(R.string.txt_addinloca), true, true);
////                            newLocation.setLat(myPlaceAttribute.getPlaceLatLng().latitude);
////                            newLocation.setLng(myPlaceAttribute.getPlaceLatLng().longitude);
////                            newLocation.setTinhtp(myPlaceAttribute.getState());
////                            newLocation.setQuanhuyen(myPlaceAttribute.getDistrict());
////                            newLocation.setTime(new Times().getTime());
////                            newLocation.setDate(new Times().getDate());
////                            newLocation.setIndex(myPlaceAttribute.getState()+"_"+myPlaceAttribute.getDistrict());
////                            newLocation.setPvAVG(5);
////                            newLocation.setVsAVG(5);
////                            newLocation.setGiaAVG(5);
////                            newLocation.setTongAVG(5);
////                            newLocation.setSize(1);
////                            newLocation.setPvTong(5);
////                            newLocation.setVsTong(5);
////                            newLocation.setGiaTong(5);
////                            //if(MyService.getUserAccount().getRole())
////                                newLocation.setVisible(true);
//////                            else
//////                                newLocation.setVisible(false);
////                            newLocation.setUserId(MyService.getUserAccount().getId());
////                            Log.i(LOG + ".onLocation", tinh + " va " + huyen);
          //                 key = dbRef.child(getResources().getString(R.string.locations_CODE)).push().getKey();
////                            Map<String, Object> newLocaValue = newLocation.toMap();
////                            Map<String, Object> childUpdates = new HashMap<>();
////                            childUpdates.put(getResources().getString(R.string.locations_CODE)
////                                    + key, newLocaValue);
////                            newLocation.setLocaID(key);
////                            dbRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
////                                @Override
////                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
////                                    if (databaseError != null) {
////                                        mProgressDialog.dismiss();
////                                        Toast.makeText(getActivity(), "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
////                                    } else {
////                                            Map<String, Object> childUpdates = new HashMap<>();
////                                            Notification notification = new Notification();
////                                            String key1 = dbRef.child(getResources().getString(R.string.notification_CODE) + "admin").push().getKey();
////                                            notification.setAccount(MyService.getUserAccount());
////                                            notification.setDate(new Times().getDate());
////                                            notification.setTime(new Times().getTime());
////                                            notification.setType(2);
////
////                                            notification.setLocation(newLocation);
////                                            notification.setReaded(false);
////                                            notification.setTo("admin");
////                                            Map<String, Object> notificationValue = notification.toMap();
////                                            childUpdates.put(getResources().getString(R.string.notification_CODE) + "admin/" + key, notificationValue);
////                                            dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                @Override
////                                                public void onComplete(@NonNull Task<Void> task) {
////                                                    if (task.isComplete()) {
////                                                        mProgressDialog.dismiss();
////                                                        Toast.makeText(getActivity(),
////                                                                getResources().getString(R.string.text_noti_addloca_succes)
////                                                                , Toast.LENGTH_SHORT).show();
////                                                        getActivity().finish();
////                                                    } else {
////                                                        mProgressDialog.dismiss();
////                                                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
////                                                    }
////                                                }
////                                            });
////
////
////
////                                    }
////                                }
////
////                            });
////
////
////                        }
////                    })
////                    .setNegativeButton("Thử lại", new DialogInterface.OnClickListener() {
////                        @Override
////                        public void onClick(DialogInterface dialog, int which) {
////                            dialog.dismiss();
////                        }
////                    });
////            AlertDialog dialog = builder.create();
////            dialog.show();
//            //builder.create();
//        } else {
//            //mProgressDialog.dismiss();
//            Toast.makeText(getActivity(), "Lỗi! Kiểm tra dữ liệu nhập vàp ", Toast.LENGTH_LONG).show();
//        }
//        pos=-1;
//    }
//
//    @Override
//    public void onGeocodingFinderSuccess(String address) {
//
//    }

}
