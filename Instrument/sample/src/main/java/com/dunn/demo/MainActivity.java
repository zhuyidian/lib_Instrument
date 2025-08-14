package com.dunn.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.coocaa.platform.cloudcomputer.CloudComputerAgentBackup;
import com.coocaa.platform.cloudcomputer.PackageManagerBackup;
import com.dunn.demo.activity.CameraPreviewActivity;
import com.dunn.demo.activity.Webview1Activity;
import com.dunn.demo.service.DeviceInfoService;
import com.dunn.frameworks.installer.commonapp1.AppCoreInstaller;
import com.dunn.frameworks.installer.commonapp1.SilentInstaller;
import com.dunn.frameworks.installer.commonapp2.IPackageInstallListener;
import com.dunn.frameworks.installer.commonapp2.InstallHelper;
import com.dunn.frameworks.installer.systemapp.SystemAppInstallerTools;
import com.dunn.frameworks.launcher.LauncherTools;

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
    private Button mActivityCamerapreview, mActivityTest2, mActivityWebview1;
    private boolean mServiceDeviceInfoFlag;
    //function1
    private Button mFuntionInstaller, mFunctionUninstaller, mFunctionPlatformInterface, mFunctionSystemInstaller, mFunctionSystemUninstaller,
            mFunctionUninstallerSystem, mFunctionRestoreInstallerSystem;
    //function2
    private Button mFunctionDefaultLauncher,mFunctionClearLauncher,mFunctionSystemInstaller2,mFunctionSystemUninstaller2;

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
        mActivityCamerapreview = (Button) findViewById(R.id.activity_camerapreview);
        mActivityTest2 = (Button) findViewById(R.id.activity_test2);
        mActivityWebview1 = (Button) findViewById(R.id.activity_webview1);
        mActivityCamerapreview.setOnClickListener(this);
        mActivityWebview1.setOnClickListener(this);

        //function1
        mFuntionInstaller = (Button) findViewById(R.id.function_installer);
        mFunctionUninstaller = (Button) findViewById(R.id.function_uninstaller);
        mFunctionPlatformInterface = (Button) findViewById(R.id.function_platforminterface);
        mFunctionSystemInstaller = (Button) findViewById(R.id.function_systeminstaller);
        mFunctionSystemUninstaller = (Button) findViewById(R.id.function_systemuninstaller);
        mFunctionUninstallerSystem = (Button) findViewById(R.id.function_uninstallersystem);
        mFunctionRestoreInstallerSystem = (Button) findViewById(R.id.function_restoreinstallersystem);
        mFuntionInstaller.setOnClickListener(this);
        mFunctionUninstaller.setOnClickListener(this);
        mFunctionPlatformInterface.setOnClickListener(this);
        mFunctionSystemInstaller.setOnClickListener(this);
        mFunctionSystemUninstaller.setOnClickListener(this);
        mFunctionUninstallerSystem.setOnClickListener(this);
        mFunctionRestoreInstallerSystem.setOnClickListener(this);
        //function2
        mFunctionDefaultLauncher = (Button) findViewById(R.id.function_defaultlauncher);
        mFunctionClearLauncher = (Button) findViewById(R.id.function_clearlauncher);
        mFunctionSystemInstaller2 = (Button) findViewById(R.id.function_systeminstallertwo);
        mFunctionSystemUninstaller2 = (Button) findViewById(R.id.function_systemuninstallertwo);
        mFunctionDefaultLauncher.setOnClickListener(this);
        mFunctionClearLauncher.setOnClickListener(this);
        mFunctionSystemInstaller2.setOnClickListener(this);
        mFunctionSystemUninstaller2.setOnClickListener(this);


        Log.i(TAG,"onCreate end");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume: ");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //activity1
            case R.id.activity_camerapreview:
                startActivity(new Intent(MainActivity.this, CameraPreviewActivity.class));
                break;
            case R.id.activity_webview1:
                startActivity(new Intent(MainActivity.this, Webview1Activity.class));
                break;
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
                //installApk();
                break;
            case R.id.function_uninstaller:
                //uninstallApkTest();
                break;
            case R.id.function_platforminterface:
                cuccInfo();
                break;
            case R.id.function_systeminstaller:
                systemInstallApk();
                break;
            case R.id.function_systemuninstaller:
                systemUninstallApk("com.happyelements.AndroidAnimal");
                break;
            case R.id.function_uninstallersystem:
                uninstallerSystemApp("com.sinovatech.unicom.ui");
                break;
            case R.id.function_restoreinstallersystem:
                restoreInstallerSystemApp("com.sinovatech.unicom.ui");
                break;
            //function2
            case R.id.function_defaultlauncher:
