package com.app.ptt.comnha.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Food_recycler_adapter;
import com.app.ptt.comnha.Adapters.Photo_recycler_adapter;
import com.app.ptt.comnha.Adapters.Post_recycler_adapter;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Dialog.AddFoodDialog;
import com.app.ptt.comnha.Dialog.ReportDialog;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Image;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.Models.FireBase.UserNotification;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChoosePhotoList;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.AppUtils;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.app.ptt.comnha.Const.Const.REPORTS.REPORT_STORE;

public class StoreDeatailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_SIGNIN = 101;
    RecyclerView postRecycler, photoRecycler, foodRecycler;
    RecyclerView.LayoutManager postLayoutManager, photoLayoutManager,
            foodLayoutManager;
    Post_recycler_adapter postAdapter;
    Photo_recycler_adapter photoAdapter = null;
    Food_recycler_adapter foodAdapter;
    ArrayList<Post> posts;
    ArrayList<Image> images;
    ArrayList<Food> foods;
    User ownUser;
    TextView txtv_storename, txtv_address, txtv_opentime, txtv_phonenumb,
            txtv_pricerate, txtv_healthyrate, txtv_servicerate, txt_user;
    Toolbar toolbar;
    ImageView imgv_writepost, imgv_addfood, imgv_viewlocation;
    CircularImageView imgv_avatar;
    FloatingActionButton fab;
    Dialog dialogEditStore;
    DatabaseReference dbRef;
    StorageReference stRef;
    ValueEventListener postValueListener, photoValueListener,
            foodValueListener, userValueListener;
    String keyReport;
    Store store;
    LinearLayout linear_progress;
    String storeID = null;
    ProgressDialog plzw8Dialog = null;
    Menu pubMenu = null;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_deatail);
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Const.DATABASE_PATH);
        stRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Const.STORAGE_PATH);
        mAuth = FirebaseAuth.getInstance();
        if (LoginSession.getInstance().getUser() != null) {
            user = LoginSession.getInstance().getUser();
        }
        if (ChooseStore.getInstance().getStore() != null) {
            storeID=ChooseStore.getInstance().getStore().getStoreID();
            getStore( ChooseStore.getInstance().getStore().getStoreID());
            ref();
        } else {
            onBackPressed();
        }
    }

    private void ref() {
        View include_view = findViewById(R.id.include_storedetail_content);
        postRecycler = (RecyclerView) include_view.findViewById(R.id.recycler_post_storedetail);
        postLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, true);
        postRecycler.setLayoutManager(postLayoutManager);
        posts = new ArrayList<>();
        postAdapter = new Post_recycler_adapter(posts, this, stRef);
        postRecycler.setAdapter(postAdapter);
        postAdapter.setOnItemClickLiestner(new Post_recycler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(Post post, View itemView) {
                Intent intent_postdetail = new Intent(StoreDeatailActivity.this,
                        PostdetailActivity.class);
                ActivityOptionsCompat option_postbanner
                        = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        StoreDeatailActivity.this, itemView.findViewById(R.id.imgv_banner_postitem),
                        "postBanner");
                ChoosePost.getInstance().setPost(post);
                startActivity(intent_postdetail, option_postbanner.toBundle());
            }
        });
        photoRecycler = (RecyclerView) include_view.findViewById(R.id.recycler_photo_storedetail);
        photoLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        photoRecycler.setLayoutManager(photoLayoutManager);
        images = new ArrayList<>();
        photoAdapter = new Photo_recycler_adapter(images, stRef, this);
        photoRecycler.setAdapter(photoAdapter);
        photoAdapter.setOnItemClickLiestner(new Photo_recycler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(Image image, Activity activity, View itemView) {
                Intent intent_openViewPhoto = new Intent(activity, ViewPhotosActivity.class);
                intent_openViewPhoto.putExtra("imgPosition", images.indexOf(image));
                startActivity(intent_openViewPhoto);
            }
        });
        foodRecycler = (RecyclerView) include_view.findViewById(R.id.recycler_foods_storedetail);
        foodLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        foodRecycler.setLayoutManager(foodLayoutManager);
        foods = new ArrayList<>();
        foodAdapter = new Food_recycler_adapter(foods, this, stRef);
        foodRecycler.setAdapter(foodAdapter);
        foodAdapter.setOnItemClickLiestner(new Food_recycler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(Food food, Activity activity, View itemView) {
                Intent intent_openFood = new Intent(getApplicationContext(),
                        FoodDetailActivity.class);
                ActivityOptionsCompat optionsCompat
                        = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, itemView.findViewById(R.id.item_rcyler_food_imgV),
                        "foodphoto");
                intent_openFood.putExtra(Const.KEY_FOOD,food.getFoodID());
                startActivity(intent_openFood, optionsCompat.toBundle());
            }
        });
        txtv_storename = (TextView) include_view.findViewById(R.id.txtv_storename_storedetail);
        txtv_address = (TextView) include_view.findViewById(R.id.txtv_address_storedetail);
        txtv_opentime = (TextView) include_view.findViewById(R.id.txtv_opentime_storedetail);
        txtv_phonenumb = (TextView) include_view.findViewById(R.id.txtv_phonenumb_storedetail);
        txtv_pricerate = (TextView) include_view.findViewById(R.id.txtv_price_storedetail);
        txtv_healthyrate = (TextView) include_view.findViewById(R.id.txtv_healthy_storedetail);
        txtv_servicerate = (TextView) include_view.findViewById(R.id.txtv_service_storedetail);
        toolbar = (Toolbar) findViewById(R.id.toolbar_storedetail);
        txt_user = (TextView) include_view.findViewById(R.id.txtv_user_storedetail);
        toolbar.setTitle(getString(R.string.txt_storedetail));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        this.setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        imgv_writepost = (ImageView) findViewById(R.id.imgv_writepost_storedetail);
        imgv_addfood = (ImageView) findViewById(R.id.imgv_addfood_storedetail);
        imgv_viewlocation = (ImageView) findViewById(R.id.imgv_viewlocation_storedetail);
        imgv_avatar = (CircularImageView) findViewById(R.id.imgv_avatar_storedetail);
        fab = (FloatingActionButton) findViewById(R.id.fab_menu_storedetail);
        imgv_writepost.setOnClickListener(this);
        imgv_addfood.setOnClickListener(this);
        imgv_viewlocation.setOnClickListener(this);
        fab.setOnClickListener(this);
        linear_progress = (LinearLayout) findViewById(R.id.linear_progress_storedetail);
        plzw8Dialog = AppUtils.setupProgressDialog(this,
                getString(R.string.txt_plzwait), null, false, false,
                ProgressDialog.STYLE_SPINNER, -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        List<Pair<Integer, String>> contents = returnContentMenuItems();
        menu = AppUtils.createMenu(menu, contents);
        pubMenu = menu;
        return super.onCreateOptionsMenu(menu);

    }

    private List<Pair<Integer, String>> returnContentMenuItems() {
        int role = 0;
        String uID = "";
        if (LoginSession.getInstance().getUser() != null) {
            role = LoginSession.getInstance().getUser().getRole();
            uID = LoginSession.getInstance().getUser().getuID();
        }
        List<Pair<Integer, String>> contents = new ArrayList<>();
        if (store != null) {
            if (role > 0) {
                if (store.isHidden()) {
                    if (store.getStoreType() == 0) {
                        contents.add(new Pair<Integer, String>
                                (R.string.txt_acceptstore, getString(R.string.txt_acceptstore)));
                        contents.add(new Pair<Integer, String>
                                (R.string.txt_rejectstore, getString(R.string.txt_rejectstore)));
                    }
                    if (store.getStoreType() == -1) {
                        contents.add(new Pair<Integer, String>
                                (R.string.txt_acceptstorereport, getString(R.string.txt_acceptstorereport)));
                        contents.add(new Pair<Integer, String>
                                (R.string.txt_rejectstorereport, getString(R.string.txt_rejectstorereport)));
                    }

                } else {
//                    contents.add(new Pair<Integer, String>
//                            (R.string.txt_rejectstore, getString(R.string.txt_rejectstore)));
                }
            }

            if (uID.equals(store.getUserID()) || role > 0) {

                contents.add(new Pair<Integer, String>
                        (R.string.txt_changeinfo, getString(R.string.txt_changeinfo)));
            }
            if (!uID.equals(store.getUserID())) {
                if (store.checkExist(uID)) {
                    contents.add(new Pair<Integer, String>
                            (R.string.txt_unfollowStore, getString(R.string.txt_unfollowStore)));

                } else {
                    contents.add(new Pair<Integer, String>
                            (R.string.txt_followStore, getString(R.string.txt_followStore)));
                }
                if (LoginSession.getInstance().getUser().isReportstoreBlocked()) {
                    AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), getString(R.string.text_block_user));
                } else {
                    contents.add(new Pair<Integer, String>
                            (R.string.txt_report, getString(R.string.txt_report)));
                }
            }

        }
        return contents;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.string.txt_report:
                if (LoginSession.getInstance().getUser() != null) {
                    reportStore();
                } else {
                    requestSignin();
                }
                return true;
            case R.string.txt_followStore:
                if (LoginSession.getInstance().getUser() != null) {
                    store.addUserToList(LoginSession.getInstance().getUser().getuID());
                    updateStore(1);
                } else {
                    requestSignin();
                }
                return true;
            case R.string.txt_unfollowStore:
                if (LoginSession.getInstance().getUser() != null) {
                    store.removeUser(LoginSession.getInstance().getUser().getuID());
                    updateStore(2);
                } else {
                    requestSignin();
                }
                return true;
            case R.string.txt_rejectstore:
                    hideStore(-2);
                return true;
            case R.string.txt_acceptstore:

                    showStore(2);
                return true;
            case R.string.txt_acceptstorereport:
                showStore(1);
                //sendBroadcast();
                return true;
            case R.string.txt_rejectstorereport:
                hideStore(-3);
                // sendBroadcast();
                return true;

            case R.string.txt_delstore:
                return true;
            case R.string.txt_changeinfo:
                editStore();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void requestSignin() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.txt_nologin)
                        + "\n" + getString(R.string.txt_uneedlogin))
                .setPositiveButton(getString(R.string.text_signin), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent_signin = new Intent(StoreDeatailActivity.this,
                                SignInActivity.class);
                        startActivityForResult(intent_signin, REQUEST_SIGNIN);
                    }
                })
                .setNegativeButton(getString(R.string.text_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }


    public Map<String, Object> notificationToUser(Map<String, Object> childUpdate, int status, int type) {

        switch (status) {
            case 1:
                //accept
                if (!store.getUserID().toLowerCase().equals(LoginSession.getInstance().getUser().getuID().toLowerCase())) {
                    UserNotification userNotification = new UserNotification();
                    userNotification.setStoreID(store.getStoreID());
                    userNotification.setType(type);
                    userNotification.setShown(true);
                    userNotification.setStatus(status);
                    Map<String, Object> userNotificationMap = userNotification.toMap();
                    String key = dbRef.child(getString(R.string.user_notification_CODE) + store.getUserID()).push().getKey();
                    childUpdate.put(getString(R.string.user_notification_CODE) + store.getUserID() + "/" + key, userNotificationMap);

                }
                break;
            //reject
            case -1:
                UserNotification userNotification = new UserNotification();
                userNotification.setStoreID(store.getStoreID());
                userNotification.setType(type);
                userNotification.setShown(true);
                userNotification.setStatus(status);
                userNotification.setƠwner(true);
                Map<String, Object> userNotificationMap = userNotification.toMap();
                String key = dbRef.child(getString(R.string.user_notification_CODE) + store.getUserID()).push().getKey();
                childUpdate.put(getString(R.string.user_notification_CODE) + store.getUserID() + "/" + key, userNotificationMap);
                break;
            //was reported
            case 3:
                UserNotification userReport = new UserNotification();
                userReport.setStoreID(store.getStoreID());
                userReport.setType(type);
                userReport.setShown(true);
                userReport.setStatus(status);
                userReport.setƠwner(true);
                userReport.setUserEffectName(LoginSession.getInstance().getUser().getUn());
                userReport.setReportId(keyReport);
                Map<String, Object> userReportMap = userReport.toMap();
                String keyReportNoti = dbRef.child(getString(R.string.user_notification_CODE) + store.getUserID()).push().getKey();
                childUpdate.put(getString(R.string.user_notification_CODE) + store.getUserID() + "/" + keyReportNoti, userReportMap);
                break;
            //reject report
            case 2:
                UserNotification userRejectReport = new UserNotification();
                userRejectReport.setStoreID(store.getStoreID());
                userRejectReport.setType(type);
                userRejectReport.setShown(true);
                userRejectReport.setStatus(status);
                userRejectReport.setƠwner(true);
                Map<String, Object> userRejectReportMap = userRejectReport.toMap();
                String keyuserRejectReportNoti = dbRef.child(getString(R.string.user_notification_CODE) + store.getUserID()).push().getKey();
                childUpdate.put(getString(R.string.user_notification_CODE) + store.getUserID() + "/" + keyuserRejectReportNoti, userRejectReportMap);
                break;
            //accept report
            case -2:
                UserNotification userAcceptReport = new UserNotification();
                userAcceptReport.setStoreID(store.getStoreID());
                userAcceptReport.setType(type);
                userAcceptReport.setShown(true);
                userAcceptReport.setStatus(status);
                userAcceptReport.setƠwner(true);
                Map<String, Object> userAcceptReportMap = userAcceptReport.toMap();
                String keyuserAcceptReportNoti = dbRef.child(getString(R.string.user_notification_CODE) + store.getUserID()).push().getKey();
                childUpdate.put(getString(R.string.user_notification_CODE) + store.getUserID() + "/" + keyuserAcceptReportNoti, userAcceptReportMap);
                break;
        }

        return childUpdate;
    }


    private void showStore(final int type) {
        plzw8Dialog.show();
        Map<String, Object> childUpdate = new HashMap<>();
        switch (store.getStoreType()) {
            case 0:
                childUpdate = notificationToUser(childUpdate, 1, 1);
                break;
            case 3:
                childUpdate = notificationToUser(childUpdate, 2, 1);
                break;
        }
        //hide all post
        childUpdate= changePostStatus(childUpdate,false);
        //hide food
        childUpdate=changeFoodStatus(childUpdate,false);
        String key = store.getStoreID();
        store.setHidden(false);
        store.setStoreType(type);
        Store childStore = store;
        Map<String, Object> storeValue = childStore.toMap();

        childUpdate.put(getString(R.string.store_CODE)
                + key, storeValue);
        dbRef.updateChildren(childUpdate)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                loadMenu();
                                plzw8Dialog.cancel();
                            }
                        }).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadMenu();
                        plzw8Dialog.cancel();
                        Toast.makeText(getApplicationContext(),
                                e.getMessage(), Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }

    private boolean updateStore(final int type) {
        Map<String, Object> updatedStore;
        updatedStore = store.toMap();
        Map<String, Object> childUpdate = new HashMap<>();
        childUpdate.put(getString(R.string.store_CODE) + store.getStoreID(), updatedStore);
        dbRef.updateChildren(childUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (pubMenu != null) {
                    List<Pair<Integer, String>> contents = returnContentMenuItems();
                    pubMenu = AppUtils.createMenu(pubMenu, contents);
                }
                if (type == 1) {
                    AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), "Đã thêm vào danh sách theo dõi");
                } else {
                    AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), "Đã bỏ theo dõi");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), "Có lỗi. Xin thử lại");
            }
        });
        if (pubMenu != null) {
            return super.onCreateOptionsMenu(pubMenu);
        } else {
            return false;
        }
    }

    public boolean loadMenu() {
        if (pubMenu != null) {
            pubMenu = AppUtils.createMenu(pubMenu, returnContentMenuItems());
            return super.onCreateOptionsMenu(pubMenu);
        }
        return false;
    }

    private void hideStore(final int type) {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setMessage(getString(R.string.txt_hideconfirm))
                .setPositiveButton(getString(R.string.txt_hide),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    final DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                plzw8Dialog.show();
                                Map<String, Object> childUpdate = new HashMap<>();
                                switch (store.getStoreType()) {
                                    case 0:
                                        childUpdate = notificationToUser(childUpdate, -1, 1);
                                        break;
                                    case 3:
                                        childUpdate = notificationToUser(childUpdate, -2, 1);
                                        break;
                                }
                                store.setHidden(true);
                                store.setStoreType(type);
                                String key = store.getStoreID();
                                Store childStore = store;
                                Map<String, Object> storeValue = childStore.toMap();
                                //hide all post
                                childUpdate= changePostStatus(childUpdate,true);
                                //hide food
                                childUpdate=changeFoodStatus(childUpdate,true);
                                childUpdate.put(getString(R.string.store_CODE)
                                        + key, storeValue);
                                dbRef.updateChildren(childUpdate)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        loadMenu();
                                                        plzw8Dialog.cancel();
                                                    }
                                                }).addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                loadMenu();
                                                plzw8Dialog.cancel();
                                                Toast.makeText(getApplicationContext(),
                                                        e.getMessage(), Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        });
                            }
                        })
                .setNegativeButton(getString(R.string.text_no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void createStoreInfo() {
        txtv_storename.setText(store.getName());
        txtv_address.setText(store.getAddress());
        txtv_opentime.setText(store.getOpentime());
        txtv_phonenumb.setText(store.getPhonenumb());
        getCustomUser(store.getUserID());
        if (store.getImgBitmap() == null) {
            if (!store.getStoreimg().equals("")) {
                StorageReference imageRef = stRef.child(store.getStoreimg());
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getApplicationContext())
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
        if (store.getSize() != 0) {
            txtv_pricerate.setText(store.getPriceSum() / store.getSize() + "");
            txtv_servicerate.setText(store.getServiceSum() / store.getSize() + "");
            txtv_healthyrate.setText(store.getHealthySum() / store.getSize() + "");
        } else {
            txtv_pricerate.setText("0");
            txtv_servicerate.setText("0");
            txtv_healthyrate.setText("0");
        }
        getPostList();
        getImageList();
        getFoodList();
    }
    public  Map<String, Object>  changePostStatus(Map<String, Object> childUpdate, boolean status){
        for (Post post:posts){
            if(post.getPostType()==1 ||post.getPostType()==2) {
                post.setHidden(status);
                Map<String, Object> map = post.toMap();
                childUpdate.put(getString(R.string.posts_CODE) + post.getPostID(), map);
            }
        }
        return childUpdate;
    }
    public  Map<String, Object>  changeFoodStatus(Map<String, Object> childUpdate, boolean status){
        for (Food food:foods){
            if(food.getFoodType()==1 ||food.getFoodType()==2) {
                food.setHidden(status);
                Map<String, Object> map = food.toMap();
                childUpdate.put(getString(R.string.food_CODE) + food.getFoodID(), map);
            }
        }
        return childUpdate;
    }
    private void getPostList() {
        postValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    Post post = dataItem.getValue(Post.class);
                    String key = dataItem.getKey();
                    post.setPostID(key);
                    if (checkExistPost(key) != -1) {
                        posts.set(checkExistPost(key), post);
                    } else {
                        posts.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.posts_CODE))
                .orderByChild("isHidden_storeID")
                .equalTo(false + "_" + storeID)
                .addValueEventListener(postValueListener);
    }

    private int checkExistPost(String id) {
        for (Post post : posts) {
            if (post.getPostID().equals(id)) {
                return posts.indexOf(post);
            }
        }
        return -1;
    }

    private int checkExistFood(String id) {
        for (Food food : foods) {
            if (food.getFoodID().equals(id)) {
                return foods.indexOf(food);
            }
        }
        return -1;
    }

    private int checkExistImage(String id) {
        for (Image image : images) {
            if (image.getImageID().equals(id)) {
                return images.indexOf(image);
            }
        }
        return -1;
    }

    private void getImageList() {
        photoValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Image image = item.getValue(Image.class);
                    String key = item.getKey();
                    image.setImageID(key);
                    if (checkExistImage(key) != -1) {
                        images.set(checkExistImage(key), image);
                    } else {
                        images.add(image);
                    }
                }
                ChoosePhotoList.getInstance().setImage(images);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.images_CODE))
                .orderByChild("isHidden_storeID")
                .equalTo(false + "_" + storeID)
                .addValueEventListener(photoValueListener);
    }

    private void getFoodList() {
        int role = 0;
        if (LoginSession.getInstance().getUser() != null) {
            role = LoginSession.getInstance().getUser().getRole();
        }
        foodValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    Food food = dataItem.getValue(Food.class);
                    String key = dataItem.getKey();
                    food.setFoodID(key);
                    if (checkExistFood(key) != -1) {
                        foods.set(checkExistFood(key), food);
                    } else {
                        foods.add(food);
                    }
                }
                foodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        if (role > 0) {
            dbRef.child(getString(R.string.food_CODE))
                    .orderByChild("storeID")
                    .equalTo(storeID)
                    .addValueEventListener(foodValueListener);
        } else {
            dbRef.child(getString(R.string.food_CODE))
                    .orderByChild("isHidden_storeID")
                    .equalTo(false + "_" + storeID)
                    .addValueEventListener(foodValueListener);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgv_writepost_storedetail:
                if (LoginSession.getInstance().getUser() != null) {
                    if (LoginSession.getInstance().getUser().isWritepostBlocked()) {
                        AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), getString(R.string.text_block_user));
                    } else {
                        writePost();
                    }
                } else {
                    requestSignin();
                }
                break;
            case R.id.imgv_addfood_storedetail:
                if (LoginSession.getInstance().getUser() != null) {
                    if (LoginSession.getInstance().getUser().isAddfoodBlocked()) {
                        AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), getString(R.string.text_block_user));
                    } else {
                        addFood();
                    }
                } else {
                    requestSignin();
                }
                break;
            case R.id.imgv_viewlocation_storedetail:
                Bitmap imgBitmap = ((BitmapDrawable) imgv_avatar.getDrawable())
                        .getBitmap();
                store.setImgBitmap(imgBitmap);
                ChooseStore.getInstance().setStore(store);
                if (!MyService.canGetLocation(this)) {
                    AppUtils.showSnackbar(this, getWindow().getDecorView(), "Bật GPS để sử dụng chức năng này", "Bật GPS", Const.SNACKBAR_TURN_ON_GPS, Snackbar.LENGTH_SHORT);
                } else {
                    Intent intent2 = new Intent(StoreDeatailActivity.this, MapActivity.class);
                    intent2.putExtra(getString(R.string.fragment_CODE),
                            getString(R.string.frag_map_CODE));
                    intent2.putExtra("type", 1);
                    startActivity(intent2);
                }
                break;
            case R.id.fab_menu_storedetail:
                Intent intent_openmenu = new Intent(this, AdapterActivity.class);
                intent_openmenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent_openmenu.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frag_menu_CODE));
                intent_openmenu.putExtra(getString(R.string._ID), storeID);
                startActivity(intent_openmenu);
                break;
            default:
                return;
        }

    }
    private void editStore() {

        Intent intent_editStore = new Intent(this, AdapterActivity.class);
        intent_editStore.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent_editStore.putExtra(getString(R.string.fragment_CODE),
                getString(R.string.frag_addstore_CODE));
        intent_editStore.putExtra("STORE",store);
        startActivity(intent_editStore);
    }
    private void writePost() {
        ChooseStore.getInstance().setStore(store);
        Intent intent_writepost = new Intent(this, AdapterActivity.class);
        intent_writepost.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent_writepost.putExtra(getString(R.string.fragment_CODE),
                getString(R.string.frag_writepost_CODE));
        startActivity(intent_writepost);
    }

    private void addFood() {
        AddFoodDialog addFoodDialog = new AddFoodDialog();
        addFoodDialog.setStyle(DialogFragment.STYLE_NORMAL,
                R.style.AddfoodDialog);
        addFoodDialog.setStore(store);
        addFoodDialog.show(getSupportFragmentManager(), "addfood_frag");
    }

    private void reportStore() {
        ReportDialog reportDialog = new ReportDialog();
        store.setStoreType(-1);
        store.setHidden(true);
        Map<String, Object> updateStore = store.toMap();
        Map<String, Object> childUpdate = new HashMap<>();
        childUpdate.put(getString(R.string.store_CODE) + store.getStoreID(), updateStore);
        reportDialog.setChildUpdate(childUpdate);
        reportDialog.setReport(REPORT_STORE, store);
        reportDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AddfoodDialog);
        reportDialog.setOnPosNegListener(new ReportDialog.OnPosNegListener() {
            @Override
            public void onPositive(boolean isClicked, Map<String,
                    Object> childUpdate, final Dialog dialog, String key) {
                if (isClicked) {
                    dialog.dismiss();
                    keyReport = key;
                    childUpdate = notificationToUser(childUpdate, 3, 1);
                    plzw8Dialog.show();
                    dbRef.updateChildren(childUpdate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    plzw8Dialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.show();
                            plzw8Dialog.dismiss();
                            Toast.makeText(StoreDeatailActivity.this, e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }

            @Override
            public void onNegative(boolean isClicked, Dialog dialog) {
                if (isClicked) {
                    dialog.dismiss();
                }
            }
        });
        reportDialog.show(getSupportFragmentManager(), "report_store");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SIGNIN:
                if (resultCode == RESULT_OK) {
                    getUser();
                } else {

                }
                break;
        }
    }

    private void getUser() {
        plzw8Dialog.show();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // User is signed in
                    getUserInfo(firebaseUser);
                    Log.d("onAuthStateChanged", "onAuthStateChanged:signed_in:" + firebaseUser.getUid());
                } else {
                    // User is signed out
                    Log.d("onAuthStateChanged", "onAuthStateChanged:signed_out");
                    plzw8Dialog.dismiss();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void getUserInfo(final FirebaseUser firebaseUser) {
        userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                String key = firebaseUser.getUid();
                user.setuID(key);
                LoginSession.getInstance().setUser(user);
                LoginSession.getInstance().setFirebUser(firebaseUser);
                mAuth.removeAuthStateListener(mAuthListener);
                loadMenu();
                plzw8Dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef.child(getString(R.string.users_CODE)
                + firebaseUser.getUid())
                .addListenerForSingleValueEvent(userValueListener);
    }

    private void getCustomUser(final String key) {
        userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ownUser = dataSnapshot.getValue(User.class);
                ownUser.setuID(key);
                txt_user.setText(ownUser.getUn());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef.child(getString(R.string.users_CODE)
                + key)
                .addListenerForSingleValueEvent(userValueListener);

    }
    private void getStore(final String key) {
        try {

            ValueEventListener storeEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        store = dataSnapshot.getValue(Store.class);
                        store.setStoreID(dataSnapshot.getKey());
                    createStoreInfo();
                    loadMenu();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
                dbRef.child(getString(R.string.store_CODE)+key).addValueEventListener(storeEventListener);

        }catch (Exception e){

        }
    }
}
