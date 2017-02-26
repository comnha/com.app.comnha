package com.app.ptt.comnha.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filterable;
import android.widget.Toast;
import android.widget.Filter;
import android.widget.Filterable;

import com.app.ptt.comnha.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class SearchFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String LOG="Google Places Autocomplete";
    private static final String PLACES_API_BASE="https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE="/autocomplete";
    public static final String OUT_JSON="/json";
    private static final String API_KEY="AIzaSyDU6VmWjBJP-k6yWgP4v4X5EVFhyCljulo";
    AutoCompleteTextView acText;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_search,container,false);
        acText=(AutoCompleteTextView) view.findViewById(R.id.acText);
        acText.setAdapter(new GooglePlacesAutocompleteAdapter(getContext(),R.layout.list_item));
        acText.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String str=(String) parent.getItemAtPosition(position);
        Toast.makeText(getContext(),str,Toast.LENGTH_LONG).show();
    }
    public static ArrayList autocomplete(String input){
        ArrayList resultList=new ArrayList();
        HttpURLConnection conn=null;
        StringBuilder jsonResults=new StringBuilder();
        try{
            StringBuilder sb=new StringBuilder(PLACES_API_BASE+TYPE_AUTOCOMPLETE+OUT_JSON);
            sb.append("?input="+ URLEncoder.encode(input,"utf8"));
            //sb.append("&components=country:gr");
            sb.append("&key="+API_KEY);

            URL url=new URL(sb.toString());
            Log.i("",url+"");
            conn=(HttpURLConnection) url.openConnection();
            InputStreamReader in=new InputStreamReader(conn.getInputStream());
            int read;
            char[] buff=new char[1024];
            while((read=in.read(buff))!=-1){
                jsonResults.append(buff,0,read);
            }
        } catch (MalformedURLException e) {
            Log.e("MYLOG", "Error processing Places API URL", e);
            return resultList;

        } catch (IOException e) {
            Log.e("MYLOG", "Error connecting to Places API", e);
            return resultList;
        }finally {
            if(conn!=null){
                conn.disconnect();
            }
        }
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e("MY LOG", "Cannot process JSON results", e);
        }

        return resultList;
    }
    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index).toString();
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }
}