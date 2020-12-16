package com.brickredstudio.twilightline;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class TwilightLineVpnService extends VpnService
{
    public static final int MESSAGE_START_PROXY_REQUEST = 1;
    public static final int MESSAGE_START_PROXY_RESPONSE = 2;
    public static final int MESSAGE_STOP_PROXY_REQUEST = 3;
    public static final int MESSAGE_STOP_PROXY_RESPONSE = 4;

    private Messenger selfMessenger = null;
    private Messenger clientMessenger = null;

    public TwilightLineVpnService()
    {
        this.selfMessenger = new Messenger(new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message)
            {
                handleClientMessage(message);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        PendingIntent pendingIntent = PendingIntent.getActivity(
            App.getContext(), 0,
            new Intent(App.getContext(), MainActivity.class),
            PendingIntent.FLAG_UPDATE_CURRENT);

        startForeground(App.NOTIFICATION_MAIN,
            new NotificationCompat.Builder(
                this, App.NOTIFICATION_CHANNEL_MAIN_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle("Twilight Line")
            .setContentText("Twilight Line Proxy Started")
            .setContentIntent(pendingIntent)
            .build());

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return this.selfMessenger.getBinder();
    }

    private void handleClientMessage(Message message)
    {
        if (message.what == MESSAGE_START_PROXY_REQUEST) {
            onMessageStartProxyRequest(message);
        } else if (message.what == MESSAGE_STOP_PROXY_REQUEST) {
            onMessageStopProxyRequest(message);
        }
    }

    private void onMessageStartProxyRequest(Message request)
    {
        this.clientMessenger = request.replyTo;

        Message response = Message.obtain();
        response.what = TwilightLineVpnService.MESSAGE_START_PROXY_RESPONSE;
        try {
            this.clientMessenger.send(response);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void onMessageStopProxyRequest(Message request)
    {
        Message response = Message.obtain();
        response.what = TwilightLineVpnService.MESSAGE_STOP_PROXY_RESPONSE;

        try {
            this.clientMessenger.send(response);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        this.clientMessenger = null;
    }
}
