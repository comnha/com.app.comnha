package com.app.ptt.comnha.Activity;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Photo_recycler_adapter;
import com.app.ptt.comnha.Adapters.SingleImageImportRvAdapter;
import com.app.ptt.comnha.Classes.SelectedImage;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.Image;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChoosePhotoList;
import com.app.ptt.comnha.SingletonClasses.CoreManager;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.AppUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltipUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfiledetailActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar mToolbar;
    Dialog dialog;
    CircularImageView imgv_profile;
    TextView txtv_email, txtv_un, txtv_expandpro, txtv_expandimg, txtv_openacti,
            txtv_name, txtv_birth, txtv_address,
            txtv_work, txtv_phonenumb, txtv_sex, txtv_seemoreimg,txtv_password;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseAuth mAuth;
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
    SingleImageImportRvAdapter singleImageImportRvAdapter;
    RecyclerView imagesrv;
    GridLayoutManager imageslm;
    SelectedImage selectedImage;
    BottomSheetDialog imgsDialog;
    String avatarimg = "";
    Button btn_localimgdialog_close, btn_localimgdialog_select;
    UploadTask uploadTask;
    ProgressDialog uploadImgDialog, mProgressDialog,plzw8Dialog;
    FirebaseUser firebaseUser;
    public ProfiledetailActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiledetail);
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Const.DATABASE_PATH);
        stRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Const.STORAGE_PATH);
        mAuth = FirebaseAuth.getInstance();
        if (LoginSession.getInstance().getUser() != null) {
            user = LoginSession.getInstance().getUser();
            userID = LoginSession.getInstance().getUser().getuID();
            ref();
            setUserInfo();
        } else {
            onBackPressed();
        }
        dialog=new Dialog(this);

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
        getSupportActionBar().setTitle(getString(R.string.nav_profile));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setShowHideAnimationEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //collapsLayout = (CollapsingToolbarLayout) findViewById(R.id.cl_prodetail);
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
        txtv_work = (TextView) include_view.findViewById(R.id.txtv_work_prodetail);
        txtv_phonenumb = (TextView) include_view.findViewById(R.id.txtv_phonenumb_prodetail);
        txtv_sex = (TextView) include_view.findViewById(R.id.txtv_sex_prodetail);
        txtv_password= (TextView) include_view.findViewById(R.id.txtv_password);
        rv_imgs = (RecyclerView) include_view.findViewById(R.id.rv_imgs_prodetail);
        imgsLm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_imgs.setLayoutManager(imgsLm);
        images = new ArrayList<>();
        imgAdapter = new Photo_recycler_adapter(images, stRef, this);
        rv_imgs.setAdapter(imgAdapter);
        if(CoreManager.getInstance().isCheckProfileFirstTime()){
            new SimpleTooltip.Builder(this)
                    .anchorView(txtv_un)
                    .text("Click vào các danh mục để sửa thông tin")
                    .gravity(Gravity.BOTTOM)
                    .animated(true)
                    .transparentOverlay(false)
                    .build()
                    .show();
            CoreManager.getInstance().setCheckProfileFirstTime(this,false);
        }
        txtv_un.setOnClickListener(this);
        txtv_name.setOnClickListener(this);
        txtv_address.setOnClickListener(this);
        txtv_birth.setOnClickListener(this);
        txtv_password.setOnClickListener(this);
        txtv_phonenumb.setOnClickListener(this);
        txtv_email.setOnClickListener(this);
        txtv_sex.setOnClickListener(this);
        txtv_work.setOnClickListener(this);
        txtv_email.setOnClickListener(this);
        imgAdapter.setOnItemClickLiestner(new Photo_recycler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(Image image, Activity activity, View itemView) {
                Intent intent_openViewPhoto = new Intent(activity, ViewPhotosActivity.class);
                intent_openViewPhoto.putExtra("imgPosition", images.indexOf(image));
                startActivity(intent_openViewPhoto);
            }
        });
        card_profile = (CardView) include_view.findViewById(R.id.cardv_pro_prodetail);
        ln_img = (LinearLayout) include_view.findViewById(R.id.ln_img_prodetail);
        txtv_seemoreimg = (TextView) include_view.findViewById(R.id.txtv_openimggala_prodetail);
        txtv_seemoreimg.setOnClickListener(this);
        imgsDialog = new BottomSheetDialog(this);
        imgsDialog.setCanceledOnTouchOutside(false);
        imgsDialog.setCancelable(false);
        imgsDialog.setContentView(R.layout.layout_writepost_imgimporting);
        imgsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (selectedImage != null) {
                    avatarimg = selectedImage.getUri().getLastPathSegment();
//                    imgv_avatar.setImageURI(selectedImage.getUri());
                    uploadImgDialog.show();
                    final StorageReference avaRef = stRef.child(avatarimg);
                    final boolean[] isUploadImgSuccess = {false};
                    Image image = new Image(avatarimg,
                            userID, 2, "", "", "");
                    Map<String, Object> imgValue = image.toMap();
                    String imgKey = dbRef.child(getString(R.string.images_CODE))
                            .push().getKey();
                    User user = LoginSession.getInstance().getUser();
                    user.setAvatar(avatarimg);
                    Map<String, Object> userValue = user.toMap();
                    final Map<String, Object> childUpdate = new HashMap<>();
                    childUpdate.put(getString(R.string.images_CODE)
                            + imgKey, imgValue);
                    childUpdate.put(getString(R.string.users_CODE)
                            + userID, userValue);

                    uploadTask = avaRef.putFile(
                            Uri.fromFile(new File(selectedImage.getUri().toString())));

                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            int progress = (int) (taskSnapshot.getBytesTransferred() * 100
                                    / taskSnapshot.getTotalByteCount());
//                    Log.d("getBytesTransferred", progress + "");
                            uploadImgDialog.setProgress(progress);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(taskSnapshot.getDownloadUrl())
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                isUploadImgSuccess[0] = true;
                                                uploadImgDialog.dismiss();
//                                                Log.d(TAG, "User profile updated.");
                                            }
                                        }
                                    });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            uploadImgDialog.dismiss();
                            Toast.makeText(ProfiledetailActivity.this
                                    , getString(R.string.txt_failedUploadImg),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                    uploadImgDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (isUploadImgSuccess[0]) {
                                mProgressDialog.show();
                                dbRef.updateChildren(childUpdate)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mProgressDialog.dismiss();
                                                Toast.makeText(ProfiledetailActivity.this,
                                                        getString(R.string.text_updateavatar_succ)
                                                        , Toast.LENGTH_LONG).show();

                                                avaRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Picasso.with(ProfiledetailActivity.this)
                                                                .load(uri)
                                                                .placeholder(R.drawable.ic_account_circle_white_48dp)
                                                                .into(imgv_profile);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });
                                                selectedImage = null;
                                                isUploadImgSuccess[0] = false;
                                                avatarimg = "";
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(ProfiledetailActivity.this,
                                                e.getMessage()
                                                , Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else {

                            }
                        }
                    });
                }
            }
        });
        btn_localimgdialog_select = (Button) imgsDialog.findViewById(R.id.btn_select_imgimporting);
        btn_localimgdialog_close = (Button) imgsDialog.findViewById(R.id.btn_close_imgimporting);
        btn_localimgdialog_select.setOnClickListener(this);
        btn_localimgdialog_close.setOnClickListener(this);
        imagesrv = (RecyclerView) imgsDialog.findViewById(R.id.rv_images_imgimporting);
        imageslm = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        imagesrv.setLayoutManager(imageslm);
        singleImageImportRvAdapter = new SingleImageImportRvAdapter(this,
                this.getContentResolver());
        imagesrv.setAdapter(singleImageImportRvAdapter);
        singleImageImportRvAdapter.setOnSingleClickListener(new SingleImageImportRvAdapter.OnSingleClickListener() {
            @Override
            public void onClick(boolean isDismiss) {
//                if (isDismiss) {
//                    imgsDialog.dismiss();
//                }
            }
        });
        uploadImgDialog = AppUtils.setupProgressDialog(this,
                getString(R.string.txt_updloadimg), null, true, false,
                ProgressDialog.STYLE_HORIZONTAL, 100);
        mProgressDialog = AppUtils.setupProgressDialog(this,
                getString(R.string.txt_updateavatar), null, true, false,
                ProgressDialog.STYLE_SPINNER, 0);
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
                ChoosePhotoList.getInstance().setImage(images);
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
            imgv_profile.setImageResource(R.drawable.ic_account_circle_white_48dp);
        } else {
            StorageReference avaRef = stRef.child(user.getAvatar());
            avaRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(ProfiledetailActivity.this)
                            .load(uri)
                            .placeholder(R.drawable.ic_account_circle_white_48dp)
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
        if(TextUtils.isEmpty(user.getHo())&&TextUtils.isEmpty(user.getTenlot())){
            txtv_name.setText(user.getTen()
                    + "\n" + getString(R.string.text_hoten));
        }else {
            txtv_name.setText(user.getHo() + " " + user.getTenlot() + " " + user.getTen()
                    + "\n" + getString(R.string.text_hoten));
        }
        txtv_birth.setText(user.getBirth() + "\n" + getString(R.string.text_birth));
        if(!TextUtils.isEmpty(user.getAddress())){
            txtv_address.setText(user.getAddress() +
                    "\n" + getString(R.string.text_address));
        } else {
            txtv_address.setText("\n" + getString(R.string.text_address));
        }
        if (!user.getWork().equals("")) {
            txtv_work.setText( user.getWork()+"\n"+getString(R.string.txt_yourwork));
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
        txtv_password.setText("**********"
                +"\n"+"Mật khẩu");
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
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {

                                        // Should we show an explanation?
                                        if (shouldShowRequestPermissionRationale(
                                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                            // Explain to the user why we need to read the contacts
                                        }

                                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                                1);
                                        return false;
                                    }
                                }
                                singleImageImportRvAdapter.readthentranstoarray();
                                imagesrv.scrollToPosition(0);
                                imgsDialog.show();
                                break;
                            case R.id.action_chosefromUploaded:
                                break;
                            case R.id.action_viewprofilephoto:
//                                final ArrayList<Image> imgs = new ArrayList<>();
//                                plzw8Dialog = AppUtils.setupProgressDialog(ProfiledetailActivity.this,
//                                        getString(R.string.txt_plzwait), null, true, false,
//                                        ProgressDialog.STYLE_SPINNER, 0);
//                                dbRef.child(getString(R.string.images_CODE))
//                                        .orderByChild("type_uID")
//                                        .equalTo(2 + "_" + userID)
//                                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                Image image = dataSnapshot.getValue(Image.class);
//                                                image.setImageID(dataSnapshot.getKey());
//                                                imgs.add(image);
//                                                ChoosePhotoList.getInstance().setImage(imgs);
//                                                Intent intent_openViewPhoto = new Intent(ProfiledetailActivity.this,
//                                                        ViewPhotosActivity.class);
//                                                intent_openViewPhoto.putExtra("imgPosition", 0);
//                                                startActivity(intent_openViewPhoto);
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//                                        });

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
            case R.id.btn_close_imgimporting:
                imgsDialog.dismiss();
                break;
            case R.id.btn_select_imgimporting:
                selectedImage = singleImageImportRvAdapter.getSelectedImage();
                imgsDialog.dismiss();
                break;
            case R.id.txtv_email_prodetail:
                showTooltip(7,v);
                break;
            case R.id.txtv_birth_prodetail:
                showTooltip(2,v);
                break;
            case R.id.txtv_name_prodetail:
                showTooltip(1,v);
                break;
            case R.id.txtv_address_prodetail:
                showTooltip(3,v);
                break;
            case R.id.txtv_work_prodetail:
                showTooltip(4,v);
                break;
            case R.id.txtv_phonenumb_prodetail:
                showTooltip(5,v);
                break;
            case R.id.txtv_sex_prodetail:
                showTooltip(6,v);
                break;
            case R.id.txtv_password:
                showTooltip(8,v);
                break;
            case R.id.txtv_un_prodetail:
                showTooltip(9,v);
                break;

        }
    }
    public void showTooltip(final int type, View v){
        final SimpleTooltip tooltip;
        tooltip = new SimpleTooltip.Builder(getApplicationContext())
                .anchorView(v)
                .gravity(Gravity.END)
                .dismissOnOutsideTouch(false)
                .dismissOnInsideTouch(false)
                .showArrow(false)
                .modal(true)
                .animated(true)
                .contentView(R.layout.tooltip_edit)
                .focusable(true)
                .build();
        if(type==7 ||type==9) {
            ImageView imageView= (ImageView)tooltip.findViewById(R.id.btn_edit);
            imageView.setImageResource(R.drawable.bg_edit_email);
        }
        tooltip.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tooltip.isShowing())
                    tooltip.dismiss();
                switch (type){
                    case 1:
                        onCustomClick(1,user.getHo()+" "+user.getTenlot()+" "+user.getTen());
                        break;
                    case 2:
                        onCustomClick(2,user.getBirth());
                        break;
                    case 3:
                        onCustomClick(3,user.getAddress());
                        break;
                    case 4:
                        onCustomClick(4,user.getWork());
                        break;
                    case 5:
                        onCustomClick(5,user.getPhonenumb());
                        break;
                    case 6:
                        if(!user.isSexual()){
                            onCustomClick(6,"Nữ");
                        }else{
                            onCustomClick(6,"Nam");
                        }
                        break;
                    case 7:
                        onCustomClick(7,user.getEmail());
                        break;
                    case 8:
                        onCustomClick(8,user.getPass());
                        break;
                    case 9:
                        onCustomClick(9,user.getUn());
                        break;
                }

            }
        });
        tooltip.show();
    }

    public void onCustomClick(final int type, final String text){


        dialog.setContentView(R.layout.dialog_edit);
        dialog.setTitle("Thay đổi");
        final EditText edt= (EditText) dialog.findViewById(R.id.edt_edit);
        LinearLayout btn= (LinearLayout) dialog.findViewById(R.id.btn_edit);
        final EditText edtNewPass= (EditText) dialog.findViewById(R.id.edt_edit1);
        final EditText edtConfirmPass= (EditText) dialog.findViewById(R.id.edt_edit2);
        final LinearLayout llPass= (LinearLayout) dialog.findViewById(R.id.ll_password);
        TextView txt= (TextView) dialog.findViewById(R.id.txt_title);
        final RadioGroup rg= (RadioGroup) dialog.findViewById(R.id.rg_total);
        rg.setVisibility(View.GONE);
        LinearLayout llEdt= (LinearLayout) dialog.findViewById(R.id.ll_edt);
        final RadioButton rbNam= (RadioButton) dialog.findViewById(R.id.rb_nam),rbNu= (RadioButton) dialog.findViewById(R.id.rb_nu);
        String title = "";
        switch (type){
            case 1:
                title="Họ tên";
                edt.setHint(title);
                edt.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case 2:
                title="Ngày sinh";
                edt.setHint(title);
                edt.setInputType(InputType.TYPE_CLASS_DATETIME);
                break;
            case 3:
                title="Địa chỉ";
                edt.setHint(title);
                edt.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case 4:
                title="Nghề ngiệp";
                edt.setHint(title);
                edt.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case 5:
                title="Số điện thoại";
                edt.setHint(title);
                edt.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case 6:
                title="Giới tính";
                llEdt.setVisibility(View.GONE);
                rg.setVisibility(View.VISIBLE);

                if(text.equals("Nam")){
                    rbNam.setChecked(true);
                }else{
                    rbNu.setChecked(true);
                }
                break;
            case 7:
                title="Email";
                edt.setHint(title);
                edt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case 8:
                title="Mật khẩu";
                edt.setHint(title);
                edt.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                llPass.setVisibility(View.VISIBLE);
                break;
            case 9:
                title="Tên tài khoản";
                edt.setHint(title);
                edt.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }
        txt.setText(title+" mới");
        if(type!=8) {
            edt.setText(text);
        }
        edt.requestFocus();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((!TextUtils.isEmpty(edt.getText())&&type!=6)||type==6
                        ||(!TextUtils.isEmpty(edt.getText())&&!TextUtils.isEmpty(edtNewPass.getText())&&!TextUtils.isEmpty(edtConfirmPass.getText())&&type==8)){
                    switch (type){
                        case 1:
                            String a= edt.getText().toString();
                            String[] name=a.split(" ");
                            if(name.length==1){
                                user.setTen(name[0]);
                                user.setHo("");
                                user.setTenlot("");
                            }else {
                                String tenLot = "";
                                user.setHo(name[0]);
                                for (int i = 1; i < name.length - 1; i++) {
                                    if (i == name.length - 2) {
                                        tenLot += name[i];
                                    } else {
                                        tenLot += name[i] + " ";
                                    }

                                }
                                user.setTenlot(tenLot);
                                user.setTen(name[name.length - 1]);
                            }
                            break;
                        case 2:
                            user.setBirth(edt.getText().toString());
                            break;

                        case 3:
                            user.setAddress(edt.getText().toString());
                            break;
                        case 4:
                            user.setWork(edt.getText().toString());
                            break;
                        case 5:
                            user.setPhonenumb(edt.getText().toString());
                            break;
                        case 6:
                            if(rbNam.isChecked()){
                                user.setSexual(true);
                            }else{
                                user.setSexual(false);
                            }
                            break;
                        case 7:
                            updateEmail(edt.getText().toString());
                            break;
                        case 8:
                            if(edt.getText().toString().equals(user.getPass())){
                                if(edtNewPass.getText().toString().length()<8){
                                    Toast.makeText(ProfiledetailActivity.this,getString(R.string.txt_passtooshort), Toast.LENGTH_SHORT).show();
                                }else {

                                    if (!edtNewPass.getText().toString().equals(edtConfirmPass.getText().toString())) {
                                        Toast.makeText(ProfiledetailActivity.this, getString(R.string.txt_notmatchpassword), Toast.LENGTH_SHORT).show();
                                    } else {
                                        updatePass(edtNewPass.getText().toString());
                                    }
                                }
                            }else{
                                Toast.makeText(ProfiledetailActivity.this, "Mật khẩu cũ nhập chưa đúng", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 9:
                            user.setUn(edt.getText().toString());
                            break;

                    }
                    if(type!=8 &&type!=7){
                        Map<String, Object> userInfo = user.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(getResources().getString(R.string.users_CODE) + user.getuID(), userInfo);
                        dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfiledetailActivity.this, "Đã sửa", Toast.LENGTH_SHORT).show();
                                getUserInfo(user.getuID());
                                setUserInfo();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfiledetailActivity.this, "Chưa sửa được. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.dismiss();
                    }

                }


            }
        });
        dialog.show();
    }
    private void getUserInfo(final String uid) {
        ValueEventListener userValueListener;
        userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                String key = uid;
                user.setuID(key);
                setUserInfo();
                LoginSession.getInstance().setUser(user);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef.child(getString(R.string.users_CODE)
                + uid)
                .addValueEventListener(userValueListener);
    }
    private void getUser() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // User is signed in
                    LoginSession.getInstance().setFirebUser(firebaseUser);
                    getUserInfo(user.getuID());
                    Log.d("onAuthStateChanged", "onAuthStateChanged:signed_in:" + firebaseUser.getUid());
                } else {

                    // User is signed out
                }
                if (mAuthListener != null) {
                    mAuth.removeAuthStateListener(mAuthListener);
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void updatePass(final String pass){
        firebaseUser=mAuth.getCurrentUser();
        firebaseUser.updatePassword(pass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                user.setPass(pass);
                Map<String, Object> userInfo = user.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(getResources().getString(R.string.users_CODE) + user.getuID(), userInfo);
                dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ProfiledetailActivity.this, "Đã đổi mật khẩu", Toast.LENGTH_SHORT).show();
                        getUser();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfiledetailActivity.this, "Chưa đổi được. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProfiledetailActivity.this, "Lỗi. Vui lòng thử lại ", Toast.LENGTH_SHORT).show();
                                    }
                                }
        );
    }
    public void updateEmail(final String email){
        firebaseUser=mAuth.getCurrentUser();
        firebaseUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                user.setEmail(email);
                Map<String, Object> userInfo = user.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(getResources().getString(R.string.users_CODE) + user.getuID(), userInfo);
                dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ProfiledetailActivity.this, "Đã đổi mật khẩu", Toast.LENGTH_SHORT).show();
                        getUser();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfiledetailActivity.this, "Chưa đổi được. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProfiledetailActivity.this, "Lỗi. Vui lòng thử lại ", Toast.LENGTH_SHORT).show();
                                    }
                                }
        );
    }



}
