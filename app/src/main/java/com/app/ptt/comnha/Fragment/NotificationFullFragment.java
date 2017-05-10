package com.app.ptt.comnha.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.app.ptt.comnha.Models.FireBase.Notification;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NotificationFullFragment extends Fragment {
    private static final String LOG=NotificationFullFragment.class.getSimpleName();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    RecyclerView.Adapter mAdapter;
    DatabaseReference dbRef;
    ArrayList<Notification> listNoti, listReaded;
    ChildEventListener notificatonChildEventListener;
    boolean readAll,deleteAll;
    //ProgressDialog plzw8Dialog;
    Map<String, Object> childUpdates;
    ArrayList<String> delList;
    public NotificationFullFragment() {
        // Required empty public constructor
    }
    ImageButton imageButton;
    boolean isConnected=false;
    IntentFilter mIntentFilter;
    Context context;
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(mBroadcastSendAddress)) {
                Log.i(LOG+".onReceive form Service","isConnected= "+ intent.getBooleanExtra("isConnected", false));
                if (intent.getBooleanExtra("isConnected", false)) {
                    isConnected = true;
                } else
                    isConnected = false;
                if(intent.getIntExtra("pos",0)!=-1){
                    listReaded.add(listNoti.get(intent.getIntExtra("pos",0)));
                    listNoti.get(intent.getIntExtra("pos",0)).setReaded(true);
                    mAdapter.notifyDataSetChanged();
                    //mAdapter = new Notification_rcycler_adapter(listNoti, getActivity(),1);
                    //mRecyclerView.setAdapter(mAdapter);
                }
            }

        }
    };
    public void setContext(Context mContext){
        this.context=mContext;
        Log.i(LOG+".setContext","OK");
    }

    @Override
    public void onStart() {
        super.onStart();
        isConnected= MyService.returnIsNetworkConnected();
        if(!isConnected){
            Toast.makeText(getActivity().getApplicationContext(),"Offline mode",Toast.LENGTH_SHORT).show();
        }
        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
        getContext().registerReceiver(broadcastReceiver,mIntentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(broadcastReceiver);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fullnotification, container, false);
        // Inflate the layout for this fragment
        isConnected = MyService.returnIsNetworkConnected();
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        anhxa(view);
        getData();
        return view;
    }
    public void getData(){
        Log.i("NOTIFICATION_list Size=",listNoti.size()+"");
        notificatonChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Notification notification=dataSnapshot.getValue(Notification.class);
                notification.setNotiID(dataSnapshot.getKey());
                listNoti.add(notification);
                Log.i("NOTIFICATION_list Size=",listNoti.size()+"");
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
//        if(MyService.getUserAccount().getRole()){
//            Log.i("NotificationFragment", "ADMIN");
////            dbRef.child(getResources().getString(R.string.notification_CODE)+"admin")
////                    .addChildEventListener(notificatonChildEventListener);
//        }
//        else{
//            Log.i("NotificationFragment", "USER");
////            dbRef.child(getResources().getString(R.string.notification_CODE)+MyService.getUserAccount().getId().toString())
////                    .addChildEventListener(notificatonChildEventListener);
//        }



        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("NotificationFragment", "Load thông báo, size="+listNoti.size());
                if(listNoti.size()==0){
                    Toast.makeText(getContext(),"Không có thông báo nào",Toast.LENGTH_SHORT).show();
                }else {
                    ArrayList<Notification> unread = new ArrayList<>();
                    ArrayList<Notification> readed = new ArrayList<>();
                    for (Notification noti : listNoti) {
                        if (noti.getReaded()) {
                            readed.add(noti);
                        } else {
                            unread.add(noti);
                        }
                    }
                    listNoti = new ArrayList<>();
                    if(unread.size()==0){
                        Toast.makeText(getContext(),"Không có thông báo mới nào",Toast.LENGTH_SHORT).show();
                    }else {
                        for (Notification un : unread) {
                            listNoti.add(un);
                        }
                    }
                    for (Notification re : readed) {
                        listNoti.add(re);
                    }

                    mAdapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void anhxa(final View view){
        delList=new ArrayList<>();
        listNoti=new ArrayList<>();
        listReaded=new ArrayList<>();
        imageButton=(ImageButton) view.findViewById(R.id.btn__Option);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);

                /** Adding menu items to the popumenu */
                popup.getMenuInflater().inflate(R.menu.option_menu_notification, popup.getMenu());

                /** Defining menu item click listener for the popup menu */
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                       switch (item.getItemId()){
                           case R.id.menu_notification_setdadoc:
                               readAll=true;
                               for(Notification un: listNoti){
                                   if(!un.getReaded())
                                     un.setReaded(true);
                               }
                               mAdapter.notifyDataSetChanged();
                               //mAdapter = new Notification_rcycler_adapter(listNoti, getActivity(),1);
                               //mRecyclerView.setAdapter(mAdapter);
                               break;
                           case R.id.menu_notification_xoatatca:
                               deleteAll=true;
                               for(Notification not: listNoti){
                                   delList.add(not.getNotiID());
                               }
                               listNoti=new ArrayList<>();
//                               if(MyService.getUserAccount().getRole()) {
//                                   mAdapter = new Notification_rcycler_adapter_admin(listNoti, getActivity(), 1);
//                                   mRecyclerView.setAdapter(mAdapter);
//                               }else{
//                                   mAdapter = new Notification_rcycler_adapter(listNoti, getActivity(), 1);
//                                   mRecyclerView.setAdapter(mAdapter);
//                               }
                               mAdapter.notifyDataSetChanged();
                               break;
                       }
                        return true;
                    }
                });

                /** Showing the popup menu */
                popup.show();
            }
        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_notification);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerViewLayoutManager = linearLayoutManager;
        mRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
