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

        startForeground(MainActivity.NOTIFICATION_ID,
            new NotificationCompat.Builder(
                this, MainActivity.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle("Twilight Line")
            .setContentText("Twilight Line Proxy Started")
            .setContentIntent(pendingIntent)
            .build());

        return START_NOT_STICKY;
    }
}
