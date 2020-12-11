package com.brickredstudio.twilightline;

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
        void run(boolean result);
    }

    private static ProxyManager _instance = null;

    private Status status = Status.IDLE;
    private StartFinishCallback startFinishCb = null;

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

    public Status getStatus()
    {
        return this.status;
    }

    public void start(StartFinishCallback startFinishCb)
    {
        this.status = Status.STARTING;
        this.startFinishCb = startFinishCb;

        startVpnService();
    }

    public void stop()
    {
    }

    private void startVpnService()
    {
    }
}
