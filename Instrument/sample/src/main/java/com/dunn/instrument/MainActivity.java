package com.dunn.instrument;

import static com.dunn.instrument.service.SpecifyProcessService.KEY_PKG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dunn.instrument.function.execmd.ExeCmdActivity;
import com.dunn.instrument.function.keepalive.KeepAliveActivity;
import com.dunn.instrument.service.CheckInfoService;
import com.dunn.instrument.service.DeviceInfoService;
import com.dunn.instrument.service.FrameworkInfoService;
import com.dunn.instrument.service.MonitorService;
import com.dunn.instrument.service.ResourceService;
import com.dunn.instrument.service.SpecifyProcessService;
import com.dunn.instrument.service.TopProcessService;
import com.dunn.instrument.tools.log.LogUtil;

import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {
    public static String TAG = "MainActivity";
    //windows1
    private Button mWindows1DeviceInfo,
            mWindows1TopProcess,
            mWindows1SpecifyProcess,
            mWindows1ResourceSimulate,
            mWindows1CheckInfo,
            mWindows1Monitor;
    private boolean mWindows1DeviceInfoFlag,
            mWindows1TopProcessFlag,
            mWindows1SpecifyProcessFlag,
            mWindows1ResourceSimulateFlag,
            mWindows1CheckInfoFlag,
            mWindows1MonitorFlag;
    //activity1
    private Button mActivity1KeepAlive,
            mActivity1ExeCmd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG,"onCreate start");
        //Debug.startMethodTracing("tracePath_test");
        //TraceCompat.beginSection("ApponCreate");
        setContentView(R.layout.activity_main);
        //TraceCompat.endSection();
        //Debug.stopMethodTracing();

        //windows1
        mWindows1DeviceInfo = (Button) findViewById(R.id.windows_1_deviceinfo);
        mWindows1TopProcess = (Button) findViewById(R.id.windows_1_topprocess);
        mWindows1SpecifyProcess = (Button) findViewById(R.id.windows_1_specifyprocess);
        mWindows1ResourceSimulate = (Button) findViewById(R.id.windows_1_resourcesimulate);
        mWindows1CheckInfo = (Button) findViewById(R.id.windows_1_checkinfo);
        mWindows1Monitor = (Button) findViewById(R.id.windows_1_monitor);
        mWindows1DeviceInfo.setOnClickListener(this);
        mWindows1TopProcess.setOnClickListener(this);
        mWindows1SpecifyProcess.setOnClickListener(this);
        mWindows1ResourceSimulate.setOnClickListener(this);
        mWindows1CheckInfo.setOnClickListener(this);
        mWindows1Monitor.setOnClickListener(this);

        //activity1
        mActivity1KeepAlive = (Button) findViewById(R.id.activity_1_keepalive);
        mActivity1ExeCmd = (Button) findViewById(R.id.activity_1_execmd);
        mActivity1KeepAlive.setOnClickListener(this);
        mActivity1ExeCmd.setOnClickListener(this);


        LogUtil.i(TAG,"onCreate end");
    }

    @Override
    protected void onResume() {
        super.onResume();
        String fingerprint = SystemProperties.get("ro.build.fingerprint", "");
        LogUtil.i(TAG,"onResume: fingerprint="+fingerprint);
        int sdk = SystemProperties.getInt("ro.build.version.sdk", -1);
        LogUtil.i(TAG,"onResume: sdk="+sdk);
        SystemProperties.set("persist.sys.poweron.test","2");
        SystemProperties.set("third.dunn.test1","123");
        SystemProperties.set("test.dunn.test1","123");
        SystemProperties.set("persist.sys.poweron.intolive", "2");
//        String strength = SystemProperties.get("persist.vendor.sys.pq.shp.strength", "");
//        LogUtil.i(TAG,"onResume: strength="+strength);

        // 获取系统用户管理器实例
        UserManager userManager = (UserManager) getSystemService(Context.USER_SERVICE);
        //获取所有的用户信息
        //List<UserInfo> users = userManager.getUsers(true);
        String name = userManager.getUserName();
        LogUtil.i(TAG,"onResume: name="+name);
    }

    @Override
    public void onClick(View v) {
        checkPermission();
        switch (v.getId()){
            //windows1
            case R.id.windows_1_deviceinfo:   //DeviceInfo
                if(mWindows1DeviceInfoFlag){
                    stopService(new Intent(MainActivity.this, DeviceInfoService.class));
                }else{
                    startService(new Intent(MainActivity.this, DeviceInfoService.class));
                }
                mWindows1DeviceInfoFlag=!mWindows1DeviceInfoFlag;
                break;
            case R.id.windows_1_topprocess:    //TopProcess
                if(mWindows1TopProcessFlag){
                    stopService(new Intent(MainActivity.this, TopProcessService.class));
                }else{
                    startService(new Intent(MainActivity.this, TopProcessService.class));
                }
                mWindows1TopProcessFlag = !mWindows1TopProcessFlag;
                break;
            case R.id.windows_1_specifyprocess:    //SpecifyProcess
                if(mWindows1SpecifyProcessFlag){
                    stopService(new Intent(MainActivity.this, SpecifyProcessService.class));
                }else{
                    //再次检查是否固定监控某个进程
                    String property = SystemProperties.get("third.perf.monitor.pkg", "");
                    if(property==null || property.isEmpty()){
                        property = MainActivity.this.getPackageName();
                    }
                    Intent intent = new Intent(MainActivity.this, SpecifyProcessService.class);
                    intent.putExtra(KEY_PKG,property);
                    startService(intent);
                }
                mWindows1SpecifyProcessFlag = !mWindows1SpecifyProcessFlag;
                break;
            case R.id.windows_1_resourcesimulate:   //ResourceSimulate
                if(mWindows1ResourceSimulateFlag){
                    stopService(new Intent(MainActivity.this, ResourceService.class));
                }else{
                    Intent intentW = new Intent(MainActivity.this, ResourceService.class);
                    intentW.setAction("com.coocaa.intent.action.RESOURCE_ACTION");
                    intentW.putExtra("resource_command", "open_window");
                    startService(intentW);
                }
                mWindows1ResourceSimulateFlag = !mWindows1ResourceSimulateFlag;
                break;
            case R.id.windows_1_checkinfo:  //check info
                if(mWindows1CheckInfoFlag){
                    stopService(new Intent(MainActivity.this, CheckInfoService.class));
                }else{
                    startService(new Intent(MainActivity.this, CheckInfoService.class));
                }
                mWindows1CheckInfoFlag = !mWindows1CheckInfoFlag;
                break;
            case R.id.windows_1_monitor:
                if(mWindows1MonitorFlag){
                    stopService(new Intent(MainActivity.this, MonitorService.class));
                }else{
                    LogUtil.i(TAG,"onClick: start MonitorService");
                    startService(new Intent(MainActivity.this, MonitorService.class));
                }
                mWindows1MonitorFlag = !mWindows1MonitorFlag;
                break;

            //activity1
            case R.id.activity_1_keepalive:  //KeepAlive
                startActivity(new Intent(MainActivity.this, KeepAliveActivity.class));
                break;
            case R.id.activity_1_execmd:  //ExeCmd
                startActivity(new Intent(MainActivity.this, ExeCmdActivity.class));
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
}