//        if(MyService.getUserAccount().getRole()) {
//            mAdapter = new Notification_rcycler_adapter_admin(listNoti, getActivity(), 1);
//        }else{
//            mAdapter=new Notification_rcycler_adapter(listNoti,getActivity(),1);
//        }
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        childUpdates=new HashMap<>();
//        if(MyService.getUserAccount().getRole()) {
//            if (deleteAll) {
//                for (String delItem : delList) {
//                    dbRef.child(getResources().getString(R.string.notification_CODE) + "admin" + "/" + delItem).removeValue()
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    Log.i("Thoát trang thông báo", "Xóa tất cả thành công!!!!!");
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // plzw8Dialog.dismiss();
//                            Log.i("Thoát trang thông báo", "Xóa tất cả lỗi!!!!!");
//                        }
//                    });
//                }
//            } else if (listNoti.size() > 0) {
//                for (Notification item : listNoti) {
//                    Map<String, Object> not = item.toMap();
////                    childUpdates.put(
////                            getResources().getString(R.string.notification_CODE) + "admin" + "/" + item.getNotiID(), not);
//                }
//                dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Log.i("Thoát trang thông báo", "Đoc tất cả thành công!!!!!");
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.i("Thoát trang thông báo", "Đọc tất cả lỗi!!!!!");
//                    }
//                });
//            }
//        }else{
//            if (deleteAll) {
//                for (String delItem : delList) {
//                    dbRef.child(getResources().getString(R.string.notification_CODE) + MyService.getUserAccount().getId() + "/" + delItem).removeValue()
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    Log.i("Thoát trang thông báo", "Xóa tất cả thành công!!!!!");
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // plzw8Dialog.dismiss();
//                            Log.i("Thoát trang thông báo", "Xóa tất cả lỗi!!!!!");
//                        }
//                    });
//                }
//            } else if (listNoti.size() > 0) {
//                for (Notification item : listNoti) {
//                    Map<String, Object> not = item.toMap();
////                    childUpdates.put(
////                            getResources().getString(R.string.notification_CODE) + MyService.getUserAccount().getId()    + "/" + item.getNotiID(), not);
//                }
//                dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Log.i("Thoát trang thông báo", "Đoc tất cả thành công!!!!!");
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.i("Thoát trang thông báo", "Đọc tất cả lỗi!!!!!");
//                    }
//                });
//            }
//        }
//        MyService.setNotifications(null);
//

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and storename
        void onFragmentInteraction(Uri uri);
    }
}
