package com.brickredstudio.twilightline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LinearLayout layout = new LinearLayout(parent.getContext());
            MyViewHolder vh = new MyViewHolder(layout);

            vh.appProxySwitch = new SwitchCompat(parent.getContext());
            vh.appProxySwitch.setText("Test");
            layout.addView(vh.appProxySwitch);

            return vh;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position)
        {
        }

        @Override
        public int getItemCount()
        {
            return 150;
        }
    }

    private RecyclerView appListView = null;
    private AppListViewAdapter appListViewAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater,
        ViewGroup container, Bundle savedInstanceState)
    {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        this.appListView = new RecyclerView(getActivity());
        this.appListView.setHasFixedSize(true);
        this.appListView.setLayoutManager(
            new LinearLayoutManager(getActivity()));
        this.appListViewAdapter = new AppListViewAdapter();
        this.appListView.setAdapter(this.appListViewAdapter);
        layout.addView(this.appListView);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
    }
}
