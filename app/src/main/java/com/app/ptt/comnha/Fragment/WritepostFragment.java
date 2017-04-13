package com.app.ptt.comnha.Fragment;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.ptt.comnha.Adapters.ImagesImportRvAdapter;
import com.app.ptt.comnha.Classes.SelectedImage;
import com.app.ptt.comnha.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class WritepostFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    public WritepostFragment() {
        // Required empty public constructor
    }

    Toolbar toolbar;
    EditText edt_content, edt_title;
    LinearLayout linear_more, linear_rate, linear_pickfood, linear_addimg, linear_pickloca,
            linear_location, linear_importimg;
    ImageView imgV_banner;
    BottomSheetDialog moreDialog, rateDialog, imgsDialog;
    DatabaseReference dbRef;
    StorageReference storeRef;
    ProgressDialog mProgressDialog;
    TextView txtV_gia, txtV_vs, txtV_pv;
    SeekBar sb_gia, sb_vs, sb_pv;
    int progress_gia = 0, progress_vs = 0, progress_pv = 0;
    public static int MEDIASTORE_LOADED_ID = 0;
    RecyclerView imagesrv;
    RecyclerView.LayoutManager imageslm;
    RecyclerView.Adapter imagesAdapter;
    ContentResolver cr;
    ArrayList<SelectedImage> selectedImages;
    ImagesImportRvAdapter imagesImportRvAdapter;
    TextView txtv_locaname, txtv_locaadd, txtv_banner, txtv_importimg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        isConnected = MyService.returnIsConnected();
        View view = inflater.inflate(R.layout.fragment_writepost, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_path));
        storeRef = FirebaseStorage.getInstance().getReferenceFromUrl(getString(R.string.firebaseStorage_path));
        cr = getContext().getContentResolver();
        anhXa(view);
        return view;
    }

    private void anhXa(View view) {
        linear_more = (LinearLayout) view.findViewById(R.id.linear_more_writepost);
        edt_content = (EditText) view.findViewById(R.id.edt_content_writepost);
        edt_title = (EditText) view.findViewById(R.id.edt_title_writepost);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_writepost);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        getActivity().setTitle(getString(R.string.text_toolbar_title_writepost));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        linear_more.setOnClickListener(this);
        setHasOptionsMenu(true);
        imgV_banner = (ImageView) view.findViewById(R.id.imgV_banner_writepost);
        moreDialog = new BottomSheetDialog(getContext());
        moreDialog.setContentView(R.layout.layout_writepost_more);

        linear_rate = (LinearLayout) moreDialog.findViewById(R.id.linear_rate_more_writepost_dialog);
        linear_pickfood = (LinearLayout) moreDialog.findViewById(R.id.linear_foodrating_more_writepost_dialog);
        linear_addimg = (LinearLayout) moreDialog.findViewById(R.id.linear_addimg_more_writepost_dialog);
        linear_pickloca = (LinearLayout) moreDialog.findViewById(R.id.linear_place_more_writepost_dialog);

        linear_rate.setOnClickListener(this);
        linear_pickfood.setOnClickListener(this);
        linear_addimg.setOnClickListener(this);
        linear_pickloca.setOnClickListener(this);
        mProgressDialog = new ProgressDialog(getActivity().getApplicationContext());
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage(getString(R.string.txt_posting_plzwait));

        rateDialog = new BottomSheetDialog(getContext());
        rateDialog.setContentView(R.layout.layout_writepost_rate);
        txtV_gia = (TextView) rateDialog.findViewById(R.id.txtV_gia_ratedialog_writepost);
        txtV_vs = (TextView) rateDialog.findViewById(R.id.txtV_vesinh_ratedialog_writepost);
        txtV_pv = (TextView) rateDialog.findViewById(R.id.txtV_phucvu_ratedialog_writepost);
        txtV_gia.setText(getString(R.string.text_gia) + ": 1");
        txtV_vs.setText(getString(R.string.text_ratevs) + ": 1");
        txtV_pv.setText(getString(R.string.text_ratepv) + ": 1");
        sb_gia = (SeekBar) rateDialog.findViewById(R.id.seekbar_gia_ratedialog_writepost);
        sb_vs = (SeekBar) rateDialog.findViewById(R.id.seekbar_vesinh_ratedialog_writepost);
        sb_pv = (SeekBar) rateDialog.findViewById(R.id.seekbar_phucvu_ratedialog_writepost);
        sb_gia.setOnSeekBarChangeListener(this);
        sb_vs.setOnSeekBarChangeListener(this);
        sb_pv.setOnSeekBarChangeListener(this);

        linear_location = (LinearLayout) view.findViewById(R.id.linear_location_writepost);
        linear_location.setVisibility(View.GONE);

        selectedImages = new ArrayList<>();
        imgsDialog = new BottomSheetDialog(getContext());
        imgsDialog.setContentView(R.layout.layout_writepost_imgimporting);
        imgsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Log.d("onDismiss", "onDismiss");
                selectedImages.clear();
                for (SelectedImage i : imagesImportRvAdapter.getSelectedImgs()
                        ) {
                    if (i.isSelected()) {
                        selectedImages.add(i);
                    }
                }
                if (selectedImages.size() > 0) {
                    linear_importimg.setVisibility(View.VISIBLE);
                    txtv_importimg.setText(selectedImages.size() + " ảnh đính kèm");
                } else {
                    linear_importimg.setVisibility(View.GONE);
                }
            }
        });

        imagesrv = (RecyclerView) imgsDialog.findViewById(R.id.rv_images_imgimporting);
        imageslm = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
        imagesrv.setLayoutManager(imageslm);
        imagesImportRvAdapter = new ImagesImportRvAdapter(getContext(), getContext().getContentResolver());
        imagesrv.setAdapter(imagesImportRvAdapter);

        txtv_locaname = (TextView) view.findViewById(R.id.txtV_locaname_writepost);
        txtv_locaadd = (TextView) view.findViewById(R.id.txtV_locaaddress_writepost);
        txtv_banner = (TextView) view.findViewById(R.id.txtV_banner_writepost);
        txtv_importimg = (TextView) view.findViewById(R.id.txtV_importimg_writepost);
        linear_importimg = (LinearLayout) view.findViewById(R.id.linear_importimg_writepost);
        linear_importimg.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_writepost, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_writepost:
                if (edt_content.getText().toString().isEmpty()) {

                } else if (edt_title.getText().toString().isEmpty()) {

                } else
                    return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_more_writepost:
                moreDialog.show();
                break;
            case R.id.linear_rate_more_writepost_dialog:
                moreDialog.dismiss();
                rateDialog.show();
                break;
            case R.id.linear_foodrating_more_writepost_dialog:
                moreDialog.dismiss();
                break;
            case R.id.linear_addimg_more_writepost_dialog:
                imagesrv.scrollToPosition(0);
                imgsDialog.show();
                moreDialog.dismiss();
                break;
            case R.id.linear_place_more_writepost_dialog:
                moreDialog.dismiss();
                break;

        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.seekbar_gia_ratedialog_writepost:
                progress_gia = i + 1;
                txtV_gia.setText(getString(R.string.text_gia) + ": " + progress_gia);
                break;
            case R.id.seekbar_vesinh_ratedialog_writepost:
                progress_vs = i + 1;
                txtV_vs.setText(getString(R.string.text_ratevs) + ": " + progress_vs);
                break;
            case R.id.seekbar_phucvu_ratedialog_writepost:
                progress_pv = i + 1;
                txtV_pv.setText(getString(R.string.text_ratepv) + ": " + progress_pv);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d("onStartTrackingTouch", seekBar.getProgress() + "");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    //    private static final String LOG = AddpostFragment.class.getSimpleName();
