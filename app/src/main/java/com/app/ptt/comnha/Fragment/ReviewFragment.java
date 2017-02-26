package com.app.ptt.comnha.Fragment;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.app.ptt.comnha.Activity.Adapter2Activity;
import com.app.ptt.comnha.Adapters.Reviewlist_rcyler_adapter;
import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Classes.RecyclerItemClickListener1;
import com.app.ptt.comnha.FireBase.Image;
import com.app.ptt.comnha.FireBase.Post;
import com.app.ptt.comnha.Modules.Sort;
import com.app.ptt.comnha.Modules.Storage;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.app.ptt.comnha.SingletonClasses.EditPost;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import android.os.Handler;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends Fragment implements View.OnClickListener {
    private static int STATUS_START = 0;
    ArrayList<Bitmap> listBitmap;
    public ReviewFragment() {
        // Required empty public constructor
    }

    private static final String LOG=ReviewFragment.class.getSimpleName();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    RecyclerView.Adapter mAdapter;
    DatabaseReference dbRef;
    ArrayList<Post> postlist;
    ArrayList<Image> postImgList;
    boolean isSaved=false;
    ChildEventListener lastnewsChildEventListener;
    int sortType;
    Context context;
    Button btn_refresh,btn_reset;
    StorageReference storeRef;
    String tinh, huyen;
    ProgressDialog mProgressDialog;
    Boolean first,second;
    boolean isConnected=false;
    IntentFilter mIntentFilter;
    ImageButton btn_search;
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
                if (intent.getBooleanExtra("finish", false)) {
                    Log.i(LOG+".SENDDDDDDDDDD","OK");
                    closeDialog();
                }
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();

        Log.i(LOG,"onStart");
        isConnected= MyService.returnIsConnected();
        if(!isConnected){
            Toast.makeText(getActivity().getApplicationContext(),"Offline mode",Toast.LENGTH_SHORT).show();
        }
        Log.i(LOG, "onStart= "+isConnected);
        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
        getContext().registerReceiver(broadcastReceiver,mIntentFilter);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG,"onResume");
       // sortType = MyService.getPosOfReviewType();
