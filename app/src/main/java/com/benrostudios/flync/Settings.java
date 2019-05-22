package com.benrostudios.flync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class Settings extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragview = inflater.inflate(R.layout.fragment_settings, null);
        //Just Adding the Button Listener here
        Button Mab = fragview.findViewById(R.id.explorer);
        Mab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating a new Intent to invoke the File Manager
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 100);
            }
        });

        return fragview;
    }

    @Override
    //Method which acts upon the Intent's Result(File Explorer)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // The Request code was randomly assigned , if you are wondering
        if (requestCode == 100) {
            //Dont Mind these debug toasts
            Toast.makeText(getActivity(), "ReqCodeMila", Toast.LENGTH_SHORT).show();
             // we get the uri of selected file here
            Uri uri = data.getData();
            if (uri != null) {
                String filename;
                 filename = uri.getPath();
                 //We Toast the URI Path
                Toast.makeText(getActivity(), filename, Toast.LENGTH_SHORT).show();
            }
        }
    }
}