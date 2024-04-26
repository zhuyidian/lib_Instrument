package com.dunn.instrument.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dunn.instrument.R;
import com.dunn.instrument.floatwindow.FloatWindowManager;
import com.dunn.instrument.floatwindow.WindowRecordBean;
import com.dunn.instrument.tools.framework.ram.MemManager;
import com.dunn.instrument.tools.framework.ram.MemTools;
import com.dunn.instrument.tools.framework.system.SystemUtil;
import com.dunn.instrument.tools.log.LogUtil;
import com.dunn.instrument.tools.thread.ThreadManager;

import java.util.List;

public class CheckInfoService extends Service {
    private static final String TAG = "CheckInfoService";
    private static final int MSG_MEMINFO = 0;
    private static final int MSG_VERSION = 1;
    public static final int MSG_CPUINFO = 2;
    public static final int MSG_PROPERTY_1 = 3;
    public static final int MSG_PROCESS_RUN = 4;
    public static final String KEY_CPURATE = "cpu_rate";
    private WindowRecordBean mBean;
    private TextView mProperty1;
    private TextView mProcessRun;
    private MemThread mMemThread;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case MSG_PROPERTY_1:
                    if (!TextUtils.isEmpty(msg.getData().getString("property")))
                        mProperty1.setText("third.get.keep_alive: "+msg.getData().getString("property"));
                    break;
                case MSG_PROCESS_RUN:
                    mProcessRun.setText("com.tianci.de is run: "+msg.getData().getBoolean("isrun"));
                    break;
            }
        }
    };

    public CheckInfoService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate: ");
        showFloatWindow();
        startThread();
