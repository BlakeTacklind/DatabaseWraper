package com.dank.exchange.backend;

public class Item{
    Item(int i, String n){
        id = i;
        name = n;

    }

    public int getID(){return id;}
    public String getName(){return name;}

    public String toString(){
        return name + " " + id;
    }

    private int id;
    private String name;
}
