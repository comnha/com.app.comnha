package com.app.ptt.comnha.Modules;

import com.app.ptt.comnha.Models.FireBase.Store;

import java.util.Comparator;

/**
 * Created by ciqaz on 09/07/2017.
 */

public class orderByStoreHightLight implements Comparator<Store> {

    @Override
    public int compare(Store o1, Store o2) {
        return (o1.getRateAVG() < o2.getRateAVG() ? 1 :
                o1.getRateAVG() > o2.getRateAVG() ? -1 : 0);

    }
}