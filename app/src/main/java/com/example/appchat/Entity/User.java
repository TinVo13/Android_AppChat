package com.example.appchat.Entity;

public class User {
    private String uid,hoten,sdt,ngaysinh,image,status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getNgaysinh() {
        return ngaysinh;
    }

    public void setNgaysinh(String ngaysinh) {
        this.ngaysinh = ngaysinh;
    }

//    public User(String hoten, String sdt, String ngaysinh, String image, String status) {
//        this.hoten = hoten;
//        this.sdt = sdt;
//        this.ngaysinh = ngaysinh;
//        this.image = image;
//        this.status = status;
//    }

    public User(String uid, String hoten, String sdt, String ngaysinh, String image, String status) {
        this.uid = uid;
        this.hoten = hoten;
        this.sdt = sdt;
        this.ngaysinh = ngaysinh;
        this.image = image;
        this.status = status;
    }

    public User() {
    }
}
