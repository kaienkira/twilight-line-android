package com.brickredstudio.twilightline;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public final class App extends Application
{
    public static final String NAME = "Twilight Line Proxy";
    public static final String TAG = "TwilightLine";
    public static final int NOTIFICATION_MAIN = 1;
    public static final String NOTIFICATION_CHANNEL_MAIN_ID = "twilight-line";
    public static final String NOTIFICATION_CHANNEL_MAIN_NAME = "twilight-line";

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

        if (AppUtil.getProcessName().contains(":proxy") == false) {
            mainProcessInit();
        }
    }

    private void mainProcessInit()
    {
        SettingsManager.createInstance();
        ProxyManager.createInstance();
        createNotificationChannel();
    }

    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_MAIN_ID,
                NOTIFICATION_CHANNEL_MAIN_NAME,
                NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager =
                getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
