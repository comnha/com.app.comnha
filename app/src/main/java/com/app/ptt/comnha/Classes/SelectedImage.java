package com.app.ptt.comnha.Classes;

import android.net.Uri;

import java.io.File;

/**
 * Created by PTT on 4/10/2017.
 */

public class SelectedImage {
    private Uri uri;
    private boolean isSelected;
    private File file;

    public SelectedImage() {
    }

    public SelectedImage(Uri uri, boolean isSelected, File file) {
        this.uri = uri;
        this.isSelected = isSelected;
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
}
