package com.dunn.instrument;

import static com.dunn.instrument.service.SpecifyProcessService.KEY_PKG;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dunn.instrument.function.keepalive.KeepAliveActivity;
import com.dunn.instrument.service.DeviceInfoService;
import com.dunn.instrument.service.SpecifyProcessService;
import com.dunn.instrument.service.TopProcessService;
import com.dunn.instrument.tools.log.LogUtil;


public class MainActivity extends Activity implements View.OnClickListener {
    public static String TAG = "MainActivity";
    private Button btn1,btn2,btn3,btn4,btn5,btn6,btn7;
    private boolean flag1,flag2,flag3,flag4,flag5,flag6,flag7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG,"onCreate start");
        //Debug.startMethodTracing("tracePath_test");
        //TraceCompat.beginSection("ApponCreate");
        setContentView(R.layout.activity_main);
        //TraceCompat.endSection();
        //Debug.stopMethodTracing();

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btn5 = (Button) findViewById(R.id.btn5);
        btn6 = (Button) findViewById(R.id.btn6);
        btn7 = (Button) findViewById(R.id.btn7);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);

        LogUtil.i(TAG,"onCreate end");
    }

    @Override
    public void onClick(View v) {
        checkPermission();
        switch (v.getId()){
            case R.id.btn1:   //DeviceInfo
                if(flag1){
                    stopService(new Intent(MainActivity.this, DeviceInfoService.class));
                }else{
                    startService(new Intent(MainActivity.this, DeviceInfoService.class));
                }
                flag1=!flag1;
                break;
            case R.id.btn2:    //TopProcess
                if(flag2){
                    stopService(new Intent(MainActivity.this, TopProcessService.class));
                }else{
                    startService(new Intent(MainActivity.this, TopProcessService.class));
                }
                flag2 = !flag2;
                break;
            case R.id.btn3:    //SpecifyProcess
                if(flag3){
                    stopService(new Intent(MainActivity.this, SpecifyProcessService.class));
                }else{
                    //再次检查是否固定监控某个进程
                    String property = SystemProperties.get("third.perf.monitor.pkg", "");
                    Intent intent = new Intent(MainActivity.this, SpecifyProcessService.class);
                    intent.putExtra(KEY_PKG,property);
                    startService(intent);
                }
                flag3 = !flag3;
                break;
            case R.id.btn4:   //KeepAlive
                Intent intent1 = new Intent(MainActivity.this, KeepAliveActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn5:
                if(flag5){

                }else{

                }
                flag5 = !flag5;
                break;
            case R.id.btn6:
                if(flag6){

                }else{

                }
                flag6 = !flag6;
                break;
            case R.id.btn7:
                if(flag7){

                }else{

                }
                flag7 = !flag7;
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        LogUtil.i(TAG,"onWindowFocusChanged hasFocus="+hasFocus);
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
            return true;
        }
        return true;
    }

//    private void testSkyMonitor(){
//        SkyMonitorHelper mSkyMonitorHelper = new SkyMonitorHelper(MainActivity.this);
//        Log.i("SkyMonitor_Test","testSkyMonitor1: mSkyMonitorHelper="+mSkyMonitorHelper);
//        mSkyMonitorHelper.testMethod();
//    }
}
