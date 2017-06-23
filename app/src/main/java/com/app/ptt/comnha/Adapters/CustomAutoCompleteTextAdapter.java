package com.app.ptt.comnha.Adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Models.Search;
import com.app.ptt.comnha.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ciqaz on 22/06/2017.
 */

public class CustomAutoCompleteTextAdapter extends ArrayAdapter<Search> {
    List<Search> list;
    int type;
    SearchFilter searchFilter;
    public CustomAutoCompleteTextAdapter(Context context,List<Search> list){
        super(context,0,list);
        this.list=new ArrayList<>();
        this.list=list;


    }
    public void setList(List<Search> list){
        this.list=new ArrayList<>();
        this.list.addAll(list);
        notifyDataSetChanged();
    }
    public void setType(int type){
        this.type=type;
        if(null!=searchFilter) {
            searchFilter.setType(type);
        }
    }
    public Search returnItem(int pos){
        return list.get(pos);
    }
    @Override
    public int getCount() {
        if(null==list){
            list=new ArrayList<>();
        }
        return list.size();
    }

    @Override
    public Filter getFilter() {
        searchFilter=new SearchFilter(this,list);
        return searchFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       Search search=list.get(position);
        LayoutInflater inflater=LayoutInflater.from(getContext());
        convertView= inflater.inflate(R.layout.item_auto_complete_text,parent,false);
        TextView txtName= (TextView) convertView.findViewById(R.id.txt_name_search);
        TextView txtAddress= (TextView) convertView.findViewById(R.id.txt_address_search);
        if(search.getType()==0){
            txtName.setText(search.getFood().getName());
            txtAddress.setText(search.getStore().getAddress());
        }
        if(search.getType()==1){
            txtName.setText(search.getStore().getName());
            txtAddress.setText(search.getStore().getAddress());
        }
        return convertView;

    }
}
class SearchFilter extends Filter {

    CustomAutoCompleteTextAdapter adapter;
    List<Search> originalList;
    List<Search> filteredList;
    int type;

    public SearchFilter(CustomAutoCompleteTextAdapter adapter, List<Search> originalList) {
        super();
        this.adapter = adapter;
        this.originalList = originalList;
        this.filteredList = new ArrayList<>();

    }
    public void setType(int type){
        this.type=type;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        filteredList.clear();
        final FilterResults results = new FilterResults();

        if (constraint == null || constraint.length() == 0) {
            filteredList.addAll(originalList);
        } else {
            final String filterPattern = constraint.toString().toLowerCase().trim();

            // Your filtering logic goes in here
            for (final Search search : originalList) {
                if(type==0 && search.getType()==0){
                    if (search.getFood().getName().toLowerCase().contains(filterPattern.toLowerCase())) {
                        filteredList.add(search);
                    }
                }
                if(type==1 && search.getType()==1){
                    if (search.getStore().getName().toLowerCase().contains(filterPattern.toLowerCase())) {
                        filteredList.add(search);
                    }
                }
            }
        }
        results.values = filteredList;
        results.count = filteredList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.setList((List<Search>) results.values);
    }
}
