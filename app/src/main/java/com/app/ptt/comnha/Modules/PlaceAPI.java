package com.app.ptt.comnha.Modules;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.json.Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cuong on 10/25/2016.
 */

public class PlaceAPI {

    private static final String LOG = PlaceAPI.class.getSimpleName();
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private static final String API_KEY = "AIzaSyDU6VmWjBJP-k6yWgP4v4X5EVFhyCljulo";
    String fullname;
    String addressNum;
    LocationFinderListener location;

    PlaceAttribute mPlaceAttributes;
    public PlaceAPI(String input, LocationFinderListener location){
        this.location=location;
        if(input!="") {
            Log.i(LOG+".PlaceAPI",input);
            mPlaceAttributes=new PlaceAttribute();
            location.onLocationFinderStart();
            new LongProgress().execute(input);
        }else{
            location.onLocationFinderSuccess(null);
        }
    }
    private class LongProgress extends AsyncTask<String,Void,String>{
        String name;
        @Override
        protected String doInBackground(String... params) {
            StringBuffer stringBuffer=new StringBuffer();
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            try {
                sb.append(URLEncoder.encode(params[0], "utf8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sb.append("&key=" + API_KEY);
            sb.append("&components=country:VN");
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
                //Log.i("JSON:",stringBuffer.toString());
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
            try {
                JSONObject jsonObj = new JSONObject(s.toString());
                JSONArray resultsJsonResult = jsonObj.getJSONArray("results");
                if (resultsJsonResult.length()>0) {
                    //for(int k=0;k<resultsJsonResult.length();k++) {
                    Log.i("onPostExecute:", "zo");
                    JSONObject temp = resultsJsonResult.getJSONObject(0);
                    fullname = temp.getString("formatted_address");
                    JSONObject geometry = temp.getJSONObject("geometry");
                    JSONObject mLocation = geometry.getJSONObject("location");
                    Double lat = mLocation.getDouble("lat");
                    Double lng = mLocation.getDouble("lng");
                    // Extract the Place descriptions from the results
                    JSONArray jsonAddresses = temp.getJSONArray("address_components");
                    addressNum = "";
                    for (int i = 0; i < jsonAddresses.length(); i++) {
                        JSONObject jsonObject = jsonAddresses.getJSONObject(i);
                        name = jsonObject.getString("long_name");
                        JSONArray jsonType = jsonObject.getJSONArray("types");
                        List<String> list = new ArrayList<>();

                        for (int j = 0; j < jsonType.length(); j++) {
                            list.add(jsonType.getString(j));
                            addtoPlaceAttribute(list, name);
                        }
                    }

                    Log.i(LOG + ".fullAddress", addressNum);
                    mPlaceAttributes.setFullname(getFullName());
                    mPlaceAttributes.setPlaceLatLng(new LatLng(lat, lng));
                    Log.i(LOG + ".addtoPlaceAttribute", "Full name: " + mPlaceAttributes.getFullname());
                    Log.i(LOG + ".addtoPlaceAttribute", "Address Number:" + mPlaceAttributes.getAddressNum());
                    Log.i(LOG + ".addtoPlaceAttribute", "Locality: " + mPlaceAttributes.getLocality());
                    Log.i(LOG + ".addtoPlaceAttribute", "District: " + mPlaceAttributes.getDistrict());
                    Log.i(LOG + ".addtoPlaceAttribute", "State: " + mPlaceAttributes.getState());
                    if (mPlaceAttributes != null && mPlaceAttributes.getPlaceLatLng() != null&&!TextUtils.isEmpty( mPlaceAttributes.getFullname())) {

                        location.onLocationFinderSuccess(mPlaceAttributes);
                    } else {
                        location.onLocationFinderSuccess(null);
                    }

                                }

            } catch (JSONException e) {
                Log.e(LOG, "Cannot process JSON results", e);
            }
        }
    }
    public String  getFullName(){
        String a = mPlaceAttributes.getAddressNum();
        String b = mPlaceAttributes.getLocality();
        String c = mPlaceAttributes.getDistrict();
        String d = mPlaceAttributes.getState();
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

            if (a == null && b == null)
                e += c;
            else
                e += ", " + c;

        }
        if (d != null) {
            if (a == null && b == null && c == null)
                e += d;
            else
                e += ", " + d;

        }
        Log.i(LOG+".getFullName",e);
        return e;

    }
    public PlaceAttribute addtoPlaceAttribute(List<String> list,String name) {
        if (name == null)
            name = "";

        else{
            Log.i(LOG + ".street_number", addressNum);
            for (int i = 0; i < list.size(); i++) {
                switch (list.get(i).toString()) {
                    case "street_number":
                        addressNum += name;
                        break;
                    case "route":
                        addressNum += " " + name;
                        mPlaceAttributes.setAddressNum(addressNum);
                        Log.i(LOG + ".route", addressNum);
                        break;
                    case "sublocality_level_1":
                        mPlaceAttributes.setLocality(name);
                        break;
                    case "administrative_area_level_2":

                        mPlaceAttributes.setDistrict(name);
                        break;
                    case "administrative_area_level_1":
                        mPlaceAttributes.setState(name);
                        break;
                    default:
                        break;
                }
            }
        }



        return mPlaceAttributes;
    }

}