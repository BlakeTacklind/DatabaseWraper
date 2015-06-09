package com.dank.exchange.backend;

import java.util.ArrayList;

/* TYPES
friendship requested = 1
friendship Accepted = 2
friendship Rejected = 3

Trade Requested = 10 - your items withheld
Trade Denied = 11
Trade Accepted - Request Location = 12 - both items withheld
Location Denied - new location = 13
Trade Cancelled = 14 - items return to owners knapsack
Trade Completed = 15 - After cleared, items switch owners

Middle Man Requested = 20
Middle Man Accepted = 21
Middle Man Rejected = 22
    OR
Middle Man Notification = 25
*/
public class Request{
    private int requestID, type;
    private ArrayList<Item> toItems, fromItems;
    private String extraString;
    private User from;
    private User to;
    private User Middleman;

    Request(int id, int ty, User frm, User t, User ei, ArrayList<Item> e1, ArrayList<Item> e2, String es){
        requestID = id;
        type = ty;
        from = frm;
        to = t;
        Middleman = ei;
        toItems = e1;
        fromItems = e2;
        extraString = es;
    }

    public int getID(){return requestID;}
    public int getType(){return type;}
    public User getToUser(){return to;}
    public User getFromUser(){return from;}
    public User getMiddlemanUser(){return Middleman;}
    public String getLocation(){return extraString;}
    public ArrayList<Item> getToItems(){return toItems;}
    public ArrayList<Item> getFromItems(){return fromItems;}

    public String toString(){
        String s = "requestID: " + requestID + ", type: " + type + ", from: " + from +
                ", to: " + to + " , middle man: " + Middleman + ", toItems: " + toItems + ", " +
                "fromItems: " + fromItems + ", location: \"" + extraString + "\" itemsTo: ";

        if(toItems != null)
            for (Item i: toItems)
                s += (i + ", ");
        else
            s += "null";

        s += " itemsFrom: ";
        if(fromItems != null)
            for (Item i: fromItems)
                s += (i + ", ");
        else
            s += "null";

        return s + ".";
    }
}
