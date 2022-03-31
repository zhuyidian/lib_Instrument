package com.dunn.instrument;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Process;
import android.util.Log;

import com.dunn.instrument.excel.ExcelHelp;
import com.dunn.instrument.logger.InitJointPoint;
import com.dunn.instrument.matrix.MatrixApplication;

public class MainApp extends MatrixApplication {

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
