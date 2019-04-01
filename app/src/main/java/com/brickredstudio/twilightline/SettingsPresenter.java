package com.brickredstudio.twilightline;

import android.content.Intent;
import android.net.VpnService;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsPresenter
{
    private static final int REQUEST_VPN_CONNECT = 0;

    private SettingsFragment view = null;

    public SettingsPresenter(SettingsFragment view)
    {
        this.view = view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_VPN_CONNECT) {
            if (resultCode != AppCompatActivity.RESULT_OK) {
                startProxyFailed(this.view.getString(
                    R.string.error_prepare_vpn_service_failed));
            } else {
                startProxyStep2();
            }
        }
    }

    public void onSwitchProxyButtonCheckedChanged(boolean isChecked)
    {
        if (isChecked) {
            startProxy();
        }
    }

    private void startProxy()
    {
        this.view.setSwitchProxyButtonEnable(false);
        Intent intent = VpnService.prepare(this.view.getActivity());
        if (intent == null) {
            startProxyStep2();
        } else {
            this.view.startActivityForResult(intent, REQUEST_VPN_CONNECT);
        }
    }

    private void startProxyStep2()
    {
        startProxyFailed(this.view.getString(
            R.string.error_boot_vpn_service_failed));
    }

    private void startProxyFailed(String errorMessage)
    {
        this.view.reportError(errorMessage);
        this.view.setSwitchProxyButtonChecked(false);
        this.view.setSwitchProxyButtonEnable(true);
    }
}
