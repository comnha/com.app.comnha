package com.app.ptt.comnha.Models.FireBase;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by PTT on 9/16/2016.
 */
public class User {
    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUn() {
        return un;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public boolean isAddfoodBlocked() {
        return isAddfoodBlocked;
    }

    public void setAddfoodBlocked(boolean addfoodBlocked) {
        isAddfoodBlocked = addfoodBlocked;
    }

    public boolean isWritepostBlocked() {
        return isWritepostBlocked;
    }

    public void setWritepostBlocked(boolean writepostBlocked) {
        isWritepostBlocked = writepostBlocked;
    }

    public boolean isAddstoreBlocked() {
        return isAddstoreBlocked;
    }

    public void setAddstoreBlocked(boolean addstoreBlocked) {
        isAddstoreBlocked = addstoreBlocked;
    }

    public boolean isReportpostBlocked() {
        return isReportpostBlocked;
    }

    public void setReportpostBlocked(boolean reportpostBlocked) {
        isReportpostBlocked = reportpostBlocked;
    }

    public boolean isReportstoreBlocked() {
        return isReportstoreBlocked;
    }

    public void setReportstoreBlocked(boolean reportstoreBlocked) {
        isReportstoreBlocked = reportstoreBlocked;
    }

    public boolean isReportimgBlocked() {
        return isReportimgBlocked;
    }

    public void setReportimgBlocked(boolean reportimgBlocked) {
        isReportimgBlocked = reportimgBlocked;
    }

    public boolean isReportfoodBlocked() {
        return isReportfoodBlocked;
    }

    public void setReportfoodBlocked(boolean reportfoodBlocked) {
        isReportfoodBlocked = reportfoodBlocked;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    String uID;
    String ho;
    String ten;
    String tenlot;
    String birth;
    String pass;
    String un;
    String email;
    String//more detail
            street = "";
    String ward = "";
    String district = "";
    String province = "";
    String phonenumb = "";
    String work = "";
    String avatar = "";


    String address="";
    boolean sexual = false,
            isAddfoodBlocked = false,
            isWritepostBlocked = false,
            isAddstoreBlocked = false,
            isReportpostBlocked = false,
            isReportstoreBlocked = false,
            isReportimgBlocked = false,
            isReportfoodBlocked = false;
    int role = 0;
    //phép kết
    String dist_prov = "";//tìm user theo tỉnh_huyện

    public User() {
    }

    public User(String un, String email, String pass,
                String ho, String ten, String tenlot, String birth) {
        this.un = un;
        this.email = email;
        this.pass = pass;
        this.ho = ho;
        this.ten = ten;
        this.tenlot = tenlot;
        this.birth = birth;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("un", un);
        result.put("pass", pass);
        result.put("email", email);
        result.put("ho", ho);
        result.put("tenlot", tenlot);
        result.put("ten", ten);
        result.put("birth", birth);
        result.put("street", street);
        result.put("ward", ward);
        result.put("district", district);
        result.put("province", province);
        result.put("phonenumb", phonenumb);
        result.put("work", work);
        result.put("sexual", sexual);
        result.put("dist_prov", dist_prov);
        result.put("role", role);
        result.put("avatar", avatar);
        result.put("address",address);
        result.put("isAddfoodBlocked", isAddfoodBlocked);
        result.put("isWritepostBlocked", isWritepostBlocked);
        result.put("isAddstoreBlocked", isAddstoreBlocked);
        result.put("isReportpostBlocked", isReportpostBlocked);
        result.put("isReportstoreBlocked", isReportstoreBlocked);
        result.put("isReportimgBlocked", isReportimgBlocked);
        result.put("isReportfoodBlocked", isReportfoodBlocked);
        return result;
    }
}
