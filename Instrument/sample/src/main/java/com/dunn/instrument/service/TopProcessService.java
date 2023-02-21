package com.dunn.instrument.service;

import static com.dunn.instrument.service.DeviceInfoService.KEY_CPURATE;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
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

public class TopProcessService extends Service {
    private static final String TAG = "TopProcessService";
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
    private PerfMonitor mMonitor;
    private ProcessThread mProcessThread;
    private long cnt = 0;  //时间计数器

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case MSG_CURRENT_PKG:
                    if (!TextUtils.isEmpty(msg.getData().getString("pkg")))
                        mPkg.setText("pkg:" + msg.getData().getString("pkg"));

                    if (!TextUtils.isEmpty(msg.getData().getString("cls")))
                        mCls.setText("cls:" + msg.getData().getString("cls"));
                    break;
                case MSG_FPS:
                    if (!TextUtils.isEmpty(msg.getData().getString("fps")))
                        mFps.setText("fps:" + msg.getData().getString("fps"));
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

    public TopProcessService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate: ");
        mMonitor = new PerfMonitor(TopProcessService.this.getApplicationContext(), new Listenter());
        showFloatWindow();
        startThread();
        CpuManager.getInstance().init(TopProcessService.this.getApplicationContext());
        MemManager.getInstance().init(TopProcessService.this.getApplicationContext());
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
            sendMsgToDeviceInfo(TopProcessService.this, mCpuInfo.cpuRate);
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
            if (fps == null) return;
            Message message = mHandler.obtainMessage();
            message.what = MSG_FPS;
            Bundle bundle = new Bundle();
            bundle.putString("fps", fps);
            message.setData(bundle);
            mHandler.sendMessage(message);
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
            if (bean == null) return;
            Message message = mHandler.obtainMessage();
            message.what = MSG_CURRENT_PKG;
            Bundle bundle = new Bundle();
            bundle.putString("pkg", bean.mPackageName);
            bundle.putString("cls", bean.mClassName);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    }

    private void showFloatWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.float_window_top_process, null);
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
                String pkg = null;
                if (mMonitor != null) pkg = mMonitor.getCurrentPkg();
                LogUtil.i(TAG, "ProcessThread: pkg=" + pkg);
                if (pkg != null) {
                    if (mMonitor != null) mMonitor.getFps(pkg);
                    if (mMonitor != null) mMonitor.getCpuInfo(pkg);
                    //每5s获取一次内存
                    if (cnt % 5 == 0) {
                        if (mMonitor != null) mMonitor.getMemInfo(pkg);
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