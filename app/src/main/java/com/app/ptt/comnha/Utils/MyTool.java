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

    class ParseToLocation extends AsyncTask<Void, Void, Void> {
        Location location;
        Store store;

        public ParseToLocation(Location l, DoInBackGroundOK doInBackGroundOK) {
            location = l;
            this.doInBackGroundOK = doInBackGroundOK;
        }

        ;
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

//    @Override
//    public void onLocationChanged(Location location) {
//        Log.i(LOG + ".onLocationChanged", "Giam sat su thay doi vi tri. lat=" + location.getLatitude() + " - lng=" + location.getLongitude());
//        if (getLocationFail) {
//            Log.i(LOG + ".onLocationChanged", "Get location fail");
//            if (mGoogleApiClient.isConnected()) {
//                if (location != null) {
//                    Log.i(LOG + ".onLocationChanged", "location!=null");
//                    ParseToLocation parseToLocation = new ParseToLocation(location, this);
//                    parseToLocation.execute();
//                }
//            } else {
//                Log.i(LOG + ".onLocationChanged", "location==null");
//                flag = -4;
//                startGoogleApi();
//            }
//        }
//        if (latitude == null && longtitude == null) {
//            this.latitude = location.getLatitude();
//            this.longtitude = location.getLongitude();
//            Store store;
//            store = returnLocationByLatLng(location.getLatitude(), location.getLongitude());
//            flag = 2;
////            placeAPI = new PlaceAPI(store.getDiachi(), this);
////        }
//            if (location != null) {
//                if (location.getLatitude() != this.latitude && location.getLongitude() != this.longtitude &&
//                        getDistance(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(this.latitude, this.longtitude)) > 2000) {
//                    Log.i(LOG + ".onLocationChanged", "Vi tri cua ban bi thay doi: " + getDistance(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(this.latitude, this.longtitude)) + "m");
//                    this.latitude = location.getLatitude();
//                    this.longtitude = location.getLongitude();
//                    flag = 2;
//                    ParseToLocation parseToLocation = new ParseToLocation(location, this);
//                    parseToLocation.execute();
//                }
//            }
//        }
//    }

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

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public boolean isGoogleApiConnected() {
        if (mGoogleApiClient != null)
            return mGoogleApiClient.isConnected();
        else return false;
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

//                        store.setQuanhuyen(c);
                    }
                    if (d != null) {
                        if (a == null && b == null && c == null)
                            e += d;
                        else
                            e += ", " + d;
//                        store.setTinhtp(d);
                    }
//                    store.setDiachi(e);
//                    store.setLat(lat);
//                    store.setLng(lon);
//                    Log.i(LOG + ".returnLocationByLatLng", "Location can tim" + store.getDiachi());
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

    public ArrayList<PlaceAttribute> returnPlaceAttributeByName(String mAddress) {
        ArrayList<PlaceAttribute> listPlaceAttribute = new ArrayList<>();
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocationName(mAddress, 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            for (Address address : addresses) {
                PlaceAttribute placeAttribute = new PlaceAttribute();
                String a = address.getAddressLine(0);
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
                Log.i(LOG + ".addtoPlaceAttribute", "Full name: " + placeAttribute.getFullname());
                Log.i(LOG + ".addtoPlaceAttribute", "Address Number:" + placeAttribute.getAddressNum());
                Log.i(LOG + ".addtoPlaceAttribute", "Locality: " + placeAttribute.getLocality());
                Log.i(LOG + ".addtoPlaceAttribute", "District: " + placeAttribute.getDistrict());
                Log.i(LOG + ".addtoPlaceAttribute", "State: " + placeAttribute.getState());
                listPlaceAttribute.add(placeAttribute);
            }
            if (listPlaceAttribute.size() > 0)
                return listPlaceAttribute;
        }
        return null;
    }

    @Override
    public void onLocationFinderStart() {

    }

    @Override
    public void onLocationFinderSuccess(PlaceAttribute placeAttribute) {
        if (placeAttribute != null) {
//            yourLocation = new Store();
//            yourLocation.setDiachi(placeAttribute.getFullname());
//            yourLocation.setQuanhuyen(placeAttribute.getDistrict());
//            yourLocation.setTinhtp(placeAttribute.getState());
            try {
                LatLng a = (returnLatLngByName(placeAttribute.getFullname()));
//                yourLocation.setLat(a.latitude);
//                yourLocation.setLng(a.longitude);
                if (flag == 2) {
                    sendBroadcast("Location");
                    getLocationFail = false;
                }
            } catch (Exception e) {
                getLocationFail = true;
            }

            if (flag == 3) {
                ArrayList<Store> mList = new ArrayList<>();
                mList.add(yourLocation);
                Storage.writeFile(mContext, Storage.parseMyLocationToJson(mList).toString(), "myLocation");
            }
        } else {
            Log.i(LOG + ".onLocationFinder", "State: null");
        }
        flag = -10;
    }

    @Override
    public void onGeocodingFinderSuccess(String address) {

    }


}