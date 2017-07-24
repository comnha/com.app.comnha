package com.app.ptt.comnha.Activity;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Comment_rcyler_adapter;
import com.app.ptt.comnha.Adapters.Photo_recycler_adapter;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Dialog.BlockUserDialog;
import com.app.ptt.comnha.Dialog.ReportDialog;
import com.app.ptt.comnha.Models.FireBase.Comment;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Image;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.ReportpostNotify;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.Models.FireBase.UserNotification;
import com.app.ptt.comnha.Modules.orderObjectByTime;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChoosePhotoList;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.AppUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.app.ptt.comnha.Const.Const.REPORTS.REPORT_POST;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostdetailActivity extends BaseActivity implements View.OnClickListener {
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
    private Store store;
    CollapsingToolbarLayout clayout;
    Food food;
    RecyclerView rv_imgs, rv_comments;
    RecyclerView.LayoutManager imgsLm, comtLm;
    ArrayList<Image> images;
    Photo_recycler_adapter imgsAdapter = null;
    List<Comment> comments;
    Map<String,Comment> commentMap;
    Comment_rcyler_adapter comtAdapter;
    DatabaseReference dbRef;
    StorageReference stRef;
    ValueEventListener commentEventListener, imagesEventListener, foodsEventListener,
            userValueListener, userEventListener;
    LinearLayout linear_rate, linear_food;
    ProgressDialog plzw8Dialog = null;
    Post post;
    BottomSheetDialog commentDialog;
    EditText edt_comment;
    String comtContent;
    Comment comment = null;
    User user = null, blockCommentUser;
    Menu pubMenu = null;
    boolean isConnected = true;
    IntentFilter mIntentFilter;
    String keyReport,userIdReport;
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    private static final int REQUEST_SIGNIN = 101;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    ProgressDialog plzwaitDialog;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastSendAddress)) {
                Log.i(LOG + ".onReceive form Service", "isConnected= " + intent.getBooleanExtra("isConnected", false));
                isConnected = intent.getBooleanExtra("isConnected", false);
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

        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Const.DATABASE_PATH);
        stRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Const.STORAGE_PATH);
        mAuth = FirebaseAuth.getInstance();

        comments=new ArrayList<>();
        commentMap=new HashMap<>();
        init();
        if (ChoosePost.getInstance().getPost() != null) {
            getPost(ChoosePost.getInstance().getPost().getPostID());

        } else {
            onBackPressed();
        }
        getAllImgs();
    }

    @Override
    public void onStart() {
        isConnected = MyService.isNetworkAvailable(this);
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

    private void init() {

        View view_include = findViewById(R.id.include_postdetail_content);
        plzw8Dialog = AppUtils.setupProgressDialog(this,
                getString(R.string.txt_plzwait), null, false, false,
                ProgressDialog.STYLE_SPINNER, 0);
        imgv_banner = (ImageView) findViewById(R.id.imgv_banner_postdetail);
        toolbar = (Toolbar) findViewById(R.id.toolbar_postdetail);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
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
                Intent intent_openViewPhoto = new Intent(activity, ViewPhotosActivity.class);
                intent_openViewPhoto.putExtra("imgPosition", images.indexOf(image));
                startActivity(intent_openViewPhoto);
            }
        });

        rv_comments = (RecyclerView) view_include.findViewById(R.id.rv_comments_postdetail);
        rv_comments.setHasFixedSize(true);
        comtLm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false);
        rv_comments.setLayoutManager(comtLm);
        comtAdapter = new Comment_rcyler_adapter(this, comments, stRef);
        rv_comments.setAdapter(comtAdapter);
        if (LoginSession.getInstance().getUser() != null) {
            if (LoginSession.getInstance().getUser().getRole() != 0) {
                comtAdapter.setOnItemLongClickListener(new Comment_rcyler_adapter.OnItemLongClickListener() {
                    @Override
                    public void onItemClick(final Comment comment, View itemView) {
                        PopupMenu popupMenu = new PopupMenu(PostdetailActivity.this,
                                itemView, Gravity.END);
                        Menu menu = popupMenu.getMenu();
                        List<Pair<Integer, String>> contents = new ArrayList<>();
                        contents.add(new Pair<Integer, String>
                                (R.string.text_delcomt,
                                        getString(R.string.text_delcomt)));
                        if(!post.getUserID().equals(LoginSession.getInstance().getUser().getuID())) {
                            contents.add(new Pair<Integer, String>
                                    (R.string.text_block_comment,
                                            getString(R.string.text_block_comment)));
                        }
                        menu = AppUtils.createMenu(menu, contents);
                        popupMenu.show();
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.string.text_delcomt:
                                        delComment(comment.getCommentID());
                                        break;
                                    case R.string.text_block_comment:
                                        plzwaitDialog.show();
                                        getUserForBlockComment(comment.getUserID());
                                }
                                return true;
                            }
                        });

                    }
                });
            }
        }

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
        plzwaitDialog = AppUtils.setupProgressDialog(this,
                getString(R.string.txt_plzwait), null, false, false,
                ProgressDialog.STYLE_SPINNER, 0);
    }


    private void createPostView() {
        if (post.getImgBitmap() != null) {
            imgv_banner.setImageBitmap(post.getImgBitmap());
        } else {
            if (post.getBanner().equals("")) {
                imgv_banner.setBackgroundResource(R.drawable.img_banner);

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
    private void getPost(final String id) {
        try {
            ValueEventListener postsEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        String key = dataSnapshot.getKey();
                        post = dataSnapshot.getValue(Post.class);
                        post.setPostID(key);
                    createPostView();
                    postID = post.getPostID();
                    comments.clear();
                    for(Map.Entry<String,Comment> entry:post.getComments().entrySet()){
                        comments.add(entry.getValue());
                    }
                    commentMap=post.getComments();
                    sortComment();
                    loadMenu();
                    getStoreDetail(post.getStoreID());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
                dbRef.child(getString(R.string.posts_CODE)+id)
                        .addValueEventListener(postsEventListener);

        }catch (Exception e){

        }
    }
    public boolean loadMenu(){
        if(pubMenu!=null) {
            pubMenu = AppUtils.createMenu(pubMenu, returnContentMenuItems());
            return super.onCreateOptionsMenu(pubMenu);
        }
        return false;
    }
    public void sortComment(){
        Collections.sort(comments,new orderObjectByTime());
        comtAdapter.notifyDataSetChanged();
    }
    private void getUserInfo() {
        userValueListener = new ValueEventListener() {
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
        dbRef.addListenerForSingleValueEvent(userValueListener);
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
                txtv_foodprice.setText(food.getPrice()+ "đ");
                rb_foodrating.setRating(post.getFoodRate());
//                int rat = (int) food.getRating() / (int) food.getTotal();
                int rat = (int) post.getFoodRate();
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
                ChoosePhotoList.getInstance().setImage(images);
                imgsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.images_CODE))
                .orderByChild("isHidden_postID")
                .equalTo(false + "_" + postID)
                .addListenerForSingleValueEvent(imagesEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu = AppUtils.createMenu(menu, returnContentMenuItems());
        pubMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    private List<Pair<Integer, String>> returnContentMenuItems() {
        int role = 0;
        String uID = "";

        if (LoginSession.getInstance().getUser() != null) {
            role = LoginSession.getInstance().getUser().getRole();
            uID = LoginSession.getInstance().getUser().getuID();
        }
        List<Pair<Integer, String>> contents = new ArrayList<>();
        if(post!=null) {
            if (role > 0) {
                contents.add(new Pair<Integer, String>
                        (R.string.txt_changeinfo, getString(R.string.txt_changeinfo)));
                if (post.isHidden()) {
                    if(post.getPostType()==0) {
                        contents.add(new Pair<Integer, String>
                                (R.string.txt_acceptpost, getString(R.string.txt_acceptpost)));
                        contents.add(new Pair<Integer, String>
                                (R.string.txt_rejectpost, getString(R.string.txt_rejectpost)));
                    }
                    if(post.getPostType()==3){
                        contents.add(new Pair<Integer, String>
                                (R.string.txt_acceptpostreport, getString(R.string.txt_acceptpostreport)));
                        contents.add(new Pair<Integer, String>
                                (R.string.txt_rejectpostreport, getString(R.string.txt_rejectpostreport)));
                    }

                } else {
                    contents.add(new Pair<Integer, String>
                            (R.string.txt_rejectpost, getString(R.string.txt_rejectpost)));
                }
            }
            if (uID.equals(post.getUserID())) {
                contents.add(new Pair<Integer, String>
                        (R.string.txt_changeinfo, getString(R.string.txt_changeinfo)));
            } else {
                if (post.checkExist(uID)) {
                    contents.add(new Pair<Integer, String>
                            (R.string.txt_unfollowPost, getString(R.string.txt_unfollowPost)));

                } else {
                    contents.add(new Pair<Integer, String>
                            (R.string.txt_followPost, getString(R.string.txt_followPost)));
                }
                contents.add(new Pair<Integer, String>
                        (R.string.txt_report, getString(R.string.txt_report)));

            }
        }
        return contents;
    }
    public boolean checkExistUser(String id){
        for(Comment comment: comments){
            if(comment.getUserID().toLowerCase().equals(id)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.string.txt_report:
                if (LoginSession.getInstance().getUser() != null) {
                    reportPost();
                } else {
                    requestSignin();
                }
                return true;
            case R.string.txt_rejectpost:
                    hidePost(-1);
                    sendBroadcast();
                return true;
            case R.string.txt_acceptpost:
                    showPost(1);

                    sendBroadcast();
                return true;
            case R.string.txt_acceptpostreport:
                showPost(2);
                sendBroadcast();
                return true;
            case R.string.txt_rejectpostreport:
                hidePost(-2);
                sendBroadcast();
                return true;

            case R.string.txt_changeinfo:
                changeContent();

                return true;
            case R.string.txt_followPost:
                if (LoginSession.getInstance().getUser() != null) {
                    updatePost(false);
                } else {
                    requestSignin();
                }
                return true;
            case R.string.txt_unfollowPost:
                if (LoginSession.getInstance().getUser() != null) {
                   updatePost(true);
                } else {
                    requestSignin();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void updatePost(boolean type){
        boolean status=false;
        if(type) {
            if (post.removeUser(LoginSession.getInstance().getUser().getuID())) {
               status=true;
            }else{
                AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), "Bạn đã bỏ theo dõi bài viết này");

            }
        }else{
            if(post.addUsertoList(LoginSession.getInstance().getUser().getuID())){
                status=true;
            }else{
                AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), "Bạn đã theo dõi bài viết này");

            }
        }

        if(status) {
            plzw8Dialog.show();
            Map<String, Object> childUpdate = new HashMap<>();
            Map<String, Object> postValue = post.toMap();
            childUpdate.put(getString(R.string.posts_CODE) + post.getPostID(), postValue);
            dbRef.updateChildren(childUpdate)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loadMenu();
                            AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), "Đã theo dõi bài viết");
                            plzw8Dialog.cancel();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    plzw8Dialog.cancel();
                    AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), "Có lỗi. Xin thử lại");
                }
            });
        }
    }
    private void sendBroadcast(){
         Intent broadcastIntent=new Intent();
        broadcastIntent.setAction(Const.INTENT_KEY_RELOAD_DATA);
        sendBroadcast(broadcastIntent);
    }

    private void requestSignin() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.txt_nologin)
                        + "\n" + getString(R.string.txt_uneedlogin))
                .setPositiveButton(getString(R.string.text_signin), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent_signin = new Intent(PostdetailActivity.this,
                                SignInActivity.class);
                        startActivityForResult(intent_signin, REQUEST_SIGNIN);
                    }
                })
                .setNegativeButton(getString(R.string.text_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void changeContent() {
        final String[] content = {post.getContent()}, title = {post.getTitle()};
        final BottomSheetDialog edtPostDialog = new BottomSheetDialog(this);
        edtPostDialog.setContentView(R.layout.layout_editpost);
        final TextInputLayout ilayout_content =
                (TextInputLayout) edtPostDialog.findViewById(R.id.ilayout_content_editpost),
                ilayout_title =
                        (TextInputLayout) edtPostDialog.findViewById(R.id.ilayout_title_editpost);

        final EditText edt_title = (EditText) edtPostDialog.findViewById(R.id.edt_title_editpost);
        edt_title.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        edt_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                title[0] = charSequence.toString();
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    ilayout_title.setError(null);
                } else {
                    ilayout_title.setError(
                            getResources().getString(R.string.txt_notitle));
                }
            }
        });
        final EditText edt_content = (EditText) edtPostDialog.findViewById(R.id.edt_content_editpost);
        edt_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
        edt_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                content[0] = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    ilayout_content.setError(null);
                } else {
                    ilayout_content.setError(
                            getResources().getString(R.string.txt_nocontent));
                }
            }
        });
        edt_content.setText(post.getContent());
        edt_title.setText(post.getTitle());
        Button btnSave = (Button) edtPostDialog.findViewById(R.id.btn_save_editpost),
                btnCancel = (Button) edtPostDialog.findViewById(R.id.btn_close_editpost);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtils.getText(edt_title).equals("")) {
                    edt_title.requestFocus();
                    ilayout_title.setError(getString(R.string.txt_notitle));
                } else if (AppUtils.getText(edt_content).equals("")) {
                    edt_content.requestFocus();
                    ilayout_content.setError(getString(R.string.txt_nocontent));
                } else {
                    if (post.getContent().equals(content[0])
                            && post.getTitle().equals(title[0])) {
                        edtPostDialog.cancel();
                    } else if (post.getTitle().equals(title[0])
                            && !post.getContent().equals(content[0])) {
                        saveEdit(post.getTitle(), content[0], edtPostDialog);
                    } else if (post.getContent().equals(content[0])
                            && !post.getTitle().equals(title[0])) {
                        saveEdit(title[0], post.getContent(), edtPostDialog);
                    } else {
                        saveEdit(title[0], content[0], edtPostDialog);
                    }
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPostDialog.cancel();
            }
        });
        edtPostDialog.show();
    }

    private void saveEdit(final String title, final String content,
                          final BottomSheetDialog edtPostDialog) {
        plzw8Dialog.show();
        Post edtPost = post;
        edtPost.setContent(content);
        edtPost.setTitle(title);
        Map<String, Object> postValue = edtPost.toMap();
        Map<String, Object> updatePost = new HashMap<>();
//        updatePost.put(getString(R.string.posts_CODE) + edtPost.getPostID(), postValue);
        updatePost.put(getString(R.string.posts_CODE) + edtPost.getPostID()
                + "/" + "content", content);
        updatePost.put(getString(R.string.posts_CODE) + edtPost.getPostID()
                + "/" + "title", title);
        dbRef.updateChildren(updatePost)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        plzw8Dialog.dismiss();
                        txtv_content.setText(content);
                        txtv_title.setText(title);
                        edtPostDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void reportPost() {
        post.setPostType(3);
        post.setHidden(true);
        Map<String,Object> updatePost=post.toMap();
        Map<String,Object> childUpdate=new HashMap<>();
        childUpdate.put(getString(R.string.posts_CODE)+post.getPostID(),updatePost);
        ReportDialog reportDialog = new ReportDialog();
        reportDialog.setChildUpdate(childUpdate);
        reportDialog.setReport(REPORT_POST, post);
        reportDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AddfoodDialog);
        reportDialog.setOnPosNegListener(new ReportDialog.OnPosNegListener() {
            @Override
            public void onPositive(boolean isClicked, Map<String,
                    Object> childUpdate, final Dialog dialog,String key) {
                if (isClicked) {
                    keyReport=key;
                    dialog.dismiss();
                    plzw8Dialog.show();
                    childUpdate= notificationToUser(childUpdate,3,2);
                    dbRef.updateChildren(childUpdate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    loadMenu();
                                    plzw8Dialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.show();
                            plzw8Dialog.dismiss();
                            Toast.makeText(PostdetailActivity.this, e.getMessage(),
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
        reportDialog.show(getSupportFragmentManager(), "report_post");
    }
    private void approveReport(final Map<String,Object> childUpdate, final boolean isApprove){
            final ValueEventListener reportPost =new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue()!=null){
                        for(DataSnapshot item: dataSnapshot.getChildren()) {
                            ReportpostNotify reportpostNotify = item.getValue(ReportpostNotify.class);
                            reportpostNotify.setId(item.getKey());
                            reportpostNotify.setApprove(!isApprove);
                            Map<String, Object> updateReport = reportpostNotify.toMap();
                            childUpdate.put(getString(R.string.reportPost_CODE) + item.getKey(), updateReport);
                        }
                    }
                    dbRef.updateChildren(childUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           loadMenu();
                            plzw8Dialog.cancel();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            plzw8Dialog.cancel();
                            Toast.makeText(getApplicationContext(),
                                    e.getMessage(), Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            dbRef.child(getString(R.string.reportPost_CODE))
                    .orderByChild("postID")
                    .equalTo(postID)
                    .addListenerForSingleValueEvent(reportPost);
    }
    private void showPost(final int postType) {
        plzw8Dialog.show();
        Map<String, Object> childUpdate = new HashMap<>();
        switch (post.getPostType()){
            case 0:
                childUpdate =notificationToUser(childUpdate,1,2);
                break;
            case 3:
                childUpdate= notificationToUser(childUpdate,2,2);
                break;
        }
        post.setHidden(false);
        post.setPostType(postType);
        if(food!=null) {
            childUpdate=changeFoodRate(childUpdate,true,post.getPostType());
        }
        if(store!=null) {
            childUpdate = changeStoreRate(childUpdate, true, post.getPostType());
        }
        String key = post.getPostID();
        Map<String, Object> postValue = post.toMap();

        childUpdate.put(getString(R.string.posts_CODE)+key,postValue);
//        childUpdate.put(getString(R.string.posts_CODE)
//                + key + "/" + "isHidden", false);
//        childUpdate.put(getString(R.string.posts_CODE)
//                + key + "/" + "isHidden_dist_prov", false
//                + "_" + post.getDist_pro());
//        childUpdate.put(getString(R.string.posts_CODE)
//                + key + "/" + "isHidden_foodID", false
//                + "_" + post.getFoodID());
//        childUpdate.put(getString(R.string.posts_CODE)
//                + key + "/" + "isHidden_storeID", false
//                + "_" + post.getStoreID());
//        childUpdate.put(getString(R.string.posts_CODE)
//                + key + "/" + "isHidden_uID", false
//                + "_" + post.getUserID());

        approveReport(childUpdate,false);
    }
    public Map<String,Object> changeFoodRate(Map<String,Object> childUpdate,boolean status,int  type){
        if(status) {
            long total = food.getTotal() + 1;
            long rat = food.getRating() + (long) post.getFoodRate();
            food.setRating(rat);
            food.setTotal(total);
        }else{
            if(type==-2) {
                long total = food.getTotal() - 1;
                long rat = food.getRating() - (long) post.getFoodRate();
                food.setRating(rat);
                food.setTotal(total);
            }
        }
            Map<String, Object> foodValue = food.toMap();
            childUpdate.put(getString(R.string.food_CODE)
                    + food.getFoodID(), foodValue);
        return childUpdate;
    }
    public Map<String,Object> changeStoreRate(Map<String,Object> childUpdate, boolean status,int type){
        if(status) {
            long priceSum = store.getPriceSum() + post.getPriceRate(),
                    healthSum = store.getHealthySum() + post.getHealthyRate(),
                    serviceSum = store.getServiceSum() + post.getServiceRate(),
                    sum = store.getSize() + 1;
            store.setPriceSum(priceSum);
            store.setHealthySum(healthSum);
            store.setServiceSum(serviceSum);
            store.setSize(sum);
        }else{
            if(type==-2){
                long priceSum = store.getPriceSum() - post.getPriceRate(),
                        healthSum = store.getHealthySum() - post.getHealthyRate(),
                        serviceSum = store.getServiceSum() - post.getServiceRate(),
                        sum = store.getSize() - 1;
                store.setPriceSum(priceSum);
                store.setHealthySum(healthSum);
                store.setServiceSum(serviceSum);
                store.setSize(sum);
            }
        }

            Map<String, Object> storeValue = store.toMap();
            childUpdate.put(getString(R.string.store_CODE) + store.getStoreID(),
                    storeValue);
        return childUpdate;
    }

    private void hidePost(final int postType) {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setMessage(getString(R.string.txt_hideconfirm))
                .setPositiveButton(getString(R.string.txt_hide),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    final DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                plzw8Dialog.show();
                                Map<String, Object> childUpdate = new HashMap<>();
                                switch (post.getPostType()){
                                case 0:
                                    childUpdate=notificationToUser(childUpdate,-1,2);
                                break;
                                case 3:
                                    childUpdate= notificationToUser(childUpdate,-2,2);
                                break;
                            }
                                post.setPostType(postType);
                                post.setHidden(true);
                                if(food!=null) {
                                    childUpdate = changeFoodRate(childUpdate, false, post.getPostType());
                                }
                                if(store!=null) {
                                    childUpdate = changeStoreRate(childUpdate, false, post.getPostType());
                                }
                                String key = post.getPostID();

                                Map<String, Object> postValue = post.toMap();

                                childUpdate.put(getString(R.string.posts_CODE)+key,postValue);
//                                childUpdate.put(getString(R.string.posts_CODE)
//                                        + key + "/" + "isHidden", true);
//                                childUpdate.put(getString(R.string.posts_CODE)
//                                        + key + "/" + "isHidden_dist_prov", true
//                                        + "_" + post.getDist_pro());
//                                childUpdate.put(getString(R.string.posts_CODE)
//                                        + key + "/" + "isHidden_foodID", true
//                                        + "_" + post.getFoodID());
//                                childUpdate.put(getString(R.string.posts_CODE)
//                                        + key + "/" + "isHidden_storeID", true
//                                        + "_" + post.getStoreID());
//                                childUpdate.put(getString(R.string.posts_CODE)
//                                        + key + "/" + "isHidden_uID", true
//                                        + "_" + post.getUserID());
                                approveReport(childUpdate,true);
                            }
                        })
                .setNegativeButton(getString(R.string.text_no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                .show();
    }
    private void getStoreDetail(String id) {
        try {
            ValueEventListener storeEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                        store = dataSnapshot.getValue(Store.class);
                        store.setStoreID(dataSnapshot.getKey());


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

                dbRef.child(getString(R.string.store_CODE)+id)
                        .addListenerForSingleValueEvent(storeEventListener);

        }catch (Exception e){

        }
    }
    public Map<String,Object> notificationToUser(Map<String,Object> childUpdate,int status,int type){

        switch (status){
            case 1:
                //accept
                //Notification to user who follow this post
                for(String mUerId: store.getUsersFollow()) {
                    try {
                        if(mUerId!=null) {
                            if (!post.getUserID().toLowerCase().equals(mUerId.toLowerCase())) {
                                UserNotification userNotification = new UserNotification();
                                userNotification.setPostID(post.getPostID());
                                userNotification.setStatus(0);
                                userNotification.setType(type);
                                userNotification.setShown(true);
                                userNotification.setUserEffectId(post.getUserID());
                                userNotification.setUserEffectName(post.getUn());
                                Map<String, Object> userNotificationMap = userNotification.toMap();
                                String key = dbRef.child(getString(R.string.user_notification_CODE) + mUerId).push().getKey();
                                childUpdate.put(getString(R.string.user_notification_CODE) + mUerId + "/" + key, userNotificationMap);
                            }
                        }
                    }catch (Exception e){

                    }
                }
                UserNotification userNotification=new UserNotification();
                userNotification.setPostID(post.getPostID());
                userNotification.setType(type);userNotification.setShown(true);
                userNotification.setStatus(status);
                userNotification.setƠwner(true);
                Map<String,Object> userNotificationMap=userNotification.toMap();
                String key =dbRef.child(getString(R.string.user_notification_CODE)+post.getUserID()).push().getKey();
                childUpdate.put(getString(R.string.user_notification_CODE)+post.getUserID()+"/"+key,userNotificationMap);

                if(!store.getUserID().toLowerCase().equals(post.getUserID().toLowerCase())){
                    UserNotification userEffectNotification=new UserNotification();
                    userEffectNotification.setPostID(post.getPostID());
                    userEffectNotification.setType(type);
                    userEffectNotification.setShown(true);
                    userEffectNotification.setStatus(status);
                    userEffectNotification.setƠwner(true);
                    userEffectNotification.setUserEffectId(post.getUserID());
                    userEffectNotification.setUserEffectName(post.getUn());
                    Map<String,Object> userEffectNotificationMap=userEffectNotification.toMap();
                    String userEffectKey =dbRef.child(getString(R.string.user_notification_CODE)+store.getUserID()).push().getKey();
                    childUpdate.put(getString(R.string.user_notification_CODE)+store.getUserID()+"/"+userEffectKey,userEffectNotificationMap);
                }
                break;
            //reject
            case -1:
                UserNotification userRejectNotification=new UserNotification();
                userRejectNotification.setPostID(post.getPostID());
                userRejectNotification.setType(type);userRejectNotification.setShown(true);
                userRejectNotification.setStatus(status);
                userRejectNotification.setƠwner(true);
                Map<String,Object> userRejectNotificationMap=userRejectNotification.toMap();
                String userRejectKey =dbRef.child(getString(R.string.user_notification_CODE)+post.getUserID()).push().getKey();
                childUpdate.put(getString(R.string.user_notification_CODE)+post.getUserID()+"/"+userRejectKey,userRejectNotificationMap);
                break;
            //was reported
            case 3:
                UserNotification userReport=new UserNotification();
                userReport.setPostID(post.getPostID());
                userReport.setType(type);userReport.setShown(true);
                userReport.setStatus(status);
                userReport.setƠwner(true);
                userReport.setUserEffectId(LoginSession.getInstance().getUser().getuID());
                userReport.setUserEffectName(LoginSession.getInstance().getUser().getUn());
                userReport.setReportId(keyReport);
                Map<String,Object> userReportMap=userReport.toMap();
                String keyReportNoti =dbRef.child(getString(R.string.user_notification_CODE)+post.getUserID()).push().getKey();
                childUpdate.put(getString(R.string.user_notification_CODE)+post.getUserID()+"/"+keyReportNoti,userReportMap);
                break;
            //reject report
            case 2:
                UserNotification userRejectReport=new UserNotification();
                userRejectReport.setPostID(post.getPostID());
                userRejectReport.setType(type);userRejectReport.setShown(true);
                userRejectReport.setStatus(status);

                userRejectReport.setƠwner(true);
                userRejectReport.setReportId(keyReport);
                Map<String,Object> userRejectReportMap=userRejectReport.toMap();
                String keyuserRejectReportNoti =dbRef.child(getString(R.string.user_notification_CODE)+post.getUserID()).push().getKey();
                childUpdate.put(getString(R.string.user_notification_CODE)+post.getUserID()+"/"+keyuserRejectReportNoti,userRejectReportMap);
                break;
            //accept report
            case -2:
                UserNotification userAcceptReport=new UserNotification();
                userAcceptReport.setPostID(post.getPostID());
                userAcceptReport.setType(type);userAcceptReport.setShown(true);
                userAcceptReport.setStatus(status);

                userAcceptReport.setƠwner(true);
                userAcceptReport.setReportId(keyReport);
                Map<String,Object> userAcceptReportMap=userAcceptReport.toMap();
                String keyuserAcceptReportNoti =dbRef.child(getString(R.string.user_notification_CODE)+post.getUserID()).push().getKey();
                childUpdate.put(getString(R.string.user_notification_CODE)+post.getUserID()+"/"+keyuserAcceptReportNoti,userAcceptReportMap);
                break;
        }

        return childUpdate;
    }
    @Override
    public void onStop() {
        super.onStop();
        this.unregisterReceiver(broadcastReceiver);
    }

    private void saveCommentValue() {
        String key = dbRef.child(getString(R.string.posts_CODE)+post.getPostID()+"/"+getString(R.string.comments_CODE)).push().getKey();
        User user = LoginSession.getInstance().getUser();
        comment = new Comment(comtContent, user.getUn(), user.getAvatar(), user.getuID(), postID);
        comment.setCommentID(key);
        commentMap.put(key,comment);
        post.setComments(commentMap);
        Map<String, Object> childUpdate = new HashMap<>();
        childUpdate=updateCommentNotification(childUpdate);
        Map<String, Object> postValue = post.toMap();
        childUpdate.put(getString(R.string.posts_CODE)+post.getPostID(), postValue);
        dbRef.updateChildren(childUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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
    public Map<String,Object> updateCommentNotification(Map<String,Object> childUpdate){
        boolean isExist=false;
        for(String mUSer:post.getUserComment()){
            if(mUSer.toLowerCase().equals(LoginSession.getInstance().getUser().getuID().toLowerCase())){
                isExist=true;
            }else{
                UserNotification userNotification=new UserNotification();
                userNotification.setUserEffectId(LoginSession.getInstance().getUser().getuID());
                userNotification.setUserEffectName(LoginSession.getInstance().getUser().getUn());
                userNotification.setType(3);userNotification.setShown(true);
                userNotification.setPostID(post.getPostID());
                userNotification.setƠwner(false);
                Map<String,Object> userNotificationMap=userNotification.toMap();
                String key =dbRef.child(getString(R.string.user_notification_CODE)+mUSer).push().getKey();
                childUpdate.put(getString(R.string.user_notification_CODE)+mUSer+"/"+key,userNotificationMap);
            }
        }
        //if user not exist in user commented list
        if(!isExist &&!LoginSession.getInstance().getUser().getuID().toLowerCase().equals(post.getUserID().toLowerCase()) ){
            post.addUsertoList(LoginSession.getInstance().getUser().getuID());
        }
        if(!LoginSession.getInstance().getUser().getuID().toLowerCase().equals(post.getUserID().toLowerCase())) {
            UserNotification userNotification=new UserNotification();
            userNotification.setUserEffectId(LoginSession.getInstance().getUser().getuID());
            userNotification.setUserEffectName(LoginSession.getInstance().getUser().getUn());
            userNotification.setType(3);userNotification.setShown(true);
            userNotification.setƠwner(true);
            userNotification.setPostID(post.getPostID());
            Map<String,Object> userNotificationMap=userNotification.toMap();
            String key =dbRef.child(getString(R.string.user_notification_CODE)+post.getUserID()).push().getKey();
            childUpdate.put(getString(R.string.user_notification_CODE)+post.getUserID()+"/"+key,userNotificationMap);
        }

        return childUpdate;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgv_send_postdetail:
                if (LoginSession.getInstance().getUser() != null) {
                    commentDialog.dismiss();
                    saveCommentValue();
                } else {

                }
                break;
            case R.id.txtv_writecomt_postdetail:
                if (LoginSession.getInstance().getUser() != null) {
                    edt_comment.getText().clear();
                    edt_comment.requestFocus();
                    commentDialog.show();
                } else {

                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SIGNIN:
                if (resultCode == RESULT_OK) {
                    getUser();
                } else {

                }
                break;
        }
    }

    private void getUser() {
        plzw8Dialog.show();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // User is signed in
                    getUserInfo(firebaseUser);
                    Log.d("onAuthStateChanged", "onAuthStateChanged:signed_in:" + firebaseUser.getUid());
                } else {
                    // User is signed out
                    Log.d("onAuthStateChanged", "onAuthStateChanged:signed_out");
                    plzw8Dialog.dismiss();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void getUserInfo(final FirebaseUser firebaseUser) {
        userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                String key = firebaseUser.getUid();
                user.setuID(key);
                LoginSession.getInstance().setUser(user);
                LoginSession.getInstance().setFirebUser(firebaseUser);
                mAuth.removeAuthStateListener(mAuthListener);
                loadMenu();
                plzw8Dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef.child(getString(R.string.users_CODE)
                + firebaseUser.getUid())
                .addListenerForSingleValueEvent(userValueListener);
    }

    private void delComment(final String comtID) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.text_delcomt))
                .setPositiveButton(getString(R.string.txt_del), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dbRef.child(getString(R.string.posts_CODE) + postID + "/"
                                + getString(R.string.comments_CODE) + comtID).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        for (Iterator<Comment> iterator=comments.iterator();iterator.hasNext();){
                                            Comment comment=iterator.next();
                                            if (comment.getCommentID().equals(comtID)) {
                                                iterator.remove();
                                                comtAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PostdetailActivity.this,
                                                e.getMessage(), Toast.LENGTH_LONG)
                                                .show();
                                    }
                                });
                    }
                })
                .setNegativeButton(getString(R.string.text_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).setCancelable(false).show();

    }

    private void getUserForBlockComment(String userID) {
        userEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                plzwaitDialog.dismiss();
                blockCommentUser = dataSnapshot.getValue(User.class);
                String key = dataSnapshot.getKey();
                blockCommentUser.setuID(key);
                if (blockCommentUser.isCommentBlocked()) {
                    Toast.makeText(PostdetailActivity.this,
                            getString(R.string.txt_blockeduser)
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    BlockUserDialog blockDialog = new BlockUserDialog();
                    blockDialog.setStyle(DialogFragment.STYLE_NORMAL,
                            R.style.AddfoodDialog);
                    blockDialog.setBlock(blockCommentUser, Const.BLOCK_TYPE.BLOCK_COMMENT);
                    blockDialog.setOnPosNegListener(new BlockUserDialog.OnPosNegListener() {
                        @Override
                        public void onPositive(boolean isClicked, Map<String,
                                Object> childUpdate, final Dialog dialog) {
                            if (isClicked) {
                                dialog.dismiss();
                                plzwaitDialog.show();
                                dbRef.updateChildren(childUpdate)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                plzwaitDialog.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.show();
                                        plzwaitDialog.dismiss();
                                        Toast.makeText(PostdetailActivity.this, e.getMessage(),
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
                    blockDialog.show(PostdetailActivity.this.getSupportFragmentManager()
                            , "frag_blockuser");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.users_CODE)
                + userID)
                .addListenerForSingleValueEvent(userEventListener);
    }
}
