package com.app.ptt.comnha.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Activity.AdapterActivity;
import com.app.ptt.comnha.Interfaces.DoInBackGroundOK;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Image;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.DoPost;
import com.app.ptt.comnha.SingletonClasses.EditPost;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditPostDialogFragment extends DialogFragment
        implements View.OnClickListener, DiscreteSeekBar.OnProgressChangeListener,AdapterView.OnItemSelectedListener, DoInBackGroundOK
{
    private static final String LOG=WritepostFragment.class.getSimpleName();
    Button btn_save,btn_mainImg,btnAddImg,btnDelImg;
    ChildEventListener commentChildEventListener, albumChildEventListener;
    ValueEventListener postValueEventListener, locaValueEventListener;
    ArrayList<Image> albumList;
    Food postFood;
    boolean newFood=false;
    CheckBox cb_monAn, cb_quanAn;
    boolean checloca = false;
    boolean checkrate = false;
    RatingBar rb_danhGiaMon;
    Food mFood;
    float mRating;
    ArrayList<String> ListID;
    String key;
    ArrayList<Uri> uris;
    String locaID;
    DoInBackGroundOK doInBackGroundOK;
    String fileKey;
    Store updateLoca, rootLocation;
    File anh_dai_dien;
    UploadTask uploadTask = null;
    ArrayList<File> myFile;
    StorageReference storeRef;
    LinearLayout ll_danhGiaQuan;
    DiscreteSeekBar mSeekBarGia, mSeekBarVS, mSeekBarPV;
    TextView txt_gia, txt_vs, txt_pv;
    Long gia = (long) 1, vs = (long) 1, pv = (long) 1;
    ImageView img, img_Daidien;
    Post newPost,oldPost;
    Map<String, Object> postValue;
    ArrayList<String> url;
    ArrayList<Image> images;
    Image newImage;
    Map<String, Object> childUpdates;
    int pc_Success=0;
    TextView txt_name, txt_address,frg_filter_txtmon;
    EditText edt_title, edt_content;
    boolean mainImg=false;
    ProgressDialog mProgressDialog;
    FragmentManager fm;
    boolean dellAll;
    public EditPostDialogFragment() {
        // Required empty public constructor
    }

    DatabaseReference dbRef;
    boolean isConnected=true;
    IntentFilter mIntentFilter;
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
            }
            if(intent.getIntExtra("stt",0)==-1){

                upload();
            }
            if(intent.getStringExtra("uriImg")!=null){
                url.add(intent.getStringExtra("uriImg"));
                int pos=intent.getIntExtra("pos",0);
                Log.i("POSSTTTTTTTTTTTTT",pos+"-----");
//                images.get(pos).setImage(intent.getStringExtra("uriImg"));
                Map<String, Object> image= images.get(pos).toMap();
                childUpdates.put(getResources().getString(R.string.images_CODE)
                        +ListID.get(pos), image);
                if (pos== (myFile.size()-1)) {
                    pc_Success++;
                    if(mainImg &&anh_dai_dien!=null) {
//                        newPost.setHinh(intent.getStringExtra("uriImg"));
                    }
                    postValue = newPost.toMap();
                    childUpdates.put(
                            getResources().getString(R.string.posts_CODE) + key, postValue);
                    pc_Success++;
                    Log.i("SSSSSSS","pc_Success_4="+pc_Success);
                    if(pc_Success==4)
                    {

                    }
                       upload();

                }
            }else{
                pc_Success=0;
                mProgressDialog.dismiss();
                Toast.makeText(getActivity(), "Xảy ra lỗi. Vui lòng thử lại"  , Toast.LENGTH_SHORT).show();

            }
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(broadcastReceiver);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        isConnected= MyService.returnIsNetworkConnected();
        View view = inflater.inflate(R.layout.fragment_edit_post_dialog, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        storeRef = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebaseStorage_path));
        ref(view);
        setData();

        Firebase.setAndroidContext(getActivity());
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(getResources().getString(R.string.txt_report));
    }
    void ref(View view) {
        fm = getActivity().getSupportFragmentManager();
        frg_filter_txtmon=(TextView) view.findViewById(R.id.frg_filter_txtmon);

        frg_filter_txtmon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PickFoodDialogFragment pickFoodFrg = new PickFoodDialogFragment();
//                pickFoodFrg.show(fm, "fragment_pickFood");
//                pickFoodFrg.setLocaID(oldPost.getLocaID());
//                pickFoodFrg.setOnPickFoodListener(new PickFoodDialogFragment.OnPickFoodListener() {
//                    @Override
//                    public void onPickFood(Food food) {
//                        frg_filter_txtmon.setText(food.getName());
//                        mFood = food;
//                        newFood=true;
//                        rb_danhGiaMon.setRating(food.getRating());
//                    }
//                });
            }
        });
        rb_danhGiaMon=(RatingBar) view.findViewById(R.id.item_rcyler_food_rb_rating);
        rb_danhGiaMon.setNumStars(3);
        rb_danhGiaMon.setStepSize(1);
        ll_danhGiaQuan=(LinearLayout) view.findViewById(R.id.ll_danhGiaQuan);
        ll_danhGiaQuan.setVisibility(View.INVISIBLE);
        img_Daidien=(ImageView) view.findViewById(R.id.img_daiDien);
        img = (ImageView) view.findViewById(R.id.frg_post_img);
        btn_mainImg=(Button) view.findViewById(R.id.btn_chooseMainImg);
        btnAddImg=(Button) view.findViewById(R.id.btn_addPhoto);
        btnDelImg=(Button) view.findViewById(R.id.btn_delPhoto);
        cb_monAn=(CheckBox) view.findViewById(R.id.cb_danhGiaMon);
        cb_quanAn=(CheckBox) view.findViewById(R.id.cb_danhGiaQuan);
        txt_name = (TextView) view.findViewById(R.id.frg_post_name);
        txt_address = (TextView) view.findViewById(R.id.frg_post_address);
        btn_save = (Button) view.findViewById(R.id.btn_save);
        txt_gia = (TextView) view.findViewById(R.id.frg_vote_txt_gia);
        txt_vs = (TextView) view.findViewById(R.id.frg_vote_txt_vs);
        txt_pv = (TextView) view.findViewById(R.id.frg_vote_txt_pv);
        mSeekBarGia = (DiscreteSeekBar) view.findViewById(R.id.frg_vote_slide_gia);
        mSeekBarVS = (DiscreteSeekBar) view.findViewById(R.id.frg_vote_slide_vesinh);
        mSeekBarPV = (DiscreteSeekBar) view.findViewById(R.id.frg_vote_slide_phucvu);
        txt_pv.setText(getResources().getString(R.string.text_servicerate) + ": " + mSeekBarPV.getMin());
        txt_vs.setText(getResources().getString(R.string.text_healthyrate) + ": " + mSeekBarVS.getMin());
        txt_gia.setText(getResources().getString(R.string.text_pricerate) + ": " + mSeekBarGia.getMin());
        edt_title = (EditText) view.findViewById(R.id.edt_title);
        edt_content = (EditText) view.findViewById(R.id.edt_content);
        btn_save.setOnClickListener(this);
        mSeekBarGia.setOnProgressChangeListener(this);
        mSeekBarPV.setOnProgressChangeListener(this);
        mSeekBarVS.setOnProgressChangeListener(this);
        cb_monAn.setChecked(false);
        cb_quanAn.setChecked(false);
        cb_quanAn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ll_danhGiaQuan.setVisibility(View.VISIBLE);

                }else
                    ll_danhGiaQuan.setVisibility(View.INVISIBLE);
                txt_vs.setText("Vệ sinh");
                txt_gia.setText("Giá");
                txt_pv.setText("Phục vụ");
                gia = (long) 1;
                vs = (long) 1;
                pv = (long) 1;
