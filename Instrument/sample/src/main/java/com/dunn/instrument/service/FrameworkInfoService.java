package com.dunn.instrument.service;

import static com.dunn.instrument.service.DeviceInfoService.KEY_CPURATE;

import android.app.ActivityManager;
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
import android.skyworth.skymonitor.ResourceMsg;
import android.skyworth.skymonitor.SkyMonitorHelper;
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
import com.dunn.instrument.function.keepalive.InterfaceKeepaliveSystem;
import com.dunn.instrument.function.keepalive.KeepAliveActivity;
import com.dunn.instrument.monitor.PerfMonitor;
import com.dunn.instrument.tools.framework.cpu.CpuManager;
import com.dunn.instrument.tools.framework.ram.MemManager;
import com.dunn.instrument.tools.framework.ram.MemTools;
import com.dunn.instrument.tools.log.LogUtil;

public class FrameworkInfoService extends Service {
    private static final String TAG = "FrameworkInfoService";
    private PerfMonitor mMonitor;
    private ProcessThread mProcessThread;
    private String mPackageName;
    private InterfaceKeepaliveSystem mInterfaceKeepaliveSystem;
    private long cnt = 0;  //时间计数器
    private int msgCount = 0;
    private static final int WHAT_MSG = 0;
    private static final int WHAT_CPU = 1;
    private static final int WHAT_MEM = 2;
    private WindowRecordBean mBean;
    private TextView mMsg;
    private TextView mTotalMem,mAvailMem,mFreeMem,mProcPss;
    private TextView mCpuRate,mProcCpuRate,mProcNum,mThreadNum;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case WHAT_MSG:
                    msgCount++;
                    if (!TextUtils.isEmpty(msg.getData().getString("content")))
                        mMsg.setText("msg:" + msg.getData().getString("content") + ", 消息次数:"+msgCount);
                    break;
                case WHAT_CPU:
                    if (!TextUtils.isEmpty(msg.getData().getString("cpuRate")))
                        mCpuRate.setText("cpuRate:" + msg.getData().getString("cpuRate"));
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
                case WHAT_MEM:
                    if (!TextUtils.isEmpty(msg.getData().getString("totalMem")))
                        mTotalMem.setText("totalMem:"+msg.getData().getString("totalMem"));

                    if (!TextUtils.isEmpty(msg.getData().getString("availMem")))
                        mAvailMem.setText("availMem:"+msg.getData().getString("availMem"));

                    if (!TextUtils.isEmpty(msg.getData().getString("freeMem")))
                        mFreeMem.setText("freeMem:"+msg.getData().getString("freeMem"));

                    if (!TextUtils.isEmpty(msg.getData().getString("procPss")))
                        mProcPss.setText("procPss:" + msg.getData().getString("procPss"));
                    break;
            }
        }
    };

    public FrameworkInfoService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate: ");
        mInterfaceKeepaliveSystem = new InterfaceKeepaliveSystem(FrameworkInfoService.this);
        mMonitor = new PerfMonitor(FrameworkInfoService.this.getApplicationContext(), new PerfListenter());
        mPackageName = FrameworkInfoService.this.getPackageName();
        if(mInterfaceKeepaliveSystem!=null) {
            mInterfaceKeepaliveSystem.registerCallback(new MsgCallback());
        }
        showFloatWindow();
        startThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //todo 直接命令行启动，这里设置监控包名会不成功，连接还没有开始回调
        LogUtil.i(TAG, "onStartCommand: intent=" + intent);
        startForegroundService(startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy: ");
        hideFloatWindow();
        if (mMonitor != null) {
            mMonitor.destory();
            mMonitor = null;
        }
        if(mInterfaceKeepaliveSystem!=null){
            mInterfaceKeepaliveSystem.unRegisterCallback();
        }
        stopThread();
    }

    class PerfListenter implements PerfMonitor.PerfMonitorListener {
        @Override
        public void onCpuInfo(CpuManager.CpuInfo mCpuInfo) {
            if (mCpuInfo == null) return;
            Message message = mHandler.obtainMessage();
            message.what = WHAT_CPU;
            Bundle bundle = new Bundle();
            bundle.putString("cpuRate", mCpuInfo.cpuRate);
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
            message.what = WHAT_MEM;
            Bundle bundle = new Bundle();
            bundle.putString("procPss", memInfo);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }

        @Override
        public void onCurrentPkgInfo(PkgClsBean bean) {
        }
    }

    public class MsgCallback implements Callback{
        @Override
        public void onMsg(ResourceMsg msg) {
            String content = "Msg:";
            if(msg!=null){
                Bundle bundle = msg.peekData();
                content = bundle.getString("content");
            }
            Message message = mHandler.obtainMessage();
            message.what = WHAT_MSG;
            Bundle bundle = new Bundle();
            bundle.putString("content",content);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
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
                        getMeminfo();
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
        message.what = WHAT_MEM;
        Bundle bundle = new Bundle();
        bundle.putString("totalMem", totalMem + " MB");
        bundle.putString("freeMem", freeMem + " MB");
        bundle.putString("availMem", availMem + " MB");
        bundle.putString("swapTotal", swapTotal + " MB");
        bundle.putString("swapFree", swapFree + " MB");
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    private interface Callback{
        public void onMsg(ResourceMsg msg);
    }

    private void startForegroundService(int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "FRAMEWORK";
            String CHANNEL_NAME = "FRAMEWORK";
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

    private void showFloatWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.float_window_framework_info, null);
        mTotalMem = view.findViewById(R.id.totalMem);
        mAvailMem = view.findViewById(R.id.availMem);
        mFreeMem = view.findViewById(R.id.freeMem);
        mProcPss = view.findViewById(R.id.procPss);
        mCpuRate = view.findViewById(R.id.cpuRate);
        mProcCpuRate = view.findViewById(R.id.procCpuRate);
        mProcNum = view.findViewById(R.id.procNum);
        mThreadNum = view.findViewById(R.id.threadNum);
        mMsg = view.findViewById(R.id.msg);
        mBean = FloatWindowManager.getInstance().createAndShowFloatWindow("framework-msg");
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
}