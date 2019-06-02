package com.benrostudios.flync;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class HomeFragment extends Fragment {


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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        float usedPercent = (float) (usedSpaceInGB/totalSpaceInGB)*100;
        circularProgress.setProgress(usedPercent, 100);
        circularProgress.setProgressTextAdapter(TEXT_ADAPTER);
        CardView sendButton = fragview.findViewById(R.id.sendCardView);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating a new Intent to invoke the File Manager
                checkPermissionsAndOpenFilePicker();

            }
        });
        return fragview;
    }

    private static final CircularProgressIndicator.ProgressTextAdapter TEXT_ADAPTER = new CircularProgressIndicator.ProgressTextAdapter() {
        @Override
        public String formatText(double progress) {
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

    @Override
    //Method which acts upon the Intent's Result(File Explorer)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // The Request code was randomly assigned , if you are wondering
        if (data != null) {
            if (requestCode == FILE_PICKER_REQUEST_CODE) {


                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    fileNameAndPaths.clear();
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        File file = new File(uri.getPath());//create path from uri
                        Log.d("ExMania", file.getPath());
                        ArrayList<String> aList = new ArrayList<String>(Arrays.asList(file.getPath().split(":")));
                        Log.d("ExMania", aList.toString());
                        try{
                            pathtofile = aList.get(1);
                            filename = nameSplitter(pathtofile,2);


                        }catch(Exception e){

                            pathtofile = getRealPathFromURI(uri);
                            filename = nameSplitter(pathtofile,2);

                        }



                        Log.d("ExMania", pathtofile);
                        fileNameAndPaths.add(filename);
                        fileNameAndPaths.add(pathtofile);
                        aList.clear();

                        selectedFilePath = pathtofile;


                    }


                } else {

                    selectedFileURI = data.getData();
                    File file = new File(selectedFileURI.getPath());//create path from uri
                    Log.d("ExMania", selectedFileURI.toString());
                    final String[] split = file.getPath().split(":");//split the path.
                    try{
                        selectedFilePath = split[1];
                        filename = nameSplitter(selectedFilePath,1);
                    }catch(Exception e){
                        selectedFilePath = getRealPathFromURI(selectedFileURI);
                        filename = nameSplitter(selectedFilePath,1);

                    }
                    fileNameAndPaths.add(filename);
                    fileNameAndPaths.add(selectedFilePath);

                }

                Log.d("ExMania", fileNameAndPaths.toString());
                new AsyncManager(getActivity(), 1,fileNameAndPaths).execute("");


            }
        }
    }


    private void checkPermissionsAndOpenFilePicker() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Nahi degi Bhos", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{permission}, PERMISSIONS_REQUEST_CODE);
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
        Intent chooseFile;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        chooseFile = Intent.createChooser(intent, "Choose a file");
        startActivityForResult(chooseFile, FILE_PICKER_REQUEST_CODE);

    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = null;
        String path = null;
        try {
            cursor = getActivity().getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String fileName = cursor.getString(0);
                path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                if (!TextUtils.isEmpty(path)) {
                    return path;
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return path;
    }

    private String nameSplitter(String name, int mode ){
        String returnName;
        if(mode == 1) {
            String[] splitter = name.split("/");
            int filepos = splitter.length - 1;
            returnName = splitter[filepos];
        }else{
            ArrayList<String> b = new ArrayList<String>(Arrays.asList(name.split("/")));
            int filepos = b.size()-1;
            returnName = b.get(filepos);
            b.clear();



        }


        return returnName;
    }


}