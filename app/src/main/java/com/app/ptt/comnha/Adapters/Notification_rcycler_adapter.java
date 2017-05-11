package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.ptt.comnha.Activity.AdapterActivity;
import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Models.FireBase.Notification;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
import com.app.ptt.comnha.SingletonClasses.ChooseNoti;

import java.util.ArrayList;

/**
 * Created by cuong on 12/22/2016.
 */

public class Notification_rcycler_adapter extends RecyclerView.Adapter<Notification_rcycler_adapter.ViewHolder> {
    ArrayList<Notification> list;
    Activity activity;
    int previuosPosition = 0;
    int type;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_date, txt_time, txt_noidung;
        public LinearLayout ll_notification;


        public ViewHolder(View view) {
            super(view);
            ll_notification=(LinearLayout) view.findViewById(R.id.ll_item_notification);
            txt_date=(TextView) view.findViewById(R.id.txt_Ngay);
            txt_time=(TextView) view.findViewById(R.id.txt_Gio);
            txt_noidung=(TextView) view.findViewById(R.id.txt_Noidung);
        }
    }
    public Notification_rcycler_adapter(ArrayList<Notification> list, Activity activity,int a) {
        this.list = list;
        this.activity = activity;
        type=a;
    }
    @Override
    public Notification_rcycler_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcyler_notification, parent, false);

        return new Notification_rcycler_adapter.ViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder( final ViewHolder holder,final int position) {
        if(type==1) {

            if (!list.get(position).getReaded()) {
                holder.txt_date.setTypeface(null, Typeface.BOLD);
                holder.txt_time.setTypeface(null, Typeface.BOLD);
                holder.txt_noidung.setTypeface(null, Typeface.BOLD);
            } else {
                holder.txt_date.setTypeface(null, Typeface.NORMAL);
                holder.txt_time.setTypeface(null, Typeface.NORMAL);
                holder.txt_noidung.setTypeface(null, Typeface.NORMAL);
            }
            holder.txt_date.setText(list.get(position).getDate());
            holder.txt_time.setText(list.get(position).getTime());
            {


                //LOCATION
//                if (foods.get(position).getType() == 1) {
//                    String text = "Món ăn:  " + foods.get(position).getFood().getName() + " mà bạn đã thêm vào quán " + foods.get(position).getStore().getName() + " đã được admin chấp nhận" + "\n.";
//                    holder.txt_noidung.setText(text);
//                }
//                if(foods.get(position).getType()==2){
//                    String text = "Quán ăn: "+foods.get(position).getStore().getName() +" mà bạn đã thêm đã được admin kiểm duyệt và chấp nhận. Cám ơn bạn đã đóng góp!!";
//                    holder.txt_noidung.setText(text);
//                }
//                if(foods.get(position).getType()==3){
//                    String text =  "Bài reiview: "+foods.get(position).getStore().getName() +" mà bạn đã thêm vào quán"+foods.get(position).getStore().getName()+" đã được admin kiểm duyệt và chấp nhận. Cám ơn bạn đã đóng góp!!";
//                    holder.txt_noidung.setText(text);
//                }
//                if(foods.get(position).getType()==4){
//                    String text =  "Admin chưa chấp nhận quán ăn "+foods.get(position).getStore().getName()+" của bạn với lý do : " +
//                            ""+foods.get(position).getReason().toString()+". Vui lòng sửa lại";
//                    holder.txt_noidung.setText(text);
//                }
//                if(foods.get(position).getType()==5){
//                    String text =  "Quán ăn "+ foods.get(position).getStore().getName()+" của bạn bị report bởi "+foods.get(position).getAccount().getUsername()+" với lý do"+ foods.get(position).getReason()
//                            +". Admin đã xác nhận report và xóa quán ăn của bạn";
//                    holder.txt_noidung.setText(text);
//                }
//                if(foods.get(position).getType()==6){
//                    String text =  "Report của bạn về quán ăn "+ foods.get(position).getStore().getName()+" đã được chấp thuận. Cám ơn bạn";
//                    holder.txt_noidung.setText(text);
//                }
//                if(foods.get(position).getType()==7){
//                    String text =  "Admin chưa chấp nhận bài đăng "+foods.get(position).getPost().getTitle()+" của bạn với lý do : " +
//                            ""+foods.get(position).getReason().toString()+". Vui lòng sửa lại";
//                    holder.txt_noidung.setText(text);
//                }
//
//
//
//                //POST
//                if(foods.get(position).getType()==7){
//                    String text = "Bài đăng: "+foods.get(position).getPost().getTitle() +" mà bạn đã thêm đã được admin kiểm duyệt và chấp nhận. Cám ơn bạn đã đóng góp!!";
//                    holder.txt_noidung.setText(text);
//                }
//                if(foods.get(position).getType()==8){
//                    String text =  "Bài đăng "+ foods.get(position).getPost().getTitle()+" của bạn bị report bởi "+foods.get(position).getAccount().getUsername()+" với lý do"+ foods.get(position).getReason()
//                            +". Admin đã xác nhận report và xóa quán ăn của bạn";
//                    holder.txt_noidung.setText(text);
//                }
//                if(foods.get(position).getType()==9){
//                    String text =  "Report của bạn về bài đăng "+ foods.get(position).getPost().getTitle()+" đã được chấp thuận. Cám ơn bạn";
//                    holder.txt_noidung.setText(text);
//                }
//                if(foods.get(position).getType()==10){
//                    String text =  "Bài đăng "+ foods.get(position).getPost().getTitle()+" đã được xóa";
//                    holder.txt_noidung.setText(text);
//                }
//
            }
        }

        //MyService.setFinish(true);
        if (position > previuosPosition) {
            AnimationUtils.animateItemRcylerV(holder, true);

        } else {
            AnimationUtils.animateItemRcylerV(holder, false);

        }
        //if(foods.get(position).getIndex().equals("user")) {
            holder.ll_notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity.getApplicationContext(), AdapterActivity.class);
                    intent.putExtra(activity.getString(R.string.fragment_CODE),
                            activity.getString(R.string.frag_storedetail_CODE));
                    Store location=list.get(position).getLocation();
                    ChooseStore.getInstance().setStore(location);
                    ChooseNoti.getInstance().setNotification(list.get(position));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    if (!list.get(position).getReaded()) {
                        holder.txt_date.setTypeface(null, Typeface.NORMAL);
                        holder.txt_time.setTypeface(null, Typeface.NORMAL);
                        holder.txt_noidung.setTypeface(null, Typeface.NORMAL);
                        MyService.setPosNotification(position);
                    }
                }
            });
        //}
        previuosPosition = position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
