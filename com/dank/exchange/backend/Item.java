package com.dank.exchange.backend;

import java.io.Serializable;

public class Item implements Serializable{
    Item(int i, String n){
        id = i;
        name = n;
        inTrade = false;
    }

    Item(int i , String n, Boolean b){
        id = i;
        name = n;
        inTrade = b;
    }

    int getID(){return id;}
    public String getName(){return name;}
    public boolean getInTrade(){return inTrade;}

    public String toString(){
        return name + " " + id;
    }

    private int id;
    private String name;
    private Boolean inTrade;
}
