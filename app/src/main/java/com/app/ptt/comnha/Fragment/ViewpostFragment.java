package com.app.ptt.comnha.Fragment;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Activity.AdapterActivity;
import com.app.ptt.comnha.Adapters.Comment_rcyler_adapter;
import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Models.FireBase.Comment;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Image;
import com.app.ptt.comnha.Models.FireBase.Notification;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.app.ptt.comnha.SingletonClasses.OpenAlbum;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewpostFragment extends Fragment implements View.OnClickListener {
    private String postID;
    ArrayList<Bitmap> listBitmap;
    private static final String LOG = ViewpostFragment.class.getSimpleName();
    ImageView img_option, img_post;
    TextView txt_un, txt_date, txt_title,
            txt_content, txt_likenumb, btn_like, btn_comment,
            txt_gia, txt_vs, txt_pv, txt_monan, txt_giamonan;
    Button btn_sendcomment;
    boolean deletesuccess;
    RatingBar ratingBar;
    EditText edt_comment;
    Food mainFood;
    ArrayList<Notification> notifications;
    RecyclerView mRecyclerView, mRecyclerViewAlbum;
    RecyclerView.LayoutManager mlayoutManager, mlayoutManagerAlbum;
    RecyclerView.Adapter mAdapter, mAdapterAlbum;
    Food postFood;
    ArrayList<Comment> comment_List;
    ArrayList<Image> albumList;
    DatabaseReference dbRef;
    Map<String, Object> childUpdates;
    StorageReference storeRef;
    ChildEventListener commentChildEventListener, albumChildEventListener;
    ValueEventListener postValueEventListener, locaValueEventListener;
    LinearLayout linearAlbum;
    ProgressDialog mProgressDialog;
    Store store;
    private TextView txt_albums;
    Bitmap mBitmap;
    boolean isConnected = true;
    IntentFilter mIntentFilter;
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastSendAddress)) {
                Log.i(LOG + ".onReceive form Service", "isConnected= " + intent.getBooleanExtra("isConnected", false));
                if (intent.getBooleanExtra("isConnected", false)) {
                    isConnected = true;
                } else
                    isConnected = false;
            }
        }
    };

    public ViewpostFragment() {
        // Required empty public constructor
    }

    Post post;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        isConnected = MyService.returnIsNetworkConnected();
        View view = inflater.inflate(R.layout.fragment_viewpost, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        storeRef = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebaseStorage_path));
        postID = ChoosePost.getInstance().getPostID();
        listBitmap = new ArrayList<>();
        albumList = new ArrayList<>();
        childUpdates = new HashMap<>();
        anhxa(view);
        postFood = null;
        mProgressDialog.show();
//        tracklocaValueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                trackLocation = dataSnapshot.getValue(TrackLocation.class);
//                trackLocation.setLocaID(dataSnapshot.getKey());
//                hastrack = true;
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
        locaValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                store = dataSnapshot.getValue(Store.class);
//                store.setLocaID(dataSnapshot.getKey());
                Log.d("myLocation", "have changed");
//                dbRef.child( getString(R.string.usertrackloca_CODE)
//                        + post.getUserId() + "/" + myLocation.getLocaID())
//                        .addListenerForSingleValueEvent(tracklocaValueEventListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        albumChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
//                    Toast.makeText(getActivity(), dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("checkListenerFromImages", "have changed");
                    final Image image = dataSnapshot.getValue(Image.class);
//                    image.setImageID(dataSnapshot.getKey());
//                    stRef.child(getResources().getString(R.string.images_CODE)
//                            + image.getName())
//                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            image.setPath(uri);
//                            albumList.add(image);
//                            mAdapterAlbum.notifyDataSetChanged();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
////                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
                } catch (NullPointerException | IllegalStateException mess) {

                }
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

        postValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
//                Log.d("post", "have changed");
//                post = dataSnapshot.getValue(Post.class);
//                post.setPostID(dataSnapshot.getKey());
//                txt_title.setText(post.getTitle());
//                txt_date.setText(post.getDate());
//                txt_content.setText(post.getContent());
//                txt_un.setText(post.getUserName());
//                Picasso.with(getContext()).load(post.getHinh()).into(img_post);
//                txt_likenumb.setText(post.getLikeCount() + " Likes   " + post.getCommentCount() + " Comments");
//                txt_price.setText(post.getPrice() + "");
//                txt_pv.setText(post.getPhucvu() + "");
//                txt_vs.setText(post.getVesinh() + "");
//                if(post.getType()==2) {
//                    txt_giamonan.setVisibility(View.INVISIBLE);
//                    txt_monan.setVisibility(View.INVISIBLE);
//                    ratingBar.setVisibility(View.INVISIBLE);
//                }else{
//                    txt_giamonan.setVisibility(View.VISIBLE);
//                    txt_monan.setVisibility(View.VISIBLE);
//                    ratingBar.setVisibility(View.VISIBLE);
//                    postFood=post.getFood();
//                    txt_giamonan.setText(postFood.getPrice()+" đ");
//                    txt_monan.setText("Tên món: "+postFood.getName());
//                    ratingBar.setIsIndicator(true);
//                    ratingBar.setNumStars(3);
//                    ratingBar.setStepSize(1);
//                    ratingBar.setRating(postFood.getRating());
//                }

                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        commentChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                Log.d("comment", "have changed");
