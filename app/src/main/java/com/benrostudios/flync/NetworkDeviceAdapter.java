package com.benrostudios.flync;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.benrostudios.flync.NetworkDiscovery.NetworkDevice;

import java.util.List;

public class NetworkDeviceAdapter extends BaseAdapter
{

    private Context mContext;
    private List<NetworkDevice> devices;


    public NetworkDeviceAdapter(Context context, List<NetworkDevice> devices)
    {
        mContext = context;
        this.devices = devices;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.network_device_list_item, parent, false);
        }

        NetworkDevice device = (NetworkDevice) getItem(position);

       TextView deviceNameTextView = convertView.findViewById(R.id.hostname_text_view);
       TextView deviceIPTextView = convertView.findViewById(R.id.device_ip_text_view);
       ImageView deviceTypeImageView = convertView.findViewById(R.id.device_type_image_view);

       deviceNameTextView.setText(device.getHostname());
       deviceIPTextView.setText(device.getIp());
       deviceTypeImageView.setImageResource(getDeviceTypeResourceFile(device));

        return convertView;
    }

    private int getDeviceTypeResourceFile(NetworkDevice device)
    {
        String deviceType = device.getType();

        if(deviceType.equals("Android"))
        {
            return R.drawable.android_phone_24;
        }
        else
        {
            return R.drawable.baseline_computer_24;
        }
    }

    @Override
    public int getCount()
    {
        if (devices == null)
        {
            return 0;
        }
        else
        {
            return devices.size();
        }
    }

    @Override
    public Object getItem(int position)
    {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }



}
