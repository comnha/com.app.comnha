package com.app.ptt.comnha.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Post_recycler_adapter;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChooseFood;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FooddetailFragment extends Fragment {
    private static final String LOG = FooddetailFragment.class.getSimpleName();
    DatabaseReference dbRef;
    StorageReference stRef;
    ArrayList<Food> foodList;
    RecyclerView postRecyclerView;
    Post_recycler_adapter postAdapter;
    RecyclerView.LayoutManager postLayoutManager;
    TextView txt_name, txt_price, txt_comment;
    ImageView imgv_photo;
    RatingBar ratingBar;
    ArrayList<Post> postlist;
    ChildEventListener postChildListener;
    ActionBar actionBar;
    Toolbar toolbar;
    Food food = ChooseFood.getInstance().getFood();
    String storeID = "", foodID = "";
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    boolean isConnected = false;
    IntentFilter mIntentFilter;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastSendAddress)) {
                Log.i(LOG + ".onReceive form Service", "isConnected= " + intent.getBooleanExtra("isConnected", false));
                if (intent.getBooleanExtra("isConnected", false)) {
                    if (!isConnected)
                        getData();
                    isConnected = true;
                } else
                    isConnected = false;
            }
        }
    };

    public FooddetailFragment() {
        // Required empty public constructor
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
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fooddetail, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        isConnected = MyService.returnIsNetworkConnected();
//        locaID = ChooseFood.getInstance().getStore().getLocaID();
        stRef = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebaseStorage_path));
        if (food != null) {
            storeID = food.getStoreID();
            foodID = food.getFoodID();
            ref(view);
            getData();
            return view;
        }


        return null;
    }

    public void getData() {
        txt_comment.setText(food.getComment());
        txt_name.setText(food.getName());
        txt_price.setText(food.getPrice() + "");
        ratingBar.setRating(food.getTotal() == 0 ? 0 : food.getRating() / food.getTotal());
        StorageReference imgRef = stRef.child(food.getFoodImg());
        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getContext())
                        .load(uri)
                        .into(imgv_photo);
            }
        });
        postChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                post.setPostID(dataSnapshot.getKey());
                postlist.add(post);
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

        dbRef.child(
                getResources().getString(R.string.posts_CODE))
                .orderByChild("foodID")
                .equalTo(foodID)
                .addChildEventListener(postChildListener);

    }

    private void ref(View view) {
        txt_name = (TextView) view.findViewById(R.id.txtv_name_fooddetail);
        txt_price = (TextView) view.findViewById(R.id.txtv_price_fooddetail);
        txt_comment = (TextView) view.findViewById(R.id.txtv_comment_fooddetail);
        imgv_photo = (ImageView) view.findViewById(R.id.imgv_photo_fooddetail);
        ratingBar = (RatingBar) view.findViewById(R.id.rb_rating_fooddetail);
        ratingBar.setIsIndicator(true);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_fooddetail);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        toolbar.setTitle(getString(R.string.txt_fooddetail));

        postlist = new ArrayList<>();
        postRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_post_fooddetail);
        postRecyclerView.setHasFixedSize(true);
        foodList = new ArrayList<>();
        postLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL,
                false);
        postRecyclerView.setLayoutManager(postLayoutManager);
        postAdapter = new Post_recycler_adapter(postlist, getActivity(), stRef);
        postRecyclerView.setAdapter(postAdapter);


        postAdapter.setOnItemClickLiestner(new Post_recycler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(Post post) {

            }
        });
//                    if (LoginSession.getInstance().getUserID() == null) {
//                        Toast.makeText(getActivity(), getString(R.string.txt_needlogin),
//                                Toast.LENGTH_SHORT).show();
//                    } else {
//        Intent intent = new Intent(getActivity(), AdapterActivity.class);
//        intent.putExtra(getString(R.string.fragment_CODE), getString(R.string.frg_viewpost_CODE));
////                    ChoosePost.getInstance().setPostID(postlist.get(position).getPostID());
//        // ChoosePost.getInstance().setTinh(tinh);
//        // ChoosePost.getInstance().setHuyen(huyen);
//        startActivity(intent);
//                    }
//                } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.txt_reportfood));
        menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.txt_updatefood));
        menu.add(Menu.NONE, 2, Menu.NONE, getString(R.string.text_delfood));
        super.onCreateOptionsMenu(menu, inflater);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    private class Store {
    }
}
