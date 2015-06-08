package com.dank.exchange.backend;

public class NotLoggedInException extends Exception{
    public NotLoggedInException(){
        super("User isn't logged in");
    }
}