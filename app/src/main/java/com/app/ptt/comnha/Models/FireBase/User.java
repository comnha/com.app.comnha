package com.app.ptt.comnha.Models.FireBase;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by PTT on 9/16/2016.
 */
public class User {
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getHo() {
        return ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getTenlot() {
        return tenlot;
    }

    public void setTenlot(String tenlot) {
        this.tenlot = tenlot;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPhonenumb() {
        return phonenumb;
    }

    public void setPhonenumb(String phonenumb) {
        this.phonenumb = phonenumb;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getPro_imgName() {
        return pro_imgName;
    }

    public void setPro_imgName(String pro_imgName) {
        this.pro_imgName = pro_imgName;
    }

    public boolean isSexual() {
        return sexual;
    }

    public void setSexual(boolean sexual) {
        this.sexual = sexual;
    }

    public String getDist_prov() {
        return dist_prov;
    }

    public void setDist_prov(String dist_prov) {
        this.dist_prov = dist_prov;
    }

    String userID,
            ho,
            ten,
            tenlot,
            birth,
            email,
    //more detail
    street = "",
            ward = "",
            district = "",
            province = "",
            phonenumb = "",
            work = "";
    //profile image
    String pro_imgName;//tên ảnh đại diện

    boolean sexual = false;
    int permission=0;

    //phép kết
    String dist_prov;//tìm user theo tỉnh_huyện

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userID", userID);
        result.put("ho", ho);
        result.put("tenlot", tenlot);
        result.put("ten", ten);
        result.put("birth", birth);
        result.put("email", email);
        result.put("permission", String.valueOf(permission));
        return result;
    }
}
