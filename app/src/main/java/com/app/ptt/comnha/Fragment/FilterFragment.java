package com.app.ptt.comnha.Fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Activity.Adapter2Activity;
import com.app.ptt.comnha.Adapters.LocatlistFilter_rcyler_adapter;
import com.app.ptt.comnha.Classes.RecyclerItemClickListener1;
import com.app.ptt.comnha.FireBase.Food;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChooseLoca;
import com.app.ptt.comnha.SingletonClasses.EditPost;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterFragment extends Fragment implements View.OnKeyListener ,View.OnClickListener, PickProvinceDialogFragment.OnnPickProvinceListener, PickDistrictDialogFragment.OnPickDistricListener {

    TextView txt_tinh, txt_quan, txt_mon, txt_loaimon;
    Button btn_tim;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<MyLocation> locaList;
    int whatProvince;
   EditText edt_content;
    ImageButton btn_search,btn_reset;
    Food mon = null;
    DatabaseReference dbRef;
    ChildEventListener locaMenuChildEventListener,foodMenuChildEventListener;
    String tinh = "", quan = "";
    String foodCateID;
    boolean searchByFood=false;
    RadioGroup radio_group;
    RadioButton rb_quan,rb_mon;
    int seemore=50;
    ProgressDialog mProgressDialog;
    ArrayList<String> searchList;
    ArrayList<Food> packageFoods;
    ArrayList<MyLocation> packageLocations;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    //FoodAdapter foodAdapter;
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    boolean isConnected = false;
    IntentFilter mIntentFilter;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastSendAddress)) {
                Log.i("" + ".onReceive form Service", "isConnected= " + intent.getBooleanExtra("isConnected", false));
                if (intent.getBooleanExtra("isConnected", false)) {
                    if (!isConnected)
                    isConnected = true;
                } else
                    isConnected = false;
            }
        }
    };
    int pos;
    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        isConnected= MyService.returnIsConnected();
        anhxa(view);
        return view;
    }
    private void anhxa(View view) {
        packageFoods=new ArrayList<>();
        packageLocations=new ArrayList<>();
        btn_reset=(ImageButton) view.findViewById(R.id.frg_filter_btnreset);
        btn_search = (ImageButton) view.findViewById(R.id.frg_filter_btnsearch);
        edt_content=(EditText) view.findViewById(R.id.frg_filter_edtsearch);
        txt_tinh = (TextView) view.findViewById(R.id.frg_filter_txttinh);
        txt_quan = (TextView) view.findViewById(R.id.frg_filter_txtquan);
        txt_mon = (TextView) view.findViewById(R.id.frg_filter_txtmon);
        locaList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_filter_recyler);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        txt_tinh.setOnClickListener(this);
        txt_quan.setOnClickListener(this);
        radio_group=(RadioGroup) view.findViewById(R.id.rb_group);
        rb_mon=(RadioButton) view.findViewById(R.id.rb_mon);
        rb_quan=(RadioButton) view.findViewById(R.id.rb_quan);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected) {
                    if (edt_content.getText().toString().equals("")) {
                        Toast.makeText(getContext(), getString(R.string.txt_noaddress),
                                Toast.LENGTH_LONG).show();
                    } else {
                        edt_content.setText("");
                        edt_content.clearFocus();
                        btn_search.setImageResource(R.drawable.ic_search_grey_600_24dp);
                    }
                } else {
                    Toast.makeText(getContext(), "You are offline", Toast.LENGTH_LONG).show();
                }
                Toast.makeText(getActivity(), "Đã click", Toast.LENGTH_SHORT).show();
            }
        });
