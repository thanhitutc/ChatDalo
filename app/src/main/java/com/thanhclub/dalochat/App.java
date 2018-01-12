package com.thanhclub.dalochat;


import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;

public class App extends Application {
    private static Context context;
    private Gson mGSon;
    private static App self;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        self = this;
        mGSon = new Gson();
    }

    public static App self(){
        return self;
    }

    public static Context getContext() {
        return context;
    }

    public Gson getGSon() {
        return mGSon;
    }
}
