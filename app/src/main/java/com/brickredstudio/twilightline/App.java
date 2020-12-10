package com.brickredstudio.twilightline;

import android.app.Application;
import android.content.Context;

public class App extends Application
{
    private static App _instance = null;

    public static App getInstance()
    {
        return _instance;
    }

    public static Context getContext()
    {
        return _instance.getApplicationContext();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        _instance = this;
    }
}
