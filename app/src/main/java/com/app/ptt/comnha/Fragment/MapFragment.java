package com.app.ptt.comnha.Fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Interfaces.LocationFinderListener;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Utils.MyTool;
import com.app.ptt.comnha.Utils.PlaceAPI;
import com.app.ptt.comnha.Utils.PlaceAttribute;
import com.app.ptt.comnha.Utils.Storage;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class MapFragment extends Fragment implements View.OnClickListener,
        LocationFinderListener,
        PickLocationBottomSheetDialogFragment.onPickListener,
        PlaceSelectionListener {
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    private IntentFilter mIntentFilter;
    private static final String LOG = MapFragment.class.getSimpleName();
    private SupportMapFragment supportMapFragment;
    private ArrayList<Store> list;
    ChildEventListener locaListChildEventListener;
    DatabaseReference dbRef;
    int distance=5000,seeMore=20;
    PlaceAPI placeAPI;
    //private ArrayList<String> listName = new ArrayList<>();
    ArrayList<PlaceAttribute> placeAttributes;
    PlaceAttribute myLocationSearch = null;
    TextView txt_TenQuan, txt_DiaChi, txt_GioMo, txt_DiemGia, txt_DiemPhucVu, txt_DiemVeSinh, txt_KhoangCach;
    GoogleMap myGoogleMap;
    Store yourLocation;
    Store customLocation;
    MyTool myTool;
    int pos = -1, option = 1;
    boolean isNearest = false;
    int temp = 1;
    MarkerOptions yourMarker = null;
    ImageButton btn_search;

    NetworkChangeReceiver mBroadcastReceiver;
    FloatingActionButton fab_filter, fab_location, fab_refresh;
    CardView card_pickProvince, card_pickDistrict, card_filterlabel, card_mylocation;
    TextView txt_tinh, txt_huyen, txt_filterLabel;
    PickLocationBottomSheetDialogFragment pickLocationDialog;
    FragmentManager fm;
    int whatProvince = -1;
    int custom=0;
    String tinh, huyen,diachi;
    ProgressDialog progressDialog;
    Boolean isConnected = false;
    PlaceAutocompleteFragment _autocompleteFragment;
    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG, "onCreate");
        list = new ArrayList<>();

        myTool = new MyTool(getContext());
        // myTool.startGoogleApi();
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG, "onResume");
    }
    public void setLocation(Store location){
        customLocation=location;
       if(customLocation!=null){
           custom=1;
       }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(LOG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.frg_map_fablocation);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_path));
        anhxa(view);
        return view;
    }

    private void anhxa(View view) {
        card_mylocation = (CardView) view.findViewById(R.id.frg_map_cardV_mylocation);
        fab_refresh = (FloatingActionButton) view.findViewById(R.id.frg_map_fabrefresh);
        //btn_search = (ImageButton) view.findViewById(R.id.frg_map_btnsearch);
        pickLocationDialog = new PickLocationBottomSheetDialogFragment();
        fm = getActivity().getSupportFragmentManager();
        card_pickProvince = (CardView) view.findViewById(R.id.frg_map_cardV_chonProvince);
        card_pickDistrict = (CardView) view.findViewById(R.id.frg_map_cardV_chonDistrict);
        card_filterlabel = (CardView) view.findViewById(R.id.frg_map_cardV_filterLabel);
        txt_huyen = (TextView) view.findViewById(R.id.frg_map_txtDistrict);
        txt_tinh = (TextView) view.findViewById(R.id.frg_map_txtProvince);
        txt_filterLabel = (TextView) view.findViewById(R.id.frg_map_txtfilterLabel);

        fab_filter = (FloatingActionButton) view.findViewById(R.id.frg_map_fabfilter);
        fab_location = (FloatingActionButton) view.findViewById(R.id.frg_map_fablocation);
         _autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity()
                        .getFragmentManager()
                        .findFragmentById(R.id.place_autocomplete_fragment);
        _autocompleteFragment.setOnPlaceSelectedListener(this);
//        btn_search.setOnClickListener(this);
        fab_filter.setOnClickListener(this);
        fab_location.setOnClickListener(this);
        fab_refresh.setOnClickListener(this);
        card_pickDistrict.setOnClickListener(this);
        card_pickProvince.setOnClickListener(this);
        card_mylocation.setOnClickListener(this);
        pickLocationDialog.setOnPickListener(this);

    }

    @Override
    public void onStart() {
        Log.i(LOG, "onStart");
        super.onStart();
        isConnected= MyService.returnIsNetworkConnected();
        if(!isConnected){
            Toast.makeText(getContext(),"Offline mode",Toast.LENGTH_SHORT).show();
        }
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
        mBroadcastReceiver = new NetworkChangeReceiver();
        getActivity().registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.frg_map_cardV_mylocation:
                if (isConnected) {
                    if (myLocationSearch != null && isNearest) {
                        Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_location_black_24dp);
                        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
//                        yourMarker = new MarkerOptions()
//                                .position(new LatLng(yourLocation.getLat(), yourLocation.getLng()))
//                                .title(yourLocation.getDiachi())
//                                .icon(markerIcon);
                        myGoogleMap.addMarker(yourMarker);
                    }
//                    if (yourLocation != null)
//                        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(yourLocation.getLat(), yourLocation.getLng()), 13));
                } else {
                    Toast.makeText(getContext(), "You are offline", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.frg_map_fabrefresh:
                if (card_pickDistrict.getTranslationY() == 0
                        && card_pickProvince.getTranslationX() == 0) {
                    AnimationUtils.animatHideTagMap(card_pickProvince, card_pickDistrict);
                }
                if (card_filterlabel.getTranslationX() == 0) {
                    AnimationUtils.animatHideTagMap2(card_filterlabel);
                }
                reloadMap();
                break;
            case R.id.frg_map_fabfilter:
                if (isConnected) {
                    PopupMenu popupMenu = new PopupMenu(getActivity(), fab_filter, Gravity.TOP | Gravity.END);
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu_viewquan, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.popup_viewquan_none:
                                    if (card_filterlabel.getTranslationX() == 0) {
                                        AnimationUtils.animatHideTagMap2(card_filterlabel);
                                    }
                                    txt_filterLabel.setText(item.getTitle());
                                    option = 1;
                                        getDataInFireBase(1);
                                    break;
                                case R.id.popup_viewquan_gia:
                                    if (card_filterlabel.getTranslationX() != 0) {
                                        AnimationUtils.animatShowTagMap2(card_filterlabel);
                                    }
                                    txt_filterLabel.setText(item.getTitle());


                                    getDataInFireBase(3);
                                    break;
                                case R.id.popup_viewquan_pv:
                                    if (card_filterlabel.getTranslationX() != 0) {
                                        AnimationUtils.animatShowTagMap2(card_filterlabel);
                                    }
                                    txt_filterLabel.setText(item.getTitle());
                                        getDataInFireBase(4);
                                    break;
                                case R.id.popup_viewquan_vs:
                                    if (card_filterlabel.getTranslationX() != 0) {
                                        AnimationUtils.animatShowTagMap2(card_filterlabel);
                                    }
                                    txt_filterLabel.setText(item.getTitle());

                                        getDataInFireBase(5);
                                    break;

                            }

                            return true;
                        }
                    });
                    popupMenu.show();
                } else {
                    Toast.makeText(getContext(), "You are offline", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.frg_map_fablocation:

                    if (card_pickDistrict.getTranslationY() == 0
                            && card_pickProvince.getTranslationX() == 0) {
//                    Log.i("transi", "pro: " + card_pickProvince.getTranslationX()
//                            + " dis: " + card_pickDistrict.getTranslationY());
                        AnimationUtils.animatHideTagMap(card_pickProvince, card_pickDistrict);
                    } else {
                        AnimationUtils.animatShowTagMap(card_pickProvince, card_pickDistrict);
                    }
                break;
            case R.id.frg_map_cardV_chonProvince:
                pickLocationDialog.show(fm, "pickProvinceDialog");
                break;
            case R.id.frg_map_cardV_chonDistrict:
                if (whatProvince >= 0) {
                    Log.i("province", whatProvince + "");
                    pickLocationDialog.setWhatProvince(whatProvince);
                    pickLocationDialog.show(fm, "pickDistrictDialog");
                } else {
                    Toast.makeText(getActivity(), getString(R.string.txt_noChoseProvince), Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }

    private void search() {
        if (isConnected) {
             {
                if (isNearest)
                    isNearest = false;
                 {
                    Log.i(LOG + ".onClick ", "loadListPlace" + pos);
                    if (diachi != null) {
                        placeAPI = new PlaceAPI(diachi, this);
                    }
                 }
            }
        } else {
            Toast.makeText(getContext(), "You are offline", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPicProvince(String province, int position) {
        whatProvince = position;
        tinh = province;
        txt_tinh.setText(province);
    }

    @Override
    public void onPickDistrict(String district) {
        txt_huyen.setText(district);
        huyen = district;
        isNearest = false;
        if(isConnected) {
            if (tinh != null && huyen != null) {
                myGoogleMap.clear();
                addMarkerYourLocation();
                getDataInFireBase(6);
            }
        }else{
            Toast.makeText(getContext(),"You are offline",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onStop() {
        Log.i(LOG, "onStop");
        super.onStop();
        getActivity().unregisterReceiver(mBroadcastReceiver);

    }
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(LOG, "onViewCreated");
        
        //progressDialog.show();
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapwhere);
        if (supportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            supportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.mapwhere, supportMapFragment).commit();
        }
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    myGoogleMap = googleMap;
                    isConnected= MyService.returnIsNetworkConnected();
                    ArrayList<Store> locations;
                    String a = Storage.readFile(getContext(), "myLocation");
                    if (a != null) {
                        locations = Storage.readJSONMyLocation(a);
                        if (locations.size() > 0)
                            yourLocation = locations.get(0);
                        else {
                            yourLocation = null;
                        }
                        if (yourLocation != null) {
                            if(custom==0){
                                getDataInFireBase(1);
                            }
                            else{
                                googleMap.clear();
                                addMarkerYourLocation();
                                if(custom==1){
//                                    float kc = (float) myTool.getDistance(new LatLng(yourLocation.getLat(), yourLocation.getLng()), new LatLng(customLocation.getLat(),customLocation.getLng()));
//                                    int c = Math.round(kc);
//                                    int d = c / 1000;
//                                    int e = c % 1000;
//                                    int f = e / 100;
//                                    customLocation.setKhoangcach(d + "," + f +"km");
                                }
                                list=new ArrayList<Store>();
                                list.add(customLocation);
                                addMarker(customLocation);
                                custom=0;
                            }

                        }
                    }
                        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            //myTool.startGoogleApi();
                            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            myGoogleMap.getUiSettings().setZoomControlsEnabled(false);
                            myGoogleMap.getUiSettings().setCompassEnabled(true);
                            myGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                            myGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
                            myGoogleMap.getUiSettings().setScrollGesturesEnabled(true);
                            myGoogleMap.getUiSettings().setTiltGesturesEnabled(true);
                            myGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
                            myGoogleMap.setTrafficEnabled(true);
                            myGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                                @Override
                                public void onCameraMove() {
                                    if (card_pickDistrict.getTranslationY() == 0
                                            && card_pickProvince.getTranslationX() == 0) {
                                        AnimationUtils.animatHideTagMap(card_pickProvince, card_pickDistrict);
                                    }
                                    if (card_filterlabel.getTranslationX() == 0) {
                                        AnimationUtils.animatHideTagMap2(card_filterlabel);
                                    }
                                }
                            });

                            myGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {
//                                    if ((marker.getPosition().latitude != yourLocation.getLat())
//                                            && (marker.getPosition().longitude != yourLocation.getLng())) {
//                                        if (myLocationSearch == null || (myLocationSearch != null
//                                                && (marker.getPosition().latitude != myLocationSearch.getPlaceLatLng().latitude
//                                                && (marker.getPosition().longitude != myLocationSearch.getPlaceLatLng().longitude)))) {
//                                            Store a = returnLocation(marker);
//
//                                                if (a != null && a.getQuanhuyen() != null && a.getStoreID() != null && a.getTinhtp() != null) {
//                                                    Intent intent = new Intent(getActivity().getApplicationContext(), Adapter2Activity.class);
//                                                    intent.putExtra(getResources().getString(R.string.fragment_CODE),
//                                                            getResources().getString(R.string.frag_locadetail_CODE));
//
//
//                                                    ChooseLoca.getInstance().setStore(a);
//
//                                                    if(!isConnected) {
//                                                        ChooseLoca.getInstance().setInfo("listLocation" + 1 + "_" + tinh + "_" + huyen);
//                                                        Log.i(LOG + ".anhxa", "listLocation" + 1 + "_" + tinh + "_" + huyen);
//                                                    }
//                                                    else
//                                                        ChooseLoca.getInstance().setInfo("");
//                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                    startActivity(intent);
//                                                }
//                                        }
//                                    }
                                }
                            });
                            myGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                }
                            });
                            myGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                @Override
                                public View getInfoWindow(Marker marker) {
//                                    Log.i(LOG + ".listSize", list.size() + "");
//                                    if ((marker.getPosition().latitude == yourLocation.getLat())
//                                            && (marker.getPosition().longitude == yourLocation.getLng())) {
//                                        View view1 = getLayoutInflater(savedInstanceState).inflate(R.layout.infowindow_your_location, null);
//                                        txt_DiaChi = (TextView) view1.findViewById(R.id.txt_DiaChi);
//                                        txt_DiaChi.setText(yourLocation.getDiachi());
//                                        Log.i(LOG + ".infoWindow", "your location");
//                                        return view1;
//                                    } else if (myLocationSearch != null) {
//                                        Log.i(LOG + ".infoWindow", "location choose:" + myLocationSearch.getFullname());
//                                        if (
//                                                marker.getPosition().latitude == myLocationSearch.getPlaceLatLng().latitude
//                                                        && marker.getPosition().longitude == myLocationSearch.getPlaceLatLng().longitude) {
//                                            View view1 = getLayoutInflater(savedInstanceState).inflate(R.layout.infowindow_your_location, null);
//                                            txt_DiaChi = (TextView) view1.findViewById(R.id.txt_DiaChi);
//                                            txt_DiaChi.setText(" Vị trí bạn chọn");
//                                            return view1;
//                                        } else {
//                                            View view1 = getLayoutInflater(savedInstanceState).inflate(R.layout.infowindowlayout, null);
//                                            txt_TenQuan = (TextView) view1.findViewById(R.id.txt_TenQuan);
//                                            txt_DiaChi = (TextView) view1.findViewById(R.id.txt_DiaChi);
//                                            txt_GioMo = (TextView) view1.findViewById(R.id.txt_GioMo);
//                                            txt_DiemGia = (TextView) view1.findViewById(R.id.txt_DiemGia);
//                                            txt_DiemPhucVu = (TextView) view1.findViewById(R.id.txt_DiemPhucVu);
//                                            txt_DiemVeSinh = (TextView) view1.findViewById(R.id.txt_DiemVeSinh);
//                                            txt_KhoangCach = (TextView) view1.findViewById(R.id.txt_KhoangCach);
//
//                                            Store a = returnLocation(marker);
//                                            if (a != null) {
//                                                txt_TenQuan.setText(a.getName());
//                                                txt_DiaChi.setText(a.getDiachi());
//                                                txt_GioMo.setText(a.getTimestart() + "-" + a.getTimeend());
//                                                float kc = (float) myTool.getDistance(new LatLng(myLocationSearch.getPlaceLatLng().latitude, myLocationSearch.getPlaceLatLng().longitude), new LatLng(a.getLat(), a.getLng()));
//                                                int c = Math.round(kc);
//                                                int d = c / 1000;
//                                                int e = c % 1000;
//                                                int f = e / 100;
//                                                txt_KhoangCach.setText(d + "," + f);
//                                                if (a.getSize() == 0) {
//                                                    txt_DiemVeSinh.setText("0");
//                                                    txt_DiemGia.setText("0");
//                                                    txt_DiemPhucVu.setText("0");
//                                                } else {
//                                                    txt_DiemVeSinh.setText(a.getVsTong() / a.getSize() + "");
//                                                    txt_DiemGia.setText(a.getGiaTong() / a.getSize() + "");
//                                                    txt_DiemPhucVu.setText(a.getPvTong() / a.getSize() + "");
//                                                }
//                                            } else
//
//                                                Log.i(LOG + ".infoWindow", "a=null");
//                                            return view1;
//                                        }
//                                    } else {
//                                        return getInfoWindowOfMarker(marker, savedInstanceState);
//                                    }
                                    return getInfoWindowOfMarker(marker, savedInstanceState) ;
                                }

                                @Override
                                public View getInfoContents(Marker marker) {
                                    return null;
                                }
                            });
                        }
                    });


                }

            });
        }
    }


    public View getInfoWindowOfMarker(Marker marker, Bundle savedInstanceState) {
        View view1 = getLayoutInflater(savedInstanceState).inflate(R.layout.infowindowlayout, null);
        txt_TenQuan = (TextView) view1.findViewById(R.id.txt_TenQuan);
        txt_DiaChi = (TextView) view1.findViewById(R.id.txt_DiaChi);
        txt_GioMo = (TextView) view1.findViewById(R.id.txt_GioMo);
        txt_DiemGia = (TextView) view1.findViewById(R.id.txt_DiemGia);
        txt_DiemPhucVu = (TextView) view1.findViewById(R.id.txt_DiemPhucVu);
        txt_DiemVeSinh = (TextView) view1.findViewById(R.id.txt_DiemVeSinh);
        txt_KhoangCach = (TextView) view1.findViewById(R.id.txt_KhoangCach);
        Store a = returnLocation(marker);
        if (a != null) {
//            Log.i(LOG + ".infoWindow", a.getDiachi() + " : " + a.getKhoangcach());
//            txt_TenQuan.setText(a.getName());
//            txt_DiaChi.setText(a.getDiachi());
//            txt_GioMo.setText(a.getTimestart() + "-" + a.getTimeend());
//            txt_KhoangCach.setText(a.getKhoangcach());
//            if (a.getSize() == 0) {
//                txt_DiemVeSinh.setText("0");
//                txt_DiemGia.setText("0");
//                txt_DiemPhucVu.setText("0");
//            } else {
//                txt_DiemVeSinh.setText(a.getVsTong() / a.getSize() + "");
//                txt_DiemGia.setText(a.getGiaTong() / a.getSize() + "");
//                txt_DiemPhucVu.setText(a.getPvTong() / a.getSize() + "");
//            }
        } else
            Log.i(LOG + ".infoWindow", "Không thể tìm được địa chỉ này");
        return view1;
    }

    public void reloadMap() {
        whatProvince = -1;
        txt_filterLabel.setText("");
        txt_tinh.setText("Tỉnh");
        txt_huyen.setText("Huyện");
        option = 1;
        isNearest = false;
        myLocationSearch = null;
        tinh = null;
        huyen =null;
        getDataInFireBase(1);
    }

    public void addMarkerCustomSearch() {
        Drawable circleDrawable = getResources().getDrawable(R.drawable.icon);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
        Log.i(LOG + ".changeLocation", myLocationSearch.getFullname());
        if(myLocationSearch.getPlaceLatLng()!=null) {
            yourMarker = new MarkerOptions()
                    .position(myLocationSearch.getPlaceLatLng())
                    .title(myLocationSearch.getFullname())
                    .icon(markerIcon);
        }
        myGoogleMap.addMarker(yourMarker);
        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocationSearch.getPlaceLatLng(), 13));

    }

    public void addMarker(Store store) {
//        Log.i(LOG + ".addMarker", "Them dia diem nhan duoc: " + selected_store.getDiachi());
//        LatLng locatioLatLng = new LatLng(selected_store.getLat(), selected_store.getLng());
//        myGoogleMap.addMarker(new MarkerOptions()
//                .position(locatioLatLng));
    }

    public void addMarkerYourLocation() {
        Drawable circleDrawable = getResources().getDrawable(R.drawable.icon);
//        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
//        yourMarker = new MarkerOptions()
//                .position(new LatLng(yourLocation.getLat(), yourLocation.getLng()))
//                .title(yourLocation.getDiachi())
//                .icon(markerIcon);
        myGoogleMap.addMarker(yourMarker);
//        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(yourLocation.getLat(), yourLocation.getLng()), 13));
    }
    public Store returnLocation(Marker marker) {
        Log.i(LOG + ".returnRoute", "Tra ve route ung voi marker");
//        if (list.size() > 0)
//            for (Store location : list) {
//                if (marker.getPosition().latitude == location.getLat()
//                        && marker.getPosition().longitude == location.getLng()) {
//                    return location;
//                }
//            }
        return null;
    }

    @Override
    public void onLocationFinderStart() {

    }

    @Override
    public void onLocationFinderSuccess(PlaceAttribute placeAttribute) {
        if (placeAttribute != null) {
            myLocationSearch = placeAttribute;
            tinh = placeAttribute.getState();
            huyen = placeAttribute.getDistrict();
            Log.i(LOG + ".onLocationFinder", "place:" + placeAttribute.getFullname());
            isNearest = true;
            Log.i(LOG + ".onClick ", "!=current quan huyen");
            getDataInFireBase(2);
            _autocompleteFragment.setText(placeAttribute.getFullname());
            //}
            pos = -1;
        } else {
            Toast.makeText(getActivity(),
                    "Không tìm được",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGeocodingFinderSuccess(String address) {

    }

    @Override
    public void onPlaceSelected(Place place) {
        diachi=place.getAddress().toString();
        _autocompleteFragment.setText(place.getAddress());
        search();

    }

    @Override
    public void onError(Status status) {
        Toast.makeText(getContext(), "error:"+status.getStatusMessage(), Toast.LENGTH_SHORT).show();
    }

    public void getDataInFireBase(int type) {
        myGoogleMap.clear();
        if (isNearest && myLocationSearch != null) {
            addMarkerCustomSearch();
        } else {
            if (yourLocation != null) {
                addMarkerYourLocation();
            }
        }
        if(type==1||type==2||type==6){
            list = new ArrayList<>();
            if (isConnected) {
                getData(type);
            }
        }
        //Gia
        if(type==3){
            for(Store location: list){
//                if(location.getGiaAVG()>6)
                    addMarker(location);
            }
        }
        //Phuc vu
        if(type==4){
            for(Store location: list){
//                if(location.getPvAVG()>6)
                    addMarker(location);
            }
        }
        //Ve sinh
        if(type==5){
            for(Store location: list){
//                if(location.getVsAVG()>6)
                    addMarker(location);
            }
        }


    }
    public void getData(int a){
        final int b=a;
        Log.i(LOG + ".getData", "OK");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Store newLocation = dataSnapshot.getValue(Store.class);
//                newLocation.setStoreID(dataSnapshot.getKey());
//                Log.i(LOG + ".getData", "newLocation.getVisible:"+newLocation.getVisible());
//                if(newLocation.getVisible()!=null){
//                    if(newLocation.getVisible()){
//                        if (isNearest&&myLocationSearch!=null) {
//                            //addMarkerCustomSearch();
//                            Log.i(LOG + ".onClick ", "isNearest && myLocationSearch != null");
//                            float kc = (float) myTool.getDistance(new LatLng(myLocationSearch.getPlaceLatLng().latitude, myLocationSearch.getPlaceLatLng().longitude), new LatLng(newLocation.getLat(), newLocation.getLng()));
//                            if(tinh!=null && huyen!=null){
//                                int c = Math.round(kc);
//                                int d = c / 1000;
//                                int e = c % 1000;
//                                int f = e / 100;
//                                newLocation.setKhoangcach(d + "," + f);
//                                list.add(newLocation);
//                                addMarker(newLocation);
//                                myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(newLocation.getLat(),newLocation.getLng()), 13));
//
//                            }else{
//                                if (kc < distance) {
//                                    int c = Math.round(kc);
//                                    int d = c / 1000;
//                                    int e = c % 1000;
//                                    int f = e / 100;
//                                    newLocation.setKhoangcach(d + "," + f);
//                                    list.add(newLocation);
//                                    addMarker(newLocation);
//                                    myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(newLocation.getLat(),newLocation.getLng()), 13));
//                                }
//                            }
//
//                        }
//                        else {
//                            Log.i(LOG + ".onClick ", "isNearest && myLocationSearch == null");
//                            float kc = (float) myTool.getDistance(new LatLng(yourLocation.getLat(), yourLocation.getLng()), new LatLng(newLocation.getLat(), newLocation.getLng()));
//                            if(tinh!=null && huyen!=null){
//                                int c = Math.round(kc);
//                                int d = c / 1000;
//                                int e = c % 1000;
//                                int f = e / 100;
//                                newLocation.setKhoangcach(d + "," + f+" km");
//                                list.add(newLocation);
//                                addMarker(newLocation);
//                                myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(newLocation.getLat(),newLocation.getLng()), 13));
//
//                            }else {
//                                if (kc < distance) {
//                                    int c = Math.round(kc);
//                                    int d = c / 1000;
//                                    int e = c % 1000;
//                                    int f = e / 100;
//                                    newLocation.setKhoangcach(d + "," + f+" km");
//                                    list.add(newLocation);
//                                    addMarker(newLocation);
//                                    myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(newLocation.getLat(), newLocation.getLng()), 13));
//                                }
//                            }
//                        }
//                    }
//                }



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

        if(b==6 && tinh!=null&&huyen!=null){
            Log.i(LOG + ".sad", "tinh huyen khac NULLLLLLLLLLLLLLL");
            dbRef.child(getString(R.string.store_CODE)).orderByChild("index")
                    .equalTo(tinh+"_"+huyen).limitToLast(seeMore)
                    .addChildEventListener(childEventListener);
        }else
        dbRef.child(getString(R.string.store_CODE)).limitToLast(seeMore)
                .addChildEventListener(childEventListener);
    }

    public void getDataOffline(String mTinh,String mHuyen){
        Log.i(LOG + ".getDataOffline", "OK");
        ArrayList<Store> locations;
        String a = Storage.readFile(getContext(), "listLocation" + 1+"_"+mTinh+"_"+mHuyen);
        if(a!=null) {
            locations = Storage.readJSONMyLocation(a);
            if(locations.size()>0) {
                for (Store location : locations) {
                    if (isNearest && myLocationSearch != null) {
                        Log.i(LOG + ".onClick ", "isNearest && myLocationSearch != null");
//                        float kc = (float) myTool.getDistance(new LatLng(myLocationSearch.getPlaceLatLng().latitude, myLocationSearch.getPlaceLatLng().longitude), new LatLng(location.getLat(), location.getLng()));
//                        if (kc < 5000) {
                            addMarker(location);
//                        }
                    } else {
//                        Log.i(LOG + ".onClick ", "isNearest && myLocationSearch == null:" + myTool.getDistance(new LatLng(yourLocation.getLat(), yourLocation.getLng()), new LatLng(location.getLat(), location.getLng())));
//                        float kc = (float) myTool.getDistance(new LatLng(yourLocation.getLat(), yourLocation.getLng()), new LatLng(location.getLat(), location.getLng()));
//                        int c = Math.round(kc);
//                        int d = c / 1000;
//                        int e = c % 1000;
//                        int f = e / 100;
//                        if(location.getKhoangcach()==null)
//                            location.setKhoangcach(d + "," + f);
                        addMarker(location);
                    }
                    list.add(location);
//                    myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLat(),location.getLng()), 13));
                }
            }else
                Toast.makeText(getContext(),"Không tìm thấy dữ liệu",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getContext(),"Không tìm thấy dữ liệu",Toast.LENGTH_LONG).show();
        }
    }

    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if(intent.getAction().equals(mBroadcastSendAddress)) {
                Log.i(LOG+".onReceive form Service","isConnected= "+ intent.getBooleanExtra("isConnected", false));
                isConnected = intent.getBooleanExtra("isConnected", false);
                ArrayList<Store> locations;
                String a = Storage.readFile(getContext(), "myLocation");
                if (a != null) {
                    locations = Storage.readJSONMyLocation(a);
                    if (locations.size() > 0)
                        yourLocation = locations.get(0);
                    else {
                        yourLocation = null;
                    }
                    if (yourLocation != null) {
                        getDataInFireBase(1);
                    }
                }
            }
        }
    }
}
