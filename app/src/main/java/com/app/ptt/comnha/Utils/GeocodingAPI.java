package com.app.ptt.comnha.Utils;

import android.os.AsyncTask;
import android.util.Log;

import com.app.ptt.comnha.Interfaces.LocationFinderListener;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by NGUYEN VAN CUONG on 2/26/2017.
 */

public class GeocodingAPI {
    private static final String LOG = PlaceAPI.class.getSimpleName();
    private static final String GEOCODING_API_BASE = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    private static final String API_KEY = "AIzaSyA_oGNaJRuD0cJx8uILuYEFrGZ1CPMD5K8";
    String fullname;
    String addressNum;
    LocationFinderListener location;

    public GeocodingAPI(Double lat,Double lon, LocationFinderListener location) {
        this.location = location;
        location.onLocationFinderStart();
        new GeocodingAPI.LongProgress().execute(new LatLng(lat,lon));
    }
    private class LongProgress extends AsyncTask<LatLng,Void,String> {
        String name;
        @Override
        protected String doInBackground(LatLng... params) {
            StringBuffer stringBuffer=new StringBuffer();
            StringBuilder sb = new StringBuilder(GEOCODING_API_BASE);
            sb.append(params[0].latitude+","+params[0].longitude);
            sb.append("&key=" + API_KEY);
            String link=sb.toString();
            Log.i("LINK:",link);
            try{
                URL url=new URL(link);
                InputStream is=url.openConnection().getInputStream();
                BufferedReader reader=new BufferedReader(new InputStreamReader(is));
                String line;
                while((line=reader.readLine())!=null){
                    stringBuffer.append(line+"\n");
                }
                is.close();
                Log.i("JSON:",stringBuffer.toString());
                return stringBuffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s!=null) {
                try {
                    JSONObject jsonObj = new JSONObject(s.toString());
                    Log.i("onPostExecute:", s.toString());
                    JSONArray resultsJsonResult = jsonObj.getJSONArray("results");
                    if (resultsJsonResult.length() > 0) {
                        //for(int k=0;k<resultsJsonResult.length();k++) {
                        Log.i("onPostExecute:", "zo");
                        JSONObject temp = resultsJsonResult.getJSONObject(0);
                        fullname = temp.getString("formatted_address");
                        // Extract the Place descriptions from the results
                        JSONArray jsonAddresses = temp.getJSONArray("address_components");
                        addressNum = "";

                        if (fullname != null) {
                            location.onGeocodingFinderSuccess(fullname);
                        } else {
                            location.onGeocodingFinderSuccess(null);
                        }

                        // }
                    }

                } catch (JSONException e) {
                    Log.e(LOG, "Cannot process JSON results", e);
                }
            }else{
                location.onGeocodingFinderSuccess(null);
            }
        }
    }

}
