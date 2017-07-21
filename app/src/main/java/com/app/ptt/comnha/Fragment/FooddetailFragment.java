package com.app.ptt.comnha.Fragment;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Activity.PostdetailActivity;
import com.app.ptt.comnha.Adapters.Post_recycler_adapter;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Dialog.AddFoodDialog;
import com.app.ptt.comnha.Dialog.ReportDialog;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.Models.FireBase.UserNotification;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChooseFood;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.AppUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.app.ptt.comnha.Const.Const.REPORTS.REPORT_FOOD;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class FooddetailFragment extends Fragment {
    private static final String LOG = FooddetailFragment.class.getSimpleName();
    DatabaseReference dbRef;
    MenuInflater inflater;
    StorageReference stRef;
    ArrayList<Food> foodList;
    RecyclerView postRecyclerView;
    Post_recycler_adapter postAdapter;
    RecyclerView.LayoutManager postLayoutManager;
    TextView txt_name, txt_price, txt_comment, txtUser;
    ImageView imgv_photo;
    RatingBar ratingBar;
    ArrayList<Post> postlist;
    ValueEventListener postEventListener;
    ActionBar actionBar;
    Toolbar toolbar;
    Food food;
    String keyReport;
    Store store;
    String storeID = "", foodID = "";
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    boolean isConnected = false;
    IntentFilter mIntentFilter;
    ProgressDialog plzw8Dialog;
    Menu pubMenu = null;

    public FooddetailFragment() {
        // Required empty public constructor
    }

    public void setStore(Store store) {
        this.store = store;
    }

    @Override
    public void onStart() {
        super.onStart();
        isConnected = MyService.isNetworkAvailable(getActivity());
        if (!isConnected) {
            Toast.makeText(getContext(), "Offline mode", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fooddetail, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Const.DATABASE_PATH);
//        locaID = ChooseFood.getInstance().getStore().getLocaID();
        stRef = FirebaseStorage.getInstance().getReferenceFromUrl(Const.STORAGE_PATH);
        ref(view);
        if (ChooseFood.getInstance().getFood() != null) {
            foodID = ChooseFood.getInstance().getFood().getFoodID();
            getFood(foodID);
        } else {
            getActivity().onBackPressed();
        }
        return view;
    }

    private void getFood(String id) {

        ValueEventListener foodValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                food = dataSnapshot.getValue(Food.class);
                String key = dataSnapshot.getKey();
                food.setFoodID(key);
                storeID = food.getStoreID();
                getData();
                loadMenu();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.food_CODE) + id)
                .addValueEventListener(foodValueListener);
    }



    private void getCustomUser(final String key) {
        ValueEventListener userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User ownUser = dataSnapshot.getValue(User.class);
                ownUser.setuID(key);
                if(txtUser!=null) {
                    txtUser.post(new Runnable() {
                        @Override
                        public void run() {
                            txtUser.setText(ownUser.getUn());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef.child(getString(R.string.users_CODE)
                + key)
                .addListenerForSingleValueEvent(userValueListener);

    }

    public void getData() {

        txt_comment.setText(food.getComment());
        txt_name.setText(food.getName());
        getCustomUser(food.getUserID());
        txt_price.setText(food.getPrice() + "");
        ratingBar.setRating(food.getTotal() == 0 ? 0 : food.getRating() / food.getTotal());
        if (food.getImgBitmap() == null) {
            StorageReference imgRef = stRef.child(food.getFoodImg());
            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(getContext())
                            .load(uri)
                            .into(imgv_photo);
                    try {
                        Bitmap imgBitmap = ((BitmapDrawable) imgv_photo.getDrawable())
                                .getBitmap();
                        food.setImgBitmap(imgBitmap);
                    } catch (NullPointerException mess) {

                    }
                }
            });
        } else {
            imgv_photo.setImageBitmap(food.getImgBitmap());
            Bitmap imgBitmap = ((BitmapDrawable) imgv_photo.getDrawable())
                    .getBitmap();
            food.setImgBitmap(imgBitmap);
        }

        postEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    Post post = dataItem.getValue(Post.class);
                    post.setPostID(dataItem.getKey());
                    postlist.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        dbRef.child(
                getResources().getString(R.string.posts_CODE))
                .orderByChild("isHidden_foodID")
                .equalTo(false + "_" + foodID)
                .addListenerForSingleValueEvent(postEventListener);

    }

    private void ref(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(getResources()
                    .getColor(R.color.color_notify_reportfood));
        }
        txt_name = (TextView) view.findViewById(R.id.txtv_name_fooddetail);
        txt_price = (TextView) view.findViewById(R.id.txtv_price_fooddetail);
        txt_comment = (TextView) view.findViewById(R.id.txtv_comment_fooddetail);
        imgv_photo = (ImageView) view.findViewById(R.id.imgv_photo_fooddetail);
        ratingBar = (RatingBar) view.findViewById(R.id.rb_rating_fooddetail);
        ratingBar.setIsIndicator(true);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_fooddetail);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        txtUser = (TextView) view.findViewById(R.id.txt_user);
        toolbar.setBackgroundColor(getResources()
                .getColor(R.color.color_notify_reportfood));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setTitle(getString(R.string.txt_fooddetail));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        postlist = new ArrayList<>();
        postRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_post_fooddetail);
        postRecyclerView.setHasFixedSize(true);
        foodList = new ArrayList<>();
        postLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL,
                false);
        postRecyclerView.setLayoutManager(postLayoutManager);
        postAdapter = new Post_recycler_adapter(postlist, getActivity(), stRef);
        postRecyclerView.setAdapter(postAdapter);
        postAdapter.setOnItemClickLiestner(new Post_recycler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(Post post, View itemView) {
                Intent intent_postdetail = new Intent(getContext(),
                        PostdetailActivity.class);
                intent_postdetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityOptionsCompat option_postbanner
                        = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(), itemView.findViewById(R.id.imgv_banner_postitem),
                        "postBanner");
                ChoosePost.getInstance().setPost(post);
                startActivity(intent_postdetail, option_postbanner.toBundle());
            }
        });
        plzw8Dialog = AppUtils.setupProgressDialog(getActivity(),
                getString(R.string.txt_plzwait), null, false, false,
                ProgressDialog.STYLE_SPINNER, 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu = AppUtils.createMenu(menu, returnContentMenuItems());
        pubMenu = menu;
        this.inflater = inflater;
        super.onCreateOptionsMenu(menu, inflater);

    }

    private List<Pair<Integer, String>> returnContentMenuItems() {
        int role = 0;
        String uID = "";

        if (LoginSession.getInstance().getUser() != null) {
            role = LoginSession.getInstance().getUser().getRole();
            uID = LoginSession.getInstance().getUser().getuID();
        }
        List<Pair<Integer, String>> contents = new ArrayList<>();
        if (food != null) {
            if (role > 0) {
                if (food.isHidden()) {
                    if (food.getFoodType() == 0) {
                        contents.add(new Pair<Integer, String>
                                (R.string.txt_acceptfood, getString(R.string.txt_acceptfood)));
                        contents.add(new Pair<Integer, String>
                                (R.string.txt_rejectfood, getString(R.string.txt_rejectfood)));
                    }
                    if (food.getFoodType() == 3) {
                        contents.add(new Pair<Integer, String>
                                (R.string.txt_acceptfoodreport, getString(R.string.txt_acceptfoodreport)));
                        contents.add(new Pair<Integer, String>
                                (R.string.txt_rejectfoodreport, getString(R.string.txt_rejectfoodreport)));
                    }

                } else {
                    contents.add(new Pair<Integer, String>
                            (R.string.txt_rejectfood, getString(R.string.txt_rejectfood)));
//                    contents.add(new Pair<Integer, String>
//                            (R.string.text_hidepost, getString(R.string.text_hidepost)));
                }
            }
            if (uID.equals(food.getUserID())) {
                contents.add(new Pair<Integer, String>
                        (R.string.txt_changeinfo, getString(R.string.txt_changeinfo)));
            } else {
                contents.add(new Pair<Integer, String>
                        (R.string.txt_report, getString(R.string.txt_report)));

            }
        }
        return contents;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.string.txt_report:
                ReportDialog reportDialog = new ReportDialog();
                Map<String,Object> childUpdate=new HashMap<>();
                food.setHidden(true);
                food.setFoodType(3);
                Map<String,Object> newFood=food.toMap();
                childUpdate.put(getString(R.string.food_CODE)+food.getFoodID(),newFood);
                reportDialog.setChildUpdate(childUpdate);
                reportDialog.setReport(REPORT_FOOD, food);
                reportDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AddfoodDialog);
                reportDialog.setOnPosNegListener(new ReportDialog.OnPosNegListener() {
                    @Override
                    public void onPositive(boolean isClicked, Map<String,
                            Object> childUpdate, final Dialog dialog, String key) {
                        if (isClicked) {
                            keyReport=key;
                            dialog.dismiss();
                            plzw8Dialog.show();
                            childUpdate = notificationToUser(childUpdate, 3, 4);
                            dbRef.updateChildren(childUpdate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            plzw8Dialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.show();
                                    plzw8Dialog.dismiss();
                                    Toast.makeText(getActivity(), e.getMessage(),
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
                reportDialog.show(getActivity().getSupportFragmentManager(), "report_food");
                return true;
            case R.string.txt_changeinfo:
                AddFoodDialog addFoodDialog = new AddFoodDialog();
                addFoodDialog.setEditFoood(true, food);
                addFoodDialog.show(getActivity()
                        .getSupportFragmentManager(), "updatefood_dialog");
                return true;
            case R.string.txt_rejectfood:
                    hideFood(-2);
                return true;
            case R.string.txt_acceptfood:
                    showFood(2);
                return true;
            case R.string.txt_acceptfoodreport:
                showFood(1);
                return true;
            case R.string.txt_rejectfoodreport:
                hideFood(-3);
                return true;
            case R.string.txt_delfood:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showFood(int type) {
        plzw8Dialog.show();
        food.setHidden(false);
        Map<String, Object> childUpdate = new HashMap<>();
        switch (food.getFoodType()) {
            case 0:
                childUpdate = notificationToUser(childUpdate, 1, 4);
                break;
            case 3:
                childUpdate = notificationToUser(childUpdate, 2, 4);
                break;
        }
        food.setFoodType(type);
        String key = food.getFoodID();
        Food childFood = food;
        Map<String, Object> foodValue = childFood.toMap();

        childUpdate.put(getString(R.string.food_CODE)
                + key, foodValue);
        dbRef.updateChildren(childUpdate)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                item.setTitle(getString(R.string.text_hidestore));
                                loadMenu();
                                plzw8Dialog.cancel();
                            }
                        }).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        plzw8Dialog.cancel();
                        Toast.makeText(getApplicationContext(),
                                e.getMessage(), Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }
    public boolean loadMenu() {
        if (pubMenu != null) {
            pubMenu = AppUtils.createMenu(pubMenu, returnContentMenuItems());
            if(inflater!=null) {
                super.onCreateOptionsMenu(pubMenu, inflater);
            }
        }
        return false;
    }
    private void hideFood(final int type) {
        new AlertDialog.Builder(getContext())
                .setCancelable(true)
                .setMessage(getString(R.string.txt_hideconfirm))
                .setPositiveButton(getString(R.string.txt_hide),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    final DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                plzw8Dialog.show();
                                Map<String, Object> childUpdate = new HashMap<>();
                                switch (food.getFoodType()) {
                                    case 0:
                                        childUpdate = notificationToUser(childUpdate, -1, 4);
                                        break;
                                    case 3:
                                        childUpdate = notificationToUser(childUpdate, -2, 4);
                                        break;
                                }
                                food.setFoodType(type);
                                food.setHidden(true);
                                String key = food.getFoodID();
                                Food childFood = food;
                                Map<String, Object> foodValue = childFood.toMap();

                                childUpdate.put(getString(R.string.food_CODE)
                                        + key, foodValue);
                                dbRef.updateChildren(childUpdate)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                       loadMenu();
                                                        plzw8Dialog.cancel();
                                                    }
                                                }).addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                plzw8Dialog.cancel();
                                                Toast.makeText(getApplicationContext(),
                                                        e.getMessage(), Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        });
                            }
                        })
                .setNegativeButton(getString(R.string.text_no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                .show();
    }

    @Override
    public void onDetach() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(getResources()
                    .getColor(R.color.color_notify_reportfood));
        }
        super.onDetach();
    }

    public Map<String, Object> notificationToUser(Map<String, Object> childUpdate, int status, int type) {
        switch (status) {
            case 1:
                //accept
                //Notification to user
                for (String mUerId : store.getUsersFollow()) {
                    if(!mUerId.toLowerCase().equals(food.getUserID().toLowerCase())) {
                        UserNotification userNotification = new UserNotification();
                        userNotification.setUserEffectId(food.getUserID());
                        userNotification.setUserEffectName(food.getUserName());
                        userNotification.setFoodId(food.getFoodID());
                        userNotification.setShown(true);
                        userNotification.setStatus(0);
                        userNotification.setStoreID(store.getStoreID());
                        userNotification.setFoodName(food.getName());
                        userNotification.setƠwner(false);
                        userNotification.setType(type);
                        Map<String, Object> userNotificationMap = userNotification.toMap();
                        String key = dbRef.child(getString(R.string.user_notification_CODE) + mUerId).push().getKey();
                        childUpdate.put(getString(R.string.user_notification_CODE) + mUerId + "/" + key, userNotificationMap);
                    }
                }
                //notify to user who create this post
                UserNotification userNotification = new UserNotification();
                userNotification.setFoodId(food.getFoodID());
                userNotification.setShown(true);
                userNotification.setStatus(status);
                userNotification.setFoodName(food.getName());
                userNotification.setStoreID(store.getStoreID());
                userNotification.setƠwner(true);
                userNotification.setType(type);
                Map<String, Object> userNotificationMap = userNotification.toMap();
                String key = dbRef.child(getString(R.string.user_notification_CODE) + food.getUserID()).push().getKey();
                childUpdate.put(getString(R.string.user_notification_CODE) + food.getUserID() + "/" + key, userNotificationMap);

                //if user create this post and user own this store is one person, notify to user own this post
                if (!food.getUserID().toLowerCase().equals(store.getUserID().toLowerCase())) {
                    UserNotification userStoreNotification = new UserNotification();
                    userStoreNotification.setFoodId(food.getFoodID());
                    userStoreNotification.setShown(true);
                    userStoreNotification.setStatus(status);
                    userStoreNotification.setFoodName(food.getName());
                    userStoreNotification.setStoreID(store.getStoreID());
                    userStoreNotification.setƠwner(true);
                    userStoreNotification.setType(type);
                    userStoreNotification.setUserEffectId(food.getUserID());
                    userStoreNotification.setUserEffectName(food.getUserName());
                    Map<String, Object> userStoreNotificationMap = userStoreNotification.toMap();
                    String userStoreNotificationKey = dbRef.child(getString(R.string.user_notification_CODE) + store.getUserID()).push().getKey();
                    childUpdate.put(getString(R.string.user_notification_CODE) + store.getUserID() + "/" + userStoreNotificationKey, userStoreNotificationMap);
                }
                    break;
                    //reject
                    case -1:
                        UserNotification userRejectNotification = new UserNotification();
                        userRejectNotification.setFoodId(food.getFoodID());
                        userRejectNotification.setShown(true);
                        userRejectNotification.setStatus(status);
                        userRejectNotification.setFoodName(food.getName());
                        userRejectNotification.setStoreID(store.getStoreID());
                        userRejectNotification.setƠwner(true);
                        userRejectNotification.setType(type);
                        Map<String, Object> userRejectNotificationMap = userRejectNotification.toMap();
                        String userRejectKey = dbRef.child(getString(R.string.user_notification_CODE) + food.getUserID()).push().getKey();
                        childUpdate.put(getString(R.string.user_notification_CODE) + food.getUserID() + "/" + userRejectKey, userRejectNotificationMap);

                        break;
                    //was reported
                    case 3:
                        UserNotification userWasReportedNotification = new UserNotification();
                        userWasReportedNotification.setFoodId(food.getFoodID());
                        userWasReportedNotification.setShown(true);
                        userWasReportedNotification.setStatus(status);
                        userWasReportedNotification.setFoodName(food.getName());
                        userWasReportedNotification.setStoreID(store.getStoreID());
                        userWasReportedNotification.setƠwner(true);
                        userWasReportedNotification.setUserEffectId(LoginSession.getInstance().getUser().getuID());
                        userWasReportedNotification.setUserEffectName(LoginSession.getInstance().getUser().getUn());
                        userWasReportedNotification.setReportId(keyReport);
                        userWasReportedNotification.setType(type);
                        Map<String, Object> userWasReportedNnMap = userWasReportedNotification.toMap();
                        String userWasReportedNKey = dbRef.child(getString(R.string.user_notification_CODE) + food.getUserID()).push().getKey();
                        childUpdate.put(getString(R.string.user_notification_CODE) + food.getUserID() + "/" + userWasReportedNKey, userWasReportedNnMap);

                        break;
                    //reject report
                    case 2:
                        UserNotification userRejectReport = new UserNotification();
                        userRejectReport.setFoodId(food.getFoodID());
                        userRejectReport.setShown(true);
                        userRejectReport.setStatus(status);
                        userRejectReport.setFoodName(food.getName());
                        userRejectReport.setStoreID(store.getStoreID());
                        userRejectReport.setƠwner(true);
                        userRejectReport.setType(type);
                        Map<String, Object> userRejectReportMap = userRejectReport.toMap();
                        String keyuserRejectReportNoti = dbRef.child(getString(R.string.user_notification_CODE) + food.getUserID()).push().getKey();
                        childUpdate.put(getString(R.string.user_notification_CODE) + food.getUserID() + "/" + keyuserRejectReportNoti, userRejectReportMap);

                        break;
                    //accept report userAcceptReport  keyuserAcceptReportNoti
                    case -2:
                        UserNotification userAcceptReport = new UserNotification();
                        userAcceptReport.setFoodId(food.getFoodID());
                        userAcceptReport.setShown(true);
                        userAcceptReport.setStatus(status);
                        userAcceptReport.setFoodName(food.getName());
                        userAcceptReport.setStoreID(store.getStoreID());
                        userAcceptReport.setƠwner(true);
                        userAcceptReport.setType(type);
                        Map<String, Object> userAcceptReportMap = userAcceptReport.toMap();
                        String keyuserAcceptReportNoti = dbRef.child(getString(R.string.user_notification_CODE) + food.getUserID()).push().getKey();
                        childUpdate.put(getString(R.string.user_notification_CODE) + food.getUserID() + "/" + keyuserAcceptReportNoti, userAcceptReportMap);
                        break;
                }

                return childUpdate;
        }

}
