package com.app.ptt.comnha.Activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.app.ptt.comnha.Adapters.PhotoAlbum_recycler_adapter;
import com.app.ptt.comnha.Models.FireBase.Image;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChoosePhotoList;
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
public class ProfilePhotoActivity extends AppCompatActivity {


    StorageReference stRef;
    DatabaseReference dbRef;
    RecyclerView rv_img;
    RecyclerView.LayoutManager imgLm;
    ArrayList<Image> images;
    PhotoAlbum_recycler_adapter imgAdapter;
    ValueEventListener imgEventListener;
    User user = LoginSession.getInstance().getUser();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilephoto);
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebase_path));
        stRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseStorage_path));
        Ref();
        getAllImg();
    }

    private void Ref() {
        rv_img = (RecyclerView) findViewById(R.id.rv_img_profilephoto);
        imgLm = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL,
                false);
        rv_img.setLayoutManager(imgLm);
        images = new ArrayList<>();
        imgAdapter = new PhotoAlbum_recycler_adapter(images, stRef, this);
        rv_img.setAdapter(imgAdapter);
        imgAdapter.setOnItemClickLiestner(new PhotoAlbum_recycler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick( Image image, Activity activity, View itemView) {
                Intent intent_openViewPhoto = new Intent(activity, ViewPhotosActivity.class);
                intent_openViewPhoto.putExtra("imgPosition", images.indexOf(image));
                startActivity(intent_openViewPhoto);
            }
        });
    }

    private void getAllImg() {
        imgEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Image image = item.getValue(Image.class);
                    image.setImageID(item.getKey());
                    images.add(image);
                }
                ChoosePhotoList.getInstance().setImage(images);
                imgAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.images_CODE))
                .orderByChild("isHidden_type_userID")
                .equalTo(false + "_" + 2 + "_" + user.getuID())
                .addListenerForSingleValueEvent(imgEventListener);
    }
}
