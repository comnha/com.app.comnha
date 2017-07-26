package com.app.ptt.comnha.Fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Foodselection_rcyler_adapter;
import com.app.ptt.comnha.Adapters.ImagesImportRvAdapter;
import com.app.ptt.comnha.Adapters.Storeselection_rcyler_adapter;
import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Classes.SelectedImage;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Image;
import com.app.ptt.comnha.Models.FireBase.NewpostNotify;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.SystemControl;
import com.app.ptt.comnha.Utils.AppUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class WritepostFragment extends Fragment implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener,
        RatingBar.OnRatingBarChangeListener {

    public static int MEDIASTORE_LOADED_ID = 0;
    Toolbar toolbar;
    EditText edt_content, edt_title;
    LinearLayout linear_more, linear_rate, linear_rate_dial, linear_pickfood_dial,
            linear_addimg_dial, linear_pickloca_dial,
            linear_location, linear_importimg, linear_banner, linear_foodrate;
    ImageView imgV_banner;
    TextView txtv_foodprice, txtv_foodname, txtv_rateComment;
    RatingBar rb_foodrating;
    CircularImageView imgv_foodimg;
    BottomSheetDialog moreDialog, rateDialog, imgsDialog,
            storeDialog, foodDialog;
    DatabaseReference dbRef;
    StorageReference stRef;
    ProgressDialog plzw8Dialog, uploadImgDialog;
    TextView txtV_price_dial, txtV_health_dial, txtV_service_dial;
    SeekBar sb_price, sb_health, sb_service;
    int progress_price = 0, progress_health = 0, progress_service = 0;
    RecyclerView imagesrv, storesrv, foodsrv;
    RecyclerView.LayoutManager imageslm, storeslm, foodslm;
    ContentResolver cr;
    ArrayList<SelectedImage> selectedImages;
    ImagesImportRvAdapter imagesImportRvAdapter;
    ArrayList<Store> stores;
    Storeselection_rcyler_adapter storesAdater;
    ArrayList<Food> foods;
    Foodselection_rcyler_adapter foodAdapter;
    TextView txtv_locaname, txtv_locaadd, txtv_banner, txtv_importimg,
            txtv_pricerate, txtv_healthrate, txtv_servicerate;
    int androidVer = Build.VERSION.SDK_INT;
    RelativeLayout relative_touchoutside;
    NestedScrollView nested_touchoutside;
    boolean isReadImg = false;
    TextInputLayout ilayout_title, ilayout_content;
    String title = "", content, banner = "";
    long pricerate = 0, healthrate = 0, servicerate = 0;
    Button btn_imgdial_select, btn_imgdial_close,
            btn_ratedial_select, btn_ratedial_cancel;
    String un = "", uID = "", avatar = "";
    User user = null;
    Store selected_store = null;
    Food selected_food = null;
    Post post;
    NewpostNotify newpostNotify = null;
    String dist_prov;
    ValueEventListener storeValueListener, foodValueListener,
            foodFromStoreValueListener;
    float foodRate = 0;
    UploadTask uploadTask;

    public WritepostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_writepost, container, false);
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Const.DATABASE_PATH);
        stRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Const.STORAGE_PATH);
        cr = getContext().getContentResolver();
        if (LoginSession.getInstance().getFirebUser() != null) {
            user = LoginSession.getInstance().getUser();
            un = LoginSession.getInstance().getUser().getUn();
            uID = LoginSession.getInstance().getUser().getuID();
            avatar = LoginSession.getInstance().getUser().getAvatar();
        } else {
            getActivity().finish();
        }

        ref(view);
        if (null != ChooseStore.getInstance().getStore()) {
            linear_location.setVisibility(View.VISIBLE);
            selected_store = ChooseStore.getInstance().getStore();
            dist_prov=selected_store.getPro_dist();
            ChooseStore.getInstance().setStore(null);
            txtv_locaadd.setText(selected_store.getAddress());
            txtv_locaname.setText(selected_store.getName());
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    private void getFoods() {
        foodValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    String key = dataItem.getKey();
                    Food food = dataItem.getValue(Food.class);
                    food.setFoodID(key);
                    foods.add(food);
                }
                foodAdapter.notifyDataSetChanged();
                plzw8Dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.food_CODE))
                .orderByChild("dist_prov")
                .equalTo(dist_prov)
                .addListenerForSingleValueEvent(foodValueListener);
    }

    private void getFoodsFromStore() {
        foodFromStoreValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    String key = dataItem.getKey();
                    Food food = dataItem.getValue(Food.class);
                    food.setFoodID(key);
                    foods.add(food);
                }
                foodAdapter.notifyDataSetChanged();
                plzw8Dialog.dismiss();
                dbRef.child(getString(R.string.food_CODE))
                        .orderByChild("storeID")
                        .equalTo(selected_store.getStoreID())
                        .removeEventListener(foodFromStoreValueListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.food_CODE))
                .orderByChild("storeID")
                .equalTo(selected_store.getStoreID())
                .addValueEventListener(foodFromStoreValueListener);
    }

    private void getStores(String storeID) {
        if (!storeID.equals("")) {
            plzw8Dialog.show();
            storeValueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.getKey();
                    Store store = dataSnapshot.getValue(Store.class);
                    store.setStoreID(key);
                    selected_store = store;

                    linear_location.setVisibility(View.VISIBLE);
                    AnimationUtils.fadeAnimation(linear_location, 300, true, 0);
                    txtv_locaadd.setText(selected_store.getAddress());
                    txtv_locaname.setText(selected_store.getName());
                    plzw8Dialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            dbRef.child(getString(R.string.store_CODE) + storeID)
                    .addListenerForSingleValueEvent(storeValueListener);
        } else {
            storeValueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                        String key = dataItem.getKey();
                        Store store = dataItem.getValue(Store.class);
                        store.setStoreID(key);
                        stores.add(store);
                    }
                    storesAdater.notifyDataSetChanged();
                    plzw8Dialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            dbRef.child(getString(R.string.store_CODE))
                    .orderByChild("isHidden_dis_pro")
                    .equalTo(String.valueOf(false) + "_" + dist_prov)
                    .addListenerForSingleValueEvent(storeValueListener);
        }
    }

    private void ref(View view) {
        linear_more = (LinearLayout) view.findViewById(R.id.linear_more_writepost);
        edt_content = (EditText) view.findViewById(R.id.edt_content_writepost);
        edt_title = (EditText) view.findViewById(R.id.edt_title_writepost);
        edt_title.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        edt_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                title = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    ilayout_title.setError(null);
                } else {
                    ilayout_title.setError(
                            getResources().getString(R.string.txt_notitle));
                }
            }
        });
        edt_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
        edt_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                content = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    ilayout_content.setError(null);
                } else {
                    ilayout_content.setError(
                            getResources().getString(R.string.txt_nocontent));
                }
            }
        });
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_writepost);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setBackgroundColor(getResources().getColor(R.color.admin_color_selection_news));
        getActivity().getWindow().clearFlags(Window.FEATURE_ACTION_BAR_OVERLAY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.admin_color_selection_news));
        }
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle(getString(R.string.text_toolbar_title_writepost));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        linear_more.setOnClickListener(this);
        setHasOptionsMenu(true);
        imgV_banner = (ImageView) view.findViewById(R.id.imgV_banner_writepost);
        moreDialog = new BottomSheetDialog(getContext());
        moreDialog.setContentView(R.layout.layout_writepost_more);


        linear_rate_dial = (LinearLayout) moreDialog.findViewById(R.id.linear_rate_more_writepost_dialog);
        linear_pickfood_dial = (LinearLayout) moreDialog.findViewById(R.id.linear_foodrating_more_writepost_dialog);
        linear_addimg_dial = (LinearLayout) moreDialog.findViewById(R.id.linear_addimg_more_writepost_dialog);
        linear_pickloca_dial = (LinearLayout) moreDialog.findViewById(R.id.linear_place_more_writepost_dialog);

        linear_rate_dial.setOnClickListener(this);
        linear_pickfood_dial.setOnClickListener(this);
        linear_addimg_dial.setOnClickListener(this);
        linear_pickloca_dial.setOnClickListener(this);

        plzw8Dialog = AppUtils.setupProgressDialog(getActivity(),
                getString(R.string.txt_plzwait), null, true, true, ProgressDialog.STYLE_SPINNER,
                0);
        rateDialog = new BottomSheetDialog(getContext());
        rateDialog.setContentView(R.layout.layout_writepost_rate);
        rateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                int x;
                int y = (linear_rate.getTop() + linear_rate.getBottom()) / 2;
                long duration = 300;
                if (healthrate > 0 && servicerate > 0 && pricerate > 0) {
                    Log.d("rateDialonDismiss", "select");
                    x = linear_rate.getLeft();
                    linear_rate.setVisibility(View.VISIBLE);
                    AnimationUtils.createOpenCR(linear_rate, duration, x, y);
                    txtv_pricerate.setText(pricerate + "");
                    txtv_servicerate.setText(servicerate + "");
                    txtv_healthrate.setText(healthrate + "");
                } else {
                    x = linear_rate.getRight();
                    AnimationUtils.createCloseCR(linear_rate, duration, x, y);
                    sb_price.setProgress(0);
                    sb_health.setProgress(0);
                    sb_service.setProgress(0);
                    Log.d("rateDialonDismiss", "cancel");

                }
            }
        });
        btn_ratedial_select = (Button) rateDialog.findViewById(R.id.btn_select_ratedialog);
        btn_ratedial_cancel = (Button) rateDialog.findViewById(R.id.btn_cancel_ratedialog);
        btn_ratedial_select.setOnClickListener(this);
        btn_ratedial_cancel.setOnClickListener(this);
        txtV_price_dial = (TextView) rateDialog.findViewById(R.id.txtV_price_ratedialog);
        txtV_health_dial = (TextView) rateDialog.findViewById(R.id.txtV_health_ratedialog);
        txtV_service_dial = (TextView) rateDialog.findViewById(R.id.txtV_service_ratedialog);
        txtV_price_dial.setText(getString(R.string.text_price) + ": 1");
        txtV_health_dial.setText(getString(R.string.text_healthyrate) + ": 1");
        txtV_service_dial.setText(getString(R.string.text_servicerate) + ": 1");
        sb_price = (SeekBar) rateDialog.findViewById(R.id.sb_price_ratedialog);
        sb_health = (SeekBar) rateDialog.findViewById(R.id.sb_health_ratedialog);
        sb_service = (SeekBar) rateDialog.findViewById(R.id.sb_service_ratedialog);
        sb_price.setOnSeekBarChangeListener(this);
        sb_health.setOnSeekBarChangeListener(this);
        sb_service.setOnSeekBarChangeListener(this);

        linear_location = (LinearLayout) view.findViewById(R.id.linear_store_writepost);
        linear_location.setVisibility(View.GONE);

        selectedImages = new ArrayList<>();
        imgsDialog = new BottomSheetDialog(getContext());
        imgsDialog.setContentView(R.layout.layout_writepost_imgimporting);
        imgsDialog.setCanceledOnTouchOutside(false);
        imgsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Log.d("onDismiss", "onDismiss");

                if (selectedImages.size() > 0) {
                    linear_importimg.setVisibility(View.VISIBLE);
                    linear_banner.setVisibility(View.VISIBLE);
                    txtv_importimg.setText(selectedImages.size() + " ảnh đính kèm");
                    if (selected_food == null) {
                        imgV_banner.setImageURI(selectedImages.get(0).getUri());
                        banner = selectedImages.get(0).getUri().getLastPathSegment();
                    }
                    imgV_banner.setScaleType(ImageView.ScaleType.FIT_XY);
                } else

                {
                    imagesImportRvAdapter.cancelSelection();
                    selectedImages.clear();
                    linear_importimg.setVisibility(View.GONE);
                    linear_banner.setVisibility(View.GONE);
                    imgV_banner.setImageURI(null);
                    banner = "";
                }
                Log.d("imgDialogonDismiss", "onDismiss");
            }
        });

        imagesrv = (RecyclerView) imgsDialog.findViewById(R.id.rv_images_imgimporting);
        imageslm = new

                GridLayoutManager(getContext(), 3,
                LinearLayoutManager.VERTICAL, false);
        imagesrv.setLayoutManager(imageslm);
        imagesImportRvAdapter = new

                ImagesImportRvAdapter(getContext(),

                getContext().

                        getContentResolver());
        imagesrv.setAdapter(imagesImportRvAdapter);

        btn_imgdial_select = (Button) imgsDialog.findViewById(R.id.btn_select_imgimporting);
        btn_imgdial_close = (Button) imgsDialog.findViewById(R.id.btn_close_imgimporting);
        btn_imgdial_select.setOnClickListener(this);
        btn_imgdial_close.setOnClickListener(this);
        txtv_locaname = (TextView) view.findViewById(R.id.txtV_storename_writepost);
        txtv_locaadd = (TextView) view.findViewById(R.id.txtV_storeaddress_writepost);
        txtv_pricerate = (TextView) view.findViewById(R.id.txtv_price_writepost);
        txtv_healthrate = (TextView) view.findViewById(R.id.txtv_health_writepost);
        txtv_servicerate = (TextView) view.findViewById(R.id.txtv_service_writepost);
        txtv_banner = (TextView) view.findViewById(R.id.txtV_banner_writepost);
        txtv_importimg = (TextView) view.findViewById(R.id.txtV_importimg_writepost);
        linear_importimg = (LinearLayout) view.findViewById(R.id.linear_importimg_writepost);
        linear_importimg.setVisibility(View.GONE);

        linear_banner = (LinearLayout) view.findViewById(R.id.linear_banner_writepost);
        linear_banner.setVisibility(View.GONE);

        linear_rate = (LinearLayout) view.findViewById(R.id.linear_rate_writepost);
        linear_rate.setVisibility(View.GONE);

        nested_touchoutside = (NestedScrollView) view.findViewById(R.id.nested_writepost);
        nested_touchoutside.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                SystemControl.hideSoftKeyboard(getActivity());
            }
        });
        ilayout_title = (TextInputLayout) view.findViewById(R.id.ilayout_title);
        ilayout_content = (TextInputLayout) view.findViewById(R.id.ilayout_content);

        storeDialog = new BottomSheetDialog(getContext());
        storeDialog.setContentView(R.layout.layout_writepost_storelist);
        storeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (selected_store != null) {
                    if (linear_location.getVisibility() != View.VISIBLE) {
                        linear_location.setVisibility(View.VISIBLE);
                        AnimationUtils.fadeAnimation(linear_location, 300, true, 0);
                    }
                    txtv_locaadd.setText(selected_store.getAddress());
                    txtv_locaname.setText(selected_store.getName());
                }
            }
        });
        storesrv = (RecyclerView) storeDialog.findViewById(R.id.rv_stores_storeselection);
        storeslm = new

                LinearLayoutManager(getContext(),

                LinearLayoutManager.VERTICAL, false);
        storesrv.setLayoutManager(storeslm);
        stores = new ArrayList<>();
        storesAdater = new

                Storeselection_rcyler_adapter(getActivity(), stores, stRef);
        storesrv.setAdapter(storesAdater);
        storesAdater.setOnItemClickLiestner(new Storeselection_rcyler_adapter
                .OnItemClickLiestner() {
            @Override
            public void onItemClick(Store store) {
                if (selected_store != null) {
                    if (!selected_store.getStoreID().equals(store.getStoreID())) {
                        linear_foodrate.setVisibility(View.GONE);
                        selected_food = null;
                        banner = "";
                        imgV_banner.setImageBitmap(null);
                        foods.clear();
                        foodAdapter.notifyDataSetChanged();
                    }
                } else {
                    selected_food = null;
                    banner = "";
                    imgV_banner.setImageBitmap(null);
                    linear_foodrate.setVisibility(View.GONE);
                    foods.clear();
                    foodAdapter.notifyDataSetChanged();
                }
                selected_store = store;
                storeDialog.dismiss();
            }
        });

        linear_foodrate = (LinearLayout) view.findViewById(R.id.linear_food_writepost);
        linear_foodrate.setVisibility(View.GONE);
        txtv_rateComment = (TextView) view.findViewById(R.id.txtv_rateComment_writepost);
        imgv_foodimg = (CircularImageView) view.findViewById(R.id.imgv_foodimg_writepost);
        txtv_foodprice = (TextView) view.findViewById(R.id.txtv_foodprice_writepost);
        txtv_foodname = (TextView) view.findViewById(R.id.txtv_foodname_writepost);
        rb_foodrating = (RatingBar) view.findViewById(R.id.rb_foodrating_writepost);
        rb_foodrating.setOnRatingBarChangeListener(this);

        foodDialog = new

                BottomSheetDialog(getContext());
        foodDialog.setContentView(R.layout.layout_writepost_foodlist);
        foodDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (selected_food != null) {
                    if (linear_foodrate.getVisibility() != View.VISIBLE) {
                        linear_foodrate.setVisibility(View.VISIBLE);
                    }
                    txtv_foodname.setText(selected_food.getName());
                    txtv_foodprice.setText(selected_food.getPrice() + "đ");
                    banner = selected_food.getFoodImg();
                    linear_banner.setVisibility(View.VISIBLE);
                    imgv_foodimg.setImageBitmap(selected_food.getImgBitmap());
                    imgV_banner.setImageBitmap(selected_food.getImgBitmap());
                }
            }
        });
        foodsrv = (RecyclerView) foodDialog.findViewById(R.id.rv_foods_foodselection);
        foodslm = new

                LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,
                false);
        foodsrv.setLayoutManager(foodslm);
        foods = new ArrayList<>();
        foodAdapter = new

                Foodselection_rcyler_adapter(getActivity(), foods, stRef);
        foodsrv.setAdapter(foodAdapter);
        foodAdapter.setOnItemClickLiestner(new Foodselection_rcyler_adapter
                .OnItemClickLiestner() {
            @Override
            public void onItemClick(Food food) {
//                if (stores.size() > 0) {
//                    for (Store storeItem : stores) {
//                        if (image.getStoreID().equals(storeItem.getStoreID())) {
//                            selected_store = storeItem;
//                        }
//                    }
//                } else {
//                    dbRef.child(getString(R.string.food_CODE))
//                    dbRef.child(getString(R.string.store_CODE)
//                            + image.getStoreID()).addValueEventListener();
//                }
                selected_food = food;
                if (selected_store == null) {
                    getStores(selected_food.getStoreID());
                }
                foodDialog.dismiss();
            }
        });
        uploadImgDialog = new

                ProgressDialog(getContext());
        uploadImgDialog.setMessage(

                getString(R.string.txt_updloadimg));
        uploadImgDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        uploadImgDialog.setProgress(0);
        uploadImgDialog.setCanceledOnTouchOutside(true);
        uploadImgDialog.setCancelable(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_writepost, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_writepost:
                if (AppUtils.checkEmptyEdt(edt_title)) {
                    edt_title.requestFocus();
                    ilayout_title.setError(getString(R.string.txt_notitle));
                } else if (AppUtils.checkEmptyEdt(edt_content)) {
                    edt_content.requestFocus();
                    ilayout_content.setError(getString(R.string.txt_nocontent));
                } else if (selected_store == null) {
                    Snackbar sbar_noselectstore = Snackbar.make(getView(),
                            getString(R.string.txt_nochoseloca),
                            Snackbar.LENGTH_INDEFINITE);
                    sbar_noselectstore.setAction(getString(R.string.txt_chooseloca), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (stores.size() == 0) {
                                plzw8Dialog.show();
                                getStores("");
                                plzw8Dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        storeDialog.show();
                                        plzw8Dialog.setOnDismissListener(null);
                                    }
                                });
                            } else {
                                storeDialog.show();
                            }
                        }
                    }).show();
                } else if (selected_food!=null && foodRate == 0) {
                    Toast.makeText(getActivity(),
                            getString(R.string.txt_noratefood),
                            Toast.LENGTH_SHORT).show();
                } else {
                    savePost();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_more_writepost:
                moreDialog.show();
                break;
            case R.id.linear_rate_more_writepost_dialog:
                moreDialog.dismiss();
                rateDialog.show();
                break;
            case R.id.linear_foodrating_more_writepost_dialog:
                moreDialog.dismiss();
                if (foods.size() == 0) {
                    plzw8Dialog.show();
                    if (selected_store != null) {
                        getFoodsFromStore();
                    } else {
                        getFoods();
                    }
                    plzw8Dialog.setOnDismissListener(new DialogInterface.
                            OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            foodDialog.show();
                            plzw8Dialog.setOnDismissListener(null);
                        }
                    });
                } else {
                    foodDialog.show();
                }
                break;
            case R.id.linear_addimg_more_writepost_dialog:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            // Explain to the user why we need to read the contacts
                        }

                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);
                        return;
                    }
                }
                if (!isReadImg) {
                    imagesImportRvAdapter.readthentranstoarray();
                    isReadImg = true;
                }
                imagesrv.scrollToPosition(0);
                imgsDialog.show();
                moreDialog.dismiss();
                break;
            case R.id.linear_place_more_writepost_dialog:
                moreDialog.dismiss();
                if (stores.size() == 0) {
                    plzw8Dialog.show();
                    getStores("");
                    plzw8Dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            storeDialog.show();
                            plzw8Dialog.setOnDismissListener(null);
                        }
                    });
                } else {
                    storeDialog.show();
                }
                break;
            case R.id.btn_close_imgimporting:
                imgsDialog.dismiss();
                break;
            case R.id.btn_select_imgimporting:
                imgsDialog.dismiss();
                selectedImages = imagesImportRvAdapter.getSelectedImgs();
                break;
            case R.id.btn_select_ratedialog:
                healthrate = sb_health.getProgress() + 1;
                pricerate = sb_price.getProgress() + 1;
                servicerate = sb_service.getProgress() + 1;
                rateDialog.dismiss();
                break;
            case R.id.btn_cancel_ratedialog:
                healthrate = 0;
                servicerate = 0;
                pricerate = 0;
                rateDialog.cancel();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.sb_price_ratedialog:
                progress_price = i + 1;
                txtV_price_dial.setText(getString(R.string.text_price) + ": " + progress_price);
                break;
            case R.id.sb_health_ratedialog:
                progress_health = i + 1;
                txtV_health_dial.setText(getString(R.string.text_healthyrate) + ": " + progress_health);
                break;
            case R.id.sb_service_ratedialog:
                progress_service = i + 1;
                txtV_service_dial.setText(getString(R.string.text_servicerate) + ": " + progress_service);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d("onStartTrackingTouch", seekBar.getProgress() + "");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void savePost() {
        String pro_dist = selected_store.getPro_dist(),
                storename = selected_store.getName(),
                storeID = selected_store.getStoreID(),
                foodID = "";
        if (selected_food != null) {
            foodID = selected_food.getFoodID();
        }
        final Map<String, Object> childUpdate = new HashMap<>();
        post = new Post(title, content, un, uID, storeID, storename,
                foodID, (long) foodRate, banner, pricerate, healthrate, servicerate,
                pro_dist);
        String postKey = dbRef.child(getString(R.string.store_CODE))
                .push().getKey();
        if(LoginSession.getInstance().getUser().getRole()>0){
            post.setHidden(false);
            post.setPostType(2);
        }else{
            post.setHidden(true);
            post.setPostType(0);
            newpostNotify = new NewpostNotify(postKey, title, uID, un,
                    dist_prov);
            String notiKey = dbRef.child(getString(R.string.notify_newpost_CODE))
                    .push().getKey();
            Map<String, Object> notifyValue = newpostNotify.toMap();
            childUpdate.put(getString(R.string.notify_newpost_CODE) + notiKey, notifyValue);
        }



        if (healthrate > 0 && servicerate > 0 && pricerate > 0) {
            if(LoginSession.getInstance().getUser().getRole()>0) {
                long priceSum = selected_store.getPriceSum() + pricerate,
                        healthSum = selected_store.getHealthySum() + healthrate,
                        serviceSum = selected_store.getServiceSum() + servicerate,
                        sum = selected_store.getSize() + 1;
                selected_store.setPriceSum(priceSum);
                selected_store.setHealthySum(healthSum);
                selected_store.setServiceSum(serviceSum);
                selected_store.setSize(sum);


                Map<String, Object> storeValue = selected_store.toMap();
                childUpdate.put(getString(R.string.store_CODE) + storeID,
                        storeValue);
            }
            post.setPriceRate(pricerate);
            post.setServiceRate(servicerate);
            post.setHealthyRate(healthrate);
        }
        if (selected_food != null
                && selected_food.getStoreID()
                .equals(selected_store.getStoreID())) {
            if(LoginSession.getInstance().getUser().getRole()>0) {
            long total = selected_food.getTotal() + 1;
            long rat = selected_food.getRating() + (long) foodRate;
            selected_food.setRating(rat);
            selected_food.setTotal(total);
            Map<String, Object> foodValue = selected_food.toMap();
            childUpdate.put(getString(R.string.food_CODE)
                    + selected_food.getFoodID(), foodValue);
            }
                post.setFoodRate((long)foodRate);
        }
        if (selectedImages.size() > 0) {
            uploadImgDialog.setMax(selectedImages.size());
            for (SelectedImage imgItem : selectedImages) {
                Image image;
                if (selectedImages.indexOf(imgItem) == 0) {
                    image = new Image(imgItem.getUri().getLastPathSegment(),
                            uID, 1, postKey, storeID, "");

                } else {
                    image = new Image(imgItem.getUri().getLastPathSegment(),
                            uID, 3, postKey, storeID, "");
                }
                Map<String, Object> imgValue = image.toMap();
                String imgKey = dbRef.child(getString(R.string.images_CODE))
                        .push().getKey();
                childUpdate.put(getString(R.string.images_CODE)
                        + imgKey, imgValue);
            }
        }
        Map<String, Object> postValue = post.toMap();
        childUpdate.put(getString(R.string.posts_CODE) + postKey,
                postValue);
        if (selectedImages.size() > 0) {
            uploadImgDialog.show();
            for (SelectedImage imgItem : selectedImages) {
                StorageReference mStorageReference = stRef.child(
                        imgItem.getUri().getLastPathSegment());
                uploadTask = mStorageReference.putFile(
                        Uri.fromFile(new File(imgItem.getUri().toString())));
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadImgDialog.setProgress(uploadImgDialog.getProgress() + 1);
                        if (uploadImgDialog.getProgress() == selectedImages.size()) {
                            uploadImgDialog.dismiss();
                            plzw8Dialog.show();
                            dbRef.updateChildren(childUpdate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            plzw8Dialog.dismiss();
                                            getActivity().finish();
                                            if (LoginSession.getInstance().getUser().getRole() > 0) {
                                                Toast.makeText(getContext(), getString(R.string.text_addpost_succ)
                                                        , Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getContext(), getString(R.string.text_addpost_succ_user)
                                                        , Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), getString(R.string.text_failedaddpost)
                                            , Toast.LENGTH_LONG).show();
                                    plzw8Dialog.dismiss();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        uploadImgDialog.dismiss();
                        Toast.makeText(getContext(), getString(R.string.text_failedaddpost)
                                , Toast.LENGTH_LONG).show();
                    }
                });
            }
        } else {
            plzw8Dialog.show();
            dbRef.updateChildren(childUpdate)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            plzw8Dialog.dismiss();
                            if (LoginSession.getInstance().getUser().getRole() > 0) {
                                Toast.makeText(getContext(), getString(R.string.text_addpost_succ)
                                        , Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), getString(R.string.text_addpost_succ_user)
                                        , Toast.LENGTH_LONG).show();

                            }
                            getActivity().finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), getString(R.string.text_failedaddpost), Toast.LENGTH_SHORT).show();
                    plzw8Dialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
        switch ((int) v) {
            case 0:
                ratingBar.setRating(1);
                txtv_rateComment.setText(getString(R.string.txt_toofuckingbad));
                break;
            case 1:
                txtv_rateComment.setText(getString(R.string.txt_toofuckingbad));
                break;
            case 2:
                txtv_rateComment.setText(getString(R.string.txt_bad));
                break;
            case 3:
                txtv_rateComment.setText(getString(R.string.txt_fine));
                break;
            case 4:
                txtv_rateComment.setText(getString(R.string.txt_good));
                break;
            case 5:
                txtv_rateComment.setText(getString(R.string.txt_toofuckinggood));
                break;
        }
        foodRate = v;
    }
}

