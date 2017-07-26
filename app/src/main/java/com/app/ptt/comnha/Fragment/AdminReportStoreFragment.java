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

import com.app.ptt.comnha.Activity.StoreDeatailActivity;
import com.app.ptt.comnha.Adapters.notify_reportstore_adapter;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Dialog.BlockUserDialog;
import com.app.ptt.comnha.Models.FireBase.ReportstoreNotify;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
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
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminReportStoreFragment extends Fragment {

    ListView listView;
    notify_reportstore_adapter itemadapter;
    ArrayList<ReportstoreNotify> items;
    DatabaseReference dbRef;
    ValueEventListener reportEventListener, storeEventListener, userEventListener;
    String dist_pro;
    Store store = null;
    ProgressDialog plzwaitDialog;
    User user;

    public AdminReportStoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_report_store, container, false);
        dbRef = FirebaseDatabase
                .getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseDB_path));
        init(view);
        if (null != CoreManager.getInstance().getMyLocation()) {
            dist_pro= LoginSession.getInstance().getUser().getDist_prov();
            Log.d("dist_pro", dist_pro);
        } else {
            if (getView() != null)
                AppUtils.showSnackbarWithoutButton(getView(), "Không tìm thấy vị trí của bạn");
        }
        getStoreReport();
        return view;
    }

    private void init(View view) {
        listView = (ListView) view.findViewById(R.id.listview_reportstore_frag);
        items = new ArrayList<>();
        itemadapter = new notify_reportstore_adapter(getActivity(), items);
        listView.setAdapter(itemadapter);
        itemadapter.setOnItemClickLiestner(new notify_reportstore_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(final ReportstoreNotify notify, Activity activity) {
                plzwaitDialog.show();
                if (!notify.isReadstate()) {
                    String key = notify.getId();
                    ReportstoreNotify childNotify = notify;
                    notify.setReadstate(true);
                    childNotify.setReadstate(true);
                    Log.d("district_province", childNotify.getDistrict_province());
                    Map<String, Object> notifyValue = childNotify.toMap();
                    Map<String, Object> updateChild = new HashMap<>();
                    updateChild.put(getString(R.string.reportStore_CODE) + key,
                            notifyValue);
                    dbRef.updateChildren(updateChild).addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    getStoreInfo(notify);

                                }
                            }
                    );
                } else {
                    getStoreInfo(notify);
                }

            }
        });
        itemadapter.setOnOptionItemClickListener(
                new notify_reportstore_adapter.OnOptionItemClickListener() {
                    @Override
                    public void onDelNotify(final ReportstoreNotify notify) {
                        new AlertDialog.Builder(getContext())
                                .setCancelable(true)
                                .setMessage(getString(R.string.txt_delconfirm))
                                .setPositiveButton(getString(R.string.txt_del), new
                                        DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialogInterface, int i) {
                                                plzwaitDialog.show();
                                                dbRef.child(getString(R.string.reportStore_CODE)
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
                    public void onBlockUser(ReportstoreNotify notify) {
                        getUserInfo(notify);
                    }
                });
        plzwaitDialog = AppUtils.setupProgressDialog(getContext(),
                getString(R.string.txt_plzwait), null, false, false,
                ProgressDialog.STYLE_SPINNER, 0);
    }

    private void getUserInfo(ReportstoreNotify notify) {
        userEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                plzwaitDialog.cancel();
                user = dataSnapshot.getValue(User.class);
                String key = dataSnapshot.getKey();
                user.setuID(key);
                blockUser(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.users_CODE)
                + notify.getUserID())
                .addListenerForSingleValueEvent(userEventListener);
    }

    private void getStoreInfo(ReportstoreNotify notify) {
        storeEventListener = new ValueEventListener() {
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
                startActivity(intent_openstore);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.store_CODE) +
                notify.getStoreID())
                .addListenerForSingleValueEvent(storeEventListener);
    }

    private void getStoreReport() {
        reportEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    ReportstoreNotify item = dataItem.getValue(ReportstoreNotify.class);
                    String key = dataItem.getKey();
                    item.setId(key);
                    items.add(item);
                }
                itemadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        if (LoginSession.getInstance().getUser().getRole() == 1) {
            dbRef.child(getString(R.string.reportStore_CODE))
                    .orderByChild("district_province")
                    .equalTo(dist_pro)
                    .addListenerForSingleValueEvent(reportEventListener);
        } else {
            dbRef.child(getString(R.string.reportStore_CODE))
                    .addListenerForSingleValueEvent(reportEventListener);
        }

    }

    private void blockUser(User user) {
        if (user.isReportstoreBlocked()) {
            Toast.makeText(getContext(), getString(R.string.txt_blockeduser)
                    , Toast.LENGTH_LONG)
                    .show();
        } else {
            BlockUserDialog blockDialog = new BlockUserDialog();
            blockDialog.setStyle(DialogFragment.STYLE_NORMAL,
                    R.style.AddfoodDialog);
            blockDialog.setBlock(user, Const.BLOCK_TYPE.BLOCK_REPRTSTORE);
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
}
