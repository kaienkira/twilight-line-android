package com.brickredstudio.twilightline;

import android.widget.Toast;

public class SettingsPresenter
{
    private SettingsFragment view = null;

    public SettingsPresenter(SettingsFragment view)
    {
        this.view = view;
    }

    public void switchVpn(boolean enable)
    {
        Toast.makeText(view.getActivity(), String.format("%b", enable),
            Toast.LENGTH_SHORT).show();
    }
}
