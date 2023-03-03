package com.brickredstudio.twilightline;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class EditPerAppProxyFragment extends Fragment
{
    private final class AppListViewAdapter
        extends RecyclerView.Adapter<AppListViewAdapter.MyViewHolder>
    {
        private final class MyViewHolder extends RecyclerView.ViewHolder
        {
            public SwitchCompat appProxySwitch = null;

            public MyViewHolder(View layout)
            {
                super(layout);
            }
        }

        private List<PackageInfo> data = null;

        @SuppressWarnings("deprecation")
        public AppListViewAdapter()
        {
            List<PackageInfo> installedApps =
                App.getContext().getPackageManager().getInstalledPackages(
                    PackageManager.GET_PERMISSIONS);
            Map<String, PackageInfo> filteredApps =
                new TreeMap<String, PackageInfo>();
            List<PackageInfo> sortedApps =
                new ArrayList<PackageInfo>();

            for (PackageInfo pi : installedApps) {
                if (pi.requestedPermissions == null) {
                    continue;
                }
                if (Arrays.asList(pi.requestedPermissions).contains(
                        Manifest.permission.INTERNET) == false) {
                    continue;
                }
                if (pi.packageName.equals(App.getContext().getPackageName())) {
                    continue;
                }

                filteredApps.put(pi.packageName, pi);
            }

            for (Map.Entry<String, PackageInfo> iter :
                    filteredApps.entrySet()) {
                sortedApps.add(iter.getValue());
            }
            Collections.sort(sortedApps, (lhs, rhs) -> {
                Integer lhsScore = getApplicationScore(lhs);
                Integer rhsScore = getApplicationScore(rhs);
                if (!lhsScore.equals(rhsScore)) {
                    return rhsScore.compareTo(lhsScore);
                }

                return lhs.packageName.compareTo(rhs.packageName);
            });

            this.data = sortedApps;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LinearLayout layout = new LinearLayout(parent.getContext());
            layout.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            MyViewHolder holder = new MyViewHolder(layout);

            holder.appProxySwitch = new SwitchCompat(parent.getContext());
            holder.appProxySwitch.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            layout.addView(holder.appProxySwitch);

            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position)
        {
            PackageInfo info = this.data.get(position);

            String desc = info.applicationInfo.loadLabel(
                App.getContext().getPackageManager()).toString();
            String text = desc + "<br/>" +
                "<font color=\"gray\">" +
                info.packageName + "(" + getApplicationScore(info) + ")" +
                "</font>";
            holder.appProxySwitch.setText(HtmlCompat.fromHtml(text, 0));
            holder.appProxySwitch.setChecked(SettingsManager.getInstance()
                .getProxyApps().contains(info.packageName));
            holder.appProxySwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (buttonView.isPressed() == false) {
                        return;
                    }
                    if (isChecked) {
                        SettingsManager.getInstance().addProxyApp(
                            info.packageName);
                    } else {
                        SettingsManager.getInstance().removeProxyApp(
                            info.packageName);
                    }
                });
        }

        @Override
        public int getItemCount()
        {
            return data.size();
        }

        private Integer getApplicationScore(PackageInfo pi)
        {
            int score = 0;

            if ((pi.applicationInfo.flags &
                    ApplicationInfo.FLAG_SYSTEM) != 0) {
                score = 1;
            } else if ((pi.applicationInfo.flags &
                    ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
                score = 2;
            } else {
                score = 3;
            }

            if (SettingsManager.getInstance()
                    .getProxyApps().contains(pi.packageName)) {
                score = 4;
            }

            return Integer.valueOf(score);
        }
    }

    private RecyclerView appListView = null;

    @Override
    public View onCreateView(LayoutInflater inflater,
        ViewGroup container, Bundle savedInstanceState)
    {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 0, 0, 0);

        // app list view
        this.appListView = new RecyclerView(getActivity());
        this.appListView.setHasFixedSize(true);
        this.appListView.setLayoutManager(
            new LinearLayoutManager(getActivity()));
        layout.addView(this.appListView);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        this.appListView.setAdapter(new AppListViewAdapter());
    }
}