//                Comment comment = dataSnapshot.getValue(Comment.class);
//                comment.setCommentID(dataSnapshot.getKey());
//                comment_List.add(comment);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        Log.d("postID", postID);
        dbRef.child(
                getString(R.string.posts_CODE) + postID)
                .addListenerForSingleValueEvent(postValueEventListener);
        dbRef.child(
                getString(R.string.postcomment_CODE) + postID)
                .addChildEventListener(commentChildEventListener);
        dbRef.child(getString(R.string.images_CODE)).orderByChild("postID").equalTo(postID)
                .addChildEventListener(albumChildEventListener);
        if (ChoosePost.getInstance().getType() == 1) {
            dbRef.child(getResources().getString(R.string.food_CODE))
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if (dataSnapshot.getKey().equals(ChoosePost.getInstance().getFoodID())) {
                                Food food = dataSnapshot.getValue(Food.class);
                                mainFood = food;
//                                    mainFood.setFoodID(dataSnapshot.getKey());

                            }

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
                    });
        }
        if (MyService.getUserAccount() != null) {
//            dbRef.child(getResources().getString(R.string.notification_CODE) + ChoosePost.getInstance().getUserID()).
//                    addChildEventListener(new ChildEventListener() {
//                                              @Override
//                                              public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                                                  Notification notification = dataSnapshot.getValue(Notification.class);
//                                                  notification.setNotiID(dataSnapshot.getKey());
//                                                  if (notification.getPost() != null)
////                                                      if (notification.getPost().getPostID().equals(postID))
//                                                          notifications.add(notification);
//
//                                              }
//
//                                              @Override
//                                              public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                                              }
//
//                                              @Override
//                                              public void onChildRemoved(DataSnapshot dataSnapshot) {
//                                              }
//
//                                              @Override
//                                              public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                                              }
//
//                                              @Override
//                                              public void onCancelled(DatabaseError databaseError) {
//
//                                              }
//                                          }
//                    );
        }
        return view;
    }

    @Override
    public void onStart() {
        isConnected = MyService.returnIsNetworkConnected();
        if (!isConnected) {
            Toast.makeText(getContext(), "Offline mode", Toast.LENGTH_SHORT).show();
        }
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
        getContext().registerReceiver(broadcastReceiver, mIntentFilter);
        super.onStart();
        Log.i(ViewpostFragment.class.getSimpleName(), "OK");
        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                dbRef.child(getString(R.string.locations_CODE) + post.getLocaID())
//                        .addListenerForSingleValueEvent(locaValueEventListener);

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ChoosePost.getInstance().setPostID(null);
        ChoosePost.getInstance().setTinh(null);
        ChoosePost.getInstance().setHuyen(null);
    }

    private void anhxa(View view) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.txt_plzwait));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        img_post = (ImageView) view.findViewById(R.id.frg_viewrev_img_user);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo);
        img_option = (ImageView) view.findViewById(R.id.frg_viewreview_imgoption);
        txt_albums = (TextView) view.findViewById(R.id.frg_frg_viewpost_txt_album);
        linearAlbum = (LinearLayout) view.findViewById(R.id.frg_viewrev_lnearAlbum);
        txt_gia = (TextView) view.findViewById(R.id.frg_frg_viewpost_txt_gia);
        txt_vs = (TextView) view.findViewById(R.id.frg_frg_viewpost_txt_vesinh);
        txt_pv = (TextView) view.findViewById(R.id.frg_frg_viewpost_txt_phucvu);
        txt_un = (TextView) view.findViewById(R.id.frg_viewrev_txtv_username);
        txt_date = (TextView) view.findViewById(R.id.frg_viewrev_txtv_postdate);
        txt_title = (TextView) view.findViewById(R.id.frg_viewrev_txtv_tittle);
        txt_content = (TextView) view.findViewById(R.id.frg_viewrev_txtv_content);
        txt_likenumb = (TextView) view.findViewById(R.id.frg_viewrev_txtv_likenumb);
        btn_like = (TextView) view.findViewById(R.id.frg_viewrev_txtv_like);
        ratingBar = (RatingBar) view.findViewById(R.id.item_rcyler_food_rb_rating);
        txt_monan = (TextView) view.findViewById(R.id.frg_viewrev_txtv_monan);
        txt_giamonan = (TextView) view.findViewById(R.id.frg_viewrev_txtv_giamon);
        btn_comment = (TextView) view.findViewById(R.id.frg_viewrev_txtv_comment);
        btn_sendcomment = (Button) view.findViewById(R.id.frg_viewrev_btn_sendcomment);
        edt_comment = (EditText) view.findViewById(R.id.frg_viewrev_edt_comment);
        comment_List = new ArrayList<Comment>();
        notifications = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_viewrev_rcyler_comments);
        mRecyclerView.setHasFixedSize(true);
        mlayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mlayoutManager);
        mAdapter = new Comment_rcyler_adapter(comment_List);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerViewAlbum = (RecyclerView) view.findViewById(R.id.frg_frg_viewpost_rcyler_album);
        mRecyclerViewAlbum.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerViewAlbum.setLayoutManager(gridLayoutManager);
        mRecyclerViewAlbum.setHorizontalScrollBarEnabled(true);
