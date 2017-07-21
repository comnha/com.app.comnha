package com.app.ptt.comnha.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Activity.StoreDeatailActivity;
import com.app.ptt.comnha.Adapters.PlacesAutoCompleteAdapter;
import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Models.MyLocation;
import com.app.ptt.comnha.Modules.PlaceAttribute;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
import com.app.ptt.comnha.SingletonClasses.CoreManager;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.MyTool;
import com.app.ptt.comnha.Utils.PlaceAPI;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class MapFragment extends Fragment implements View.OnClickListener,
        PickLocationBottomSheetDialogFragment.onPickListener {
    public static final String TAG = MapFragment.class.getSimpleName();
    private IntentFilter mIntentFilter;
    View viewInfoWindow, viewInfoWindowYourLocation;
    private static final String LOG = MapFragment.class.getSimpleName();
    private SupportMapFragment supportMapFragment;
    private ArrayList<Store> list;
    DatabaseReference dbRef;
    int distance = 5000, seeMore = 20;
    PlaceAPI placeAPI;
    PlaceAttribute myLocationSearch = null;
    TextView txt_TenQuan, txt_DiaChi, txt_GioMo, txt_DiemGia, txt_DiemPhucVu, txt_DiemVeSinh, txt_KhoangCach, txt_Your_Location, txt_Title;
    private CircleImageView imgMarker;
    private ImageView btnSearch;
    GoogleMap myGoogleMap;
    MyLocation yourLocation;
    Store customLocation;
    MyTool myTool;
    int pos = -1, option = 1;
    MarkerOptions yourMarker = null;
    AutoCompleteTextView edtSearch;

    FloatingActionButton fab_filter, fab_location, fab_refresh;
    CardView card_pickProvince, card_pickDistrict, card_filterlabel, card_mylocation, card_distance;
    TextView txt_tinh, txt_huyen, txt_filterLabel, txt_distance;
    PlacesAutoCompleteAdapter placesAutoCompleteAdapter;
    PickLocationBottomSheetDialogFragment pickLocationDialog;
    FragmentManager fm;
    int whatProvince = -1;
    StorageReference stRef;
    int custom = 0;
    String tinh, huyen;
    Boolean isConnected = false;
    int sortType = -1;

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
        stRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Const.STORAGE_PATH);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG, "onResume");
    }

    public void setLocation(Store location) {
        customLocation = location;
        if (customLocation != null) {
            custom = 1;
        }else{
            custom=0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(LOG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Const.DATABASE_PATH);
        anhxa(view);

        viewInfoWindow = getLayoutInflater(savedInstanceState).inflate(R.layout.infowindowlayout, null);
        txt_TenQuan = (TextView) viewInfoWindow.findViewById(R.id.txt_TenQuan);
        txt_DiaChi = (TextView) viewInfoWindow.findViewById(R.id.txt_DiaChi);
        txt_GioMo = (TextView) viewInfoWindow.findViewById(R.id.txt_GioMo);
        txt_DiemGia = (TextView) viewInfoWindow.findViewById(R.id.txt_DiemGia);
        txt_DiemPhucVu = (TextView) viewInfoWindow.findViewById(R.id.txt_DiemPhucVu);
        txt_DiemVeSinh = (TextView) viewInfoWindow.findViewById(R.id.txt_DiemVeSinh);
        txt_KhoangCach = (TextView) viewInfoWindow.findViewById(R.id.txt_KhoangCach);
        imgMarker = (CircleImageView) viewInfoWindow.findViewById(R.id.imageView2);
        viewInfoWindowYourLocation = getLayoutInflater(savedInstanceState).inflate(R.layout.infowindow_your_location, null);
        txt_Your_Location = (TextView) viewInfoWindowYourLocation.findViewById(R.id.txt_DiaChi);
        txt_Title = (TextView) viewInfoWindowYourLocation.findViewById(R.id.txt_TenQuan);
        return view;
    }

    private void anhxa(View view) {
        card_mylocation = (CardView) view.findViewById(R.id.frg_map_cardV_mylocation);
        fab_refresh = (FloatingActionButton) view.findViewById(R.id.frg_map_fabrefresh);
        btnSearch = (ImageView) view.findViewById(R.id.btn_search);
        pickLocationDialog = new PickLocationBottomSheetDialogFragment();
        fm = getActivity().getSupportFragmentManager();
        card_pickProvince = (CardView) view.findViewById(R.id.frg_map_cardV_chonProvince);
        card_pickDistrict = (CardView) view.findViewById(R.id.frg_map_cardV_chonDistrict);
        card_distance = (CardView) view.findViewById(R.id.frg_map_cardV_distance);
        card_filterlabel = (CardView) view.findViewById(R.id.frg_map_cardV_filterLabel);
        txt_huyen = (TextView) view.findViewById(R.id.frg_map_txtDistrict);
        txt_tinh = (TextView) view.findViewById(R.id.frg_map_txtProvince);
        txt_filterLabel = (TextView) view.findViewById(R.id.frg_map_txtfilterLabel);
        txt_distance = (TextView) view.findViewById(R.id.txt_distance);
        txt_distance.setText("5 km");
        distance = 5 * 1000;
        fab_filter = (FloatingActionButton) view.findViewById(R.id.frg_map_fabfilter);
        fab_location = (FloatingActionButton) view.findViewById(R.id.frg_map_fablocation);

        edtSearch = (AutoCompleteTextView) view.findViewById(R.id.edt_search);
        setupAutoCompleteTextView();
        btnSearch.setOnClickListener(this);
        edtSearch.setOnClickListener(this);
        fab_filter.setOnClickListener(this);
        fab_location.setOnClickListener(this);
        fab_refresh.setOnClickListener(this);
        card_distance.setOnClickListener(this);
        card_pickDistrict.setOnClickListener(this);
        card_pickProvince.setOnClickListener(this);
        card_mylocation.setOnClickListener(this);
        pickLocationDialog.setOnPickListener(this);

    }

    private void setupAutoCompleteTextView() {
        placesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getContext(), R.layout.autocomplete_list_item, myTool);
        edtSearch.setAdapter(placesAutoCompleteAdapter);
        edtSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                myLocationSearch = placesAutoCompleteAdapter.getItemOfList(position);
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())) {
                    btnSearch.setImageResource(R.drawable.ic_close_50black_24dp);
                } else {
                    btnSearch.setImageResource(R.drawable.ic_search_50black_24dp);
                    myLocationSearch=null;
                }
                txt_distance.setText( distance/1000+" km");
                card_distance.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    getDataInFireBase(2);
                    handled = true;
                }
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view = getActivity().getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = new View(getActivity());
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                return handled;
            }
        });

    }

    @Override
    public void onStart() {
        Log.i(LOG, "onStart");
        super.onStart();
        isConnected = MyService.isNetworkAvailable(getActivity());
        if (!isConnected) {
            Toast.makeText(getContext(), "Offline mode", Toast.LENGTH_SHORT).show();
        }
        mIntentFilter = new IntentFilter();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_map_cardV_mylocation:
                if (isConnected) {
                    if (myLocationSearch != null) {
                        Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_location_black_24dp);
                        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                        yourMarker = new MarkerOptions()
                                .position(new LatLng(yourLocation.getLat(), yourLocation.getLng()))
                                .title(yourLocation.getAddress())
                                .icon(markerIcon);
                        myGoogleMap.addMarker(yourMarker);
                    }

                    if (yourLocation != null)
                        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(yourLocation.getLat(), yourLocation.getLng()), 13));
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
                                case R.id.popup_viewquan_all:
                                    if (card_filterlabel.getTranslationX() == 0) {
                                        AnimationUtils.animatHideTagMap2(card_filterlabel);
                                    }
                                    txt_filterLabel.setText(item.getTitle());
                                    option = 1;
                                    if (myLocationSearch != null) {
                                        getDataInFireBase(2);
                                    } else {
                                        getDataInFireBase(7);
                                    }
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
                            card_distance.setEnabled(true);
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
                    AnimationUtils.animatHideTagMap(card_pickProvince, card_pickDistrict);
                } else {
                    AnimationUtils.animatShowTagMap(card_pickProvince, card_pickDistrict);
                }
                break;
            case R.id.frg_map_cardV_distance:
                card_distance.setEnabled(true);
                Dialog a = onCreateDialog();
                a.show();
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
            case R.id.btn_search:
                if (!TextUtils.isEmpty(edtSearch.getText().toString())) {
                    edtSearch.setText("");
                }
                break;

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

        if (isConnected) {
            if (tinh != null && huyen != null) {

                myGoogleMap.clear();
                getDataInFireBase(6);
                edtSearch.setText("");
                txt_distance.setText("- km");
                card_distance.setEnabled(false);
            }
        } else {
            Toast.makeText(getContext(), "You are offline", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onStop() {
        Log.i(LOG, "onStop");
        super.onStop();


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
                    MyLocation a = CoreManager.getInstance().getMyLocation();
                    if (a != null) {
                        yourLocation = a;
                        if (yourLocation != null) {
                            addMarkerYourLocation();
                        }
                        getData();
                    }
                    googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                                    if ((marker.getPosition().latitude != yourLocation.getLat())
                                            && (marker.getPosition().longitude != yourLocation.getLng())) {
                                        if (myLocationSearch == null || (myLocationSearch != null
                                                && (marker.getPosition().latitude != myLocationSearch.getPlaceLatLng().latitude
                                                && (marker.getPosition().longitude != myLocationSearch.getPlaceLatLng().longitude)))) {
                                            Store a = returnLocation(marker);

                                                if (a != null && a.getDistrict() != null && a.getStoreID() != null && a.getProvince() != null) {
                                                    Intent intent = new Intent(getActivity().getApplicationContext(), StoreDeatailActivity.class);
                                                    Bitmap imgBitmap = ((BitmapDrawable) imgMarker.getDrawable())
                                                            .getBitmap();
                                                    a.setImgBitmap(imgBitmap);
                                                    ChooseStore.getInstance().setStore(a);

                                                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }
                                        }
                                    }
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
                                    Log.i(LOG + ".listSize", list.size() + "");
                                    if ((marker.getPosition().latitude == yourLocation.getLat())
                                            && (marker.getPosition().longitude == yourLocation.getLng())) {
                                        txt_Title.setText(" Vị trí của bạn");
                                        txt_Your_Location.setText(yourLocation.getAddress());
                                        return viewInfoWindowYourLocation;
                                    } else if (myLocationSearch != null) {
                                        if (marker.getPosition().latitude == myLocationSearch.getPlaceLatLng().latitude
                                                && marker.getPosition().longitude == myLocationSearch.getPlaceLatLng().longitude) {
                                            txt_Title.setText(" Vị trí bạn chọn");
                                            txt_Your_Location.setText("");
                                            return viewInfoWindowYourLocation;
                                        } else {
                                            Store a = returnLocation(marker);
                                            if (a != null) {
                                                txt_TenQuan.setText(a.getName());
                                                txt_DiaChi.setText(a.getAddress());
                                                txt_GioMo.setText(a.getOpentime());

                                                txt_KhoangCach.setText(a.getDistance());
                                                if (a.getSize() == 0) {
                                                    txt_DiemVeSinh.setText("0");
                                                    txt_DiemGia.setText("0");
                                                    txt_DiemPhucVu.setText("0");
                                                } else {
                                                    txt_DiemVeSinh.setText(a.getHealthySum() + "");
                                                    txt_DiemGia.setText(a.getPriceSum() + "");
                                                    txt_DiemPhucVu.setText(a.getServiceSum() + "");
                                                }
                                                if(null!=a.getImgBitmap()){
                                                    imgMarker.setImageBitmap(a.getImgBitmap());
                                                }else {
                                                    if (!a.getStoreimg().equals("")) {
                                                        StorageReference imgRef = stRef.child(a
                                                                .getStoreimg());
                                                        Log.d("Imgpath", imgRef.getDownloadUrl() + "");
                                                        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                Log.d("getUrl().addOnSuccess", uri.toString() + "");
                                                                Picasso.with(getActivity())
                                                                        .load(uri)
                                                                        .into(imgMarker);
                                                            }
                                                        });
                                                    } else {
                                                        imgMarker.setImageResource(R.drawable.ic_item_store);
                                                    }
                                                }
                                            } else {
                                                return null;
                                            }
                                            return viewInfoWindow;
                                        }
                                    } else {
                                        return getInfoWindowOfMarker(marker);
                                    }
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


    public View getInfoWindowOfMarker(Marker marker) {


        Store a;
        if(customLocation==null){
            a= returnLocation(marker);
        }else{
            a=customLocation;
            card_distance.setEnabled(false);
        }
        if (a != null) {
            Log.i(LOG + ".infoWindow", a.getAddress() + " : " + a.getDistance());
            txt_TenQuan.setText(a.getName());
            txt_DiaChi.setText(a.getAddress());
            txt_GioMo.setText(a.getOpentime());
            txt_KhoangCach.setText(a.getDistance());
            if (a.getSize() == 0) {
                txt_DiemVeSinh.setText("0");
                txt_DiemGia.setText("0");
                txt_DiemPhucVu.setText("0");
            } else {
                txt_DiemVeSinh.setText(a.getHealthySum() + "");
                txt_DiemGia.setText(a.getPriceSum() + "");
                txt_DiemPhucVu.setText(a.getServiceSum() + "");
            }
            int width = getPixelFromDimen(getActivity(), R.dimen.image_size);
            if( TextUtils.isEmpty(a.getStoreimg())){
                Picasso.with(getActivity()).load(R.mipmap.ic_launcher).resize(width, width).into(imgMarker);
            }else {
                Picasso.with(getActivity()).load(a.getStoreimg()).error(R.mipmap.ic_launcher).resize(width, width).into(imgMarker);
            }
        } else
            Log.i(LOG + ".infoWindow", "Không thể tìm được địa chỉ này");
        return viewInfoWindow;
    }

    public int getPixelFromDimen(Context context, int dimenRes) {
        int valueInPixels = (int) context.getResources().getDimension(dimenRes);
        return valueInPixels;
    }

    public void reloadMap() {
        whatProvince = -1;
        txt_filterLabel.setText("");
        txt_tinh.setText("Tỉnh");
        txt_huyen.setText("Huyện");
        edtSearch.setText("");
        option = 1;
        myLocationSearch = null;
        tinh = null;
        huyen = null;
        getDataInFireBase(1);
        sortType = -1;
        txt_distance.setText(distance/1000 +" km");
        card_distance.setEnabled(true);
    }

    public void addMarkerCustomSearch() {
        Drawable circleDrawable = getResources().getDrawable(R.drawable.icon);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
        Log.i(LOG + ".changeLocation", myLocationSearch.getFullname());
        if (myLocationSearch.getPlaceLatLng() != null) {
            yourMarker = new MarkerOptions()
                    .position(myLocationSearch.getPlaceLatLng())
                    .title(myLocationSearch.getFullname())
                    .icon(markerIcon);
        }
        myGoogleMap.addMarker(yourMarker);
        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocationSearch.getPlaceLatLng(), 13));

    }

    public void addMarker(Store store) {
        Log.i(LOG + ".addMarker", "Them dia diem nhan duoc: " + store.getAddress());
        LatLng locatioLatLng = new LatLng(store.getLat(), store.getLng());
        myGoogleMap.addMarker(new MarkerOptions()
                .position(locatioLatLng));
    }

    public void addMarkerYourLocation() {
        Drawable circleDrawable = getResources().getDrawable(R.drawable.icon);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
        yourMarker = new MarkerOptions()
                .position(new LatLng(yourLocation.getLat(), yourLocation.getLng()))
                .title(yourLocation.getAddress())
                .icon(markerIcon);
        myGoogleMap.addMarker(yourMarker);
        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(yourLocation.getLat(), yourLocation.getLng()), 13));

    }

    public Store returnLocation(Marker marker) {
        Log.i(LOG + ".returnRoute", "Tra ve route ung voi marker");
        if (list.size() > 0)
            for (Store location : list) {
                if (marker.getPosition().latitude == location.getLat()
                        && marker.getPosition().longitude == location.getLng()) {
                    return location;
                }
            }
        return null;
    }

    public void getDataInFireBase(int type) {
        sortType = type;
        myGoogleMap.clear();
        double lat=0,lng=0;
        float kc = 0;
        if (myLocationSearch != null) {
            addMarkerCustomSearch();
            lat=myLocationSearch.getPlaceLatLng().latitude;
            lng=myLocationSearch.getPlaceLatLng().longitude;
        } else {
            if (yourLocation != null) {
                addMarkerYourLocation();
                lat=yourLocation.getLat();
                lng=yourLocation.getLng();
            }
        }
        for (Store newLocation : list) {
            kc = (float) myTool.distanceFrom_in_Km(lat, lng, newLocation.getLat(), newLocation.getLng());
            int c = Math.round(kc);
            int d = c / 1000;
            int e = c % 1000;
            int f = e / 100;
            //sort = tinh + huyen
            if (type == 6 ||(tinh!=null &&huyen!=null &&
                    ( type==3 ||type==4||type==5||type==2||type==7))) {
                if (tinh.equals(newLocation.getProvince())
                        && huyen.equals(newLocation.getDistrict())) {
                    //gia
                    if (type == 3) {
                        if (newLocation.getPriceSum() >= 6)
                            addMarker(newLocation);
                    } else {
                        //Phuc vu
                        if (type == 4) {
                            if (newLocation.getServiceSum() >= 6)
                                addMarker(newLocation);
                        } else {
                            //Ve sinh
                            if (type == 5) {
                                if (newLocation.getHealthySum() >= 6)
                                    addMarker(newLocation);
                            } else {
                                //all
                                    newLocation.setDistance(d + "," + f + " km");
                                    addMarker(newLocation);
                            }
                        }
                    }
                    myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(newLocation.getLat(), newLocation.getLng()), 13));
                }
            } else {
                txt_distance.setText( distance/1000+" km");
                card_distance.setEnabled(true);
                if (kc < distance) {
                    //Gia
                    if (type == 3) {
                        if (newLocation.getPriceSum() >= 6)
                            addMarker(newLocation);
                    } else {
                        //Phuc vu
                        if (type == 4) {
                            if (newLocation.getServiceSum() >= 6)
                                addMarker(newLocation);
                        } else {
                            //Ve sinh
                            if (type == 5) {
                                if (newLocation.getHealthySum() >= 6)
                                    addMarker(newLocation);
                            } else {
                                newLocation.setDistance(d + "," + f + " km");
                                addMarker(newLocation);
                            }
                        }
                    }


                }
            }
        }


    }

    public void getData() {
        sortType=1;
        Log.i(LOG + ".getData", "OK");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Store newLocation = dataSnapshot.getValue(Store.class);
                newLocation.setStoreID(dataSnapshot.getKey());
                if (!TextUtils.isEmpty(newLocation.getStoreimg())) {
                    StorageReference imgRef = stRef.child(newLocation.getStoreimg());
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("getUrl().addOnSuccess", uri.toString() + "");
                            newLocation.setStoreimg(uri.toString());
                        }
                    });
                }
                list.add(newLocation);
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
        if (LoginSession.getInstance().getUser()!=null && LoginSession.getInstance().getUser().getRole() != 0) {
            dbRef.child(getString(R.string.store_CODE)).limitToLast(seeMore)
                    .addChildEventListener(childEventListener);
        } else {
            dbRef.child(getString(R.string.store_CODE))
                    .orderByChild("isHidden").equalTo(false).limitToLast(seeMore)
                    .addChildEventListener(childEventListener);
        }

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if(custom==1){
                    addMarkerYourLocation();
                   float kc = (float) myTool.distanceFrom_in_Km(yourLocation.getLat(), yourLocation.getLng(), customLocation.getLat(), customLocation.getLng());
                   int c = Math.round(kc);
                   int d = c / 1000;
                   int e = c % 1000;
                   int f = e / 100;
                   customLocation.setDistance(d + "," + f + " km");
                    addMarker(customLocation);
                    myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(customLocation.getLat(), customLocation.getLng()), 13));

                }else {
                   getDataInFireBase(1);
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Dialog onCreateDialog() {
        final NumberPicker numberPicker = new NumberPicker(getActivity());
        numberPicker.setMaxValue(50);
        numberPicker.setMinValue(1);
        numberPicker.setValue(distance / 1000);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(numberPicker);
        builder.setTitle("Changing the distance");
        builder.setMessage("Choose a value :");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                distance = numberPicker.getValue() * 1000;
                getDataInFireBase(sortType);
                txt_distance.setText(numberPicker.getValue() + "km");

            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
