package com.benrostudios.flync;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.benrostudios.flync.data.AppDatabase;
import com.benrostudios.flync.data.History;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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


            if (mode == 1) {

                Socket sock;
                try {


                    // sendfile
                    String ipAddress = "192.168.0.109";
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
                    insertIntoDatabase(mContext, filename, filesize, ipAddress);
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
            else if (mode == 2)
            {
                //Receive Async Task here
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
        Toast.makeText(mContext, "HEMANTH SAYS SENT", Toast.LENGTH_SHORT).show();
    }

    private void insertIntoDatabase(Context context, String fileName, long fileSize, String computerName)//rn, im treating computerName as the IP. when the final computer selector screen works, this will serve proper functionality
    {
        Date curDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ssZ");
        History historyEntry = new History(com.benrostudios.flync.data.History.SEND, fileName, fileSize, computerName, simpleDateFormat.format(curDate));
        AppDatabase db = AppDatabase.getAppDatabase(context);
        db.historyDao().insert(historyEntry);
    }

}