//        getVersion();
//        MemManager.getInstance().init(CheckInfoService.this.getApplicationContext());
        initReceivers();

        //test
        SystemProperties.set("third.get.keep_alive","0");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //todo 直接命令行启动，这里设置监控包名会不成功，连接还没有开始回调
        startForegroundService(startId);
        handleInnerEvent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleInnerEvent(Intent intent) {
        if (intent == null) return;
//        String cpuRate = intent.getStringExtra(KEY_CPURATE);
//        if (cpuRate == null) {
//            return;
//        }
//        Message message = mHandler.obtainMessage();
//        message.what = MSG_CPUINFO;
//        Bundle bundle = new Bundle();
//        bundle.putString("cpuRate", cpuRate);
//        message.setData(bundle);
//        mHandler.sendMessage(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy: ");
        stopThread();
        hideFloatWindow();
        destoryReceivers();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void showFloatWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.float_window_check_info, null);
        mProperty1 = view.findViewById(R.id.property1);
        mProcessRun = view.findViewById(R.id.process_run);
        // appMemUsageTextView.setTextColor(getResources().getColor(R.color.black));
        mBean = FloatWindowManager.getInstance().createAndShowFloatWindow("check-info");
        if (mBean != null && mBean.getContentView() != null) {
            RelativeLayout mWindowContent = mBean.getContentView();
            if (mWindowContent != null) {
                mWindowContent.addView(view);
            }
        }
    }

    private void hideFloatWindow() {
        FloatWindowManager.getInstance().removeFloatWindow(mBean);
    }

    private void startForegroundService(int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "DEVICE_INFO";
            String CHANNEL_NAME = "DEVICE_INFO";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

            Intent intent = new Intent();
            intent.setAction("notification.receiver.action.deviceinfo");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Notification notification = new Notification.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.ic_launcher_background).setContentIntent(pendingIntent).build();
            startForeground(startId, notification);
        }
    }

    private void startThread() {
        stopThread();
        mMemThread = new MemThread();
        mMemThread.start();
    }

    private void stopThread() {
        if (mMemThread != null) {
            mMemThread.exit();
            mMemThread = null;
        }
    }

    private class MemThread extends Thread {
        private boolean isStart = false;

        @Override
        public synchronized void start() {
            super.start();
            isStart = true;
        }

        public synchronized void exit() {
            isStart = false;
        }

        @Override
        public void run() {
            while (isStart) {
                try {
                    Thread.sleep(1000);
//                    getMeminfo();
                    getProperty();
                    getDeIsRun();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getProperty(){
        String property1 = SystemProperties.get("third.get.keep_alive", "");
        LogUtil.i(TAG, "property1: "+property1);
        Message message = mHandler.obtainMessage();
        message.what = MSG_PROPERTY_1;
        Bundle bundle = new Bundle();
        bundle.putString("property", property1);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    private void getDeIsRun(){
        boolean isRun = isApplicationRunning("com.tianci.de");
        LogUtil.i(TAG, "com.tianci.de isRun: "+isRun);
        Message message = mHandler.obtainMessage();
        message.what = MSG_PROCESS_RUN;
        Bundle bundle = new Bundle();
        bundle.putBoolean("isrun", isRun);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    private void getMeminfo() {
        int memoryUnit = 1024;
        MemTools.MemInfo memInfo = MemTools.getSystemMemInfo();
        long totalMem = memInfo.memTotal / memoryUnit;
        long availMem = memInfo.memAvailable / memoryUnit;
        long freeMem = memInfo.memFree / memoryUnit;
        long buffers = memInfo.buffers / memoryUnit;
        long cachedMem = memInfo.cached / memoryUnit;
        long swapTotal = memInfo.swapTotal / memoryUnit;
        long swapFree = memInfo.swapFree / memoryUnit;
        if (availMem == 0) {
            ActivityManager.MemoryInfo info = MemManager.getInstance().getMemoryInfo();
            availMem = info.availMem / memoryUnit / memoryUnit;
        }

        Message message = mHandler.obtainMessage();
        message.what = MSG_MEMINFO;
        Bundle bundle = new Bundle();
        bundle.putString("totalMem", totalMem + " MB");
        bundle.putString("freeMem", freeMem + " MB");
        bundle.putString("availMem", availMem + " MB");
        bundle.putString("swapTotal", swapTotal + " MB");
        bundle.putString("swapFree", swapFree + " MB");
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    private void getVersion() {
        ThreadManager.getInstance().ioThread(new Runnable() {
            @Override
            public void run() {
                String version = SystemUtil.getSystemVersions();
                Message message = mHandler.obtainMessage();
                message.what = MSG_VERSION;
                Bundle bundle = new Bundle();
                bundle.putString("version", version);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }
        });
    }

    private boolean isApplicationRunning(String packageName) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : appProcesses) {
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(processInfo.processName, PackageManager.GET_ACTIVITIES);
                if (packageInfo.packageName.equals(packageName)) {
                    isRunning = true;
                    break;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return isRunning;
    }

    private void initReceivers() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        CheckInfoService.this.registerReceiver(mPackageReceiver, intentFilter);
    }

    private void destoryReceivers(){
        CheckInfoService.this.unregisterReceiver(mPackageReceiver);
    }

    private final BroadcastReceiver mPackageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            LogUtil.i(TAG, "onReceive: " + action);
            String data = intent.getDataString();
            if (TextUtils.isEmpty(data)) {
                return;
            }
            String packageName = data.split(":")[1];
            LogUtil.i(TAG, "onReceive: packageName: " + packageName);
            if (TextUtils.isEmpty(packageName)) {
                return;
            }
            switch (action) {
                case Intent.ACTION_PACKAGE_ADDED:
                    LogUtil.i(TAG, "onReceive: ACTION_PACKAGE_ADDED packageName: " + packageName);
                    if("com.tianci.de".equals(packageName)){
                        SystemProperties.set("third.get.keep_alive","0");
                    }
                    break;
                case Intent.ACTION_PACKAGE_REMOVED:
                    LogUtil.i(TAG, "onReceive: ACTION_PACKAGE_REMOVED packageName: " + packageName);
                    break;
                case Intent.ACTION_PACKAGE_REPLACED:
                    LogUtil.i(TAG, "onReceive: ACTION_PACKAGE_REPLACED packageName: " + packageName);
                    break;
            }
        }
    };

}