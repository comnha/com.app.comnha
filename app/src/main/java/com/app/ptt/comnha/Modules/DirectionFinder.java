package com.app.ptt.comnha.Modules;


import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

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


public class DirectionFinder {
    private static final String DIRECTION_URL_API="https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY="AIzaSyDU6VmWjBJP-k6yWgP4v4X5EVFhyCljulo";
    private static final String LOG = "DirectionFinder";
    private LocationFinderListener listener;
    private String origin;
    private Geocoder geocoder;
    private String destination,ID;
    Route route;
    String type;
    public DirectionFinder(LocationFinderListener listener, String origin, String destination, String ID, String className){
        this.listener=listener;
        this.origin=origin;
        this.destination=destination;
        this.ID=ID;
        this.type=className;
        Log.i(LOG,origin+" -> "+destination);
    }
    public void execute() throws UnsupportedEncodingException {
        if(destination!=null &&origin!=null) {
          //  listener.onDirectionFinderStart();
            new DowloadRawData().execute(createURL());
        }
    }
    private String createURL() throws UnsupportedEncodingException {
        String urlOrigin= URLEncoder.encode(origin,"utf-8");
        String urlDestination=URLEncoder.encode(destination,"utf-8");
        Log.i(LOG,DIRECTION_URL_API +"origin="+urlOrigin+"&destination="+urlDestination+"&key="+GOOGLE_API_KEY);
        return DIRECTION_URL_API +"origin="+urlOrigin+"&destination="+urlDestination+"&key="+GOOGLE_API_KEY;
    }
    private class DowloadRawData extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... params) {
            String link=params[0];
            try{
                URL url=new URL(link);
                    InputStream is=url.openConnection().getInputStream();
                    StringBuffer stringBuffer=new StringBuffer();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(is));
                    String line;
                    while((line=reader.readLine())!=null){
                        stringBuffer.append(line+"\n");
                }
                return stringBuffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res){
            try{
                Log.i(LOG+".onPstEx","classname:"+type);
                if(type.equals("StoreFragment")) //Chi lay khoang cach
                    parseJSonCustom(res);
                else
                    parseJSon(res);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
    private void parseJSon(String data) throws JSONException {
        Log.i(LOG,"Parse Json TYPE=1");
        if(data==null){
            Log.i(LOG,"parseJSon: FAIL");
            return;
        }else {
            JSONObject jsonData = new JSONObject(data);
            JSONArray jsonRoutes = jsonData.getJSONArray("routes");
            for (int i = 0; i < jsonRoutes.length(); i++) {
                JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
                route = new Route();
                    JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
                    JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
                    JSONObject jsonLeg = jsonLegs.getJSONObject(0);
                    JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
                    JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
                    JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
                    JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");
                    route.setDistance(new MyDistance(jsonDistance.getString("text"), jsonDistance.getInt("value")));
                    route.setDuration(new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value")));
                    route.setEndAddress(jsonLeg.getString("end_address"));
                    route.setStartAddress(jsonLeg.getString("start_address"));
                    route.setStartLocation(new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng")));
                    route.setEndLocation(new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng")));
                    route.setPoints(decodePolyLine(overview_polylineJson.getString("points")));
                    route.setLocalID(ID);
                    Log.i(LOG, "lat=" + route.getEndLocation().latitude + " lon=" + route.getEndLocation().longitude);
                    Log.i(LOG, route.getLocalID() + "");
            }
            //listener.onDirectionFinderSuccess(route);
        }
    }
    private void parseJSonCustom(String data) throws JSONException {
        Log.i(LOG,"Parse Json TYPE=2");
        if(data==null){
            Log.i(LOG,"parseJSon: FAIL");
            return;
        }else {
            JSONObject jsonData = new JSONObject(data);
            JSONArray jsonRoutes = jsonData.getJSONArray("routes");
            for (int i = 0; i < jsonRoutes.length(); i++) {
                JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
                route = new Route();
                // JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
                JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
                JSONObject jsonLeg = jsonLegs.getJSONObject(0);
                JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
                // JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
                // JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
                // JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");
                route.setDistance(new MyDistance(jsonDistance.getString("text"), jsonDistance.getInt("value")));
                //  route.setDuration(new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value")));
                //route.setEndAddress(jsonLeg.getString("end_address"));
                // route.setStartAddress(jsonLeg.getString("start_address"));
                //route.setStartLocation(new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng")));
                //  route.setEndLocation(new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng")));
                //  route.setPoints(decodePolyLine(overview_polylineJson.getString("points")));
                route.setLocalID(ID);
//                    Log.i(LOG, "lat=" + route.getEndLocation().latitude + " lon=" + route.getEndLocation().longitude);
                Log.i(LOG+".JSonCt", route.getDistance().text);
            }
           // listener.onDirectionFinderSuccess(route);
        }
    }
    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}
