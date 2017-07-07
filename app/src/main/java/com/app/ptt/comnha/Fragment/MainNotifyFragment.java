package com.app.ptt.comnha.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.UserNotification;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainNotifyFragment extends Fragment {
    ListView listView;
    List<UserNotification> list;
    DatabaseReference dbRef;
    TextView txtNotti;

    public MainNotifyFragment() {
        // Required empty public constructor



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("MainFragmentPage","createviewNotify");
        View view = inflater.inflate(R.layout.fragment_main_notify, container, false);
        listView = (ListView) view.findViewById(R.id.listV_notifyfrag);
        txtNotti= (TextView) view.findViewById(R.id.txt_text);
        list=new ArrayList<>();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Const.DATABASE_PATH);
        getNotification();
        return view;
    }
    private void getNotification(){
        if(LoginSession.getInstance().getUser()!=null) {
            try {
                ValueEventListener valueEventListener=new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            UserNotification notification = item.getValue(UserNotification.class);
                            notification.setId(item.getKey());
                            if (checkExist(item.getKey()) == -1) {
                                list.add(notification);
                                txtNotti.setText(list.size()+"");
                            } else {
                                list.set(checkExist(item.getKey()), notification);
                            }
                        }
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
}
