package com.example.layarkarya.Model;

public class UserDetailsModel {
    public String phone;

    public String getAva_url() {
        return ava_url;
    }

    public void setAva_url(String ava_url) {
        this.ava_url = ava_url;
    }

    public String ava_url;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String address;
    public String city;

    public UserDetailsModel(){

    }

    public UserDetailsModel(String phone, String address, String city, String province, String ava_url) {
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.province = province;
        this.ava_url = ava_url;
    }

    public String province;
}
