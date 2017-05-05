package com.app.ptt.comnha.Activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.ptt.comnha.Adapters.Food_recycler_adapter;
import com.app.ptt.comnha.Adapters.Photo_recycler_adapter;
import com.app.ptt.comnha.Adapters.Post_recycler_adapter;
import com.app.ptt.comnha.Fragment.AddFoodFragment;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Image;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChooseFood;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
import com.app.ptt.comnha.Utils.AppUtils;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StoreDeatailActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView postRecycler, photoRecycler, foodRecycler;
    RecyclerView.LayoutManager postLayoutManager, photoLayoutManager,
            foodLayoutManager;
    Post_recycler_adapter postAdapter;
    Photo_recycler_adapter photoAdapter;
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
    ChildEventListener postChildListener, photoChildListener,
            foodChildListener;

    Store store = ChooseStore.getInstance().getStore();
    LinearLayout linear_progress;
    String storeID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_deatail);
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebase_path));
        stRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseStorage_path));
        ref();

        if (store == null) {
            finish();
        } else {
            storeID = store.getStoreID();
            createStoreInfo();
        }
    }

    private void ref() {
        View include_view = findViewById(R.id.include_content);
        postRecycler = (RecyclerView) include_view.findViewById(R.id.recycler_post_storedetail);
        postLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, true);
        postRecycler.setLayoutManager(postLayoutManager);
        posts = new ArrayList<>();
        postAdapter = new Post_recycler_adapter(posts, this, stRef);
        postRecycler.setAdapter(postAdapter);
        photoRecycler = (RecyclerView) include_view.findViewById(R.id.recycler_photo_storedetail);
        photoLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, true);
        photoRecycler.setLayoutManager(photoLayoutManager);
        images = new ArrayList<>();
        photoAdapter = new Photo_recycler_adapter(images, stRef, this);
        photoRecycler.setAdapter(photoAdapter);
        photoAdapter.setOnItemClickLiestner(new Photo_recycler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(Image image, Activity activity) {
                Intent intent_openPhoto = new Intent(activity, ViewPhotoActivity.class);
                intent_openPhoto.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_openPhoto);
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
        toolbar.setTitle("");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        ((AppCompatActivity) this).setSupportActionBar(toolbar);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu = AppUtils.createMenu(menu, new String[]{
                getString(R.string.txt_report),
                getString(R.string.txt_followStore),
                getString(R.string.text_hidestore),
                getString(R.string.txt_changeinfo)});
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                return true;
            case 1:
                return true;
            case 2:
                return true;
            case 3:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

            }else {
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

        postChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                String key = dataSnapshot.getKey();
                post.setPostID(key);
                posts.add(post);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        photoChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Image image = dataSnapshot.getValue(Image.class);
                String key = dataSnapshot.getKey();
                image.setImageID(key);
                images.add(image);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        foodChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Food food = dataSnapshot.getValue(Food.class);
                String key = dataSnapshot.getKey();
                food.setFoodID(key);
                foods.add(food);
                foodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.food_CODE))
                .orderByChild("storeID")
                .equalTo(storeID)
                .addChildEventListener(foodChildListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgv_writepost_storedetail:
                Intent intent_writepost = new Intent(this, AdapterActivity.class);
                intent_writepost.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent_writepost.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frag_writepost_CODE));
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

    @Override
    protected void onStop() {
        super.onStop();
        dbRef.removeEventListener(foodChildListener);
        dbRef.removeEventListener(photoChildListener);
        dbRef.removeEventListener(postChildListener);
    }
}