//        mAdapterAlbum = new Photo_recycler_adapter(albumList, getActivity());
        mRecyclerViewAlbum.setAdapter(mAdapterAlbum);

        linearAlbum.setOnClickListener(this);
//        edt_comment.setFocusable(false);
        btn_sendcomment.setOnClickListener(this);
        btn_like.setOnClickListener(this);
        btn_comment.setOnClickListener(this);
//        if (LoginSession.getInstance().getUserID() == null) {
//            edt_comment.setVisibility(View.INVISIBLE);
//        } else {
//            edt_comment.setVisibility(View.VISIBLE);
//        }
        img_option.setOnClickListener(this);
        txt_albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (albumList.size() > 0) {
                    Intent intent = new Intent(getActivity(), AdapterActivity.class);
                    intent.putExtra(getString(R.string.fragment_CODE),
                            getString(R.string.frg_viewalbum_CODE));
                    intent.putExtra(getString(R.string.fromFrag),
                            getString(R.string.frg_viewpost_CODE));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    OpenAlbum.getInstance().setPostID(postID);
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(broadcastReceiver);
        dbRef.removeEventListener(locaValueEventListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frg_viewrev_txtv_like:
                break;
            case R.id.frg_viewrev_btn_sendcomment:
                if (isConnected) {
                    if (!edt_comment.getText().toString().equals("")) {
//                        Comment newComment = new Comment();
//                        newComment.setContent(edt_comment.getText().toString());
//                        newComment.setUsername(LoginSession.getInstance().getUsername());
//                        newComment.setUserID(LoginSession.getInstance().getUserID());
//                        newComment.setDate(new Times().getDate());
//                        newComment.setTime(new Times().getTime());
//                        newComment.setPostID(postID);
//                        edt_comment.setText(null);
//                        String key = dbRef.child(getResources().getString(R.string.postcomment_CODE) + postID).push().getKey();
//                        Map<String, Object> commentValues = newComment.toMap();
//                        Map<String, Object> childUpdates = new HashMap<String, Object>();
//                        childUpdates.put(
//                                getResources().getString(R.string.postcomment_CODE) + postID + "/"
//                                        + key, commentValues);
//                        childUpdates.put(
//                                getResources().getString(R.string.posts_CODE)
//                                        + postID + "/commentCount", comment_List.size() + 1);
//                    childUpdates.put(tinh + "/" + huyen + "/" +
//                            getResources().getString(R.string.locationpost_CODE) + post.getLocaID() + "/"
//                            + postID + "/commentCount", comment_List.size() + 1);
//                    childUpdates.put(getResources().getString(R.string.locauserpost_CODE)
//                            + post.getLocaID() + "/"
//                            + post.getUid() + "/"
//                            + postID + "/commentCount", comment_List.size() + 1);
//                        childUpdates.put(
//                                getResources().getString(R.string.userpost_CODE)
//                                + post.getUserId() + "/"
//                                + postID + "/commentCount", comment_List.size() + 1);
//                        dbRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                if (databaseError != null) {
//                                    Log.e("updateChildrenComment", databaseError.getMessage());
//                                     } else {
//                                    MyService.setChangeContent("justCommend");
//                                }
//                            }
//                        });
                    }
                } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
                break;
            case R.id.frg_viewrev_txtv_comment:
                edt_comment.requestFocusFromTouch();
                break;
            case R.id.frg_viewrev_lnearAlbum:
                break;
            case R.id.frg_viewreview_imgoption:
                if (isConnected) {
                    AnimationUtils.rotate90postoption(img_option);
                    final PopupMenu popupMenu = new PopupMenu(getActivity(), img_option, Gravity.START);
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu_reviewdetial, popupMenu.getMenu());
                    Menu menu = popupMenu.getMenu();
                    if (MyService.getUserAccount() != null) {
//                        if(MyService.getUserAccount().getRole())
//                        {
////                            if(post.getVisible()){
////                                menu.findItem(R.id.menu_postdetail_phandoi).setVisible(true);
////                            }else
////                                menu.findItem(R.id.menu_postdetail_dongy).setVisible(true);
//                            menu.findItem(R.id.menu_postdetail_xoa).setVisible(true);
//                            menu.findItem(R.id.menu_postdetail_sua).setVisible(true);
//                        }
////                        if(MyService.getUserAccount().getId().equals(post.getUserId()))
////                        {
////                            menu.findItem(R.id.menu_postdetail_sua).setVisible(true);
////                            if(!MyService.getUserAccount().getRole())
////                                menu.findItem(R.id.menu_postdetail_ycxoa).setVisible(true);
////                        }
////                        else if(MyService.getUserAccount()!=null)
////                        {
////                            menu.findItem(R.id.menu_postdetail_report).setVisible(true);
////                        }
                    }
                    popupMenu.show();
                    popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                        @Override
                        public void onDismiss(PopupMenu menu) {
                            AnimationUtils.rotate90postoptionBack(img_option);
                        }
                    });
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_postdetail_ycxoa:
                                    android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(getContext());
                                    builder1.setTitle("Thông báo");
                                    builder1.setMessage("Bạn có yêu cầu xóa bài đăng của bạn không?");
                                    builder1.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
