package com.dunn.instrument.service;

import android.app.Service;
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
import com.dunn.instrument.floatwindow.FloatWindowManager;
import com.dunn.instrument.floatwindow.WindowRecordBean;
import com.dunn.instrument.tools.log.LogUtil;

public class InfoTest2Service extends Service {
    private static final String TAG = "InfoTest2Service";
    private WindowRecordBean mBean;
    private TextView cpuUsageTextView;
    private TextView appCpuUsageTextView;
    private TextView memUsageTextView;
    private TextView appMemUsageTextView;
    private TextView appFpsTextView;
    private UITestThread mThread;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what == 0) {
                if (!TextUtils.isEmpty(msg.getData().getString("text1")))
                    cpuUsageTextView.setText(msg.getData().getString("text1"));

                if (!TextUtils.isEmpty(msg.getData().getString("text2")))
                    memUsageTextView.setText(msg.getData().getString("text2"));

                if (!TextUtils.isEmpty(msg.getData().getString("text3")))
                    appCpuUsageTextView.setText(msg.getData().getString("text3"));

                if (!TextUtils.isEmpty(msg.getData().getString("text4")))
                    appMemUsageTextView.setText(msg.getData().getString("text4"));

                if (!TextUtils.isEmpty(msg.getData().getString("text5")))
                    appFpsTextView.setText(msg.getData().getString("text5"));
            }
        }
    };

    public InfoTest2Service() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate: ");
        showFloatWindow();
        startThread();
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
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void showFloatWindow() {
        View view1 = LayoutInflater.from(this).inflate(R.layout.float_window, null);
        cpuUsageTextView = view1.findViewById(R.id.cpu_usage);
        //cpuUsageTextView.setTextColor(getResources().getColor(R.color.black));
        appCpuUsageTextView = view1.findViewById(R.id.app_cpu_usage);
        // appCpuUsageTextView.setTextColor(getResources().getColor(R.color.black));

        memUsageTextView = view1.findViewById(R.id.mem_usage);
        // memUsageTextView.setTextColor(getResources().getColor(R.color.black));
        appMemUsageTextView = view1.findViewById(R.id.app_mem_usage);
        appFpsTextView = view1.findViewById(R.id.app_fps);
        // appMemUsageTextView.setTextColor(getResources().getColor(R.color.black));
        mBean = FloatWindowManager.getInstance().createAndShowFloatWindow();
        if(mBean!=null && mBean.getContentView()!=null){
            RelativeLayout mWindowContent = mBean.getContentView();
            if(mWindowContent!=null){
                mWindowContent.addView(view1);
            }
        }
    }

    private void hideFloatWindow() {
        FloatWindowManager.getInstance().removeFloatWindow(mBean);
    }

    private void startThread() {
        stopThread();
        mThread = new UITestThread();
        mThread.start();
    }

    private void stopThread() {
        if (mThread != null) {
            mThread.exit();
            mThread = null;
        }
    }

    private class UITestThread extends Thread {
        private boolean isStart = false;
        private int count = 0;

        @Override
        public synchronized void start() {
            super.start();
            isStart = true;
            count = 0;
        }

        public synchronized void exit() {
            isStart = false;
        }

        @Override
        public void run() {
            while (isStart) {
                try {
                    count++;
                    if (count > 1000) count = 0;
                    Thread.sleep(2000);
                    sendMsg(count + "");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendMsg(String msg) {
        Message message = mHandler.obtainMessage();
        message.what = 0;
        Bundle bundle = new Bundle();
        bundle.putString("text1", msg);
        bundle.putString("text2", msg);
        bundle.putString("text3", msg);
        bundle.putString("text4", msg);
        bundle.putString("text5", msg);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }
}