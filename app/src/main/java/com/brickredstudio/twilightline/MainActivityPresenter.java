package com.brickredstudio.twilightline;

import android.util.Log;

public final class MainActivityPresenter
{
    private MainActivity activity = null;

    public MainActivityPresenter(MainActivity activity)
    {
        this.activity = activity;
    }

    public void startProxy()
    {
        Log.i(App.TAG, "start proxy");
        this.activity.setStartProxySwitchEnabled(false);
        this.activity.prepareVpnService();
    }

    public void stopProxy()
    {
        Log.i(App.TAG, "stop proxy");
        this.activity.setStartProxySwitchEnabled(false);

        ProxyManager.getInstance().stop(
            () -> {
                activity.setStartProxySwitchEnabled(true);
            });
    }

    public void onPrepareVpnServiceResult(boolean prepareResult)
    {
        if (prepareResult == false) {
            activity.setStartProxySwitchChecked(false);
            activity.setStartProxySwitchEnabled(true);
            return;
        }

        ProxyManager.getInstance().start(
            () -> {
                activity.setStartProxySwitchEnabled(true);
            });
    }
}
