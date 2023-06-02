package com.dunn.instrument.service;

import static com.dunn.instrument.service.SpecifyProcessService.KEY_PKG;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemProperties;
import android.util.Log;

import com.dunn.instrument.MainActivity;
import com.dunn.instrument.R;
import com.dunn.instrument.function.keepalive.KeepAliveActivity;
import com.dunn.instrument.thread.ThreadHelp;
import com.dunn.instrument.tools.log.LogUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class ResourceService extends Service{
    static final String TAG = "ResourceService";
    public static boolean hasStarted = false;
    private volatile boolean mCpuStart = false;
    private List<PssBean> mCacheList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        LogUtil.i(TAG, " attachBaseContext");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, " onCreate");

        hasStarted = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService(startId);
        handleInnerEvent(intent);
        return super.onStartCommand(intent, flags, startId);
        //return START_NOT_STICKY;  只会拉起进程
    }

    /**
     * CPU command:
     * cpu_close、cpu_low、cpu_middle、cpu_high
     * MEM command:
     * mem_close、mem_low、mem_middle、mem_high
     *
     * 注意：
     * 使用CPU或MEM前先停止，也即先执行cpu_close或mem_close。否则无效。
     *
     * 单次启动：
     * am start-foreground-service -a com.coocaa.intent.action.RESOURCE_ACTION --es resource_command cpu_low
     *
     * 循环10启动：
     * while true; do am start-foreground-service -a com.coocaa.intent.action.RESOURCE_ACTION --es resource_command cpu_low;sleep 10;done;
     */
    private void handleInnerEvent(Intent intent) {
        LogUtil.d(TAG, "handleInnerEvent intent=" + intent);
        if (intent == null) return;
        String command = intent.getStringExtra("resource_command");
        LogUtil.d(TAG, "handleInnerEvent command = " + command + ",from=" + intent.getStringExtra("from"));
        if (command == null) {
            LogUtil.d(TAG, "handleInnerEvent command return!!!!!!");
            return;
        }

        if("open_window".equals(command)) {
            Intent intentW = new Intent(ResourceService.this, FrameworkInfoService.class);
            startService(intentW);
        }else if("cpu_low".equals(command)){
            cpuLowUsageTest();
        }else if("cpu_middle".equals(command)){
            cpuMiddleUsageTest();
        }else if("cpu_high".equals(command)){
            cpuHighUsageTest();
        }else if("cpu_close".equals(command)){
            cpuCloseUsageTest();
        }else if("mem_low".equals(command)){
            mem100MBTest();
        }else if("mem_middle".equals(command)){
            mem150MBTest();
        }else if("mem_high".equals(command)){
            mem200MBTest();
        }else if("mem_close".equals(command)){
            memFreeTest();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LogUtil.d(TAG, " onLowMemory!!!");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        LogUtil.d(TAG, " onTrimMemory level=" + level);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, " onDestroy!!!");
        hasStarted = false;
    }

    private void startForegroundService(int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "RESOURCE";
            String CHANNEL_NAME = "RESOURCE";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

            Intent intent = new Intent();
            intent.setAction("notification.receiver.action.test");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Notification notification = new Notification.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.icon_contorl).setContentIntent(pendingIntent).build();
            startForeground(startId, notification);
            LogUtil.d(TAG, "startForegroundService");
        }
    }

