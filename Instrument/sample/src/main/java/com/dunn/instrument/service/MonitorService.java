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
import android.skyworth.skymonitor.SkyMonitorHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dunn.instrument.R;
import com.dunn.instrument.floatwindow.FloatWindowManager;
import com.dunn.instrument.floatwindow.WindowRecordBean;
import com.dunn.instrument.function.keepalive.InterfaceKeepaliveSystem;
import com.dunn.instrument.tools.log.LogUtil;

import java.util.List;

public class MonitorService extends Service {
    private static final String TAG = "MonitorService";
    private InterfaceKeepaliveSystem mInterfaceKeepaliveSystem;
    private static final int MSG_SYS_ALL = 0;
    private static final int MSG_SYS_CPU = 1;
    public static final int MSG_SYS_MEM = 2;
    public static final int MSG_SYS_IO = 3;
    private WindowRecordBean mBean;
    private TextView mSysCpuIdle, mSysMemAvailable, mSysIoUsage, mSysAll;
    private MemThread mMemThread;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case MSG_SYS_ALL:
                    double cpu = msg.getData().getDouble("cpuIdle", -1);
                    double mem = msg.getData().getDouble("memAvailable", -1);
                    double io = msg.getData().getDouble("ioUsage", -1);
                    mSysAll.setText("cpu: "+cpu+" mem: "+mem+" io: "+io);
                    break;
                case MSG_SYS_CPU:
                    mSysCpuIdle.setText("cpu idle: "+msg.getData().getFloat("cpuIdle", -1));
                    break;
                case MSG_SYS_MEM:
                    mSysMemAvailable.setText("mem available: "+msg.getData().getFloat("memAvailable", -1));
                    break;
                case MSG_SYS_IO:
                    mSysIoUsage.setText("io usage: "+msg.getData().getFloat("ioUsage", -1));
                    break;
            }
        }
    };

    public MonitorService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate: ");
        mInterfaceKeepaliveSystem = new InterfaceKeepaliveSystem(this);
        mInterfaceKeepaliveSystem.registerCallback(new ResourceCallback() {
            @Override
            public void onSysCpu(float cpuIdle) {
                LogUtil.i(TAG, "onCreate: onSysCpu cpuIdle="+cpuIdle);
                Message message = mHandler.obtainMessage();
                message.what = MSG_SYS_CPU;
                Bundle bundle = new Bundle();
                bundle.putFloat("cpuIdle", cpuIdle);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            @Override
            public void onSysMem(float memAvailable) {
                LogUtil.i(TAG, "onCreate: onSysMem memAvailable="+memAvailable);
                Message message = mHandler.obtainMessage();
                message.what = MSG_SYS_MEM;
                Bundle bundle = new Bundle();
                bundle.putFloat("memAvailable", memAvailable);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            @Override
            public void onSysIo(float ioUsage) {
                LogUtil.i(TAG, "onCreate: onSysIo ioUsage="+ioUsage);
                Message message = mHandler.obtainMessage();
                message.what = MSG_SYS_IO;
                Bundle bundle = new Bundle();
                bundle.putFloat("ioUsage", ioUsage);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            @Override
            public void onSysAll(double cpuIdle, double memAvailable, double ioUsage) {
                LogUtil.i(TAG, "onCreate: onSysAll cpuIdle="+cpuIdle+", memAvailable="+memAvailable+", ioUsage="+ioUsage);
                Message message = mHandler.obtainMessage();
                message.what = MSG_SYS_ALL;
                Bundle bundle = new Bundle();
                bundle.putDouble("cpuIdle", cpuIdle);
                bundle.putDouble("memAvailable", memAvailable);
                bundle.putDouble("ioUsage", ioUsage);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }
        }, this.getPackageName(), 0);
        showFloatWindow();
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy: ");
        mInterfaceKeepaliveSystem.unRegisterCallback(this.getPackageName());
        hideFloatWindow();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void showFloatWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.float_window_monitor, null);
        mSysCpuIdle = view.findViewById(R.id.sys_cpu_idle);
        mSysMemAvailable = view.findViewById(R.id.sys_mem_available);
        mSysIoUsage = view.findViewById(R.id.sys_io_usage);
        mSysAll = view.findViewById(R.id.sys_all);
        mBean = FloatWindowManager.getInstance().createAndShowFloatWindow("monitor");
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
            String CHANNEL_ID = "MONITOR";
            String CHANNEL_NAME = "MONITOR";
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface ResourceCallback{
        public void onSysCpu(float cpuIdle);
        public void onSysMem(float memAvailable);
        public void onSysIo(float ioUsage);
        public void onSysAll(double cpuIdle, double memAvailable, double ioUsage);
    }

}