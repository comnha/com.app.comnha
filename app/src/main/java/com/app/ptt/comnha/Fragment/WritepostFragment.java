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

    public WritepostFragment() {
        // Required empty public constructor
    }

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
    public static int MEDIASTORE_LOADED_ID = 0;
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
    String dist_prov = "Quận 9_Hồ Chí Minh";
    ValueEventListener storeValueListener, foodValueListener,
            foodFromStoreValueListener;
    float foodRate = 0;
    UploadTask uploadTask;

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
        if(null!=ChooseStore.getInstance().getStore()){
            linear_location.setVisibility(View.VISIBLE);
            selected_store=ChooseStore.getInstance().getStore();
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
        post = new Post(title, content, un, uID, storeID, storename,
                foodID, (long) foodRate, banner, pricerate, healthrate, servicerate,
                pro_dist);
        String postKey = dbRef.child(getString(R.string.store_CODE))
                .push().getKey();
        Map<String, Object> postValue = post.toMap();
        final Map<String, Object> childUpdate = new HashMap<>();
        childUpdate.put(getString(R.string.posts_CODE) + postKey,
                postValue);
        if (healthrate > 0 && servicerate > 0 && pricerate > 0) {
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
        if (selected_food != null
                && selected_food.getStoreID()
                .equals(selected_store.getStoreID())) {
            long total = selected_food.getTotal() + 1;
            long rat = selected_food.getRating() + (long) foodRate;
            selected_food.setRating(rat);
            selected_food.setTotal(total);
            Map<String, Object> foodValue = selected_food.toMap();
            childUpdate.put(getString(R.string.food_CODE)
                    + selected_food.getFoodID(), foodValue);
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
        newpostNotify = new NewpostNotify(postKey, title, uID, un,
                dist_prov);
        String notiKey = dbRef.child(getString(R.string.notify_newpost_CODE))
                .push().getKey();
        Map<String, Object> notifyValue = newpostNotify.toMap();
        childUpdate.put(getString(R.string.notify_newpost_CODE) + notiKey, notifyValue);
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
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(),
                                            e.getMessage(), Toast.LENGTH_LONG).show();
                                    plzw8Dialog.dismiss();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        uploadImgDialog.dismiss();
                        Toast.makeText(getContext(), e.getMessage(),
                                Toast.LENGTH_SHORT)
                                .show();
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
                            getActivity().finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),
                            e.getMessage(), Toast.LENGTH_LONG).show();
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
    //    private static final String LOG = AddpostFragment.class.getSimpleName();
//    Button btn_save, btn_mainImg, btnAddImg;
//    CheckBox cb_monAn, cb_quanAn;
//    boolean checloca = false;
//    boolean checkrate = false;
//    RatingBar rb_danhGiaMon;
//    Food mFood = new Food();
//    float mRating = 0;
//    ArrayList<String> ListID;
//    String key;
//    ArrayList<Uri> uris;
//    String locaID;
//    DoInBackGroundOK doInBackGroundOK;
//    String fileKey;
//    MyLocation updateLoca;
//    File anh_dai_dien;
//    UploadTask uploadTask = null;
//    ArrayList<File> myFile;
//    StorageReference stRef;
//    LinearLayout ll_danhGiaQuan;
//    DiscreteSeekBar mSeekBarGia, mSeekBarVS, mSeekBarPV;
//    TextView txt_price, txt_vs, txt_pv;
//    Long gia = (long) 1, vs = (long) 1, pv = (long) 1;
//    ImageView img, img_Daidien;
//    Post newPost;
//    Map<String, Object> postValue;
//    ArrayList<String> url;
//    ArrayList<Image> upLoadImages;
//    Image newImage;
//    Map<String, Object> childUpdates;
//    int pc_Success = 0;
//    TextView txt_name, txt_address, frg_filter_txtmon;
//    EditText edt_title, edt_content;
//    boolean mainImg = false;
//    ProgressDialog plzw8Dialog;
//    FragmentManager fm;

