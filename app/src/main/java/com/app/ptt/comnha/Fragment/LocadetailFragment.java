package com.app.ptt.comnha.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Activity.Adapter2Activity;
import com.app.ptt.comnha.Activity.AdapterActivity;
import com.app.ptt.comnha.Adapters.Photos_rcyler_adapter;
import com.app.ptt.comnha.Adapters.Reviewlist_rcyler_adapter;
import com.app.ptt.comnha.Adapters.Thucdon_rcyler_adapter;

import com.app.ptt.comnha.Classes.RecyclerItemClickListener1;
import com.app.ptt.comnha.Modules.Times;
import com.app.ptt.comnha.FireBase.Food;
import com.app.ptt.comnha.FireBase.Image;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.FireBase.Notification;
import com.app.ptt.comnha.FireBase.Post;
import com.app.ptt.comnha.Modules.ConnectionDetector;
import com.app.ptt.comnha.Modules.Storage;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChooseFood;
import com.app.ptt.comnha.SingletonClasses.ChooseLoca;
import com.app.ptt.comnha.SingletonClasses.ChooseNoti;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.app.ptt.comnha.SingletonClasses.DoPost;
import com.app.ptt.comnha.SingletonClasses.EditPost;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.SingletonClasses.OpenAlbum;
import com.app.ptt.comnha.SingletonClasses.EditLocal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
public class LocadetailFragment extends Fragment {
    private String locaID;
    private static final String LOG = LocadetailFragment.class.getSimpleName();
    DatabaseReference dbRef;
    Map<String, Object> childUpdates;
    ProgressDialog mProgressDialog;
    RecyclerView mRecyclerView, menuRecyclerView, albumRecyclerView;
    RecyclerView.Adapter mAdapter, menuAdapter, albumAdapter;
    RecyclerView.LayoutManager mlayoutManager, menulayoutManager, albumLayoutManager;
    TextView txt_name, txt_diachi, txt_gio, txt_gia, txt_vesinh, txt_phucvu, txt_sdt, txt_tien;
    LinearLayout btn_themanh, btn_dangreview,btn_dangmonan, btn_denmap;
    ArrayList<Post> postlist;
    ChildEventListener postChildEventListener, locaMenuChildEventListener, imageChildEventListener,locationValueEventListener;
    ActionBar actionBar;
    Toolbar toolbar;
    MyLocation location;
    StorageReference storeRef;
    ArrayList<Food> foodList;
    ArrayList<Notification> notifications;
    ArrayList<Image> files;
    String fileName;
    int stt=0;
    TextView txtalbum;
    boolean deletesuccess=true;
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    boolean isConnected = false;
    IntentFilter mIntentFilter;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastSendAddress)) {
                Log.i(LOG + ".onReceive form Service", "isConnected= " + intent.getBooleanExtra("isConnected", false));
                if (intent.getBooleanExtra("isConnected", false)) {
                    if (!isConnected)
                        getData();
                    isConnected = true;
                } else
                    isConnected = false;
            }
        }
    };

    public LocadetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        isConnected = MyService.returnIsConnected();
        if (!isConnected) {
            Toast.makeText(getContext(), "Offline mode", Toast.LENGTH_SHORT).show();
        }
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
        getContext().registerReceiver(broadcastReceiver, mIntentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locadetail, container, false);
        isConnected = MyService.returnIsConnected();
        locaID = ChooseLoca.getInstance().getLocation().getLocaID();
        fileName = ChooseLoca.getInstance().getInfo();
        Log.d(LOG + ".onCreateView", "fileName=" + fileName);
        storeRef = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebaseStorage_path));
        if (locaID != null) {
            location=ChooseLoca.getInstance().getLocation();

            andxa(view);

                if (!isConnected) {
                    Log.d(LOG + ".onCreateView", locaID);
                    //Toast.makeText(getContext(), "Offline mode", Toast.LENGTH_LONG).show();
                    if (Storage.readFile(getContext(), fileName) != null) {
                        ArrayList<MyLocation> locations;
                        String a = Storage.readFile(getContext(), fileName);
                        if (a != null) {
                            locations = Storage.readJSONMyLocation(a);
                            if (locations.size() > 0) {
                                for (MyLocation mLocation : locations) {
                                    if (mLocation.getLocaID().equals(locaID)) {
                                        location = mLocation;
                                        location.setQuanhuyen(LoginSession.getInstance().getHuyen());
                                        location.setTinhtp(LoginSession.getInstance().getTinh());
                                        String gio = location.getTimestart() + " - " + location.getTimeend();
                                        String tenquan = location.getName();
                                        String diachi = location.getDiachi();
                                        String sdt = location.getSdt();
                                        txt_sdt.setText(sdt);
                                        txt_diachi.setText(diachi);
                                        txt_tien.setText(String.valueOf(location.getGiamin()) +
                                                " - " + String.valueOf(location.getGiamax()));
                                        txt_gio.setText(gio);
                                        txt_name.setText(tenquan);
                                        try {
                                            txt_gia.setText(String.valueOf(location.getGiaAVG() + ""));
                                            txt_vesinh.setText(String.valueOf(location.getVsAVG() + ""));
                                            txt_phucvu.setText(String.valueOf(location.getPvAVG() + ""));
                                        } catch (ArithmeticException mess) {
                                            txt_gia.setText(String.valueOf(0));
                                            txt_vesinh.setText(String.valueOf(0));
                                            txt_phucvu.setText(String.valueOf(0));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (Storage.readFile(getContext(), "postlist_" + 1) != null) {
                        String a = Storage.readFile(getContext(), "postlist_" + 1);
                        ArrayList<Post> posts = null;
                        if (a != null) {
                            try {
                                // posts = Storage.readJSONPost(a);
                                posts = new ArrayList<>();
                                posts = Storage.readJSONPost1(a);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (posts.size() > 0) {
                                for (Post post : posts) {
                                    if (post.getLocaID().equals(locaID)) {
                                        postlist.add(post);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    }
                    return view;
                } else {
                    getData();
                    return view;
                }

        }


        return null;
    }

    public void getData() {
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
            locationValueEventListener=new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    location = dataSnapshot.getValue(MyLocation.class);
                    location.setLocaID(dataSnapshot.getKey());
                    if(location.getLocaID().equals(locaID)) {
                        try {
                            String gio = location.getTimestart() + " - " + location.getTimeend();
                            String tenquan = location.getName();
                            String diachi = location.getDiachi();
                            String sdt = location.getSdt();
                            long vs = location.getVsTong() / location.getSize();
                            long pv = location.getPvTong() / location.getSize();
                            long gia = location.getGiaTong() / location.getSize();
                            location.setVsAVG(vs);
                            location.setPvAVG(pv);
                            location.setGiaAVG(gia);
                            long tong = (vs + pv + gia) / 3;
                            location.setTongAVG(tong);
                            txt_sdt.setText(sdt);
                            txt_diachi.setText(diachi);
                            txt_tien.setText(String.valueOf(location.getGiamin()) +
                                    " - " + String.valueOf(location.getGiamax()));
                            txt_gio.setText(gio);
                            txt_name.setText(tenquan);
                            try {
                                txt_gia.setText(String.valueOf(location.getGiaAVG() + ""));
                                txt_vesinh.setText(String.valueOf(location.getVsAVG() + ""));
                                txt_phucvu.setText(String.valueOf(location.getPvAVG() + ""));
                            } catch (ArithmeticException mess) {
                                txt_gia.setText(String.valueOf(0));
                                txt_vesinh.setText(String.valueOf(0));
                                txt_phucvu.setText(String.valueOf(0));
                            }
                        } catch (Exception e) {
                                getActivity().finish();
                        }
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

        postChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                post.setPostID(dataSnapshot.getKey());
                if(post.getVisible()){
                    postlist.add(post);
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                post.setPostID(dataSnapshot.getKey());
                for (Post mpost : postlist)
                    if (mpost.getPostID().equals(dataSnapshot.getKey()))
                        mpost = post;
                mAdapter.notifyDataSetChanged();
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
        locaMenuChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Food food = dataSnapshot.getValue(Food.class);
                food.setMonID(dataSnapshot.getKey());
                if (food.getVisible()) {
                    foodList.add(food);
                    menuAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Food food = dataSnapshot.getValue(Food.class);
                food.setMonID(dataSnapshot.getKey());
                for (Food mfood: foodList)
                    if(mfood.getMonID().equals(dataSnapshot.getKey()))
                        mfood=food;
                menuAdapter.notifyDataSetChanged();
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
        imageChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
//                    Toast.makeText(getActivity(), dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("checkListenerFromImages", "have changed=> files size= " + files.size());
                    final Image image = dataSnapshot.getValue(Image.class);
                    image.setImageID(dataSnapshot.getKey());
                    storeRef.child(getResources().getString(R.string.images_CODE)
                            + image.getName())
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            image.setPath(uri);
                            files.add(image);
                            albumAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (NullPointerException | IllegalStateException mess) {

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try {
//                    Toast.makeText(getActivity(), dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("checkListenerFromImages", "have changed=> files size= " + files.size());
                    final Image image = dataSnapshot.getValue(Image.class);
                    image.setImageID(dataSnapshot.getKey());
                    storeRef.child(getResources().getString(R.string.images_CODE)
                            + image.getName())
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            image.setPath(uri);
                            files.add(image);
                            albumAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (NullPointerException | IllegalStateException mess) {

                }
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

        //if(!MyService.getDeleted()) {
        Log.d(getResources().getString(R.string.key_CODE), locaID);
        Log.d(getResources().getString(R.string.key_CODE), locaID);
            dbRef.child(getString(R.string.locations_CODE))
                .addChildEventListener(locationValueEventListener);
        dbRef.child(
                getResources().getString(R.string.posts_CODE)).orderByChild("locaID").equalTo(locaID)
                .addChildEventListener(postChildEventListener);
        dbRef.child(
                getResources().getString(R.string.thucdon_CODE)).orderByChild("locaID")
                .equalTo(locaID).addChildEventListener(locaMenuChildEventListener);
        dbRef.child(getResources().getString(R.string.images_CODE))
                .orderByChild("locaID").equalTo(locaID).limitToFirst(3)
                .addChildEventListener(imageChildEventListener);
        if(MyService.getUserAccount()!=null) {
            dbRef.child(getResources().getString(R.string.notification_CODE) + MyService.getUserAccount().getId()).
                    addChildEventListener(new ChildEventListener() {
                                              @Override
                                              public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                  try {
                                                      Notification notification = dataSnapshot.getValue(Notification.class);
                                                      notification.setNotiID(dataSnapshot.getKey());
                                                      if (notification.getLocation().getLocaID().equals(locaID)) {
                                                          notifications.add(notification);
                                                      }
                                                  } catch (Exception e) {

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
                                          }
                    );
//        }else{
//            try {
//                getActivity().finish();
//            }catch (Exception e){
//                return;
//            }
//        }
        }
    }

    private void andxa(View view) {
        txtalbum = (TextView) view.findViewById(R.id.frg_locadetial_txtAlbum);

        toolbar = (Toolbar) view.findViewById(R.id.frg_locadetial_toolbar);
        txt_tien = (TextView) view.findViewById(R.id.frg_lcdetail_txt_tien);
        txt_diachi = (TextView) view.findViewById(R.id.frg_lcdetail_txt_diachi);
        txt_name = (TextView) view.findViewById(R.id.frg_lcdetail_txt_tenquan);
        txt_gio = (TextView) view.findViewById(R.id.frg_lcdetail_txt_thoigian);
        txt_gia = (TextView) view.findViewById(R.id.frg_lcdetail_txt_gia);
        txt_vesinh = (TextView) view.findViewById(R.id.frg_lcdetail_txt_vesinh);
        txt_phucvu = (TextView) view.findViewById(R.id.frg_lcdetail_txt_phucvu);
        btn_themanh = (LinearLayout) view.findViewById(R.id.frg_lcdetail_btn_themanh);
        btn_denmap=(LinearLayout) view.findViewById(R.id.frg_lcdetail_denmap);
        btn_dangreview = (LinearLayout) view.findViewById(R.id.frg_lcdetail_dangreview);
        btn_dangmonan = (LinearLayout) view.findViewById(R.id.frg_lcdetail_dangmonan);
        txt_sdt = (TextView) view.findViewById(R.id.frg_lcdetail_txt_sdt);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Location Detail");
        setHasOptionsMenu(true);
        postlist = new ArrayList<Post>();
        files = new ArrayList<>();
        foodList = new ArrayList<>();
        notifications=new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_lcdetail_rcyler_review);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mlayoutManager = linearLayoutManager;
        mRecyclerView.setLayoutManager(mlayoutManager);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        mAdapter = new Reviewlist_rcyler_adapter(postlist, getActivity(),3);
        mRecyclerView.setAdapter(mAdapter);
        menuRecyclerView = (RecyclerView) view.findViewById(R.id.frg_lcdetail_rcyler_dsMon);
//        LinearLayoutManager linearLayoutManager1 =
//                new LinearLayoutManager(getActivity(),
//                        LinearLayoutManager.HORIZONTAL, false);
        menulayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        menuRecyclerView.setLayoutManager(menulayoutManager);

        menuAdapter = new Thucdon_rcyler_adapter(foodList, getActivity());
        menuRecyclerView.setAdapter(menuAdapter);
        albumRecyclerView = (RecyclerView) view.findViewById(R.id.frg_lcdetail_rcyler_album);
        albumLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        albumRecyclerView.setLayoutManager(albumLayoutManager);

        albumAdapter = new Photos_rcyler_adapter(files, getActivity());
        albumRecyclerView.setAdapter(albumAdapter);
        txtalbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (files.size() > 0) {
                    Intent intent = new Intent(getActivity(), Adapter2Activity.class);
                    intent.putExtra(getString(R.string.fragment_CODE),
                            getString(R.string.frg_viewalbum_CODE));
                    intent.putExtra(getString(R.string.fromFrag),
                            getString(R.string.frag_locadetail_CODE));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    OpenAlbum.getInstance().setPostID(locaID);
                    startActivity(intent);
                }
            }
        });
        btn_dangreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected) {
                    if (LoginSession.getInstance().getUserID() == null) {
                        Toast.makeText(getActivity(), getString(R.string.txt_needlogin),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        DoPost.getInstance().setMyLocation(location);
                        Intent intent = new Intent(getActivity(), Adapter2Activity.class);
                        intent.putExtra(getString(R.string.fragment_CODE), getString(R.string.frag_addpost_CODE));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
            }
        });
        btn_dangmonan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected) {
                    if (LoginSession.getInstance().getUserID() == null) {
                    } else {
                        Intent intent = new Intent(getActivity(), Adapter2Activity.class);
                        intent.putExtra(getResources().getString(R.string.fragment_CODE),
                                getResources().getString(R.string.frg_themmon_CODE));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (location != null) {
                            ChooseLoca.getInstance().setLocation(location);
                            startActivity(intent);
                        } else {
                            ConnectionDetector.showLoadingAlert(getContext());
                        }
                    }
                } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
            }
        });
        btn_denmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseLoca.getInstance().setLocation(location);
                Intent intent2 = new Intent(getActivity(), AdapterActivity.class);
                intent2.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frag_map_CODE));
                intent2.putExtra("type", 1);
                startActivity(intent2);
            }
        });
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener1(getActivity().getApplicationContext()
                , new RecyclerItemClickListener1.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isConnected) {
//                    if (LoginSession.getInstance().getUserID() == null) {
//                        Toast.makeText(getActivity(), getString(R.string.txt_needlogin),
//                                Toast.LENGTH_SHORT).show();
//                    } else {
                    Intent intent = new Intent(getActivity(), Adapter2Activity.class);
                    intent.putExtra(getString(R.string.fragment_CODE), getString(R.string.frg_viewpost_CODE));
                    EditPost.getInstance().setPost(postlist.get(position));
                    ChoosePost.getInstance().setPostID(postlist.get(position).getPostID());
                   // ChoosePost.getInstance().setTinh(tinh);
                   // ChoosePost.getInstance().setHuyen(huyen);
                    startActivity(intent);
//                    }
                } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
            }
        }));
        menuRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener1(getActivity().getApplicationContext()
                , new RecyclerItemClickListener1.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i("ZOOOOOOOOOOOO","VIEW FOOD CLICK");
                Intent intent=new Intent(getActivity(),Adapter2Activity.class);
                intent.putExtra(getString(R.string.fragment_CODE),getString(R.string.frg_viewfood_CODE));
                ChooseFood.getInstance().setFood(foodList.get(position));
                ChooseFood.getInstance().setLocation(location);
                startActivity(intent);
            }
        }));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.option_menu_locadetail, menu);
        MenuItem ycx = menu.findItem(R.id.menu_locadetail_ycxoa);
        MenuItem dy = menu.findItem(R.id.menu_locadetail_dongy);
        MenuItem pd = menu.findItem(R.id.menu_locadetail_phandoi);
        MenuItem xoa = menu.findItem(R.id.menu_locadetail_xoa);
        MenuItem sua = menu.findItem(R.id.menu_locadetail_sua);
        MenuItem report = menu.findItem(R.id.menu_locadetail_report);
        if(MyService.getUserAccount()!=null) {
            // show the button when some condition is true
            if (MyService.getUserAccount().getRole()) {
//                if (location.getVisible()) {
//                    pd.setVisible(true);
//
//
//                } else {
//                    dy.setVisible(true);
//                }
                xoa.setVisible(true);
                sua.setVisible(true);

            } else if (MyService.getUserAccount().getId().equals(location.getUserId())) {
                ycx.setVisible(true);
                sua.setVisible(true);
            } else {
                report.setVisible(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.menu_locadetail_ycxoa:
                if (isConnected) {
                    AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(getContext());
                    builder1.setTitle("Thông báo");
                    builder1.setMessage("Bạn có yêu cầu xóa bài đăng của bạn không?");
                    builder1.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            EditLocal.getInstance().setMyLocation(location);
                            ReasonStoreDialogFragment temp = new ReasonStoreDialogFragment();
                            temp.setType(3);
                            temp.show(getActivity().getSupportFragmentManager(), "fragment_reportStore");
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
                } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_locadetail_sua:
                if (isConnected) {
                    EditLocal.getInstance().setMyLocation(location);
                    EditStoreDialogFragment reportStoreDialog = new EditStoreDialogFragment();
                    reportStoreDialog.show(getActivity().getSupportFragmentManager(), "fragment_editStore");
                    return true;
                } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
//            case R.id.menu_locadetail_an:
//                if (isConnected) {
//                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
//                    builder.setTitle("Thông báo");
//                    builder.setMessage("Bạn có muốn ẩn bài đăng này không?");
//                    builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                            mProgressDialog = ProgressDialog.show(getActivity(),
//                                    getResources().getString(R.string.txt_plzwait),
//                                    "Đang ẩn",
//                                    true, false);
//                            Map<String, Object> updateLocal = location.toMap();
//                            childUpdates=new HashMap<>();
//                            location.setVisible(false);
//                            childUpdates.put(
//                                    getResources().getString(R.string.locations_CODE)
//                                            + location.getLocaID(), updateLocal);
//                            for(Food food: foodList){
//                                food.setVisible(false);
//                                Map<String, Object> updatefood = food.toMap();
//                                childUpdates.put(
//                                        getResources().getString(R.string.thucdon_CODE)
//                                                + food.getMonID(), updatefood);
//                            }
//                            for(Post post: postlist){
//                                post.setVisible(false);
//                                Map<String, Object> updatepost = post.toMap();
//                                childUpdates.put(
//                                        getResources().getString(R.string.posts_CODE)
//                                                + post.getPostID(), updatepost);
//                            }
//
//                            dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    Notification notification = new Notification();
//                                    String key1 = dbRef.child(getResources().getString(R.string.notification_CODE) + EditLocal.getInstance().getMyLocation().getUserId()).push().getKey();
//                                    notification.setAccount(MyService.getUserAccount());
//                                    notification.setDate(new Times().getDate());
//                                    notification.setTime(new Times().getTime());
//                                    notification.setLocation(EditLocal.getInstance().getMyLocation());
//                                    notification.setType(4);
//                                    notification.setReason("Quán ăn bạn đăng đã bị report. Hãy coi lại thông tin quán ăn và sửa cho đúng");
//                                    notification.setReaded(false);
//                                    childUpdates = new HashMap<String, Object>();
//                                    Map<String, Object> notificationValue = notification.toMap();
//                                    childUpdates.put(getResources().getString(R.string.notification_CODE) + EditLocal.getInstance().getMyLocation().getUserId() + "/" + key1, notificationValue);
//                                    dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isComplete()) {
//                                                Toast.makeText(getContext(), "Ẩn thành công. Đã gửi yêu cầu", Toast.LENGTH_SHORT).show();
//                                                mProgressDialog.dismiss();
//                                                getActivity().finish();
//                                            } else {
//                                                mProgressDialog.dismiss();
//                                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    });
//                                    getActivity().finish();
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(getContext(), "Lỗi khi ẩn", Toast.LENGTH_SHORT).show();
//                                    mProgressDialog.dismiss();
//                                    getActivity().finish();
//                                    return;
//
//                                }
//                            });
//
//                        }
//                    });
//                    builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
//                    builder.show();
//
//                } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
//                return true;
            case R.id.menu_locadetail_xoa:
                if (isConnected) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                    builder.setTitle("Thông báo");
                    builder.setMessage("Bạn có muốn xóa bài đăng này không?");
                    builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            mProgressDialog = ProgressDialog.show(getActivity(),
                                    getResources().getString(R.string.txt_plzwait),
                                    "Đang xóa",
                                    true, false);
                            childUpdates=new HashMap<>();
                            for(Food food: foodList){
                                if(deletesuccess) {
                                    dbRef.child(getResources().getString(R.string.thucdon_CODE)+ food.getMonID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            deletesuccess = true;
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            deletesuccess = false;
                                        }
                                    });
                                }else{
                                    Toast.makeText(getContext(), "Xóa món ăn không thành công", Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                    getActivity().finish();
                                    return;
                                }
                            }
                            for(Post post: postlist){
                                if(deletesuccess) {
                                    dbRef.child(getResources().getString(R.string.posts_CODE)+ post.getPostID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            deletesuccess = true;
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            deletesuccess = false;
                                        }
                                    });
                                }else{
                                    Toast.makeText(getContext(), "Xóa bài đăng không thành công", Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                    getActivity().finish();
                                    return;
                                }
                            }
                            for(Image img:files){
                                if(deletesuccess) {
                                    dbRef.child(getResources().getString(R.string.images_CODE)+ img.getImageID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            deletesuccess = true;
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            deletesuccess = false;
                                        }
                                    });
                                }else{
                                    Toast.makeText(getContext(), "Xóa hình không thành công", Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                    getActivity().finish();
                                    return;
                                }
                            }
                            for(Notification not: notifications)
                            if(deletesuccess) {
                                dbRef.child(getResources().getString(R.string.notification_CODE)+ location.getUserId()+not.getNotiID()).
                                        removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        deletesuccess = true;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        deletesuccess = false;
                                    }
                                });
                            }else{
                                Toast.makeText(getContext(), "Xóa thông báo không thành công", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                                getActivity().finish();
                                return;
                            }
                            for(Image img:files){
                                Log.i("OK", "img:"+img.getName());
                                if(deletesuccess) {
                                    StorageReference myChildRef = storeRef.child(
                                            getResources().getString(R.string.images_CODE)
                                                    + img.getName());
                                    Log.i("OK", "url:"+  getResources().getString(R.string.images_CODE)
                                            + img.getName());
                                    myChildRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.i("OK", "deletesuccess:"+deletesuccess);
                                            deletesuccess=true;
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i("FAIL", "deletesuccess:"+deletesuccess);
                                            deletesuccess=false;
                                        }
                                    });
                                }else{
                                    Toast.makeText(getContext(), "Xóa hình không thành công", Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                    getActivity().finish();

                                    return;
                                }

                            }
                            if(deletesuccess) {
                                dbRef.child(getResources().getString(R.string.locations_CODE)+ location.getLocaID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        deletesuccess = true;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        deletesuccess = false;
                                    }
                                });
                            }else{
                                Toast.makeText(getContext(), "Xóa quán không thành công", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                                getActivity().finish();
                            }
                            if(deletesuccess) {
                                if(location.getIsReported()){
                                    Notification notification = new Notification();
                                    String key1 = dbRef.child(getResources().getString(R.string.notification_CODE) + EditLocal.getInstance().getMyLocation().getUserId()).push().getKey();
                                    notification.setAccount(MyService.getUserAccount());
                                    notification.setDate(new Times().getDate());
                                    notification.setTime(new Times().getTime());
                                    notification.setLocation(EditLocal.getInstance().getMyLocation());
                                    notification.setType(5);
                                    notification.setReaded(false);
                                    childUpdates = new HashMap<String, Object>();
                                    Map<String, Object> notificationValue = notification.toMap();
                                    childUpdates.put(getResources().getString(R.string.notification_CODE) + EditLocal.getInstance().getMyLocation().getUserId() + "/" + key1, notificationValue);
                                    //childUpdates.put(getResources().getString(R.string.notification_CODE) + EditLocal.getInstance().getMyLocation().getNot().getAccount().getUsername() + "/" + key1, notificationValue);
                                    dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isComplete()) {
                                                Notification notification = new Notification();
                                                String key1 = dbRef.child(getResources().getString(R.string.notification_CODE) +  location.getReporter()).push().getKey();
                                                notification.setAccount(MyService.getUserAccount());
                                                notification.setDate(new Times().getDate());
                                                notification.setTime(new Times().getTime());
                                                notification.setLocation(EditLocal.getInstance().getMyLocation());
                                                notification.setType(6);
                                                notification.setReaded(false);
                                                childUpdates = new HashMap<String, Object>();
                                                Map<String, Object> notificationValue = notification.toMap();
                                                childUpdates.put(getResources().getString(R.string.notification_CODE) + location.getReporter() + "/" + key1, notificationValue);
                                                dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isComplete()) {
                                                            Toast.makeText(getContext(), "Xóa thành công. Đã thông báo tới người sử dụng", Toast.LENGTH_SHORT).show();
                                                            mProgressDialog.dismiss();
                                                            FragmentTransaction transaction;
                                                            StoreFragment storeFragment;
                                                            storeFragment = new StoreFragment();
                                                            storeFragment.setFilter(1);
                                                            storeFragment.setTinh(LoginSession.getInstance().getTinh());
                                                            storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                                            storeFragment.setContext(getContext());
                                                            transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                                            transaction.replace(R.id.frame, storeFragment);
                                                            transaction.commit();
                                                        } else {
                                                            mProgressDialog.dismiss();
                                                            getActivity().finish();
                                                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            } else {
                                                mProgressDialog.dismiss();
                                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                FragmentTransaction transaction;
                                                StoreFragment storeFragment;
                                                storeFragment = new StoreFragment();
                                                storeFragment.setFilter(1);
                                                storeFragment.setTinh(LoginSession.getInstance().getTinh());
                                                storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                                storeFragment.setContext(getContext());
                                                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                                transaction.replace(R.id.frame, storeFragment);
                                                transaction.commit();
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                    getActivity().finish();
                                }
                            }

//                            dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    Toast.makeText(getContext(), "Đã ẩn thành công", Toast.LENGTH_SHORT).show();
//                                    mProgressDialog.dismiss();
//                                    getActivity().finish();
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(getContext(), "Lỗi khi ẩn", Toast.LENGTH_SHORT).show();
//                                    mProgressDialog.dismiss();
//                                }
//                            });

                        }
                    });
                    builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();

                } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
                return true;
