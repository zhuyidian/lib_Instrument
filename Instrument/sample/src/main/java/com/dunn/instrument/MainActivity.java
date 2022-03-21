package com.dunn.instrument;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dunn.instrument.excel.ExcelHelp;
import com.dunn.instrument.logger.InitJointPoint;
import com.dunn.instrument.logger.LogJointPoint;
import com.dunn.instrument.logger.ReleaseJointPoint;
import com.dunn.instrument.logger.UploadJointPoint;

public class MainActivity extends Activity {
    public static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ApiCrash.crashInit(getApplication());
        ApiAnrWatchDog.anrInit(getApplicationContext());

        long AA = 99 / 1000;
        float BB = 99 / (float)1000;
        Log.i(TAG,"AA="+AA+", BB="+BB);
    }

    public void click(View view) {
        Log.i(TAG,"click:");
        ApiLogger.logOut("logger日志测试");
        ApiExcel.excelInfo();
        ApiExcel.excelSubmit();
        //ApiCrash.nativeCrashTest();
//        try {
//            Thread.sleep(20000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApiLogger.loggerRelease();
    }

    public void jump() {
        Intent intent = new Intent(this,SecondActivity.class);
        startActivity(intent);
    }
}
