package com.app.ptt.comnha.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.CustomAutoCompleteTextAdapter;
import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Fragment.PickLocationBottomSheetDialogFragment;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Models.Search;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
import com.app.ptt.comnha.SingletonClasses.CoreManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity implements View.OnClickListener, PickLocationBottomSheetDialogFragment.onPickListener {
    ImageView imgv_back;
    List<Search> list;
    List<Store> stores;
    List<Food> foods;
    LinearLayout reveal_linear;
    TabLayout tabLayout;
    RelativeLayout relative_temp;
    AutoCompleteTextView edtSearch;
    CustomAutoCompleteTextAdapter adapter;
    int tabPos=0;
    ValueEventListener foodValueListener,childEventListener;
    DatabaseReference dbRef;
    ImageButton btnSearch;
    String dist_pro;
    Button btnQuan,btnHuyen;
    FragmentManager fm;
    PickLocationBottomSheetDialogFragment pickLocationDialog;
    int whatProvince = -1;
    String tinh,huyen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ref();

        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseDB_path));
        list=new ArrayList<>();
        stores=new ArrayList<>();
        foods=new ArrayList<>();
        dist_pro = CoreManager.getInstance().getMyLocation().getDistrict()+"_"+CoreManager.getInstance().getMyLocation().getProvince();
        pickLocationDialog = new PickLocationBottomSheetDialogFragment();
        pickLocationDialog.setOnPickListener(this);
        fm = getSupportFragmentManager();
    }

    private void ref() {
        imgv_back = (ImageView) findViewById(R.id.imgv_back_search);
        btnSearch= (ImageButton) findViewById(R.id.btn_search);
        btnSearch.setVisibility(View.INVISIBLE);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText("");
            }
        });
        imgv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        edtSearch = (AutoCompleteTextView) findViewById(R.id.edt_content_search);
        btnHuyen= (Button) findViewById(R.id.btn_tp);
        btnQuan= (Button) findViewById(R.id.btn_quan);
        btnQuan.setOnClickListener(this);
        btnHuyen.setOnClickListener(this);
       edtSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               if(tabPos==0){
                   edtSearch.setText(adapter.returnItem(position).getFood().getName());
               }else{
                   edtSearch.setText(adapter.returnItem(position).getStore().getName());
               }
               Intent intent = new Intent(SearchActivity.this, StoreDeatailActivity.class);
