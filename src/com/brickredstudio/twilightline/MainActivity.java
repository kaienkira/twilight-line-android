package com.brickredstudio.twilightline;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager().beginTransaction()
            .replace(R.id.frame, new SettingsFragment())
            .commit();
    }
}
