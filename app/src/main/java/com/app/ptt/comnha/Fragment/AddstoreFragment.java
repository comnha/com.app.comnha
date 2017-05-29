package com.app.ptt.comnha.Fragment;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.SingleImageImportRvAdapter;
import com.app.ptt.comnha.Classes.SelectedImage;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.NewstoreNotify;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Modules.LocationFinderListener;
import com.app.ptt.comnha.Modules.MyTool;
import com.app.ptt.comnha.Modules.PlaceAPI;
import com.app.ptt.comnha.Modules.PlaceAttribute;
import com.app.ptt.comnha.Modules.Times;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.AppUtils;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;
import java.io.IOException;
import java.security.Permission;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddstoreFragment extends Fragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener,
        DialogInterface.OnDismissListener, DialogInterface.OnCancelListener, LocationFinderListener {
    FloatingActionButton fab_save;
    public static final String LOG = AddstoreFragment.class.getSimpleName();
    ArrayList<PlaceAttribute> mPlaceAttribute;
    EditText edt_storeName, edt_phoneNumb ;
    List<String> addressList;
    AutoCompleteTextView edt_address;
    Button btn_opentime, btn_closetime;
    ProgressDialog mProgressDialog, uploadImgDialog;
    DatabaseReference dbRef;
    StorageReference stRef;
    UploadTask uploadTask;
    Calendar now;
    TimePickerDialog tpd;
    int edtID, pos = -1;
    Store newLocation;
    int hour, min;
    String key;
    PlaceAPI placeAPI;
    boolean isConnected = true;
    IntentFilter mIntentFilter;
    Toolbar toolbar;
    private TextView txtv_avatar, txtv_save;
    ImageView imgv_avatar;
    SingleImageImportRvAdapter singleImageImportRvAdapter;
    private RecyclerView imagesrv;
    private GridLayoutManager imageslm;
    private SelectedImage selectedImage;
    private BottomSheetDialog imgsDialog;
    private MyTool myTool;
    String storename, address, phonenumb, opentime,
            province, district , storeimg = "";
    double lat, lng;
    String userID;//người tạo
    Store store;
    String un;
    NewstoreNotify notify;

    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastSendAddress)) {
                Log.i(LOG + ".onReceive form Service", "isConnected= " + intent.getBooleanExtra("isConnected", false));
                isConnected = intent.getBooleanExtra("isConnected", false);
            }
        }
    };


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

    public AddstoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addstore, container, false);
        isConnected = MyService.returnIsNetworkConnected();
        now = Calendar.getInstance();
        if (LoginSession.getInstance().getFirebUser() != null) {
            userID = LoginSession.getInstance().getFirebUser().getUid();
            un = LoginSession.getInstance().getFirebUser().getDisplayName();
            if (un==null){
                un=LoginSession.getInstance().getFirebUser().getEmail();
            }
        } else {
            getActivity().finish();
        }
        anhXa(view);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        FirebaseStorage storage = FirebaseStorage.getInstance();
        stRef = storage.getReferenceFromUrl(getString(R.string.firebaseStorage_path));

        return view;
    }

    private void setupAutoCompleteTextView(){
        myTool=new MyTool(getActivity());
        addressList=new ArrayList<>();
        edt_address.setAdapter(new PlacesAutoCompleteAdapter(getContext(), R.layout.autocomplete_list_item));
        edt_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
            }
        });
    }
        class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        ArrayList<String> resultList;
        Context mContext;
        int mResource;

        public PlacesAutoCompleteAdapter(Context context, int resource) {
            super(context, resource);
            mContext = context;
            mPlaceAttribute = new ArrayList<>();
            mResource = resource;
        }

        @Override
        public int getCount() {
            if (resultList != null) {
                return resultList.size();
            } else return 0;
        }


        @Override
        public String getItem(int position) {
            return resultList.get(position);
        }

        @NonNull
        @Override
        public Filter getFilter() {
            final Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        mPlaceAttribute = new ArrayList<>();
                        mPlaceAttribute = myTool.returnPlaceAttributeByName(constraint.toString());
                        if (mPlaceAttribute != null) {
                            resultList = new ArrayList<>();
                            for (PlaceAttribute placeAttribute : mPlaceAttribute)
                                resultList.add(placeAttribute.getFullname());
                            filterResults.values = resultList;
                            filterResults.count = resultList.size();
                        } else {
                            PlaceAttribute a1 =new PlaceAttribute();
                            a1.setFullname(constraint.toString());
                            mPlaceAttribute.add(a1);
                        }
                    }
                    return filterResults;
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
    void anhXa(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(getResources()
                    .getColor(R.color.color_selection_report));
        }
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_addloca);
        toolbar.setTitle(getString(R.string.text_addloca));

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        toolbar.setBackgroundColor(getResources()
                .getColor(R.color.color_selection_report));
        edt_storeName = (EditText) view.findViewById(R.id.frg_addloction_edt_storename);
        edt_storeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                storename = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edt_address = (AutoCompleteTextView) view.findViewById(R.id.frg_addloction_edt_address);
        setupAutoCompleteTextView();
        edt_phoneNumb = (EditText) view.findViewById(R.id.frg_addloction_edt_phonenumb);
        edt_phoneNumb.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phonenumb = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        tpd = TimePickerDialog.newInstance(this, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), now.get(Calendar.SECOND), true);
        tpd.setAccentColor(getResources()
                .getColor(R.color.addlocal_color_opentime));
        tpd.setOnDismissListener(this);
        tpd.setOnCancelListener(this);

        btn_closetime = (Button) view.findViewById(R.id.frg_addloction_btn_closetime);
        btn_opentime = (Button) view.findViewById(R.id.frg_addloction_btn_opentime);
        btn_closetime.setOnClickListener(this);
        btn_opentime.setOnClickListener(this);
        btn_opentime.setText(new Times().getTimeNoSecond());
        btn_closetime.setText(new Times().getTimeNoSecond());
        opentime = new Times().getTimeNoSecond() + " - "
                + new Times().getTimeNoSecond();
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
                if (selectedImage != null) {
                    storeimg = selectedImage.getUri().getLastPathSegment();
                    imgv_avatar.setImageURI(selectedImage.getUri());
                }
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
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getString(R.string.txt_addstore));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(true);
        uploadImgDialog = new ProgressDialog(getContext());
        uploadImgDialog.setMessage(getString(R.string.txt_updloadimg));
        uploadImgDialog.setMax(100);
        uploadImgDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        uploadImgDialog.setCanceledOnTouchOutside(true);
        uploadImgDialog.setCancelable(true);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frg_addloction_txtv_save:
                if (AppUtils.checkEmptyEdt(edt_storeName)) {
                    edt_storeName.setError(getString(R.string.txt_nostorename));
                } else if (AppUtils.checkEmptyEdt(edt_address)) {
                    edt_address.setError(getString(R.string.txt_noaddress));
                } else if (AppUtils.checkEmptyEdt(edt_phoneNumb)) {
                    edt_phoneNumb.setError(getText(R.string.txt_nophonenumb));
                } else {
                    new PlaceAPI(mPlaceAttribute.get(pos).getFullname(),this);
                }
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
                String[] permissons=new String[1];
                permissons[0]=Const.mListPermissions[0];
                List<String> results=AppUtils.checkPermissions(getActivity(),permissons);
                if (results.size()>0){
                    AppUtils.requestPermission(getActivity(),results,Const.PERMISSION_LOCATION_FLAG);
                } else{
                    singleImageImportRvAdapter.readthentranstoarray();
                    imagesrv.scrollToPosition(0);
                    imgsDialog.show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Const.PERMISSION_LOCATION_FLAG:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    singleImageImportRvAdapter.readthentranstoarray();
                    imagesrv.scrollToPosition(0);
                    imgsDialog.show();
                }else{

                }
                break;
        }
    }

    private void savestore() {
        final boolean[] isUploadImgSuccess = {false};
        String key = dbRef.child(getString(R.string.store_CODE)).push().getKey(),
                notifyKey = dbRef.child(getString(R.string.notify_newstore_CODE))
                        .push().getKey();

        store = new Store(storename, address, phonenumb, opentime,
                province, district, lat, lng, userID, storeimg);
        notify = new NewstoreNotify(key, storename, address,
                userID, un, district, province);
        Map<String, Object> storeValues = store.toMap(),
                notifyValues = notify.toMap();
        final Map<String, Object> childUpdate = new HashMap<>();
        childUpdate.put(getString(R.string.store_CODE) + key, storeValues);
        childUpdate.put(getString(R.string.notify_newstore_CODE) + notifyKey,
                notifyValues);
        if (selectedImage != null) {
            Log.d("path", selectedImage.getUri().toString());
            uploadImgDialog.show();
            StorageReference mStorageReference = stRef.child(storeimg);
            uploadTask = mStorageReference.putFile(
                    Uri.fromFile(new File(selectedImage.getUri().toString())));

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int progress = (int) (taskSnapshot.getBytesTransferred() * 100
                            / taskSnapshot.getTotalByteCount());
//                    Log.d("getBytesTransferred", progress + "");
                    uploadImgDialog.setProgress(progress);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    isUploadImgSuccess[0] = true;
                    uploadImgDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    uploadImgDialog.dismiss();
                    Toast.makeText(getContext(), getString(R.string.txt_failedUploadImg),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
            uploadImgDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (isUploadImgSuccess[0]) {
                        mProgressDialog.show();
                        dbRef.updateChildren(childUpdate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(getContext(), getString(R.string.text_addloca_succ)
                                                , Toast.LENGTH_LONG).show();
                                        getActivity().finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mProgressDialog.dismiss();
                                Toast.makeText(getContext(), getString(R.string.text_failedaddloca)
                                        , Toast.LENGTH_LONG).show();

                            }
                        });

                    }
                }
            });
        } else {
            mProgressDialog.show();
            mProgressDialog.show();
            dbRef.updateChildren(childUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getContext(), getString(R.string.text_addloca_succ)
                            , Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getContext(), getString(R.string.text_failedaddloca)
                            , Toast.LENGTH_LONG).show();
                }
            });
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
        String close = new Times().getTimeNoSecond(),
                open = new Times().getTimeNoSecond();
        switch (edtID) {
            case R.id.frg_addloction_btn_opentime:
                if (hour > -1) {
                    open = hour + ":" + min;
                    btn_opentime.setText(open);
                }
                break;
            case R.id.frg_addloction_btn_closetime:
                if (hour > -1) {
                    close = hour + ":" + min;
                    btn_closetime.setText(close);
                }
                break;
        }
        opentime = open + " - " + close;
    }

    @Override
    public void onDetach() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(getResources()
                    .getColor(R.color.colorPrimaryDark));
        }
        super.onDetach();

    }

    @Override
    public void onLocationFinderStart() {

    }

    @Override
    public void onLocationFinderSuccess(final PlaceAttribute placeAttribute) {
        if (placeAttribute != null) {
            final PlaceAttribute myPlaceAttribute = placeAttribute;

            Log.i(LOG + ".onLocationFinder", placeAttribute.getState() + "-" + placeAttribute.getDistrict());
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Địa chỉ: " + placeAttribute.getFullname()).setTitle("Xác nhận")
                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            address=placeAttribute.getFullname();
                            lat=placeAttribute.getPlaceLatLng().latitude;
                            lng=placeAttribute.getPlaceLatLng().longitude;
                            district=placeAttribute.getDistrict();
                            province=placeAttribute.getState();
                            savestore();
                        }
                    })
                    .setNegativeButton("Thử lại", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {

            Toast.makeText(getActivity(), "Lỗi! Kiểm tra dữ liệu nhập vàp ", Toast.LENGTH_LONG).show();
        }
        pos=-1;
    }

    @Override
    public void onGeocodingFinderSuccess(String address) {

    }

}
