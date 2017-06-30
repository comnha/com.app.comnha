package com.app.ptt.comnha.Activity;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.User_rcyler_adapter;
import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Fragment.PickLocationBottomSheetDialogFragment;
import com.app.ptt.comnha.Interfaces.Comunication;
import com.app.ptt.comnha.Interfaces.Transactions;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.Models.Search;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.Utils.AppUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserManageActivity extends BaseActivity implements View.OnClickListener, PickLocationBottomSheetDialogFragment.onPickListener, Transactions {
    ImageView imgv_back;
    List<Search> listSearch;
    List<User> list, listShown;
    RecyclerView rvList;
    LinearLayout reveal_linear;
    TabLayout tabLayout;
    RelativeLayout relative_temp;

    int tabPos = 0;
    StorageReference stRef;
    ValueEventListener foodValueListener, childEventListener;
    DatabaseReference dbRef;

    String dist_pro;
    Button btnQuan, btnHuyen, btnQuan1, btnHuyen1;
    FragmentManager fm;
    User_rcyler_adapter user_rcyler_adapter;
    PickLocationBottomSheetDialogFragment pickLocationDialog;
    int whatProvince = -1, whatProvince1 = -1;
    boolean flagChooseLocation = false;
    String tinh, huyen, tinh1, huyen1;
    int posType = -1;
    ImageButton btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manage);
        list = new ArrayList<>();
        listShown = new ArrayList<>();
        ref();

        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Const.DATABASE_PATH);

        pickLocationDialog = new PickLocationBottomSheetDialogFragment();
        pickLocationDialog.setOnPickListener(this);
        fm = getSupportFragmentManager();
        getUserList();
        Comunication.transactions = this;
    }

    private void ref() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        stRef = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebaseStorage_path));
        rvList = (RecyclerView) findViewById(R.id.rv_list_user);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(linearLayoutManager);
        user_rcyler_adapter = new User_rcyler_adapter(UserManageActivity.this, listShown, stRef);
        rvList.setAdapter(user_rcyler_adapter);


        btnHuyen = (Button) findViewById(R.id.btn_tp);
        btnQuan = (Button) findViewById(R.id.btn_quan);
        btnReset = (ImageButton) findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(this);
        btnQuan.setOnClickListener(this);
        btnHuyen.setOnClickListener(this);
        btnQuan.setVisibility(View.GONE);
        btnHuyen.setVisibility(View.GONE);
        btnReset.setVisibility(View.GONE);
        reveal_linear = (LinearLayout) findViewById(R.id.linear_search);
        relative_temp = (RelativeLayout) findViewById(R.id.relative_search);
        relative_temp.setBackgroundColor(
                getResources().getColor(R.color.color_notify_reportfood));
        tabLayout = (TabLayout) findViewById(R.id.tablayout_search);
        View user = LayoutInflater.from(this).inflate(R.layout.tab_search, null);
        user.findViewById(R.id.imgv_icon_tabsearch)
                .setBackgroundResource(R.drawable.icon_user);
        TabLayout.Tab foodtab = tabLayout.newTab().setCustomView(user);
        View admin = LayoutInflater.from(this).inflate(R.layout.tab_search, null);
        admin.findViewById(R.id.imgv_icon_tabsearch)
                .setBackgroundResource(R.drawable.icon_admin);
        TabLayout.Tab storetab = tabLayout.newTab().setCustomView(admin);
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
                            whatProvince1 = -1;
                            posType = 1;
                            getDataFiltered(posType);
                            btnQuan.setVisibility(View.GONE);
                            btnHuyen.setVisibility(View.GONE);
                            btnReset.setVisibility(View.GONE);
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
                                posType = 2;
                                getDataFiltered(posType);
                            } else {
                                posType = 3;
                                getDataFiltered(posType);
                            }

                            btnQuan.setVisibility(View.VISIBLE);
                            btnHuyen.setVisibility(View.VISIBLE);
                            btnReset.setVisibility(View.VISIBLE);
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

    private void getUserList() {
        user_rcyler_adapter.clearList();
        showProgressDialog("Loading...", "Vui lòng đợi");
        childEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    User user = dataItem.getValue(User.class);
                    String key = dataItem.getKey();
                    user.setuID(key);
                    if (getUserInList(key) != -1) {
                        list.set(getUserInList(key), user);
                        getDataFiltered(posType);
                    } else {
                        list.add(user);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        dbRef.child(getString(R.string.users_CODE))
                .addValueEventListener(childEventListener);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                closeDialog();
                if (tabPos == 0) {
                    posType = 1;
                    getDataFiltered(posType);
                } else {
                    posType = 2;
                    getDataFiltered(posType);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getDataFiltered(int type) {
        user_rcyler_adapter.clearList();
        for (User user : list) {
            //user
            if (type == 1) {
                if (user.getRole() == 0) {
                    user_rcyler_adapter.addItem(user);
                }
            } else {
                //admin
                if (type == 2) {
                    if (user.getRole() == 1) {
                        user_rcyler_adapter.addItem(user);
                    }
                } else {
                    //custom place
                    if (type == 3) {
                        if (user.getRole() == 1 && user.getDist_prov().toLowerCase().equals(dist_pro.toLowerCase())) {
                            user_rcyler_adapter.addItem(user);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
                flagChooseLocation = false;
                break;
            case R.id.btn_tp:
                pickLocationDialog.show(fm, "pickProvinceDialog");
                flagChooseLocation = false;
                break;
            case R.id.btn_reset:
                whatProvince = -1;
                btnHuyen.setText("Tỉnh/TP");
                btnQuan.setText("Quận/huyện");
                tinh = "";
                huyen = "";
                if (tabPos == 0) {
                    posType = 1;
                    getDataFiltered(posType);
                } else {
                    posType = 2;
                    getDataFiltered(posType);
                }
                break;
        }
    }


    @Override
    public void onPicProvince(String province, int position) {
        if (flagChooseLocation) {
            tinh1 = province;
            btnHuyen1.setText(tinh1);
            whatProvince1 = position;
        } else {
            tinh = province;
            btnHuyen.setText(tinh);
            whatProvince = position;
        }


    }

    @Override
    public void onPickDistrict(String district) {
        if (flagChooseLocation) {
            huyen1 = district;
            btnQuan1.setText(huyen1);
        } else {
            huyen = district;
            btnQuan.setText(huyen);
            if (MyService.isNetworkAvailable(this)) {
                dist_pro = huyen + "_" + tinh;
                posType = 3;
                getDataFiltered(posType);
            } else {
                Toast.makeText(this, "You are offline", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void removeUser(final User user) {

        dbRef.child(getString(R.string.users_CODE)
                + user.getuID())
                .removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), "Xóa thành công");
                        list = new ArrayList<User>();
                        getUserList();
                    }
                });
    }

    public void updateUser(final User user) {
        if (user.getRole() == 0) {
            user.setRole(1);
        } else {
            user.setRole(0);
            user.setDistrict("");
            user.setProvince("");
            user.setDist_prov("");
        }
        showProgressDialog("Đang xử lý", "Vui lòng đợi");
        Map<String, Object> newUser;
        newUser = user.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(getString(R.string.users_CODE) + "/" + user.getuID(), newUser);
        dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (user.getRole() == 1) {
                    AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), "Cấp quyền thành công");
                } else {
                    AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), "Xóa quyền thành công");
                }
                tinh1 = "";
                huyen1 = "";
                closeDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), "Đã xảy ra lỗi");
                tinh1 = "";
                huyen1 = "";
                closeDialog();
            }
        });
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
        removeUser(user);
    }

    @Override
    public void changeRole(User user) {
        if (user.getRole() == 0) {
            showDialogSetAdminLocation(user);
        } else {
            updateUser(user);
        }
    }

    @Override
    public void onSearchItemClick(Search search) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem search = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                user_rcyler_adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    public int getUserInList(String id) {
        for (User user : list) {
            if (id.toLowerCase().equals(user.getuID().toLowerCase())) {
                return list.indexOf(user);
            }
        }
        return -1;
    }

    public void showDialogSetAdminLocation(final User user) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_set_admin_location);
        btnHuyen1 = (Button) dialog.findViewById(R.id.btn_tp1);
        btnQuan1 = (Button) dialog.findViewById(R.id.btn_quan1);
        btnQuan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagChooseLocation = true;
                if (whatProvince1 >= 0) {
                    Log.i("province", whatProvince1 + "");
                    pickLocationDialog.setWhatProvince(whatProvince1);
                    pickLocationDialog.show(fm, "pickDistrictDialog");
                } else {
                    Toast.makeText(UserManageActivity.this, getString(R.string.txt_noChoseProvince), Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnHuyen1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagChooseLocation = true;
                pickLocationDialog.show(fm, "pickProvinceDialog");
            }
        });
        ImageButton btn = (ImageButton) dialog.findViewById(R.id.btn_apply);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(tinh1) && !TextUtils.isEmpty(huyen1)) {
                    user.setDist_prov(huyen1 + "_" + tinh1);
                    user.setDistrict(huyen1);
                    user.setProvince(tinh1);
                    updateUser(user);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

}
