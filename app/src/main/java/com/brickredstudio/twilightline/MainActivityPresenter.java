package com.brickredstudio.twilightline;

import android.util.Log;

public final class MainActivityPresenter
{
    private static final String TAG = "TwilightLine";

    private MainActivity activity = null;

    public MainActivityPresenter(MainActivity activity)
    {
        this.activity = activity;

        ProxyManager.createInstance();
    }

    public void startProxy()
    {
        Log.i(TAG, "start proxy");
        this.activity.setStartProxySwitchEnabled(false);
        ProxyManager.getInstance().start(
            (success) -> {
                if (success == false) {
                    activity.setStartProxySwitchChecked(false);
                }
                activity.setStartProxySwitchEnabled(true);
            });
    }

    public void stopProxy()
    {
        Log.i(TAG, "stop proxy");
        ProxyManager.getInstance().stop();
    }
}
