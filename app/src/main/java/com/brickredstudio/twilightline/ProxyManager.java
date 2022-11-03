package com.brickredstudio.twilightline;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import androidx.core.content.ContextCompat;

public final class ProxyManager
{
    public enum Status
    {
        IDLE,
        STARTING,
        RUNNING,
        STOPPING,
    }

    @FunctionalInterface
    public interface StartFinishCallback
    {
        void run();
    }

    @FunctionalInterface
    public interface StopFinishCallback
    {
        void run();
    }

    private static ProxyManager _instance = null;

    private Status status = Status.IDLE;
    private Messenger selfMessenger = null;
    private ServiceConnection proxyServiceConnection = null;
    private Messenger proxyServiceMessenger = null;
    private StartFinishCallback startFinishCallback = null;
    private StopFinishCallback stopFinishCallback = null;

    public static void createInstance()
    {
        if (_instance == null) {
            _instance = new ProxyManager();
        }
    }

    public static void releaseInstance()
    {
        if (_instance != null) {
            _instance = null;
        }
    }

    public static ProxyManager getInstance()
    {
        return _instance;
    }

    private ProxyManager()
    {
        this.selfMessenger = new Messenger(new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message)
            {
                handleProxyServiceMessage(message);
            }
        });

        this.proxyServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                onProxyServiceConnected(name, service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {
                onProxyServiceDisconnected(name);
            }
        };
    }

    public Status getStatus()
    {
        return this.status;
    }

    public void start(StartFinishCallback startFinishCallback)
    {
        if (this.status != Status.IDLE) {
            return;
        }

        this.status = Status.STARTING;
        this.startFinishCallback = startFinishCallback;

        Intent intent = new Intent(
            App.getContext(), TwilightLineVpnService.class);
        ContextCompat.startForegroundService(
            App.getContext(), intent);
        App.getContext().bindService(intent, this.proxyServiceConnection, 0);
    }

    public void stop(StopFinishCallback stopFinishCallback)
    {
        if (this.status != Status.RUNNING) {
            return;
        }

        this.status = Status.STOPPING;
        this.stopFinishCallback = stopFinishCallback;

        Message request = Message.obtain();
        request.what = TwilightLineVpnService.MESSAGE_STOP_PROXY_REQUEST;
        try {
            this.proxyServiceMessenger.send(request);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void onProxyServiceConnected(ComponentName name, IBinder service)
    {
        this.proxyServiceMessenger = new Messenger(service);

        Bundle b = new Bundle();
        b.putBoolean("is_global_proxy",
            SettingsManager.getInstance().isPerAppProxyEnabled() == false);
        b.putString("allowed_app_list",
            SettingsManager.getInstance().getProxyAppsString());
        b.putString("proxy_config_name",
            SettingsManager.getInstance().getProxyConfigName());

        Message request = Message.obtain();
        request.what = TwilightLineVpnService.MESSAGE_START_PROXY_REQUEST;
        request.replyTo = this.selfMessenger;
        request.obj = b;
        try {
            this.proxyServiceMessenger.send(request);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void onProxyServiceDisconnected(ComponentName name)
    {
        this.proxyServiceMessenger = null;
    }

    private void handleProxyServiceMessage(Message message)
    {
        if (message.what ==
                TwilightLineVpnService.MESSAGE_START_PROXY_RESPONSE) {
            onMessageStartProxyResponse(message);
        } else if (message.what ==
                TwilightLineVpnService.MESSAGE_STOP_PROXY_RESPONSE) {
            onMessageStopProxyResponse(message);
        }
    }

    private void onMessageStartProxyResponse(Message response)
    {
        StartFinishCallback startFinishCallback = this.startFinishCallback;

        this.status = Status.RUNNING;
        this.startFinishCallback = null;

        if (startFinishCallback != null) {
            startFinishCallback.run();
        }
    }

    private void onMessageStopProxyResponse(Message response)
    {
        App.getContext().unbindService(this.proxyServiceConnection);
        Intent intent = new Intent(
            App.getContext(), TwilightLineVpnService.class);
        App.getContext().stopService(intent);

        StopFinishCallback stopFinishCallback = this.stopFinishCallback;

        this.status = Status.IDLE;
        this.stopFinishCallback = null;

        if (stopFinishCallback != null) {
            stopFinishCallback.run();
        }
    }
}
