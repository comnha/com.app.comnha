package com.app.ptt.comnha.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.app.ptt.comnha.Adapters.ViewPhotoVPadapter;
import com.app.ptt.comnha.Classes.ZoomOutPageTransformer;
import com.app.ptt.comnha.Models.FireBase.Image;
import com.app.ptt.comnha.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by PTT on 4/21/2017.
 */

public class ViewPhotoActivity extends AppCompatActivity {
    ViewPager vp_viewphoto;
    ArrayList<Image> images;
    StorageReference stRef;
    ViewPhotoVPadapter photoAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_viewphoto);
        stRef = FirebaseStorage.getInstance().getReferenceFromUrl(getString(R.string.firebaseStorage_path));
        init();
    }

    private void init() {
        vp_viewphoto = (ViewPager) findViewById(R.id.viewpager_viewphoto);
        images = new ArrayList<>();
        photoAdapter = new ViewPhotoVPadapter(images, this, stRef);
        vp_viewphoto.setAdapter(photoAdapter);
        vp_viewphoto.setPageTransformer(true, new ZoomOutPageTransformer());
    }
}
