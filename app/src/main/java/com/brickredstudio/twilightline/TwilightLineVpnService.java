package com.brickredstudio.twilightline;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class TwilightLineVpnService extends VpnService
{
    public static final int MESSAGE_START_PROXY_REQUEST = 1;
    public static final int MESSAGE_START_PROXY_RESPONSE = 2;
    public static final int MESSAGE_STOP_PROXY_REQUEST = 3;
    public static final int MESSAGE_STOP_PROXY_RESPONSE = 4;

    private static final int VPN_MTU = 1500;
    private static final String VPN_TUN_DEVICE_IPV4 = "172.27.0.1";
    private static final String VPN_TUN_ROUTER_IPV4 = "172.27.0.2";
    private static final String VPN_TUN_DEVICE_IPV6 = "2001:db87::1";
    private static final String VPN_TUN_ROUTER_IPV6 = "2001:db87::2";

    private Messenger selfMessenger = null;
    private Messenger clientMessenger = null;
    private ParcelFileDescriptor vpnFileDescriptor = null;

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

        if (startVpnService(true, null) == false) {
            Log.e(App.TAG, "start vpn service failed");
            return;
        }
        if (startTwilightLineClient() == false) {
            Log.e(App.TAG, "start tlclient failed");
            return;
        }

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
        stopTwilightLineClient();
        stopVpnService();

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

    private boolean startVpnService(boolean isGlobalProxy, String[] allowedAppList)
    {
        VpnService.Builder b = new VpnService.Builder();
        b.setMtu(VPN_MTU);
        b.setSession(App.NAME);
        // ipv4
        b.addAddress(VPN_TUN_DEVICE_IPV4, 30);
        b.addDnsServer(VPN_TUN_ROUTER_IPV4);
        b.addRoute("0.0.0.0", 0);
        // ipv6
        b.addAddress(VPN_TUN_DEVICE_IPV6, 126);
        b.addDnsServer(VPN_TUN_ROUTER_IPV6);
        b.addRoute("::", 0);

        String selfApp = App.getContext().getPackageName();
        if (isGlobalProxy) {
            try {
                b.addDisallowedApplication(selfApp);
            } catch (Exception e) {
                Log.e(App.TAG, String.format(
                    "add disallowed app(%s) failed", selfApp));
            }
        } else {
            for (String allowedApp : allowedAppList) {
                if (allowedApp != selfApp) {
                    try {
                        b.addAllowedApplication(allowedApp);
                    } catch (Exception e) {
                        Log.e(App.TAG, String.format(
                            "add allowed app(%s) failed", allowedApp));
                        return false;
                    }
                }
            }
        }

        try {
            this.vpnFileDescriptor = b.establish();
        } catch (Exception e) {
            Log.e(App.TAG, String.format(
                "establish vpn failed: %s", e.toString()));
            return false;
        }

        return true;
    }

    private void stopVpnService()
    {
        if (this.vpnFileDescriptor != null) {
            try {
                this.vpnFileDescriptor.close();
            } catch (Exception e) {
            }
            this.vpnFileDescriptor = null;
        }
    }

    private boolean startTwilightLineClient()
    {
        String progPath =
            App.getContext().getApplicationInfo().nativeLibraryDir +
            "/libtlclient.so";

        Log.i(App.TAG, String.format("start %s", progPath));

        return true;
    }

    private void stopTwilightLineClient()
    {
    }
}