//                                            EditPost.getInstance().setPost(post);
//                                            ReasonPostDialogFragment temp = new ReasonPostDialogFragment();
//                                            temp.setType(3);
//                                            temp.show(getActivity().getSupportFragmentManager(), "fragment_reportPost");
                                        }
                                    });
                                    builder1.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    builder1.show();
                                    return true;
//                                case R.id.menu_postdetail_dongy:
//                                    if(!post.getVisible()) {
//                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
//                                        builder.setTitle("Thông báo");
//                                        builder.setMessage("Bạn đồng ý đăng hiện bài đăng này?");
//                                        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                dialog.cancel();
//                                                mProgressDialog = ProgressDialog.show(getActivity(),
//                                                        getResources().getString(R.string.txt_plzwait),
//                                                        "Đang xử lý",
//                                                        true, false);
//                                                post.setVisible(true);
//                                                long giaTong = myLocation.getGiaTong() + post.getPrice(),
//                                                        vsTong = myLocation.getVsTong() + post.getVesinh(),
//                                                        pvTong = myLocation.getPvTong() + post.getPhucvu(),
//                                                        size = myLocation.getSize() + 1;
//                                                myLocation.setGiaTong(giaTong);
//                                                myLocation.setVsTong(vsTong);
//                                                myLocation.setPvTong(pvTong);
//                                                myLocation.setSize(size);
//                                                myLocation.setGiaAVG(giaTong / size);
//                                                myLocation.setVsAVG(vsTong / size);
//                                                myLocation.setPvAVG(pvTong / size);
//
//                                                Map<String, Object> updateLocal = myLocation.toMap();
//                                                if (ChoosePost.getInstance().getType() == 1 && mainFood!=null) {
//                                                    float a = mainFood.getRating();
//                                                    float b = ((a + EditPost.getInstance().getPost().getFood().getRating()) / 2);
//                                                    mainFood.setRating(b);
//                                                    Map<String, Object> updateFood = mainFood.toMap();
//                                                    childUpdates.put(getResources().getString(R.string.thucdon_CODE) + mainFood.getFoodID(), updateFood);
//                                                }
//                                                childUpdates.put(getResources().getString(R.string.posts_CODE) + postID, post);
//                                                childUpdates.put(getResources().getString(R.string.locations_CODE) + myLocation.getLocaID(), updateLocal);
//                                                dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                        Notification notification = new Notification();
//                                                        String key1 = dbRef.child(getResources().getString(R.string.notification_CODE) + post.getUserId()).push().getKey();
//                                                        notification.setAccount(MyService.getUserAccount());
//                                                        notification.setDate(new Times().getDate());
//                                                        notification.setTime(new Times().getTime());
//                                                        notification.setStore(myLocation);
//                                                        notification.setType(7);
//                                                        notification.setPost(post);
//                                                        notification.setReaded(false);
//                                                        notification.setTo(post.getUserId());
//                                                        Map<String, Object> notificationValue = notification.toMap();
//                                                        childUpdates.put(getResources().getString(R.string.notification_CODE) + post.getUserId() + "/" + key1, notificationValue);
//                                                        dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                if (task.isComplete()) {
//                                                                    Toast.makeText(getContext(), "Đã thông qua bài viết", Toast.LENGTH_SHORT).show();
//                                                                    mProgressDialog.dismiss();
//                                                                    getActivity().finish();
//                                                                } else {
//                                                                    mProgressDialog.dismiss();
//                                                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                                                }
//                                                            }
//                                                        });
//                                                    }
//                                                }).addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//                                                        Toast.makeText(getContext(), "Lỗi!!", Toast.LENGTH_SHORT).show();
//                                                        mProgressDialog.dismiss();
//                                                    }
//                                                });
//                                            }
//                                        });
//                                        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                dialog.cancel();
//                                            }
//                                        });
//                                        builder.show();
//                                    }
//                                    return true;
//                                case R.id.menu_postdetail_phandoi:
//                                    if (isConnected) {
//                                        if(post.getVisible()) {
//                                            AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
//                                            builder2.setTitle("Thông báo");
//                                            builder2.setMessage("Bạn muốn ẩn quán ăn này bài đăng này?");
//                                            builder2.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.cancel();
//                                                    mProgressDialog = ProgressDialog.show(getActivity(),
//                                                            getResources().getString(R.string.txt_plzwait),
//                                                            "Đang xử lý",
//                                                            true, false);
//
//
//                                                    post.setVisible(false);
//                                                    long giaTong = myLocation.getGiaTong() - post.getPrice(),
//                                                            vsTong = myLocation.getVsTong() - post.getVesinh(),
//                                                            pvTong = myLocation.getPvTong() - post.getPhucvu(),
//                                                            size = myLocation.getSize() - 1;
//                                                    myLocation.setGiaTong(giaTong);
//                                                    myLocation.setVsTong(vsTong);
//                                                    myLocation.setPvTong(pvTong);
//                                                    myLocation.setSize(size);
//                                                    myLocation.setGiaAVG(giaTong / size);
//                                                    myLocation.setVsAVG(vsTong / size);
//                                                    myLocation.setPvAVG(pvTong / size);
//                                                    Map<String, Object> updateLocal = myLocation.toMap();
//                                                    if (ChoosePost.getInstance().getType() == 1) {
//                                                        float a = mainFood.getRating();
//                                                        float b = (2 * a - EditPost.getInstance().getPost().getFood().getRating());
//                                                        mainFood.setRating(b);
//                                                        Map<String, Object> updateFood = mainFood.toMap();
//                                                        childUpdates.put(getResources().getString(R.string.thucdon_CODE) + mainFood.getFoodID(), updateFood);
//                                                    }
//                                                    childUpdates.put(getResources().getString(R.string.locations_CODE) + myLocation.getLocaID(), updateLocal);
//                                                    Map<String, Object> updatePost = post.toMap();
//                                                    childUpdates.put(getResources().getString(R.string.posts_CODE) + postID, updatePost);
//                                                    dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            mProgressDialog.dismiss();
//                                                            EditPost.getInstance().setPost(post);
//                                                            ReasonPostDialogFragment temp = new ReasonPostDialogFragment();
//                                                            temp.setType(1);
//                                                            temp.show(getActivity().getSupportFragmentManager(), "fragment_reasonPost");
//                                                            //getActivity().finish();
//                                                        }
//                                                    }).addOnFailureListener(new OnFailureListener() {
//                                                        @Override
//                                                        public void onFailure(@NonNull Exception e) {
//                                                            Toast.makeText(getContext(), "Lỗi khi ẩn", Toast.LENGTH_SHORT).show();
//                                                            mProgressDialog.dismiss();
//                                                            getActivity().finish();
//                                                            return;
//
//                                                        }
//                                                    });
//
//                                                }
//                                            });
//
//                                            builder2.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.cancel();
//                                                }
//                                            });
//                                            builder2.show();
//                                        }
//
//
//                                        //
//
//                                    } else
//                                        Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
//                                    return true;
                                case R.id.menu_postdetail_xoa:
                                    android.app.AlertDialog.Builder builder3 = new android.app.AlertDialog.Builder(getContext());
                                    builder3.setTitle("Thông báo");
                                    builder3.setMessage("Bạn có muốn xóa bài đăng này không?");
                                    builder3.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            mProgressDialog = ProgressDialog.show(getActivity(),
                                                    getResources().getString(R.string.txt_plzwait),
                                                    "Đang xóa",
                                                    true, false);
                                            childUpdates = new HashMap<>();
