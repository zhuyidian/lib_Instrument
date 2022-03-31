package com.dunn.instrument;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Process;
import android.util.Log;

import com.dunn.instrument.excel.ExcelHelp;

public class MainApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ApiExcel.excelInit(getApplicationContext());
//        ApiExcel.clearExcel();
        ApiExcel.setFunctionRowName();
        ApiExcel.setUiRowName();
        ApiExcel.getInfo().mModule0.mPid = Process.myPid()+"";
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
