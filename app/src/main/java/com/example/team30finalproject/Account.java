package com.example.team30finalproject;

public class Account {

    public String email;


    public Account() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Account(String email) {
        this.email = email;
    }

}
