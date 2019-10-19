package com.example.oandshooter.utils;

import android.content.Context;
import android.content.Intent;

import java.util.concurrent.TimeUnit;

public class MyData {
    private static final MyData ourInstance = new MyData();

    public static MyData getInstance() {
        return ourInstance;
    }
    public static String email = "" ;
    public static Intent i = null;
    public static int as = 30000;
    //public static int minute = (int) TimeUnit.MINUTES.toMillis(1);
    public static int time = 60000 ;
    public static Context context = null;
    private MyData() {


    }
}
