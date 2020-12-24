package com.brickredstudio.twilightline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public final class SettingsFragment extends Fragment
{
    private SwitchCompat perAppProxySwitch = null;
    private SwitchCompat editPerAppProxySwitch = null;

    @Override
    public View onCreateView(LayoutInflater inflater,
        ViewGroup container, Bundle savedInstanceState)
    {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(0, 20, 0, 0);

        this.perAppProxySwitch = new SwitchCompat(getActivity());
        this.perAppProxySwitch.setText("Using Per-App Proxy");
        this.perAppProxySwitch.setOnCheckedChangeListener(
            (buttonView, isChecked) -> {
                onSetPerAppProxySwitchChecked(isChecked);
            });
        layout.addView(this.perAppProxySwitch);

        this.editPerAppProxySwitch = new SwitchCompat(getActivity());
        this.editPerAppProxySwitch.setText("Edit Per-App Proxy");
        this.editPerAppProxySwitch.setChecked(false);
        this.editPerAppProxySwitch.setPadding(20, 0, 0, 0);
        layout.addView(this.editPerAppProxySwitch);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        this.perAppProxySwitch.setChecked(
            SettingsManager.getInstance().getPerAppProxyEnabled());
    }

    public void setEnabled(boolean enabled)
    {
        this.perAppProxySwitch.setEnabled(enabled);
        this.editPerAppProxySwitch.setEnabled(enabled);
    }

    public void onSetPerAppProxySwitchChecked(boolean checked)
    {
        SettingsManager.getInstance().setPerAppProxyEnabled(checked);
    }
}
