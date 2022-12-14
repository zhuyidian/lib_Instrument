package com.dunn.instrument;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dunn.instrument.service.DeviceInfoService;
import com.dunn.instrument.service.ProcessService;


public class MainActivity extends Activity implements View.OnClickListener {
    public static String TAG = "MainActivity";
    private Button btn1,btn2,btn3,btn4,btn5,btn6,btn7;
    private boolean flag1,flag2,flag3,flag4,flag5,flag6,flag7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn1:
                if(flag1){
                    stopInfoService();
                }else{
                    startInfoService();
                }
                flag1=!flag1;
                break;
            case R.id.btn2:
                if(flag2){
                    stopService(new Intent(MainActivity.this, DeviceInfoService.class));
                }else{
                    startService(new Intent(MainActivity.this, DeviceInfoService.class));
                }
                flag2 = !flag2;
                break;
            case R.id.btn3:
                if(flag3){
                    stopService(new Intent(MainActivity.this, ProcessService.class));
                }else{
                    startService(new Intent(MainActivity.this, ProcessService.class));
                }
                flag3 = !flag3;
                break;
            case R.id.btn4:
                if(flag4){
                    //stopService(new Intent(MainActivity.this, InfoTest3Service.class));
                }else{
                    //startService(new Intent(MainActivity.this, InfoTest3Service.class));
                }
                flag4 = !flag4;
                break;
            case R.id.btn5:
                if(flag5){
                    //stopService(new Intent(MainActivity.this, InfoTest4Service.class));
                }else{
                    //startService(new Intent(MainActivity.this, InfoTest4Service.class));
                }
                flag5 = !flag5;
                break;
            case R.id.btn6:
                if(flag6){
                    //stopService(new Intent(MainActivity.this, InfoTest5Service.class));
                }else{
                    //startService(new Intent(MainActivity.this, InfoTest5Service.class));
                }
                flag6 = !flag6;
                break;
            case R.id.btn7:
                if(flag7){
                    //stopService(new Intent(MainActivity.this, InfoTest6Service.class));
                }else{
                    //startService(new Intent(MainActivity.this, InfoTest6Service.class));
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
        //stopInfoService();
    }

    private void startInfoService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
            return;
        }
        //startService(new Intent(MainActivity.this, InfoService.class));
    }

    private void stopInfoService(){
        //stopService(new Intent(MainActivity.this, InfoService.class));
    }
}
