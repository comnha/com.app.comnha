package com.app.ptt.comnha.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ptt.comnha.Activity.AdapterActivity;
import com.app.ptt.comnha.Activity.PostdetailActivity;
import com.app.ptt.comnha.Activity.StoreDeatailActivity;
import com.app.ptt.comnha.Adapters.NotificationAdapter;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Interfaces.Comunication;
import com.app.ptt.comnha.Interfaces.OnMItemListener;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.ReportfoodNotify;
import com.app.ptt.comnha.Models.FireBase.ReportpostNotify;
import com.app.ptt.comnha.Models.FireBase.ReportstoreNotify;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.Models.FireBase.UserNotification;
import com.app.ptt.comnha.Modules.orderByDate;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainNotifyFragment extends Fragment implements OnMItemListener {
    RecyclerView rvList;
    List<String> idReports;
    List<UserNotification> list;
    Map<String,Object> listLoop;
    boolean isRegister;
    boolean isNew=false;
    DatabaseReference dbRef;
    ValueEventListener userValueListener;
    User ownUser;
    StorageReference stRef;
    SwipeRefreshLayout swipeRefresh;
    NotificationAdapter notificationAdapter;
    IntentFilter mIntentFilter;
    Intent broadcastIntent;
    UserChange mBroadcastReceiver;
    public MainNotifyFragment() {
        // Required empty public constructor



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("MainFragmentPage","createviewNotify");
        View view = inflater.inflate(R.layout.fragment_main_notify, container, false);
        rvList = (RecyclerView) view.findViewById(R.id.listV_notifyfrag);
        LinearLayoutManager ll=new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(ll);
        list=new ArrayList<>();
        idReports=new ArrayList<>();
        stRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Const.STORAGE_PATH);
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Const.DATABASE_PATH);
         notificationAdapter=new NotificationAdapter(getActivity(),dbRef,stRef);
        rvList.setAdapter(notificationAdapter);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_notifyfrag);
        swipeRefresh.setColorSchemeResources(R.color.admin_color_selection_news,
                R.color.color_selection_report);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(LoginSession.getInstance().getUser()!=null) {
                    notificationAdapter.clearAll();
                    getNotification();
                }else{
                    swipeRefresh.setRefreshing(false);
                }
            }
        });
        getNotification();

        Comunication.onMItemListener=this;
        return view;
    }

    private void getReport(){

        ValueEventListener postReport=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item:dataSnapshot.getChildren()){
                    ReportpostNotify reportpostNotify  = item.getValue(ReportpostNotify.class);
                    idReports.add(reportpostNotify.getPostID());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ValueEventListener storeReport=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item:dataSnapshot.getChildren()){
                    ReportstoreNotify reportstoreNotify  = item.getValue(ReportstoreNotify.class);
                    idReports.add(reportstoreNotify.getStoreID());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ValueEventListener foodReport=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item:dataSnapshot.getChildren()){
                    ReportfoodNotify reportfoodNotify  = item.getValue(ReportfoodNotify.class);
                    idReports.add(reportfoodNotify.getFoodID());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.reportPost_CODE))
                .orderByChild("isApprove")
                .equalTo(true)
                .addValueEventListener(postReport);
        dbRef.child(getString(R.string.reportStore_CODE))
                .orderByChild("isApprove")
                .equalTo(true)
                .addValueEventListener(storeReport);
        dbRef.child(getString(R.string.reportFood_CODE))
                .orderByChild("isApprove")
                .equalTo(true)
                .addValueEventListener(foodReport);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getNotification();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private boolean checkExistInReport(UserNotification id){
        boolean result=false;
        for(String mId:idReports){
            if(id.getType()==1 &&id.getStoreID().toLowerCase().equals(mId.toLowerCase())){
                result= true;
            }
            if(id.getType()==4 &&id.getFoodId().toLowerCase().equals(mId.toLowerCase())){
                result= true;
            }

            if((id.getType()==3 ||id.getType()==2) &&id.getPostID().toLowerCase().equals(mId.toLowerCase())){
                result= true;
            }
            if(result) {
                if (id.getStatus() != -2 || id.getStatus() != 2 || id.getStatus() != 3) {
                    result = false;
                }
            }
        }
        return result;
    }

    private void getNotification(){
        list.clear();
        listLoop=new HashMap<>();
        if(LoginSession.getInstance().getUser()!=null) {
            try {
                ValueEventListener valueEventListener=new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        isNew=false;
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            UserNotification notification = item.getValue(UserNotification.class);
                            notification.setId(item.getKey());
                            if(!checkExistInReport(notification)) {
                                if (checkExist(item.getKey()) == -1) {
                                    checkExistInList(notification);
                                } else {
                                    list.set(checkExist(item.getKey()), notification);
                                }
                            }
                        }
                        sort();
                        if(listLoop!=null) {
                            if(listLoop.size()>0) {
                                updateLoopList(listLoop);
                            }
                        }
                        swipeRefresh.setRefreshing(false);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                dbRef.child(getString(R.string.user_notification_CODE)+LoginSession.getInstance().getUser().getuID()+"/")
                        .addValueEventListener(valueEventListener);

            }catch (Exception e){
            }
        }
    }

    private void checkExistInList(UserNotification notification){
        if(list==null){
            list=new ArrayList<>();
        }
        if(list.size()==0){
            list.add(notification);

        }else {
            for (UserNotification noti : list) {
                //check multi comment of one user
                if (noti.getUserEffectName() != null && noti.getPostID() != null && noti.getType() == 3) {
                    if (noti.getType() == notification.getType()
                            && noti.getUserEffectName().equals(notification.getUserEffectName())
                            && noti.getType() == 3 && noti.getPostID().equals(notification.getPostID())) {
                        if (noti.isReaded()) {
                            if (!notification.isReaded()) {
                                list.set(list.indexOf(noti), notification);
                            }
                            return;
                        } else {
                            if (!notification.isReaded()) {
                                notification.setReaded(true);
                                if (listLoop == null) {
                                    listLoop = new HashMap<>();
                                }
                                listLoop = updateListNoti(listLoop, notification);
                            }
                            return;
                        }
                    }
                }
            }
            list.add(notification);
        }
    }

    private int checkExist(String id){
        for(UserNotification notification:list){
            if(notification.getId().toLowerCase().equals(id.toLowerCase())){
                return list.indexOf(notification);
            }
        }
        return -1;
    }

    public void sort(){
        List<UserNotification> listReaded,listUnreaded;
        listReaded=new ArrayList<>();
        listUnreaded=new ArrayList<>();
        for(UserNotification item:list){
            if(!item.isReaded()){
                listUnreaded.add(item);
            }else{
                listReaded.add(item);
            }
        }
        Collections.sort(listReaded,new orderByDate());
        Collections.sort(listUnreaded,new orderByDate());
        list=new ArrayList<>();
        list.addAll(listUnreaded);
        list.addAll(listReaded);
        notificationAdapter.clearAll();
        notificationAdapter.addAll(list);

    }

    @Override
    public void onNotiClick(UserNotification noti) {
        if(!noti.isReaded()){
            noti.setReaded(true);
            updateNoti(noti);
        }
        switch (noti.getType()){
            case 1://new store
                Intent intent_storedetail = new Intent(getContext(),
                        StoreDeatailActivity.class);
                Store store = new Store();
                store.setStoreID(noti.getStoreID());
                ChooseStore.getInstance().setStore(store);
                startActivity(intent_storedetail);
                break;
            case 2://new post
                Intent intent_postdetail = new Intent(getContext(),
                        PostdetailActivity.class);

                Post post=new Post();
                post.setPostID(noti.getPostID());
                ChoosePost.getInstance().setPost(post);
                startActivity(intent_postdetail);
                break;
            case 3://new comment
                Intent intent_postdetail_comment = new Intent(getContext(),
                        PostdetailActivity.class);

                Post postComment=new Post();
                postComment.setPostID(noti.getPostID());
                ChoosePost.getInstance().setPost(postComment);
                startActivity(intent_postdetail_comment);
                break;
            case 4://new food
                Intent intent_openFood = new Intent(getActivity(),
                        AdapterActivity.class);
                intent_openFood.putExtra(getString(R.string.fragment_CODE)
                        , getString(R.string.frag_foodetail_CODE));
                intent_openFood.putExtra(Const.KEY_FOOD,noti.getFoodId());
                startActivity(intent_openFood);
                break;
        }
    }

    public Map<String,Object> updateListNoti(Map<String,Object> childUpdate,UserNotification noti){
        Map<String,Object> updateUserNotification=noti.toMap();
        childUpdate.put(getString(R.string.user_notification_CODE)+LoginSession.getInstance().getUser().getuID()+"/"+noti.getId(),updateUserNotification);
        return childUpdate;
    }

    private void updateLoopList(Map<String,Object> childUpdate){
        dbRef.updateChildren(childUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listLoop=null;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void updateNoti(UserNotification userNotification){
        Map<String,Object> updateUserNotification=userNotification.toMap();
        Map<String,Object> childUpdate;
        childUpdate=new HashMap();
        childUpdate.put(getString(R.string.user_notification_CODE)+LoginSession.getInstance().getUser().getuID()+"/"+userNotification.getId(),updateUserNotification);
        dbRef.updateChildren(childUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(LoginSession.getInstance().getUser()!=null) {
            getReport();
        }
        mIntentFilter = new IntentFilter(Const.INTENT_KEY_USER_CHANGE);
        mBroadcastReceiver = new UserChange();
        broadcastIntent = new Intent();
        getActivity().registerReceiver(mBroadcastReceiver, mIntentFilter);
        isRegister = true;
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

    class UserChange extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Const.INTENT_KEY_USER_CHANGE:
                    if(LoginSession.getInstance().getUser()!=null){
                        getReport();
                    }else{
                        list=new ArrayList<>();
                        notificationAdapter.clearAll();

                    }
                    break;
            }
        }
    }

}
