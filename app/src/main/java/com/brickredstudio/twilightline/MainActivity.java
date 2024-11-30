package com.brickredstudio.twilightline;

import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentContainerView;

public final class MainActivity extends AppCompatActivity
{
    private MainActivityPresenter presenter = null;
    private SwitchCompat startProxySwitch = null;
    private SettingsFragment settingsFragment = null;
    private ActivityResultLauncher<Intent> vpnPrepareLauncher = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);

        this.presenter = new MainActivityPresenter(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        // edge to edge support
        ViewCompat.setOnApplyWindowInsetsListener(layout,
            (v, insets) -> {
                Insets i = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars());
                v.setPadding(
                    i.left + 20,
                    i.top + 20,
                    i.right + 20,
                    i.bottom + 20);
                return insets;
            });

        // start proxy switch
        this.startProxySwitch = new SwitchCompat(this);
        this.startProxySwitch.setText("Proxy Switch");
        this.startProxySwitch.setOnCheckedChangeListener(
            (buttonView, isChecked) -> {
                if (buttonView.isPressed() == false) {
                    return;
                }
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

        setContentView(layout);

        this.settingsFragment = new SettingsFragment();
        getSupportFragmentManager().beginTransaction()
            .setReorderingAllowed(true)
            .add(settingsContainer.getId(), this.settingsFragment)
            .commit();

        this.vpnPrepareLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    onPrepareVpnServiceResult(
                        result.getResultCode() == AppCompatActivity.RESULT_OK);
                }
            });
    }

    @Override
    public void onDestroy()
    {
        this.presenter.stopProxy();
        super.onDestroy();
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
            this.vpnPrepareLauncher.launch(intent);
        }
    }

    private void onPrepareVpnServiceResult(boolean result)
    {
        this.presenter.onPrepareVpnServiceResult(result);
    }
}
