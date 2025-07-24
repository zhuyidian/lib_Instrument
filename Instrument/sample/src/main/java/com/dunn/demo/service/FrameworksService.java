package com.dunn.demo.service;

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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dunn.demo.R;
import com.dunn.demo.floatwindow.FloatWindowManager;
import com.dunn.demo.floatwindow.WindowRecordBean;

import upc.unicom.cloudcomputer.customed.CloudComputerAgent;
import upc.unicom.cloudcomputer.customed.InfoManager;

//import komect.aisoho.cloudcomputer.CloudComputerAgent;
//import komect.aisoho.cloudcomputer.SettingManager;

public class FrameworksService extends Service {
    private static final String TAG = "FrameworksService";
    private static final int MSG_KEY = 0;
    private static final int MSG_VERSION = 1;
    public static final int MSG_CPUINFO = 2;
    private WindowRecordBean mBean;
    private TextView mKey;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case MSG_KEY:
                    String cuei = msg.getData().getString("cuei");
                    String sn = msg.getData().getString("sn");
                    mKey.setText("info: cuei="+cuei+", sn="+sn);
                    break;
            }
        }
    };

    public FrameworksService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        showFloatWindow();
        initData();
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
        Log.i(TAG, "onDestroy: ");
        hideFloatWindow();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void showFloatWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.float_window_frameworks, null);
        mKey = view.findViewById(R.id.id_key);
        mBean = FloatWindowManager.getInstance().createAndShowFloatWindow("Frameworks");
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
            String CHANNEL_ID = "FRAMEWORKS";
            String CHANNEL_NAME = "FRAMEWORKS";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

            Intent intent = new Intent();
            intent.setAction("notification.receiver.action.frameworks");
            //PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new Notification.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.ic_launcher_background).setContentIntent(pendingIntent).build();
            startForeground(startId, notification);
        }
    }

    private void initData() {
        CloudComputerAgent agent = new CloudComputerAgent(FrameworksService.this);
        InfoManager infoManager = agent.getInfoManager();
        int batteryLevel = infoManager.getBatteryLevel();
        int batteryScale = infoManager.getBatteryScale();
        int batteryStatus = infoManager.getBatteryStatus();
        String cuei = infoManager.getCuei();
        String diskSize = infoManager.getDiskSize();
        String memorySize = infoManager.getMemorySize();
        String mac = infoManager.getMac();
        String sn = infoManager.getSn();
        String firmwareVersion = infoManager.getFirmwareVersion();
        String protocolVersion = infoManager.getProtocolVersion();
        Message message = mHandler.obtainMessage();
        message.what = MSG_KEY;
        Bundle bundle = new Bundle();
        bundle.putString("cuei", cuei);
        bundle.putString("sn", sn);
        message.setData(bundle);
        mHandler.sendMessage(message);

//        settingManager.setInputListener(new View.OnKeyListener(){
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                Log.i(TAG, "initData$onKey: view="+view+", i="+i+", keyEvent="+keyEvent);
//                int code = i;
//                int action = keyEvent.getAction();
//                Message message = mHandler.obtainMessage();
//                message.what = MSG_KEY;
//                Bundle bundle = new Bundle();
//                bundle.putInt("code", code);
//                bundle.putInt("action", action);
//                message.setData(bundle);
//                mHandler.sendMessage(message);
//                return false;
//            }
//        });
    }
}