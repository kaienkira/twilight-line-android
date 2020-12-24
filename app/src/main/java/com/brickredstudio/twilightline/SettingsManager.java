package com.brickredstudio.twilightline;

import android.content.Context;
import android.content.SharedPreferences;

public final class SettingsManager
{
    private static SettingsManager _instance = null;

    private SharedPreferences preferences = null;
    private boolean perAppProxyEnabled = false;

    public static void createInstance()
    {
        if (_instance == null) {
            _instance = new SettingsManager();
        }
    }

    public static void releaseInstance()
    {
        if (_instance != null) {
            _instance = null;
        }
    }

    public static SettingsManager getInstance()
    {
        return _instance;
    }

    public SettingsManager()
    {
        this.preferences = App.getContext().getSharedPreferences(
            "main_config", Context.MODE_PRIVATE);
        this.perAppProxyEnabled = this.preferences.getBoolean(
            "per_app_proxy_enabled", false);
    }

    public boolean getPerAppProxyEnabled()
    {
        return this.perAppProxyEnabled;
    }

    public void setPerAppProxyEnabled(boolean enabled)
    {
        this.perAppProxyEnabled = enabled;

        SharedPreferences.Editor e = this.preferences.edit();
        e.putBoolean("per_app_proxy_enabled", this.perAppProxyEnabled);
        e.apply();
    }
}
