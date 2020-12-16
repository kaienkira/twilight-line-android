package com.brickredstudio.twilightline;

import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public final class MainActivity extends AppCompatActivity
{
    private MainActivityPresenter presenter = null;
    private SwitchCompat startProxySwitch = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.presenter = new MainActivityPresenter(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(20, 20, 20, 20);

        this.startProxySwitch = new SwitchCompat(this);
        this.startProxySwitch.setText("Proxy Switch");
        this.startProxySwitch.setOnCheckedChangeListener(
            (buttonView, isChecked) -> {
                if (isChecked) {
                    presenter.startProxy();
                } else {
                    presenter.stopProxy();
                }
            });
        layout.addView(this.startProxySwitch);

        setContentView(layout);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        onPrepareVpnServiceResult(resultCode == AppCompatActivity.RESULT_OK);
    }

    public void setStartProxySwitchChecked(boolean checked)
    {
        this.startProxySwitch.setChecked(checked);
    }

    public void setStartProxySwitchEnabled(boolean enabled)
    {
        this.startProxySwitch.setEnabled(enabled);
    }

    public void prepareVpnService()
    {
        Intent intent = VpnService.prepare(this);
        if (intent == null) {
            onPrepareVpnServiceResult(true);
        } else {
            startActivityForResult(intent, 0);
        }
    }

    private void onPrepareVpnServiceResult(boolean result)
    {
        this.presenter.onPrepareVpnServiceResult(result);
    }
}
