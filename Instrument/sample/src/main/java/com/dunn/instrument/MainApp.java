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
        //ApiLogger.loggerInit();
        //ApiExcel.excelInit(getApplicationContext());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
