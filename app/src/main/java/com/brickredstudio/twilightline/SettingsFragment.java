package com.brickredstudio.twilightline;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import java.util.ArrayList;
import java.util.List;

public final class SettingsFragment extends Fragment
{
    private List<String> chooseProxyConfigData = null;
    private TextView chooseProxyConfigLabel = null;
    private Spinner chooseProxyConfigSpinner = null;
    private SwitchCompat perAppProxySwitch = null;
    private SwitchCompat editPerAppProxySwitch = null;
    private FragmentContainerView editPerAppProxyContainer = null;
    private EditPerAppProxyFragment editPerAppProxyFragment = null;

    @Override
    public View onCreateView(LayoutInflater inflater,
        ViewGroup container, Bundle savedInstanceState)
    {
        initChooseProxyConfigData();

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(0, 20, 0, 0);

        // choose proxy config spinner
        this.chooseProxyConfigSpinner = new Spinner(getActivity());
        this.chooseProxyConfigSpinner.setPrompt("Choose Proxy Config");
        this.chooseProxyConfigSpinner.setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(
                    AdapterView<?> parent, View view, int position, long id)
                {
                    onChooseProxyConfig(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {
                }
            });
        {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                this.chooseProxyConfigData);
            adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
            this.chooseProxyConfigSpinner.setAdapter(adapter);
        }
        {
            RelativeLayout subLayout = new RelativeLayout(getActivity());

            this.chooseProxyConfigLabel = new TextView(getActivity());
            this.chooseProxyConfigLabel.setText("Choose Proxy Config");
            this.chooseProxyConfigLabel.setTextColor(
                AppUtil.getColorStateList(R.color.label));
            subLayout.addView(this.chooseProxyConfigLabel);

            {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                subLayout.addView(this.chooseProxyConfigSpinner, lp);
            }

            layout.addView(subLayout);
        }

        // per app proxy switch
        this.perAppProxySwitch = new SwitchCompat(getActivity());
        this.perAppProxySwitch.setText("Using Per-App Proxy");
        this.perAppProxySwitch.setOnCheckedChangeListener(
            (buttonView, isChecked) -> {
                onPerAppProxySwitchChecked(isChecked);
            });
        layout.addView(this.perAppProxySwitch);

        // edit per app proxy switch
        this.editPerAppProxySwitch = new SwitchCompat(getActivity());
        this.editPerAppProxySwitch.setPadding(20, 0, 0, 0);
        this.editPerAppProxySwitch.setText("Edit Per-App Proxy");
        this.editPerAppProxySwitch.setChecked(false);
        this.editPerAppProxySwitch.setOnCheckedChangeListener(
            (buttonView, isChecked) -> {
                onEditPerAppProxySwitchChecked(isChecked);
            });
        layout.addView(this.editPerAppProxySwitch);

        // edit per app proxy fragment
        this.editPerAppProxyContainer =
            new FragmentContainerView(getActivity());
        this.editPerAppProxyContainer.setId(View.generateViewId());
        this.editPerAppProxyContainer.setPadding(20, 0, 0, 0);
        layout.addView(this.editPerAppProxyContainer);

        return layout;
    }

    private void initChooseProxyConfigData()
    {
        this.chooseProxyConfigData = new ArrayList<String>();

        String[] configFiles = AppUtil.listAssets("config/tl-client");
        if (configFiles == null) {
            return;
        }

        for (String f : configFiles) {
            this.chooseProxyConfigData.add(
                f.replace("tlclient-", "").replace(".json", ""));
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        this.perAppProxySwitch.setChecked(
            SettingsManager.getInstance().isPerAppProxyEnabled());

        if (this.chooseProxyConfigData.size() > 0) {
            int index = this.chooseProxyConfigData.indexOf(
                SettingsManager.getInstance().getProxyConfigName());
            if (index < 0) {
                index = 0;
            }
            this.chooseProxyConfigSpinner.setSelection(index);
        }
    }

    public void setEnabled(boolean enabled)
    {
        this.chooseProxyConfigLabel.setEnabled(enabled);
        this.chooseProxyConfigSpinner.setEnabled(enabled);
        this.perAppProxySwitch.setEnabled(enabled);
        this.editPerAppProxySwitch.setEnabled(enabled);

        if (enabled == false) {
            this.editPerAppProxySwitch.setChecked(false);
        }
    }

    public void onChooseProxyConfig(int pos)
    {
        SettingsManager.getInstance().setProxyConfigName(
            this.chooseProxyConfigData.get(pos));
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
