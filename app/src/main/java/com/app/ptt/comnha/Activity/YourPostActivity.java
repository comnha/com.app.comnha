package com.app.ptt.comnha.Activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.app.ptt.comnha.Adapters.Post_recycler_adapter;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class YourPostActivity extends AppCompatActivity {


    StorageReference stRef;
    DatabaseReference dbRef;
    RecyclerView rv_post;
    RecyclerView.LayoutManager postLm;
    ArrayList<Post> posts;
    Post_recycler_adapter postAdapter;
    ValueEventListener postEventListener;
    User user = LoginSession.getInstance().getUser();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yourpost);
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebase_path));
        stRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseStorage_path));
        Ref();
        getAllImg();
    }

    private void Ref() {
        rv_post = (RecyclerView) findViewById(R.id.rv_img_yourpost);
        postLm = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL,
                false);
        rv_post.setLayoutManager(postLm);
        posts = new ArrayList<>();
        postAdapter = new Post_recycler_adapter(posts, this, stRef);
        rv_post.setAdapter(postAdapter);
        postAdapter.setOnItemClickLiestner(new Post_recycler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(Post post, View itemView) {

            }
        });
    }

    private void getAllImg() {
        postEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Post post = item.getValue(Post.class);
                    post.setPostID(item.getKey());
                    posts.add(post);
                    postAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.posts_CODE))
                .orderByChild("isHidden_uID")
                .equalTo(false + "_" + user.getuID())
                .addListenerForSingleValueEvent(postEventListener);
    }
}
