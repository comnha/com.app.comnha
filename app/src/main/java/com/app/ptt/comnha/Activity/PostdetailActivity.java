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
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.Models.FireBase.UserNotification;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChoosePhotoList;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.AppUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    CollapsingToolbarLayout clayout;
    Food food;
    RecyclerView rv_imgs, rv_comments;
    RecyclerView.LayoutManager imgsLm, comtLm;
    ArrayList<Image> images;
    Photo_recycler_adapter imgsAdapter = null;
    ArrayList<Comment> comments;
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

        if (ChoosePost.getInstance().getPost() != null) {
            post = ChoosePost.getInstance().getPost();
            postID = post.getPostID();
        } else {
            onBackPressed();
        }
        init();
        createPostView();
        getAllImgs();
        getAllComts();
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
        comments = new ArrayList<>();
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
                        contents.add(new Pair<Integer, String>
                                (R.string.text_block_comment,
                                        getString(R.string.text_block_comment)));
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
                Log.d("postde_commtsize: ", comments.size() + "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.posts_CODE) + postID + "/"
                + getString(R.string.comments_CODE))
                .orderByChild("isHidden")
                .equalTo(false)
                .addListenerForSingleValueEvent(commentEventListener);
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
        if (role == 1) {
            contents.add(new Pair<Integer, String>
                    (R.string.txt_changeinfo, getString(R.string.txt_changeinfo)));
            if (post.isHidden()) {
                contents.add(new Pair<Integer, String>
                        (R.string.txt_showpost, getString(R.string.txt_showpost)));
            } else {
                contents.add(new Pair<Integer, String>
                        (R.string.text_hidepost, getString(R.string.text_hidepost)));
            }
        } else {
            if (uID.equals(post.getUserID())) {
                if (post.isHidden()) {
                    contents.add(new Pair<Integer, String>
                            (R.string.txt_showpost, getString(R.string.txt_showpost)));
                } else {
                    contents.add(new Pair<Integer, String>
                            (R.string.text_hidepost, getString(R.string.text_hidepost)));
                }
            }
            contents.add(new Pair<Integer, String>
                    (R.string.txt_report, getString(R.string.txt_report)));
        }
        return contents;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.string.txt_report:
                if (LoginSession.getInstance().getUser() != null) {
                    reportPost();
                } else {
                    requesSignin();
                }
                return true;
            case R.string.text_hidepost:
                if (!post.isHidden()) {
                    hidePost();
                }
                return true;
            case R.string.txt_showpost:
                if (post.isHidden()) {
                    showPost();
                }
                return true;
            case R.string.txt_changeinfo:
                changeContent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void requesSignin() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.txt_nologin)
                        + "\n" + getString(R.string.txt_uneedlogin))
                .setPositiveButton(getString(R.string.text_signin), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent_signin = new Intent(PostdetailActivity.this,
                                AdapterActivity.class);
                        intent_signin.putExtra(getString(R.string.fragment_CODE),
                                getString(R.string.frg_signin_CODE));
                        intent_signin.putExtra("signinfromPostDe", 1);
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

        ReportDialog reportDialog = new ReportDialog();
        reportDialog.setReport(REPORT_POST, post);
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

    private void showPost() {
        plzw8Dialog.show();
        post.setHidden(false);
        String key = post.getPostID();
//        Toast.makeText(StoreDeatailActivity.this,
//                key, Toast.LENGTH_SHORT).show();
        Post childPost = post;
        Map<String, Object> postValue = childPost.toMap();
        Map<String, Object> childUpdate = new HashMap<>();
        childUpdate.put(getString(R.string.posts_CODE)
                + key + "/" + "isHidden", false);
        childUpdate.put(getString(R.string.posts_CODE)
                + key + "/" + "isHidden_dist_prov", false
                + "_" + post.getDist_pro());
        childUpdate.put(getString(R.string.posts_CODE)
                + key + "/" + "isHidden_foodID", false
                + "_" + post.getFoodID());
        childUpdate.put(getString(R.string.posts_CODE)
                + key + "/" + "isHidden_storeID", false
                + "_" + post.getStoreID());
        childUpdate.put(getString(R.string.posts_CODE)
                + key + "/" + "isHidden_uID", false
                + "_" + post.getUserID());
        dbRef.updateChildren(childUpdate)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                item.setTitle(getString(R.string.text_hidestore));
                                pubMenu.clear();
                                PostdetailActivity.this.onCreateOptionsMenu(pubMenu);
                                plzw8Dialog.cancel();
                            }
                        }).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        plzw8Dialog.cancel();
                        Toast.makeText(getApplicationContext(),
                                e.getMessage(), Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }

    private void hidePost() {
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
                                post.setHidden(true);
                                String key = post.getPostID();
//                                Toast.makeText(StoreDeatailActivity.this,
//                                        key, Toast.LENGTH_SHORT).show();
                                Post childPost = post;
                                Map<String, Object> postValue = childPost.toMap();
                                Map<String, Object> childUpdate = new HashMap<>();
//                                childUpdate.put(getString(R.string.posts_CODE)
//                                        + key, postValue);
                                childUpdate.put(getString(R.string.posts_CODE)
                                        + key + "/" + "isHidden", true);
                                childUpdate.put(getString(R.string.posts_CODE)
                                        + key + "/" + "isHidden_dist_prov", true
                                        + "_" + post.getDist_pro());
                                childUpdate.put(getString(R.string.posts_CODE)
                                        + key + "/" + "isHidden_foodID", true
                                        + "_" + post.getFoodID());
                                childUpdate.put(getString(R.string.posts_CODE)
                                        + key + "/" + "isHidden_storeID", true
                                        + "_" + post.getStoreID());
                                childUpdate.put(getString(R.string.posts_CODE)
                                        + key + "/" + "isHidden_uID", true
                                        + "_" + post.getUserID());
                                dbRef.updateChildren(childUpdate)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        pubMenu.clear();
                                                        PostdetailActivity.this.onCreateOptionsMenu(pubMenu);
                                                        plzw8Dialog.cancel();
                                                    }
                                                }).addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                plzw8Dialog.cancel();
                                                Toast.makeText(getApplicationContext(),
                                                        e.getMessage(), Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        });
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

    @Override
    public void onStop() {
        super.onStop();
        this.unregisterReceiver(broadcastReceiver);
    }

    private void saveCommentValue() {
        String key = dbRef.child(getString(R.string.comments_CODE)).push().getKey();
        User user = LoginSession.getInstance().getUser();
        comment = new Comment(comtContent, user.getUn(), user.getAvatar(), user.getuID(), postID);
        comment.setCommentID(key);
        Map<String, Object> comtValue = comment.toMap();
        Map<String, Object> childUpdate = new HashMap<>();
        childUpdate.put(getString(R.string.posts_CODE) + postID + "/"
                + getString(R.string.comments_CODE) + key, comtValue);
        //update notification
        childUpdate=updateCommentNotification(childUpdate,user.getuID());

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
    public Map<String,Object> updateCommentNotification(Map<String,Object> childUpdate,String user){
        boolean isExist=false;
        for(String mUSer:post.getUserComment()){
            if(mUSer.toLowerCase().equals(user.toLowerCase())){
                isExist=true;
            }else{
                UserNotification userNotification=new UserNotification();
                userNotification.setUserEffectId(user);
                userNotification.setType(3);
                Map<String,Object> userNotificationMap=userNotification.toMap();
                String key =dbRef.child(getString(R.string.user_notification_CODE)+mUSer).push().getKey();
                childUpdate.put(getString(R.string.user_notification_CODE)+mUSer+"/"+key,userNotificationMap);
            }
        }
        //if user not exist in user commented list
        if(!isExist){
            if(!user.toLowerCase().equals(post.getUserID().toLowerCase())) {
                post.addUsertoList(user);
                Map<String, Object> updatePost = post.toMap();
                childUpdate.put((getString(R.string.posts_CODE) + post.getPostID()), updatePost);
//                UserNotification userNotification=new UserNotification();
//                userNotification.setUserEffectId(user);
//                userNotification.setType(3);
//                Map<String,Object> userNotificationMap=userNotification.toMap();
//                String key =dbRef.child(getString(R.string.user_notification_CODE)+post.getUserID()).push().getKey();
//                childUpdate.put(getString(R.string.user_notification_CODE)+post.getUserID()+"/"+key,userNotificationMap);
            }
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
                pubMenu.clear();
                PostdetailActivity.this.onCreateOptionsMenu(pubMenu);
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
//                                        for (Comment item : comments) {
//                                            if (item.getCommentID().equals(comtID)) {
//                                                comments.remove(comments.indexOf(item));
//                                                comtAdapter.notifyDataSetChanged();
//                                            }
//                                        }
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
