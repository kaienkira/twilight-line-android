package com.brickredstudio.twilightline;

import android.content.Intent;
import android.net.VpnService;
import androidx.core.app.NotificationCompat;

public class TwilightLineVpnService extends VpnService
{
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        startForeground(MainActivity.NOTIFICATION_ID,
            new NotificationCompat.Builder(
                this, MainActivity.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Twilight Line")
            .setContentText("Twilight Line Proxy Started")
            .setSmallIcon(R.drawable.app_icon)
            .build());

        return START_NOT_STICKY;
    }
}
