package com.dank.exchange.backend;

public class Item{
    public Item(int i, String n){
        id = i;
        name = n;
    }

    public int id(){return id;}
    public String name(){return name;}

    private int id;
    private String name;
}