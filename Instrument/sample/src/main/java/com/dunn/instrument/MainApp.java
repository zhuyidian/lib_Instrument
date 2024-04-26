package com.dunn.instrument;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.dunn.instrument.api.ApiExcel;
import com.dunn.instrument.floatwindow.FloatWindowManager;
import com.dunn.instrument.function.keepalive.KeepAliveActivity;
import com.dunn.instrument.service.FrameworkInfoService;
import com.dunn.instrument.service.ResourceService;
import com.dunn.instrument.shell.Telnet;
import com.dunn.instrument.tools.log.LogUtil;

public class MainApp extends Application {
    private static final String TAG = "MainApp";
    private Telnet mTelnet;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case 0:
                    Intent intentW = new Intent(MainApp.this, ResourceService.class);
                    intentW.setAction("com.coocaa.intent.action.RESOURCE_ACTION");
                    intentW.putExtra("resource_command", "open_window");
                    startService(intentW);
                    break;
                case 1:
                    Intent intentW1 = new Intent(MainApp.this, ResourceService.class);
                    intentW1.setAction("com.coocaa.intent.action.RESOURCE_ACTION");
                    intentW1.putExtra("resource_command", "cpu_close");
                    startService(intentW1);
                    break;
                case 2:
                    Intent intentW2 = new Intent(MainApp.this, ResourceService.class);
                    intentW2.setAction("com.coocaa.intent.action.RESOURCE_ACTION");
                    intentW2.putExtra("resource_command", "cpu_low");
                    startService(intentW2);
                    break;
            }
        }
    };

    public MainApp() {
        LogUtil.i("","");
        LogUtil.TAG = "Instrument";
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        LogUtil.i(TAG,"attachBaseContext:");

//        mHandler.sendEmptyMessageDelayed(0,1);
//        mHandler.sendEmptyMessageDelayed(1,2);
//        mHandler.sendEmptyMessageDelayed(2,3);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG,"onCreate:");
        //mTelnet = new Telnet();
        //mTelnet.startTelnet(MainApp.this.getApplicationContext());
        FloatWindowManager.getInstance().init(MainApp.this.getApplicationContext());

        //excel
//        ApiExcel.excelInit(getApplicationContext());
//        ApiExcel.setFunctionRowName();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


}
