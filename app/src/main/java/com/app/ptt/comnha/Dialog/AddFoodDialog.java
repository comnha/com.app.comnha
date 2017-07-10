package com.app.ptt.comnha.Dialog;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.SingleImageImportRvAdapter;
import com.app.ptt.comnha.Classes.SelectedImage;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Models.FireBase.UserNotification;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.AppUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFoodDialog extends DialogFragment implements View.OnClickListener {
    private static final String LOG = AddFoodDialog.class.getSimpleName();
    EditText edt_name, edt_price, edt_comment;
    DatabaseReference dbRef;
    StorageReference stRef;
    private ProgressDialog mProgressDialog;
    private String locaID;
    Button btn_save, btn_cancel;
    FragmentManager dialogFm;
    boolean isConnected = true;
    ImageView imgv_photo;
    Map<String, Object> childUpdates;
    IntentFilter mIntentFilter;
    TextInputLayout ilayout_name, ilayout_commnent, ilayout_price;
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    String name = "", comment = "", avatarname = "",
            price = "", key = "";
    Food newFood;
    private Store store;

    SingleImageImportRvAdapter singleImageImportRvAdapter;
    private RecyclerView imagesrv;
    private GridLayoutManager imageslm;
    private SelectedImage selectedImage;
    private BottomSheetDialog imgsDialog;
    UploadTask uploadTask;
    boolean isEditFood = false;
    Food oldfood;

    public void setStore(Store store) {
        this.store = store;
    }

    public void setEditFoood(boolean isEditFood, Food oldfood) {
        this.isEditFood = isEditFood;
        this.oldfood = oldfood;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastSendAddress)) {
                Log.i(LOG + ".onReceive form Service", "isConnected= "
                        + intent.getBooleanExtra("isConnected", false));
                isConnected = intent.getBooleanExtra("isConnected", false);
            }
        }
    };

    public AddFoodDialog() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        isConnected = MyService.returnIsNetworkConnected();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Const.DATABASE_PATH);
        stRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Const.STORAGE_PATH);
        View view = inflater.inflate(R.layout.fragment_addfood, container, false);
        init(view);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getString(R.string.text_addfood));
        return dialog;
    }

    private void init(View view) {
        dialogFm = getActivity().getSupportFragmentManager();
        edt_price = (EditText) view.findViewById(R.id.edt_price_addfood);
        edt_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().equals("")) {
                    price = charSequence.toString();
                } else {
                    price = "";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    ilayout_price.setError(null);
                } else {
                    ilayout_price.setError(
                            getResources().getString(R.string.txt_nofoodprice));
                }
            }
        });
        edt_name = (EditText) view.findViewById(R.id.edt_foodname_addfood);
        edt_name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        edt_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    ilayout_name.setError(null);
                } else {
                    ilayout_name.setError(
                            getResources().getString(R.string.txt_nofoodname));
                }
            }
        });
        edt_comment = (EditText) view.findViewById(R.id.edt_comment_addfood);
        edt_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                comment = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    ilayout_commnent.setError(null);
                } else {
                    ilayout_commnent.setError(
                            getResources().getString(R.string.txt_nodescribefood));
                }
            }
        });
        edt_comment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
        btn_save = (Button) view.findViewById(R.id.btn_save_addfood);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel_addfood);
        btn_save.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        ilayout_commnent = (TextInputLayout) view.findViewById(R.id.ilayout_comment_addfood);
        ilayout_name = (TextInputLayout) view.findViewById(R.id.ilayout_name_addfood);
        ilayout_price = (TextInputLayout) view.findViewById(R.id.ilayout_price_addfood);
        ilayout_name.setCounterMaxLength(50);
        ilayout_commnent.setCounterMaxLength(200);
        imgv_photo = (ImageView) view.findViewById(R.id.imgv_photo_addfood);
        imgv_photo.setOnClickListener(this);
        imgv_photo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                imgv_photo.setImageURI(null);
                imgv_photo.setImageResource(
                        R.drawable.ic_add_to_photos_50black_24dp);
                selectedImage = null;
                imgv_photo.setScaleType(ImageView.ScaleType.CENTER);
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
                    avatarname = selectedImage.getUri().getLastPathSegment();
                    imgv_photo.setImageURI(selectedImage.getUri());
                    imgv_photo.setScaleType(ImageView.ScaleType.FIT_XY);
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
        mProgressDialog.setMessage(getResources().getString(R.string.txt_addinmon));
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(true);
        if (isEditFood) {
            if (oldfood.getImgBitmap() == null) {
                StorageReference imgRef = stRef.child(oldfood.getFoodImg());
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getContext())
                                .load(uri)
                                .into(imgv_photo);
                    }
                });
            } else {
                imgv_photo.setImageBitmap(oldfood.getImgBitmap());
            }
            edt_name.setText(oldfood.getName());
            name = oldfood.getName();
            edt_price.setText(oldfood.getPrice() + "");
            price = String.valueOf(oldfood.getPrice());
            edt_comment.setText(oldfood.getComment());
            comment = oldfood.getComment();
            avatarname = oldfood.getFoodImg();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_addfood:
                if (AppUtils.checkEmptyEdt(edt_name)) {
                    edt_name.requestFocus();
                } else if (AppUtils.checkEmptyEdt(edt_price)) {
                    edt_price.requestFocus();
                } else if (AppUtils.checkEmptyEdt(edt_comment)) {
                    edt_comment.requestFocus();
                } else if (!isEditFood) {
                    if (selectedImage == null) {
                        Toast.makeText(getContext(), getString(R.string.txt_nophoto),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        saveFood();
                    }
                } else {
                    updateFood();
                }
                break;
            case R.id.btn_cancel_addfood:
                getDialog().cancel();
                break;
            case R.id.imgv_photo_addfood:
                String[] permissons=new String[1];
                permissons[0]= Const.mListPermissions[0];
                List<String> results=AppUtils.checkPermissions(getActivity(),permissons);
                if (results.size()>0){
                    AppUtils.requestPermission(getActivity(),results,Const.PERMISSION_LOCATION_FLAG);
                } else {
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

    private void updateFood() {
        mProgressDialog = AppUtils.setupProgressDialog(getContext(),
                getString(R.string.txt_updatinfood), null, false, false,
                ProgressDialog.STYLE_SPINNER, 0);
        mProgressDialog.show();
        Food childfood = oldfood;
        childfood.setName(name);
        childfood.setPrice(Long.parseLong(price));
        childfood.setComment(comment);
        childfood.setFoodImg(avatarname);
        Map<String, Object> foodValue = childfood.toMap();
        final Map<String, Object> childUpdate = new HashMap<>();
        childUpdate.put(getString(R.string.food_CODE) + childfood.getFoodID(), foodValue);
        if (!avatarname.equals(oldfood.getFoodImg())) {
            StorageReference imgRef = stRef.child(avatarname);
            uploadTask = imgRef.putFile(
                    Uri.fromFile(new File(selectedImage.getUri().toString())));
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dbRef.updateChildren(childUpdate).addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProgressDialog.dismiss();
                                    getDialog().dismiss();
                                    Toast.makeText(getContext(),
                                            getString(R.string.text_updatefood_succ),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressDialog.cancel();
                            Toast.makeText(getContext(),
                                    getString(R.string.text_updatefood_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.cancel();
                    Toast.makeText(getContext(), getString(R.string.txt_failedUploadImg)
                            , Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            dbRef.updateChildren(childUpdate).addOnSuccessListener(
                    new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mProgressDialog.dismiss();
                            getDialog().dismiss();
                            Toast.makeText(getContext(),
                                    getString(R.string.text_updatefood_succ),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.cancel();
                    Toast.makeText(getContext(),
                            getString(R.string.text_updatefood_failed),
                            Toast.LENGTH_SHORT).show();
                }
            });
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

    private void saveFood() {
        mProgressDialog.show();
        newFood = new Food(name, comment, Long.valueOf(price),
                0, LoginSession.getInstance().getFirebUser().getUid(),
                store.getStoreID(), store.getDistrict(), store.getProvince(),
                avatarname);
        key = dbRef.child(getResources().getString(R.string.food_CODE)).push().getKey();
        Map<String, Object> foodvalue = newFood.toMap();
        childUpdates = new HashMap<>();
        childUpdates.put(
                getResources().getString(R.string.food_CODE)
                        + key, foodvalue);
            for(String mUerId: store.getUsersFollow()){
                UserNotification userNotification=new UserNotification();
                userNotification.setUserEffectId(LoginSession.getInstance().getUser().getuID());
                userNotification.setUserEffectName(LoginSession.getInstance().getUser().getUn());
                userNotification.setFoodId(key);
                userNotification.setStoreID(store.getStoreID());
                userNotification.setFoodName(name);
                userNotification.setƠwnPost(false);
                userNotification.setType(4);
                Map<String,Object> userNotificationMap=userNotification.toMap();
                String key =dbRef.child(getString(R.string.user_notification_CODE)+mUerId).push().getKey();
                childUpdates.put(getString(R.string.user_notification_CODE)+mUerId+"/"+key,userNotificationMap);
            }
        if(!LoginSession.getInstance().getUser().getuID().toLowerCase().equals(store.getUserID().toLowerCase())){
            UserNotification userNotification=new UserNotification();
            userNotification.setUserEffectId(LoginSession.getInstance().getUser().getuID());
            userNotification.setUserEffectName(LoginSession.getInstance().getUser().getUn());
            userNotification.setFoodId(key);
            userNotification.setFoodName(name);
            userNotification.setStoreID(store.getStoreID());
            userNotification.setƠwnPost(true);
            userNotification.setType(4);
            Map<String,Object> userNotificationMap=userNotification.toMap();
            String key =dbRef.child(getString(R.string.user_notification_CODE)+store.getUserID()).push().getKey();
            childUpdates.put(getString(R.string.user_notification_CODE)+store.getUserID()+"/"+key,userNotificationMap);
        }



        StorageReference imgRef = stRef.child(avatarname);
        uploadTask = imgRef.putFile(
                Uri.fromFile(new File(selectedImage.getUri().toString())));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dbRef.updateChildren(childUpdates).addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mProgressDialog.dismiss();
                                getDialog().dismiss();
                                Toast.makeText(getContext(),
                                        getString(R.string.text_addfood_succ),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.cancel();
                        Toast.makeText(getContext(),
                                getString(R.string.text_addfood_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressDialog.cancel();
                Toast.makeText(getContext(), getString(R.string.txt_failedUploadImg)
                        , Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
        isConnected = MyService.returnIsNetworkConnected();
        if (!isConnected) {
            Toast.makeText(getContext(), "Offline mode", Toast.LENGTH_SHORT).show();
        }
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
        getContext().registerReceiver(broadcastReceiver, mIntentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(broadcastReceiver);
        ChooseStore.getInstance().setStore(null);
    }


}
