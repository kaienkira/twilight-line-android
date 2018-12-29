package com.brickredstudio.twilightline;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat
{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.settings, rootKey);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main, menu);
    }
}
