package com.benrostudios.flync;


import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.os.Looper;
import android.os.StrictMode;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import static android.content.ContentValues.TAG;
import static android.provider.Telephony.Carriers.PORT;


public class Settings extends Fragment {
public String messageStr = "HelloNibba";
private String data ;

    @Nullable


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragview = inflater.inflate(R.layout.fragment_settings, null);


        final Button explorer = fragview.findViewById(R.id.explorer);
        explorer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("clicked");
                new NetworkDiscovery(getActivity());
                getActivity().runOnUiThread(new Runnable() {
                    public void run(){
                        Toast.makeText(getActivity(), data, Toast.LENGTH_LONG).show();
                    }
                });

            }

        });


        return fragview;
    }


    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    private void udpReceiver() {

        final boolean bKeepRunning = true;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                byte[] lmessage = new byte[15000];
                DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);

                try {
                    DatagramSocket socket = new DatagramSocket(1150);

                    while (bKeepRunning) {
                        socket.receive(packet);
                        message = new String(lmessage, 0, packet.getLength());
                        String hello = packet.getAddress().toString();
                       data= message;
                        System.out.println(hello);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();



       return;

    }
}