//    Button btn_save, btn_mainImg, btnAddImg;
//    CheckBox cb_monAn, cb_quanAn;
//    boolean checloca = false;
//    boolean checkrate = false;
//    RatingBar rb_danhGiaMon;
//    Food mFood = new Food();
//    float mRating = 0;
//    ArrayList<String> ListID;
//    String key;
//    ArrayList<Uri> uris;
//    String locaID;
//    DoInBackGroundOK doInBackGroundOK;
//    String fileKey;
//    MyLocation updateLoca;
//    File anh_dai_dien;
//    UploadTask uploadTask = null;
//    ArrayList<File> myFile;
//    StorageReference storeRef;
//    LinearLayout ll_danhGiaQuan;
//    DiscreteSeekBar mSeekBarGia, mSeekBarVS, mSeekBarPV;
//    TextView txt_gia, txt_vs, txt_pv;
//    Long gia = (long) 1, vs = (long) 1, pv = (long) 1;
//    ImageView img, img_Daidien;
//    Post newPost;
//    Map<String, Object> postValue;
//    ArrayList<String> url;
//    ArrayList<Image> images;
//    Image newImage;
//    Map<String, Object> childUpdates;
//    int pc_Success = 0;
//    TextView txt_name, txt_address, frg_filter_txtmon;
//    EditText edt_title, edt_content;
//    boolean mainImg = false;
//    ProgressDialog mProgressDialog;
//    FragmentManager fm;

//
//    DatabaseReference dbRef;
//    boolean isConnected = true;
//    IntentFilter mIntentFilter;
//    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
//    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(mBroadcastSendAddress)) {
//                Log.i(LOG + ".onReceive form Service", "isConnected= " + intent.getBooleanExtra("isConnected", false));
//                if (intent.getBooleanExtra("isConnected", false)) {
//                    isConnected = true;
//                } else
//                    isConnected = false;
//            }
//            if (intent.getIntExtra("stt", 0) == -1) {
//
//                upload();
//            }
//            if (intent.getStringExtra("uriImg") != null) {
//                url.add(intent.getStringExtra("uriImg"));
//                int pos = intent.getIntExtra("pos", 0);
//                Log.i("POSSTTTTTTTTTTTTT", pos + "-----");
//                images.get(pos).setImage(intent.getStringExtra("uriImg"));
//                Map<String, Object> image = images.get(pos).toMap();
//                childUpdates.put(getResources().getString(R.string.images_CODE)
//                        + ListID.get(pos), image);
//                if (pos == (myFile.size() - 1)) {
//                    pc_Success++;
//                    if (mainImg && anh_dai_dien != null) {
//                        newPost.setHinh(intent.getStringExtra("uriImg"));
//                    }
//                    postValue = newPost.toMap();
//                    childUpdates.put(
//                            getResources().getString(R.string.posts_CODE) + key, postValue);
//                    pc_Success++;
//                    Log.i("SSSSSSS", "pc_Success_4=" + pc_Success);
//                    if (pc_Success == 4)
//                        upload();
//
//                }
//            } else {
//                pc_Success = 0;
//                mProgressDialog.dismiss();
//                Toast.makeText(getActivity(), "Xảy ra lỗi. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
//
//            }
//        }
//    };
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        getContext().unregisterReceiver(broadcastReceiver);
//    }

