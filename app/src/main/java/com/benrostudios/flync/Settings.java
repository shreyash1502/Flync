package com.benrostudios.flync;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Bundle;

import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.Toast;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;



public class Settings extends Fragment {






    private String selectedFilePath;
    private String filename;
    public static final int PERMISSIONS_REQUEST_CODE = 0;
    public static final int FILE_PICKER_REQUEST_CODE = 1;
    @Nullable




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity()==null)
            Toast.makeText(null, "Activity is null", Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragview = inflater.inflate(R.layout.fragment_settings, null);
        if(getActivity()==null)
            Toast.makeText(null, "NullActivity", Toast.LENGTH_SHORT).show();
        //Just Adding the Button Listener here
        Button Mab = fragview.findViewById(R.id.explorer);
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Mab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating a new Intent to invoke the File Manager
                checkPermissionsAndOpenFilePicker();
            }
        });
        Button Sab = fragview.findViewById(R.id.sned);
        Sab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Socket sock = null;
                try{
                    sock = new Socket("192.168.0.109",1149);
                    DataOutputStream DOS = new DataOutputStream(sock.getOutputStream());
                    DOS.writeUTF(filename);




                }catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {


                    System.out.println("Connecting...");

                    // sendfile

                    File myFile = new File(selectedFilePath);

                    byte [] mybytearray  = new byte [(int)myFile.length()];
                    Toast.makeText(getActivity(), mybytearray.toString(), Toast.LENGTH_SHORT).show();



                    FileInputStream fis = new FileInputStream(myFile);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.read(mybytearray,0,mybytearray.length);
                    OutputStream os = sock.getOutputStream();
                    Toast.makeText(getActivity(), "Sending.....", Toast.LENGTH_SHORT).show();
                    os.write(mybytearray,0,mybytearray.length);
                    os.flush();

                    sock.close();
                    Toast.makeText(getActivity(), "Sent!", Toast.LENGTH_SHORT).show();
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });





        return fragview;
    }

    @Override
    //Method which acts upon the Intent's Result(File Explorer)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // The Request code was randomly assigned , if you are wondering
        if (requestCode == FILE_PICKER_REQUEST_CODE ) {
            String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            selectedFilePath = path;

            if (path != null) {
                Log.d("Path: ", path);
                Toast.makeText(getContext(), "Picked file: " + path, Toast.LENGTH_LONG).show();
                String[] splitter = path.split("/");
                int filepos = splitter.length-1;
                filename = splitter[filepos];

            }
        }
    }

    private void checkPermissionsAndOpenFilePicker() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                showError();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            openFilePicker();
        }
    }

    private void showError() {
        Toast.makeText(getActivity(), "Allow external storage reading", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFilePicker();
                } else {
                    showError();
                }
            }
        }
    }

    public void openFilePicker() {


        new MaterialFilePicker()

                .withRequestCode(1)
                .withSupportFragment(MainActivity.getFrag())
                .withFilterDirectories(true) // Set directories filterable (false by default)
                .withHiddenFiles(false) // Show hidden files and folders
                .start();
        
    }




}
