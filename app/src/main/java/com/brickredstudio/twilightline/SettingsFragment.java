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

    @Override
    public View onCreateView(LayoutInflater inflater,
        ViewGroup container, Bundle savedInstanceState)
    {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(0, 20, 0, 0);

        this.perAppProxySwitch = new SwitchCompat(getActivity());
        this.perAppProxySwitch.setText("Using Per-App Proxy");
        layout.addView(this.perAppProxySwitch);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
    }

    public void setEnabled(boolean enabled)
    {
        this.perAppProxySwitch.setEnabled(enabled);
    }
}
