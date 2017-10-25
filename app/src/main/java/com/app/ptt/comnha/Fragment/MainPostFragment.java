package com.app.ptt.comnha.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ptt.comnha.Activity.PostdetailActivity;
import com.app.ptt.comnha.Adapters.Post_recycler_adapter;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Interfaces.OnLoadMoreListener;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.app.ptt.comnha.SingletonClasses.CoreManager;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.AppUtils;
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
    LinearLayoutManager layoutManager;
    ArrayList<Post> posts;
    Post_recycler_adapter postadapter;
    DatabaseReference dbRef;
    ValueEventListener postsEventListener;
    String dist_pro;
    boolean isRegister;
    int stt = 6;
    boolean isLoadMore;
    StorageReference stRef;
    SwipeRefreshLayout swipeRefresh;
    int itemCount = 0, typeSort = -1, count = 6;
    IntentFilter mIntentFilter;
    Intent broadcastIntent;
    LocationChange mBroadcastReceiver;

    public MainPostFragment() {
        // Required empty public constructor
    }

    public void setAttribute(String tinh, String huyen) {
        // Required empty public constructor
        dist_pro = huyen + "_" + tinh;
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


        return view;
    }

    public void addMore(int pos) {
        if (!isLoadMore) {
            postadapter.setIsLoading(false);
            postadapter.clearList();
            if (posts.size() <= stt||stt<=0) {
                stt = posts.size();
                postadapter.setMoreDataAvailable(false);
            } else {
                postadapter.setMoreDataAvailable(true);
            }
            for (int i = 0; i < stt; i++) {
                final int finalI = i;
                postadapter.addToList(posts.get(finalI));

            }
            if (typeSort != -1) {
                postadapter.sortByType(typeSort);
            }

        } else {
            final int tempCount = pos;
            itemCount = pos + count;
            if (posts.size() < itemCount) {
                if (posts.size() > tempCount) {
                    itemCount = posts.size();
                } else {
                    itemCount = 0;
                }

            } else {

            }
            if (tempCount < itemCount) {
                for (int i = tempCount; i < itemCount; i++) {
                    final int finalI = i;
                    postadapter.addToList(posts.get(finalI));
                }

                if (typeSort != -1) {
                    postadapter.sortByType(typeSort);
                }
                //
                stt=itemCount;
                if (posts.size() == postadapter.getSize()) {
                    postadapter.setMoreDataAvailable(false);
                } else {
                    postadapter.setMoreDataAvailable(true);
                }


            } else {
                postadapter.setMoreDataAvailable(false);

            }
            isLoadMore = false;
            postadapter.setIsLoading(false);
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.scrollToPosition(postadapter.getSize() - 1);
                }
            });

        }


    }

    private void getPostList(final String dist_pro) {
        try {
            posts.clear();
            int role = 0;
            if (LoginSession.getInstance().getUser() != null) {
                role = LoginSession.getInstance().getUser().getRole();
            }
            postsEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        String key = item.getKey();
                        Post post = item.getValue(Post.class);
                        post.setPostID(key);
                        int pos = -1;
                        for (Post mPost : posts) {
                            if (mPost.getPostID().equals(key)) {
                                pos = posts.indexOf(mPost);
                            }
                        }
                        if (pos != -1) {
                            posts.set(pos, post);
                        } else {
                            posts.add(post);
                        }

                    }
                    addMore(0);
                    swipeRefresh.setRefreshing(false);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            if (role != 0) {
                dbRef.child(getString(R.string.posts_CODE))
                        .orderByChild("dist_pro")
                        .equalTo(dist_pro)
                        .addValueEventListener(postsEventListener);
            } else {
                dbRef.child(getString(R.string.posts_CODE))
                        .orderByChild("isHidden_dist_prov")
                        .equalTo(false + "_" + dist_pro)
                        .addValueEventListener(postsEventListener);
            }
        } catch (Exception e) {

        }
    }

    private void ref(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerV_postfrag);
        layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        posts = new ArrayList<>();
        postadapter = new Post_recycler_adapter(getContext(), stRef);
        mRecyclerView.setAdapter(postadapter);
        postadapter.setLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                int index = postadapter.getSize();
                onLoadMore(index);
            }
        });
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
                typeSort = -1;
                stt = 6;
                sendBroadcastSortPostReset();
                getPostList(dist_pro);
            }
        });
    }

    private void sendBroadcastSortPostReset(){
        broadcastIntent = new Intent();
        broadcastIntent.setAction(Const.INTENT_KEY_SORT_POST_TYPE);
        getActivity().sendBroadcast(broadcastIntent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    public void onLoadMore(final int pos) {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                postadapter.addToList(null);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                postadapter.removeLastItemFromList();
                isLoadMore = true;
                addMore(pos);
            }
        }, 3000);
    }

    @Override
    public void onResume() {
        super.onResume();
        mIntentFilter = new IntentFilter(Const.INTENT_KEY_RECEIVE_LOCATION);
        mIntentFilter.addAction(Const.INTENT_KEY_SORT_POST);
        mIntentFilter.addAction(Const.INTENT_KEY_RELOAD_DATA);
        mBroadcastReceiver = new LocationChange();
        isRegister=true;
        broadcastIntent = new Intent();
        getActivity().registerReceiver(mBroadcastReceiver, mIntentFilter);
        if (null != CoreManager.getInstance().getMyLocation()) {
            if (TextUtils.isEmpty(CoreManager.getInstance().getHuyen()) && TextUtils.isEmpty(CoreManager.getInstance().getTinh())) {
                dist_pro = CoreManager.getInstance().getMyLocation().getDistrict()
                        + "_" + CoreManager.getInstance().getMyLocation().getProvince();
            } else {
                dist_pro = CoreManager.getInstance().getHuyen()
                        + "_" + CoreManager.getInstance().getTinh();
            }
            getPostList(dist_pro);
        } else {
            if (getView() != null)
                AppUtils.showSnackbarWithoutButton(getView(), "Không tìm thấy vị trí của bạn");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mBroadcastReceiver != null &&isRegister) {
                getActivity().unregisterReceiver(mBroadcastReceiver);
                isRegister=false;
            }
        }catch (Exception e){

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (mBroadcastReceiver != null&&isRegister) {
                getActivity().unregisterReceiver(mBroadcastReceiver);
                isRegister=false;
            }
        }catch (Exception e){

        }
    }

    class LocationChange extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Const.INTENT_KEY_RECEIVE_LOCATION:
                    if (intent.getStringExtra(Const.KEY_TINH)!= null) {
                        if (intent.getStringExtra(Const.KEY_HUYEN)!= null) {
                            dist_pro = intent.getStringExtra(Const.KEY_HUYEN).toString()
                                    + "_" + intent.getStringExtra(Const.KEY_TINH).toString();
                            stt = 6;
                            getPostList(dist_pro);
                        }
                    }
                    break;
                case Const.INTENT_KEY_RELOAD_DATA:
                    getPostList(dist_pro);
                    break;
                case Const.INTENT_KEY_SORT_POST:
                    if (intent.getIntExtra(Const.KEY_SORT, 0) != 0) {
                        typeSort = intent.getIntExtra(Const.KEY_SORT, 0);
                        postadapter.sortByType(typeSort);
                    }
                    break;
            }
        }

    }
}
