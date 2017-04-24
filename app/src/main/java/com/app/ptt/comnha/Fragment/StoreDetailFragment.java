package com.app.ptt.comnha.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.ptt.comnha.Activity.AdapterActivity;
import com.app.ptt.comnha.Activity.ViewPhotoActivity;
import com.app.ptt.comnha.Adapters.Photo_recycler_adapter;
import com.app.ptt.comnha.Adapters.Post_recycler_adapter;
import com.app.ptt.comnha.Models.FireBase.Image;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by PTT on 4/20/2017.
 */

public class StoreDetailFragment extends Fragment implements View.OnClickListener {
    RecyclerView postRecycler, photoRecycler;
    RecyclerView.LayoutManager postLayoutManager, photoLayoutManager;
    Post_recycler_adapter postAdapter;
    Photo_recycler_adapter photoAdapter;
    ArrayList<Post> posts;
    ArrayList<Image> images;

    TextView txtv_storename, txtv_address, txtv_opentime, txtv_phonenumb,
            txtv_pricerate, txtv_healthyrate, txtv_servicerate;
    Toolbar toolbar;
    ImageView imgv_writepost, imgv_addfood, imgv_viewlocation,
            imgv_avatar;
    FloatingActionButton fab;

    DatabaseReference dbRef;
    StorageReference stRef;
    ChildEventListener postChildListener, photoChildListener;

    Store store = ChooseStore.getInstance().getStore();
    LinearLayout linear_progress;
    String storeID = store.getStoreID();

    public StoreDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storedetail, container, false);
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebase_path));
        stRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseStorage_path));
        ref(view);
        if (store == null) {
            getActivity().finish();
        }
        return view;
    }

    private void ref(View view) {
        View include_view = view.findViewById(R.id.include_content);
        postRecycler = (RecyclerView) include_view.findViewById(R.id.recycler_post_storedetail);
        postLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, true);
        postRecycler.setLayoutManager(postLayoutManager);
        posts = new ArrayList<>();
        postAdapter = new Post_recycler_adapter(posts, getActivity(), stRef);
        postRecycler.setAdapter(postAdapter);
        photoRecycler = (RecyclerView) include_view.findViewById(R.id.recycler_photo_storedetail);
        photoLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, true);
        photoRecycler.setLayoutManager(photoLayoutManager);
        images = new ArrayList<>();
        photoAdapter = new Photo_recycler_adapter(images, stRef, getActivity());
        photoRecycler.setAdapter(photoAdapter);
        photoAdapter.setOnItemClickLiestner(new Photo_recycler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(Image post) {
                Intent intent_openPhoto = new Intent(getContext(), ViewPhotoActivity.class);
                intent_openPhoto.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_openPhoto);
            }
        });

        txtv_storename = (TextView) include_view.findViewById(R.id.txtv_storename_storedetail);
        txtv_address = (TextView) include_view.findViewById(R.id.txtv_address_storedetail);
        txtv_opentime = (TextView) include_view.findViewById(R.id.txtv_opentime_storedetail);
        txtv_phonenumb = (TextView) include_view.findViewById(R.id.txtv_phonenumb_storedetail);
        txtv_pricerate = (TextView) include_view.findViewById(R.id.txtv_price_storedetail);
        txtv_healthyrate = (TextView) include_view.findViewById(R.id.txtv_healthy_storedetail);
        txtv_servicerate = (TextView) include_view.findViewById(R.id.txtv_service_storedetail);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_storedetail);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(getContext().getResources().getColor(android.R.color.white));
        setHasOptionsMenu(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        imgv_writepost = (ImageView) view.findViewById(R.id.imgv_writepost_storedetail);
        imgv_addfood = (ImageView) view.findViewById(R.id.imgv_addfood_storedetail);
        imgv_viewlocation = (ImageView) view.findViewById(R.id.imgv_viewlocation_storedetail);
        imgv_avatar = (ImageView) view.findViewById(R.id.imgv_avatar_storedetail);
        fab = (FloatingActionButton) view.findViewById(R.id.fab_menu_storedetail);
        imgv_writepost.setOnClickListener(this);
        imgv_addfood.setOnClickListener(this);
        imgv_viewlocation.setOnClickListener(this);
        fab.setOnClickListener(this);
        linear_progress = (LinearLayout) view.findViewById(R.id.linear_progress_storedetail);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.txt_reportStore));
        menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.txt_followtStore));
//        inflater.inflate(R.menu.menu_item_notify, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                return true;
            case 1:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
    }

    @Override
    public void onStart() {
        super.onStart();
        createStoreInfo();
    }

    private void createStoreInfo() {
        txtv_storename.setText(store.getName());
        txtv_address.setText(store.getAddress());
        txtv_opentime.setText(store.getOpentime());
        txtv_phonenumb.setText(store.getPhonenumb());
        if (store.getSize() != 0) {
            txtv_pricerate.setText(store.getPriceSum() / store.getSize() + "");
            txtv_servicerate.setText(store.getServiceSum() / store.getSize() + "");
            txtv_healthyrate.setText(store.getHealthySum() / store.getSize() + "");
        } else {
            txtv_pricerate.setText("0");
            txtv_servicerate.setText("0");
            txtv_healthyrate.setText("0");
        }
        if (!store.getStoreimg().equals("")) {
            StorageReference imageRef = stRef.child(store.getStoreimg());
            Glide.with(getActivity())
                    .using(new FirebaseImageLoader())
                    .load(imageRef)
                    .placeholder(R.color.colorPrimary)
                    .into(imgv_avatar);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgv_writepost_storedetail:
                Intent intent_writepost = new Intent(getContext(), AdapterActivity.class);
                intent_writepost.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent_writepost.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frag_writepost_CODE));
                startActivity(intent_writepost);
                break;
            case R.id.imgv_addfood_storedetail:
                break;
            case R.id.imgv_viewlocation_storedetail:

                break;
            case R.id.fab_menu_storedetail:
                Intent intent_openmenu = new Intent(getContext(), AdapterActivity.class);
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
}
