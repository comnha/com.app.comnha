package com.app.ptt.comnha.Utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by cuong on 11/2/2016.
 */

public class PlaceAttribute {
    private String fullname;

    public String getAddressNum() {
        return addressNum;
    }

    public void setAddressNum(String address) {
        this.addressNum = address;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    private String ID;
    private String addressNum;
    private String locality;
    private String district;
    private String state;

    public LatLng getPlaceLatLng() {
        return placeLatLng;
    }

    public void setPlaceLatLng(LatLng placeLatLng) {
        this.placeLatLng = placeLatLng;
    }

    LatLng placeLatLng;
    public PlaceAttribute() {
    }


    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }



    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

}