//            case R.id.menu_locadetail_dongy:
//                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
//                builder.setTitle("Thông báo");
//                builder.setMessage("Bạn đồng ý đăng hiện bài đăng này?");
//                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                       dialog.cancel();
//                        mProgressDialog = ProgressDialog.show(getActivity(),
//                                getResources().getString(R.string.txt_plzwait),
//                                "Đang xử lý",
//                                true, false);
//                        location.setVisible(true);
//                        Map<String, Object> updateLocal = location.toMap();
//                        childUpdates = new HashMap<>();
//                        childUpdates.put(
//                                getResources().getString(R.string.locations_CODE)
//                                        + location.getLocaID(), updateLocal);
//                        for (Food food : foodList) {
//                            food.setVisible(true);
//                            Map<String, Object> updatefood = food.toMap();
//                            childUpdates.put(
//                                    getResources().getString(R.string.thucdon_CODE)
//                                            + food.getMonID(), updatefood);
//                        }
//                        for (Post post : postlist) {
//                            post.setVisible(true);
//                            Map<String, Object> updatepost = post.toMap();
//                            childUpdates.put(
//                                    getResources().getString(R.string.posts_CODE)
//                                            + post.getPostID(), updatepost);
//                        }
//                        dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                Notification notification = new Notification();
//                                String key1 = dbRef.child(getResources().getString(R.string.notification_CODE) + location.getUserId()).push().getKey();
//                                notification.setAccount(MyService.getUserAccount());
//                                notification.setDate(new Times().getDate());
//                                notification.setTime(new Times().getTime());
//                                notification.setType(2);
//                                notification.setLocation(location);
//                                notification.setReaded(false);
//                                notification.setTo(location.getUserId());
//                                Map<String, Object> notificationValue = notification.toMap();
//                                childUpdates.put(getResources().getString(R.string.notification_CODE) + location.getUserId()+"/" + key1, notificationValue);
//                                dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isComplete()) {
//                                            Toast.makeText(getContext(), "Đã thông qua bài viết", Toast.LENGTH_SHORT).show();
//                                            mProgressDialog.dismiss();
//                                            getActivity().finish();
//                                        } else {
//                                            mProgressDialog.dismiss();
//                                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(getContext(), "Lỗi!!", Toast.LENGTH_SHORT).show();
//                                mProgressDialog.dismiss();
//                            }
//                        });
//                    }
//                });
//                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
//                return true;
//            case R.id.menu_locadetail_phandoi:
//                if (isConnected) {
//                    //
//                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
//                    builder1.setTitle("Thông báo");
//                    builder1.setMessage("Bạn muốn ẩn quán ăn này bài đăng này?");
//                    builder1.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                            mProgressDialog = ProgressDialog.show(getActivity(),
//                                    getResources().getString(R.string.txt_plzwait),
//                                    "Đang ẩn",
//                                    true, false);
//                            location.setVisible(false);
//                            Map<String, Object> updateLocal = location.toMap();
//                            childUpdates = new HashMap<>();
//                            childUpdates.put(
//                                    getResources().getString(R.string.locations_CODE)
//                                            + location.getLocaID(), updateLocal);
//                            for (Food food : foodList) {
//                                food.setVisible(false);
//                                Map<String, Object> updatefood = food.toMap();
//                                childUpdates.put(
//                                        getResources().getString(R.string.thucdon_CODE)
//                                                + food.getMonID(), updatefood);
//                            }
//                            for (Post post : postlist) {
//                                post.setVisible(false);
//                                Map<String, Object> updatepost = post.toMap();
//                                childUpdates.put(
//                                        getResources().getString(R.string.posts_CODE)
//                                                + post.getPostID(), updatepost);
//                            }
//
//                            dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    mProgressDialog.dismiss();
//                                    EditLocal.getInstance().setMyLocation(location);
//                                    ReasonStoreDialogFragment temp = new ReasonStoreDialogFragment();
//                                    temp.setType(1);
//                                    temp.show(getActivity().getSupportFragmentManager(), "fragment_reasonStore");
//                                    //getActivity().finish();
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(getContext(), "Lỗi khi ẩn", Toast.LENGTH_SHORT).show();
//                                    mProgressDialog.dismiss();
//                                    getActivity().finish();
//                                    return;
//
//                                }
//                            });
//                        }
//                    });
//                    builder1.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
//                    builder1.show();
//
//
//                    //
//
//                    return true;
//                } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
//                return true;
            case R.id.menu_locadetail_report:
                if (isConnected) {
                    EditLocal.getInstance().setMyLocation(location);
                    ReasonStoreDialogFragment temp = new ReasonStoreDialogFragment();
                    temp.setType(2);
                    temp.show(getActivity().getSupportFragmentManager(), "fragment_reportStore");
                    return true;
                } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
//        ChooseLoca.getInstance().setLocaID(null);
        ChooseLoca.getInstance().setLocation(null);
        ChoosePost.getInstance().setPostID(null);
        ChoosePost.getInstance().setTinh(null);
        ChoosePost.getInstance().setHuyen(null);
        ChooseNoti.getInstance().setNotification(null);
        location=null;
    }
}
