package com.app.ptt.comnha.Modules;

import com.app.ptt.comnha.Models.FireBase.Store;

import java.util.Comparator;

/**
 * Created by ciqaz on 09/07/2017.
 */

public class orderByHealthy implements Comparator<Store> {

    @Override
    public int compare(Store o1, Store o2) {
        long o1Size = 1, o2Size = 1;
        if (o1.getSize() != 0) {
            o1Size = o1.getSize();
        }
        if (o2.getSize() != 0) {
            o2Size = o2.getSize();
        }
        return ((o1.getHealthySum() / o1Size) < (o2.getHealthySum() / o2Size) ? 1 :
                (o1.getHealthySum() / o1Size) > (o2.getHealthySum() / o2Size) ? -1 : 0);

    }
}