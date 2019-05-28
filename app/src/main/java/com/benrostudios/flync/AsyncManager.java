package com.benrostudios.flync;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class AsyncManager extends AsyncTask< String , Integer ,String> {

    Activity mContext;
    Integer mode;
    TextView le ;



    public  AsyncManager(Activity context,Integer mode)
    {
        this.mContext=context;
        this.mode = mode;
        le =  (TextView) mContext.findViewById(R.id.le);


    }
    @Override
    protected String doInBackground(String... params) {
        String selectedFilePath = params[0];
        String filename = params[1];

        if(mode==1) {

            Socket sock;
            try {


                // sendfile
                sock = new Socket("192.168.0.109", 1149);
                File myFile = new File(selectedFilePath);
                long filesize = myFile.length();
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
        }else if(mode == 2){
            //Recive Async Task here







        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        le.setText("Progress:" + progress[0]);


    }

    @Override
    protected void onPostExecute(String bitmaps) {

    }
}