//                                    if(post.getVisible()) {
//                                        long giaTong = store.getGiaTong() - post.getPrice(),
//                                                vsTong = store.getVsTong() - post.getVesinh(),
//                                                pvTong = store.getPvTong() - post.getPhucvu(),
//                                                size = store.getSize() - 1;
//                                        store.setGiaTong(giaTong);
//                                        store.setVsTong(vsTong);
//                                        store.setPvTong(pvTong);
//                                        store.setSize(size);
//                                        store.setGiaAVG(giaTong / size);
//                                        store.setVsAVG(vsTong / size);
//                                        store.setPvAVG(pvTong / size);
//                                        Map<String, Object> updateLocal = store.toMap();
//                                        if (ChoosePost.getInstance().getType() == 1) {
//                                            float a = mainFood.getRating();
//                                            float b = (2 * a - EditPost.getInstance().getPost().getFood().getRating());
//                                            mainFood.setRating(b);
//                                            Map<String, Object> updateFood = mainFood.toMap();
//                                            childUpdates.put(getResources().getString(R.string.thucdon_CODE) + mainFood.getFoodID(), updateFood);
//                                        }

//                                        childUpdates.put(getResources().getString(R.string.locations_CODE) + store.getLocaID(), updateLocal);
//                                    }
//                                            deletesuccess = true;
//                                            for(Comment comment:comment_List){
//                                                if (deletesuccess) {
//                                                    dbRef.child(getResources().getString(R.string.postcomment_CODE) + comment.getCommentID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            deletesuccess = true;
//                                                        }
//                                                    }).addOnFailureListener(new OnFailureListener() {
//                                                        @Override
//                                                        public void onFailure(@NonNull Exception e) {
//                                                            deletesuccess = false;
//                                                        }
//                                                    });
//                                                } else {
//                                                    Toast.makeText(getContext(), "Xóa comment không thành công", Toast.LENGTH_SHORT).show();
//                                                    mProgressDialog.dismiss();
//                                                    getActivity().finish();
//                                                    return;
//                                                }
//                                            }
//                                            for (Image img : albumList) {
//                                                if (deletesuccess) {
//                                                    dbRef.child(getResources().getString(R.string.images_CODE) + img.getImageID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            deletesuccess = true;
//                                                        }
//                                                    }).addOnFailureListener(new OnFailureListener() {
//                                                        @Override
//                                                        public void onFailure(@NonNull Exception e) {
//                                                            deletesuccess = false;
//                                                        }
//                                                    });
//                                                } else {
//                                                    Toast.makeText(getContext(), "Xóa hình không thành công", Toast.LENGTH_SHORT).show();
//                                                    mProgressDialog.dismiss();
//                                                    getActivity().finish();
//                                                    return;
//                                                }
//                                            }
//                                            for (Notification not : notifications)
//                                                if (deletesuccess) {
//                                                    dbRef.child(getResources().getString(R.string.notification_CODE) + ChoosePost.getInstance().getUserID() + "/" + not.getNotiID()).
//                                                            removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            deletesuccess = true;
//                                                        }
//                                                    }).addOnFailureListener(new OnFailureListener() {
//                                                        @Override
//                                                        public void onFailure(@NonNull Exception e) {
//                                                            deletesuccess = false;
//                                                        }
//                                                    });
//                                                } else {
//                                                    Toast.makeText(getContext(), "Xóa thông báo không thành công", Toast.LENGTH_SHORT).show();
//                                                    mProgressDialog.dismiss();
//                                                    getActivity().finish();
//                                                    return;
//                                                }
//                                            for (Image img : albumList) {
//                                                Log.i("OK", "img:" + img.getName());
//                                                if (deletesuccess) {
//                                                    StorageReference myChildRef = stRef.child(
//                                                            getResources().getString(R.string.images_CODE)
//                                                                    + img.getName());
//                                                    Log.i("OK", "url:" + getResources().getString(R.string.images_CODE)
//                                                            + img.getName());
//                                                    myChildRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            Log.i("OK", "deletesuccess:" + deletesuccess);
//                                                            deletesuccess = true;
//                                                        }
//                                                    }).addOnFailureListener(new OnFailureListener() {
//                                                        @Override
//                                                        public void onFailure(@NonNull Exception e) {
//                                                            Log.i("FAIL", "deletesuccess:" + deletesuccess);
//                                                            deletesuccess = false;
//                                                        }
//                                                    });
//                                                } else {
//                                                    Toast.makeText(getContext(), "Xóa hình không thành công", Toast.LENGTH_SHORT).show();
//                                                    mProgressDialog.dismiss();
//                                                    getActivity().finish();
//                                                    return;
//                                                }
//                                            }
//                                            if (deletesuccess) {
//                                                dbRef.child(getResources().getString(R.string.posts_CODE) + post.getPostID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                        deletesuccess = true;
//                                                    }
//                                                }).addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//                                                        deletesuccess = false;
//                                                    }
//                                                });
//                                            } else {
//                                                Toast.makeText(getContext(), "Xóa bài đăng không thành công", Toast.LENGTH_SHORT).show();
//                                                mProgressDialog.dismiss();
//                                                getActivity().finish();
//                                            }
//                                            if (deletesuccess) {
//                                                if (post.getIsReported()) {
//                                                    Notification notification = new Notification();
//                                                    String key1 = dbRef.child(getResources().getString(R.string.notification_CODE) + ChoosePost.getInstance().getUserID()).push().getKey();
//                                                    notification.setAccount(MyService.getUserAccount());
//                                                    notification.setDate(new Times().getDate());
//                                                    notification.setTime(new Times().getTime());
//                                                    notification.setPost(EditPost.getInstance().getPost());
//                                                    notification.setStore(store);
//                                                    notification.setType(8);
//                                                    notification.setReaded(false);
//                                                    childUpdates = new HashMap<String, Object>();
//                                                    Map<String, Object> notificationValue = notification.toMap();
//                                                    childUpdates.put(getResources().getString(R.string.notification_CODE) + ChoosePost.getInstance().getUserID() + "/" + key1, notificationValue);
//                                                    dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            if (task.isComplete()) {
//                                                                Notification notification = new Notification();
//                                                                String key1 = dbRef.child(getResources().getString(R.string.notification_CODE) + post.getReporter()).push().getKey();
//                                                                notification.setAccount(MyService.getUserAccount());
//                                                                notification.setDate(new Times().getDate());
//                                                                notification.setTime(new Times().getTime());
//                                                                notification.setPost(EditPost.getInstance().getPost());
//                                                                notification.setStore(store);
//                                                                notification.setType(9);
//                                                                notification.setReaded(false);
//                                                                childUpdates = new HashMap<String, Object>();
//                                                                Map<String, Object> notificationValue = notification.toMap();
//                                                                childUpdates.put(getResources().getString(R.string.notification_CODE) + post.getReporter() + "/" + key1, notificationValue);
//                                                                dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                    @Override
//                                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                                        if (task.isComplete()) {
//                                                                            Toast.makeText(getContext(), "Xóa thành công. Đã thông báo tới người sử dụng", Toast.LENGTH_SHORT).show();
//                                                                            mProgressDialog.dismiss();
//                                                                            getActivity().finish();
//                                                                        } else {
//                                                                            mProgressDialog.dismiss();
//                                                                            getActivity().finish();
//                                                                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                                                        }
//                                                                    }
//                                                                });
//
//                                                            } else {
//                                                                mProgressDialog.dismiss();
//                                                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                                                getActivity().finish();
//                                                            }
//                                                        }
//                                                    });
//                                                } else {
//                                                    Notification notification = new Notification();
//                                                    String key1 = dbRef.child(getResources().getString(R.string.notification_CODE) + ChoosePost.getInstance().getUserID()).push().getKey();
//                                                    notification.setAccount(MyService.getUserAccount());
//                                                    notification.setDate(new Times().getDate());
//                                                    notification.setTime(new Times().getTime());
//                                                    notification.setStore(store);
//                                                    notification.setPost(EditPost.getInstance().getPost());
//                                                    notification.setType(10);
//                                                    notification.setReaded(false);
//                                                    childUpdates = new HashMap<String, Object>();
//                                                    Map<String, Object> notificationValue = notification.toMap();
//                                                    childUpdates.put(getResources().getString(R.string.notification_CODE) + ChoosePost.getInstance().getUserID() + "/" + key1, notificationValue);
//                                                    dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
//                                                            mProgressDialog.dismiss();
//                                                            getActivity().finish();
//
//                                                        }
//                                                    });
//                                                }
//
//                                            }
                                        }
                                    });
