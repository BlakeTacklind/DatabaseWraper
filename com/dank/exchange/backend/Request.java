package com.dank.exchange.backend;

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
    private int requestID, type, fromID, toID, mmID;
    private int[] toItems, fromItems;
    private String extraString;

    public Request(int id, int ty, int frm, int to, int ei,int[] e1, int[] e2, String es){
        requestID = id;
        type = ty;
        fromID = frm;
        toID = to;
        toItems = e1;
        fromItems = e2;
        extraString = es;
        mmID = ei;
    }

    public String toString(){
        return "requestID: " + requestID + ", type: " + type + ", fromID: " + fromID +
                ", toID: " + toID + " , mmID: " + mmID + ", toItems: " + toItems + ", fromItems: " + fromItems +
                ", extrastring: \"" + extraString + "\".";
    }
}
