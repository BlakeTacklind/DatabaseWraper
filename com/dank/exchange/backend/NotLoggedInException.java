package com.dank.exchange.backend;

public class NotLoggedInException extends Exception{
    NotLoggedInException(){
        super("User isn't logged in");
    }
}