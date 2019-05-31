package com.benrostudios.flync;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class AsyncManager extends AsyncTask<String, Integer ,String> {

    Activity mContext;
    Integer mode;
    private ArrayList<String> fileNameAndPaths ;
    String[] filenames ;



    public  AsyncManager(Activity context,Integer mode,ArrayList<String> fnp)
    {
        this.mContext=context;
        this.mode = mode;
        this.fileNameAndPaths= fnp;



    }
    @Override
    protected String doInBackground(String... params) {
        for(int i=0,j=1; i <= fileNameAndPaths.size()-1;i=i+2,j=i+1) {
            String selectedFilePath = fileNameAndPaths.get(j);
            String filename = fileNameAndPaths.get(i);



            if (mode == 1) {

                Socket sock;
                try {


                    // sendfile
                    sock = new Socket("192.168.0.109", 1149);
                    File myFile = new File(selectedFilePath);
                    long filesize = myFile.length();
                    Log.d("sentfiles",selectedFilePath+" "+filesize);
                    DataOutputStream DOS = new DataOutputStream(sock.getOutputStream());
                    DOS.writeUTF(filename + "/" + filesize);

                    byte[] mybytearray = new byte[(int) myFile.length()];


                    FileInputStream fis = new FileInputStream(myFile);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.read(mybytearray, 0, mybytearray.length);
                    OutputStream os = sock.getOutputStream();

                    os.write(mybytearray, 0, mybytearray.length);
                    os.flush();

                    sock.close();

                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (mode == 2) {
                //Receive Async Task here


            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {



    }

    @Override
    protected void onPostExecute(String bitmaps) {
        Toast.makeText(mContext, "Sent!", Toast.LENGTH_SHORT).show();

    }
}