//               Bitmap imgBitmap = ((BitmapDrawable) imgMarker.getDrawable())
//                       .getBitmap();
//               a.setImgBitmap(imgBitmap);
               ChooseStore.getInstance().setStore(adapter.returnItem(position).getStore());
               startActivity(intent);
           }
       });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(s.toString())){
                    btnSearch.setVisibility(View.INVISIBLE);
                }else{
                    btnSearch.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled=false;
                if(actionId==EditorInfo.IME_ACTION_DONE){
                    handled=true;
                }
                InputMethodManager imm= (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view=getCurrentFocus();
                if(view==null){
                    view=new View(SearchActivity.this);
                }
                imm.hideSoftInputFromInputMethod(view.getWindowToken(),0);
                return handled;
            }
        });
        reveal_linear = (LinearLayout) findViewById(R.id.linear_search);
        relative_temp = (RelativeLayout) findViewById(R.id.relative_search);
        relative_temp.setBackgroundColor(
                getResources().getColor(R.color.color_notify_reportfood));
        tabLayout = (TabLayout) findViewById(R.id.tablayout_search);
        View foodtabview = LayoutInflater.from(this).inflate(R.layout.tab_search, null);
        foodtabview.findViewById(R.id.imgv_icon_tabsearch)
                .setBackgroundResource(R.drawable.ic_tab_food);
        TabLayout.Tab foodtab = tabLayout.newTab().setCustomView(foodtabview);
        View storetabview = LayoutInflater.from(this).inflate(R.layout.tab_search, null);
        storetabview.findViewById(R.id.imgv_icon_tabsearch)
                .setBackgroundResource(R.drawable.ic_tab_store);
        TabLayout.Tab storetab = tabLayout.newTab().setCustomView(storetabview);
        tabLayout.addTab(foodtab, 0);
        tabLayout.addTab(storetab, 1);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int top = tabLayout.getTop(), btm = reveal_linear.getBottom(),
                        left = tabLayout.getLeft(),
                        right = tabLayout.getRight(), h = tabLayout.getHeight(),
                        w = tabLayout.getWidth();
                int cx = 0,
                        cy = 0;
                long duration = 250;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    switch (tab.getPosition()) {
                        case 0:
                            cx = left;
                            cy = btm;
                            relative_temp.setBackgroundColor(getResources()
                                    .getColor(R.color.admin_color_selection_news));
                            reveal_linear.setBackgroundColor(
                                    getResources()
                                            .getColor(R.color.color_notify_reportfood));
                            AnimationUtils.getInstance()
                                    .createOpenCR(reveal_linear, duration, cx, cy);
                            getWindow().setStatusBarColor(
                                    getResources().getColor(R.color.color_notify_reportfood));
                            if(null!=adapter){
                                adapter.setType(0);

                            }
                                tabPos=0;

                            break;

                        case 1:
                            cx = right;
                            cy = btm;
                            relative_temp.setBackgroundColor(getResources()
                                    .getColor(R.color.color_notify_reportfood));
                            reveal_linear.setBackgroundColor(
                                    getResources()
                                            .getColor(R.color.admin_color_selection_news));
                            AnimationUtils.getInstance()
                                    .createOpenCR(reveal_linear, duration, cx, cy);
                            getWindow().setStatusBarColor(
                                    getResources().getColor(R.color.admin_color_selection_news));
                            if(null!=adapter){
                                adapter.setType(1);

                            }
                                tabPos=1;

                            break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    private void getFoodList() {
        foodValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    Food food = dataItem.getValue(Food.class);
                    String key = dataItem.getKey();
                    food.setFoodID(key);

                    for(Store store: stores){
                        if(food.getStoreID().equals(store.getStoreID())){
                            Search search=new Search();
                            search.setType(0);
                            search.setFood(food);
                            search.setStore(store);
                            list.add(search);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                closeDialog();
                adapter=new CustomAutoCompleteTextAdapter(getApplicationContext(),list);
                adapter.setType(tabPos);
                edtSearch.setAdapter(adapter);
                edtSearch.setThreshold(3);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        dbRef.child(getString(R.string.food_CODE))
                .orderByChild("isHidden_dist_prov")
                .equalTo(false + "_" + dist_pro)
                .addValueEventListener(foodValueListener);
    }
    private void getStoreList() {
        showProgressDialog("Loading...","Vui lòng đợi");
        childEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Store store = item.getValue(Store.class);
                    store.setStoreID(item.getKey());
                    Log.d("added", "added");
                    stores.add(store);
                    Search search=new Search() ;
                    search.setStore(store);
                    search.setType(1);
                    list.add(search);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.store_CODE)).orderByChild("isHidden_dis_pro")
                .equalTo(false + "_" + dist_pro)
                .addValueEventListener(childEventListener);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getFoodList();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_quan:
                if (whatProvince >= 0) {
                    Log.i("province", whatProvince + "");
                    pickLocationDialog.setWhatProvince(whatProvince);
                    pickLocationDialog.show(fm, "pickDistrictDialog");
                } else {
                    Toast.makeText(this, getString(R.string.txt_noChoseProvince), Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_tp:
                pickLocationDialog.show(fm, "pickProvinceDialog");
                break;
        }
    }

    @Override
    public void onPicProvince(String province, int position) {
        whatProvince = position;
        tinh = province;
        btnHuyen.setText(tinh);
    }

    @Override
    public void onPickDistrict(String district) {
        list.clear();
        huyen = district;
        btnQuan.setText(huyen);
        if (MyService.isNetworkAvailable(this)) {
           dist_pro=huyen+"_"+tinh;
            getStoreList();
        } else {
            Toast.makeText(this, "You are offline", Toast.LENGTH_LONG).show();
        }
    }
}
