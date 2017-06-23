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
import com.app.ptt.comnha.Dialog.ReportDialog;
import com.app.ptt.comnha.Fragment.AddFoodFragment;

import com.app.ptt.comnha.Fragment.MapFragment;
import com.app.ptt.comnha.Interfaces.Comunication;
import com.app.ptt.comnha.Interfaces.SendLocationListener;

import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Image;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChooseFood;
import com.app.ptt.comnha.SingletonClasses.ChoosePhotoList;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;

import com.app.ptt.comnha.SingletonClasses.CoreManager;

import com.app.ptt.comnha.SingletonClasses.LoginSession;

import com.app.ptt.comnha.Utils.AppUtils;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class StoreDeatailActivity extends AppCompatActivity implements View.OnClickListener, SendLocationListener {
    RecyclerView postRecycler, photoRecycler, foodRecycler;
    RecyclerView.LayoutManager postLayoutManager, photoLayoutManager,
            foodLayoutManager;
    Post_recycler_adapter postAdapter;
    Photo_recycler_adapter photoAdapter = null;
    Food_recycler_adapter foodAdapter;
    ArrayList<Post> posts;
    ArrayList<Image> images;
    ArrayList<Food> foods;
    TextView txtv_storename, txtv_address, txtv_opentime, txtv_phonenumb,
            txtv_pricerate, txtv_healthyrate, txtv_servicerate;
    Toolbar toolbar;
    ImageView imgv_writepost, imgv_addfood, imgv_viewlocation;
    CircularImageView imgv_avatar;
    FloatingActionButton fab;

    DatabaseReference dbRef;
    StorageReference stRef;
    ValueEventListener postValueListener, photoValueListener, foodValueListener;

    Store store = ChooseStore.getInstance().getStore();
    LinearLayout linear_progress;
    String storeID = null;
    ProgressDialog plzw8Dialog = null;
    Menu pubMenu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_deatail);
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebase_path));
        stRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseStorage_path));
        if (store == null) {
            onBackPressed();
        } else {
            storeID = store.getStoreID();
            ChooseStore.getInstance().setStore(null);
        }
        ref();
        createStoreInfo();
        Comunication.sendLocationListener=this;
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
//                Toast.makeText(getContext(),
//                        selected_store.getName() + "", Toast.LENGTH_SHORT).show();
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
                        AdapterActivity.class);
                ActivityOptionsCompat optionsCompat
                        = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, itemView.findViewById(R.id.item_rcyler_food_imgV),
                        "foodphoto");
                intent_openFood.putExtra(getString(R.string.fragment_CODE)
                        , getString(R.string.frag_foodetail_CODE));
                ChooseFood.getInstance().setFood(food);
                intent_openFood.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        if (role == 1) {
            contents.add(new Pair<Integer, String>
                    (R.string.txt_changeinfo, getString(R.string.txt_changeinfo)));
            contents.add(new Pair<Integer, String>
                    (R.string.txt_delstore, getString(R.string.txt_delstore)));
            if (store.isHidden()) {
                contents.add(new Pair<Integer, String>
                        (R.string.txt_showstore, getString(R.string.txt_showstore)));
            } else {
                contents.add(new Pair<Integer, String>
                        (R.string.text_hidestore, getString(R.string.text_hidestore)));
            }
        } else {
            contents.add(new Pair<Integer, String>
                    (R.string.txt_report, getString(R.string.txt_report)));
            contents.add(new Pair<Integer, String>
                    (R.string.txt_followStore, getString(R.string.txt_followStore)));
            if (store.getUserID().equals(uID)) {
                if (store.isHidden()) {
                    contents.add(new Pair<Integer, String>
                            (R.string.txt_showstore, getString(R.string.txt_showstore)));
                } else {
                    contents.add(new Pair<Integer, String>
                            (R.string.text_hidestore, getString(R.string.text_hidestore)));
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
                    new AlertDialog.Builder(StoreDeatailActivity.this)
                            .setMessage(getString(R.string.txt_needlogin))
                            .setPositiveButton(getString(R.string.text_signin), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    StoreDeatailActivity.this.finish();
                                }
                            }).setNegativeButton(getString(R.string.text_no),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
                return true;
            case R.string.txt_followStore:
                return true;
            case R.string.text_hidestore:
                if (!store.isHidden()) {
                    hideStore();
                }
                return true;
            case R.string.txt_showstore:
                if (store.isHidden()) {
                    showStore();
                }
                return true;
            case R.string.txt_delstore:

                return true;
            case R.string.txt_changeinfo:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showStore(final MenuItem item) {
        plzw8Dialog.show();
        store.setHidden(false);
        String key = store.getStoreID();
//        Toast.makeText(StoreDeatailActivity.this,
//                key, Toast.LENGTH_SHORT).show();
        Store childStore = store;
        Map<String, Object> storeValue = childStore.toMap();
        Map<String, Object> childUpdate = new HashMap<>();
        childUpdate.put(getString(R.string.store_CODE)
                + key, storeValue);
        dbRef.updateChildren(childUpdate)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                item.setTitle(getString(R.string.text_hidestore));
                                pubMenu.clear();
                                StoreDeatailActivity.this.onCreateOptionsMenu(pubMenu);
                                plzw8Dialog.cancel();
                            }
                        }).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        plzw8Dialog.cancel();
                        Toast.makeText(getApplicationContext(),
                                e.getMessage(), Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }

    private void hideStore() {
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
                                store.setHidden(true);
                                String key = store.getStoreID();
//                                Toast.makeText(StoreDeatailActivity.this,
//                                        key, Toast.LENGTH_SHORT).show();
                                Store childStore = store;
                                Map<String, Object> storeValue = childStore.toMap();
                                Map<String, Object> childUpdate = new HashMap<>();
                                childUpdate.put(getString(R.string.store_CODE)
                                        + key, storeValue);
                                dbRef.updateChildren(childUpdate)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        pubMenu.clear();
                                                        StoreDeatailActivity.this.onCreateOptionsMenu(pubMenu);
                                                        plzw8Dialog.cancel();
                                                    }
                                                }).addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
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

    private void getPostList() {
        postValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    Post post = dataItem.getValue(Post.class);
                    String key = dataItem.getKey();
                    post.setPostID(key);
                    posts.add(post);
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
                .addListenerForSingleValueEvent(postValueListener);
    }

    private void getImageList() {
        photoValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Image image = item.getValue(Image.class);
                    String key = item.getKey();
                    image.setImageID(key);
                    images.add(image);
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
                .addListenerForSingleValueEvent(photoValueListener);
    }

    private void getFoodList() {
        int role = 0;
        if (LoginSession.getInstance().getUser() != null) {
            role = LoginSession.getInstance().getUser().getRole();
        }
        if (role == 1) {
            foodValueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                        Food food = dataItem.getValue(Food.class);
                        String key = dataItem.getKey();
                        food.setFoodID(key);
                        foods.add(food);
                    }
                    foodAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            dbRef.child(getString(R.string.food_CODE))
                    .orderByChild("storeID")
                    .equalTo(storeID)
                    .addListenerForSingleValueEvent(foodValueListener);
        } else {
            foodValueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                        Food food = dataItem.getValue(Food.class);
                        String key = dataItem.getKey();
                        food.setFoodID(key);
                        foods.add(food);
                    }
                    foodAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            dbRef.child(getString(R.string.food_CODE))
                    .orderByChild("isHidden_storeID")
                    .equalTo(false + "_" + storeID)
                    .addListenerForSingleValueEvent(foodValueListener);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgv_writepost_storedetail:
                Intent intent_writepost = new Intent(this, AdapterActivity.class);
                intent_writepost.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent_writepost.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frag_writepost_CODE));
                ChooseStore.getInstance().setStore(store);
                startActivity(intent_writepost);
                break;
            case R.id.imgv_addfood_storedetail:
                AddFoodFragment addFoodFragment = new AddFoodFragment();
                addFoodFragment.setStyle(DialogFragment.STYLE_NORMAL,
                        R.style.AddfoodDialog);
                addFoodFragment.setStore(store);
                addFoodFragment.show(getSupportFragmentManager(), "addfood_frag");
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

    private void reportStore() {
        ReportDialog reportDialog = new ReportDialog();
        reportDialog.setReport(REPORT_STORE, store);
        reportDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AddfoodDialog);
        reportDialog.setOnPosNegListener(new ReportDialog.OnPosNegListener() {
            @Override
            public void onPositive(boolean isClicked, Map<String,
                    Object> childUpdate, final Dialog dialog) {
                if (isClicked) {
                    dialog.dismiss();
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
    public void notice() {
        getPostList();
        getImageList();
        getFoodList();
    }
}
