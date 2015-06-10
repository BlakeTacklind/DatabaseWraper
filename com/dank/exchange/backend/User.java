package com.dank.exchange.backend;

import java.io.Serializable;

public class User implements Serializable {
    User(int i, String n){
        id = i;
        name = n;
    }

    int getID(){return id;}
    public String getName(){return name;}

    public String toString(){
        return name + " " + id;
    }

    private int id;
    private String name;
}
