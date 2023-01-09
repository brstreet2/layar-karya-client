package com.example.layarkarya.Model;

public class UserModel {
    public String fullName, lastName, email;

    public int contentCount;

    public int movieWatched;

    public int getContentCount() {
        return contentCount;
    }

    public void setContentCount(int contentCount) {
        this.contentCount = contentCount;
    }

    public int getMovieWatched() {
        return movieWatched;
    }

    public void setMovieWatched(int movieWatched) {
        this.movieWatched = movieWatched;
    }

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

    public UserModel(String fullName, String lastName, String email, int coin, int contentCount, int movieWatched){
        this.fullName = fullName;
        this.lastName = lastName;
        this.email = email;
        this.coin = coin;
        this.contentCount = contentCount;
        this.movieWatched = movieWatched;
    }

}
