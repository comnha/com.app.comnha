package com.app.ptt.comnha.SingletonClasses;

import com.app.ptt.comnha.FireBase.Store;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by PTT on 10/24/2016.
 */
public class DoPost {
    private static DoPost ourInstance;
    private long gia, vesinh, phucvu;
    private ArrayList<File> files;
    private Store store;
    public static DoPost getInstance() {
        if (ourInstance == null) {
            ourInstance = new DoPost();
        }
        return ourInstance;
    }

    private DoPost() {
    }
    public void setPhucvu(long phucvu) {
        this.phucvu = phucvu;
    }

    public void setVesinh(long vesinh) {
        this.vesinh = vesinh;
    }

    public void setGia(long gia) {
        this.gia = gia;
    }

    public long getGia() {
        return gia;
    }

    public long getVesinh() {
        return vesinh;
    }

    public long getPhucvu() {
        return phucvu;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
