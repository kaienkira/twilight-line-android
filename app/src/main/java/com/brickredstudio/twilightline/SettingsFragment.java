package com.brickredstudio.twilightline;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
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
                    public void onCheckedChanged(
                        CompoundButton buttonView, boolean isChecked) {
                        presenter.onSwitchProxyButtonCheckedChanged(isChecked);
                    }
                });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        this.presenter.onActivityResult(requestCode, resultCode, data);
    }

    public void reportError(String errorMessage)
    {
        Toast.makeText(this.getActivity(), errorMessage, Toast.LENGTH_LONG).show();
    }

    public void setSwitchProxyButtonEnable(boolean isEnable)
    {
        this.switchProxyButton.setEnabled(isEnable);
    }

    public void setSwitchProxyButtonChecked(boolean isChecked)
    {
        this.switchProxyButton.setChecked(isChecked);
    }
}
