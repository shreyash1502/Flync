package com.benrostudios.flync;

import android.app.Activity;
import android.content.Intent;


import java.net.DatagramPacket;
import java.net.DatagramSocket;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkDiscovery {
    Thread pinghread;
    Timer timer2;
    Activity mActivity;
    boolean udprun = true;
    public static  ArrayList<NetworkDevice> discoverylist = new ArrayList<NetworkDevice>();




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


                    while (true) {
                        recsocket.receive(packet);
                        message = new String(lmessage, 0, packet.getLength());
                        String splitter[] = message.split("/");
                        String Name = splitter[0];
                        String Type = splitter[1];
                        String discoveryIP = packet.getAddress().toString();
                        System.out.println(discoveryIP.substring(1));
                        System.out.println(Name);
                        System.out.println(Type);

                        if (discoverylist.contains(discoveryIP)) {
                            System.out.println("Already Discovered");
                            System.out.println(discoverylist);
                        } else {
                            discoverylist.add(new NetworkDevice(discoveryIP.substring(1), Name, Type));

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


        return;

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




    class RemindTask extends TimerTask {

        @Override
        public void run() {
            System.out.println("Timeout!");
            udprun = false;
            timer2.cancel();
            afterRun();

        }

        public void afterRun(){
            DeviceSelectorFragment.adapter.notifyDataSetChanged();
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
