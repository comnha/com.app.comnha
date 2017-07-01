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
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Interfaces.Comunication;
import com.app.ptt.comnha.Interfaces.SendLocationListener;
import com.app.ptt.comnha.Models.FireBase.NewpostNotify;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.app.ptt.comnha.SingletonClasses.CoreManager;
import com.app.ptt.comnha.Utils.AppUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.facebook.login.widget.ProfilePictureView.TAG;


public class MainPostFragment extends Fragment implements SendLocationListener {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Post> posts;
    Post_recycler_adapter postadapter;
    DatabaseReference dbRef;
    ValueEventListener postsEventListener;
    String pro_dist;
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
                .getReferenceFromUrl(Const.DATABASE_PATH);
        stRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Const.STORAGE_PATH);
        ref(view);

        getPostList(pro_dist);

        if (null != CoreManager.getInstance().getMyLocation()) {
            pro_dist=CoreManager.getInstance().getMyLocation().getDistrict()
                    +"_"+CoreManager.getInstance().getMyLocation().getProvince();
            Log.d(TAG, "pro_dist: " + pro_dist);

            getPostList(pro_dist);
        } else {
            if (getView() != null)
                AppUtils.showSnackbarWithoutButton(getView(), "Không tìm thấy vị trí của bạn");
        }
        Comunication.sendLocationListener = this;
        return view;
    }


    private void getPostList(final String pro_dist) {
        posts.clear();
        postsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    String key = item.getKey();
                    Post post = item.getValue(Post.class);
                    post.setPostID(key);
                    int pos=-1;
                    for(Post mPost:posts){
                        if(mPost.getPostID().equals(key)){
                            pos=posts.indexOf(mPost);
                        }
                    }
                    if(pos!=-1){
                        posts.set(pos,post);
                    }else {
                        posts.add(post);
                    }

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
                .equalTo(false + "_" + pro_dist)

                .addValueEventListener(postsEventListener);

    }

    @Override
    public void notice() {
        if (null != CoreManager.getInstance().getMyLocation()) {
            pro_dist = CoreManager.getInstance().getMyLocation().getProvince() + "_" + CoreManager.getInstance().getMyLocation().getDistrict();
            getPostList(pro_dist);
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
                getPostList(pro_dist);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
}
