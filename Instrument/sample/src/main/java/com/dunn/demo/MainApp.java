package com.dunn.demo;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.dunn.demo.floatwindow.FloatWindowManager;

public class MainApp extends Application {
    private static final String TAG = "MainApp";
    public static Context mContext;

    public MainApp() {
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.i(TAG,"attachBaseContext:");
        mContext = base;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"onCreate:");
        FloatWindowManager.getInstance().init(MainApp.this.getApplicationContext());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
