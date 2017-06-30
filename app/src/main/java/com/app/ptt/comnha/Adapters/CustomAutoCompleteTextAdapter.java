package com.app.ptt.comnha.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.LayoutRes;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.ptt.comnha.Interfaces.Comunication;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Models.Search;
import com.app.ptt.comnha.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ciqaz on 22/06/2017.
 */

public class CustomAutoCompleteTextAdapter extends RecyclerView.Adapter<CustomAutoCompleteTextAdapter.MViewHolder> implements Filterable {
    List<Search> list;
    List<Search> filteredList;
    int type;
    StorageReference stRef;
    Context context;

    public CustomAutoCompleteTextAdapter(Context context,List<Search> list,StorageReference stRef){
        this.list=new ArrayList<>();
        filteredList=new ArrayList<>();
        this.list=list;
        filteredList=list;
        this.context=context;
        this.stRef=stRef;


    }
    public void clearList(){
        list=new ArrayList<>();
        filteredList=list;
        notifyDataSetChanged();
    }

    public void setList(List<Search> list){
        this.list.clear();
        this.list=list;
        filteredList=list;
        notifyDataSetChanged();
    }
    public void addItem(Search item){
        this.list.add(item);
        filteredList=list;
        notifyDataSetChanged();
    }
    public void setType(int type){
        this.type=type;
    }
    public Search returnItem(int pos){
        return list.get(pos);
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final String filterPattern = constraint.toString();
                final FilterResults results = new FilterResults();
                if (filterPattern.isEmpty()) {
                    filteredList=list;
                } else {

                    List<Search> mList=new ArrayList<>();
                    // Your filtering logic goes in here
                    for (final Search search : list) {
                        if(type==0 && search.getType()==0){
                            if (search.getFood().getName().toLowerCase().contains(filterPattern.toLowerCase())) {
                                mList.add(search);
                            }
                        }
                        if(type==1 && search.getType()==1){
                            if (search.getStore().getName().toLowerCase().contains(filterPattern.toLowerCase())) {
                                mList.add(search);
                            }
                        }

                    }
                    filteredList=mList;
                }
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList=(List<Search>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_auto_complete_text,parent,false);
        return new MViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MViewHolder holder, int position) {
        final Search search=filteredList.get(position);
        if(search.getType()==0){
            holder.txtName.setText(search.getFood().getName());
            holder.txtAddress.setText(search.getStore().getAddress());
            if (!TextUtils.isEmpty(search.getFood().getFoodImg())) {
                StorageReference imgRef = stRef.child(search.getFood().getFoodImg());
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(context)
                                .load(uri)
                                .into(holder.img);
                    }
                });
            } else {
                holder.img.setImageResource(R.drawable.ic_item_notify_reportfood);
            }
        }
        if(search.getType()==1){
            holder.txtName.setText(search.getStore().getName());
            holder.txtAddress.setText(search.getStore().getAddress());
            if (!TextUtils.isEmpty(search.getStore().getStoreimg())) {
                StorageReference imgRef = stRef.child(search.getStore().getStoreimg());
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(context)
                                .load(uri)
                                .into(holder.img);
                    }
                });
            } else {
                holder.img.setImageResource(R.drawable.ic_item_notify_reportstore);
            }
        }
        holder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comunication.transactions.onSearchItemClick(search);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(null==filteredList)
            filteredList=new ArrayList<>();
        return filteredList.size() ;
    }
    public static class MViewHolder extends RecyclerView.ViewHolder{
        private TextView txtName,txtAddress;
        private CircularImageView img;
        private LinearLayout llItem;

        public MViewHolder(View itemView) {
            super(itemView);
            img= (CircularImageView) itemView.findViewById(R.id.img_item);
            txtName= (TextView) itemView.findViewById(R.id.txt_name_search);
            txtAddress= (TextView) itemView.findViewById(R.id.txt_address_search);
            llItem= (LinearLayout) itemView.findViewById(R.id.ll_item);
        }
    }
}

