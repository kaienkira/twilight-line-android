package com.brickredstudio.twilightline;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.VpnService;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import java.io.FileDescriptor;

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
    private Process twilightLineClientProcess = null;
    private Process tun2SocksProcess = null;

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
        if (startTun2Socks() == false) {
            Log.e(App.TAG, "start tun2socks failed");
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
        stopTun2Socks();
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
        b.addAddress(VPN_TUN_DEVICE_IPV4, 30);
        b.addDnsServer(VPN_TUN_ROUTER_IPV4);
        b.addAddress(VPN_TUN_DEVICE_IPV6, 126);
        b.addDnsServer(VPN_TUN_ROUTER_IPV6);

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

        // add route
        b.addRoute("0.0.0.0", 0);
        b.addRoute("::", 0);

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
        String configPath = App.getContext().getCacheDir() +
            "/tl-client-config.json";
        if (AppUtil.copyAsset(
                "config/tl-client-config.json", configPath) == false) {
            return false;
        }
        String cmd = progPath + " -e " + configPath;
        Log.i(App.TAG, String.format("start %s", cmd));

        try {
            this.twilightLineClientProcess =
                Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            Log.e(App.TAG, String.format(
                "start failed: %s", e.toString()));
            return false;
        }

        return true;
    }

    private void stopTwilightLineClient()
    {
        if (this.twilightLineClientProcess != null) {
            try {
                this.twilightLineClientProcess.destroy();
            } catch (Exception e) {
            }
            this.twilightLineClientProcess = null;
        }
    }

    private boolean startTun2Socks()
    {
        String progPath =
            App.getContext().getApplicationInfo().nativeLibraryDir +
            "/libtun2socks.so";
        String sockPath = App.getContext().getCacheDir() +
            "/tunfd.sock";

        String cmd = progPath +
            " --netif-ipaddr " + VPN_TUN_ROUTER_IPV4 +
            " --netif-ip6addr " + VPN_TUN_ROUTER_IPV6 +
            " --sock-path " + sockPath +
            " --socks-server-addr 127.0.0.1:9058" +
            " --dnsgw 127.0.0.1:9059" +
            " --tunmtu " + Integer.toString(VPN_MTU) +
            " --loglevel info";
        Log.i(App.TAG, String.format("start %s", cmd));

        try {
            this.tun2SocksProcess =
                Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            Log.e(App.TAG, String.format(
                "start failed: %s", e.toString()));
            return false;
        }

        // send vpn fd
        LocalSocket localSocket = null;
        FileDescriptor[] fds = new FileDescriptor[1];
        fds[0] = this.vpnFileDescriptor.getFileDescriptor();
        int tryCount = 0;
        for (;;) {
            try {
                Thread.sleep(1000);
                localSocket = new LocalSocket();
                localSocket.connect(new LocalSocketAddress(
                    sockPath, LocalSocketAddress.Namespace.FILESYSTEM));
                localSocket.setFileDescriptorsForSend(fds);
                localSocket.getOutputStream().write(1);
                break;

            } catch (Exception e) {
                if (++tryCount >= 10) {
                    Log.e(App.TAG, String.format(
                        "send vpn fd failed: %s", e.toString()));
                    return false;
                }
            } finally {
                if (localSocket != null) {
                    try {
                        localSocket.close();
                    } catch (Exception e) {
                    }
                    localSocket = null;
                }
            }
        }

        return true;
    }

    private void stopTun2Socks()
    {
        if (this.tun2SocksProcess != null) {
            try {
                this.tun2SocksProcess.destroy();
            } catch (Exception e) {
            }
            this.tun2SocksProcess = null;
        }
    }
}
