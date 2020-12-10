package com.brickredstudio.twilightline;

import android.content.Intent;
import android.net.VpnService;

public class TwilightLineVpnService extends VpnService
{
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_NOT_STICKY;
    }
}
