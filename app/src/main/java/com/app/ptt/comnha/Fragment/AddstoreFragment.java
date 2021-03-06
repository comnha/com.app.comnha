package com.app.ptt.comnha.Fragment;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.PlacesAutoCompleteAdapter;
import com.app.ptt.comnha.Adapters.SingleImageImportRvAdapter;
import com.app.ptt.comnha.Classes.SelectedImage;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Image;
import com.app.ptt.comnha.Models.FireBase.NewstoreNotify;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Modules.LocationFinderListener;
import com.app.ptt.comnha.Modules.PlaceAPI;
import com.app.ptt.comnha.Modules.PlaceAttribute;
import com.app.ptt.comnha.Modules.Times;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.AppUtils;
import com.app.ptt.comnha.Utils.MyTool;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddstoreFragment extends Fragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener,
        DialogInterface.OnDismissListener, DialogInterface.OnCancelListener, LocationFinderListener
{
    public static final String LOG = AddstoreFragment.class.getSimpleName();
    boolean isEdit;
    Map<String, Object> childUpdate;
    EditText edt_storeName, edt_phoneNumb ;
    List<String> addressList;
    List<Post> posts;
    boolean isChangeLocation;
    List<Food> foods;
    AutoCompleteTextView edt_address;
    Button btn_opentime, btn_closetime;
    ProgressDialog mProgressDialog, uploadImgDialog;
    DatabaseReference dbRef;
    StorageReference stRef;
    UploadTask uploadTask;
    Calendar now;
    TimePickerDialog tpd;
    int edtID, pos = -1;
    int hour, min;
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
    String storename, address, phonenumb,opentime,
            province, district , storeimg = "",storeId;
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
        isConnected = MyService.isNetworkAvailable(getActivity());
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
        isConnected = MyService.isNetworkAvailable(getActivity());
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
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Const.DATABASE_PATH);
        stRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Const.STORAGE_PATH);
        if(store!=null){
            editStore(store);
        }
        return view;
    }
    public void setEditStore(Store store){
            this.store=store;
    }

    private void setupAutoCompleteTextView(){
        myTool=new MyTool(getActivity());
        addressList=new ArrayList<>();
        edt_address.setAdapter(new PlacesAutoCompleteAdapter(getContext(), R.layout.autocomplete_list_item,myTool));
        edt_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
            }
        });
    }
    private void editStore(Store store){
        posts=new ArrayList<>();
        foods=new ArrayList<>();
        getFoodList(store);
        getPostList(store);
        isEdit=true;
        storeId=store.getStoreID();
        edt_storeName.setText(store.getName());
        edt_address.setText(store.getAddress());
        edt_phoneNumb.setText(store.getPhonenumb());
        if (store.getImgBitmap() == null) {
            if (!store.getStoreimg().equals("")) {
                StorageReference imageRef = stRef.child(store.getStoreimg());
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getActivity())
                                .load(uri)
                                .into(imgv_avatar);
                    }
                });

            } else {
                imgv_avatar.setImageResource(R.drawable.ic_item_store);
            }
        } else {
            imgv_avatar.setImageBitmap(store.getImgBitmap());
        }
        String[] list=store.getOpentime().split("-");
        btn_opentime.setText(list[0].toString().trim());
        btn_closetime.setText(list[1].toString().trim());
        storeimg=store.getStoreimg();
        phonenumb=store.getPhonenumb();
        address=store.getAddress();
        storename=store.getName();
        opentime=store.getOpentime();
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
        uploadImgDialog.setCancelable(false);
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
                    if(isEdit) {
                        if (checkChangeAddress(store)) {
                            new PlaceAPI(edt_address.getText().toString(), this);
                        } else {
                            address = store.getAddress();
                            lat = store.getLat();
                            lng = store.getLng();
                            district = store.getDistrict();
                            province = store.getProvince();
                            savestore();
                        }
                    }else {
                        new PlaceAPI(edt_address.getText().toString(), this);
                    }
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
    private void getPostList(Store store) {
        ValueEventListener postValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    Post post = dataItem.getValue(Post.class);
                    String key = dataItem.getKey();
                    post.setPostID(key);
                    posts.add(post);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.posts_CODE))
                .orderByChild("isHidden_storeID")
                .equalTo(false + "_" + store.getStoreID())
                .addListenerForSingleValueEvent(postValueListener);
    }
    private void getFoodList(Store store) {

        ValueEventListener foodValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    Food food = dataItem.getValue(Food.class);
                    String key = dataItem.getKey();
                    food.setFoodID(key);
                    foods.add(food);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
            dbRef.child(getString(R.string.food_CODE))
                    .orderByChild("isHidden_storeID")
                    .equalTo(false + "_" + store.getStoreID())
                    .addListenerForSingleValueEvent(foodValueListener);
    }
    private boolean checkChangeAddress(Store store){
      if(!store.getAddress().trim().toLowerCase().equals(edt_address.getText().toString().trim().toLowerCase())){
          return true;
      }
      return false;
    }
    private void savestore() {
        childUpdate = new HashMap<>();
        final boolean[] isUploadImgSuccess = {false};
        String key,notifyKey;
        store = new Store(storename, address, phonenumb, opentime,
                province, district, lat, lng, userID, storeimg);
        if(!isEdit) {
            key = dbRef.child(getString(R.string.store_CODE)).push().getKey();
            notifyKey = dbRef.child(getString(R.string.notify_newstore_CODE))
                    .push().getKey();
            if (LoginSession.getInstance().getUser().getRole() > 0) {
                store.setStoreType(2);
                store.setHidden(false);
            } else {
                store.setStoreType(0);
                store.setHidden(true);
                notify = new NewstoreNotify(key, storename, address,
                        userID, un, district, province);
                Map<String, Object> notifyValues = notify.toMap();
                childUpdate.put(getString(R.string.notify_newstore_CODE) + notifyKey,
                        notifyValues);
            }
        }else{
            key=storeId;
            if(isChangeLocation){
                childUpdate=changeFoodStatus(childUpdate,district+"_"+province);
                childUpdate=changePostStatus(childUpdate,district+"_"+province);
            }
        }



        Map<String, Object> storeValues = store.toMap();


        childUpdate.put(getString(R.string.store_CODE) + key, storeValues);
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
                                        //Comunication.sendLocationListener.notice();
                                        if(!isEdit){
                                            if(LoginSession.getInstance().getUser().getRole()>0) {
                                                Toast.makeText(getContext(), getString(R.string.text_addloca_succ)
                                                        , Toast.LENGTH_LONG).show();
                                            }else{
                                                Toast.makeText(getContext(), getString(R.string.  text_addloca_succ_user)
                                                        , Toast.LENGTH_LONG).show();

                                            }
                                        }else{
                                            Toast.makeText(getContext(), getString(R.string.text_okededitloca)
                                                    , Toast.LENGTH_LONG).show();
                                        }

                                        getActivity().finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mProgressDialog.dismiss();
                                if(!isEdit) {
                                    Toast.makeText(getContext(), getString(R.string.text_failedaddloca)
                                            , Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getContext(), getString(R.string.text_failededitloca)
                                            , Toast.LENGTH_LONG).show();
                                }

                            }
                        });

                    }
                }
            });
        } else {
            mProgressDialog.show();
            dbRef.updateChildren(childUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mProgressDialog.dismiss();
                    //Comunication.sendLocationListener.notice();
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

    public  Map<String, Object>  changePostStatus(Map<String, Object> childUpdate, String huyen_tinh){
        for (Post post:posts){
                post.setDist_pro(huyen_tinh);
                Map<String, Object> map = post.toMap();
                childUpdate.put(getString(R.string.posts_CODE) + post.getPostID(), map);
        }
        return childUpdate;
    }
    public  Map<String, Object>  changeFoodStatus(Map<String, Object> childUpdate, String huyen_tinh){
        for (Food food:foods){
                food.setDist_prov(huyen_tinh);
                Map<String, Object> map = food.toMap();
            childUpdate.put(getString(R.string.food_CODE) + food.getFoodID(), map);
        }
        return childUpdate;
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
                            if(isEdit){
                                isChangeLocation=true;
                            }
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
