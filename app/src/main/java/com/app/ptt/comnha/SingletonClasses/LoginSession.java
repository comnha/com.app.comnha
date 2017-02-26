package com.app.ptt.comnha.SingletonClasses;

/**
 * Created by PTT on 9/27/2016.
 */
public class LoginSession {
    private static LoginSession ourInstance;
    private String userID, username, email, tinh, huyen, ten, ho, tenlot, ngaysinh, password;

    public boolean getRole() {
        return role;
    }

    public void setRole(boolean role) {
        this.role = role;
    }

    boolean role;
    public static LoginSession getInstance() {
        if (ourInstance == null) {
            ourInstance = new LoginSession();
        }
        return ourInstance;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNgaysinh(String ngaysinh) {
        this.ngaysinh = ngaysinh;
    }

    public String getNgaysinh() {
        return ngaysinh;
    }

    private LoginSession() {
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public void setTenlot(String tenlot) {
        this.tenlot = tenlot;
    }

    public String getTen() {

        return ten;
    }

    public String getHo() {
        return ho;
    }

    public String getTenlot() {
        return tenlot;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setTinh(String tinh) {
        this.tinh = tinh;
    }

    public void setHuyen(String huyen) {
        this.huyen = huyen;
    }

    public String getTinh() {

        return tinh;
    }

    public String getHuyen() {
        return huyen;
    }
}
