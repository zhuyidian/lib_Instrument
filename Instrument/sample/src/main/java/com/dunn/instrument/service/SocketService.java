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
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dunn.instrument.R;
import com.dunn.instrument.floatwindow.FloatWindowManager;
import com.dunn.instrument.floatwindow.WindowRecordBean;
import com.dunn.instrument.function.execmd.CmdBean;
import com.dunn.instrument.function.execmd.ExeCmdActivity;
import com.dunn.instrument.shell.RunCmdImpl;
import com.dunn.instrument.tools.log.LogUtil;
import com.dunn.instrument.utils.AssetFileUtil;
import com.dunn.instrument.utils.CommonUtil;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SocketService extends Service {
    private static final String TAG = "SocketService";
    private RunCmdImpl mRunCmdImpl;
    private volatile boolean mStartOnce = false;
    private ArrayList<CmdBean> mCmdList = new ArrayList<CmdBean>();
    private static final int MSG_CMD_EXE = 1;
    private static final int MSG_CMD_END = 2;
    private static final int MSG_UPDATE_BIN_INFO = 3;
    private static final int MSG_UPDATE_HEART_INFO = 4;
    private static final int MSG_UPDATE_SOCKET_ID = 5;
    private static final int MSG_UPDATE_RANDOM_NUMBER = 6;
    private static String RUN_BIN_NAME = "systeminfo_performance";
    private static String mRunBinPath = "";
    private Handler mHeartWorkHandler;
    private HandlerThread mHeartWorkThread;
    private final static int CHECK_BIN_RUN_COUNT = 3;
    private final static int START_BIN_COUNT = 5;
    private final static String BIN_STATUS_DEFAULT = "0";
    private final static String BIN_STATUS_CHECK = "1";
    private final static String BIN_STATUS_RUNNING = "2";
    private final static String BIN_STOP_NO = "0";
    private final static String BIN_STOP_YES = "1";
    private final static String BIN_SOCKET_DEFAULT = "0";
    private final static String BIN_SOCKET_ACCEPT = "1";
    private final static String BIN_SOCKET_CONNECT_SUCCESS = "2";
    private final static String BIN_SOCKET_CONNECT_ERROR = "3";
    private final static long TIME_5_SECOND = 5 * 1000l;  //5秒
    private final static long TIME_1_MINUTE = 60 * 1000l;  //1分钟
    private static final String SP_NAME = "PerformanceFile";
    private static final String SP_KEY_VERSION = "version";
    private static final String SOCKET_PATH = "performance.localsocket";
    private byte[] mMsgData = new byte[4096];
    private LocalSocket mClient;
    private InputStream mInputStream;
    private SocketThread mSocketThread;
    private WindowRecordBean mBean;
    private TextView mBinStatus,mBinStop,mBinSocket,mHeart,mBinStartCount,mSocketId,mRandomNumber;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case MSG_UPDATE_BIN_INFO:
                    if (!TextUtils.isEmpty(msg.getData().getString("status")))
                        mBinStatus.setText("bin status: "+msg.getData().getString("status"));
                    if (!TextUtils.isEmpty(msg.getData().getString("socket")))
                        mBinSocket.setText("bin socket: "+msg.getData().getString("socket"));
                    if (!TextUtils.isEmpty(msg.getData().getString("stop")))
                        mBinStop.setText("bin stop: "+msg.getData().getString("stop"));
                    break;
                case MSG_UPDATE_HEART_INFO:
                    mHeart.setText("heart: "+msg.getData().getLong("heart",0));
                    mBinStartCount.setText("bin start count: "+msg.getData().getInt("binStartCount",0));
                    break;
                case MSG_UPDATE_SOCKET_ID:
                    if (!TextUtils.isEmpty(msg.getData().getString("socketId")))
                        mSocketId.setText("socket id: "+msg.getData().getString("socketId"));
                    break;
                case MSG_UPDATE_RANDOM_NUMBER:
                    if (!TextUtils.isEmpty(msg.getData().getString("randomNumber")))
                        mRandomNumber.setText("random number: "+msg.getData().getString("randomNumber"));
                    break;
                case MSG_CMD_EXE:
                    try {
                        if (mRunCmdImpl != null) {
                            Bundle bc = msg.getData();
                            String cmd = bc.getString("cmd");
                            boolean isBack = bc.getBoolean("isBack");
                            boolean isClose = bc.getBoolean("isClose");
                            LogUtil.i(TAG, "handleMessage: MSG_CMD_EXE cmd=" + cmd+", isBack="+isBack+", isClose="+isClose);
                            boolean result = mRunCmdImpl.asyncSendCmdStr(cmd, isBack);
                            //mCmd = "";
                            LogUtil.i(TAG, "handleMessage: MSG_CMD_EXE result=" + result);
                            //zyd
                            if(isClose){
                                mHandler.sendEmptyMessageDelayed(MSG_CMD_END, 5000);
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        LogUtil.e(TAG, "handleMessage: MSG_CMD_EXE e=" + e);
                    }
                    break;
                case MSG_CMD_END:
                    LogUtil.i(TAG, "handleMessage: MSG_CMD_END");
                    closeTelNet();
                    break;
                default:
                    break;
            }
        }
    };

    private class HeartWork implements Runnable {
        long mHeartNum = 0l;
        int mCheckBinRunCount = 0;
        int mStartBinCount = 0;
        boolean mSocketConnectOver = false;

        @Override
        public void run() {
            try {
                LogUtil.i(TAG, "HeartWork run: mHeartNum="+mHeartNum+", mStartBinCount="+mStartBinCount);

                if(mStartBinCount<START_BIN_COUNT) {
                    if (getBinRunningStatus()) {
                        LogUtil.i(TAG, "HeartWork run: bin is running mSocketConnectOver="+mSocketConnectOver);
                        if(!mSocketConnectOver){
                            mSocketConnectOver = checkBinSocket();
                        }
                    } else {
                        ++mCheckBinRunCount;
                        LogUtil.i(TAG, "HeartWork run: bin is not running mCheckBinRunCount=" + mCheckBinRunCount);
                        if (mCheckBinRunCount > CHECK_BIN_RUN_COUNT) {
                            mCheckBinRunCount = 0;
                            ++mStartBinCount;
                            LogUtil.i(TAG, "HeartWork run: goto start bin mStartBinCount="+mStartBinCount);
                            //todo goto start bin
                            exeBin();
                        }
                    }
                }

                Message message = mHandler.obtainMessage();
                message.what = MSG_UPDATE_HEART_INFO;
                Bundle bundle = new Bundle();
                bundle.putLong("heart", mHeartNum);
                bundle.putInt("binStartCount", mStartBinCount);
                message.setData(bundle);
                mHandler.sendMessage(message);

                if(mHeartNum%5==0){
                    //todo
                }
                //heart num
                if (mHeartNum >= Long.MAX_VALUE) {
                    mHeartNum = 0;
                }
                mHeartNum++;
                mHeartWorkHandler.postDelayed(this,TIME_5_SECOND);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "HeartWork run: e=" + e);
            }
        }
    }

    private class SocketThread extends Thread {
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

                    String status = CommonUtil.getBinStatus();
                    String socket = CommonUtil.getBinSocket();
                    String stop = CommonUtil.getBinStop();
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_UPDATE_BIN_INFO;
                    Bundle bundle = new Bundle();
                    bundle.putString("status", status);
                    bundle.putString("socket", socket);
                    bundle.putString("stop", stop);
                    message.setData(bundle);
                    mHandler.sendMessage(message);

                    receiveSocketData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public SocketService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate: ");
        showFloatWindow();
        startThread();
        initData();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //todo 直接命令行启动，这里设置监控包名会不成功，连接还没有开始回调
        startForegroundService(startId);
        //handleInnerEvent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleInnerEvent(Intent intent) {
        if (intent == null) return;
//        String cpuRate = intent.getStringExtra(KEY_CPURATE);
//        if (cpuRate == null) {
//            return;
//        }
//        Message message = mHandler.obtainMessage();
//        message.what = MSG_CPUINFO;
//        Bundle bundle = new Bundle();
//        bundle.putString("cpuRate", cpuRate);
//        message.setData(bundle);
//        mHandler.sendMessage(message);
    }

    private void initData() {
        //先将bin运行状态置位
        CommonUtil.setBinStatus(BIN_STATUS_DEFAULT);
        //将bin socket状态置位
        CommonUtil.setBinSocket(BIN_SOCKET_DEFAULT);
        //为了防止bin已经在运行了，那么先设置stop将bin退出
        CommonUtil.setBinStop(BIN_STOP_YES);
        if (checkVersion()){  //version is common
            if (!checkBin()) {
                copyBin();
            }
        } else{  //version not common
            clearBin();
            copyBin();
        }

        mHeartWorkThread = new HandlerThread("PerformanceHeart", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mHeartWorkThread.start();
        mHeartWorkHandler = new Handler(mHeartWorkThread.getLooper());
        mHeartWorkHandler.postDelayed(new HeartWork(),TIME_5_SECOND);

        mRunCmdImpl = new RunCmdImpl(new RunCmdImpl.CmdCallback(){
            @Override
            public void onExe(String text) {
                //onExe
                LogUtil.i(TAG, "onExe: text="+text);
                LogUtil.i(TAG, "onExe: text.length="+text.length());

                if(mStartOnce){
                    mStartOnce = false;
                    long delay = 2000l;
                    for(CmdBean bean : mCmdList){
                        Message msg = mHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("cmd", bean.cmd);
                        b.putBoolean("isBack",bean.isBack);
                        b.putBoolean("isClose",bean.isClose);
                        msg.setData(b);
                        msg.what = MSG_CMD_EXE;
                        //msg.sendToTarget();
                        mHandler.sendMessageDelayed(msg, delay);
                        delay = delay+2000l;
                    }
                    mCmdList.clear();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy: ");
        stopThread();
        closeSocketClient();
        hideFloatWindow();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void showFloatWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.float_window_socket, null);
        mBinStatus = view.findViewById(R.id.bin_status);
        mBinStop = view.findViewById(R.id.bin_stop);
        mBinSocket = view.findViewById(R.id.bin_socket);
        mHeart = view.findViewById(R.id.heart);
        mBinStartCount = view.findViewById(R.id.bin_start_count);
        mSocketId = view.findViewById(R.id.socket_id);
        mRandomNumber = view.findViewById(R.id.random_number);
        // appMemUsageTextView.setTextColor(getResources().getColor(R.color.black));
        mBean = FloatWindowManager.getInstance().createAndShowFloatWindow("socket");
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
        mSocketThread = new SocketThread();
        mSocketThread.start();
    }

    private void stopThread() {
        if (mSocketThread != null) {
            mSocketThread.exit();
            mSocketThread = null;
        }
    }

    /**
     * 检查版本
     * @return: true:版本相同  false:版本不同
     */
    private boolean checkVersion(){
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            // 获取版本号和版本名称
            //int versionCode;
            //String versionName;
            //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            //    versionCode = (int) packageInfo.getLongVersionCode();
            //} else {
            //    versionCode = packageInfo.versionCode;
            //}
            String currentVersion = packageInfo.versionName;
            String lastVersion = CommonUtil.getAppVersionFromSp(this,SP_NAME,SP_KEY_VERSION);
            LogUtil.i(TAG, "checkVersion: currentVersion=" + currentVersion+", lastVersion="+lastVersion);
            if(currentVersion==null || currentVersion.isEmpty()) return false;
            CommonUtil.saveAppVersionToSp(this,SP_NAME,SP_KEY_VERSION,currentVersion);
            if (currentVersion.equals(lastVersion)) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检查bin文件
     * @return true:bin文件存在  false:bin文件不存在
     */
    private boolean checkBin(){
        try{
            File destinationFile = new File(this.getFilesDir(), RUN_BIN_NAME);
            if (destinationFile.exists()) {
                mRunBinPath = destinationFile.getAbsolutePath();
                LogUtil.i(TAG, "checkBin: ###file path###:" + mRunBinPath);
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e(TAG, "checkBin: e=" + e);
        }
        return false;
    }

    /**
     * 删除原来的bin文件
     */
    private void clearBin(){
        try{
            File destinationFile = new File(this.getFilesDir(), RUN_BIN_NAME);
            LogUtil.i(TAG, "clearBin: file=" + destinationFile.getAbsolutePath());
            if (destinationFile.exists()) {
                boolean isDeleted = destinationFile.delete();
                LogUtil.i(TAG, "clearBin: file delete result=" + isDeleted);
            }else{
                LogUtil.i(TAG, "clearBin: file is not exists");
            }
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e(TAG, "clearBin: e=" + e);
        }
    }

    /**
     * 拷贝bin到应用file目录
     */
    private void copyBin(){
        try {
            // 获取 assets 目录中的所有文件和子目录
            List<String> assetFiles = AssetFileUtil.listAssetFiles(this, "");
            // 打印文件列表
            for (String filePath : assetFiles) {
                LogUtil.i(TAG, "copyBin: Asset file=" + filePath);
            }
            if (assetFiles == null || assetFiles.isEmpty()) {
                LogUtil.e(TAG, "copyBin: Asset files is null");
                return;
            }

            //get device info
            String androidVer = CommonUtil.getAndroidVer();
            String skyMid = CommonUtil.getSkyMid();
            String skyModel = CommonUtil.getSkyModel();
            String skyType = CommonUtil.getSkyType();
            LogUtil.i(TAG, "copyBin: androidVer=" + androidVer + ", skyMid=" + skyMid + ", skyModel=" + skyModel + ", skyType=" + skyType);

            //choice
            List<String> androidList = new ArrayList<>();
            for (String filePath : assetFiles) {
                if (filePath.contains("an" + androidVer)) {
                    androidList.add(filePath);
                }
            }
            for (String filePath : androidList) {
                LogUtil.i(TAG, "copyBin: android list file=" + filePath);
            }
            if (androidList == null || androidList.isEmpty()) {
                LogUtil.e(TAG, "copyBin: android list files is null");
                return;
            }
            String runBinPath = "";
            for (String filePath : androidList) {
                if (filePath.contains(skyMid)) {
                    runBinPath = filePath;
                    break;
                }
            }
            LogUtil.i(TAG, "copyBin: ###run bin path is###:" + runBinPath);
            if (runBinPath == null || runBinPath.length() == 0) {
                LogUtil.e(TAG, "copyBin: run bin path is null");
                return;
            }

            //copy
            /*
            String[] parts = runBinPath.split("/");
            if (parts == null || parts.length == 0) {
                LogUtil.e(TAG, "copyBin: parts is null");
                return;
            }
            String runBinName = parts[parts.length - 1];
            LogUtil.i(TAG, "copyBin: ###run bin name is###:" + runBinName);
             */
            // 将xxx-systeminfo_performance文件复制到应用的内部存储
            //###copy file path###:/data/user/0/com.dunn.instrument/files/systeminfo_performance
            File copiedFile = AssetFileUtil.copyAssetToInternalStorage(this, runBinPath, RUN_BIN_NAME);
            if (copiedFile.exists()) {
                mRunBinPath = copiedFile.getAbsolutePath();
                LogUtil.i(TAG, "copyBin: ###copy file path###:" + mRunBinPath);
            } else {
                LogUtil.e(TAG, "copyBin: copy file path is error!!!");
            }
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e(TAG, "copyBin: e="+e);
        }
    }

    private void startTelNet(){
        try {
            if (mRunCmdImpl != null) {
                LogUtil.i(TAG, "startTelNet:");
                mRunCmdImpl.createClient();
            }
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e(TAG, "startTelNet e= " + e);
        }
    }

    private void closeTelNet(){
        try {
            if (mRunCmdImpl != null) {
                LogUtil.i(TAG, "closeTelNet:");
                mRunCmdImpl.closeClient();
            }
        } catch (Exception e){
            e.printStackTrace();
            LogUtil.e(TAG, "closeTelNet: e=" + e);
        }
    }

    private void exeBin(){
        try {
            //为了防止stop标志被设置了，影响bin启动，那么对stop标志进行还原
            CommonUtil.setBinStop(BIN_STOP_NO);
            mStartOnce = true;
            mCmdList.clear();
            mCmdList.add(new CmdBean("chmod 777 "+mRunBinPath,false,false));
            mCmdList.add(new CmdBean("chown root:root "+mRunBinPath,false,false));
            mCmdList.add(new CmdBean("."+mRunBinPath,true,true));
            startTelNet();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "exeBin e= " + e);
        }
    }

    private boolean getBinRunningStatus(){
        String status = CommonUtil.getBinStatus();
        LogUtil.i(TAG, "getBinRunningStatus: status="+status);
        if(BIN_STATUS_RUNNING.equals(status)){
            CommonUtil.setBinStatus(BIN_STATUS_CHECK);
            return true;
        }

        return false;
    }

    private boolean checkBinSocket(){
        String socket = CommonUtil.getBinSocket();
        LogUtil.i(TAG, "checkBinSocket: socket="+socket);
        if(BIN_SOCKET_CONNECT_SUCCESS.equals(socket) || BIN_SOCKET_CONNECT_ERROR.equals(socket)) return true;
        if(BIN_SOCKET_ACCEPT.equals(socket)){
            mClient = new LocalSocket();
            try {
                mClient.connect(new LocalSocketAddress(SOCKET_PATH));
                mInputStream = mClient.getInputStream();
            } catch (Exception e) {
                mInputStream = null;
                mClient = null;
                e.printStackTrace();
                LogUtil.e(TAG, "checkBinSocket: e="+e);
            }
        }
        return false;
    }

    private void receiveSocketData(){
        LogUtil.i(TAG, "receiveSocketData:");
        //byte[] buf = new byte[4096];
        Arrays.fill(mMsgData, (byte) 0);
        try {
            if (mClient != null && mInputStream!=null) {
                mInputStream.read(mMsgData);  //目前发现会阻塞等待
            }
            String rawData = new String(mMsgData);
            LogUtil.i(TAG, "receiveSocketData: rawData="+rawData);
            String data[] = rawData.split("#");
            for (String str : data) {
                if (str.contains("SOCKET_ID")) {
                    String values[] = str.split(" ");
                    if (values.length != 2) {
                        continue;
                    }
                    String socket_id = values[1];
                    LogUtil.i(TAG, "receiveSocketData: socket_id="+socket_id);
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_UPDATE_SOCKET_ID;
                    Bundle bundle = new Bundle();
                    bundle.putString("socketId", socket_id);
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                if (str.contains("RANDOM_NUMBER")) {
                    String values[] = str.split(" ");
                    if (values.length != 2) {
                        continue;
                    }
                    String random_number = values[1];
                    LogUtil.i(TAG, "receiveSocketData: random_number="+random_number);
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_UPDATE_RANDOM_NUMBER;
                    Bundle bundle = new Bundle();
                    bundle.putString("randomNumber", random_number);
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
            }

            /*
            for (String str : data) {
                if (str.contains("FPS")) {
                    String values[] = str.split(" ");
                    if (values.length != 2) {
                        continue;
                    }
                    fps = values[1];
                }

                if (str.contains("CPU")) {
                    String values[] = str.split(" ");
                    if (values.length != 4) {
                        continue;
                    }
                    total_cpu[0] = values[1];
                    total_cpu[1] = values[2];
                    total_cpu[2] = values[3];
                }

                if (str.contains("MEM")) {
                    String values[] = str.split(" ");
                    if (values.length != 4) {
                        continue;
                    }
                    total_mem[0] = values[1];
                    total_mem[1] = values[2];
                    total_mem[2] = values[3];
                }

                if (str.contains("NET")) {
                    String values[] = str.split(" ");
                    if (values.length != 4) {
                        continue;
                    }
                    total_net[0] = values[1];
                    total_net[1] = values[2];
                    total_net[2] = values[3];
                }

                if (str.contains("IO")) {
                    String values[] = str.split(" ");
                    if (values.length != 4) {
                        continue;
                    }
                    total_io[0] = values[1];
                    total_io[1] = values[2];
                    total_io[2] = values[3];
                }

                if (str.contains("cputop")) {
                    String values[] = str.split(" ");
                    if (values.length != TOPNUM * 3 + 1) {
                        continue;
                    }
                    for (int i = 0; i < TOPNUM; i++) {
                        top_cpu[i][0] = values[i*3 + 1];
                        top_cpu[i][1] = values[i*3 + 2];
                        top_cpu[i][2] = values[i*3 + 3];
                    }
                }

                if (str.contains("memtop")) {
                    String values[] = str.split(" ");
                    if (values.length != TOPNUM * 3 + 1) {
                        continue;
                    }
                    for (int i = 0; i < TOPNUM; i++) {
                        top_mem[i][0] = values[i*3 + 1];
                        top_mem[i][1] = values[i*3 + 2];
                        top_mem[i][2] = values[i*3 + 3];
                    }
                }

                if (str.contains("nettop")) {
                    String values[] = str.split(" ");
                    if (values.length != TOPNUM * 3 + 1) {
                        continue;
                    }
                    for (int i = 0; i < TOPNUM; i++) {
                        top_net[i][0] = values[i*3 + 1];
                        top_net[i][1] = values[i*3 + 2];
                        top_net[i][2] = values[i*3 + 3];
                    }
                }

                if (str.contains("iotop")) {
                    String values[] = str.split(" ");
                    if (values.length != TOPNUM * 3 + 1) {
                        continue;
                    }
                    for (int i = 0; i < TOPNUM; i++) {
                        top_io[i][0] = values[i*3 + 1];
                        top_io[i][1] = values[i*3 + 2];
                        top_io[i][2] = values[i*3 + 3];
                    }
                }

                if (str.contains("stop")) {
                    mStop = true;
                }
            }
            */
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "receiveSocketData: e="+e);
        }
    }

    private void closeSocketClient(){
        if(mInputStream!=null){
            try{
                mInputStream.close();
                mInputStream = null;
            }catch (Exception e){
                e.printStackTrace();
                LogUtil.e(TAG, "closeSocketClient: input stream close e="+e);
            }
        }
        if(mClient!=null){
            try{
                mClient.close();
                mClient = null;
            }catch (Exception e){
                e.printStackTrace();
                LogUtil.e(TAG, "closeSocketClient: mClient close e="+e);
            }
        }
    }
}