//                                    builder3.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.cancel();
//                                        }
//                                    });
//                                    builder3.show();
                                    return true;

                                case R.id.menu_postdetail_sua:
//                                    if (isConnected) {
//                                        EditPost.getInstance().setPost(post);
//                                        EditPost.getInstance().setAlbumList(albumList);
//                                        if(ChoosePost.getInstance().getType() == 1 && mainFood!=null){
//                                            EditPost.getInstance().setDanhgia(mainFood.getRating());
//                                               }
//
//                                        EditPostDialogFragment editPostDialog = new EditPostDialogFragment();
//                                        editPostDialog.show(getActivity().getSupportFragmentManager(), "fragment_editPost");
//
//                                        return true;
//                                    } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.menu_fooddetail_report:
//                                    if (isConnected) {
//                                       EditPost.getInstance().setPost(post);
//                                        ReasonStoreDialogFragment temp = new ReasonStoreDialogFragment();
//                                        temp.setType(2);
//                                        temp.show(getActivity().getSupportFragmentManager(), "fragment_reportStore");
//                                        return true;
//                                    } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
                                    return true;
                                default:
                                    return false;
                            }
                        }

                    });
                } else
                    Toast.makeText(getContext(), "You are offline mode", Toast.LENGTH_SHORT).show();
                break;
        }
    }

