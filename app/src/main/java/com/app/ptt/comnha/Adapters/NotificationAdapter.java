package com.app.ptt.comnha.Adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.Activity.ProfiledetailActivity;
import com.app.ptt.comnha.Interfaces.Comunication;
import com.app.ptt.comnha.Interfaces.Transactions;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.Models.FireBase.UserNotification;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Utils.AppUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ciqaz on 08/07/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    List<UserNotification> list;
    Context context;
    DatabaseReference dbRef;
    StorageReference stRef;
    public NotificationAdapter(Context context,DatabaseReference dbRef,StorageReference stRef){
        this.context=context;
        this.list=new ArrayList<>();
        this.dbRef=dbRef;
        this.stRef=stRef;
    }
    public void addAll(List<UserNotification> userNotifications){
        this.list.addAll(userNotifications);
        notifyDataSetChanged();
    }
    public void clearAll(){
        list=new ArrayList<>();
        notifyDataSetChanged();
    }
    public void addItem(UserNotification userNotification){
        this.list.add(userNotification);
        notifyDataSetChanged();
    }
    public void removeItem(UserNotification userNotification){
        int pos=-1;
        try {
            pos=list.indexOf(userNotification);
        }catch (Exception e){

        }
        if(pos!=-1){
            list.remove(pos);
            notifyDataSetChanged();
        }
    }
    @Override
    public NotificationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_notification,parent,false);
        return new NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(final NotificationHolder holder, int position) {
        final UserNotification noti=list.get(position);
        switch (noti.getType()){
            //new store add
            case 1:
                break;
            //new post add
            case 2:
                String textPostAdd="";
                textPostAdd+=noti.getUserEffectName();
                textPostAdd+=" đã thêm 1 bài viết mới vào quán ăn của bạn";
                final Link userNamePostAdd=new Link(noti.getUserEffectName());
                userNamePostAdd.setBold(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    userNamePostAdd.setTextColor(context.getColor(R.color.colorFloat2));
                }else{
                    userNamePostAdd.setTextColor(context.getResources().getColor(R.color.colorFloat2));
                }
                holder.txtTitle.setText(textPostAdd);
                holder.txtTitle.post(new Runnable() {
                    @Override
                    public void run() {
                        LinkBuilder.on(holder.txtTitle).addLink(userNamePostAdd).build();
                    }
                });
                holder.txtTime.setText(noti.getDate());

                break;
            //comment
            case 3:
                String textComment="";
                textComment+=noti.getUserEffectName();
                if(noti.isƠwnPost()){
                    textComment+=" đã thêm 1 bình luận vào bài đăng của bạn";
                }else{
                    textComment+=" đã thêm 1 bình luận vào bài đăng bạn theo dõi";
                }

                final Link userNameComment=new Link(noti.getUserEffectName());
                userNameComment.setBold(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    userNameComment.setTextColor(context.getColor(R.color.colorFloat2));
                }else{
                    userNameComment.setTextColor(context.getResources().getColor(R.color.colorFloat2));
                }
                holder.txtTitle.setText(textComment);
                holder.txtTitle.post(new Runnable() {
                    @Override
                    public void run() {
                        LinkBuilder.on(holder.txtTitle).addLink(userNameComment).build();
                    }
                });
                holder.txtTime.setText(noti.getDate());
                break;
            //new food add
            case 4:
                String textFood="",foodName=noti.getFoodName();
                textFood+=noti.getUserEffectName();
                if(noti.isƠwnPost()){
                    textFood+=" đã thêm món ăn "+foodName+" vào quán ăn của bạn";
                }else{
                    textFood+=" đã thêm món ăn "+foodName+" vào quán ăn của bạn bạn theo dõi";
                }

                Link userNameAddFood=new Link(noti.getUserEffectName());
                userNameAddFood.setBold(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    userNameAddFood.setTextColor(context.getColor(R.color.colorFloat2));
                }else{
                    userNameAddFood.setTextColor(context.getResources().getColor(R.color.colorFloat2));
                }
                Link linkFoodName=new Link(noti.getFoodName());
                linkFoodName.setBold(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    linkFoodName.setTextColor(context.getColor(R.color.color_notify_newpost));
                }else{
                    linkFoodName.setTextColor(context.getResources().getColor(R.color.color_notify_newpost));
                }
                final List<Link> links=new ArrayList<>();
                links.add(linkFoodName);
                links.add(userNameAddFood);
                holder.txtTitle.setText(textFood);
                holder.txtTitle.post(new Runnable() {
                    @Override
                    public void run() {
                        LinkBuilder.on(holder.txtTitle).addLinks(links).build();
                    }
                });
                holder.txtTime.setText(noti.getDate());
                break;
        }
        if(noti.isReaded()){
            holder.imgCheck.setVisibility(View.VISIBLE);
        }else{
            holder.imgCheck.setVisibility(View.INVISIBLE);
        }
        checkStatusUser(holder,noti.getUserEffectId());
        holder.cvBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comunication.onMItemListener.onNotiClick(noti);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class NotificationHolder extends RecyclerView.ViewHolder{
        private TextView txtTitle,txtTime;
        private CircularImageView img;
        private CardView cvBackground;
        private ImageView imgCheck;
        public NotificationHolder(View itemView) {
            super(itemView);
            txtTitle= (TextView) itemView.findViewById(R.id.txt_title);
            txtTime= (TextView) itemView.findViewById(R.id.txt_time);
            img= (CircularImageView) itemView.findViewById(R.id.img_user);
            cvBackground= (CardView) itemView.findViewById(R.id.cardv_storeitem);
            imgCheck= (ImageView) itemView.findViewById(R.id.img_read);
            imgCheck.setVisibility(View.INVISIBLE);
        }
    }
    private void checkStatusUser(final NotificationHolder holder, String id){
        try {
            ValueEventListener userValueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        String key = dataSnapshot.getKey();
                        User user = dataSnapshot.getValue(User.class);
                        user.setuID(key);
                        if(TextUtils.isEmpty(user.getAvatar())){
                            holder.img.setImageResource(R.drawable.ic_account_circle_white_48dp);
                        }else{
                            StorageReference avaRef = stRef.child(user.getAvatar());
                            avaRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.with(context)
                                            .load(uri)
                                            .placeholder(R.drawable.ic_account_circle_white_48dp)
                                            .into(holder.img);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    holder.img.setImageResource(R.drawable.ic_account_circle_white_48dp);
                }
            };
            dbRef.child(context.getString(R.string.users_CODE)+id)
                    .addValueEventListener(userValueListener);
        }catch (Exception e){
            holder.img.setImageResource(R.drawable.ic_account_circle_white_48dp);

        }
    }
}
