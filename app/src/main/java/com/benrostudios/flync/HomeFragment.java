package com.benrostudios.flync;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.benrostudios.flync.data.History;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class HomeFragment extends Fragment
{


    private static final String TAG = "Hello";
    private String selectedFilePath;
    private Uri selectedFileURI;

    private ArrayList<String> fileNameAndPaths = new ArrayList<>();
    private String filename;
    private String pathtofile;
    private int filesize;
    public static final int PERMISSIONS_REQUEST_CODE = 0;
    public static final int FILE_PICKER_REQUEST_CODE = 1;
    private final static double noBytesInOneGB = 1000000000.0;

    private TextView mUsedSpaceTextView;
    private TextView mTotalSpaceTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View fragview = inflater.inflate(R.layout.fragment_home, null);

        CircularProgressIndicator circularProgress = fragview.findViewById(R.id.circular_progress);
        mUsedSpaceTextView = fragview.findViewById(R.id.usedStorageTextView);
        mTotalSpaceTextView = fragview.findViewById(R.id.totalStorageTextView);


        StatFs stat = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        double totalSpaceInGB = stat.getTotalBytes() / noBytesInOneGB;
        double freeSpaceInGB = stat.getAvailableBytes() / noBytesInOneGB;
        double usedSpaceInGB = totalSpaceInGB - freeSpaceInGB;

        mUsedSpaceTextView.setText(usedSpaceFormattedText(usedSpaceInGB));
        mTotalSpaceTextView.setText(totalSpaceFormattedText(totalSpaceInGB));

        float usedPercent = (float) (usedSpaceInGB / totalSpaceInGB) * 100;
        circularProgress.setProgress(usedPercent, 100);
        circularProgress.setProgressTextAdapter(TEXT_ADAPTER);
        CardView sendButton = fragview.findViewById(R.id.sendCardView);
        CardView receiveButton = fragview.findViewById(R.id.receiveCardView);
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Creating a new Intent to invoke the File Manager
                openDeviceSelectorFragment();

            }
        });
        receiveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Creating a new Intent to invoke the File Manager
                checkPermissionsAndOpen();
                Toast.makeText(getActivity(), "Listening...", Toast.LENGTH_LONG).show();

            }
        });
        return fragview;
    }

    private static final CircularProgressIndicator.ProgressTextAdapter TEXT_ADAPTER = new CircularProgressIndicator.ProgressTextAdapter()
    {
        @Override
        public String formatText(double progress)
        {
            String text = String.format("%.1f", progress) + "% used";
            return text;
        }
    };

    private String usedSpaceFormattedText(double usedSpaceInGB)
    {
        return String.format("%.2f", usedSpaceInGB) + " " + getString(R.string.gb);
    }

    private String totalSpaceFormattedText(double totalSpaceInGB)
    {
        return getString(R.string.used_of) + " " + String.format("%.2f", totalSpaceInGB) + " " + getString(R.string.gb);
    }

    private void openDeviceSelectorFragment()
    {
        Fragment deviceSelector = new DeviceSelectorFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, deviceSelector);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void checkPermissionsAndOpen()
    {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED)
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission))
            {
                showError();
            }
            else
            {
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, PERMISSIONS_REQUEST_CODE);

            }
        }
        else
        {
            try
            {
                Receive();
            }
            catch (IOException e)
            {
                Log.e("HomeFragment", e.getMessage());
            }

        }
    }

    private void showError()
    {
        Toast.makeText(getActivity(), "Allow external storage reading", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_CODE:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    System.out.println("Perms Granted");
                    try
                    {
                        Receive();
                    }
                    catch (IOException e)
                    {
                        Log.e("HomeFragment", e.getMessage());
                    }
                }
                else
                {
                    showError();
                }
            }
        }
    }


    public void Receive() throws IOException
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    String msg_received;
                    int filesize;


                    long start = System.currentTimeMillis();
                    int bytesRead;
                    int current = 0;

                    // create socket
                    ServerSocket servsock = new ServerSocket(1149);
                    while (true)
                    {
                        System.out.println("Waiting...");

                        Socket sock = servsock.accept();
                        System.out.println("Accepted connection : " + sock);

                        DataInputStream DIS = new DataInputStream(sock.getInputStream());
                        String incomingmessages = DIS.readUTF();
                        String[] splitter = incomingmessages.split("/");
                        msg_received = splitter[0];
                        if (splitter[1].contains("/"))
                        {
                            filesize = 104857600;

                        }
                        else
                        {
                            filesize = Integer.parseInt(splitter[1]) + 1;
                            System.out.println("" + filesize);
                        }
                        // receive file

                        byte[] mybytearray = new byte[filesize];
                        InputStream is = sock.getInputStream();
                        File file = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS), msg_received);
                        System.out.println(file);
                        boolean isCreated = file.createNewFile();
                        FileOutputStream fos = new FileOutputStream(file);
                        if (isCreated)
                        {
                            System.out.println("FileCreated");

                        }
                        else
                        {
                            System.out.println("Already Exists?");// The file is receievd , but it cannot create it on your phone

                        }

                        OutputStream bos = new BufferedOutputStream(fos);
                        bytesRead = is.read(mybytearray, 0, mybytearray.length);
                        current = bytesRead;

                        do
                        {
                            bytesRead =
                                    is.read(mybytearray, current, (mybytearray.length - current));
                            if (bytesRead >= 0) current += bytesRead;
                        }
                        while (bytesRead > -1);
                        System.out.println("Downloaded!");
                        bos.write(mybytearray, 0, current);
                        bos.flush();
                        long end = System.currentTimeMillis();
                        System.out.println(end - start);
                        bos.close();


                        sock.close();
                    }
                }
                catch (IOException e)
                {

                    System.out.println(e.toString());

                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

}