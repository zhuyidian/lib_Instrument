package com.dunn.instrument;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.dunn.instrument.floatwindow.FloatWindowManager;
import com.dunn.instrument.tools.log.LogUtil;

public class MainApp extends Application {

    public MainApp() {
        LogUtil.i("","");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FloatWindowManager.getInstance().init(this);
        LogUtil.TAG = "Instrument";
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