//                mSeekBarGia.setProgress((int) oldPost.getPrice());
//                mSeekBarPV.setProgress((int) oldPost.getPhucvu());
//                mSeekBarVS.setProgress((int) oldPost.getVesinh());
            }
        });
        frg_filter_txtmon.setVisibility(View.INVISIBLE);
        rb_danhGiaMon.setVisibility(View.INVISIBLE);
        cb_monAn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    frg_filter_txtmon.setVisibility(View.VISIBLE);
                    rb_danhGiaMon.setVisibility(View.VISIBLE);
                }else {
                    frg_filter_txtmon.setVisibility(View.INVISIBLE);
                    rb_danhGiaMon.setVisibility(View.INVISIBLE);
                }
                frg_filter_txtmon.setText("Chọn món");
//                if(oldPost.getType()==1)
//                    rb_danhGiaMon.setRating(oldPost.getFood().getRating());
            }
        });
        btnAddImg.setOnClickListener(this);
        btn_mainImg.setOnClickListener(this);
        rb_danhGiaMon.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if (rating == 1 && cb_monAn.isChecked())
                    Toast.makeText(getContext(), "Dở tệ", Toast.LENGTH_SHORT).show();
                if (rating == 2  && cb_monAn.isChecked())
                    Toast.makeText(getContext(), "Bình thường", Toast.LENGTH_SHORT).show();
                if (rating == 3 && cb_monAn.isChecked())
                    Toast.makeText(getContext(), "Ngon tuyệt", Toast.LENGTH_SHORT).show();
                mRating = rating;
            }
        });
        btnDelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Thông báo");
                alertDialog.setMessage("Bạn muốn xóa các hình đã đăng?");
                alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dellAll=true;
                        dialog.dismiss();
                    }
                });
                alertDialog.setNegativeButton("Trở về", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });

    }
    public void setData() {
        oldPost = EditPost.getInstance().getPost();
        albumList = EditPost.getInstance().getAlbumList();
//        txt_address.setText(oldPost.getDiachi());
//        txt_name.setText(oldPost.getLocaName());
//        Picasso.with(getContext()).load(oldPost.getHinh()).into(img_Daidien);
//        if (oldPost.getType() == 1) {
//            cb_monAn.setChecked(true);
//            rb_danhGiaMon.setRating(oldPost.getFood().getRating());
//            frg_filter_txtmon.setVisibility(View.VISIBLE);
//            frg_filter_txtmon.setText(oldPost.getFood().getName());
//            rb_danhGiaMon.setVisibility(View.VISIBLE);
//            mRating = oldPost.getFood().getRating();
//            mFood = oldPost.getFood();
//        } else {
//            cb_monAn.setChecked(false);
//            frg_filter_txtmon.setVisibility(View.INVISIBLE);
//            rb_danhGiaMon.setVisibility(View.INVISIBLE);
//        }
//        if(oldPost.getVesinh()!=0&&oldPost.getPrice()!=0&&oldPost.getPhucvu()!=0) {
//            ll_danhGiaQuan.setVisibility(View.VISIBLE);
//            cb_quanAn.setChecked(true);
//            txt_vs.setText("Vệ sinh:" +oldPost.getVesinh());
//            txt_price.setText("Giá:"+ oldPost.getPrice());
//            txt_pv.setText("Phục vụ:"+ oldPost.getPhucvu());
//            gia = oldPost.getPrice();
//            vs = oldPost.getVesinh();
//            pv = oldPost.getPhucvu();
//            mSeekBarGia.setProgress((int) oldPost.getPrice());
//            mSeekBarPV.setProgress((int) oldPost.getPhucvu());
//            mSeekBarVS.setProgress((int) oldPost.getVesinh());
//        }
//        edt_title.setText(oldPost.getTitle());
//        edt_content.setText(oldPost.getContent());


//        dbRef.child(getResources().getString(R.string.locations_CODE)).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
////                if(dataSnapshot.getKey().equals(oldPost.getLocaID())) {
////                    updateLoca = dataSnapshot.getValue(Store.class);
////                    updateLoca.setLocaID(dataSnapshot.getKey());
////
////                    rootLocation = updateLoca;
////                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    class ParseImg extends AsyncTask<Void,Void,Bitmap>{
        File file;
        DoInBackGroundOK mdoInBackGroundOK;
        public ParseImg(File file, DoInBackGroundOK doInBackGroundOK){
            this.file=file;
            this.mdoInBackGroundOK=doInBackGroundOK;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            FileInputStream fis = null;
            try {
                File img=new File(file.toString());
                fis = new FileInputStream(img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
            Bitmap bm = BitmapFactory.decodeStream(fis);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100 , baos);
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mdoInBackGroundOK.DoInBackGroundImg(bitmap);

        }
    }
    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        Log.i(LOG + ".onResume", "onResume");
        if (MyService.getActionAddPost() == 1) {
            if(MyService.getStatusChooseImg()) {
                if(DoPost.getInstance().getFiles()!=null) {
                    if (DoPost.getInstance().getFiles().size() > 0) {
                        anh_dai_dien = DoPost.getInstance().getFiles().get(0);
                        ParseImg parseImg = new ParseImg(DoPost.getInstance().getFiles().get(0), this);
                        parseImg.execute();
                    }
                }
            }else{
                Log.i(LOG + ".ACTION=1", "Không lấy được hình");
            }
        }
        if(MyService.getActionAddPost()==2){
            if(DoPost.getInstance().getFiles()!=null)
                btnAddImg.setText("Số hình đã thêm: "+DoPost.getInstance().getFiles().size());
        }
//        Toast.makeText(getActivity().getApplicationContext(), "resume post Frag with key: " + locaID, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.frg_post_fabchoseloca:
//                if(isConnected) {
//                    Intent intent = new Intent(getActivity(), Adapter2Activity.class);
//                    intent.putExtra(getString(R.string.fragment_CODE), getString(R.string.frag_chooseloca_CODE));
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.frg_post_fabchoseimg:
//                Intent intent1 = new Intent(getActivity().getApplicationContext(), Adapter2Activity.class);
//                intent1.putExtra(getResources().getString(R.string.fragment_CODE),
//                        getResources().getString(R.string.frag_chooseimg_CODE));
//                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent1);
//                break;
//            case R.id.frg_post_fabrate:
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                DoVoteFragment dovoteFragment = DoVoteFragment.newIntance(getResources().getString(R.string.text_vote));
//                dovoteFragment.show(fm, "fragment_dovote");
//                break;
            case R.id.btn_save:
                if (isConnected) {
                    savePost(view);
                } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_chooseMainImg:
                if (isConnected) {
                    MyService.setActionAddPost(1);
                    Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(getActivity().getApplicationContext(), AdapterActivity.class);
                    intent1.putExtra(getResources().getString(R.string.fragment_CODE),
                            getResources().getString(R.string.frag_chooseimg_CODE));

                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);

                } else {
                    MyService.setActionAddPost(-1);
                    Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_addPhoto:
                if (isConnected) {
                    MyService.setActionAddPost(2);
                    Intent intent1 = new Intent(getActivity().getApplicationContext(), AdapterActivity.class);
                    intent1.putExtra(getResources().getString(R.string.fragment_CODE),
                            getResources().getString(R.string.frag_chooseimg_CODE));
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);

                } else {
                    MyService.setActionAddPost(-1);
                    Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        isConnected = MyService.returnIsNetworkConnected();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
        getContext().registerReceiver(broadcastReceiver, mIntentFilter);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
    private void savePost(View view) {

        try {
            if (gia < 1
                    && vs < 1
                    && pv < 1) {
                checkrate = true;
            }
        } catch (NullPointerException mess) {
            checkrate = true;
        }
        if (edt_title.getText().toString().trim().equals("")) {
            Snackbar.make(view, getResources().getString(R.string.txt_notitle), Snackbar.LENGTH_SHORT).show();
        } else if (edt_content.getText().toString().trim().equals("")) {
            Snackbar.make(view, getResources().getString(R.string.txt_nocontent), Snackbar.LENGTH_SHORT).show();

        }
// else if (cb_monAn.isChecked()&&(mFood.getLocaID()==null)) {
//            Snackbar.make(view, "Chưa chọn món hoặc chưa đánh giá món", Snackbar.LENGTH_SHORT).show();
//
//        }

        else {
            dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
//            key = oldPost.getPostID();
//            locaID = oldPost.getLocaID();
//            updateLoca.setLocaID(null);
            mProgressDialog = ProgressDialog.show(getActivity(),
                    getResources().getString(R.string.txt_plzwait),
                    getResources().getString(R.string.txt_addinpost),
                    true, false);
            pc_Success=0;
            if(MyService.getUserAccount()!=null)
                addpost(key, locaID, updateLoca);
        }
    }

    public byte[] ImageView_byte(ImageView v) {
        BitmapDrawable drawable = (BitmapDrawable) v.getDrawable();
        Bitmap bm = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
    private void addpost(String key, String locaID, Store updateLoca) {
        url=new ArrayList<>();
        images=new ArrayList<>();
        uris=new ArrayList<>();
        childUpdates = new HashMap<String, Object>();
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        newPost = oldPost;
//        Toast.makeText(getContext(),"POS TYPE="+oldPost.getType()+"---"+cb_monAn.isChecked(), Toast.LENGTH_SHORT).show();
//        if(cb_monAn.isChecked()&&oldPost.getType()==2){
//            Log.d("Chưa có món","");
//            Food food=mFood;
//            food.setRating(mRating);
//            float d = (int) ((mFood.getRating() + mRating) / 2);
//            newPost.setType(1);
//            newPost.setFood(food);
//            Log.d("Đã thêm .có món"+ food.getName()+"-d:"+d,"");
//            food.setRating(d);
//            Map<String, Object> updateFood = food.toMap();
//            childUpdates.put(
//                    getResources().getString(R.string.thucdon_CODE)
//                            + food.getFoodID(), updateFood);
//            pc_Success++;
//            Toast.makeText(getContext(),"Đã thêm .có món"+ food.getName()+"-d:"+d, Toast.LENGTH_SHORT).show();
//        }else{
//            if(cb_monAn.isChecked()&&oldPost.getType()==1){
                //Toast.makeText(getContext(),mFood.getFoodID()+"-"+oldPost.getFood().getFoodID(), Toast.LENGTH_SHORT).show();
//                Log.i("Đã có món","");
//                if(newFood){
//                    if(mFood.getFoodID().equals(oldPost.getFood().getFoodID())){
//                        Log.i("Món cũ","");
//                        if(mRating==oldPost.getFood().getRating()){
//                            Log.i("Đánh giá cũ","");
//                        }else{
//                            float a=oldPost.getFood().getRating();
//                            float b= (2 *mFood.getRating()-a);
//                            float d =((b + mRating) / 2);
//                            mFood.setRating(d);
//                            Map<String, Object> updateFood = mFood.toMap();
//                            childUpdates.put(
//                                    getResources().getString(R.string.thucdon_CODE)
//                                            + mFood.getFoodID(), updateFood);
//                            Log.i("Đánh giá mới","a="+a+"-b="+b+"-d="+d);
//                        }
//                    }else{
//                        float a=oldPost.getFood().getRating();
//                        float b= (2 *EditPost.getInstance().getDanhgia()-a);
//                        Log.i("Hủy đánh giá cũ a="+a+"-"+EditPost.getInstance().getDanhgia(),"Đánh giá mới b="+b);
//                        Food oldFood=oldPost.getFood();
//                        oldFood.setRating(b);
//                        Map<String, Object> updateFood = oldFood.toMap();
//                        childUpdates.put(
//                                getResources().getString(R.string.thucdon_CODE)
//                                        + oldFood.getFoodID(), updateFood);
//                        Toast.makeText(getContext(),"Update mon cu:"+oldFood.getName()+"-"+oldFood.getRating(), Toast.LENGTH_SHORT).show();
//                        Food newFood =mFood;
//
//                        Log.i("Món mới:"+newFood.getName(),"Đánh giá mới:"+mRating);
//                        float d =((mFood.getRating() + mRating) / 2);
//                        newFood.setRating(d);
//                        Map<String, Object> updateFood1 = newFood.toMap();
//                        childUpdates.put(
//                                getResources().getString(R.string.thucdon_CODE)
//                                        + newFood.getFoodID(), updateFood1);
//                        Toast.makeText(getContext(),"Update mon moi:"+newFood.getName()+"-"+newFood.getRating(), Toast.LENGTH_SHORT).show();
//
//                        Log.i("","Lưu món mới và đánh giá vào");
//                        newFood.setRating(mRating);
//                        newPost.setFood(newFood);
//                        Log.i("","Lưu món vào newPost");
//                        Toast.makeText(getContext(),"Update bai post:"+newPost.getFood().getName()+"-"+newPost.getFood().getRating(), Toast.LENGTH_SHORT).show();

//                    }
//                }else{
//                        if(mRating==oldPost.getFood().getRating()){
//                            Log.i("Đánh giá cũ","");
//                        }else{
//                            float a=oldPost.getFood().getRating();
//                            Food temp=mFood;
//                            temp.setRating(mRating);
//                            float b= (2 *mFood.getRating()-a);
//                            float d =((b + mRating) / 2);
//                            newPost.setFood(temp);
//                            mFood.setRating(d);
//                            Map<String, Object> updateFood = mFood.toMap();
//                            childUpdates.put(
//                                    getResources().getString(R.string.thucdon_CODE)
//                                            + mFood.getFoodID(), updateFood);
//
//                            Log.i("Đánh giá mới1111","a="+a+"-b="+b+"-d="+d);
//                        }
//                }
//                pc_Success++;
//            }else{
//                newPost.setType(2);
//                pc_Success++;
//            }
        }


//        if (cb_monAn.isChecked()) {
//                float b =EditPost.getInstance().getDanhgia();
//                Food food=mFood;
//                food.setRating(mRating);
//
//            Toast.makeText(getContext(), "old:"+oldPost.getType()+"---- new:"+newPost.getType(), Toast.LENGTH_SHORT).show();
//                if(oldPost.getType()==1){
//                    Log.i("B1","oldpost_type=1");
//                    float a = EditPost.getInstance().getDanhgia();
//                    b= (2 * a - EditPost.getInstance().getPost().getFood().getRating());
//                    Log.i("B2","a="+a+"-b="+b);
//                }
//                if (mFood != null) {
//                    float c = b;
//                    float d = (int) ((c + mRating) / 2);
//                    mFood.setRating(d);
//                    childUpdates.put(
//                            getResources().getString(R.string.thucdon_CODE)
//                                    + mFood.getFoodID(), mFood);
//
//                    Log.i("SSSSSSS","pc_Success1="+pc_Success);
//                }
//                newPost.setType(1);
//                pc_Success++;
//                newPost.setFood(food);
//                Log.i("SSSSSSS","pc_Success1.1="+pc_Success);
//        } else {
//            newPost.setType(2);
//            pc_Success++;
//            Log.i("SSSSSSS","pc_Success1_Not choose="+pc_Success);
//        }
//        if (!checkrate && cb_quanAn.isChecked()) {
//            newPost.setPrice(gia);
//            newPost.setVesinh(vs);
//            newPost.setPhucvu(pv);
//            long giaTong = updateLoca.getGiaTong() + gia,
//                    vsTong = updateLoca.getVsTong() + vs,
//                    pvTong = updateLoca.getPvTong() +pv,
//                    size = updateLoca.getSize() + 1;
//            updateLoca.setGiaTong(giaTong);
//            updateLoca.setVsTong(vsTong);
//            updateLoca.setPvTong(pvTong);
//            updateLoca.setSize(size);
//            updateLoca.setGiaAVG(giaTong / size);
//            updateLoca.setVsAVG(vsTong / size);
//            updateLoca.setPvAVG(pvTong / size);
//            updateLoca.setTongAVG((giaTong + vsTong + pvTong) / (size * 3));

                Map<String, Object> updateLocal = updateLoca.toMap();
//                childUpdates.put(
//                        getResources().getString(R.string.locations_CODE)
//                                + locaID, updateLocal);
//            pc_Success++;
//            Log.i("SSSSSSS","pc_Success_2="+pc_Success);
//        } else {
//            pc_Success++;
//            Log.i("SSSSSSS", "pc_Success_2Not choose=" + pc_Success);
//        }
//        Log.i("ZOOOOOOO AddPost", "");
//        if(!edt_title.getText().toString().equals(oldPost.getTitle()))
//        {
//            newPost.setTitle(edt_title.getText().toString());
//        }
//        if(!edt_content.getText().toString().equals(oldPost.getContent())){
//            newPost.setContent(edt_content.getText().toString());
//        }
//        newPost.setDate(new Times().getTime());
//        newPost.setTime(new Times().getDate());
//        newPost.setVisible(true);
//        myFile=new ArrayList<>();
//        if(dellAll){
//            for (Image img : albumList) {
//                dbRef.child(getResources().getString(R.string.images_CODE) + img.getImageID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//            }
//            for(Image img:albumList){
//                Log.i("OK", "img:"+img.getName());
//                    StorageReference myChildRef = stRef.child(
//                            getResources().getString(R.string.images_CODE)
//                                    + img.getName());
//                    Log.i("OK", "url:"+  getResources().getString(R.string.images_CODE)
//                            + img.getName());
//                    myChildRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    });
//            }
//            newPost.setHinh(null);
//        }
//        if (MyService.getStatusChooseImg() && MyService.getActionAddPost() == 2) {
//            Log.i("SSSSSSS","myFile="+myFile.size());
//            myFile = DoPost.getInstance().getFiles();
//            if (anh_dai_dien != null && mainImg) {
//                myFile.add(anh_dai_dien);
//                Log.i("SSSSSSS", "myFile + 1=" + myFile.size());
//            }
//            new uploadImg().execute();
//        }else
//        if( MyService.getActionAddPost() == 1 &&MyService.getStatusChooseImg()){
//            Log.i("SSSSSSS","myFile="+myFile.size());
//            myFile = DoPost.getInstance().getFiles();
//            new uploadImg().execute();
//        }
//        else{
//            pc_Success++;
//            postValue = newPost.toMap();
//            childUpdates.put(
//                    getResources().getString(R.string.posts_CODE) + key, postValue);
//            pc_Success++;
//            Log.i("SSSSSSS","pc_Success_3_Not choose");
//            Log.i("SSSSSSS","pc_Success_4="+pc_Success);
//
//            if(pc_Success==4)
//                upload();
//        }
//


//    }
    public void upload(){
        dbRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), "Sửa bị lỗi" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
//                    if(!MyService.getUserAccount().getRole()) {
//                        Notification notification = new Notification();
//                        String key1 = dbRef.child(getResources().getString(R.string.notification_CODE) + "admin").push().getKey();
//                        notification.setAccount(MyService.getUserAccount());
//                        notification.setDate(new Times().getDate());
//                        notification.setTime(new Times().getTime());
//                        notification.setType(7);
////                        newPost.setPostID(key);
//                        notification.setPost(newPost);
//                        notification.setStore(updateLoca);
//                        notification.setReaded(false);
//                        notification.setTo("admin");
//                        Map<String, Object> notificationValue = notification.toMap();
//                        childUpdates = new HashMap<>();
//                        childUpdates.put(getResources().getString(R.string.notification_CODE) + "admin/" + key1, notificationValue);
//                        dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isComplete()) {
//                                    plzw8Dialog.dismiss();
//                                    Toast.makeText(getActivity(), "Sửa thành công", Toast.LENGTH_SHORT).show();
//                                    getActivity().finish();
//                                } else {
//                                    plzw8Dialog.dismiss();
//                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    }else{
//                        plzw8Dialog.dismiss();
//                        Toast.makeText(getActivity(), "Sửa thành công", Toast.LENGTH_SHORT).show();
//                        getActivity().finish();
//                    }
                }
            }
        });
        pc_Success=0;
    }
    class uploadImg extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            if(myFile!=null) {
                storeRef = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebaseStorage_path));
                ListID = new ArrayList<>();
                Log.i("uploadImg", "myFile=" + myFile.size());
                for (File f : myFile) {
                    Uri uri = Uri.fromFile(f);
                    fileKey = dbRef.child(getResources().getString(R.string.images_CODE1)).push().getKey();
                    ListID.add(fileKey);
                    newImage = new Image();
//                    newImage.setName(uri.getLastPathSegment());
//                    newImage.setPostID(key);
//                    newImage.setUserID(MyService.getUserAccount().getId());
//                    newImage.setLocaID(locaID);
                    images.add(newImage);
                    Log.i("SSSSSSS", "images=" + images.size());
                    StorageReference myChildRef = storeRef.child(
                            getResources().getString(R.string.images_CODE)
                                    + uri.getLastPathSegment());
                    uploadTask = myChildRef.putFile(uri);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Uri uri = taskSnapshot.getDownloadUrl();
//                            uris.add(uri);
                            if (uris.size() == images.size())
                                MyService.setUriImg(uris);
//                            Log.i("ZOOOOOOO UploadImage", "isSuccess = true;" + uri.toString());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.i("ZOOOOOOO UploadImage", "isSuccess = false;");
                                                }
                                            }
                    );
                }
            }else{
                MyService.setUriImg(null);
            }

            return null;
        }
    }

    @Override
    public void DoInBackGroundStart() {

    }

    @Override
    public void DoInBackGroundOK(Boolean isSuccess, int type) {
    }

    @Override
    public void DoInBackGroundImg(Bitmap bitmap) {
        try {
            img_Daidien.setImageBitmap(bitmap);
            mainImg=true;
            Log.i(LOG + ".ACTION=1", "set Anh dai dien: OK");
        } catch (Exception e) {
            Log.i(LOG + ".ACTION=1", "set Anh dai dien: FAIL");
        }
    }

    @Override
    public void DoInBackGroundLocation(Store location) {

    }

    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.frg_vote_slide_gia:
                txt_gia.setText(getResources().getString(R.string.text_pricerate) + ": " + String.valueOf(value));
                try {
                    gia = (long) seekBar.getProgress();

                } catch (NullPointerException mess) {
                    gia = (long) 1;
                }
                break;
            case R.id.frg_vote_slide_phucvu:
                txt_pv.setText(getResources().getString(R.string.text_servicerate) + ": " + String.valueOf(value));
                try {
                    pv = (long) seekBar.getProgress();
                } catch (NullPointerException mess) {
                    pv = (long) 1;
                }
                break;
            case R.id.frg_vote_slide_vesinh:
                txt_vs.setText(getResources().getString(R.string.text_healthyrate) + ": " + String.valueOf(value));
                try {
                    vs = (long) seekBar.getProgress();
                } catch (NullPointerException mess) {
                    vs = (long) 1;
                }
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

