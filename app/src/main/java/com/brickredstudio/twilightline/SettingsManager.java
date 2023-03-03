package com.brickredstudio.twilightline;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class SettingsManager
{
    private static SettingsManager _instance = null;

    private SharedPreferences preferences = null;
    private String proxyConfigName = null;
    private boolean perAppProxyEnabled = false;
    private Set<String> proxyApps = null;

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

    @SuppressWarnings("deprecation")
    public SettingsManager()
    {
        this.preferences = App.getContext().getSharedPreferences(
            "main_config", Context.MODE_PRIVATE);
        this.proxyConfigName = this.preferences.getString(
            "proxy_config_name", "");
        this.perAppProxyEnabled = this.preferences.getBoolean(
            "per_app_proxy_enabled", false);
        this.proxyApps = new TreeSet<String>();
        {
            Set<String> p = this.preferences.getStringSet("proxy_apps", null);
            if (p != null) {
                for (String appPackageName : p) {
                    try {
                        App.getContext().getPackageManager()
                            .getPackageInfo(appPackageName, 0);
                    } catch (Exception e) {
                        continue;
                    }
                    this.proxyApps.add(appPackageName);
                }
            }
        }
    }

    public String getProxyConfigName()
    {
        return this.proxyConfigName;
    }

    public void setProxyConfigName(String name)
    {
        this.proxyConfigName = name;

        SharedPreferences.Editor e = this.preferences.edit();
        e.putString("proxy_config_name", this.proxyConfigName);
        e.apply();
    }

    public boolean isPerAppProxyEnabled()
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

    public Set<String> getProxyApps()
    {
        return this.proxyApps;
    }

    public String getProxyAppsString()
    {
        return TextUtils.join("|", this.proxyApps);
    }

    public void addProxyApp(String appPackageName)
    {
        if (this.proxyApps.add(appPackageName)) {
            SharedPreferences.Editor e = this.preferences.edit();
            e.putStringSet("proxy_apps", this.proxyApps);
            e.apply();
        }
    }

    public void removeProxyApp(String appPackageName)
    {
        if (this.proxyApps.remove(appPackageName)) {
            SharedPreferences.Editor e = this.preferences.edit();
            e.putStringSet("proxy_apps", this.proxyApps);
            e.apply();
        }
    }
}
