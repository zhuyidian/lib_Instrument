package com.dunn.instrument.service;

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

import com.dunn.instrument.R;
import com.dunn.instrument.tools.log.LogUtil;

public class CmdService extends Service{
    static final String TAG = "CmdService";
    public static boolean hasStarted = false;

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
        String cmd = intent.getStringExtra("cmd");
        String from = intent.getStringExtra("from");
        LogUtil.d(TAG, "handleInnerEvent cmd = " + cmd + ",from=" + from);
        if (cmd == null) {
            LogUtil.d(TAG, "handleInnerEvent cmd return!!!!!!");
            return;
        }

        if("start_deviceinfo".equals(cmd)) {
            Intent intentW = new Intent(CmdService.this, DeviceInfoService.class);
            startService(intentW);
        }else if("stop_deviceinfo".equals(cmd)){
            Intent intentW = new Intent(CmdService.this, DeviceInfoService.class);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
