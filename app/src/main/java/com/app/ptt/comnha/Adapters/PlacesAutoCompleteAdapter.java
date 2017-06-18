package com.app.ptt.comnha.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.app.ptt.comnha.Modules.PlaceAttribute;
import com.app.ptt.comnha.Utils.MyTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ciqaz on 14/06/2017.
 */

public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    ArrayList<String> resultList;
    Context mContext;
    int mResource;
    MyTool myTool;
    List<PlaceAttribute> mPlaceAttribute;

    public PlacesAutoCompleteAdapter(Context context, int resource, MyTool myTool) {
        super(context, resource);
        mContext = context;
        this.myTool=myTool;
        mResource = resource;
    }
    public PlaceAttribute getItemOfList(int pos){
        return mPlaceAttribute.get(pos);
    }
    @Override
    public int getCount() {
        if (resultList != null) {
            return resultList.size();
        } else return 0;
    }


    @Override
    public String getItem(int position) {
        return resultList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        final Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    mPlaceAttribute = new ArrayList<>();
                    mPlaceAttribute = myTool.returnPlaceAttributeByName(constraint.toString());
                    if (mPlaceAttribute != null) {
                        resultList = new ArrayList<>();
                        for (PlaceAttribute placeAttribute : mPlaceAttribute)
                            resultList.add(placeAttribute.getFullname());
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    } else {
                        PlaceAttribute a1 =new PlaceAttribute();
                        a1.setFullname(constraint.toString());
                        mPlaceAttribute.add(a1);
                    }
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