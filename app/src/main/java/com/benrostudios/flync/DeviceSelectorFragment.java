package com.benrostudios.flync;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.benrostudios.flync.NetworkDiscovery.NetworkDevice;
import com.benrostudios.flync.data.History;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class DeviceSelectorFragment extends Fragment
{

    static NetworkDeviceAdapter adapter;
    static ArrayList<NetworkDevice> devices;

    public static final int PERMISSIONS_REQUEST_CODE = 0;
    public static final int FILE_PICKER_REQUEST_CODE = 1;

    private static final String TAG = "Hello";
    private String selectedFilePath;
    private Uri selectedFileURI;

    private ArrayList<String> fileNameAndPaths = new ArrayList<>();
    private String filename;
    private String pathtofile;
    private int filesize;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View fragView = inflater.inflate(R.layout.fragment_device_selector, null);

        new NetworkDiscovery(getActivity());

        devices = NetworkDiscovery.discoverylist;

        adapter = new NetworkDeviceAdapter(getActivity(), devices);
        ListView itemsListView = fragView.findViewById(R.id.devices_list_view);
        itemsListView.setDivider(null);
        itemsListView.setAdapter(adapter);
        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                AsyncManager.ipAddress = devices.get(position).getIp();
                checkPermissionsAndOpen();
            }
        });
        return fragView;
    }

    public static void updateDevices()
    {
        devices = NetworkDiscovery.discoverylist;
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
            openFilePicker();
        }
    }


    private void showError()
    {
        Toast.makeText(getActivity(), "Allow external storage reading", Toast.LENGTH_SHORT).show();
    }

    public void openFilePicker()
    {
        Intent chooseFile;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        chooseFile = Intent.createChooser(intent, "Choose a file");
        startActivityForResult(chooseFile, FILE_PICKER_REQUEST_CODE);
    }

    @Override
    //Method which acts upon the Intent's Result(File Explorer)
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // The Request code was randomly assigned , if you are wondering
        if (data != null)
        {
            fileNameAndPaths.clear();
            if (requestCode == FILE_PICKER_REQUEST_CODE)
            {


                ClipData clipData = data.getClipData();
                if (clipData != null)
                {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++)
                    {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        File file = new File(uri.getPath());//create path from uri
                        Log.d("ExMania", file.getPath());
                        ArrayList<String> aList = new ArrayList<String>(Arrays.asList(file.getPath().split(":")));
                        Log.d("ExMania", aList.toString());

                        if (file.getPath().contains("raw"))
                        {

                            try
                            {
                                final String[] split = file.getPath().split(":");
                                Log.d("ExMania", "multiple raw");
                                pathtofile = split[1];
                                Log.d("Looper", split[1]);
                                filename = nameSplitter(pathtofile);


                            }
                            catch (Exception e)
                            {
                                Log.d("ExMania", "Exception Entered");
                                Log.d("ExMania", e.toString());

                            }


                        }
                        else
                        {

                            try
                            {
                                Log.d("ExMania", "multiple content");
                                pathtofile = PathFetcher.getPath(getContext(), uri);
                                filename = nameSplitter(pathtofile);


                            }
                            catch (Exception e)
                            {
                                Log.d("ExMania", "Exception Entered");
                                Log.d("ExMania", e.toString());

                            }


                        }
                        Log.d("ExMania", pathtofile);
                        fileNameAndPaths.add(filename);
                        fileNameAndPaths.add(pathtofile);
                        aList.clear();

                        selectedFilePath = pathtofile;
                    }


                }
                else
                {

                    selectedFileURI = data.getData();
                    File file = new File(selectedFileURI.getPath());//create path from uri
                    Log.d("ExMania", selectedFileURI.toString());
                    if (file.getPath().contains("raw"))
                    {
                        Log.d("ExMania", "Single RAW");
                        final String[] split = file.getPath().split(":");//split the path.
                        try
                        {
                            selectedFilePath = split[1];
                            filename = nameSplitter(selectedFilePath);
                        }
                        catch (Exception e)
                        {
                            Log.d("ExMania", e.toString());

                        }
                    }
                    else
                    {
                        final String[] split = file.getPath().split(":");//split the path.
                        try
                        {
                            Log.d("ExMania", "Single content");
                            selectedFilePath = PathFetcher.getPath(getContext(), selectedFileURI);
                            filename = nameSplitter(selectedFilePath);
                        }
                        catch (Exception e)
                        {
                            Log.d("ExMania", e.toString());

                        }
                    }
                    fileNameAndPaths.add(filename);
                    fileNameAndPaths.add(selectedFilePath);
                    Log.d("ExMan", fileNameAndPaths.toString());

                }

                Log.d("ExMania", fileNameAndPaths.toString());
                new AsyncManager(getActivity(), 1, fileNameAndPaths).execute("");
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, new HomeFragment(), "home")
                        .commit();


            }
        }
    }

    private String nameSplitter(String name)
    {
        String returnName;

        String[] splitter = name.split("/");
        int filepos = splitter.length - 1;
        returnName = splitter[filepos];

        return returnName;
    }

}
