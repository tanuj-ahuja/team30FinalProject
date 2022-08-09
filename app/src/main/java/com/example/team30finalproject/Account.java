package com.example.team30finalproject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Account {

    public String email;
    public ArrayList uploads;

    public Account() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Account(String email) {
        this.email = email;
        this.uploads = new ArrayList<Produce>();
    }

    public Account(String email, ArrayList<Produce> uploads) {
        this.email = email;
        this.uploads = uploads;
    }

}
