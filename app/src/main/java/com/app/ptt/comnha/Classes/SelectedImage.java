package com.app.ptt.comnha.Classes;

import android.net.Uri;

import java.io.File;

/**
 * Created by PTT on 4/10/2017.
 */

public class SelectedImage {
    private Uri uri;
    private boolean state;
    private File file;

    public SelectedImage(Uri uri, boolean state, File file) {
        this.uri = uri;
        this.state = state;
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

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
