package com.brickredstudio.twilightline;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import androidx.core.app.NotificationCompat;

public class TwilightLineVpnService extends VpnService
{
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
}