//        String key=null;
//        if(searchByFood){
//            key=packageFoods.get(position).getLocaID();
//        }else{
//            key = packageLocations.get(position).getLocaID();
//
//        }
//        if(key!=null) {
//            Intent intent = new Intent(getActivity().getApplicationContext(), Adapter2Activity.class);
//            intent.putExtra(getResources().getString(R.string.fragment_CODE),
//                    getResources().getString(R.string.frag_locadetail_CODE));
//            MyLocation location = new MyLocation();
//            location.setLocaID(key);
//            ChooseLoca.getInstance().setLocation(location);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }else{
//            Toast.makeText(getContext(), "Lỗi r", Toast.LENGTH_SHORT).show();
//        }
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_mon:
                        searchByFood=true;
                        if(tinh!=""&&quan!=""){
                            getData(1);
                        }
                        else{
                            getData(0);
                        }
                        break;
                    case R.id.rb_quan:
                        searchByFood=false;
                        if(tinh!=""&&quan!=""){
                            getData(3);
                        }
                        else{
                            getData(2);
                        }
                        break;
                }
            }
        });
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener1(getActivity(), new RecyclerItemClickListener1.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String key = locaList.get(position).getLocaID();
                Intent intent = new Intent(getActivity().getApplicationContext(), Adapter2Activity.class);
                intent.putExtra(getResources().getString(R.string.fragment_CODE),
                        getResources().getString(R.string.frag_locadetail_CODE));
                MyLocation location=new MyLocation();
                location.setLocaID(key);
                ChooseLoca.getInstance().setLocation(location);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }));
        getData(2);
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        switch (v.getId()) {
            case R.id.frg_filter_txttinh:
                PickProvinceDialogFragment pickProvinceFrg = new PickProvinceDialogFragment();
                pickProvinceFrg.show(fm, "fragment_pickProvince");
                pickProvinceFrg.setOnPickProvinceListener(this);
                break;
            case R.id.frg_filter_txtquan:
                if (tinh.equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.txt_noChoseProvince), Toast.LENGTH_SHORT).show();
                } else {
                    PickDistrictDialogFragment pickDistrictFrg = new PickDistrictDialogFragment();
                    pickDistrictFrg.setWhatprovince(whatProvince);
                    pickDistrictFrg.show(fm, "fragment_pickDistrict");
                    pickDistrictFrg.setOnPickDistricListener(this);
                }
                break;

            case R.id.frg_filter_btnreset:
                tinh="";
                quan="";
                txt_quan.setText("Quận");
                txt_tinh.setText("Huyện");
                getData(3);
                break;

        }
    }

    @Override
    public void onPickDistrict(String district) {
        txt_quan.setText(district);
        quan = district;
        if(searchByFood){
            getData(1);
        }else{
            getData(3);
        }

    }

    @Override
    public void onPickProvince(String province, int position) {
        txt_tinh.setText(province);
        whatProvince = position ;
        tinh = province;

    }

    public void getData(final int type){
        searchList=new ArrayList<>();
        packageFoods=new ArrayList<>();
        packageLocations=new ArrayList<>();
        mProgressDialog = ProgressDialog.show(getActivity(),
                getResources().getString(R.string.txt_plzwait),
                "Đang load dữ liệu",
                true, false);

        foodMenuChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Food food=dataSnapshot.getValue(Food.class);
                food.setMonID(dataSnapshot.getKey());
                packageFoods.add(food);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        locaMenuChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MyLocation myLocation = dataSnapshot.getValue(MyLocation.class);
                myLocation.setLocaID(dataSnapshot.getKey());
                packageLocations.add(myLocation);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(type>1){
                    if(packageLocations.size()>0){
                        locaList=packageLocations;
                        mAdapter = new LocatlistFilter_rcyler_adapter(locaList,2);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(getContext(), "Không có quán ăn nào", Toast.LENGTH_SHORT).show();
                    }

                }else{
                        if(packageFoods.size()>0){
                            //foodAdapter=new FoodAdapter(getContext(),packageFoods);
                            //edt_content.setAdapter(foodAdapter);
                        }else{
                            Toast.makeText(getContext(), "Không có món ăn nào", Toast.LENGTH_SHORT).show();
                        }
                }


            mProgressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(type==0){
            dbRef.child(
                    getString(R.string.thucdon_CODE)).limitToLast(seemore)
                    .addChildEventListener(foodMenuChildEventListener);
            dbRef.child(
                    getString(R.string.locations_CODE)).limitToLast(seemore)
                    .addChildEventListener(locaMenuChildEventListener);
        }
        if(type==2){
            dbRef.child(
                    getString(R.string.locations_CODE)).limitToLast(seemore)
                    .addChildEventListener(locaMenuChildEventListener);
        }

        if(type==3) {
            dbRef.child(
                    getString(R.string.locations_CODE)).orderByChild("index").equalTo(tinh + "_" + quan).limitToLast(seemore)
                    .addChildEventListener(locaMenuChildEventListener);
        }
        if(type==1){
            dbRef.child(
                    getString(R.string.locations_CODE)).orderByChild("index").equalTo(tinh + "_" + quan).limitToLast(seemore)
                    .addChildEventListener(locaMenuChildEventListener);
            dbRef.child(
                    getString(R.string.thucdon_CODE)).orderByChild("index").equalTo(tinh + "_" + quan).limitToLast(seemore)
                    .addChildEventListener(foodMenuChildEventListener);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            return true;
        }
        return false;
    }
    class filterObject{

    }



}
