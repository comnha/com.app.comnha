package com.app.ptt.comnha.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Interfaces.DoInBackGroundOK;
import com.app.ptt.comnha.Interfaces.LocationFinderListener;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Models.MyLocation;
import com.app.ptt.comnha.Modules.PlaceAttribute;
import com.firebase.client.Firebase;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by cuong on 10/27/2016.
 */

public class MyTool implements
        LocationFinderListener, DoInBackGroundOK {
    private Context mContext;
    private static final String LOG = MyTool.class.getSimpleName();
    private LocationRequest mLocationRequest;
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    private Double latitude = null, longtitude = null;
    private String fileName = "note.json";
    Geocoder geocoder;
    ProgressDialog progressDialog;
    Firebase ref;
    int count = 0;
    ArrayList<Store> listLocation;
    PlaceAPI placeAPI;
    Intent broadcastIntent;
    Store yourLocation = new Store();
    int temp = 1;

    int flag;
    boolean getLocationFail = false;
    int pos = 0;
    public static final String mBroadcastSendAddress1 = "mBroadcastSendAddress1";

    public MyTool(Context context) {
        Log.i(LOG + ".MyTool", "Khoi chay MyTool");
        this.mContext = context;
        listLocation = new ArrayList<>();
        broadcastIntent = new Intent();
        Locale locale = new Locale("vi","VN");
        Locale.setDefault(locale);
        geocoder = new Geocoder(mContext, Locale.getDefault());
    }


    public double getDistance(LatLng LatLng1, LatLng LatLng2) {
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude(LatLng1.latitude);
        locationA.setLongitude(LatLng1.longitude);
        Location locationB = new Location("B");
        locationB.setLatitude(LatLng2.latitude);
        locationB.setLongitude(LatLng2.longitude);
        distance = locationA.distanceTo(locationB);
        return distance;
    }
    public float distanceFrom_in_Km(double lat1, double lng1, double lat2, double lng2) {
//        double earthRadius = 6371000; //meters
//        double dLat = Math.toRadians(lat2-lat1);
//        double dLng = Math.toRadians(lng2-lng1);
//        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
//                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
//                        Math.sin(dLng/2) * Math.sin(dLng/2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//        float dist = (float) (earthRadius * c);
//
//        return dist;
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(lat1);
        startPoint.setLongitude(lng1);

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(lat2);
        endPoint.setLongitude(lng2);

        double distance= (int) startPoint.distanceTo(endPoint);
        return (float) distance;
    }


    @Override
    public void DoInBackGroundStart() {

    }

    @Override
    public void DoInBackGroundOK(Boolean isSuccess, int type) {

    }

    @Override
    public void DoInBackGroundImg(Bitmap bitmap) {

    }

    @Override
    public void DoInBackGroundLocation(Store location) {
        if (location != null) {
            flag = 2;
//            placeAPI = new PlaceAPI(location.getDiachi(), this);
        } else {
            Log.i(LOG + ".onConnected", "LOI BI NULL");
            flag = -4;
            sendBroadcast("GetLocationError");
        }

        getLocationFail = false;
    }

    @Override
    public void onLocationFinderStart() {

    }

    @Override
    public void onGeocodingFinderSuccess(String address) {

    }

    @Override
    public void onLocationFinderSuccess(PlaceAttribute placeAttribute) {

    }

    class ParseToLocation extends AsyncTask<Void, Void, Void> {
        Location location;
        Store store;

        public ParseToLocation(Location l, DoInBackGroundOK doInBackGroundOK) {
            location = l;
            this.doInBackGroundOK = doInBackGroundOK;
        }

        DoInBackGroundOK doInBackGroundOK;

        @Override
        protected Void doInBackground(Void... params) {
            store = returnLocationByLatLng(location.getLatitude(), location.getLongitude());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (store != null) {
                doInBackGroundOK.DoInBackGroundLocation(store);
            }
        }
    }

    public void sendBroadcast(String a) {
        broadcastIntent = new Intent();
        if (flag == 2) {
            broadcastIntent.setAction(Const.BROADCAST_SEND_INFO);
            broadcastIntent.putExtra("STT", flag);
            mContext.sendBroadcast(broadcastIntent);
        }
        if (flag == -4) {
            broadcastIntent.setAction(Const.BROADCAST_SEND_INFO);
            broadcastIntent.putExtra("STT", flag);
            mContext.sendBroadcast(broadcastIntent);
        }
    }

    public Store getYourLocation() {
        Log.i(LOG + ".returnLocation", "Lay vi tri cua ban");
        if (yourLocation != null)
            return yourLocation;
        else
            return null;
    }

    public Store returnLocationByLatLng(Double latitude, Double longitude) {
        Store store = new Store();
        List<Address> addresses;
        Double lat = latitude;
        Double lon = longitude;
        try {
            if (lat != null && lon != null) {
                addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    String a = address.getAddressLine(0);
                    String b = address.getSubLocality();
                    String c = address.getSubAdminArea();
                    String d = address.getAdminArea();
                    String e = "";
                    if (a != null) {
                        e += a;
                    }
                    if (b != null) {
                        if (a == null)
                            e += b;
                        else
                            e += ", " + b;

                    }

                    if (c != null) {
                        Scanner kb = new Scanner(c);
                        String name;
                        while (kb.hasNext()) {
                            name = kb.next();
                            if (name.equals("District") || name.equals("Quận")) {
                                c = "Quận";
                                while (kb.hasNext()) {
                                    c += " " + kb.next();
                                }
                            }
                        }
                        if (a == null && b == null)
                            e += c;
                        else
                            e += ", " + c;

                        store.setProvince(c);
                    }
                    if (d != null) {
                        if (a == null && b == null && c == null)
                            e += d;
                        else
                            e += ", " + d;
                        store.setDistrict(d);
                    }
                    store.setAddress(e);
                    store.setLat(lat);
                    store.setLng(lon);
                    return store;
                }
                return null;
            }
        } catch (IOException e) {
        }
        return null;
    }
    public MyLocation returnMyLocation(Double latitude, Double longitude) {
        MyLocation store = new MyLocation();
        List<Address> addresses;
        Double lat = latitude;
        Double lon = longitude;
        try {
            if (lat != null && lon != null) {
                addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses.size() > 0) {
                    String houseNum,ward,district,city;
                    Address address = addresses.get(0);
                    if(address.getAddressLine(0)==null||
                            address.getAddressLine(1)==null||
                            address.getAddressLine(2)==null||
                            address.getAddressLine(3)==null ){
                         houseNum = address.getSubThoroughfare()+" "+address.getThoroughfare();
                         ward = address.getSubLocality();
                         district = address.getSubAdminArea();
                         city = address.getAdminArea();
                    }else{
                         houseNum = address.getAddressLine(0);
                         ward = address.getAddressLine(1);
                         district = address.getAddressLine(2);
                         city = address.getAddressLine(3);
                    }


                    String finalAddress = "";
                    if (houseNum != null) {
                        finalAddress += houseNum;
                    }
                    if (ward != null) {
                        if (houseNum == null)
                            finalAddress += ward;
                        else
                            finalAddress += ", " + ward;

                    }

                    if (district != null) {
                        Scanner kb = new Scanner(district);
                        String name;
                        while (kb.hasNext()) {
                            name = kb.next();
                            if (name.equals("District") || name.equals("Quận")) {
                                district = "Quận";
                                while (kb.hasNext()) {
                                    district += " " + kb.next();
                                }
                            }
                        }
                        if (houseNum == null && ward == null)
                            finalAddress += district;
                        else
                            finalAddress += ", " + district;
                        store.setDistrict(district);
                    }
                    if (city != null) {
                        if (houseNum == null && ward == null && district == null)
                            finalAddress += city;
                        else
                            finalAddress += ", " + city;
                        store.setProvince(city);
                    }
                    store.setAddress(finalAddress);
                    store.setLat(lat);
                    store.setLng(lon);
                    return store;
                }
                return null;
            }
        } catch (IOException e) {
        }
        return null;
    }

    public LatLng returnLatLngByName(String address) {
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocationName(address, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            Log.i(LOG, "convert: latitude=" + addresses.get(0).getLatitude() + "longitude=" + addresses.get(0).getLongitude());
            return new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
        } else return null;
    }

    public List<PlaceAttribute> returnPlaceAttributeByName(String mAddress) {
        List<PlaceAttribute> listPlaceAttribute = new ArrayList<>();
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocationName(mAddress, 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            for (Address address : addresses) {
                PlaceAttribute placeAttribute = new PlaceAttribute();
                String a = address.getSubThoroughfare()+" " +address.getThoroughfare();
                String b = address.getSubLocality();
                String c = address.getSubAdminArea();
                String d = address.getAdminArea();
                String e = "";
                if (a != null) {
                    e += a;
                    placeAttribute.setAddressNum(a);
                }
                if (b != null) {
                    if (a == null)
                        e += b;
                    else
                        e += ", " + b;
                    placeAttribute.setLocality(b);
                }
                if (c != null) {
                    Scanner kb = new Scanner(c);
                    String name;
                    while (kb.hasNext()) {
                        name = kb.next();
                        if (name.equals("District") || name.equals("Quận")) {
                            c = "Quận";
                            while (kb.hasNext()) {
                                c += " " + kb.next();
                            }
                        }
                    }
                    if (a == null && b == null)
                        e += c;
                    else
                        e += ", " + c;
                    placeAttribute.setDistrict(c);
                }
                if (d != null) {
                    if (a == null && b == null && c == null)
                        e += d;
                    else
                        e += ", " + d;
                    placeAttribute.setState(d);
                }
                placeAttribute.setFullname(e);
                placeAttribute.setPlaceLatLng(new LatLng(address.getLatitude(), address.getLongitude()));
                listPlaceAttribute.add(placeAttribute);
            }
            if (listPlaceAttribute.size() > 0)
                return listPlaceAttribute;
        }
        return null;
    }





}