//                setDefaultLauncher("com.coocaa.cucclauncher");
//                setDefaultLauncher("com.coocaa.study.jxw");
                setDefaultLauncher("com.coocaa.launcherdemo");
                break;
            case R.id.function_clearlauncher:
                setClearLauncher("com.coocaa.launcherdemo");
                break;
            case R.id.function_systeminstallertwo:
                systemInstallApk2();
                break;
            case R.id.function_systemuninstallertwo:
                systemUninstallApk2("com.happyelements.AndroidAnimal");
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

    /**
     * CloudComputerAgentBackup是平台接口，app中的CloudComputerAgent只为了占位编译
     * CloudComputerAgentBackup是系统接口，普通应用调用，具体安装是由某个系统应用执行
     * 普通应用会通过此接口静默安装应用，普通应用非系统应用，推荐厂商在实现是通过调用有权限的服务端进行安装
     * 注：因为牵扯到apk拷贝，系统应用需要从普通调用进程拷贝apk，所以要考虑权限问题，因此普通调用进程最好将apk放置于系统应用有权限拷贝apk的地方
     */
    private void installApkTest(){
        try {
            Log.d(TAG,"installApkTest: ");
            File cacheDir = MainActivity.this.getCacheDir();
            String cacheDirPath = cacheDir.getAbsolutePath();
            File file = new File(cacheDirPath, "installer.apk");
            String filePath = file.getAbsolutePath();
            Log.i(TAG, "installApkTest: filePath=" + filePath);
            CloudComputerAgentBackup agent = new CloudComputerAgentBackup(MainActivity.this);
            PackageManagerBackup manager = agent.getPackageManager();
            manager.installPackage(Uri.fromFile(file), new PackageManagerBackup.PackageInstallObserver() {
                @Override
                public void packageInstalled(String packageName, int returnCode) {
                    Log.i(TAG, "installApk$packageInstalled: packageName=" + packageName + ", returnCode=" + returnCode);
                }
            }, 2, "");
        }catch (Exception e){
            Log.e(TAG,"installApkTest: e="+e);
            Log.e(TAG, "installApkTest: e.getCause()=" + e.getCause());
        }
    }

    /**
     * CloudComputerAgentBackup是平台接口，app中的CloudComputerAgent只为了占位编译
     * CloudComputerAgentBackup是系统接口，普通应用调用，具体卸载是由某个系统应用执行
     * 普通应用会通过此接口静默卸载应用，普通应用非系统应用，推荐厂商在实现是通过调用有权限的服务端进行卸载，仅针对第三方应用，不能卸载系统应用
     */
    private void uninstallApkTest(){
        try {
            Log.d(TAG,"uninstallApkTest: ");
            CloudComputerAgentBackup agent = new CloudComputerAgentBackup(MainActivity.this);
            PackageManagerBackup manager = agent.getPackageManager();
            manager.deletePackage("com.dunn.installer", new PackageManagerBackup.PackageDeleteObserver() {
                @Override
                public void packageDeleted(String packageName, int returnCode) {
                    Log.i(TAG, "uninstallApk$packageDeleted: packageName=" + packageName + ", returnCode=" + returnCode);
                }
            }, 2);
        }catch (Exception e){
            Log.e(TAG,"uninstallApkTest: e="+e);
            Log.e(TAG, "uninstallApkTest: e.getCause()=" + e.getCause());
        }
    }

    /**
     * CloudComputerAgentBackup是平台接口，app中的CloudComputerAgent只为了占位编译
     * CloudComputerAgentBackup是系统接口，普通应用调用，具体安装是由某个系统应用执行
     * 普通应用会通过此接口静默安装应用，普通应用非系统应用，推荐厂商在实现是通过调用有权限的服务端进行安装
     * 注：因为牵扯到apk拷贝，系统应用需要从普通调用进程拷贝apk，所以要考虑权限问题，因此普通调用进程最好将apk放置于系统应用有权限拷贝apk的地方
     */
    private void installApk(){
        try {
            Log.d(TAG,"installApk: ");
            //File cacheDir = MainActivity.this.getCacheDir();
            File sdCardDir = Environment.getExternalStorageDirectory();
            //String cacheDirPath = cacheDir.getAbsolutePath();
            String sdCardDirPath = sdCardDir.getAbsolutePath();
            File file = new File(sdCardDirPath, "installer.apk");
            String filePath = file.getAbsolutePath();
            Log.i(TAG, "installApk: filePath=" + filePath);

            CloudComputerAgentBackup agent = new CloudComputerAgentBackup(MainActivity.this);
            PackageManagerBackup manager = agent.getPackageManager();
            manager.installPackage(Uri.fromFile(file), new PackageManagerBackup.PackageInstallObserver() {
                @Override
                public void packageInstalled(String packageName, int returnCode) {
                    Log.i(TAG, "installApk$packageInstalled: packageName=" + packageName + ", returnCode=" + returnCode);
                }
            }, 0, "");
        }catch (Exception e){
            Log.e(TAG,"installApk: e="+e);
            Log.e(TAG, "installApk: e.getCause()=" + e.getCause());
        }
    }

    /**
     * CloudComputerAgentBackup是平台接口，app中的CloudComputerAgent只为了占位编译
     * CloudComputerAgentBackup是系统接口，普通应用调用，具体卸载是由某个系统应用执行
     * 普通应用会通过此接口静默卸载应用，普通应用非系统应用，推荐厂商在实现是通过调用有权限的服务端进行卸载，仅针对第三方应用，不能卸载系统应用
     */
    private void uninstallApk(){
        try {
            Log.d(TAG, "uninstallApk: ");
            CloudComputerAgentBackup agent = new CloudComputerAgentBackup(MainActivity.this);
            PackageManagerBackup manager = agent.getPackageManager();
            manager.deletePackage("com.dunn.installer", new PackageManagerBackup.PackageDeleteObserver() {
                @Override
                public void packageDeleted(String packageName, int returnCode) {
                    Log.i(TAG, "uninstallApk$packageDeleted: packageName=" + packageName + ", returnCode=" + returnCode);
                }
            }, 0);
        }catch (Exception e){
            Log.e(TAG,"uninstallApk: e="+e);
            Log.e(TAG, "uninstallApk: e.getCause()=" + e.getCause());
        }
    }

    /**
     * 至少是平台签名应用才可以
     */
    private void systemInstallApk(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    File cacheDir = MainActivity.this.getCacheDir();
                    String cacheDirPath = cacheDir.getAbsolutePath();
                    File file = new File(cacheDirPath, "installer.apk");
                    Log.d(TAG,"systemInstallApk: path="+file.getAbsolutePath());
                    new SilentInstaller(MainActivity.this).silentInstall(Uri.fromFile(file).getPath(), new AppCoreInstaller.InstallerListener() {
                        @Override
                        public void onInstallStart(String pkg) {
                            Log.d(TAG, "systemInstallApk onInstallStart: pkg=" + pkg);
                        }

                        @Override
                        public void onInstallEnd(String archive, AppCoreInstaller.INSTALL_RESULT result, String pkg, String extra) {
                            Log.d(TAG, "systemInstallApk onInstallEnd: archive=" + archive + ",pkg=" + pkg + ",result=" + result);
                        }
                    });
                }catch (Exception e){
                    Log.e(TAG,"systemInstallApk: e="+e);
                    Log.e(TAG, "systemInstallApk: e.getCause()=" + e.getCause());
                }
            }
        });
    }

    /**
     * 至少是平台签名应用才可以
     */
    private void systemUninstallApk(String pkg){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG,"systemUninstallApk: pkg="+pkg);
                    new SilentInstaller(MainActivity.this).unInstall(pkg, new AppCoreInstaller.UninstallListener() {
                        @Override
                        public void onUninstallStart(String packageName) {
                            Log.d(TAG, "systemUninstallApk onUninstallStart: pkg=" + packageName);
                        }

                        @Override
                        public void onUninstallEnd(String packageName, AppCoreInstaller.UNINSTALL_RESULT result, String extra) {
                            Log.d(TAG, "systemUninstallApk onUninstallEnd: pkg=" + packageName+", result="+result);
                        }
                    });
                }catch (Exception e){
                    Log.e(TAG,"systemUninstallApk: e="+e);
                    Log.e(TAG, "systemUninstallApk: e.getCause()=" + e.getCause());
                }
            }
        });
    }

    /**
     * 至少是平台签名应用才可以
     */
    private void uninstallerSystemApp(String pkg){
        try{
            Log.d(TAG,"uninstallerSystemApp: pkg="+pkg);
            SystemAppInstallerTools.unInstallApp(MainActivity.this,pkg);
        }catch (Exception e){
            Log.e(TAG,"uninstallerSystemApp: e="+e);
            Log.e(TAG, "uninstallerSystemApp: e.getCause()=" + e.getCause());
        }
    }

    /**
     * 至少是平台签名应用才可以
     */
    private void restoreInstallerSystemApp(String pkg){
        try{
            Log.d(TAG,"restoreInstallerSystemApp: pkg="+pkg);
            SystemAppInstallerTools.installApp(MainActivity.this,pkg);
        }catch (Exception e){
            Log.e(TAG,"restoreInstallerSystemApp: e="+e);
            Log.e(TAG, "restoreInstallerSystemApp: e.getCause()=" + e.getCause());
        }
    }

    /**
     * 设置默认主页
     * 至少是平台签名应用才可以
     * 注：切记app权限声明
     * @param pkg
     */
    private void setDefaultLauncher(String pkg){
        try {
            Log.d(TAG, "setDefaultLauncher: pkg="+pkg);
            LauncherTools.setDefaultHome(MainActivity.this, pkg);
        }catch (Exception e){
            Log.e(TAG,"setDefaultLauncher: e="+e);
            Log.e(TAG, "setDefaultLauncher: e.getCause()=" + e.getCause());
        }
    }

    private void setClearLauncher(String pkg){
        try {
            Log.d(TAG, "setClearLauncher: pkg="+pkg);
            LauncherTools.clearDefaultLauncher(MainActivity.this, pkg);
        }catch (Exception e){
            Log.e(TAG,"setClearLauncher: e="+e);
            Log.e(TAG, "setClearLauncher: e.getCause()=" + e.getCause());
        }
    }

    /**
     * 至少是平台签名应用才可以
     */
    private void systemInstallApk2(){
        try {
            Log.d(TAG, "systemInstallApk2: ");
            File cacheDir = MainActivity.this.getCacheDir();
            String cacheDirPath = cacheDir.getAbsolutePath();
            File file = new File(cacheDirPath, "installer.apk");
            Log.d(TAG,"systemInstallApk2: path="+file.getAbsolutePath());
            InstallHelper hepler = new InstallHelper(MainActivity.this);
            hepler.installPackage(file, new IPackageInstallListener() {
                @Override
                public void onPackageInstalled(String basePackageName, boolean success, int returnCode, String msg) {
                    Log.d(TAG,"systemInstallApk2 onPackageInstalled: basePackageName="+basePackageName+", success="+success+
                            ", returnCode="+returnCode+", msg="+msg);
                }
            });
        }catch (Exception e){
            Log.e(TAG,"systemInstallApk2: e="+e);
            Log.e(TAG, "systemInstallApk2: e.getCause()=" + e.getCause());
        }
    }

    /**
     * 至少是平台签名应用才可以
     */
    private void systemUninstallApk2(String pkg){
        try {
            Log.d(TAG, "systemUninstallApk2: pkg="+pkg);
            InstallHelper hepler = new InstallHelper(MainActivity.this);
            hepler.uninstallPackage(pkg);
        }catch (Exception e){
            Log.e(TAG,"systemUninstallApk2: e="+e);
            Log.e(TAG, "systemUninstallApk2: e.getCause()=" + e.getCause());
        }
    }

    /**
     * CloudComputerAgent是平台接口，app中的CloudComputerAgent只为了占位编译
     */
    private void cuccInfo(){
        try {
            Log.d(TAG, "cuccInfo: ");
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
            Log.i(TAG, "cuccInfo: cuei=" + cuei);
            Log.i(TAG, "cuccInfo: mac=" + mac);
            Log.i(TAG, "cuccInfo: sn=" + sn);
            Log.i(TAG, "cuccInfo: batteryLevel=" + batteryLevel);
            Log.i(TAG, "cuccInfo: batteryScale=" + batteryScale);
            Log.i(TAG, "cuccInfo: batteryStatus=" + batteryStatus);
            Log.i(TAG, "cuccInfo: diskSize=" + diskSize);
            Log.i(TAG, "cuccInfo: memorySize=" + memorySize);
            Log.i(TAG, "cuccInfo: firmwareVersion=" + firmwareVersion);
            Log.i(TAG, "cuccInfo: protocolVersion=" + protocolVersion);
        }catch (Exception e){
            Log.e(TAG,"cuccInfo: e="+e);
            Log.e(TAG, "cuccInfo: e.getCause()=" + e.getCause());
        }
    }
}
