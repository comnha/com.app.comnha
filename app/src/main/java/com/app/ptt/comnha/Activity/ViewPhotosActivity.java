package com.app.ptt.comnha.Activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.ViewPhotoVPadapter;
import com.app.ptt.comnha.Classes.ZoomOutPageTransformer;
import com.app.ptt.comnha.Dialog.ReportDialog;
import com.app.ptt.comnha.Models.FireBase.Image;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChoosePhotoList;
import com.app.ptt.comnha.Utils.AppUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

import static com.app.ptt.comnha.Const.Const.REPORTS.REPORT_IMG;

/**
 * Created by PTT on 4/21/2017.
 */

public class ViewPhotosActivity extends AppCompatActivity {
    ViewPager vp_viewphoto;
    ArrayList<Image> images;
    ;
    StorageReference stRef;
    ViewPhotoVPadapter photoAdapter;
    int position = 0;
    Toolbar toolbar;
    ProgressDialog plzw8Dialog;
    DatabaseReference dbRef;
    Image image = null;
    AppBarLayout applayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_viewphoto);
        Intent intent = getIntent();
        position = intent.getIntExtra("imgPosition", 0);
        Log.i("ViewPhoto_pos", position + "");
        stRef = FirebaseStorage
                .getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseStorage_path));
        dbRef = FirebaseDatabase
                .getInstance()
                .getReferenceFromUrl(getString(R.string.firebase_path));
        init();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_viewphoto);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        applayout = (AppBarLayout) findViewById(R.id.applayout_viewphoto);
        applayout.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(android.R.color.transparent)));
        vp_viewphoto = (ViewPager) findViewById(R.id.viewpager_viewphoto);
        images = new ArrayList<>();
        images = ChoosePhotoList.getInstance().getImage();
        image = images.get(position);
        photoAdapter = new ViewPhotoVPadapter(images, this, stRef);
        vp_viewphoto.setAdapter(photoAdapter);
        vp_viewphoto.setPageTransformer(true, new ZoomOutPageTransformer());
        vp_viewphoto.setCurrentItem(position);
        photoAdapter.setOnImageClick(new ViewPhotoVPadapter.OnImageClick() {
            @Override
            public void onClick() {
                if (applayout.getVisibility() == View.INVISIBLE) {
                    applayout.setVisibility(View.VISIBLE);
                }
                if (applayout.getVisibility() == View.VISIBLE) {
                    applayout.setVisibility(View.INVISIBLE);
                }
                Log.d("viewphoto_clickimg", "true");
            }
        });
        plzw8Dialog = AppUtils.setupProgressDialog(this,
                getString(R.string.txt_plzwait), null, false, false,
                ProgressDialog.STYLE_SPINNER, -1);
        vp_viewphoto.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                image = images.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu = AppUtils.createMenu(menu, new String[]{
                getString(R.string.txt_report),
                getString(R.string.txt_imgdetail),
                getString(R.string.txt_hideimg)
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case 0:
                ReportDialog reportDialog = new ReportDialog();
                reportDialog.setReport(REPORT_IMG, image);
                reportDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AddfoodDialog);
                reportDialog.setOnPosNegListener(new ReportDialog.OnPosNegListener() {
                    @Override
                    public void onPositive(boolean isClicked, Map<String,
                            Object> childUpdate, final Dialog dialog) {
                        if (isClicked) {
                            dialog.dismiss();
                            plzw8Dialog.show();
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
                                    Toast.makeText(ViewPhotosActivity.this,
                                            e.getMessage(),
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
                reportDialog.show(getSupportFragmentManager(), "report_store");
                return true;
            case 1:
                return true;
            case 2:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
