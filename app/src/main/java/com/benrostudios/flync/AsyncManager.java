package com.benrostudios.flync;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.benrostudios.flync.data.AppDatabase;
import com.benrostudios.flync.data.History;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class AsyncManager extends AsyncTask<String, Integer, String> {

    Activity mContext;
    Integer mode;
    BluetoothAdapter mBluetoothAdapter;
    private ArrayList<String> fileNameAndPaths;
    String[] filenames;


    public AsyncManager(Activity context, Integer mode, ArrayList<String> fnp) {
        this.mContext = context;
        this.mode = mode;
        this.fileNameAndPaths = fnp;
    }

    @Override
    protected String doInBackground(String... params) {
        if (mode == 1) {

            for (int i = 0, j = 1; i <= fileNameAndPaths.size() - 1; i = i + 2, j = i + 1) {
                String selectedFilePath = fileNameAndPaths.get(j);
                String filename = fileNameAndPaths.get(i);

                Socket sock;
                try {


                    // sendfile
                    String ipAddress = "192.168.1.13";
                    sock = new Socket(ipAddress, 1149);
                    File myFile = new File(selectedFilePath);

                    long filesize = myFile.length();
                    Log.d("sentfiles", selectedFilePath + " " + filesize);
                    DataOutputStream DOS = new DataOutputStream(sock.getOutputStream());
                    DOS.writeUTF(filename + "/" + filesize);

                    byte[] mybytearray = new byte[(int) myFile.length()];


                    FileInputStream fis = new FileInputStream(myFile);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.read(mybytearray, 0, mybytearray.length);
                    OutputStream os = sock.getOutputStream();

                    os.write(mybytearray, 0, mybytearray.length);
                    os.flush();
                    //insert into db here
                    insertIntoDatabase(mContext, filename, filesize, ipAddress, History.SEND);
                    sock.close();

                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        } else if (mode == 2) {
            String tester ="Unknown";

                    try{tester = Build.MODEL;}catch(Exception e){System.out.println(e.toString());}
            sendBroadcast(tester, 1149);
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
    }

    @Override
    protected void onPostExecute(String bitmaps) {
        switch (mode) {
            case 1:
                Toast.makeText(mContext, "Data Sent", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(mContext, "Packet Broadcasted", Toast.LENGTH_SHORT).show();
                System.out.println("Packet Broadcasted");
                break;
            default:
                Toast.makeText(mContext, "No Mode Given", Toast.LENGTH_SHORT).show();
        }

    }

    private void insertIntoDatabase(Context context, String fileName, long fileSize, String computerName, int sendOrReceive) {
        Date curDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy @ HH:mm a");
        History historyEntry = new History(sendOrReceive, fileName, fileSize, computerName, simpleDateFormat.format(curDate));
        AppDatabase db = AppDatabase.getAppDatabase(context);
        db.historyDao().insert(historyEntry);
    }

    public void sendBroadcast(String messageStr, int port) {
        // Hack Prevent crash (sending should be done using an async task)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            //Open a random port to send the package
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            byte[] sendData = messageStr.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, getBroadcastAddress(), port);
            socket.send(sendPacket);
            System.out.println(getClass().getName() + "Broadcast packet sent to: " + getBroadcastAddress().getHostAddress());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        }
    }

    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }




}