//    void anhXa(View view) {
//        fm = getActivity().getSupportFragmentManager();
//        frg_filter_txtmon = (TextView) view.findViewById(frg_filter_txtmon);
//
//        frg_filter_txtmon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PickFoodDialogFragment pickFoodFrg = new PickFoodDialogFragment();
//                pickFoodFrg.show(fm, "fragment_pickFood");
//                Log.i("CCS", DoPost.getInstance().getMyLocation().getLocaID() + "-------------------------");
//                pickFoodFrg.setLocaID(DoPost.getInstance().getMyLocation().getLocaID());
//                pickFoodFrg.setOnPickFoodListener(new PickFoodDialogFragment.OnPickFoodListener() {
//                    @Override
//                    public void onPickFood(Food food) {
//                        frg_filter_txtmon.setText(food.getTenmon());
//                        mFood = food;
//                        rb_danhGiaMon.setRating(food.getDanhGia());
//                    }
//                });
//            }
//        });
//        rb_danhGiaMon = (RatingBar) view.findViewById(rb_danhGiaMon);
//        ll_danhGiaQuan = (LinearLayout) view.findViewById(ll_danhGiaQuan);
//        ll_danhGiaQuan.setVisibility(View.INVISIBLE);
//        img_Daidien = (ImageView) view.findViewById(R.id.img_daiDien);
//        img = (ImageView) view.findViewById(R.id.frg_post_img);
//        btn_mainImg = (Button) view.findViewById(R.id.btn_chooseMainImg);
//        btnAddImg = (Button) view.findViewById(R.id.btn_addPhoto);
//        cb_monAn = (CheckBox) view.findViewById(R.id.cb_danhGiaMon);
//        cb_quanAn = (CheckBox) view.findViewById(R.id.cb_danhGiaQuan);
//        txt_name = (TextView) view.findViewById(R.id.frg_post_name);
//        txt_address = (TextView) view.findViewById(R.id.frg_post_address);
//        btn_save = (Button) view.findViewById(R.id.btn_save);
//        txt_gia = (TextView) view.findViewById(R.id.frg_vote_txt_gia);
//        txt_vs = (TextView) view.findViewById(R.id.frg_vote_txt_vs);
//        txt_pv = (TextView) view.findViewById(R.id.frg_vote_txt_pv);
//        mSeekBarGia = (DiscreteSeekBar) view.findViewById(R.id.frg_vote_slide_gia);
//        mSeekBarVS = (DiscreteSeekBar) view.findViewById(R.id.frg_vote_slide_vesinh);
//        mSeekBarPV = (DiscreteSeekBar) view.findViewById(R.id.frg_vote_slide_phucvu);
//        txt_pv.setText(getResources().getString(R.string.text_ratepv) + ": " + mSeekBarPV.getMin());
//        txt_vs.setText(getResources().getString(R.string.text_ratevs) + ": " + mSeekBarVS.getMin());
//        txt_gia.setText(getResources().getString(R.string.text_rategia) + ": " + mSeekBarGia.getMin());
//        edt_title = (EditText) view.findViewById(R.id.edt_title);
//        edt_content = (EditText) view.findViewById(R.id.edt_content);
//        btn_save.setOnClickListener(this);
//        mSeekBarGia.setOnProgressChangeListener(this);
//        mSeekBarPV.setOnProgressChangeListener(this);
//        mSeekBarVS.setOnProgressChangeListener(this);
//        cb_monAn.setChecked(false);
//        cb_quanAn.setChecked(false);
//        cb_quanAn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    ll_danhGiaQuan.setVisibility(View.VISIBLE);
//
//                } else
//                    ll_danhGiaQuan.setVisibility(View.INVISIBLE);
//                txt_vs.setText("Vệ sinh");
//                txt_gia.setText("Giá");
//                txt_pv.setText("Phục vụ");
//                gia = (long) 1;
//                vs = (long) 1;
//                pv = (long) 1;
//                mSeekBarGia.setProgress(1);
//                mSeekBarPV.setProgress(1);
//                mSeekBarVS.setProgress(1);
//            }
//        });
//        frg_filter_txtmon.setVisibility(View.INVISIBLE);
//        rb_danhGiaMon.setVisibility(View.INVISIBLE);
//        cb_monAn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    frg_filter_txtmon.setVisibility(View.VISIBLE);
//                    rb_danhGiaMon.setVisibility(View.VISIBLE);
//                } else {
//                    frg_filter_txtmon.setVisibility(View.INVISIBLE);
//                    rb_danhGiaMon.setVisibility(View.INVISIBLE);
//                }
//                frg_filter_txtmon.setText("Chọn món");
//                rb_danhGiaMon.setNumStars(3);
//
//            }
//        });
//        btnAddImg.setOnClickListener(this);
//        btn_mainImg.setOnClickListener(this);
//        rb_danhGiaMon.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//
//                if (rating == 1 && cb_monAn.isChecked())
//                    Toast.makeText(getContext(), "Dở tệ", Toast.LENGTH_SHORT).show();
//                if (rating == 2 && cb_monAn.isChecked())
//                    Toast.makeText(getContext(), "Bình thường", Toast.LENGTH_SHORT).show();
//                if (rating == 3 && cb_monAn.isChecked())
//                    Toast.makeText(getContext(), "Ngon tuyệt", Toast.LENGTH_SHORT).show();
//                mRating = rating;
//            }
//        });
//
//    }
//
//    class ParseImg extends AsyncTask<Void, Void, Bitmap> {
//        File file;
//        DoInBackGroundOK mdoInBackGroundOK;
//
//        public ParseImg(File file, DoInBackGroundOK doInBackGroundOK) {
//            this.file = file;
//            this.mdoInBackGroundOK = doInBackGroundOK;
//        }
//
//        @Override
//        protected Bitmap doInBackground(Void... params) {
//            FileInputStream fis = null;
//            try {
//                File img = new File(file.toString());
//                fis = new FileInputStream(img);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                return null;
//            }
//            Bitmap bm = BitmapFactory.decodeStream(fis);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
//            return bm;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            super.onPostExecute(bitmap);
//            mdoInBackGroundOK.DoInBackGroundImg(bitmap);
//
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.i(LOG + ".onResume", "onResume");
//        if (MyService.getActionAddPost() == 1) {
//            if (MyService.getStatusChooseImg()) {
//                if (DoPost.getInstance().getFiles() != null) {
//                    if (DoPost.getInstance().getFiles().size() > 0) {
//                        anh_dai_dien = DoPost.getInstance().getFiles().get(0);
//                        ParseImg parseImg = new ParseImg(DoPost.getInstance().getFiles().get(0), this);
//                        parseImg.execute();
//                    }
//                }
//            } else {
//                Log.i(LOG + ".ACTION=1", "Không lấy được hình");
//            }
//        }
//        if (MyService.getActionAddPost() == 2) {
//            if (DoPost.getInstance().getFiles() != null)
//                btnAddImg.setText("Số hình đã thêm: " + DoPost.getInstance().getFiles().size());
//        }
////        Toast.makeText(getActivity().getApplicationContext(), "resume post Frag with key: " + locaID, Toast.LENGTH_SHORT).show();
//    }
//
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
////            case R.id.frg_post_fabchoseloca:
////                if(isConnected) {
////                    Intent intent = new Intent(getActivity(), Adapter2Activity.class);
////                    intent.putExtra(getString(R.string.fragment_CODE), getString(R.string.frag_chooseloca_CODE));
////                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                    startActivity(intent);
////                }else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
////                break;
////            case R.id.frg_post_fabchoseimg:
////                Intent intent1 = new Intent(getActivity().getApplicationContext(), Adapter2Activity.class);
////                intent1.putExtra(getResources().getString(R.string.fragment_CODE),
////                        getResources().getString(R.string.frag_chooseimg_CODE));
////                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                startActivity(intent1);
////                break;
////            case R.id.frg_post_fabrate:
////                FragmentManager fm = getActivity().getSupportFragmentManager();
////                DoVoteFragment dovoteFragment = DoVoteFragment.newIntance(getResources().getString(R.string.text_vote));
////                dovoteFragment.show(fm, "fragment_dovote");
////                break;
//            case R.id.btn_save:
//                if (isConnected) {
//                    savePost(view);
//                } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.btn_chooseMainImg:
//                if (isConnected) {
//                    MyService.setActionAddPost(1);
//                    Intent intent1 = new Intent(getActivity().getApplicationContext(), Adapter2Activity.class);
//                    intent1.putExtra(getResources().getString(R.string.fragment_CODE),
//                            getResources().getString(R.string.frag_chooseimg_CODE));
//
//                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent1);
//
//                } else {
//                    MyService.setActionAddPost(-1);
//                    Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case R.id.btn_addPhoto:
//                if (isConnected) {
//                    MyService.setActionAddPost(2);
//                    Intent intent1 = new Intent(getActivity().getApplicationContext(), Adapter2Activity.class);
//                    intent1.putExtra(getResources().getString(R.string.fragment_CODE),
//                            getResources().getString(R.string.frag_chooseimg_CODE));
//                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent1);
//
//                } else {
//                    MyService.setActionAddPost(-1);
//                    Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        isConnected = MyService.returnIsConnected();
//        mIntentFilter = new IntentFilter();
//        mIntentFilter.addAction(mBroadcastSendAddress);
//        getContext().registerReceiver(broadcastReceiver, mIntentFilter);
//        try {
//            if (DoPost.getInstance().getMyLocation() != null) {
//                img.setVisibility(View.VISIBLE);
//                txt_address.setVisibility(View.VISIBLE);
//                txt_address.setText(DoPost.getInstance().getMyLocation().getDiachi());
//                txt_name.setText(DoPost.getInstance().getMyLocation().getName());
//            } else {
//                img.setVisibility(View.GONE);
//                txt_address.setVisibility(View.GONE);
//            }
//        } catch (NullPointerException mess) {
//            img.setVisibility(View.GONE);
//            txt_address.setVisibility(View.GONE);
//            Log.e("nullChooseloca", mess.getMessage());
//        }
//
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        DoPost.getInstance().setMyLocation(null);
//        DoPost.getInstance().setVesinh(0);
//        DoPost.getInstance().setGia(0);
//        DoPost.getInstance().setFiles(null);
//        DoPost.getInstance().setPhucvu(0);
//    }
//
//    private void savePost(View view) {
//
//        try {
//            if (DoPost.getInstance().getMyLocation() == null) {
//                checloca = true;
//            }
//        } catch (NullPointerException mess) {
//            checloca = true;
//        }
//        try {
//            if (gia < 1
//                    && vs < 1
//                    && pv < 1) {
//                checkrate = true;
//            }
//        } catch (NullPointerException mess) {
//            checkrate = true;
//        }
//        if (edt_title.getText().toString().trim().equals("")) {
//            Snackbar.make(view, getResources().getString(R.string.txt_notitle), Snackbar.LENGTH_SHORT).show();
//        } else if (edt_content.getText().toString().trim().equals("")) {
//            Snackbar.make(view, getResources().getString(R.string.txt_nocontent), Snackbar.LENGTH_SHORT).show();
//
//        } else if (cb_monAn.isChecked() && (mFood.getLocaID() == null)) {
//            Snackbar.make(view, "Chưa chọn món hoặc chưa đánh giá món", Snackbar.LENGTH_SHORT).show();
//
//        } else if (checloca) {
//            Snackbar.make(view, getResources().getString(R.string.txt_nochoseloca), Snackbar.LENGTH_SHORT).show();
//        } else {
//            dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
//            key = dbRef.child(getString(R.string.posts_CODE)).push().getKey();
//            updateLoca = DoPost.getInstance().getMyLocation();
//            locaID = DoPost.getInstance().getMyLocation().getLocaID();
//            Toast.makeText(getContext(), "LOCAL ID:" + locaID, Toast.LENGTH_SHORT).show();
//            mProgressDialog = ProgressDialog.show(getActivity(),
//                    getResources().getString(R.string.txt_plzwait),
//                    getResources().getString(R.string.txt_addinpost),
//                    true, false);
//            pc_Success = 0;
//            if (MyService.getUserAccount() != null)
//                addpost(key, locaID, updateLoca);
//        }
//    }
//
//    private void addpost(String key, String locaID, MyLocation updateLoca) {
//        url = new ArrayList<>();
//        images = new ArrayList<>();
//        uris = new ArrayList<>();
//        childUpdates = new HashMap<String, Object>();
//        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
//        newPost = new Post();
//        if (cb_monAn.isChecked()) {
//            Food tempFood = mFood;
//            tempFood.setDanhGia(mRating);
//            newPost.setType(1);
//            if (mFood.getLocaID() != null) {
//                float a = mFood.getDanhGia();
//                Log.i("CSS", a + "_---------------------------");
//                float b = ((a + mRating) / 2);
//                Log.i("CSS", b + "_---------------------------");
//                mFood.setDanhGia(b);
//                childUpdates.put(
//                        getResources().getString(R.string.thucdon_CODE)
//                                + mFood.getMonID(), mFood);
//                pc_Success++;
//                Log.i("SSSSSSS", "pc_Success1=" + pc_Success);
//                newPost.setFood(tempFood);
//            }
//        } else {
//            newPost.setType(2);
//            pc_Success++;
//            Log.i("SSSSSSS", "pc_Success_Not choose=" + pc_Success);
//        }
//        if (!checkrate && cb_quanAn.isChecked()) {
//            newPost.setGia(gia);
//            newPost.setVesinh(vs);
//            newPost.setPhucvu(pv);
//            long giaTong = updateLoca.getGiaTong() + gia,
//                    vsTong = updateLoca.getVsTong() + vs,
//                    pvTong = updateLoca.getPvTong() + pv,
//                    size = updateLoca.getSize() + 1;
//            updateLoca.setGiaTong(giaTong);
//            updateLoca.setVsTong(vsTong);
//            updateLoca.setPvTong(pvTong);
//            updateLoca.setSize(size);
//            updateLoca.setGiaAVG(giaTong / size);
//            updateLoca.setVsAVG(vsTong / size);
//            updateLoca.setPvAVG(pvTong / size);
//            updateLoca.setTongAVG((giaTong + vsTong + pvTong) / (size * 3));
//            Map<String, Object> updateLocal = updateLoca.toMap();
//            Toast.makeText(getContext(), "SAO K DC Z TA:" + updateLoca.getTongAVG(), Toast.LENGTH_SHORT).show();
//            childUpdates.put(
//                    getResources().getString(R.string.locations_CODE)
//                            + locaID, updateLocal);
//            pc_Success++;
//            Log.i("SSSSSSS", "pc_Success_2=" + pc_Success);
//        } else {
//            pc_Success++;
//            Log.i("SSSSSSS", "pc_Success_2Not choose=" + pc_Success);
//        }
//        Log.i("ZOOOOOOO AddPost", "");
//        newPost.setTitle(edt_title.getText().toString());
//        newPost.setIndex(DoPost.getInstance().getMyLocation().getTinhtp() + "_" + DoPost.getInstance().getMyLocation().getQuanhuyen());
//        newPost.setContent(edt_content.getText().toString());
//        newPost.setUserId(MyService.getUserAccount().getId());
//        newPost.setUserName(MyService.getUserAccount().getUsername());
//        newPost.setDate(new Times().getTime());
//        newPost.setTime(new Times().getDate());
//        newPost.setLocaID(locaID);
//        //     if(MyService.getUserAccount().getRole()){
//        newPost.setVisible(true);
////        }else
////            newPost.setVisible(false);
//        newPost.setLocaName(DoPost.getInstance().getMyLocation().getName());
//        newPost.setDiachi(DoPost.getInstance().getMyLocation().getDiachi());
//        newPost.setIndex(DoPost.getInstance().getMyLocation().getTinhtp() + "_" + DoPost.getInstance().getMyLocation().getQuanhuyen());
//        myFile = new ArrayList<>();
//        if (MyService.getStatusChooseImg() && MyService.getActionAddPost() == 2) {
//            Log.i("SSSSSSS", "myFile=" + myFile.size());
//            myFile = DoPost.getInstance().getFiles();
//
//            if (anh_dai_dien != null && mainImg) {
//                myFile.add(anh_dai_dien);
//                Log.i("SSSSSSS", "myFile + 1=" + myFile.size());
//            }
//            new uploadImg().execute();
//        } else if (MyService.getStatusChooseImg() && MyService.getActionAddPost() == 1) {
//            Log.i("SSSSSSS", "myFile=" + myFile.size());
//            myFile = DoPost.getInstance().getFiles();
//            new uploadImg().execute();
//        } else {
//            pc_Success++;
//            postValue = newPost.toMap();
//            childUpdates.put(
//                    getResources().getString(R.string.posts_CODE) + key, postValue);
//            pc_Success++;
//            Log.i("SSSSSSS", "pc_Success_3_Not choose");
//            Log.i("SSSSSSS", "pc_Success_4=" + pc_Success);
//            if (pc_Success == 4)
//                upload();
//        }
//
//
//    }
//
//    public void upload() {
//        dbRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                if (databaseError != null) {
//                    mProgressDialog.dismiss();
//                    Toast.makeText(getActivity(), "Đăng bài bị lỗi" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                } else {
//                    if (!MyService.getUserAccount().getRole()) {
//                        Notification notification = new Notification();
//                        String key1 = dbRef.child(getResources().getString(R.string.notification_CODE) + "admin").push().getKey();
//                        notification.setAccount(MyService.getUserAccount());
//                        notification.setDate(new Times().getDate());
//                        notification.setTime(new Times().getTime());
//                        notification.setType(3);
//                        newPost.setPostID(key);
//                        notification.setPost(newPost);
//                        notification.setLocation(DoPost.getInstance().getMyLocation());
//                        notification.setReaded(false);
//                        notification.setTo("admin");
//                        Map<String, Object> notificationValue = notification.toMap();
//                        childUpdates = new HashMap<>();
//                        childUpdates.put(getResources().getString(R.string.notification_CODE) + "admin/" + key, notificationValue);
//                        dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isComplete()) {
//                                    mProgressDialog.dismiss();
//                                    Toast.makeText(getActivity(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
//                                    getActivity().finish();
//                                } else {
//                                    mProgressDialog.dismiss();
//                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    } else {
//                        mProgressDialog.dismiss();
//                        Toast.makeText(getActivity(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
//                        getActivity().finish();
//                    }
//                }
//            }
//        });
//        pc_Success = 0;
//    }
//
//    class uploadImg extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... params) {
//            storeRef = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebaseStorage_path));
//            ListID = new ArrayList<>();
//            Log.i("uploadImg", "myFile=" + myFile.size());
//            for (File f : myFile) {
//                Uri uri = Uri.fromFile(f);
//                fileKey = dbRef.child(getResources().getString(R.string.images_CODE1)).push().getKey();
//                ListID.add(fileKey);
//                newImage = new Image();
//                newImage.setName(uri.getLastPathSegment());
//                newImage.setPostID(key);
//                newImage.setUserID(MyService.getUserAccount().getId());
//                newImage.setLocaID(locaID);
//                images.add(newImage);
//                Log.i("SSSSSSS", "images=" + images.size());
//                StorageReference myChildRef = storeRef.child(
//                        getResources().getString(R.string.images_CODE)
//                                + uri.getLastPathSegment());
//                uploadTask = myChildRef.putFile(uri);
//                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Uri uri = taskSnapshot.getDownloadUrl();
//                        uris.add(uri);
//                        if (uris.size() == images.size())
//                            MyService.setUriImg(uris);
//                        Log.i("ZOOOOOOO UploadImage", "isSuccess = true;" + uri.toString());
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Log.i("ZOOOOOOO UploadImage", "isSuccess = false;");
//                                            }
//                                        }
//                );
//            }
//            return null;
//        }
//    }
//
//    @Override
//    public void DoInBackGroundStart() {
//
//    }
//
//    @Override
//    public void DoInBackGroundOK(Boolean isSuccess, int type) {
//    }
//
//    @Override
//    public void DoInBackGroundImg(Bitmap bitmap) {
//        try {
//            img_Daidien.setImageBitmap(bitmap);
//            mainImg = true;
//            Log.i(LOG + ".ACTION=1", "set Anh dai dien: OK");
//        } catch (Exception e) {
//            Log.i(LOG + ".ACTION=1", "set Anh dai dien: FAIL");
//        }
//    }
//
//    @Override
//    public void DoInBackGroundLocation(MyLocation location) {
//
//    }
//
//    //        class StoreImg extends AsyncTask<ArrayList<File>,Void,Boolean> {
////        DoInBackGroundOK doInBackGroundOK;
////        StorageReference storeRef;
////        UploadTask uploadTask;
////        boolean isSuccess=false;
////        public StoreImg(DoInBackGroundOK mdoInBackGroundOK, StorageReference storageReference,UploadTask mup){
////            doInBackGroundOK=mdoInBackGroundOK;
////            storeRef=storageReference;
////            uploadTask=mup;
////        }
////        @Override
////        protected Boolean doInBackground(ArrayList<File>... params) {
////            Log.i("ZOOOOOOO ParseImg","");
////
////            return isSuccess;
////        }
////
////        @Override
////        protected void onPostExecute(Boolean aBoolean) {
////            super.onPostExecute(aBoolean);
////            if(aBoolean){
////                doInBackGroundOK.DoInBackGroundOK(true,1);
////            } else{
////                doInBackGroundOK.DoInBackGroundOK(false,1);
////            }
////        }
////    }
////    class UpDateLoca extends AsyncTask<MyLocation,Void,Boolean>{
////        long gia,vs,pv;
////        String locaID;
////        Boolean isSuccess=false;
////        Map<String, Object> childUpdates;
////        DoInBackGroundOK doInBackGroundOK;
////        DatabaseReference dbRef;
////
////        public UpDateLoca(long mgia,long mvs,long mpv,String mlocaID, DoInBackGroundOK mdoInBackGroundOK,DatabaseReference databaseReference){
////            gia=mgia;
////            vs=mvs;
////            pv=mpv;
////            locaID=mlocaID;
////            doInBackGroundOK=mdoInBackGroundOK;
////            childUpdates=new HashMap<>();
////            dbRef=databaseReference;
////        }
////        @Override
////        protected Boolean doInBackground(MyLocation... params) {
////            Log.i("ZOOOOOOO UpDateLoca","");
////            MyLocation updateLoca=params[0];
////            long giaTong = updateLoca.getGiaTong() + gia,
////                    vsTong = updateLoca.getVsTong() + vs,
////                    pvTong = updateLoca.getPvTong() +pv,
////                    size = updateLoca.getSize() + 1;
////            Log.i("CSS"+size,giaTong+"_-----/n"+vsTong+"----/n"+pvTong);
////            updateLoca.setGiaTong(giaTong);
////            updateLoca.setVsTong(vsTong);
////            updateLoca.setPvTong(pvTong);
////            updateLoca.setSize(size);
////            updateLoca.setGiaAVG(giaTong / size);
////            updateLoca.setVsAVG(vsTong / size);
////            updateLoca.setPvAVG(pvTong / size);
////            updateLoca.setTongAVG((giaTong + vsTong + pvTong) / (size * 3));
////            childUpdates.put(
////                    getResources().getString(R.string.locations_CODE)
////                            + locaID, updateLoca);
////            dbRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
////                @Override
////                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
////                    if (databaseError != null) {
////                       isSuccess=false;
////                    } else {
////                        isSuccess=true;
////                    }
////                }
////            });
////            return isSuccess;
////        }
////
////        @Override
////        protected void onPostExecute(Boolean aBoolean) {
////            super.onPostExecute(aBoolean);
////            if(aBoolean){
////                doInBackGroundOK.DoInBackGroundOK(true,2);
////            } else{
////                doInBackGroundOK.DoInBackGroundOK(false,2);
////            }
////        }
////    }
////    class AddPost extends AsyncTask<Post,Void, Boolean>{
////        String title,content,userId,userName,localName,localDiaChi,locaID,key;
////        DoInBackGroundOK doInBackGroundOK;
////        Map<String, Object> childUpdates;
////        Boolean isSuccess=false;
////        boolean mainImg=false;
////        ImageView img_Daidien;
////        DatabaseReference dbRef;
////        public AddPost(String mtitle,String mContent,String muserID,String muserName,
////                        String mlocalName, String mlocalDiaChi, DoInBackGroundOK mDoInBackGroundOK,
////                       boolean mmainImg, ImageView img,String mlocalID,String mKey,DatabaseReference databaseReference){
////            title=mtitle;
////            content=mContent;
////            userId=muserID;
////            userName=muserName;
////            localName=mlocalName;
////            localDiaChi=mlocalDiaChi;
////            mainImg=mmainImg;
////            doInBackGroundOK=mDoInBackGroundOK;
////            img_Daidien=img;
////            locaID=mlocalID;
////            childUpdates=new HashMap<>();
////            key=mKey;
////            dbRef=databaseReference;
////        }
////        @Override
////        protected Boolean doInBackground(Post... params) {
////
////            dbRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
////                @Override
////                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
////                    if (databaseError != null) {
////                        isSuccess=false;
////                    } else {
////                        isSuccess=true;
////                    }
////                }
////            });
////            return isSuccess;
////        }
////
////        @Override
////        protected void onPostExecute(Boolean aBoolean) {
////            super.onPostExecute(aBoolean);
////            if(aBoolean){
////                doInBackGroundOK.DoInBackGroundOK(true,3);
////            } else{
////                doInBackGroundOK.DoInBackGroundOK(false,3);
////            }
////        }
////    }
////    class AddImg extends AsyncTask<ArrayList<File>,Void,Boolean>{
////        DoInBackGroundOK doInBackGroundOK;
////        Map<String, Object> childUpdates;
////        DatabaseReference dbRef;
////        String key,locaID;
////        public AddImg(DoInBackGroundOK mdoInBackGroundOK, DatabaseReference databaseReference,String mkey,String mlocalID){
////            doInBackGroundOK=mdoInBackGroundOK;
////            dbRef=databaseReference;
////            childUpdates=new HashMap<>();
////
////        }
////        boolean isSuccess=false;
////        @Override
////        protected Boolean doInBackground(ArrayList<File>... params) {
////            for (File f : params[0]) {
////                Uri uri = Uri.fromFile(f);
////                String fileKey = dbRef.child(getResources().getString(R.string.images_CODE)).push().getKey();
////                Image newImage = new Image();
////                newImage.setName(uri.getLastPathSegment());
////                newImage.setPostID(key);
////                newImage.setUserID(MyService.getUserID());
////                newImage.setLocaID(locaID);
////                childUpdates.put(getResources().getString(R.string.images_CODE)
////                        + fileKey, newImage);
////            }
////            return isSuccess;
////        }
////
////        @Override
////        protected void onPostExecute(Boolean aBoolean) {
////            super.onPostExecute(aBoolean);
////            if(aBoolean)
////                doInBackGroundOK.DoInBackGroundOK(null,4);
////            else
////                doInBackGroundOK.DoInBackGroundOK(null,5);
////        }
////    }
////    @Override
////    public void DoInBackGroundStart() {
////    }
////
////    @Override
////    public void DoInBackGroundOK(Boolean isSuccess,int type) {
////        if(type<4){
////            pc_Success++;
////            this.childUpdates=childUpdates;
////                Log.i("CHAY OKKKKKKKKKKKK","");
////                dbRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
////                    @Override
////                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
////                        if (databaseError != null) {
////                            Log.i("CHAY ","FAILLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
////
////                        } else {
////                            Log.i("CHAY ","OKKKKKKKKKKK");
////
////                        }
////                    }
////                });
////            childUpdates=new HashMap<>();
////
////            Log.i("DoInBackGroundOK","OK-"+type+"-pc success"+pc_Success+"-"+childUpdates.size());
////        }else{
////            if(type==4){
////                Log.i("DoInBackGroundOK","type==4");
////                Toast.makeText(getContext(),"Thêm thành công",Toast.LENGTH_SHORT).show();
////                getActivity().finish();
////            }else
////                Toast.makeText(getContext(),"Thêm thất bại. Xin thử lại",Toast.LENGTH_SHORT).show();
////            mProgressDialog.dismiss();
////        }
////        if(pc_Success==4){
//////            Log.i("DoInBackGroundOK","pc_Success==4 ---"+childUpdates.size());
//////            AddData addData=new AddData(this,dbRef);
//////            addData.execute(childUpdates);
////            pc_Success=0;
////        }
////
////    }
//    @Override
//    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
//        switch (seekBar.getId()) {
//            case R.id.frg_vote_slide_gia:
//                txt_gia.setText(getResources().getString(R.string.text_rategia) + ": " + String.valueOf(value));
//                try {
//                    gia = (long) seekBar.getProgress();
//
//                } catch (NullPointerException mess) {
//                    gia = (long) 1;
//                }
//                break;
//            case R.id.frg_vote_slide_phucvu:
//                txt_pv.setText(getResources().getString(R.string.text_ratepv) + ": " + String.valueOf(value));
//                try {
//                    pv = (long) seekBar.getProgress();
//                } catch (NullPointerException mess) {
//                    pv = (long) 1;
//                }
//                break;
//            case R.id.frg_vote_slide_vesinh:
//                txt_vs.setText(getResources().getString(R.string.text_ratevs) + ": " + String.valueOf(value));
//                try {
//                    vs = (long) seekBar.getProgress();
//                } catch (NullPointerException mess) {
//                    vs = (long) 1;
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
//
//    }
//
//    @Override
//    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
//
//    }
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
}
