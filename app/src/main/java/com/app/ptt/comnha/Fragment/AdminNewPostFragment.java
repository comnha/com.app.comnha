package com.app.ptt.comnha.Fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.app.ptt.comnha.Activity.PostdetailActivity;
import com.app.ptt.comnha.Adapters.notify_newpost_adapter;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Dialog.BlockUserDialog;
import com.app.ptt.comnha.Models.FireBase.NewpostNotify;
import com.app.ptt.comnha.Models.FireBase.NewstoreNotify;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.app.ptt.comnha.SingletonClasses.CoreManager;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.AppUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminNewPostFragment extends Fragment {

    ListView listView;
    notify_newpost_adapter itemadapter;
    List<NewpostNotify> items;
    DatabaseReference dbRef;
    ValueEventListener notiEventListener, postEventListener, userEventListener;
    String dist_pro;
    ProgressDialog plzwaitDialog;
    Post post = null;
    User user;

    public AdminNewPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_newpost, container, false);
        dbRef = FirebaseDatabase
                .getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseDB_path));
        ref(view);
        if(LoginSession.getInstance().getUser().getRole()>0){
            dist_pro=LoginSession.getInstance().getUser().getDist_prov();
            getdata();
        }else{
            getActivity().finish();
        }
        return view;
    }

    private void ref(View view) {
        listView = (ListView) view.findViewById(R.id.listview_newpost_frag);
        items = new ArrayList<>();
        itemadapter = new notify_newpost_adapter(getActivity(), items);
        listView.setAdapter(itemadapter);
        itemadapter.setOnItemClickLiestner(new notify_newpost_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(final NewpostNotify notify, Activity activity) {
                plzwaitDialog.show();
                if (!notify.isReadstate()) {
                    String key = notify.getId();
                    NewpostNotify childNotify = notify;
                    notify.setReadstate(true);
                    childNotify.setReadstate(true);
                    Log.d("district_province", childNotify.getDistrict_province());
                    childNotify.setReadState_pro_dist(
                            String.valueOf(childNotify.isReadstate())
                                    + "_" + childNotify.getDistrict_province());
                    Map<String, Object> notifyValue = childNotify.toMap();
                    Map<String, Object> updateChild = new HashMap<>();
                    updateChild.put(getString(R.string.notify_newpost_CODE) + key,
                            notifyValue);
                    dbRef.updateChildren(updateChild).addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    getPostInfo(notify);
                                }
                            }
                    );
                } else {
                    getPostInfo(notify);
                }

            }
        });
        itemadapter.setOnOptionItemClickListener(new notify_newpost_adapter.OnOptionItemClickListener() {
            @Override
            public void onDelNotify(final NewpostNotify notify) {
                new AlertDialog.Builder(getContext())
                        .setCancelable(true)
                        .setMessage(getString(R.string.txt_delconfirm))
                        .setPositiveButton(getString(R.string.txt_del), new
                                DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialogInterface, int i) {
                                        plzwaitDialog.show();
                                        dbRef.child(getString(R.string.notify_newpost_CODE)
                                                + notify.getId())
                                                .removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        plzwaitDialog.cancel();
                                                        items.remove(notify);
                                                        itemadapter.notifyDataSetChanged();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        plzwaitDialog.cancel();
                                                        Toast.makeText(getContext(),
                                                                e.getMessage(),
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                }).setNegativeButton(getString(R.string.text_no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                                int i) {
                                dialogInterface.cancel();
                            }
                        }).show();
            }

            @Override
            public void onBlockUser(NewpostNotify notify) {
                plzwaitDialog.show();
                getUserInfo(notify);
            }
        });
        plzwaitDialog = AppUtils.setupProgressDialog(getContext(),
                getString(R.string.txt_plzwait), null, false, false,
                ProgressDialog.STYLE_SPINNER, 0);
    }

    private void getUserInfo(NewpostNotify notify) {
        userEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                plzwaitDialog.cancel();
                user = dataSnapshot.getValue(User.class);
                String key = dataSnapshot.getKey();
                user.setuID(key);
                if (user.isWritepostBlocked()) {
                    Toast.makeText(getContext(), getString(R.string.txt_blockeduser)
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    BlockUserDialog blockDialog = new BlockUserDialog();
                    blockDialog.setStyle(DialogFragment.STYLE_NORMAL,
                            R.style.AddfoodDialog);
                    blockDialog.setBlock(user, Const.BLOCK_TYPE.BLOCK_WRITEPOST);
                    blockDialog.setOnPosNegListener(new BlockUserDialog.OnPosNegListener() {
                        @Override
                        public void onPositive(boolean isClicked, Map<String,
                                Object> childUpdate, final Dialog dialog) {
                            if (isClicked) {
                                dialog.dismiss();
                                plzwaitDialog.show();
                                dbRef.updateChildren(childUpdate)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                plzwaitDialog.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.show();
                                        plzwaitDialog.dismiss();
                                        Toast.makeText(getContext(), e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        }

                        @Override
                        public void onNegative(boolean isClicked, Dialog dialog) {
                            if (isClicked) {
                                dialog.dismiss();
                            }
                        }
                    });
                    blockDialog.show(getFragmentManager(), "frag_blockuser");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.users_CODE)
                + notify.getUserID())
                .addListenerForSingleValueEvent(userEventListener);
    }

    private void getPostInfo(NewpostNotify notify) {
        postEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post = dataSnapshot.getValue(Post.class);
                String key = dataSnapshot.getKey();
                post.setPostID(key);
                ChoosePost.getInstance().setPost(post);
                Intent intent_openstore = new Intent(getActivity(),
                        PostdetailActivity.class);
                intent_openstore.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                plzwaitDialog.dismiss();
                startActivity(intent_openstore);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.posts_CODE) +
                notify.getPostID())
                .addListenerForSingleValueEvent(
                        postEventListener);
    }

    private void getdata() {
        notiEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    NewpostNotify item = dataItem.getValue(NewpostNotify.class);
                    String key = dataItem.getKey();
                    item.setId(key);
                    int pos=-1;
                    for(NewpostNotify notify:items){
                        if(notify.getId().equals(item.getId())){
                            pos=items.indexOf(notify);
                        }
                    }
                    if(pos!=-1){
                        items.set(pos,item);
                    }else {
                        items.add(item);
                    }

                }
                sortList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        if(LoginSession.getInstance().getUser().getRole()==1) {
            dbRef.child(getString(R.string.notify_newpost_CODE))
                    .orderByChild("district_province")
                    .equalTo(dist_pro)
                    .addListenerForSingleValueEvent(notiEventListener);
        }else {
            dbRef.child(getString(R.string.notify_newpost_CODE))
                    .addListenerForSingleValueEvent(notiEventListener);
        }
    }
    public void sortList(){
        List<NewpostNotify> list=new ArrayList<>();
        for(NewpostNotify notify:items){
            if(!notify.isReadstate()){
                list.add(notify);
            }
        }
        for(NewpostNotify notify:items){
            if(notify.isReadstate()){
                list.add(notify);
            }
        }
        itemadapter.setList(list);
    }
}