//
//    DatabaseReference dbRef;
//    boolean isConnected = true;
//    IntentFilter mIntentFilter;
//    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
//    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(mBroadcastSendAddress)) {
//                Log.i(LOG + ".onReceive form Service", "isConnected= " + intent.getBooleanExtra("isConnected", false));
//                if (intent.getBooleanExtra("isConnected", false)) {
//                    isConnected = true;
//                } else
//                    isConnected = false;
//            }
//            if (intent.getIntExtra("stt", 0) == -1) {
//
//                upload();
//            }
//            if (intent.getStringExtra("uriImg") != null) {
//                url.add(intent.getStringExtra("uriImg"));
//                int pos = intent.getIntExtra("pos", 0);
//                Log.i("POSSTTTTTTTTTTTTT", pos + "-----");
//                upLoadImages.get(pos).setImage(intent.getStringExtra("uriImg"));
//                Map<String, Object> image = upLoadImages.get(pos).toMap();
//                childUpdates.put(getResources().getString(R.string.images_CODE)
//                        + ListID.get(pos), image);
//                if (pos == (myFile.size() - 1)) {
//                    pc_Success++;
//                    if (mainImg && anh_dai_dien != null) {
//                        newPost.setHinh(intent.getStringExtra("uriImg"));
//                    }
//                    postValue = newPost.toMap();
//                    childUpdates.put(
//                            getResources().getString(R.string.posts_CODE) + key, postValue);
//                    pc_Success++;
//                    Log.i("SSSSSSS", "pc_Success_4=" + pc_Success);
//                    if (pc_Success == 4)
//                        upload();
//
//                }
//            } else {
//                pc_Success = 0;
//                plzw8Dialog.dismiss();
//                Toast.makeText(getActivity(), "Xảy ra lỗi. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
//
//            }
//        }
//    };
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        getContext().unregisterReceiver(broadcastReceiver);
//    }

