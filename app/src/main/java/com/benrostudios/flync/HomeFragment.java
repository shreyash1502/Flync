package com.benrostudios.flync;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class HomeFragment extends Fragment {

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

}