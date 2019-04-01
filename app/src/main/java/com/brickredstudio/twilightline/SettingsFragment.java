package com.brickredstudio.twilightline;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat
{
    private SettingsPresenter presenter = null;
    private Switch switchProxyButton = null;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.settings, rootKey);
        setHasOptionsMenu(true);

        this.presenter = new SettingsPresenter(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main, menu);

        {
            MenuItem item = menu.findItem(R.id.switch_proxy);
            this.switchProxyButton =
                item.getActionView().findViewById(R.id.switch_proxy_button);
            this.switchProxyButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        presenter.switchVpn(isChecked);
                    }
                });
        }
    }
}
