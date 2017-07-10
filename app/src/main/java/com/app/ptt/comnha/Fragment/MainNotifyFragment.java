package com.app.ptt.comnha.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Activity.AdapterActivity;
import com.app.ptt.comnha.Activity.PostdetailActivity;
import com.app.ptt.comnha.Adapters.NotificationAdapter;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Interfaces.Comunication;
import com.app.ptt.comnha.Interfaces.OnMItemListener;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.Models.FireBase.UserNotification;
import com.app.ptt.comnha.Modules.orderByDate;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChooseFood;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainNotifyFragment extends Fragment implements OnMItemListener {
    RecyclerView rvList;
    List<UserNotification> list;
    boolean isNew=false;
    DatabaseReference dbRef;
    ValueEventListener userValueListener;
    User ownUser;
    StorageReference stRef;
    SwipeRefreshLayout swipeRefresh;
    NotificationAdapter notificationAdapter;
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
                notificationAdapter.clearAll();
                getNotification();
            }
        });
        getNotification();
        Comunication.onMItemListener=this;
        return view;
    }
    private void getNotification(){
        if(LoginSession.getInstance().getUser()!=null) {
            try {
                ValueEventListener valueEventListener=new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        isNew=false;
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            UserNotification notification = item.getValue(UserNotification.class);
                            notification.setId(item.getKey());
                            if (checkExist(item.getKey()) == -1) {
                                list.add(notification);
                            } else {
                                list.set(checkExist(item.getKey()), notification);
                            }
                        }
                        sort();

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
                Food food=new Food();
                food.setFoodID(noti.getFoodId());
                ChooseFood.getInstance().setFood(food);
                intent_openFood.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_openFood);
                break;
        }
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

}
