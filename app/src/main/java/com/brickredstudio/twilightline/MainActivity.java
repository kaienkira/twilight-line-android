package com.brickredstudio.twilightline;

import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentContainerView;

public final class MainActivity extends AppCompatActivity
{
    private MainActivityPresenter presenter = null;
    private SwitchCompat startProxySwitch = null;
    private SettingsFragment settingsFragment = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.presenter = new MainActivityPresenter(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        // start proxy switch
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

        // settings fragment
        FragmentContainerView settingsContainer =
            new FragmentContainerView(this);
        settingsContainer.setId(View.generateViewId());
        layout.addView(settingsContainer);

        // set view
        setContentView(layout);

        // add fragments
        this.settingsFragment = new SettingsFragment();
        getSupportFragmentManager().beginTransaction()
            .setReorderingAllowed(true)
            .add(settingsContainer.getId(), this.settingsFragment)
            .commit();
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

    public void setSettingsFragmentEnabled(boolean enabled)
    {
        this.settingsFragment.setEnabled(enabled);
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
