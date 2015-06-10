package com.dank.exchange.backend;

import java.io.Serializable;
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

Middle Man Trade Request = 20 - Location provide
Middle Man Trade Rejected = 21
Middle Man Trade Accepted = 22 - Location provided
Middle Man Notification = 23
Middle Man Phase 1 = 24
Middle Man Phase 2 = 25
*/
public class Request implements Serializable {
    private int requestID, type;
    private ArrayList<Item> toItems, fromItems;
    private String extraString, extraString2;
    private User from;
    private User to;
    private User Middleman;

    Request(int id, int ty, User frm, User t) {
        requestID = id;
        type = ty;
        from = frm;
        to = t;
        Middleman = null;
        toItems = null;
        fromItems = null;
        extraString = null;
        extraString = null;
    }

    Request(int id, int ty, User frm, User t, User ei, ArrayList<Item> e1, ArrayList<Item> e2, String s1, String s2){
        requestID = id;
        type = ty;
        from = frm;
        to = t;
        Middleman = ei;
        toItems = e1;
        fromItems = e2;
        extraString = s1;
        extraString = s2;
    }

    Request(int id, int ty, User frm, User t, User ei, ArrayList<Item> e1, ArrayList<Item> e2, String s1){
        requestID = id;
        type = ty;
        from = frm;
        to = t;
        Middleman = ei;
        toItems = e1;
        fromItems = e2;
        extraString = s1;
        extraString = null;
    }

    Request(int id, int ty, User frm, User t, ArrayList<Item> e1, ArrayList<Item> e2, String s1){
        requestID = id;
        type = ty;
        from = frm;
        to = t;
        Middleman = null;
        toItems = e1;
        fromItems = e2;
        extraString = s1;
        extraString2 = null;
    }

    Request(int id, int ty, User frm, User t, ArrayList<Item> e1, ArrayList<Item> e2){
        requestID = id;
        type = ty;
        from = frm;
        to = t;
        Middleman = null;
        toItems = e1;
        fromItems = e2;
        extraString = null;
        extraString2 = null;
    }

    int getID(){return requestID;}
    public int getType(){return type;}
    public User getToUser(){return to;}
    public User getFromUser(){return from;}
    public User getMiddlemanUser(){return Middleman;}
    public String getLocation(){return extraString;}
    public String getLocation2(){return extraString2;}
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