//        if(MyService.getChangeContent()!="") {
//            sortType = MyService.getPosOfReviewType();
//            MyService.setChangeContent("");
//            if (MyService.saveToListSaved("postlist_" + sortType + "_" + tinh + "_" + huyen) == 1)
//                getDataOffline();
//            else
//                getData();
//        }
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.i(LOG,"onStop");
       // MyService.setPosOfReviewType(sortType);
        ChoosePost.getInstance().setPostID(null);
        ChoosePost.getInstance().setTinh(null);
        ChoosePost.getInstance().setHuyen(null);
        getContext().unregisterReceiver(broadcastReceiver);
    }
    public void setContext(Context mContext){
        this.context=mContext;
        Log.i(LOG+".setContext","OK");
    }

    public void setTinh(String tinh) {
        this.tinh = tinh;
    }

    public void setHuyen(String huyen) {
        this.huyen = huyen;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
        Log.i(LOG + ".setSortType", "sortType="+sortType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        isConnected= MyService.returnIsConnected();
        storeRef = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebaseStorage_path));
        Log.i(LOG + ".onCreateView", "OK");
        //mProgressDialog=ProgressDialog.show(getActivity(),"Loading","Please wait",true,false);
        // Inflate the layout for this fragment
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        anhxa(view);
        if(!isConnected){
            getDataOffline();
        }else {
            
//            if (MyService.getChangeContent() != "") {
//                sortType = MyService.getPosOfReviewType();
//                MyService.setChangeContent("");
//            }
//            if (MyService.saveToListSaved("postlist_" + sortType + "_" + tinh + "_" + huyen) == 1) {
//                getDataOffline();
//            } else
          
                getData();
        }

        return view;
    }

    private class runOnThread extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            postlist = new ArrayList<>();
            Log.i(LOG + ".getDataOffline", "OK");
            if (Storage.readFile(context, "postlist_" + sortType + "_" + tinh + "_" + huyen) != null) {
                String a = Storage.readFile(context, "postlist_" + sortType + "_" + tinh + "_" + huyen);
                //Log.i(LOG + ".onCreateView - " + "postlist_"+sortType+"_"+tinh+"_"+huyen, ""+a.toString());
                ArrayList<Post> posts = null;
                if (a != null) {
                    try {
                        posts = new ArrayList<>();
                        posts = Storage.readJSONPost1(a);
                    } catch (Exception e) {
                        if (isConnected)
                            getData();
                        e.printStackTrace();
                    }

                    if (posts.size() > 0) {
                        for (Post post : posts) {
                            postlist.add(post);
                        }
                    }
                } else {
                    if (isConnected)
                        getData();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    private class runOnThread1 extends AsyncTask<ArrayList<Post>,Void,ArrayList<Post>>{
        int count =0;

        @Override
        protected ArrayList<Post> doInBackground(final ArrayList<Post>... params) {

            lastnewsChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setPostID(dataSnapshot.getKey());
                    if(post.getVisible()) {
                        postlist.add(post);
                    }
                    Log.i(LOG + ".userName ",post.getUserId()+"");
                    isSaved=false;
//                    try {
//                        Storage.parsePostToJson(postlist);
//                        if (Storage.parsePostToJson(postlist).toString() != null) {
//                            Storage.writeFile(context, Storage.parsePostToJson(postlist).toString(), "postlist_" + sortType + "_" + tinh + "_" + huyen);
//                            MyService.saveToListSaved("postlist_" + sortType + "_" + tinh + "_" + huyen);
//                        }
//                        Log.i(LOG + ".getData ", "size = " + postlist.size());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    if (STATUS_START > 0) {
                        btn_refresh.setVisibility(View.VISIBLE);
                        AnimationUtils.animatbtnRefreshIfChange(btn_refresh);
                    }
                    STATUS_START = 1;

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setPostID(dataSnapshot.getKey());
                    for(Post a:postlist){
                        if(a.getPostID().equals(post.getPostID())){
                            postlist.set(postlist.indexOf(a),post);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setPostID(dataSnapshot.getKey());
                    for(Post a:postlist){
                        if(a.getPostID().equals(post.getPostID())){
                            postlist.remove(a);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            if(tinh!=null&&huyen!=null){
                dbRef.child("Posts")
                        .orderByChild("index").equalTo(tinh+"_"+huyen)
                        .limitToLast(100)
                        .addChildEventListener(lastnewsChildEventListener);
            }else
            {
                dbRef.child("Posts")
                        .limitToLast(100)
                        .addChildEventListener(lastnewsChildEventListener);

            }
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(postlist.size()>0){
                        Log.i(LOG + ".POS=",sortType+"");


                        Log.i(LOG + ".POS=",postlist.size()+"");

                        if(sortType==2) {
                            Sort sort = new Sort();
                            postlist = sort.sort(postlist);
                            Log.i(LOG + ".getDataSorted", "OK");
                            for(Post post:postlist){
                                Log.i(LOG + ".POS="+postlist.indexOf(post), "DAO VI TRI-"+post.getCommentCount());
                            }
                            ArrayList<Post> list=new ArrayList<Post>();
                            for (int i=postlist.size()-1;i>=0;i--){
                                list.add(postlist.get(i));
                                Log.i(LOG + ".POS="+i, "VI TRI"+postlist.get(i).getCommentCount());
                            }
                            postlist=list;
                        }else
                        {
                            ArrayList<Post> list=new ArrayList<Post>();
                            for (int i=postlist.size()-1;i>=0;i--){
                                list.add(postlist.get(i));
                                Log.i(LOG + ".POS="+i, "VI TRI"+postlist.get(i).getDate());
                            }
                            postlist=list;
                            for(Post post:postlist){
                                Log.i(LOG + ".POS="+postlist.indexOf(post), "DAO VI TRI-"+post.getDate());
                            }
                            Log.i(LOG + ".getDataSorted", "NO");
                        }
                        mAdapter = new Reviewlist_rcyler_adapter(postlist, getActivity(), 1);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(getContext(), "Không có review mới", Toast.LENGTH_SHORT).show();
                    }

                   closeDialog();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return postlist;
        }

        @Override
        protected void onPostExecute(ArrayList<Post> posts) {
            super.onPostExecute(posts);
        }
    }

    public void getDataOffline(){
        new runOnThread().execute();
    }
    public void getData(){
        showProgressDialog();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isShowProgress()){
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    Toast.makeText(context, "Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }
        }, 15000);
        new runOnThread1().execute();
        //Toast.makeText(getContext(), "CC", Toast.LENGTH_SHORT).show();

    }
    protected void closeDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    protected void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(getContext(), null, "Loading...");
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }
    }

    protected boolean isShowProgress() {
        return mProgressDialog.isShowing();
    }
    private void anhxa(View view) {
        postlist = new ArrayList<>();
        Log.i(LOG+".anhxa","OK");
        btn_refresh = (Button) view.findViewById(R.id.frg_review_btn_refresh);
        postImgList=new ArrayList<>();



        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_review);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerViewLayoutManager = linearLayoutManager;
        mRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener1(getActivity(), new RecyclerItemClickListener1.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(isConnected) {
                    Intent intent = new Intent(getActivity(), Adapter2Activity.class);
                    intent.putExtra(getResources().getString(R.string.fragment_CODE),
                            getResources().getString(R.string.frg_viewpost_CODE));
                    EditPost.getInstance().setPost(postlist.get(position));
                    ChoosePost.getInstance().setPostID(postlist.get(position).getPostID());
                    ChoosePost.getInstance().setTinh(tinh);
                    ChoosePost.getInstance().setHuyen(huyen);
                    ChoosePost.getInstance().setType(postlist.get(position).getType());
                    ChoosePost.getInstance().setUserID(postlist.get(position).getUserId());
                    if(postlist.get(position).getType()==1)
                        ChoosePost.getInstance().setFoodID(postlist.get(position).getFood().getMonID());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else
                Toast.makeText(context,"You are offline",Toast.LENGTH_LONG).show();

            }
        }));
        //btn_refresh.setVisibility(View.GONE);
        btn_refresh.setOnClickListener(this);
        mAdapter = new Reviewlist_rcyler_adapter(postlist, getActivity(), 1);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_review_btn_refresh:
                postlist=new ArrayList<>();
                getData();
                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                //AnimationUtils.animatbtnRefreshIfClick(btn_refresh);
                //btn_refresh.setVisibility(View.GONE);
                break;
        }
    }



}

