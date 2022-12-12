package com.dunn.instrument.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
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
import com.dunn.instrument.tools.log.LogUtil;
import com.dunn.instrument.tools.thread.ThreadManager;
import com.tianci.tv.utils.TVSDKDebug;

import swaiotos.sal.SalModule;
import swaiotos.sal.platform.IDeviceInfo;

public class DeviceInfoService extends Service {
    private static final String TAG = "DeviceInfoService";
    private WindowRecordBean mBean;
    private TextView mModel;
    private TextView mMachine;
    private TextView mChip;
    private TextView mAndroidVersion;
    private TextView mArm;
    private DataThread mDataThread;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what == 0) {
//                if (!TextUtils.isEmpty(msg.getData().getString("text1")))
//                    //cpuUsageTextView.setText(msg.getData().getString("text1"));
//
//                if (!TextUtils.isEmpty(msg.getData().getString("text2")))
//                    //memUsageTextView.setText(msg.getData().getString("text2"));
//
//                if (!TextUtils.isEmpty(msg.getData().getString("text3")))
//                    //appCpuUsageTextView.setText(msg.getData().getString("text3"));
//
//                if (!TextUtils.isEmpty(msg.getData().getString("text4")))
//                    //appMemUsageTextView.setText(msg.getData().getString("text4"));
//
//                if (!TextUtils.isEmpty(msg.getData().getString("text5")))
//                    //appFpsTextView.setText(msg.getData().getString("text5"));
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
        getDeviceInfo();
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
        View view = LayoutInflater.from(this).inflate(R.layout.float_window_device_info, null);
        mModel = view.findViewById(R.id.model);
        //cpuUsageTextView.setTextColor(getResources().getColor(R.color.black));
        mMachine = view.findViewById(R.id.machine);
        // appCpuUsageTextView.setTextColor(getResources().getColor(R.color.black));
        mChip = view.findViewById(R.id.chip);
        // memUsageTextView.setTextColor(getResources().getColor(R.color.black));
        mAndroidVersion = view.findViewById(R.id.android_version);
        mArm = view.findViewById(R.id.arm);
        // appMemUsageTextView.setTextColor(getResources().getColor(R.color.black));
        mBean = FloatWindowManager.getInstance().createAndShowFloatWindow();
        if(mBean!=null && mBean.getContentView()!=null){
            RelativeLayout mWindowContent = mBean.getContentView();
            if(mWindowContent!=null){
                mWindowContent.addView(view);
            }
        }
    }

    private void hideFloatWindow() {
        FloatWindowManager.getInstance().removeFloatWindow(mBean);
    }

    private void getDeviceInfo(){
        ThreadManager.getInstance().ioThread(new Runnable() {
            @Override
            public void run() {
                try {
                    IDeviceInfo mIDeviceInfo = swaiotos.sal.SAL.getModule(DeviceInfoService.this, SalModule.DEVICE_INFO);
                    String mac = mIDeviceInfo.getMac();
                    LogUtil.i(TAG, "getDeviceInfo: Mac=" + mac);
                    String model = mIDeviceInfo.getModel();
                    LogUtil.i(TAG, "getDeviceInfo: Model=" + model);
                    String chip = mIDeviceInfo.getChip();
                    LogUtil.i(TAG, "getDeviceInfo: Chip=" + chip);
                    String deviceId = mIDeviceInfo.getDeviceID();
                    LogUtil.i(TAG, "getDeviceInfo: DeviceID=" + deviceId);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "getDeviceInfo: e=" + e);
                }
            }
        });
    }

    private void startThread() {
        stopThread();
        mDataThread = new DataThread();
        mDataThread.start();
    }

    private void stopThread() {
        if (mDataThread != null) {
            mDataThread.exit();
            mDataThread = null;
        }
    }

    private static class DataThread extends Thread {
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
                    Thread.sleep(3000);
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