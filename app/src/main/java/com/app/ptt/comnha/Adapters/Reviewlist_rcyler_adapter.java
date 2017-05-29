package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.R;

import java.util.ArrayList;

/**
 * Created by PTT on 10/4/2016.
 */

public class Reviewlist_rcyler_adapter extends RecyclerView.Adapter<Reviewlist_rcyler_adapter.ViewHolder> {
    ArrayList<Post> list;
    Activity activity;
    ArrayList<String> bitmapList;
    public int previuosPosition = 0;
    public static int type;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_un, txt_postdate, txt_tenmon,
                txt_gia, txt_likenumb, txt_commentnumb, txt_time, txt_tiltereview, txt_diachi, txt_ratingtext,txt_tenquan;
        public RatingBar ratingBar;
        public ImageView img_user;

        public ViewHolder(View view) {
            super(view);
//            if(type==2){
//                txt_time = (TextView) view.findViewById(R.id.food_txtv_time);
//                txt_un = (TextView) view.findViewById(R.id.food_txtv_username);
//                txt_postdate = (TextView) view.findViewById(R.id.food_txtv_postdate);
//                txt_likenumb = (TextView) view.findViewById(R.id.food_txtv_likenumb);
//                txt_commentnumb = (TextView) view.findViewById(R.id.food_txtv_commentnumb);
//                txt_tiltereview = (TextView) view.findViewById(R.id.txt_tiltereview);
//                ratingBar= (RatingBar) view.findViewById(R.id.ratingbar_item);
//
//                txt_ratingtext=(TextView) view.findViewById(R.id.rating_text);
//            }else{
//                txt_time = (TextView) view.findViewById(R.id.post_txtv_time);
//                txt_un = (TextView) view.findViewById(R.id.post_txtv_un);
//                txt_tenmon = (TextView) view.findViewById(R.id.review_txtv_tenmon);
//                txt_postdate = (TextView) view.findViewById(R.id.post_txtv_postdate);
//                txt_gia = (TextView) view.findViewById(R.id.review_txtv_gia);
//                txt_likenumb = (TextView) view.findViewById(R.id.post_txtv_likenumb);
//                txt_commentnumb = (TextView) view.findViewById(R.id.post_txtv_commentnumb);
//                txt_tenquan = (TextView) view.findViewById(R.id.post_txtv_storename);
//                txt_diachi = (TextView) view.findViewById(R.id.review_txtv_diachi);
//                img_user = (ImageView) view.findViewById(R.id.review_img_quan);
//            }


        }
    }
    public Reviewlist_rcyler_adapter(ArrayList<Post> list, Activity activity,int type) {
        this.list = list;
        this.activity = activity;
        bitmapList=new ArrayList<>();
        Reviewlist_rcyler_adapter.type =type;


    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
//        if(type==2){
////            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcyler_fooddetail, parent, false);
//        }else
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcyler_review_food, parent, false);

        return new ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(type==2){
//            if (foods.get(position).getType() == 1) {
//                holder.ratingBar.setNumStars(3);
//                holder.ratingBar.setIsIndicator(true);
//                holder.ratingBar.setStepSize(1);
//                float a =  foods.get(position).getFood().getDanhGia();
//                holder.ratingBar.setRating(a);
//                if (a < 1.5) {
//                    holder.txt_ratingtext.setText("Dở tệ");
//                }
//                if (a > 1.5 &&a < 2.5) {
//                    holder.txt_ratingtext.setText("Bình thường");
//                }
//                if (a >= 2.5) {
//                    holder.txt_ratingtext.setText("Ngon tuyệt");
//                }
//            }
//            holder.txt_tiltereview.setText(foods.get(position).getTitle());

        }else {
//            if (foods.get(position).getType() == 1) {
//                holder.txt_tenmon.setText(foods.get(position).getFood().getName());
//                holder.txt_price.setText(foods.get(position).getFood().getGia() + " đ");
//            } else {
//                holder.txt_tenmon.setText("");
//                holder.txt_price.setText("");
//            }
//            if (foods.get(position).getHinh() != null)
//                Picasso.with(activity)
//                        .load(foods.get(position).getHinh())
//                        .into(holder.img_user);
//
//            if(type==3) {
//                holder.txtv_storename.setText(foods.get(position).getUserName());
//                holder.txtv_address.setText(foods.get(position).getTitle());
//            }else{
//                holder.txtv_storename.setText(foods.get(position).getLocaName());
//                holder.txtv_address.setText(foods.get(position).getDiachi());
//            }

        }
//        holder.txt_postdate.setText(foods.get(position).getDate());
//        holder.txt_time.setText(foods.get(position).getTime());
//        holder.txt_un.setText(foods.get(position).getUserName());
//        holder.txt_likenumb.setText(String.valueOf(foods.get(position).getLikeCount()) + " Likes");
//        holder.txt_commentnumb.setText(String.valueOf(foods.get(position).getCommentCount()) + " Comments");

        //MyService.setFinish(true);
        if (position > previuosPosition) {
            AnimationUtils.animateItemRcylerV(holder, true);

        } else {
            AnimationUtils.animateItemRcylerV(holder, false);

        }

        previuosPosition = position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
