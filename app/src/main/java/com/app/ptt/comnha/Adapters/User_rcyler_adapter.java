package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.app.ptt.comnha.Interfaces.Comunication;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PTT on 10/5/2016.
 */

public class User_rcyler_adapter extends RecyclerView.Adapter<User_rcyler_adapter.CustomHolder> implements Filterable {

    private List<User> mFilteredList;
    List<User> users;
    StorageReference stRef;
    Activity activity;
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString =constraint.toString();
                if(charString.isEmpty()){
                    mFilteredList=users;
                }else{
                    List<User> filteredList=new ArrayList<>();
                    for(User user: users){
                        if(user.getUn().toLowerCase().contains(charString)
                                ||user.getHo().contains(charString)
                                ||user.getTenlot().contains(charString)
                                ||user.getTen().contains(charString)){
                            filteredList.add(user);
                        }
                    }
                    mFilteredList=filteredList;
                }
                FilterResults filterResults=new FilterResults();
                filterResults.values=mFilteredList;
                return filterResults;


            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredList=(List<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class CustomHolder extends RecyclerView.ViewHolder {
        private ImageView imgv_avatar;
        private TextView txtName, txtAddress,txtMenu,txtAdmin,txtUserName;

        public CustomHolder(View itemView) {
            super(itemView);
            imgv_avatar = (ImageView) itemView.findViewById(R.id.avatar_itemcomt);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
            txtAddress = (TextView) itemView.findViewById(R.id.txt_address);
            txtMenu= (TextView) itemView.findViewById(R.id.txt_menu);
            txtAdmin= (TextView) itemView.findViewById(R.id.txt_admin);
            txtUserName= (TextView) itemView.findViewById(R.id.txt_nick_name);
        }
    }



    @Override
    public CustomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcyler_user, parent, false);
        return new CustomHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CustomHolder holder, int position) {
        final User user= mFilteredList.get(position);
        if(null!=user) {
            holder.txtMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popup = new PopupMenu(activity, holder.txtMenu);

                    if (user.getRole() == 1) {
                        popup.inflate(R.menu.options_menu_user_admin);
                    } else {
                        popup.inflate(R.menu.options_menu_user);
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_edit:
                                    //handle menu1 click
                                    if (user.getRole() == 1) {
                                        AlertDialog.Builder alBuilder = new AlertDialog.Builder(activity);
                                        alBuilder.setTitle("Bạn có muốn xóa quyền admin tài khoản này không?");
                                        alBuilder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Comunication.transactions.changeRole(user);
                                                dialog.dismiss();
                                            }
                                        });
                                        alBuilder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        alBuilder.create();
                                        alBuilder.show();
                                    } else {
                                        Comunication.transactions.changeRole(user);
                                    }


                                    break;
                                case R.id.menu_del:
                                    AlertDialog.Builder alBuilder = new AlertDialog.Builder(activity);
                                    alBuilder.setTitle("Bạn có muốn xóa tài khoản này không?");
                                    alBuilder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Comunication.transactions.deleteUser(user);
                                            dialog.dismiss();
                                        }
                                    });
                                    alBuilder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    alBuilder.create();
                                    alBuilder.show();
                                    break;

                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });
            if (!user.getAvatar().equals("")) {
                StorageReference imgRef = stRef.child(user.getAvatar());
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(activity)
                                .load(uri)
                                .into(holder.imgv_avatar);
                    }
                });
            } else {
                holder.imgv_avatar.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
            }
            holder.txtName.setText(user.getHo().toString() + " " + user.getTenlot().toString() + " " + user.getTen().toString());
            holder.txtAddress.setText("Điạ chỉ: " + user.getAddress().toString());
            holder.txtUserName.setText(user.getUn());
            if (user.getRole() == 1) {
                holder.txtAdmin.setText(user.getDistrict() + "-" + user.getProvince());
            } else {
                holder.txtAdmin.setText("");
            }
        }
    }


    @Override
    public int getItemCount() {
        if(mFilteredList==null){
            mFilteredList=new ArrayList<>();
        }
        return mFilteredList.size();
    }

    public User_rcyler_adapter(Activity activity, List<User> users,
                               StorageReference stRef) {
        this.users=new ArrayList<>();
        this.users = users;
        this.mFilteredList=new ArrayList<>();
        mFilteredList=users ;
        this.stRef = stRef;
        this.activity = activity;
    }
    public void addItem(User item){
        this.users.add(item);
        mFilteredList=users;
        notifyDataSetChanged();
    }
    public void setList(List<User> users){
        this.users.clear();
        this.users.addAll(users);
        mFilteredList=users;
        notifyDataSetChanged();
    }
    public void clearList(){
        this.users=new ArrayList<>();
        mFilteredList=users;
        notifyDataSetChanged();
    }
    public int returnSize(){
        return users.size();
    }

}
