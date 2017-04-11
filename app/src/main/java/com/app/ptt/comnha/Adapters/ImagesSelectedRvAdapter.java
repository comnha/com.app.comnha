package com.app.ptt.comnha.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.ptt.comnha.Classes.SelectedImage;
import com.app.ptt.comnha.R;

import java.util.ArrayList;

/**
 * Created by PTT on 4/10/2017.
 */

public class ImagesSelectedRvAdapter extends RecyclerView.Adapter<ImagesSelectedRvAdapter.ViewHolder> {
    Context context;
    ArrayList<SelectedImage> selectedImages;

    public ImagesSelectedRvAdapter(Context context, ArrayList<SelectedImage> selectedImages) {
        this.context = context;
        this.selectedImages = selectedImages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imageselected_rv, parent, false));

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("onBindViewHolder", selectedImages.get(position).isState() + "");
//        holder.img.setBackgroundColor(context.getResources().getColor(android.R.color.black));
        if (selectedImages.get(position).isState()) {
            holder.img.setImageURI(selectedImages.get(position).getUri());
        }
    }


    @Override
    public int getItemCount() {
        return selectedImages.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.imgV_imageselected);
        }
    }

}
