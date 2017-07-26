package com.app.ptt.comnha.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
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

import com.app.ptt.comnha.Activity.StoreDeatailActivity;
import com.app.ptt.comnha.Adapters.Store_recycler_adapter;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Interfaces.OnLoadMoreListener;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
import com.app.ptt.comnha.SingletonClasses.CoreManager;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.AppUtils;
import com.app.ptt.comnha.Utils.MyTool;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainStoreFragment extends Fragment {
    private final static String TAG = "MainStoreFragment";
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    Store_recycler_adapter itemadapter;
    List<Store> stores;
    DatabaseReference dbRef;
    ValueEventListener storeEventListener;
    String dist_pro;
    boolean isRegister;
    StorageReference stRef;
    SwipeRefreshLayout swipeRefresh;
    MyTool myTool;
    boolean isLoadMore;
    int stt=8;
    int itemCount = 0, typeSort = -1, count = 6;
    IntentFilter mIntentFilter;
    Intent broadcastIntent;
    LocationChange mBroadcastReceiver;


    public MainStoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MainFragmentPage", "createviewStore");
        View view = inflater.inflate(R.layout.fragment_main_store, container, false);
        dbRef = FirebaseDatabase.getInstance().
                getReferenceFromUrl(Const.DATABASE_PATH);
        stRef = FirebaseStorage.getInstance().getReferenceFromUrl(
                getString(R.string.firebaseStorage_path));
        ref(view);

        myTool = new MyTool(getActivity());

        return view;
    }

    private void getStoreList(final String dist_pro) {
        try {
            stores.clear();
            int role = 0;
            if (LoginSession.getInstance().getUser() != null) {
                role = LoginSession.getInstance().getUser().getRole();
            }
            storeEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        Store store = item.getValue(Store.class);
                        store.setStoreID(item.getKey());
                        int pos = -1;
                        for (Store mStore : stores) {
                            if (mStore.getStoreID().equals(store.getStoreID())) {

                                pos = stores.indexOf(mStore);
                                // stores.indexOf(mStore);
                            }
                        }
                        if (pos != -1) {
                            if (store.isHidden()) {
                                stores.remove(pos);
                            } else {
                                stores.set(pos, store);
                            }
                        } else {
                            stores.add(store);
                        }
                        Log.d("added", "added");
                    }
                    new calculateDistance().execute();
                    swipeRefresh.setRefreshing(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            if (role != 0) {
                dbRef.child(getString(R.string.store_CODE))
                        .orderByChild("pro_dist")
                        .equalTo(dist_pro)
                        .addValueEventListener(storeEventListener);
            } else {
                dbRef.child(getString(R.string.store_CODE))
                        .orderByChild("isHidden_dis_pro")
                        .equalTo(String.valueOf(false) + "_" + dist_pro)
                        .addValueEventListener(storeEventListener);
            }
        }catch (Exception e){

        }
    }

    public void addMore(int pos) {
        if (!isLoadMore) {
            itemadapter.setIsLoading(false);
            itemadapter.clearList();
            if (stores.size() <= stt || stt <= 0) {
                stt = stores.size();
                itemadapter.setMoreDataAvailable(false);
            } else {
                itemadapter.setMoreDataAvailable(true);
            }
            for (int i = 0; i < stt; i++) {
                final int finalI = i;
                itemadapter.addToList(stores.get(finalI));
            }
            if (typeSort != -1) {
                itemadapter.sortByType(typeSort);
            }
        } else {
            final int tempCount = pos;
            itemCount = pos + count;
            if (stores.size() < itemCount) {
                if (stores.size() > tempCount) {
                    itemCount = stores.size();
                } else {
                    itemCount = 0;
                }

            } else {

            }

            if (tempCount < itemCount) {
                for (int i = tempCount; i < itemCount; i++) {
                    final int finalI = i;
                    itemadapter.addToList(stores.get(finalI));
                }

                if (typeSort != -1) {
                    itemadapter.sortByType(typeSort);
                }
                stt=itemCount;
                if (stores.size() == itemadapter.getSize()) {
                    itemadapter.setMoreDataAvailable(false);
                } else {
                    itemadapter.setMoreDataAvailable(true);
                }


            } else {
                itemadapter.setMoreDataAvailable(false);

            }
            isLoadMore = false;
            itemadapter.setIsLoading(false);
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.smoothScrollToPosition(itemadapter.getSize() - 1);
                }
            });

        }


    }

    private void ref(final View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerV_storefrag);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        stores = new ArrayList<>();
        itemadapter = new Store_recycler_adapter(getContext(), stRef);
        itemadapter.setOnItemClickLiestner(new Store_recycler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(Store store, View itemView) {
                if(store!=null) {
                    Intent intent_storedetail = new Intent(getContext(),
                            StoreDeatailActivity.class);
                    intent_storedetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ChooseStore.getInstance().setStore(store);
                    ActivityOptionsCompat optionsCompat
                            = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            getActivity(), itemView.findViewById(R.id.imgv_avatar_storeitem),
                            "avatarStore");
//                Toast.makeText(getContext(),
//                        selected_store.getName() + "", Toast.LENGTH_SHORT).show();
                    startActivity(intent_storedetail, optionsCompat.toBundle());
                }
            }
        });
        mRecyclerView.setAdapter(itemadapter);
        itemadapter.setLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                int index = itemadapter.getSize();
                onLoadMore(index);
            }
        });
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_storefrag);
        swipeRefresh.setColorSchemeResources(R.color.admin_color_selection_news,
                R.color.color_selection_report);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                typeSort = -1;
                stt = 8;
                sendBroadcastSortStoreReset();
                getStoreList(dist_pro);
            }
        });
    }

    private void sendBroadcastSortStoreReset(){
        broadcastIntent = new Intent();
        broadcastIntent.setAction(Const.INTENT_KEY_SORT_STORE_TYPE);
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
                itemadapter.addToList(null);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                itemadapter.removeLastItemFromList();
                isLoadMore = true;
                addMore(pos);
            }
        }, 3000);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        mIntentFilter = new IntentFilter(Const.INTENT_KEY_RECEIVE_LOCATION);
        mIntentFilter.addAction(Const.INTENT_KEY_RECEIVE_LOCATION_TAB);
        mIntentFilter.addAction(Const.INTENT_KEY_SORT_STORE);
        mIntentFilter.addAction(Const.INTENT_KEY_RELOAD_DATA);
        mBroadcastReceiver = new LocationChange();
        broadcastIntent = new Intent();
        getActivity().registerReceiver(mBroadcastReceiver, mIntentFilter);
        isRegister=true;
        if (null != CoreManager.getInstance().getMyLocation()) {
            if(TextUtils.isEmpty(CoreManager.getInstance().getHuyen())&&TextUtils.isEmpty(CoreManager.getInstance().getTinh())) {
                dist_pro = CoreManager.getInstance().getMyLocation().getDistrict()
                        + "_" + CoreManager.getInstance().getMyLocation().getProvince();
            }else{
                dist_pro = CoreManager.getInstance().getHuyen()
                        + "_" + CoreManager.getInstance().getTinh();
            }
            getStoreList(dist_pro);
        } else {
            if (getView() != null) {
                AppUtils.showSnackbarWithoutButton(getView(), "Không tìm thấy vị trí của bạn");
            }
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
            if (mBroadcastReceiver != null &&isRegister) {
                getActivity().unregisterReceiver(mBroadcastReceiver);
                isRegister=false;
            }
        }catch (Exception e){

        }
    }

    public class calculateDistance extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                for (Store store : stores) {
                    double distance = 0;
                    try {
                        distance = myTool.distanceFrom_in_Km(store.getLat(), store.getLng(),
                                CoreManager.getInstance().getMyLocation().getLat(), CoreManager.getInstance().getMyLocation().getLng());
                        int c = (int) Math.round(distance);
                        int d = c / 1000;
                        int e = c % 1000;
                        int f = e / 100;
                        store.setDistance(d + "." + f);
                    } catch (Exception e) {

                    }

                }
            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isLoadMore = false;
            addMore(0);
        }
    }

    class LocationChange extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case Const.INTENT_KEY_RECEIVE_LOCATION:
                    if(intent.getStringExtra(Const.KEY_TINH).toString()!=null){
                        if(intent.getStringExtra(Const.KEY_HUYEN).toString()!=null){
                            dist_pro = intent.getStringExtra(Const.KEY_HUYEN).toString()
                                    + "_" +intent.getStringExtra(Const.KEY_TINH).toString();
                            stt = 8;
                            getStoreList(dist_pro);
                        }
                    }
                    break;
                case Const.INTENT_KEY_RECEIVE_LOCATION_TAB:
                    if(intent.getStringExtra(Const.KEY_TINH).toString()!=null){
                        if(intent.getStringExtra(Const.KEY_HUYEN).toString()!=null){
                            dist_pro = intent.getStringExtra(Const.KEY_HUYEN).toString()
                                    + "_" +intent.getStringExtra(Const.KEY_TINH).toString();
                            getStoreList(dist_pro);
                        }
                    }
                    break;
                case Const.INTENT_KEY_RELOAD_DATA:
                    getStoreList(dist_pro);
                    break;
                case Const.INTENT_KEY_SORT_STORE:
                    if(intent.getIntExtra(Const.KEY_SORT,0)!=0){
                        typeSort=intent.getIntExtra(Const.KEY_SORT,0);
                        itemadapter.sortByType(typeSort);
                    }
                    break;
            }
        }
    }
}
