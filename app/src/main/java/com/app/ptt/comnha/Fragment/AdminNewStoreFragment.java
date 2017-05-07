package com.app.ptt.comnha.Fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.app.ptt.comnha.Activity.StoreDeatailActivity;
import com.app.ptt.comnha.Adapters.notify_newstore_adapter;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.NewstoreNotify;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminNewStoreFragment extends Fragment {

    ListView listView;
    notify_newstore_adapter itemadapter;
    ArrayList<NewstoreNotify> items;
    DatabaseReference dbRef;
    ValueEventListener storeValueListener, userValueListener,
            childEventListener;
    String dist_pro = "Quận 9_HCM";
    Store store = null;
    ProgressDialog plzwaitDialog;
    User user;

    public AdminNewStoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_newstore, container, false);
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebase_path));
        ref(view);
        getdata();
        Log.d("notinewstore_createView", "createview");
        return view;
    }

    private void getdata() {

        dbRef.child(getString(R.string.notify_newstore_CODE))
                .orderByChild("district_province")
                .equalTo(dist_pro)
                .addValueEventListener(childEventListener);
    }

    private void ref(View view) {
        listView = (ListView) view.findViewById(R.id.listview_newstore_frag);
        items = new ArrayList<>();
        itemadapter = new notify_newstore_adapter(getActivity(), items);
        listView.setAdapter(itemadapter);
        itemadapter.setOnItemClickLiestner(new notify_newstore_adapter
                .OnItemClickLiestner() {
            @Override
            public void onItemClick(final NewstoreNotify notify, Activity activity) {
                plzwaitDialog.show();
                if (!notify.isReadstate()) {
                    String key = notify.getId();
                    NewstoreNotify childNotify = notify;
                    notify.setReadstate(true);
                    childNotify.setId(null);
                    childNotify.setReadstate(true);
                    Log.d("district_province", childNotify.getDistrict_province());
                    childNotify.setReadState_pro_dist(
                            String.valueOf(childNotify.isReadstate())
                                    + "_" + childNotify.getDistrict_province());
                    Map<String, Object> notifyValue = childNotify.toMap();
                    Map<String, Object> updateChild = new HashMap<>();
                    updateChild.put(getString(R.string.notify_newstore_CODE) + key,
                            notifyValue);
                    dbRef.updateChildren(updateChild).addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dbRef.child(getString(R.string.store_CODE) +
                                            notify.getStoreID())
                                            .addValueEventListener(storeValueListener);
                                }
                            }
                    );
                } else {

                    dbRef.child(getString(R.string.store_CODE) +
                            notify.getStoreID())
                            .addValueEventListener(storeValueListener);
                }

            }
        });
        itemadapter.setOnOptionItemClickListener(
                new notify_newstore_adapter.OnOptionItemClickListener() {
                    @Override
                    public void onDelNotify(final NewstoreNotify notify) {
                        new AlertDialog.Builder(getContext())
                                .setCancelable(true)
                                .setMessage(getString(R.string.txt_delconfirm))
                                .setPositiveButton(getString(R.string.txt_del), new
                                        DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialogInterface, int i) {
                                                plzwaitDialog.show();
                                                dbRef.child(getString(R.string.notify_newstore_CODE)
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
                    public void onBlockUser(NewstoreNotify notify) {
                        plzwaitDialog.show();
                        dbRef.child(getString(R.string.users_CODE)
                                + notify.getUserID())
                                .addValueEventListener(userValueListener);
                    }
                });
        plzwaitDialog = new ProgressDialog(getContext());
        plzwaitDialog.setMessage(getString(R.string.txt_plzwait));
        plzwaitDialog.setCanceledOnTouchOutside(false);
        plzwaitDialog.setCancelable(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("notifynewstore_onAttach", "attached");
        childEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    NewstoreNotify item = dataItem.getValue(NewstoreNotify.class);
                    String key = dataItem.getKey();
                    item.setId(key);
                    items.add(item);
                }
                itemadapter.notifyDataSetChanged();
                dbRef.child(getString(R.string.notify_newstore_CODE))
                        .orderByChild("district_province")
                        .equalTo(dist_pro).removeEventListener(childEventListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        storeValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                store = dataSnapshot.getValue(Store.class);
                String key = dataSnapshot.getKey();
                store.setStoreID(key);
                ChooseStore.getInstance().setStore(store);
                Intent intent_openstore = new Intent(getActivity(),
                        StoreDeatailActivity.class);
                intent_openstore.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                plzwaitDialog.dismiss();
                dbRef.child(getString(R.string.store_CODE)
                        + key)
                        .removeEventListener(storeValueListener);
                startActivity(intent_openstore);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                plzwaitDialog.cancel();
                user = dataSnapshot.getValue(User.class);
                String key = dataSnapshot.getKey();
                user.setuID(key);
                dbRef.child(getString(R.string.users_CODE)
                        + key)
                        .removeEventListener(userValueListener);
                if (user.isAddstoreBlocked()) {
                    Toast.makeText(getContext(), getString(R.string.txt_blockeduser)
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    BlockUserDialog blockDialog = new BlockUserDialog();
                    blockDialog.setStyle(DialogFragment.STYLE_NORMAL,
                            R.style.AddfoodDialog);
                    blockDialog.setBlock(user, Const.BLOCK_TYPE.BLOCK_ADDSTORE);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("notifynewstore_onResume", "resume");
    }

    @Override
    public void onDetach() {
        Log.d("adminnewstore", "Detached");

        super.onDetach();
    }

    @Override
    public void onStop() {
        super.onStop();
        dbRef.child(getString(R.string.notify_newstore_CODE))
                .removeEventListener(childEventListener);
    }
}
