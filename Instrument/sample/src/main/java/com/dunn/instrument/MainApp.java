package com.dunn.instrument;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import com.dunn.instrument.floatwindow.FloatWindowManager;
import com.dunn.instrument.tools.log.LogUtil;

public class MainApp extends Application {
    private static final String TAG = "MainApp";
    public static Context mContext;

    public MainApp() {
        LogUtil.i("","");
        LogUtil.TAG = "Instrument";
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        LogUtil.i(TAG,"attachBaseContext:");
        mContext = base;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG,"onCreate:");
        FloatWindowManager.getInstance().init(MainApp.this.getApplicationContext());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
