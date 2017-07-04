package com.app.ptt.comnha.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.CustomAutoCompleteTextAdapter;
import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Fragment.PickLocationBottomSheetDialogFragment;
import com.app.ptt.comnha.Interfaces.Comunication;
import com.app.ptt.comnha.Interfaces.Transactions;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Models.FireBase.User;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity implements View.OnClickListener, PickLocationBottomSheetDialogFragment.onPickListener, Transactions {

    List<Search> list;
    List<Store> stores;
    List<Food> foods;
    LinearLayout reveal_linear;
    TabLayout tabLayout;
    RelativeLayout relative_temp;
    RecyclerView rvList;
    CustomAutoCompleteTextAdapter adapter;
    int tabPos = 0;
    ValueEventListener foodValueListener, childEventListener;
    DatabaseReference dbRef;
    StorageReference stRef;
    String dist_pro;
    Button btnQuan, btnHuyen;
    FragmentManager fm;
    PickLocationBottomSheetDialogFragment pickLocationDialog;
    int whatProvince = -1;
    String tinh, huyen;
    int type = 0;
    ImageButton btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        stRef = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebaseStorage_path));
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseDB_path));
        list = new ArrayList<>();
        stores = new ArrayList<>();
        foods = new ArrayList<>();
        ref();
        dist_pro = CoreManager.getInstance().getMyLocation().getDistrict() + "_" + CoreManager.getInstance().getMyLocation().getProvince();
        pickLocationDialog = new PickLocationBottomSheetDialogFragment();
        pickLocationDialog.setOnPickListener(this);
        fm = getSupportFragmentManager();

        Comunication.transactions = this;
        getStoreList();

    }

    private void ref() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.txt_search));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rvList = (RecyclerView) findViewById(R.id.rv_list_user);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(linearLayoutManager);
        adapter = new CustomAutoCompleteTextAdapter(getApplicationContext(), list, stRef);
        adapter.setType(0);
        rvList.setAdapter(adapter);
        btnHuyen = (Button) findViewById(R.id.btn_tp);
        btnQuan = (Button) findViewById(R.id.btn_quan);
        btnReset = (ImageButton) findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(this);
        btnQuan.setOnClickListener(this);
        btnHuyen.setOnClickListener(this);

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
                            tabPos = 0;

                            if (null != adapter) {
                                adapter.setType(0);
                            }
                            if (TextUtils.isEmpty(tinh) && TextUtils.isEmpty(huyen)) {
                                type = 0;
                            } else {
                                type = 2;
                            }
                            getDataFiltered(type);
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
                            tabPos = 1;
                            if (TextUtils.isEmpty(tinh) && TextUtils.isEmpty(huyen)) {
                                type = 1;
                            } else {
                                type = 3;
                            }
                            getDataFiltered(type);
                            if (null != adapter) {
                                adapter.setType(1);
                            }

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

                    for (Store store : stores) {
                        if (food.getStoreID().equals(store.getStoreID())) {
                            Search search = new Search();
                            search.setType(0);
                            search.setFood(food);
                            search.setStore(store);
                            if (-1 != getItemInList(food.getFoodID(), 0)) {
                                list.set(getItemInList(food.getFoodID(), 0), search);
                                getDataFiltered(type);
                            } else {
                                list.add(search);
                            }
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
                getDataFiltered(type);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        dbRef.child(getString(R.string.food_CODE))
                .addValueEventListener(foodValueListener);
    }

    public void getDataFiltered(int type) {
        adapter.clearList();
        for (Search search : list) {
            //all food
            switch (type) {
                //all food
                case 0:
                    if (search.getType() == 0) {
                        adapter.addItem(search);
                    }
                    break;
                //all store
                case 1:
                    if (search.getType() == 1) {
                        adapter.addItem(search);
                    }
                    break;
                //custom food
                case 2:
                    if (search.getType() == 0 && search.getFood().getDist_prov().toLowerCase().equals(dist_pro.toLowerCase())) {
                        adapter.addItem(search);
                    }
                    break;
                //custom store
                case 3:
                    if (search.getType() == 1 && search.getStore().getPro_dist().toLowerCase().equals(dist_pro.toLowerCase())) {
                        adapter.addItem(search);
                    }
                    break;

            }

        }
    }

    private void getStoreList() {
        showProgressDialog("Loading...", "Vui lòng đợi");
        childEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Store store = item.getValue(Store.class);
                    store.setStoreID(item.getKey());
                    Log.d("added", "added");
                    stores.add(store);
                    Search search = new Search();
                    search.setStore(store);
                    search.setType(1);
                    if (-1 != getItemInList(store.getStoreID(), 1)) {
                        list.set(getItemInList(store.getStoreID(), 1), search);
                        getDataFiltered(type);
                    } else {
                        list.add(search);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.store_CODE))
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

    public int getItemInList(String id, int type) {
        for (Search search : list) {
            //store
            if (type == 1) {
                if (search.getType() == 1 && id.toLowerCase().equals(search.getStore().getStoreID().toLowerCase())) {
                    return list.indexOf(search);
                }
            } else {
                if (search.getType() == 0 && id.toLowerCase().equals(search.getFood().getFoodID().toLowerCase())) {
                    return list.indexOf(search);
                }
            }

        }
        return -1;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            case R.id.btn_reset:
                whatProvince = -1;
                btnHuyen.setText("Tỉnh/TP");
                btnQuan.setText("Quận/huyện");
                tinh = "";
                huyen = "";
                if (tabPos == 0) {
                    type = 0;
                    getDataFiltered(type);
                } else {
                    type = 1;
                    getDataFiltered(type);
                }
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

        huyen = district;
        btnQuan.setText(huyen);
        if (MyService.isNetworkAvailable(this)) {
            dist_pro = huyen + "_" + tinh;
            if (tabPos == 0) {
                type = 2;
            } else {
                type = 3;

            }
            getDataFiltered(type);
        } else {
            Toast.makeText(this, "You are offline", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setupFirebase() {

    }

    @Override
    public boolean createNew() {
        return false;
    }

    @Override
    public void update() {

    }

    @Override
    public void delete() {

    }

    @Override
    public void deleteUser(User user) {

    }

    @Override
    public void changeRole(User user) {

    }

    @Override
    public void changeUserPermission(User user) {

    }

    @Override
    public void onSearchItemClick(Search search) {
        Intent intent = new Intent(SearchActivity.this, StoreDeatailActivity.class);
        if (search.getType() == 0) {
            ChooseStore.getInstance().setStore(list.get(getItemInList(search.getFood().getStoreID(), 1)).getStore());
        } else {
            ChooseStore.getInstance().setStore(list.get(getItemInList(search.getStore().getStoreID(), 1)).getStore());
        }

        startActivity(intent);
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
}