//    void ref(View view) {
//        fm = getActivity().getSupportFragmentManager();
//        frg_filter_txtmon = (TextView) view.findViewById(frg_filter_txtmon);
//
//        frg_filter_txtmon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onCustomClick(View v) {
//                PickFoodDialogFragment pickFoodFrg = new PickFoodDialogFragment();
//                pickFoodFrg.show(fm, "fragment_pickFood");
//                Log.i("CCS", DoPost.getInstance().getMyLocation().getStoreID() + "-------------------------");
//                pickFoodFrg.setStoreID(DoPost.getInstance().getMyLocation().getStoreID());
//                pickFoodFrg.setOnPickFoodListener(new PickFoodDialogFragment.OnPickFoodListener() {
//                    @Override
//                    public void onPickFood(Food selected_food) {
//                        frg_filter_txtmon.setText(selected_food.getName());
//                        mFood = selected_food;
//                        rb_danhGiaMon.setRating(selected_food.getRating());
//                    }
//                });
//            }
//        });
//        rb_danhGiaMon = (RatingBar) view.findViewById(rb_danhGiaMon);
//        ll_danhGiaQuan = (LinearLayout) view.findViewById(ll_danhGiaQuan);
//        ll_danhGiaQuan.setVisibility(View.INVISIBLE);
//        img_Daidien = (ImageView) view.findViewById(R.id.img_daiDien);
//        img = (ImageView) view.findViewById(R.id.frg_post_img);
//        btn_mainImg = (Button) view.findViewById(R.id.btn_chooseMainImg);
//        btnAddImg = (Button) view.findViewById(R.id.btn_addPhoto);
//        cb_monAn = (CheckBox) view.findViewById(R.id.cb_danhGiaMon);
//        cb_quanAn = (CheckBox) view.findViewById(R.id.cb_danhGiaQuan);
//        txt_name = (TextView) view.findViewById(R.id.frg_post_name);
//        txt_address = (TextView) view.findViewById(R.id.frg_post_address);
//        btn_save = (Button) view.findViewById(R.id.btn_save);
//        txt_price = (TextView) view.findViewById(R.id.frg_vote_txt_gia);
//        txt_vs = (TextView) view.findViewById(R.id.frg_vote_txt_vs);
//        txt_pv = (TextView) view.findViewById(R.id.frg_vote_txt_pv);
//        mSeekBarGia = (DiscreteSeekBar) view.findViewById(R.id.frg_vote_slide_gia);
//        mSeekBarVS = (DiscreteSeekBar) view.findViewById(R.id.frg_vote_slide_vesinh);
//        mSeekBarPV = (DiscreteSeekBar) view.findViewById(R.id.frg_vote_slide_phucvu);
//        txt_pv.setText(getResources().getString(R.string.text_ratepv) + ": " + mSeekBarPV.getMin());
//        txt_vs.setText(getResources().getString(R.string.text_ratevs) + ": " + mSeekBarVS.getMin());
//        txt_price.setText(getResources().getString(R.string.text_rategia) + ": " + mSeekBarGia.getMin());
//        edt_title = (EditText) view.findViewById(R.id.edt_title);
//        edt_content = (EditText) view.findViewById(R.id.edt_content);
//        btn_save.setOnClickListener(this);
//        mSeekBarGia.setOnProgressChangeListener(this);
//        mSeekBarPV.setOnProgressChangeListener(this);
//        mSeekBarVS.setOnProgressChangeListener(this);
//        cb_monAn.setChecked(false);
//        cb_quanAn.setChecked(false);
//        cb_quanAn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    ll_danhGiaQuan.setVisibility(View.VISIBLE);
//
//                } else
//                    ll_danhGiaQuan.setVisibility(View.INVISIBLE);
//                txt_vs.setText("Vệ sinh");
//                txt_price.setText("Giá");
//                txt_pv.setText("Phục vụ");
//                gia = (long) 1;
//                vs = (long) 1;
//                pv = (long) 1;
//                mSeekBarGia.setProgress(1);
//                mSeekBarPV.setProgress(1);
//                mSeekBarVS.setProgress(1);
//            }
//        });
//        frg_filter_txtmon.setVisibility(View.INVISIBLE);
//        rb_danhGiaMon.setVisibility(View.INVISIBLE);
//        cb_monAn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    frg_filter_txtmon.setVisibility(View.VISIBLE);
//                    rb_danhGiaMon.setVisibility(View.VISIBLE);
//                } else {
//                    frg_filter_txtmon.setVisibility(View.INVISIBLE);
//                    rb_danhGiaMon.setVisibility(View.INVISIBLE);
//                }
//                frg_filter_txtmon.setText("Chọn món");
//                rb_danhGiaMon.setNumStars(3);
//
//            }
//        });
//        btnAddImg.setOnClickListener(this);
//        btn_mainImg.setOnClickListener(this);
//        rb_danhGiaMon.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//
//                if (rating == 1 && cb_monAn.isChecked())
//                    Toast.makeText(getContext(), "Dở tệ", Toast.LENGTH_SHORT).show();
//                if (rating == 2 && cb_monAn.isChecked())
//                    Toast.makeText(getContext(), "Bình thường", Toast.LENGTH_SHORT).show();
//                if (rating == 3 && cb_monAn.isChecked())
//                    Toast.makeText(getContext(), "Ngon tuyệt", Toast.LENGTH_SHORT).show();
//                mRating = rating;
//            }
//        });
//
//    }
//
//    class ParseImg extends AsyncTask<Void, Void, Bitmap> {
//        File file;
//        DoInBackGroundOK mdoInBackGroundOK;
//
//        public ParseImg(File file, DoInBackGroundOK doInBackGroundOK) {
//            this.file = file;
//            this.mdoInBackGroundOK = doInBackGroundOK;
//        }
//
//        @Override
//        protected Bitmap doInBackground(Void... params) {
//            FileInputStream fis = null;
//            try {
//                File img = new File(file.toString());
//                fis = new FileInputStream(img);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                return null;
//            }
//            Bitmap bm = BitmapFactory.decodeStream(fis);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
//            return bm;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            super.onPostExecute(bitmap);
//            mdoInBackGroundOK.DoInBackGroundImg(bitmap);
//
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.i(LOG + ".onResume", "onResume");
//        if (MyService.getActionAddPost() == 1) {
//            if (MyService.getStatusChooseImg()) {
//                if (DoPost.getInstance().getFiles() != null) {
//                    if (DoPost.getInstance().getFiles().size() > 0) {
//                        anh_dai_dien = DoPost.getInstance().getFiles().get(0);
//                        ParseImg parseImg = new ParseImg(DoPost.getInstance().getFiles().get(0), this);
//                        parseImg.execute();
//                    }
//                }
//            } else {
//                Log.i(LOG + ".ACTION=1", "Không lấy được hình");
//            }
//        }
//        if (MyService.getActionAddPost() == 2) {
//            if (DoPost.getInstance().getFiles() != null)
//                btnAddImg.setText("Số hình đã thêm: " + DoPost.getInstance().getFiles().size());
//        }
////        Toast.makeText(getActivity().getApplicationContext(), "resume post Frag with key: " + locaID, Toast.LENGTH_SHORT).show();
//    }
//
//
//    @Override
//    public void onCustomClick(View view) {
//        switch (view.getId()) {
////            case R.id.frg_post_fabchoseloca:
////                if(isConnected) {
////                    Intent intent = new Intent(getActivity(), Adapter2Activity.class);
////                    intent.putExtra(getString(R.string.fragment_CODE), getString(R.string.frag_chooseloca_CODE));
////                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                    startActivity(intent);
////                }else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
////                break;
////            case R.id.frg_post_fabchoseimg:
////                Intent intent1 = new Intent(getActivity().getApplicationContext(), Adapter2Activity.class);
////                intent1.putExtra(getResources().getString(R.string.fragment_CODE),
////                        getResources().getString(R.string.frag_chooseimg_CODE));
////                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                startActivity(intent1);
////                break;
////            case R.id.frg_post_fabrate:
////                FragmentManager fm = getActivity().getSupportFragmentManager();
////                DoVoteFragment dovoteFragment = DoVoteFragment.newIntance(getResources().getString(R.string.text_vote));
////                dovoteFragment.show(fm, "fragment_dovote");
////                break;
//            case R.id.btn_save:
//                if (isConnected) {
//                    savePost(view);
//                } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.btn_chooseMainImg:
//                if (isConnected) {
//                    MyService.setActionAddPost(1);
//                    Intent intent1 = new Intent(getActivity().getApplicationContext(), Adapter2Activity.class);
//                    intent1.putExtra(getResources().getString(R.string.fragment_CODE),
//                            getResources().getString(R.string.frag_chooseimg_CODE));
//
//                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent1);
//
//                } else {
//                    MyService.setActionAddPost(-1);
//                    Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case R.id.btn_addPhoto:
//                if (isConnected) {
//                    MyService.setActionAddPost(2);
//                    Intent intent1 = new Intent(getActivity().getApplicationContext(), Adapter2Activity.class);
//                    intent1.putExtra(getResources().getString(R.string.fragment_CODE),
//                            getResources().getString(R.string.frag_chooseimg_CODE));
//                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent1);
//
//                } else {
//                    MyService.setActionAddPost(-1);
//                    Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        isConnected = MyService.returnIsConnected();
//        mIntentFilter = new IntentFilter();
//        mIntentFilter.addAction(mBroadcastSendAddress);
//        getContext().registerReceiver(broadcastReceiver, mIntentFilter);
//        try {
//            if (DoPost.getInstance().getMyLocation() != null) {
//                img.setVisibility(View.VISIBLE);
//                txt_address.setVisibility(View.VISIBLE);
//                txt_address.setText(DoPost.getInstance().getMyLocation().getDiachi());
//                txt_name.setText(DoPost.getInstance().getMyLocation().getName());
//            } else {
//                img.setVisibility(View.GONE);
//                txt_address.setVisibility(View.GONE);
//            }
//        } catch (NullPointerException mess) {
//            img.setVisibility(View.GONE);
//            txt_address.setVisibility(View.GONE);
//            Log.e("nullChooseloca", mess.getMessage());
//        }
//
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        DoPost.getInstance().setMyLocation(null);
//        DoPost.getInstance().setVesinh(0);
//        DoPost.getInstance().setPrice(0);
//        DoPost.getInstance().setFiles(null);
//        DoPost.getInstance().setPhucvu(0);
//    }
//
//    private void savePost(View view) {
//
//        try {
//            if (DoPost.getInstance().getMyLocation() == null) {
//                checloca = true;
//            }
//        } catch (NullPointerException mess) {
//            checloca = true;
//        }
//        try {
//            if (gia < 1
//                    && vs < 1
//                    && pv < 1) {
//                checkrate = true;
//            }
//        } catch (NullPointerException mess) {
//            checkrate = true;
//        }
//        if (edt_title.getText().toString().trim().equals("")) {
//            Snackbar.make(view, getResources().getString(R.string.txt_notitle), Snackbar.LENGTH_SHORT).show();
//        } else if (edt_content.getText().toString().trim().equals("")) {
//            Snackbar.make(view, getResources().getString(R.string.txt_nocontent), Snackbar.LENGTH_SHORT).show();
//
//        } else if (cb_monAn.isChecked() && (mFood.getStoreID() == null)) {
//            Snackbar.make(view, "Chưa chọn món hoặc chưa đánh giá món", Snackbar.LENGTH_SHORT).show();
//
//        } else if (checloca) {
//            Snackbar.make(view, getResources().getString(R.string.txt_nochoseloca), Snackbar.LENGTH_SHORT).show();
//        } else {
//            dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
//            key = dbRef.child(getString(R.string.posts_CODE)).push().getKey();
//            updateLoca = DoPost.getInstance().getMyLocation();
//            locaID = DoPost.getInstance().getMyLocation().getStoreID();
//            Toast.makeText(getContext(), "LOCAL ID:" + locaID, Toast.LENGTH_SHORT).show();
//            plzw8Dialog = ProgressDialog.show(getActivity(),
//                    getResources().getString(R.string.txt_plzwait),
//                    getResources().getString(R.string.txt_addinpost),
//                    true, false);
//            pc_Success = 0;
//            if (MyService.getUserAccount() != null)
//                addpost(key, locaID, updateLoca);
//        }
//    }
//
//    private void addpost(String key, String locaID, MyLocation updateLoca) {
//        url = new ArrayList<>();
//        upLoadImages = new ArrayList<>();
//        uris = new ArrayList<>();
//        childUpdates = new HashMap<String, Object>();
//        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
//        newPost = new Post();
//        if (cb_monAn.isChecked()) {
//            Food tempFood = mFood;
//            tempFood.setRating(mRating);
//            newPost.setType(1);
//            if (mFood.getStoreID() != null) {
//                float a = mFood.getRating();
//                Log.i("CSS", a + "_---------------------------");
//                float b = ((a + mRating) / 2);
//                Log.i("CSS", b + "_---------------------------");
//                mFood.setRating(b);
//                childUpdates.put(
//                        getResources().getString(R.string.thucdon_CODE)
//                                + mFood.getFoodID(), mFood);
//                pc_Success++;
//                Log.i("SSSSSSS", "pc_Success1=" + pc_Success);
//                newPost.setImage(tempFood);
//            }
//        } else {
//            newPost.setType(2);
//            pc_Success++;
//            Log.i("SSSSSSS", "pc_Success_Not choose=" + pc_Success);
//        }
//        if (!checkrate && cb_quanAn.isChecked()) {
//            newPost.setPrice(gia);
//            newPost.setVesinh(vs);
//            newPost.setPhucvu(pv);
//            long giaTong = updateLoca.getGiaTong() + gia,
//                    vsTong = updateLoca.getVsTong() + vs,
//                    pvTong = updateLoca.getPvTong() + pv,
//                    size = updateLoca.getSize() + 1;
//            updateLoca.setGiaTong(giaTong);
//            updateLoca.setVsTong(vsTong);
//            updateLoca.setPvTong(pvTong);
//            updateLoca.setSize(size);
//            updateLoca.setGiaAVG(giaTong / size);
//            updateLoca.setVsAVG(vsTong / size);
//            updateLoca.setPvAVG(pvTong / size);
//            updateLoca.setTongAVG((giaTong + vsTong + pvTong) / (size * 3));
//            Map<String, Object> updateLocal = updateLoca.toMap();
//            Toast.makeText(getContext(), "SAO K DC Z TA:" + updateLoca.getTongAVG(), Toast.LENGTH_SHORT).show();
//            childUpdates.put(
//                    getResources().getString(R.string.locations_CODE)
//                            + locaID, updateLocal);
//            pc_Success++;
//            Log.i("SSSSSSS", "pc_Success_2=" + pc_Success);
//        } else {
//            pc_Success++;
//            Log.i("SSSSSSS", "pc_Success_2Not choose=" + pc_Success);
//        }
//        Log.i("ZOOOOOOO AddPost", "");
//        newPost.setTitle(edt_title.getText().toString());
//        newPost.setIndex(DoPost.getInstance().getMyLocation().getTinhtp() + "_" + DoPost.getInstance().getMyLocation().getQuanhuyen());
//        newPost.setContent(edt_content.getText().toString());
//        newPost.setUserId(MyService.getUserAccount().getId());
//        newPost.setUserName(MyService.getUserAccount().getUsername());
//        newPost.setDate(new Times().getTime());
//        newPost.setTime(new Times().getDate());
//        newPost.setStoreID(locaID);
//        //     if(MyService.getUserAccount().getExistUser()){
//        newPost.setVisible(true);
////        }else
////            newPost.setVisible(false);
//        newPost.setLocaName(DoPost.getInstance().getMyLocation().getName());
//        newPost.setDiachi(DoPost.getInstance().getMyLocation().getDiachi());
//        newPost.setIndex(DoPost.getInstance().getMyLocation().getTinhtp() + "_" + DoPost.getInstance().getMyLocation().getQuanhuyen());
//        myFile = new ArrayList<>();
//        if (MyService.getStatusChooseImg() && MyService.getActionAddPost() == 2) {
//            Log.i("SSSSSSS", "myFile=" + myFile.size());
//            myFile = DoPost.getInstance().getFiles();
//
//            if (anh_dai_dien != null && mainImg) {
//                myFile.add(anh_dai_dien);
//                Log.i("SSSSSSS", "myFile + 1=" + myFile.size());
//            }
//            new uploadImg().execute();
//        } else if (MyService.getStatusChooseImg() && MyService.getActionAddPost() == 1) {
//            Log.i("SSSSSSS", "myFile=" + myFile.size());
//            myFile = DoPost.getInstance().getFiles();
//            new uploadImg().execute();
//        } else {
//            pc_Success++;
//            postValue = newPost.toMap();
//            childUpdates.put(
//                    getResources().getString(R.string.posts_CODE) + key, postValue);
//            pc_Success++;
//            Log.i("SSSSSSS", "pc_Success_3_Not choose");
//            Log.i("SSSSSSS", "pc_Success_4=" + pc_Success);
//            if (pc_Success == 4)
//                upload();
//        }
//
//
//    }
//
//    public void upload() {
//        dbRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                if (databaseError != null) {
//                    plzw8Dialog.dismiss();
//                    Toast.makeText(getActivity(), "Đăng bài bị lỗi" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                } else {
//                    if (!MyService.getUserAccount().getExistUser()) {
//                        Notification notification = new Notification();
//                        String key1 = dbRef.child(getResources().getString(R.string.notification_CODE) + "admin").push().getKey();
//                        notification.setAccount(MyService.getUserAccount());
//                        notification.setDate(new Times().getDate());
//                        notification.setTime(new Times().getTime());
//                        notification.setType(3);
//                        newPost.setPostID(key);
//                        notification.setPost(newPost);
//                        notification.setStore(DoPost.getInstance().getMyLocation());
//                        notification.setReaded(false);
//                        notification.setTo("admin");
//                        Map<String, Object> notificationValue = notification.toMap();
//                        childUpdates = new HashMap<>();
//                        childUpdates.put(getResources().getString(R.string.notification_CODE) + "admin/" + key, notificationValue);
//                        dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isComplete()) {
//                                    plzw8Dialog.dismiss();
//                                    Toast.makeText(getActivity(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
//                                    getActivity().finish();
//                                } else {
//                                    plzw8Dialog.dismiss();
//                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    } else {
//                        plzw8Dialog.dismiss();
//                        Toast.makeText(getActivity(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
//                        getActivity().finish();
//                    }
//                }
//            }
//        });
//        pc_Success = 0;
//    }
//
//    class uploadImg extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... params) {
//            stRef = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebaseStorage_path));
//            ListID = new ArrayList<>();
//            Log.i("uploadImg", "myFile=" + myFile.size());
//            for (File f : myFile) {
//                Uri uri = Uri.fromFile(f);
//                fileKey = dbRef.child(getResources().getString(R.string.images_CODE1)).push().getKey();
//                ListID.add(fileKey);
//                newImage = new Image();
//                newImage.setName(uri.getLastPathSegment());
//                newImage.setPostID(key);
//                newImage.setUserID(MyService.getUserAccount().getId());
//                newImage.setStoreID(locaID);
//                upLoadImages.add(newImage);
//                Log.i("SSSSSSS", "upLoadImages=" + upLoadImages.size());
//                StorageReference myChildRef = stRef.child(
//                        getResources().getString(R.string.images_CODE)
//                                + uri.getLastPathSegment());
//                uploadTask = myChildRef.putFile(uri);
//                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Uri uri = taskSnapshot.getDownloadUrl();
//                        uris.add(uri);
//                        if (uris.size() == upLoadImages.size())
//                            MyService.setUriImg(uris);
//                        Log.i("ZOOOOOOO UploadImage", "isSuccess = true;" + uri.toString());
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Log.i("ZOOOOOOO UploadImage", "isSuccess = false;");
//                                            }
//                                        }
//                );
//            }
//            return null;
//        }
//    }
//
//    @Override
//    public void DoInBackGroundStart() {
//
//    }
//
//    @Override
//    public void DoInBackGroundOK(Boolean isSuccess, int type) {
//    }
//
//    @Override
//    public void DoInBackGroundImg(Bitmap bitmap) {
//        try {
//            img_Daidien.setImageBitmap(bitmap);
//            mainImg = true;
//            Log.i(LOG + ".ACTION=1", "set Anh dai dien: OK");
//        } catch (Exception e) {
//            Log.i(LOG + ".ACTION=1", "set Anh dai dien: FAIL");
//        }
//    }
//
//    @Override
//    public void DoInBackGroundLocation(MyLocation location) {
//
//    }
//
//    //        class StoreImg extends AsyncTask<ArrayList<File>,Void,Boolean> {
////        DoInBackGroundOK doInBackGroundOK;
////        StorageReference stRef;
////        UploadTask uploadTask;
////        boolean isSuccess=false;
////        public StoreImg(DoInBackGroundOK mdoInBackGroundOK, StorageReference storageReference,UploadTask mup){
////            doInBackGroundOK=mdoInBackGroundOK;
////            stRef=storageReference;
////            uploadTask=mup;
////        }
////        @Override
////        protected Boolean doInBackground(ArrayList<File>... params) {
////            Log.i("ZOOOOOOO ParseImg","");
////
////            return isSuccess;
////        }
////
////        @Override
////        protected void onPostExecute(Boolean aBoolean) {
////            super.onPostExecute(aBoolean);
////            if(aBoolean){
////                doInBackGroundOK.DoInBackGroundOK(true,1);
////            } else{
////                doInBackGroundOK.DoInBackGroundOK(false,1);
////            }
////        }
////    }
////    class UpDateLoca extends AsyncTask<MyLocation,Void,Boolean>{
////        long gia,vs,pv;
////        String locaID;
////        Boolean isSuccess=false;
////        Map<String, Object> childUpdates;
////        DoInBackGroundOK doInBackGroundOK;
////        DatabaseReference dbRef;
////
////        public UpDateLoca(long mgia,long mvs,long mpv,String mlocaID, DoInBackGroundOK mdoInBackGroundOK,DatabaseReference databaseReference){
////            gia=mgia;
////            vs=mvs;
////            pv=mpv;
////            locaID=mlocaID;
////            doInBackGroundOK=mdoInBackGroundOK;
////            childUpdates=new HashMap<>();
////            dbRef=databaseReference;
////        }
////        @Override
////        protected Boolean doInBackground(MyLocation... params) {
////            Log.i("ZOOOOOOO UpDateLoca","");
////            MyLocation updateLoca=params[0];
////            long giaTong = updateLoca.getGiaTong() + gia,
////                    vsTong = updateLoca.getVsTong() + vs,
////                    pvTong = updateLoca.getPvTong() +pv,
////                    size = updateLoca.getSize() + 1;
////            Log.i("CSS"+size,giaTong+"_-----/n"+vsTong+"----/n"+pvTong);
////            updateLoca.setGiaTong(giaTong);
////            updateLoca.setVsTong(vsTong);
////            updateLoca.setPvTong(pvTong);
////            updateLoca.setSize(size);
////            updateLoca.setGiaAVG(giaTong / size);
////            updateLoca.setVsAVG(vsTong / size);
////            updateLoca.setPvAVG(pvTong / size);
////            updateLoca.setTongAVG((giaTong + vsTong + pvTong) / (size * 3));
////            childUpdates.put(
////                    getResources().getString(R.string.locations_CODE)
////                            + locaID, updateLoca);
////            dbRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
////                @Override
////                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
////                    if (databaseError != null) {
////                       isSuccess=false;
////                    } else {
////                        isSuccess=true;
////                    }
////                }
////            });
////            return isSuccess;
////        }
////
////        @Override
////        protected void onPostExecute(Boolean aBoolean) {
////            super.onPostExecute(aBoolean);
////            if(aBoolean){
////                doInBackGroundOK.DoInBackGroundOK(true,2);
////            } else{
////                doInBackGroundOK.DoInBackGroundOK(false,2);
////            }
////        }
////    }
////    class AddPost extends AsyncTask<Post,Void, Boolean>{
////        String title,content,userId,userName,localName,localDiaChi,locaID,key;
////        DoInBackGroundOK doInBackGroundOK;
////        Map<String, Object> childUpdates;
////        Boolean isSuccess=false;
////        boolean mainImg=false;
////        ImageView img_Daidien;
////        DatabaseReference dbRef;
////        public AddPost(String mtitle,String mContent,String muserID,String muserName,
////                        String mlocalName, String mlocalDiaChi, DoInBackGroundOK mDoInBackGroundOK,
////                       boolean mmainImg, ImageView img,String mlocalID,String mKey,DatabaseReference databaseReference){
////            title=mtitle;
////            content=mContent;
////            userId=muserID;
////            userName=muserName;
////            localName=mlocalName;
////            localDiaChi=mlocalDiaChi;
////            mainImg=mmainImg;
////            doInBackGroundOK=mDoInBackGroundOK;
////            img_Daidien=img;
////            locaID=mlocalID;
////            childUpdates=new HashMap<>();
////            key=mKey;
////            dbRef=databaseReference;
////        }
////        @Override
////        protected Boolean doInBackground(Post... params) {
////
////            dbRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
////                @Override
////                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
////                    if (databaseError != null) {
////                        isSuccess=false;
////                    } else {
////                        isSuccess=true;
////                    }
////                }
////            });
////            return isSuccess;
////        }
////
////        @Override
////        protected void onPostExecute(Boolean aBoolean) {
////            super.onPostExecute(aBoolean);
////            if(aBoolean){
////                doInBackGroundOK.DoInBackGroundOK(true,3);
////            } else{
////                doInBackGroundOK.DoInBackGroundOK(false,3);
////            }
////        }
////    }
////    class AddImg extends AsyncTask<ArrayList<File>,Void,Boolean>{
////        DoInBackGroundOK doInBackGroundOK;
////        Map<String, Object> childUpdates;
////        DatabaseReference dbRef;
////        String key,locaID;
////        public AddImg(DoInBackGroundOK mdoInBackGroundOK, DatabaseReference databaseReference,String mkey,String mlocalID){
////            doInBackGroundOK=mdoInBackGroundOK;
////            dbRef=databaseReference;
////            childUpdates=new HashMap<>();
////
////        }
////        boolean isSuccess=false;
////        @Override
////        protected Boolean doInBackground(ArrayList<File>... params) {
////            for (File f : params[0]) {
////                Uri uri = Uri.fromFile(f);
////                String fileKey = dbRef.child(getResources().getString(R.string.images_CODE)).push().getKey();
////                Image newImage = new Image();
////                newImage.setName(uri.getLastPathSegment());
////                newImage.setPostID(key);
////                newImage.setUserID(MyService.getUserID());
////                newImage.setStoreID(locaID);
////                childUpdates.put(getResources().getString(R.string.images_CODE)
////                        + fileKey, newImage);
////            }
////            return isSuccess;
////        }
////
////        @Override
////        protected void onPostExecute(Boolean aBoolean) {
////            super.onPostExecute(aBoolean);
////            if(aBoolean)
////                doInBackGroundOK.DoInBackGroundOK(null,4);
////            else
////                doInBackGroundOK.DoInBackGroundOK(null,5);
////        }
////    }
////    @Override
////    public void DoInBackGroundStart() {
////    }
////
////    @Override
////    public void DoInBackGroundOK(Boolean isSuccess,int type) {
////        if(type<4){
////            pc_Success++;
////            this.childUpdates=childUpdates;
////                Log.i("CHAY OKKKKKKKKKKKK","");
////                dbRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
////                    @Override
////                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
////                        if (databaseError != null) {
////                            Log.i("CHAY ","FAILLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
////
////                        } else {
////                            Log.i("CHAY ","OKKKKKKKKKKK");
////
////                        }
////                    }
////                });
////            childUpdates=new HashMap<>();
////
////            Log.i("DoInBackGroundOK","OK-"+type+"-pc success"+pc_Success+"-"+childUpdates.size());
////        }else{
////            if(type==4){
////                Log.i("DoInBackGroundOK","type==4");
////                Toast.makeText(getContext(),"Thêm thành công",Toast.LENGTH_SHORT).show();
////                getActivity().finish();
////            }else
////                Toast.makeText(getContext(),"Thêm thất bại. Xin thử lại",Toast.LENGTH_SHORT).show();
////            plzw8Dialog.dismiss();
////        }
////        if(pc_Success==4){
//////            Log.i("DoInBackGroundOK","pc_Success==4 ---"+childUpdates.size());
//////            AddData addData=new AddData(this,dbRef);
//////            addData.execute(childUpdates);
////            pc_Success=0;
////        }
////
////    }
//    @Override
//    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
//        switch (seekBar.getId()) {
//            case R.id.frg_vote_slide_gia:
//                txt_price.setText(getResources().getString(R.string.text_rategia) + ": " + String.valueOf(value));
//                try {
//                    gia = (long) seekBar.getProgress();
//
//                } catch (NullPointerException mess) {
//                    gia = (long) 1;
//                }
//                break;
//            case R.id.frg_vote_slide_phucvu:
//                txt_pv.setText(getResources().getString(R.string.text_ratepv) + ": " + String.valueOf(value));
//                try {
//                    pv = (long) seekBar.getProgress();
//                } catch (NullPointerException mess) {
//                    pv = (long) 1;
//                }
//                break;
//            case R.id.frg_vote_slide_vesinh:
//                txt_vs.setText(getResources().getString(R.string.text_ratevs) + ": " + String.valueOf(value));
//                try {
//                    vs = (long) seekBar.getProgress();
//                } catch (NullPointerException mess) {
//                    vs = (long) 1;
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
//
//    }
//
//    @Override
//    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
//
//    }
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
}

