package com.benrostudios.flync;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AsyncManager extends AsyncTask<String, Integer, String>
{

    Activity mContext;
    Integer mode;
    private ArrayList<String> fileNameAndPaths;
    String[] filenames;


    public AsyncManager(Activity context, Integer mode, ArrayList<String> fnp)
    {
        this.mContext = context;
        this.mode = mode;
        this.fileNameAndPaths = fnp;
    }

    @Override
    protected String doInBackground(String... params)
    {
        for (int i = 0, j = 1; i <= fileNameAndPaths.size() - 1; i = i + 2, j = i + 1)
        {
            String selectedFilePath = fileNameAndPaths.get(j);
            String filename = fileNameAndPaths.get(i);


            if (mode == History.SEND) {

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

                } catch (UnknownHostException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if (mode == History.RECEIVE)
            {

                try {
                    Recieve();
                    Log.d("SockERROR","Started");
                }catch(IOException e){
                 Log.d("SockERROR",e.toString());

                }
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress)
    {
    }

    @Override
    protected void onPostExecute(String bitmaps)
    {
        Toast.makeText(mContext, "Data has been sent", Toast.LENGTH_SHORT).show();
    }

    private void insertIntoDatabase(Context context, String fileName, long fileSize, String computerName, int sendOrReceive)
    {
        Date curDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy @ HH:mm a");
        History historyEntry = new History(sendOrReceive, fileName, fileSize, computerName, simpleDateFormat.format(curDate));
        AppDatabase db = AppDatabase.getAppDatabase(context);
        db.historyDao().insert(historyEntry);
    }
    public void Recieve() throws IOException {
        String msg_received;
        String sendersIp = "";
        int filesize;


        long start = System.currentTimeMillis();
        int bytesRead;
        int current = 0;

        // create socket
        ServerSocket servsock = new ServerSocket(1149);
        while (true) {
            System.out.println("Waiting...");

            Socket sock = servsock.accept();
            System.out.println("Accepted connection : " + sock);
            DataInputStream DIS = new DataInputStream(sock.getInputStream());
            String incomingmessages = DIS.readUTF();
            String[] splitter = incomingmessages.split("/");
            msg_received = splitter[0];
            if(splitter[1].contains("/")){
                filesize = 104857600;

            }else{
                filesize = Integer.parseInt(splitter[1])+1;
                System.out.println(""+filesize);
            }
            // receive file
            byte [] mybytearray  = new byte [filesize];
            InputStream is = sock.getInputStream();
            FileOutputStream fos = new FileOutputStream("C:\\Users\\aryan\\Documents\\"+msg_received);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bytesRead = is.read(mybytearray,0,mybytearray.length);
            current = bytesRead;

            // thanks to A. Cádiz for the bug fix
            do {
                bytesRead =
                        is.read(mybytearray, current, (mybytearray.length-current));
                if(bytesRead >= 0) current += bytesRead;
            } while(bytesRead > -1);
            System.out.println("Downloaded!");
            bos.write(mybytearray, 0 , current);
            bos.flush();
            long end = System.currentTimeMillis();
            System.out.println(end-start);
            bos.close();
            insertIntoDatabase(mContext, msg_received, filesize, sendersIp, History.RECEIVE);
            sock.close();
        }
    }

}