//    private void deletepost() {
//        if (hastrack) {
//            int trackcount = trackLocation.getCountTrack() - 1;
//            if (trackLocation.getCountTrack() == 0) {
//                dbRef.child(
//                        getString(R.string.usertrackloca_CODE) +
//                        post.getUserId()
//                        + "/" + myLocation.getLocaID()).removeValue()
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                delete();
//                            }
//                        });
//            } else if (trackLocation.getCountTrack() > 0) {
//                trackLocation.setCountTrack(trackcount);
//                Map<String, Object> updatechild = new HashMap<>();
//                updatechild.put(
//                        getString(R.string.usertrackloca_CODE) +
//                        post.getUserId()
//                        + "/" + myLocation.getLocaID(), trackLocation);
//                dbRef.updateChildren(updatechild)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                delete();
//                            }
//                        });
//            }
//        } else {
////            Toast.makeText(getContext(),"false",Toast.LENGTH_SHORT).show();
//            delete();
//        }
//    }

    private void delete() {
//        long size = store.getSize() - 1;
//        store.setSize(size);
//        if (size != 0) {
//            store.setGiaTong(store.getGiaTong() - post.getPrice());
//            store.setVsTong(store.getVsTong() - post.getVesinh());
//            store.setPvTong(store.getPvTong() - post.getPhucvu());
//            store.setGiaAVG(store.getGiaTong() / size);
//            store.setVsAVG(store.getVsTong() / size);
//            store.setPvAVG(store.getPvTong() / size);
//        } else {
//            store.setGiaTong(0);
//            store.setVsTong(0);
//            store.setPvTong(0);
//            store.setGiaAVG(0);
//            store.setVsAVG(0);
//            store.setPvAVG(0);
//        }

//        store.setTongAVG((store.getGiaAVG() + store.getVsAVG() +
//                store.getPvAVG()) / 3);
//        Map<String, Object> updateChild = new HashMap<>();
//        updateChild.put(
//                getString(R.string.locations_CODE) + store.getLocaID(), store);
//        dbRef.updateChildren(updateChild).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                dbRef.child(
//                         getString(R.string.posts_CODE) + postID).removeValue()
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (!task.isComplete()) {
//                                    mProgressDialog.dismiss();
//                                    Toast.makeText(getContext(),
//                                            task.getException().getMessage(),
//                                            Toast.LENGTH_SHORT).show();
//                                } else {
//                                    dbRef.child(
//                                            getString(R.string.postcomment_CODE) +
//                                                    postID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (!task.isComplete()) {
//                                                Toast.makeText(getContext(),
//                                                        task.getException().getMessage(),
//                                                        Toast.LENGTH_SHORT).show();
//                                            } else {
//                                                mProgressDialog.dismiss();
//                                                Toast.makeText(getContext(), "Xóa thành công",
//                                                        Toast.LENGTH_SHORT).show();
//                                                MyService.setChangeContent("justDelete");
//                                                getActivity().finish();
//                                            }
//                                        }
//                                    });
////                                    dbRef.child(
////                                            getString(R.string.userpost_CODE)
////                                            + post.getUserId() + "/" + postID).removeValue()
////                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                @Override
////                                                public void onComplete(@NonNull Task<Void> task) {
////                                                    if (!task.isComplete()) {
////                                                        Toast.makeText(getContext(),
////                                                                task.getException().getMessage(),
////                                                                Toast.LENGTH_SHORT).show();
////                                                    } else {
////
////                                                    }
////                                                }
////                                            });
//                                }
//                            }
//                        });
//            }
//        });
    }
}
