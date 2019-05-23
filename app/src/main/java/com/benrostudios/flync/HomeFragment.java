package com.benrostudios.flync;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

public class HomeFragment extends Fragment {

    private final static double noBytesInOneGB = 1073741824.0;

    private TextView storageInfoTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragview = inflater.inflate(R.layout.fragment_home, null);
        storageInfoTextView = fragview.findViewById(R.id.storageTextView);

        //code to find out how much storage exists and how much is used
        StatFs stat = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        double totalSpaceInGB = stat.getTotalBytes()/noBytesInOneGB;
        double freeSpaceInGB = stat.getAvailableBytes()/noBytesInOneGB;
        storageInfoTextView.setText("total: " + totalSpaceInGB + "\n free: " + freeSpaceInGB);

        return fragview;
    }

}