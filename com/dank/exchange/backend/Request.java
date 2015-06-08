package com.dank.exchange.backend;

/* TYPES
friendship requested = 1
friendship Accepted = 2
friendship Rejected = 3

Trade Requested = 10
Trade Denied = 11
Request Location = 12 (assumed accepted)
Location Denied - new location = 13
Location Accepted = 14
Trade Completed = 15

MiddleMan Requested = 20
MiddleMan Accepted = 21
MiddleMan Rejected = 22
*/
public class Request{
    private int requestID, type, fromID, toID, extraInt;
    private int[] extra1, extra2;
    private String extrastring;

    public Request(int id, int ty, int frm, int to, int ei,int[] e1, int[] e2, String es){
        requestID = id;
        type = ty;
        fromID = frm;
        toID = to;
        extra1 = e1;
        extra2 = e2;
        extrastring = es;
        extraInt = ei;
    }

    public String toString(){
        return "requestID: " + requestID + ", type: " + type + ", fromID: " + fromID +
                ", toID: " + toID + " , extraInt: " + extraInt + ", extra1: " + extra1 + ", extra2: " + extra2 +
                ", extrastring: \"" + extrastring + "\".";
    }
}
