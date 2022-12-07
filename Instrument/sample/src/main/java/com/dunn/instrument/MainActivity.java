package com.dunn.instrument;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class MainActivity extends Activity {
    public static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Debug.startMethodTracing("tracePath_test");
        //TraceCompat.beginSection("ApponCreate");
        setContentView(R.layout.activity_main);
        //TraceCompat.endSection();
        //Debug.stopMethodTracing();
    }

    public void click(View view) {
        Log.i(TAG,"click:");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
