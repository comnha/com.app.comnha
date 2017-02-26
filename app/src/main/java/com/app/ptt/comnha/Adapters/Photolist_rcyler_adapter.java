package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.app.ptt.comnha.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by PTT on 10/23/2016.
 */

public class Photolist_rcyler_adapter extends RecyclerView.Adapter<Photolist_rcyler_adapter.ViewHolder> {
    private Cursor mMediaStoreCursor;
    private final Activity mActivity;
    ArrayList<File> paths;

    public Photolist_rcyler_adapter(Activity mActivity) {
        this.mActivity = mActivity;
        paths = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_rcyler_choosept, parent, false);
        return new ViewHolder(view);
    }

    private boolean clicked;

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Bitmap bitmap = getBitmapFromMediaStore(position);

        mMediaStoreCursor.moveToPosition(position);
        Log.i("onbindView", position + "");
        final File path = new File(mMediaStoreCursor.getString(mMediaStoreCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
        if (bitmap != null) {
            holder.getImageView().setImageBitmap(bitmap);
            try {
                if (paths.size() > 0) {
                    Log.i("size", "lon hon 0");
                    if (paths.get(paths.indexOf(path)) == null) {
                        holder.checkBox.setChecked(false);
                        Log.i("checkbox " + position, "false");
                    } else {
                        Log.i("checkbox1 " + position, "true");
                        holder.checkBox.setChecked(true);
                        Log.i("path", path.toString());
                        Log.i("path1", paths.get(paths.indexOf(path)).toString());
                    }
                }
            } catch (ArrayIndexOutOfBoundsException mess) {
                Log.i("checkbox2 " + position, "false");
                holder.checkBox.setChecked(false);
            }
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.checkBox.isChecked()) {
                        holder.checkBox.setChecked(false);
                        paths.remove(paths.indexOf(path));
                    } else {
                        holder.checkBox.setChecked(true);
                        paths.add(path);
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return (mMediaStoreCursor == null) ? 0 : mMediaStoreCursor.getCount();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_rcyler_chosept_imgV);
            checkBox = (CheckBox) itemView.findViewById(R.id.item_rcyler_chosept_chbox);
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

    private Cursor swapCursor(Cursor cursor) {
        if (mMediaStoreCursor == cursor) {
            return null;
        }
        Cursor oldCursor = mMediaStoreCursor;
        this.mMediaStoreCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    public void changeCursor(Cursor cursor) {
        Cursor oldCursor = swapCursor(cursor);
        if (oldCursor != null) {
            oldCursor.close();
        }
    }

    public ArrayList<File> getFiles() {
        return paths;
    }

    private Bitmap getBitmapFromMediaStore(int position) {
        int dataIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Images.Media.DATA);
        mMediaStoreCursor.moveToPosition(position);
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        bitmap = BitmapFactory.decodeFile(
                mMediaStoreCursor.getString(dataIndex), options);
        return bitmap;
    }
}
