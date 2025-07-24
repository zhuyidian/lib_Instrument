package com.dunn.demo.service;

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
import android.util.Log;

import com.dunn.demo.R;


public class CmdService extends Service{
    static final String TAG = "CmdService";
    public static boolean hasStarted = false;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.i(TAG, " attachBaseContext");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, " onCreate");

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
        Log.d(TAG, "handleInnerEvent intent=" + intent);
        if (intent == null) return;
        String cmd = intent.getStringExtra("cmd");
        String from = intent.getStringExtra("from");
        Log.d(TAG, "handleInnerEvent cmd = " + cmd + ",from=" + from);
        if (cmd == null) {
            Log.d(TAG, "handleInnerEvent cmd return!!!!!!");
            return;
        }

        if("start_deviceinfo".equals(cmd)) {
            Intent intentW = new Intent(CmdService.this, DeviceInfoService.class);
            startService(intentW);
        }else if("stop_deviceinfo".equals(cmd)){
            Intent intentW = new Intent(CmdService.this, DeviceInfoService.class);
            stopService(intentW);
        }else if("start_frameworks".equals(cmd)){
            Intent intentW = new Intent(CmdService.this, FrameworksService.class);
            startService(intentW);
        }else if("stop_frameworks".equals(cmd)){
            Intent intentW = new Intent(CmdService.this, FrameworksService.class);
            stopService(intentW);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, " onLowMemory!!!");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d(TAG, " onTrimMemory level=" + level);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, " onDestroy!!!");
        hasStarted = false;
    }

    private void startForegroundService(int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "CMD";
            String CHANNEL_NAME = "CMD";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

            Intent intent = new Intent();
            intent.setAction("notification.receiver.action.cmd");
            //PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new Notification.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.icon_contorl).setContentIntent(pendingIntent).build();
            startForeground(startId, notification);
            Log.d(TAG, "startForegroundService");
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
