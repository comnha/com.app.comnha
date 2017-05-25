package com.app.ptt.comnha.Activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Comment_rcyler_adapter;
import com.app.ptt.comnha.Adapters.Photo_recycler_adapter;
import com.app.ptt.comnha.Models.FireBase.Comment;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Image;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostdetailActivity extends AppCompatActivity implements View.OnClickListener {
    private String postID;
    private static final String LOG = PostdetailActivity.class.getSimpleName();
    ImageView imgv_banner, imgv_sendcomment;
    TextView txtv_un, txtv_date, txtv_content, txtv_title, txt_likenumb,
            txtv_comtNumb, txtv_pricerate, txtv_healthrate,
            txtv_servicerate, txtv_foodname, txtv_foodprice,
            txtv_rateComment, txtv_writecomt, txtv_storename;
    CircularImageView imgv_avatar, imgv_food;
    RatingBar rb_foodrating;
    Toolbar toolbar;
    CollapsingToolbarLayout clayout;
    Food food;
    RecyclerView rv_imgs, rv_comments;
    RecyclerView.LayoutManager imgsLm, comtLm;
    ArrayList<Image> images;
    Photo_recycler_adapter imgsAdapter;
    ArrayList<Comment> comments;
    Comment_rcyler_adapter comtAdapter;
    DatabaseReference dbRef;
    StorageReference stRef;
    ValueEventListener commentEventListener, imagesEventListener, foodsEventListener,
            userEventListener;
    LinearLayout linear_rate, linear_food;
    ProgressDialog plzw8Dialog;
    Post post;
    BottomSheetDialog commentDialog;
    EditText edt_comment;
    String comtContent;
    Comment comment = null;
    User user = null;
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

    public PostdetailActivity() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_post_detail);
        isConnected = MyService.returnIsNetworkConnected();
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        stRef = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebaseStorage_path));
        if (ChoosePost.getInstance().getPost() != null) {
            post = ChoosePost.getInstance().getPost();
            postID = post.getPostID();
        } else {
            onBackPressed();
        }
        Ref();
        createPostView();
        getAllImgs();
        getAllComts();
    }

    @Override
    public void onStart() {
        isConnected = MyService.returnIsNetworkConnected();
        if (!isConnected) {
            Toast.makeText(this, "Offline mode", Toast.LENGTH_SHORT).show();
        }
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
        this.registerReceiver(broadcastReceiver, mIntentFilter);
        super.onStart();
        Log.i(PostdetailActivity.class.getSimpleName(), "OK");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void Ref() {

        View view_include = findViewById(R.id.include_postdetail_content);
        plzw8Dialog = AppUtils.setupProgressDialog(this,
                getString(R.string.txt_plzwait), null, false, false,
                ProgressDialog.STYLE_SPINNER, 0);
        imgv_banner = (ImageView) findViewById(R.id.imgv_banner_postdetail);
        toolbar = (Toolbar) findViewById(R.id.toolbar_postdetail);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_24dp);
        this.setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        txt_likenumb = (TextView) view_include.findViewById(R.id.txtv_likenumb_postdetail);
        txtv_comtNumb = (TextView) view_include.findViewById(R.id.txtv_comtnumb_postdetail);
        imgv_avatar = (CircularImageView) view_include.findViewById(R.id.imgv_user_postdetail);
        txtv_un = (TextView) view_include.findViewById(R.id.txtv_username_postdetail);
        txtv_date = (TextView) view_include.findViewById(R.id.txtv_postdate_postdetail);
        linear_rate = (LinearLayout) view_include.findViewById(R.id.linear_rate_postdetail);
        linear_rate.setVisibility(View.GONE);
        txtv_pricerate = (TextView) view_include.findViewById(R.id.txtv_price_postdetaill);
        txtv_healthrate = (TextView) view_include.findViewById(R.id.txtv_health_postdetail);
        txtv_servicerate = (TextView) view_include.findViewById(R.id.txtv_service_postdetail);
        linear_food = (LinearLayout) view_include.findViewById(R.id.linear_food_postdetail);
        linear_food.setVisibility(View.GONE);
        imgv_food = (CircularImageView) view_include.findViewById(R.id.imgv_foodimg_postdetail);
        txtv_foodname = (TextView) view_include.findViewById(R.id.txtv_foodname_postdetail);
        txtv_foodprice = (TextView) view_include.findViewById(R.id.txtv_foodprice_postdetail);
        txtv_foodprice = (TextView) view_include.findViewById(R.id.txtv_foodprice_postdetail);
        txtv_rateComment = (TextView) view_include.findViewById(R.id.txtv_rateComment_postdetail);
        rb_foodrating = (RatingBar) view_include.findViewById(R.id.rb_foodrating_postdetail);
        rb_foodrating.setIsIndicator(true);
        txtv_content = (TextView) view_include.findViewById(R.id.txtv_content_postdetail);
        txtv_title = (TextView) view_include.findViewById(R.id.txtv_title_postdetail);
        rv_imgs = (RecyclerView) view_include.findViewById(R.id.rv_album_postdetail);
        rv_imgs.setHasFixedSize(true);
        imgsLm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                false);
        rv_imgs.setLayoutManager(imgsLm);
        images = new ArrayList<>();
        imgsAdapter = new Photo_recycler_adapter(images, stRef, this);
        rv_imgs.setAdapter(imgsAdapter);
        imgsAdapter.setOnItemClickLiestner(new Photo_recycler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(Image image, Activity activity, View itemView) {
                Intent open_viewphoto = new Intent(activity, ViewPhotoActivity.class);
                ActivityOptionsCompat option_viewphoto =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                                itemView.findViewById(R.id.item_rv_imgv),
                                "postBanner");
                startActivity(open_viewphoto, option_viewphoto.toBundle());
            }
        });

        rv_comments = (RecyclerView) view_include.findViewById(R.id.rv_comments_postdetail);
        rv_comments.setHasFixedSize(true);
        comtLm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false);
        rv_comments.setLayoutManager(comtLm);
        comments = new ArrayList<>();
        comtAdapter = new Comment_rcyler_adapter(this, comments, stRef);
        rv_comments.setAdapter(comtAdapter);

        txtv_writecomt = (TextView) findViewById(R.id.txtv_writecomt_postdetail);
        txtv_writecomt.setOnClickListener(this);
        commentDialog = new BottomSheetDialog(this);
        commentDialog.setContentView(R.layout.layout_comment);
        imgv_sendcomment = (ImageView) commentDialog
                .findViewById(R.id.imgv_send_postdetail);
        imgv_sendcomment.setOnClickListener(this);
        edt_comment = (EditText) commentDialog.findViewById(R.id.edt_comment_postdetail);
        edt_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                comtContent = editable.toString();
            }
        });
        commentDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (comment != null) {

                } else {
                    edt_comment.setText("");
                    edt_comment.clearFocus();
                    comtContent = null;
                }
            }
        });
        txtv_storename = (TextView) view_include.findViewById(R.id.txtv_storename_postdetail);
        clayout = (CollapsingToolbarLayout) findViewById(R.id.clayout_postdetail);
        clayout.setTitleEnabled(true);
        clayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
        clayout.setExpandedTitleColor(getResources().getColor(android.R.color.white));
    }

    private void createPostView() {
        if (post.getImgBitmap() != null) {
            imgv_banner.setImageBitmap(post.getImgBitmap());
        } else {
            if (post.getBanner().equals("")) {

            } else {
                StorageReference bannerRef = stRef.child(post.getBanner());
                bannerRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(PostdetailActivity.this)
                                .load(uri)
                                .into(imgv_banner);
                    }
                });
            }
        }
        txtv_un.setText(post.getUn());
        getUserInfo();
        txtv_date.setText(post.getDate());
        txtv_content.setText(post.getContent());
        txtv_title.setText(post.getTitle());
        txtv_storename.setText(post.getStoreName());
        if (post.getPriceRate() > 0 && post.getHealthyRate() > 0 && post.getServiceRate() > 0) {
            linear_rate.setVisibility(View.VISIBLE);
            txtv_pricerate.setText(post.getPriceRate() + "");
            txtv_healthrate.setText(post.getHealthyRate() + "");
            txtv_servicerate.setText(post.getServiceRate() + "");
        }
        if (!post.getFoodID().equals("")) {
            linear_food.setVisibility(View.VISIBLE);
            txtv_foodname.setText(" ");
            txtv_foodprice.setText(" ");
            txtv_rateComment.setText(" ");
            txtv_foodname.setBackgroundColor(getResources().getColor(R.color.color_waiting));
            txtv_foodprice.setBackgroundColor(getResources().getColor(R.color.color_waiting));
            txtv_rateComment.setBackgroundColor(getResources().getColor(R.color.color_waiting));
            rb_foodrating.setRating(0);
            getFoodInfo();
        }
    }

    private void getUserInfo() {
        userEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                String key = dataSnapshot.getKey();
                user.setuID(key);
                if (!user.getAvatar().equals("")) {
                    StorageReference avaRef = stRef.child(user.getAvatar());
                    avaRef.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.with(PostdetailActivity.this)
                                            .load(uri)
                                            .error(R.drawable.ic_thumbavatar)
                                            .into(imgv_avatar);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    imgv_avatar.setImageResource(R.drawable.ic_thumbavatar);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.addListenerForSingleValueEvent(userEventListener);
    }

    private void getFoodInfo() {
        foodsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                food = dataSnapshot.getValue(Food.class);
                String key = dataSnapshot.getKey();
                food.setFoodID(key);
                StorageReference imgRef = stRef.child(food.getFoodImg());
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(PostdetailActivity.this)
                                .load(uri)
                                .into(imgv_food);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
                txtv_foodname.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                txtv_foodprice.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                txtv_rateComment.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                txtv_foodname.setText(food.getName());
                txtv_foodprice.setText(food.getPrice() + "");
                rb_foodrating.setRating(food.getRating());
                int rat = (int) food.getRating() / (int) food.getTotal();
                switch (rat) {
                    case 1:
                        txtv_rateComment.setText(getString(R.string.txt_toofuckingbad));
                        break;
                    case 2:
                        txtv_rateComment.setText(getString(R.string.txt_bad));
                        break;
                    case 3:
                        txtv_rateComment.setText(getString(R.string.txt_fine));
                        break;
                    case 4:
                        txtv_rateComment.setText(getString(R.string.txt_good));
                        break;
                    case 5:
                        txtv_rateComment.setText(getString(R.string.txt_toofuckinggood));
                        break;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.food_CODE) + post.getFoodID())
                .addListenerForSingleValueEvent(foodsEventListener);
    }

    private void getAllComts() {
        commentEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Comment comment = item.getValue(Comment.class);
                    String key = item.getKey();
                    comment.setCommentID(key);
                    comments.add(comment);
                }
                comtAdapter.notifyDataSetChanged();
                dbRef.child(getString(R.string.posts_CODE) + postID + "/"
                        + getString(R.string.comments_CODE))
                        .orderByChild("isHidden_postID")
                        .equalTo(false + "_" + postID)
                        .removeEventListener(commentEventListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.posts_CODE) + postID + "/"
                + getString(R.string.comments_CODE))
                .orderByChild("isHidden_postID")
                .equalTo(false + "_" + postID)
                .addValueEventListener(commentEventListener);
    }

    private void getAllImgs() {
        imagesEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Image image = item.getValue(Image.class);
                    String key = item.getKey();
                    image.setImageID(key);
                    images.add(image);
                }
                imgsAdapter.notifyDataSetChanged();
                dbRef.child(getString(R.string.images_CODE))
                        .orderByChild("isHidden_postID")
                        .equalTo(false + "_" + postID)
                        .removeEventListener(imagesEventListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.images_CODE))
                .orderByChild("isHidden_postID")
                .equalTo(false + "_" + postID)
                .addValueEventListener(imagesEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (post.isHidden()) {
            menu = AppUtils.createMenu(menu, new String[]{
                    getString(R.string.txt_report),
                    getString(R.string.txt_showpost),
                    getString(R.string.txt_changeinfo)});
        } else {
            menu = AppUtils.createMenu(menu, new String[]{
                    getString(R.string.txt_report),
                    getString(R.string.text_hidepost),
                    getString(R.string.txt_changeinfo)});
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                return true;
            case 1:
                return true;
            case 2:
                if (post.isHidden()) {
//                    showStore(item);
                } else {
//                    hideStore(item);
                }
                return true;
            case 3:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        this.unregisterReceiver(broadcastReceiver);
    }

    private void saveCommentValue() {
        String key = dbRef.child(getString(R.string.comments_CODE)).push().getKey();
        User user = LoginSession.getInstance().getUser();
        comment = new Comment(comtContent, user.getUn(), user.getAvatar(), user.getuID(), postID);
        Map<String, Object> comtValue = comment.toMap();
        Map<String, Object> childUpdate = new HashMap<>();
        childUpdate.put(getString(R.string.posts_CODE) + postID + "/"
                + getString(R.string.comments_CODE) + key, comtValue);
        dbRef.updateChildren(childUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        comments.add(comment);
                        comtAdapter.notifyDataSetChanged();
                        comment = null;
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PostdetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                edt_comment.setText(comment.getContent());
                comment = null;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgv_send_postdetail:
                commentDialog.dismiss();
                saveCommentValue();
                break;
            case R.id.txtv_writecomt_postdetail:
                commentDialog.show();
                break;
        }
    }

}
