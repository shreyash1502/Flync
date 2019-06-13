package com.benrostudios.flync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.benrostudios.flync.NetworkDiscovery.NetworkDevice;

import java.util.ArrayList;
import java.util.List;

public class DeviceSelectorFragment extends Fragment
{

    NetworkDeviceAdapter adapter;
    ArrayList<NetworkDevice> devices;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View fragview = inflater.inflate(R.layout.fragment_device_selector, null);

        NetworkDiscovery networkDiscovery = new NetworkDiscovery(getActivity());
        devices = networkDiscovery.discoverylist;
        adapter = new NetworkDeviceAdapter(getActivity(), devices);
        ListView itemsListView  = fragview.findViewById(R.id.devices_list_view);
        itemsListView.setDivider(null);
        itemsListView.setEmptyView(fragview.findViewById(R.id.no_device_empty_view));
        itemsListView.setAdapter(adapter);

        return fragview;
    }

}
