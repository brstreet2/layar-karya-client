package com.example.layarkarya.Model;

public class UserModel {
    public String fullName, lastName, email;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int coin;

    public UserModel(){

    }

    public UserModel(String fullName, String lastName, String email, int coin){
        this.fullName = fullName;
        this.lastName = lastName;
        this.email = email;
        this.coin = coin;
    }

}
