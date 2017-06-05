package com.app.ptt.comnha.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ptt.comnha.Activity.PostdetailActivity;
import com.app.ptt.comnha.Adapters.Post_recycler_adapter;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class MainPostFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Post> posts;
    Post_recycler_adapter postadapter;
    DatabaseReference dbRef;
    ValueEventListener postsEventListener;
    String dist_pro = "Quận 9_HCM";
    StorageReference stRef;
    SwipeRefreshLayout swipeRefresh;

    public MainPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MainFragmentPage", "createviewPost");
        View view = inflater.inflate(R.layout.fragment_main_post, container, false);
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebase_path));
        stRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseStorage_path));
        ref(view);
        getPostList();
        return view;
    }

    private void getPostList() {
        int role = LoginSession.getInstance().getUser().getRole();
        if (role == 1) {
            postsEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        String key = item.getKey();
                        Post post = item.getValue(Post.class);
                        post.setPostID(key);
                        posts.add(post);
                    }
                    postadapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            dbRef.child(getString(R.string.posts_CODE))
                    .orderByChild("pro_dist")
                    .equalTo(dist_pro)
                    .addListenerForSingleValueEvent(postsEventListener);
        } else {
            postsEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        String key = item.getKey();
                        Post post = item.getValue(Post.class);
                        post.setPostID(key);
                        posts.add(post);
                    }
                    postadapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            dbRef.child(getString(R.string.posts_CODE))
                    .orderByChild("isHidden_dist_prov")
                    .equalTo(false + "_" + dist_pro)
                    .addListenerForSingleValueEvent(postsEventListener);
        }
    }

    private void ref(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerV_postfrag);
        layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        posts = new ArrayList<>();
        postadapter = new Post_recycler_adapter(posts, getContext(), stRef);
        mRecyclerView.setAdapter(postadapter);
        postadapter.setOnItemClickLiestner(new Post_recycler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(Post post, View itemView) {
                Intent intent_postdetail = new Intent(getContext(),
                        PostdetailActivity.class);
                ActivityOptionsCompat option_postbanner
                        = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(), itemView.findViewById(R.id.imgv_banner_postitem),
                        "postBanner");
                ChoosePost.getInstance().setPost(post);
//                Toast.makeText(getContext(),
//                        selected_store.getName() + "", Toast.LENGTH_SHORT).show();
                startActivity(intent_postdetail, option_postbanner.toBundle());
            }
        });
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_postfrag);
        swipeRefresh.setColorSchemeResources(R.color.admin_color_selection_news,
                R.color.color_selection_report);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                posts.clear();
                getPostList();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
