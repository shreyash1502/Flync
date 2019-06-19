package com.benrostudios.flync;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.net.DatagramPacket;
import java.net.DatagramSocket;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkDiscovery {
    Thread pinghread;
    Timer timer2;
    Activity mActivity;
    public boolean udprun = true;
    public static  ArrayList<NetworkDevice> discoverylist = new ArrayList<NetworkDevice>();
    public static  ArrayList<String> alreadyDiscovered = new ArrayList<String>();




    public NetworkDiscovery(Activity act){

        this.mActivity = act;

        udprun = true;
        timer2 = new Timer();
        timer2.schedule(new RemindTask(),6900);
        udpReceiver();
        sendPingpackets();
        System.out.println("hi");

    }

    public void udpsender(){
        new AsyncManager(mActivity, 2,null).execute("");
    }

    private void udpReceiver() {


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                String message;
                byte[] lmessage = new byte[15000];
                DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);
                try{
                    DatagramSocket recsocket  = new DatagramSocket(1150);
                try {


                    while (true)
                    {
                        recsocket.receive(packet);
                        message = new String(lmessage, 0, packet.getLength());
                        String splitter[] = message.split("/");
                        String Name = splitter[0];
                        String Type = splitter[1];
                        String discoveryIP = packet.getAddress().toString();
                        System.out.println(discoveryIP.substring(1));
                        System.out.println(Name);
                        System.out.println(Type);

                        if (alreadyDiscovered.contains(discoveryIP)) {
                            System.out.println("Already Discovered");
                            System.out.println(discoverylist);
                        } else {
                            discoverylist.add(new NetworkDevice(discoveryIP.substring(1), Name, Type));
                            alreadyDiscovered.add(discoveryIP);

                        }

                        if(alreadyDiscovered !=null){
                            mActivity.runOnUiThread(new Runnable(){
                                public void run() {
                                    DeviceSelectorFragment.updateDevices();
                                    DeviceSelectorFragment.adapter.notifyDataSetChanged();

                                }
                            });

                        }

                    }
                    } finally{
                      recsocket.close();

                }
                } catch (Throwable e) {
                    e.printStackTrace();
                }


            }
        });
        thread.setDaemon(true);
        thread.start();

    }

    public void sendPingpackets(){
       pinghread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(udprun){udpsender();
                    try {
                        pinghread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println(e.toString());
                    }

                }

            }
        });
        pinghread.setDaemon(true);

        pinghread.start();

       }

//START: UI and UX stuff for DeviceSelectorFragment
    private void showSnackbar()
    {
        Snackbar snackbar = Snackbar.make(mActivity.findViewById(R.id.device_selector_coordinator_layout), R.string.scanning_stopped, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.restart, new RestartListner());
        snackbar.setBehavior(new NoSwipeBehavior());
        snackbar.show();
    }



    private class RestartListner implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            discoverylist.clear();
            mActivity.runOnUiThread(new Runnable(){
                public void run() {
                    DeviceSelectorFragment.adapter.notifyDataSetChanged();
                }
            });
            showScanning();
            new NetworkDiscovery(mActivity);
        }
    }

    private class NoSwipeBehavior extends BaseTransientBottomBar.Behavior
    {
        @Override
        public boolean canSwipeDismissView(View child)
        {
            return false;
        }
    }

    private ProgressBar progressCircle;
    private TextView scanningDevicesTextView;
    private TextView noDevicesTextView;


    private void showScanning()
    {
        mActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                progressCircle = mActivity.findViewById(R.id.progress_circular);
                scanningDevicesTextView = mActivity.findViewById(R.id.scanning_devices_text_view);
                noDevicesTextView = mActivity.findViewById(R.id.no_devices);
                progressCircle.setVisibility(View.VISIBLE);
                scanningDevicesTextView.setVisibility(View.VISIBLE);
                noDevicesTextView.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void showSomeDevicesButNotScanning()
    {
        mActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                progressCircle = mActivity.findViewById(R.id.progress_circular);
                scanningDevicesTextView = mActivity.findViewById(R.id.scanning_devices_text_view);
                noDevicesTextView = mActivity.findViewById(R.id.no_devices);
                progressCircle.setVisibility(View.INVISIBLE);
                scanningDevicesTextView.setVisibility(View.INVISIBLE);
                noDevicesTextView.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void showNoDevicesNotScanning()
    {
        mActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                progressCircle = mActivity.findViewById(R.id.progress_circular);
                scanningDevicesTextView = mActivity.findViewById(R.id.scanning_devices_text_view);
                noDevicesTextView = mActivity.findViewById(R.id.no_devices);
                progressCircle.setVisibility(View.INVISIBLE);
                scanningDevicesTextView.setVisibility(View.INVISIBLE);
                noDevicesTextView.setVisibility(View.VISIBLE);
            }
        });
    }



//END: UI and UX stuff for DeviceSelectorFragment



    class RemindTask extends TimerTask {

        @Override
        public void run() {
            System.out.println("Timeout!");
            udprun = false;
            timer2.cancel();
            afterRun();

        }

        public void afterRun(){
            if(discoverylist.size()==0 || discoverylist == null)
                showNoDevicesNotScanning();
            else
                showSomeDevicesButNotScanning();

            showSnackbar();
        }

    }


    public class NetworkDevice{
        String ip;
        String hostname;
        String type;

        public NetworkDevice(String IP , String HOSTNAME , String TYPE){
            this.ip = IP;
            this.hostname = HOSTNAME;
            this.type = TYPE;
        }

        public String getIp(){
            return ip;
        }

        public String getHostname(){
            return hostname;
        }

        public String getType(){
            return type;
        }


    }



}