//    public static void startCenterService(Context context, int cmd) {
//        Log.d(TAG, "startCenterService cmd=" + cmd);
//        Intent intent = new Intent(context, CenterService.class);
//        if (cmd != -1) {
//            intent.putExtra(Constant.CONTROL_CENTER_COMMAND, cmd);
//        }
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(intent);
//            } else {
//                context.startService(intent);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            CLog.e(TAG, "startCenterService e=" + e.getMessage());
//        }
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void memFreeTest(){
        mCacheList.clear();
        System.gc();
    }

    private void mem100MBTest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mCacheList.clear();
                /*
                 * 100M = 100*1024*1000 = 102,400,000 / 100 = 1024000
                 */
                for(int i=0;i<1024000;i++){
                    PssBean bean = new PssBean();
                    mCacheList.add(bean);
                }
                LogUtil.d(TAG, "mem100MBTest: mCacheList size="+mCacheList.size());
            }
        }).start();
    }

    private void mem150MBTest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mCacheList.clear();
                /*
                 * 150M = 150*1024*1000 = 153,600,000 / 100 = 1,536,000
                 */
                for(int i=0;i<1536000;i++){
                    PssBean bean = new PssBean();
                    mCacheList.add(bean);
                }
                LogUtil.d(TAG, "mem150MBTest: mCacheList size="+mCacheList.size());
            }
        }).start();
    }

    private void mem200MBTest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mCacheList.clear();
                /*
                 * 150M = 200*1024*1000 = 204,800,000 / 100 = 2,048,000
                 */
                for(int i=0;i<2048000;i++){
                    PssBean bean = new PssBean();
                    mCacheList.add(bean);
                }
                LogUtil.d(TAG, "mem200MBTest: mCacheList size="+mCacheList.size());
            }
        }).start();
    }

    private void cpuCloseUsageTest(){
        mCpuStart = false;
    }

    private void cpuLowUsageTest(){
        if(mCpuStart){
            return;
        }
        mCpuStart = true;
        int threadCount = 1;
        int core1 = getNumberOfCPUCores();
        int core2 = getNumCpuCores();
        LogUtil.d(TAG, "cpuLowUsageTest: thread count=" + threadCount+", core="+core1+", core2="+core2);
        for(int i = 0; i< threadCount; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long value=0l;
                    while(mCpuStart){
                        value++;
                        value+=1;
                        value+=2;
                        if(value>=Long.MAX_VALUE){
                            value = 0l;
                        }
                    }
                }
            }).start();
        }
    }

    private void cpuMiddleUsageTest(){
        if(mCpuStart){
            return;
        }
        mCpuStart = true;
        int threadCount = ThreadHelp.CPUCOUNT / 2;
        LogUtil.d(TAG, "cpuMiddleUsageTest: thread count=" + threadCount);
        for(int i = 0; i< threadCount; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long value=0l;
                    while(mCpuStart){
                        value++;
                        value+=1;
                        value+=2;
                        if(value>=Long.MAX_VALUE){
                            value = 0l;
                        }
                    }
                }
            }).start();
        }
    }

    private void cpuHighUsageTest(){
        if(mCpuStart){
            return;
        }
        mCpuStart = true;
        int threadCount = ThreadHelp.CPUCOUNT;
        LogUtil.d(TAG, "cpuHighUsageTest: thread count=" + threadCount);
        for(int i = 0; i< threadCount; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long value=0l;
                    while(mCpuStart){
                        value++;
                        value+=1;
                        value+=2;
                        if(value>=Long.MAX_VALUE){
                            value = 0l;
                        }
                    }
                }
            }).start();
        }
    }

    private int getNumberOfCPUCores() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            // Gingerbread doesn't support giving a single application access to both cores, but a
            // handful of devices (Atrix 4G and Droid X2 for example) were released with a dual-core
            // chipset and Gingerbread; that can let an app in the background run without impacting
            // the foreground application. But for our purposes, it makes them single core.
            return 1;  //上面的意思就是2.3以前不支持多核,有些特殊的设备有双核...不考虑,就当单核!!
        }
        int cores;
        try {
            cores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
            LogUtil.d(TAG, "getNumberOfCPUCores: files cores=" + cores);
        } catch (SecurityException e) {
            cores = ThreadHelp.CPUCOUNT;   //这个常量得自己约定
        } catch (NullPointerException e) {
            cores = ThreadHelp.CPUCOUNT;
        }
        LogUtil.d(TAG, "getNumberOfCPUCores: cores=" + cores);
        return cores;
    }

    private final FileFilter CPU_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getName();
            //regex is slow, so checking char by char.
            if (path.startsWith("cpu")) {
                for (int i = 3; i < path.length(); i++) {
                    if (path.charAt(i) < '0' || path.charAt(i) > '9') {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    };

    private int getNumCpuCores() {
        try {
            // Get directory containing CPU info
            java.io.File dir = new java.io.File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            java.io.File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    // Check if filename is "cpu", followed by a single digit number
                    if (java.util.regex.Pattern.matches("cpu[0-9]+", file.getName())) {
                        return true;
                    }
                    return false;
                }
            });
            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            // Default to return 1 core
            Log.e(TAG, "Failed to count number of cores, defaulting to 1", e);
            return 1;
        }
    }

    /**
     * 100byte 的类
     */
    public class PssBean{
        public long mTime = 0l;
        public long mCount = 0l;
        public long mAge = 0l;
        public long mPss = 0l;
        public long mUse = 0l;

        public long nTime = 0l;
        public long nCount = 0l;
        public long nAge = 0l;
        public long nPss = 0l;
        public long nUse = 0l;

        public long hPss = 0l;
        public long hUse = 0l;

        public int cPss = 0;
    }
}
