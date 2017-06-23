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

import com.app.ptt.comnha.Activity.AdapterActivity;
import com.app.ptt.comnha.Adapters.notify_reportfood_adapter;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Dialog.BlockUserDialog;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.ReportfoodNotify;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChooseFood;
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
public class AdminReportFoodFragment extends Fragment {

    ListView listView;
    notify_reportfood_adapter itemadapter;
    ArrayList<ReportfoodNotify> items;
    DatabaseReference dbRef;
    ValueEventListener reportEventListener, foodEventListener, userEventListener;
    String dist_pro;
    Food food = null;
    ProgressDialog plzwaitDialog;
    User user;

    public AdminReportFoodFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_report_food, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_path));
        init(view);
        getFoodReport();
        return view;
    }

    private void init(View view) {
        listView = (ListView) view.findViewById(R.id.listview_reportfood_frag);
        items = new ArrayList<>();
        itemadapter = new notify_reportfood_adapter(getActivity(), items);
        listView.setAdapter(itemadapter);
        itemadapter.setOnItemClickLiestner(new notify_reportfood_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(final ReportfoodNotify notify, Activity activity) {
                plzwaitDialog.show();
                if (!notify.isReadstate()) {
                    String key = notify.getId();
                    ReportfoodNotify childNotify = notify;
                    notify.setReadstate(true);
                    childNotify.setReadstate(true);
                    Log.d("district_province", childNotify.getDistrict_province());
                    Map<String, Object> notifyValue = childNotify.toMap();
                    Map<String, Object> updateChild = new HashMap<>();
                    updateChild.put(getString(R.string.reportFood_CODE) + key,
                            notifyValue);
                    dbRef.updateChildren(updateChild).addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            }
                    );
                } else {
                    getFoodInfo(notify);
                }

            }
        });
        itemadapter.setOnOptionItemClickListener(
                new notify_reportfood_adapter.OnOptionItemClickListener() {
                    @Override
                    public void onDelNotify(final ReportfoodNotify notify) {
                        new AlertDialog.Builder(getContext())
                                .setCancelable(true)
                                .setMessage(getString(R.string.txt_delconfirm))
                                .setPositiveButton(getString(R.string.txt_del), new
                                        DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialogInterface, int i) {
                                                plzwaitDialog.show();
                                                dbRef.child(getString(R.string.reportFood_CODE)
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
                                                                        Toast.LENGTH_LONG)
                                                                        .show();
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
                    public void onBlockUser(ReportfoodNotify notify) {
                        getUserInfo(notify);
                    }
                });
        plzwaitDialog = AppUtils.setupProgressDialog(getContext(),
                getString(R.string.txt_plzwait), null, false, false,
                ProgressDialog.STYLE_SPINNER, 0);
    }

    private void getUserInfo(ReportfoodNotify notify) {
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

    private void getFoodInfo(ReportfoodNotify notify) {
            foodEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    food = dataSnapshot.getValue(Food.class);
                    String key = dataSnapshot.getKey();
                    food.setFoodID(key);
                    ChooseFood.getInstance().setFood(food);
                    Intent intent_openFood = new Intent(getActivity(),
                            AdapterActivity.class);
                    intent_openFood.putExtra(getString(R.string.fragment_CODE)
                            , getString(R.string.frag_foodetail_CODE));
                    intent_openFood.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    plzwaitDialog.dismiss();
                    startActivity(intent_openFood);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            dbRef.child(getString(R.string.food_CODE) +
                    notify.getFoodID())
                    .addListenerForSingleValueEvent(foodEventListener);
    }

    private void blockUser(User user) {
        if (user.isReportfoodBlocked()) {
            Toast.makeText(getContext(), getString(R.string.txt_blockeduser)
                    , Toast.LENGTH_LONG)
                    .show();
        } else {
            BlockUserDialog blockDialog = new BlockUserDialog();
            blockDialog.setStyle(DialogFragment.STYLE_NORMAL,
                    R.style.AddfoodDialog);
            blockDialog.setBlock(user, Const.BLOCK_TYPE.BLOCK_REPRTFOOD);
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

    private void getFoodReport() {
        reportEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    ReportfoodNotify item = dataItem.getValue(ReportfoodNotify.class);
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
        dbRef.child(getString(R.string.reportFood_CODE))
                .orderByChild("district_province")
                .equalTo(dist_pro)
                .addListenerForSingleValueEvent(reportEventListener);
    }
}
