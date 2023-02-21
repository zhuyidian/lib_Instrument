package com.dunn.instrument.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
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

public class DeviceInfoService extends Service {
    private static final String TAG = "DeviceInfoService";
    private static final int MSG_MEMINFO = 0;
    private static final int MSG_VERSION = 1;
    public static final int MSG_CPUINFO = 2;
    public static final String KEY_CPURATE = "cpu_rate";
    private WindowRecordBean mBean;
    private TextView mVersion;
    private TextView mTotalMem;
    private TextView mAvailMem;
    private TextView mFreeMem;
    private TextView mSwapTotal;
    private TextView mSwapFree;
    private TextView mCpuRate;
    private MemThread mMemThread;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case MSG_MEMINFO:
                    if (!TextUtils.isEmpty(msg.getData().getString("totalMem")))
                        mTotalMem.setText("totalMem:"+msg.getData().getString("totalMem"));

                    if (!TextUtils.isEmpty(msg.getData().getString("availMem")))
                        mAvailMem.setText("availMem:"+msg.getData().getString("availMem"));

                    if (!TextUtils.isEmpty(msg.getData().getString("freeMem")))
                        mFreeMem.setText("freeMem:"+msg.getData().getString("freeMem"));

                    if (!TextUtils.isEmpty(msg.getData().getString("swapTotal")))
                        mSwapTotal.setText("swapTotal:"+msg.getData().getString("swapTotal"));

                    if (!TextUtils.isEmpty(msg.getData().getString("swapFree")))
                        mSwapFree.setText("swapFree:"+msg.getData().getString("swapFree"));
                    break;
                case MSG_VERSION:
                    if (!TextUtils.isEmpty(msg.getData().getString("version")))
                        mVersion.setText("version:"+msg.getData().getString("version"));
                    break;
                case MSG_CPUINFO:
                    if (!TextUtils.isEmpty(msg.getData().getString("cpuRate")))
                        mCpuRate.setText("cpu usage:"+msg.getData().getString("cpuRate"));
                    break;
            }
        }
    };

    public DeviceInfoService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate: ");
        showFloatWindow();
        startThread();
        getVersion();
        MemManager.getInstance().init(DeviceInfoService.this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //todo 直接命令行启动，这里设置监控包名会不成功，连接还没有开始回调
        //startForegroundService(startId);
        handleInnerEvent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleInnerEvent(Intent intent) {
        if (intent == null) return;
        String cpuRate = intent.getStringExtra(KEY_CPURATE);
        if (cpuRate == null) {
            return;
        }
        Message message = mHandler.obtainMessage();
        message.what = MSG_CPUINFO;
        Bundle bundle = new Bundle();
        bundle.putString("cpuRate", cpuRate);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy: ");
        stopThread();
        hideFloatWindow();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void showFloatWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.float_window_device_info, null);
        mVersion = view.findViewById(R.id.version);
        mTotalMem = view.findViewById(R.id.totalMem);
        //cpuUsageTextView.setTextColor(getResources().getColor(R.color.black));
        mAvailMem = view.findViewById(R.id.availMem);
        // appCpuUsageTextView.setTextColor(getResources().getColor(R.color.black));
        mFreeMem = view.findViewById(R.id.freeMem);
        // memUsageTextView.setTextColor(getResources().getColor(R.color.black));
        mSwapTotal = view.findViewById(R.id.swapTotal);
        mSwapFree = view.findViewById(R.id.swapFree);
        mCpuRate = view.findViewById(R.id.cpuRate);
        // appMemUsageTextView.setTextColor(getResources().getColor(R.color.black));
        mBean = FloatWindowManager.getInstance().createAndShowFloatWindow();
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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
                    Thread.sleep(5000);
                    getMeminfo();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
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

}