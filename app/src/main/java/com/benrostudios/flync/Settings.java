package com.benrostudios.flync;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;



public class Settings extends Fragment {


    @Nullable



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragview = inflater.inflate(R.layout.fragment_settings, null);
        if (getActivity() == null)
            Toast.makeText(null, "NullActivity", Toast.LENGTH_SHORT).show();
       



        return fragview;
    }


}
