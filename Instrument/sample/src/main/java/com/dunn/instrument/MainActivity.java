package com.dunn.instrument;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {
    public static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ApiCrash.crashInit(getApplication());
        //ApiAnrWatchDog.anrInit(getApplicationContext());
        //ApiMethodChoreographer.methodInit();
        //ApiMethodChoreographer.choreographerInit();
    }

    public void click(View view) {
        Log.i(TAG,"click:");
        ApiLogger.logOut("logger日志测试");
        ApiExcel.excelInfo();
        ApiExcel.excelSubmit();
        //ApiCrash.nativeCrashTest();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
