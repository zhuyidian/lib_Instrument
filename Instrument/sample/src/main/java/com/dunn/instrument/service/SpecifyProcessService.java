package com.dunn.instrument.service;

import static com.dunn.instrument.service.DeviceInfoService.KEY_CPURATE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
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
import com.dunn.instrument.bean.PkgClsBean;
import com.dunn.instrument.floatwindow.FloatWindowManager;
import com.dunn.instrument.floatwindow.WindowRecordBean;
import com.dunn.instrument.monitor.PerfMonitor;
import com.dunn.instrument.tools.framework.cpu.CpuManager;
import com.dunn.instrument.tools.framework.ram.MemManager;
import com.dunn.instrument.tools.log.LogUtil;

public class SpecifyProcessService extends Service {
    private static final String TAG = "SpecifyProcessService";
    private static final int MSG_PKG = 0;
    private static final int MSG_CPU = 2;
    private static final int MSG_MEM = 3;
    public static final String KEY_PKG = "pkg";
    private WindowRecordBean mBean;
    private TextView mPkg;
    private TextView mProcCpuRate;
    private TextView mProcNum;
    private TextView mThreadNum;
    private TextView mProcTotalPss;
    private ProcessThread mProcessThread;
    private long cnt = 0;  //时间计数器
    private PerfMonitor mMonitor;
    private String mPackageName;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case MSG_PKG:
                    if (!TextUtils.isEmpty(msg.getData().getString("pkg")))
                        mPkg.setText("pkg:" + msg.getData().getString("pkg"));
                    break;
                case MSG_CPU:
                    if (!TextUtils.isEmpty(msg.getData().getString("procCpuRate"))) {
                        String procCpuRate = msg.getData().getString("procCpuRate");
                        LogUtil.i(TAG, "MSG_CPU procCpuRate:" + procCpuRate);
                        mProcCpuRate.setText("procCpuRate:" + procCpuRate);
                    }
                    if (!TextUtils.isEmpty(msg.getData().getString("procNum")))
                        mProcNum.setText("procNum:" + msg.getData().getString("procNum"));
                    if (!TextUtils.isEmpty(msg.getData().getString("threadNum")))
                        mThreadNum.setText("threadNum:" + msg.getData().getString("threadNum"));
                    break;
                case MSG_MEM:
                    if (!TextUtils.isEmpty(msg.getData().getString("procTotalPss")))
                        mProcTotalPss.setText("procTotalPss:" + msg.getData().getString("procTotalPss"));
                    break;
            }
        }
    };

    public SpecifyProcessService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate: ");
        mMonitor = new PerfMonitor(SpecifyProcessService.this.getApplicationContext(), new Listenter());
        showFloatWindow();
        startThread();
        CpuManager.getInstance().init(SpecifyProcessService.this.getApplicationContext());
        MemManager.getInstance().init(SpecifyProcessService.this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //todo 直接命令行启动，这里设置监控包名会不成功，连接还没有开始回调
        LogUtil.i(TAG, "onStartCommand: intent=" + intent);
        startForegroundService(startId);
        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        if (intent.hasExtra(KEY_PKG)) {
            mPackageName = intent.getStringExtra(KEY_PKG);
            if (mPackageName != null) {
                Message message = mHandler.obtainMessage();
                message.what = MSG_PKG;
                Bundle bundle = new Bundle();
                bundle.putString("pkg", mPackageName);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy: ");
        stopThread();
        hideFloatWindow();
        if (mMonitor != null) {
            mMonitor.destory();
            mMonitor = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class Listenter implements PerfMonitor.PerfMonitorListener {
        @Override
        public void onCpuInfo(CpuManager.CpuInfo mCpuInfo) {
            if (mCpuInfo == null) return;
            sendMsgToDeviceInfo(SpecifyProcessService.this, mCpuInfo.cpuRate);
            Message message = mHandler.obtainMessage();
            message.what = MSG_CPU;
            Bundle bundle = new Bundle();
            bundle.putString("procCpuRate", mCpuInfo.procCpuRate);
            bundle.putString("procNum", mCpuInfo.procProcessNum);
            bundle.putString("threadNum", mCpuInfo.procThreadNum);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }

        @Override
        public void onFps(String fps) {
        }

        @Override
        public void onMemInfo(String memInfo) {
            if (memInfo == null) return;
            Message message = mHandler.obtainMessage();
            message.what = MSG_MEM;
            Bundle bundle = new Bundle();
            bundle.putString("procTotalPss", memInfo);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }

        @Override
        public void onCurrentPkgInfo(PkgClsBean bean) {
        }
    }

    private void startForegroundService(int startId) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "SPECIFY-PROCESS";
            String CHANNEL_NAME = "SPECIFY-PROCESS";
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

    private void showFloatWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.float_window_specify_process, null);
        mPkg = view.findViewById(R.id.pkg);
        mProcCpuRate = view.findViewById(R.id.procCpuRate);
        mProcNum = view.findViewById(R.id.procNum);
        mThreadNum = view.findViewById(R.id.threadNum);
        mProcTotalPss = view.findViewById(R.id.procTotalPss);
        mBean = FloatWindowManager.getInstance().createAndShowFloatWindow("specify-process");
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

    private void startThread() {
        stopThread();
        mProcessThread = new ProcessThread();
        mProcessThread.start();
    }

    private void stopThread() {
        if (mProcessThread != null) {
            mProcessThread.exit();
            mProcessThread = null;
        }
    }

    private class ProcessThread extends Thread {
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
                long start = System.currentTimeMillis();
                if (mPackageName != null) {
                    LogUtil.i(TAG, "ProcessThread: mPackageName=" + mPackageName);
                    if (mMonitor != null) mMonitor.getCpuInfo(mPackageName);
                    //每5s获取一次内存
                    if (cnt % 5 == 0) {
                        if (mMonitor != null) mMonitor.getMemInfo(mPackageName);
                    }
                    cnt++;
                }
                try {
                    Thread.sleep(Math.max(1000 * 1 - (System.currentTimeMillis() - start), 0));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendMsgToDeviceInfo(Context context, String cpuRate) {
        Intent intent = new Intent(context, DeviceInfoService.class);
        intent.putExtra(KEY_CPURATE, cpuRate);
        try {
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(intent);
//            } else {
            context.startService(intent);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}