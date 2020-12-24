package com.brickredstudio.twilightline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

public final class SettingsFragment extends Fragment
{
    private SwitchCompat perAppProxySwitch = null;
    private SwitchCompat editPerAppProxySwitch = null;
    private FragmentContainerView editPerAppProxyContainer = null;
    private EditPerAppProxyFragment editPerAppProxyFragment = null;

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
                onPerAppProxySwitchChecked(isChecked);
            });
        layout.addView(this.perAppProxySwitch);

        this.editPerAppProxySwitch = new SwitchCompat(getActivity());
        this.editPerAppProxySwitch.setPadding(20, 0, 0, 0);
        this.editPerAppProxySwitch.setText("Edit Per-App Proxy");
        this.editPerAppProxySwitch.setChecked(false);
        this.editPerAppProxySwitch.setOnCheckedChangeListener(
            (buttonView, isChecked) -> {
                onEditPerAppProxySwitchChecked(isChecked);
            });
        layout.addView(this.editPerAppProxySwitch);

        this.editPerAppProxyContainer =
            new FragmentContainerView(getActivity());
        this.editPerAppProxyContainer.setId(View.generateViewId());
        this.editPerAppProxyContainer.setPadding(20, 0, 0, 0);
        layout.addView(this.editPerAppProxyContainer);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        this.perAppProxySwitch.setChecked(
            SettingsManager.getInstance().isPerAppProxyEnabled());
    }

    public void setEnabled(boolean enabled)
    {
        this.perAppProxySwitch.setEnabled(enabled);
        this.editPerAppProxySwitch.setEnabled(enabled);

        if (enabled == false) {
            this.editPerAppProxySwitch.setChecked(false);
        }
    }

    public void onPerAppProxySwitchChecked(boolean checked)
    {
        SettingsManager.getInstance().setPerAppProxyEnabled(checked);
    }

    public void onEditPerAppProxySwitchChecked(boolean checked)
    {
        if (checked) {
            this.editPerAppProxyFragment =
                new EditPerAppProxyFragment();
            getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(editPerAppProxyContainer.getId(),
                     this.editPerAppProxyFragment)
                .commit();
        } else {
            getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .remove(this.editPerAppProxyFragment)
                .commit();
            this.editPerAppProxyFragment = null;
        }
    }
}
