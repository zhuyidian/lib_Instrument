package com.dunn.instrument.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dunn.instrument.R;
import com.dunn.instrument.floatwindow.FloatWindowManager;
import com.dunn.instrument.floatwindow.WindowRecordBean;
import com.dunn.instrument.tools.framework.cpu.CpuManager;
import com.dunn.instrument.tools.framework.fps.FpsInfo;
import com.dunn.instrument.tools.framework.fps.GetFpsUtils;
import com.dunn.instrument.tools.framework.ram.MemManager;
import com.dunn.instrument.tools.framework.ram.MemTools;
import com.dunn.instrument.tools.log.LogUtil;
import com.dunn.instrument.tools.thread.ThreadManager;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProcessService extends Service {
    private static final String TAG = "ProcessService";
    private static final int MSG_CURRENT_PKG = 0;
    private static final int MSG_FPS = 1;
    private static final int MSG_CPU = 2;
    private static final int MSG_MEM = 3;
    private WindowRecordBean mBean;
    private TextView mPkg;
    private TextView mCls;
    private TextView mFps;
    private TextView mProcCpuRate;
    private TextView mProcNum;
    private TextView mThreadNum;
    private TextView mProcTotalPss;
    private ProcessThread mProcessThread;
    private long cnt = 0;  //时间计数器
    private ActivityManager mActivityManager;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case MSG_CURRENT_PKG:
                    if (!TextUtils.isEmpty(msg.getData().getString("pkg")))
                        mPkg.setText("pkg:"+msg.getData().getString("pkg"));

                    if (!TextUtils.isEmpty(msg.getData().getString("cls")))
                        mCls.setText("cls:"+msg.getData().getString("cls"));
                    break;
                case MSG_FPS:
                    if (!TextUtils.isEmpty(msg.getData().getString("fps")))
                        mFps.setText("fps:"+msg.getData().getString("fps"));
                    break;
                case MSG_CPU:
                    if (!TextUtils.isEmpty(msg.getData().getString("procCpuRate"))) {
                        String procCpuRate = msg.getData().getString("procCpuRate");
                        LogUtil.i(TAG, "MSG_CPU procCpuRate:" + procCpuRate);
                        mProcCpuRate.setText("procCpuRate:" + procCpuRate);
                    }
                    if (!TextUtils.isEmpty(msg.getData().getString("procNum")))
                        mProcNum.setText("procNum:"+msg.getData().getString("procNum"));
                    if (!TextUtils.isEmpty(msg.getData().getString("threadNum")))
                        mThreadNum.setText("threadNum:"+msg.getData().getString("threadNum"));
                    break;
                case MSG_MEM:
                    if (!TextUtils.isEmpty(msg.getData().getString("procTotalPss")))
                        mProcTotalPss.setText("procTotalPss:"+msg.getData().getString("procTotalPss"));
                    break;
            }
        }
    };

    public ProcessService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate: ");
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        showFloatWindow();
        startThread();
        CpuManager.getInstance().init(ProcessService.this.getApplicationContext());
        MemManager.getInstance().init(ProcessService.this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //todo 直接命令行启动，这里设置监控包名会不成功，连接还没有开始回调
        LogUtil.i(TAG, "onStartCommand: intent=" + intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy: ");
        mActivityManager = null;
        stopThread();
        hideFloatWindow();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void showFloatWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.float_window_process, null);
        mPkg = view.findViewById(R.id.pkg);
        mCls = view.findViewById(R.id.cls);
        mFps = view.findViewById(R.id.fps);
        mProcCpuRate = view.findViewById(R.id.procCpuRate);
        mProcNum = view.findViewById(R.id.procNum);
        mThreadNum = view.findViewById(R.id.threadNum);
        mProcTotalPss = view.findViewById(R.id.procTotalPss);
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
                String pkg = getCurrentPkg();
                LogUtil.i(TAG,"ProcessThread: pkg="+pkg);
                if (pkg != null) {
                    getFps(pkg);
                    getCpuInfo(pkg);
                    //每5s获取一次内存
                    if (cnt % 5 == 0) {
                        ThreadManager.getInstance().ioThread(new MemRunnable(pkg));
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

    private String getCurrentPkg() {
        String pkg = SystemProperties.get("sky.current.apk", "");
        String cls = SystemProperties.get("sky.current.actname", "");
        if (TextUtils.isEmpty(pkg)) {
            ComponentName cn = mActivityManager.getRunningTasks(1).get(0).topActivity;
            pkg = cn.getPackageName();
            cls = cn.getClassName();
        }

        Message message = mHandler.obtainMessage();
        message.what = MSG_CURRENT_PKG;
        Bundle bundle = new Bundle();
        bundle.putString("pkg", pkg);
        bundle.putString("cls", cls);
        message.setData(bundle);
        mHandler.sendMessage(message);

        return pkg;
    }

    private void getFps(String pkgName) {
        StringBuilder text = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        double[] info = GetFpsUtils.getInfo(pkgName);
        double fps1 = info[0];
        double ss = info[2];
        double jumpingFrames = info[1];
        double totalFrame = info[3];
        long maxFrameTime = (long) info[4];
        int stuckSum2 = GetFpsUtils.getStuckSum2();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            text.append("").append(fps1)
                    .append(" 总帧数：").append(totalFrame)   //间隔内获取到的所有帧数
                    .append(" 最大帧时长：").append(maxFrameTime)  //最大桢时长
                    .append(" 掉帧数：").append(jumpingFrames)  //大于16.67的帧数
                    .append(" 卡顿次数：").append(stuckSum2)
                    .append(" 流畅分：").append(decimalFormat.format(ss));
        } else {
            float fps = FpsInfo.fps();
            text.append("").append(fps);
        }
//        LogUtil.i(TAG,"getFps: pkgName="+pkgName+", fps="+String.valueOf(text));
        Message message = mHandler.obtainMessage();
        message.what = MSG_FPS;
        Bundle bundle = new Bundle();
        bundle.putString("fps", String.valueOf(text));
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    private void getCpuInfo(String pkgName){
        CpuManager.CpuInfo mCpuInfo = CpuManager.getInstance().getCpuInfo(pkgName);
        sendMsgToDeviceInfo(ProcessService.this,mCpuInfo.cpuRate);
        Message message = mHandler.obtainMessage();
        message.what = MSG_CPU;
        Bundle bundle = new Bundle();
        bundle.putString("procCpuRate", mCpuInfo.procCpuRate);
        bundle.putString("procNum", mCpuInfo.procProcessNum);
        bundle.putString("threadNum", mCpuInfo.procThreadNum);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    private class MemRunnable implements Runnable{
        String mPkg;

        public MemRunnable(String pkg) {
            mPkg = pkg;
        }

        @Override
        public void run() {
            MemManager.ProcMemInfo mProcMemInfo = MemManager.getInstance().getProcMemInfo(mPkg);
            Message message = mHandler.obtainMessage();
            message.what = MSG_MEM;
            Bundle bundle = new Bundle();
            bundle.putString("procTotalPss", mProcMemInfo.totalPss + " MB");
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    }

    private void sendMsgToDeviceInfo(Context context, String cpuRate){
        Intent intent = new Intent(context, DeviceInfoService.class);
        intent.putExtra("CPURATE", cpuRate);
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