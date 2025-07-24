package com.dunn.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.coocaa.platform.cloudcomputer.CloudComputerAgentBackup;
import com.coocaa.platform.cloudcomputer.PackageManagerBackup;
import com.dunn.demo.service.DeviceInfoService;
import com.dunn.demo.service.FrameworksService;
import com.dunn.demo.utils.CommonUtil;

import java.io.File;

import upc.unicom.cloudcomputer.customed.CloudComputerAgent;
import upc.unicom.cloudcomputer.customed.InfoManager;


public class MainActivity extends Activity implements View.OnClickListener {
    public static String TAG = "MainActivity-dunn";
    //service1
    private Button mServiceDeviceinfo,
            mServiceTest1,
            mServiceTest2;
    //activity1
    private Button mActivityTest1,
            mActivityTest2;
    private boolean mServiceDeviceInfoFlag;
    //function1
    private Button mFuntionInstaller, mFunctionUninstaller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate start");

        setContentView(R.layout.activity_main);

        //service1
        mServiceDeviceinfo = (Button) findViewById(R.id.service_deviceinfo);
        mServiceTest1 = (Button) findViewById(R.id.service_test1);
        mServiceTest2 = (Button) findViewById(R.id.service_test2);
        mServiceDeviceinfo.setOnClickListener(this);

        //activity1
        mActivityTest1 = (Button) findViewById(R.id.activity_test1);
        mActivityTest2 = (Button) findViewById(R.id.activity_test2);

        //function1
        mFuntionInstaller = (Button) findViewById(R.id.function_installer);
        mFunctionUninstaller = (Button) findViewById(R.id.function_uninstaller);
        mFuntionInstaller.setOnClickListener(this);
        mFunctionUninstaller.setOnClickListener(this);

        Log.i(TAG,"onCreate end");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume: ");
        //CommonUtil.getBatteryLevel(MainActivity.this);
        //String status = CommonUtil.getBatteryStatus(MainActivity.this);
        //Log.i(TAG,"onResume: status="+status);
        //CommonUtil.getFactoryMacAddresses(MainActivity.this);
        //String control = SystemProperties.get("ro.broadcast.control","");
        testCucc();
        Log.i(TAG,"onResume: rssi="+CommonUtil.getWiFiRssi(MainActivity.this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //service1
            case R.id.service_deviceinfo:
                if(mServiceDeviceInfoFlag){
                    stopService(new Intent(MainActivity.this, DeviceInfoService.class));
                }else{
                    startService(new Intent(MainActivity.this, DeviceInfoService.class));
                }
                mServiceDeviceInfoFlag=!mServiceDeviceInfoFlag;
                break;
            //function1
            case R.id.function_installer:
                installApk();
                break;
            case R.id.function_uninstaller:
                uninstallApkTest();
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
        Log.i(TAG,"onWindowFocusChanged hasFocus="+hasFocus);
    }

    public void installApkTest(){
        File cacheDir = MainActivity.this.getCacheDir();
        String cacheDirPath = cacheDir.getAbsolutePath();
        File file = new File(cacheDirPath,"installer.apk");
        String filePath = file.getAbsolutePath();
        Log.i(TAG,"installApkTest: filePath="+filePath);
        CloudComputerAgentBackup agent = new CloudComputerAgentBackup(MainActivity.this);
        PackageManagerBackup manager = agent.getPackageManager();
        manager.installPackage(Uri.fromFile(file), new PackageManagerBackup.PackageInstallObserver(){
            @Override
            public void packageInstalled(String packageName, int returnCode) {
                Log.i(TAG,"installApk$packageInstalled: packageName="+packageName+", returnCode="+returnCode);
            }
        },2,"");
    }

    public void uninstallApkTest(){
        CloudComputerAgentBackup agent = new CloudComputerAgentBackup(MainActivity.this);
        PackageManagerBackup manager = agent.getPackageManager();
        manager.deletePackage("com.dunn.installer", new PackageManagerBackup.PackageDeleteObserver(){
            @Override
            public void packageDeleted(String packageName, int returnCode) {
                Log.i(TAG,"uninstallApk$packageDeleted: packageName="+packageName+", returnCode="+returnCode);
            }
        },2);
    }

    public void installApk(){
        //File cacheDir = MainActivity.this.getCacheDir();
        File sdCardDir = Environment.getExternalStorageDirectory();
        //String cacheDirPath = cacheDir.getAbsolutePath();
        String sdCardDirPath = sdCardDir.getAbsolutePath();
        File file = new File(sdCardDirPath,"installer.apk");
        String filePath = file.getAbsolutePath();
        Log.i(TAG,"installApk: filePath="+filePath);

        CloudComputerAgentBackup agent = new CloudComputerAgentBackup(MainActivity.this);
        PackageManagerBackup manager = agent.getPackageManager();
        manager.installPackage(Uri.fromFile(file), new PackageManagerBackup.PackageInstallObserver(){
            @Override
            public void packageInstalled(String packageName, int returnCode) {
                Log.i(TAG,"installApk$packageInstalled: packageName="+packageName+", returnCode="+returnCode);
            }
        },0,"");
    }

    public void uninstallApk(){
        CloudComputerAgentBackup agent = new CloudComputerAgentBackup(MainActivity.this);
        PackageManagerBackup manager = agent.getPackageManager();
        manager.deletePackage("com.dunn.installer", new PackageManagerBackup.PackageDeleteObserver(){
            @Override
            public void packageDeleted(String packageName, int returnCode) {
                Log.i(TAG,"uninstallApk$packageDeleted: packageName="+packageName+", returnCode="+returnCode);
            }
        },0);
    }

    public void testCucc(){
        CloudComputerAgent agent = new CloudComputerAgent(MainActivity.this);
        InfoManager infoManager = agent.getInfoManager();
        int batteryLevel = infoManager.getBatteryLevel();
        int batteryScale = infoManager.getBatteryScale();
        int batteryStatus = infoManager.getBatteryStatus();
        String cuei = infoManager.getCuei();
        String diskSize = infoManager.getDiskSize();
        String memorySize = infoManager.getMemorySize();
        String mac = infoManager.getMac();
        String sn = infoManager.getSn();
        String firmwareVersion = infoManager.getFirmwareVersion();
        String protocolVersion = infoManager.getProtocolVersion();
        Log.i(TAG,"testCucc: cuei="+cuei);
        Log.i(TAG,"testCucc: mac="+mac);
        Log.i(TAG,"testCucc: sn="+sn);
        Log.i(TAG,"testCucc: batteryLevel="+batteryLevel);
        Log.i(TAG,"testCucc: batteryScale="+batteryScale);
        Log.i(TAG,"testCucc: batteryStatus="+batteryStatus);
        Log.i(TAG,"testCucc: diskSize="+diskSize);
        Log.i(TAG,"testCucc: memorySize="+memorySize);
        Log.i(TAG,"testCucc: firmwareVersion="+firmwareVersion);
        Log.i(TAG,"testCucc: protocolVersion="+protocolVersion);
    }
}
