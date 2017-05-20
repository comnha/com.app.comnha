package com.app.ptt.comnha.Activity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.app.ptt.comnha.Adapters.Photo_recycler_adapter;
import com.app.ptt.comnha.Models.FireBase.Image;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfiledetailActivity extends AppCompatActivity implements View.OnClickListener {
    private CollapsingToolbarLayout collapsLayout;
    Toolbar mToolbar;
    CircularImageView imgv_profile;
    TextView txtv_email, txtv_un, txtv_expandpro, txtv_expandimg, txtv_openacti,
            txtv_name, txtv_birth, txtv_address, txtv_distpro,
            txtv_work, txtv_phonenumb, txtv_sex, txtv_seemoreimg;
    User user = null;
    String userID;
    DatabaseReference dbRef;
    StorageReference stRef;
    RecyclerView rv_imgs;
    RecyclerView.LayoutManager imgsLm;
    ArrayList<Image> images;
    Photo_recycler_adapter imgAdapter;
    ValueEventListener imgEventListener;
    CardView card_profile;
    LinearLayout ln_img;

    public ProfiledetailActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiledetail);
        dbRef = FirebaseDatabase
                .getInstance()
                .getReferenceFromUrl(getString(R.string.firebase_path));
        stRef = FirebaseStorage
                .getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseStorage_path));
        if (LoginSession.getInstance().getUser() != null) {
            user = LoginSession.getInstance().getUser();
            userID = LoginSession.getInstance().getUser().getuID();
            ref();
            setUserInfo();
        } else {
            onBackPressed();
        }
    }

    void ref() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.color_notify_reportimg));
        }
        View include_view = findViewById(R.id.include_profiledetail_content);
        imgv_profile = (CircularImageView) findViewById(R.id.imgV_profile_prodetail);
        imgv_profile.setOnClickListener(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_prodetail);
        this.setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setShowHideAnimationEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        collapsLayout = (CollapsingToolbarLayout) findViewById(R.id.cl_prodetail);
        txtv_un = (TextView) findViewById(R.id.txtv_un_prodetail);
        txtv_email = (TextView) findViewById(R.id.txtv_email_prodetail);
        txtv_expandpro = (TextView) include_view.findViewById(R.id.txtv_expandprofile_prodetail);
        txtv_expandpro.setOnClickListener(this);
        txtv_expandimg = (TextView) include_view.findViewById(R.id.txtv_expandimg_prodetail);
        txtv_expandimg.setOnClickListener(this);
        txtv_openacti = (TextView) include_view.findViewById(R.id.txtv_openyouracti_prodetail);
        txtv_openacti.setOnClickListener(this);
        txtv_birth = (TextView) include_view.findViewById(R.id.txtv_birth_prodetail);
        txtv_name = (TextView) include_view.findViewById(R.id.txtv_name_prodetail);
        txtv_address = (TextView) include_view.findViewById(R.id.txtv_address_prodetail);
        txtv_distpro = (TextView) include_view.findViewById(R.id.txtv_distpro_prodetail);
        txtv_work = (TextView) include_view.findViewById(R.id.txtv_work_prodetail);
        txtv_phonenumb = (TextView) include_view.findViewById(R.id.txtv_phonenumb_prodetail);
        txtv_sex = (TextView) include_view.findViewById(R.id.txtv_sex_prodetail);
        rv_imgs = (RecyclerView) include_view.findViewById(R.id.rv_imgs_prodetail);
        imgsLm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_imgs.setLayoutManager(imgsLm);
        images = new ArrayList<>();
        imgAdapter = new Photo_recycler_adapter(images, stRef, this);
        rv_imgs.setAdapter(imgAdapter);
        imgAdapter.setOnItemClickLiestner(new Photo_recycler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(Image image, Activity activity, View itemView) {

            }
        });
        card_profile = (CardView) include_view.findViewById(R.id.cardv_pro_prodetail);
        ln_img = (LinearLayout) include_view.findViewById(R.id.ln_img_prodetail);
        txtv_seemoreimg = (TextView) include_view.findViewById(R.id.txtv_openimggala_prodetail);
        txtv_seemoreimg.setOnClickListener(this);
    }

    private void getImages() {
        imgEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Image image = item.getValue(Image.class);
                    String key = item.getKey();
                    image.setImageID(key);
                    images.add(image);
                }
                imgAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.images_CODE))
                .orderByChild("isHidden_uID")
                .equalTo(false + "_" + userID)
                .addListenerForSingleValueEvent(imgEventListener);
    }

    private void setUserInfo() {
        if (user.getAvatar().equals("")) {

        } else {
            StorageReference avaRef = stRef.child(user.getAvatar());
            avaRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(ProfiledetailActivity.this)
                            .load(uri)
                            .into(imgv_profile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
        txtv_un.setText(user.getUn());
        txtv_email.setText(user.getEmail());
        txtv_name.setText(user.getHo() + " " + user.getTenlot() + " " + user.getTen()
                + "\n" + getString(R.string.text_hoten));
        txtv_birth.setText(user.getBirth() + "\n" + getString(R.string.text_birth));
        if (!user.getWard().equals("") && !user.getStreet().equals("")) {
            txtv_address.setText(user.getStreet() + ", " + user.getWard() +
                    "\n" + getString(R.string.text_address));
        } else {
            txtv_address.setText("\n" + getString(R.string.text_address));
        }
        if (!user.getDist_prov().equals("")) {
            txtv_distpro.setText(getString(R.string.txt_dispro) + ": " + user.getDistrict() + ", " + user.getProvince());
        } else {
            txtv_distpro.setText("\n" + getString(R.string.txt_dispro));
        }
        if (!user.getWard().equals("")) {
            txtv_work.setText(getString(R.string.txt_yourwork) + ": " + user.getWork());
        } else {
            txtv_work.setText("\n" + getString(R.string.txt_yourwork));
        }
        if (user.isSexual()) {
            txtv_sex.setText(getString(R.string.txt_male)
                    + "\n" + getString(R.string.txt_sex));
        } else {
            txtv_sex.setText(getString(R.string.txt_female)
                    + "\n" + getString(R.string.txt_sex));
        }
        if (!user.getPhonenumb().equals("")) {
            txtv_phonenumb.setText(user.getPhonenumb()
                    + "\n" + getString(R.string.txt_yourphone));
        } else {
            txtv_phonenumb.setText("\n" + getString(R.string.txt_yourphone));
        }
        getImages();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgV_profile_prodetail:
                PopupMenu popupMenu = new PopupMenu(this,
                        imgv_profile, Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_upavatar, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_chosefromloca:
                                break;
                            case R.id.action_chosefromUploaded:
                                break;
                            case R.id.action_viewprofilephoto:
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                break;
            case R.id.txtv_expandprofile_prodetail:
                if (card_profile.getVisibility() == View.GONE) {
                    txtv_expandpro.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
                            getResources().getDrawable(R.drawable.ic_arrow_drop_up_50black_24dp));
                    card_profile.setVisibility(View.VISIBLE);
                } else {
                    txtv_expandpro.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
                            getResources().getDrawable(R.drawable.ic_arrow_drop_down_50black_24dp));
                    card_profile.setVisibility(View.GONE);
                }
                break;
            case R.id.txtv_expandimg_prodetail:

                if (ln_img.getVisibility() == View.GONE) {
                    ln_img.setVisibility(View.VISIBLE);
                    txtv_expandimg.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
                            getResources().getDrawable(R.drawable.ic_arrow_drop_up_50black_24dp));
                } else {
                    txtv_expandimg.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
                            getResources().getDrawable(R.drawable.ic_arrow_drop_down_50black_24dp));
                    ln_img.setVisibility(View.GONE);
                }
                break;
            case R.id.txtv_openyouracti_prodetail:
                Intent intent_openacti = new Intent(this, YourActiActivity.class);
                startActivity(intent_openacti);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.txtv_openimggala_prodetail:
                Intent intent_moreimg = new Intent(this, AlbumActivity.class);
                startActivity(intent_moreimg);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }
}
