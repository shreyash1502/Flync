package com.benrostudios.flync;

import android.content.Context;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;


public class Finder extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "FINDER";

    private WeakReference<Context> mContextRef;
    private Context mContext;

    public Finder(Context context) {
        mContextRef = new WeakReference<Context>(context);
        this.mContext = context;

    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, "Let's sniff the network");

        return null;
    }
}