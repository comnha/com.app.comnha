package com.app.ptt.comnha.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Fragment.PickLocationBottomSheetDialogFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

/**
 * Created by doba on 3/21/17.
 */

public class LocationController implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = "LocationController";
    private int count =0;
    public interface LocationControllerListener {
        void onFail();
        void requestPermisson(List<String> strings);
        void onLocationChanged(Location location);
    }

    private LocationControllerListener locationControllerListener;

    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    public LocationController(Context context) {
        this.context = context;
    }

    public void initController(){
        initGoogleAPIClient(context);
    }

    public void setLocationListener(LocationControllerListener locationListener) {
        this.locationControllerListener = locationListener;
    }

    private void initGoogleAPIClient(Context context) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        createLocationRequest();
    }
    public boolean isGoogleAPIClientConnected(){
        return mGoogleApiClient.isConnected();
    }



    /**
     * Create location request object
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    }



    public void loadLocationService() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            locationControllerListener.onLocationChanged(mLastLocation);
            Log.e(TAG, String.format("%f - %f",mLastLocation.getLatitude(),mLastLocation.getLongitude()));
        }
        else{
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        }
    }
    private void callPermissionUI() {
        String[] locationPermissions=new String[2];
        locationPermissions[0]=Const.mListPermissions[2];
        locationPermissions[1]=Const.mListPermissions[4];
        List<String> results=AppUtils.checkPermissions(context,locationPermissions);
        if (results.size()>0){
            locationControllerListener.requestPermisson(results);
        } else{
            loadLocationService();
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(TAG, "onConnected ");
        callPermissionUI();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        locationControllerListener.onFail();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(count<3) {
            count++;
            if (mGoogleApiClient.isConnected()) {
                mLastLocation = location;
                locationControllerListener.onLocationChanged(location);
            } else {
                if(mGoogleApiClient!=null) {
                    mGoogleApiClient.disconnect();
                }

                mGoogleApiClient = null;
                mLocationRequest = null;
            }
        }else{
            if(mGoogleApiClient!=null) {
                mGoogleApiClient.disconnect();
            }
            mGoogleApiClient = null;
            mLocationRequest = null;
        }
    }

    public void connect(){
        mGoogleApiClient.connect();
    }

    public void disconnect(){
        if(mGoogleApiClient!=null) {
            mGoogleApiClient.disconnect();
        }

    }
}
