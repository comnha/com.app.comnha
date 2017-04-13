package com.app.ptt.comnha.Adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.ptt.comnha.Classes.SelectedImage;
import com.app.ptt.comnha.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by PTT on 4/10/2017.
 */

public class SingleImageImportRvAdapter extends RecyclerView.Adapter<SingleImageImportRvAdapter.ViewHolder> {
    Cursor imageCursor;
    Context context;
    ContentResolver cr;
    ArrayList<SelectedImage> selectedImages;
    int firstPosition = -1;
    OnSingleClickListener onSingleClickListener;
    public interface OnSingleClickListener{
        void onClick(boolean isDismiss);
    }

    public void setOnSingleClickListener(OnSingleClickListener onSingleClickListener) {
        this.onSingleClickListener = onSingleClickListener;
    }

    public SingleImageImportRvAdapter(Context context, ContentResolver cr) {
        this.cr = cr;
        this.notifyDataSetChanged();
        this.context = context;
        selectedImages = new ArrayList<>();
        transtoarray();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imageimport_rv, parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.img.setImageURI(selectedImages.get(position).getUri());
        Log.d("datauri", selectedImages.get(position).getUri().toString());
        if (!selectedImages.get(position).isSelected()) {
            holder.selected_imgv.setBackground(context.getResources().getDrawable(R.drawable.checked_bound));
            Log.i("checkbox " + position, "false");
        } else {
            Log.i("checkbox1 " + position, "true");
            holder.selected_imgv.setBackground(context.getResources().getDrawable(R.drawable.selected_img_bound));
        }
        holder.selected_imgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedImages.get(position).isSelected()) {
//                    holder.selected_imgv.setBackground(context.getResources().getDrawable(R.drawable.checked_bound));
                    selectedImages.get(position).setSelected(false);
                    firstPosition = position;
                } else {
//                    holder.selected_imgv.setBackground(context.getResources().getDrawable(R.drawable.selected_img_bound));
                    if (firstPosition < 0) {//lần đầu show dialog, chưa chọn
                        selectedImages.get(position).setSelected(true);
                    } else {
                        selectedImages.get(position).setSelected(true);
                        selectedImages.get(firstPosition).setSelected(false);
                    }
                    firstPosition = position;
                }
                onSingleClickListener.onClick(true);

            }
        });
    }

    public SelectedImage getSelectedImage() {
        SelectedImage selectedImage = new SelectedImage();
        for (SelectedImage item : selectedImages) {
            if (item.isSelected()) {
                selectedImage = item;
            }
        }
        Log.d("SelectedImage", selectedImages.indexOf(selectedImage) + "");
        return selectedImage;
    }


    @Override
    public int getItemCount() {
        Log.d("getItemCount", "" + imageCursor.getCount());
        return selectedImages.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img, selected_imgv;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.imgV_imageimport);
            selected_imgv = (ImageView) itemView.findViewById(R.id.imgv_selected_imageimport);
        }
    }

    private void transtoarray() {
        String[] projecttion = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
        };
        imageCursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projecttion, null, null, null);
        if (imageCursor != null && imageCursor.getCount() > 0) {
            for (int i = 0; i < imageCursor.getCount(); i++) {
                imageCursor.moveToPosition(i);
                Uri datauri = Uri.parse(imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                File datafile = new File(imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                selectedImages.add(new SelectedImage(datauri, false, datafile));
            }
        }
    }
}
