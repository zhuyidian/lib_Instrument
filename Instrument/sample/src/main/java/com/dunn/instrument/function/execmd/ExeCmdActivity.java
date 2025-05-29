package com.dunn.instrument.function.execmd;

import static com.dunn.instrument.function.keepalive.InterfaceKeepalive.PROCESS_CMD_AUTO_START;
import static com.dunn.instrument.function.keepalive.InterfaceKeepalive.PROCESS_CMD_AUTO_START_ALLOW;
import static com.dunn.instrument.function.keepalive.InterfaceKeepalive.PROCESS_CMD_AUTO_START_NOT_ALLOW;
import static com.dunn.instrument.function.keepalive.InterfaceKeepalive.PROCESS_CMD_BACKGROUND;
import static com.dunn.instrument.function.keepalive.InterfaceKeepalive.PROCESS_CMD_BACKGROUND_KEEP;
import static com.dunn.instrument.function.keepalive.InterfaceKeepalive.PROCESS_CMD_BACKGROUND_LIMIT;
import static com.dunn.instrument.function.keepalive.InterfaceKeepalive.PROCESS_CMD_BACKGROUND_SMART;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.skyworth.skymonitor.AppCustom;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dunn.instrument.MainApp;
import com.dunn.instrument.R;
import com.dunn.instrument.function.keepalive.AppStateCtl;
import com.dunn.instrument.function.keepalive.AppsAdapter;
import com.dunn.instrument.function.keepalive.AppsBean;
import com.dunn.instrument.function.keepalive.InterfaceKeepaliveSystem;
import com.dunn.instrument.service.FrameworkInfoService;
import com.dunn.instrument.service.ResourceService;
import com.dunn.instrument.service.SocketService;
import com.dunn.instrument.shell.RunCmdImpl;
import com.dunn.instrument.tools.framework.pkms.PkmsUtil;
import com.dunn.instrument.tools.log.LogUtil;
import com.dunn.instrument.tools.thread.ThreadManager;
import com.dunn.instrument.utils.AssetFileUtil;
import com.dunn.instrument.utils.CommonUtil;
import com.dunn.instrument.view.dialog.DelayShowDialog;
import com.dunn.instrument.view.dialog.TCallBack;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExeCmdActivity extends Activity implements View.OnClickListener {
    public static String TAG = "ExeCmdActivity";
    private RunCmdImpl mRunCmdImpl;
    private volatile boolean mStartOnce = false;
    private ArrayList<CmdBean> mCmdList = new ArrayList<CmdBean>();
    private static final int MSG_CMD_EXE = 1;
    private static final int MSG_CMD_END = 2;
    private static final int MSG_CMD_UI_UPDATE = 3;
    private static String RUN_BIN_NAME = "systeminfo_performance";
    private static String mRunBinPath = "";
    private StringBuilder mSb = new StringBuilder();
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
    private LocalSocket mClient;
    private InputStream mInputStream;
    private ScrollView mCmdScrollView;
    private TextView mCmdWindow;
    //btn1
    private Button mBtn1Start,
            mBtn1Close,
            mBtn1Pwd,
            mBtn1Ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execmd);
        initView();
        initData();
    }

    private void initView() {
        //cmd window
        mCmdScrollView = (ScrollView) findViewById(R.id.cmd_scrollview);
        mCmdWindow = (TextView) findViewById(R.id.cmd_window);
        mCmdScrollView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 强制显示垂直滑动条
                    mCmdScrollView.setVerticalScrollBarEnabled(true);
                    mCmdScrollView.setScrollbarFadingEnabled(false);
                } else {
                    // 强制显示垂直滑动条
                    mCmdScrollView.setVerticalScrollBarEnabled(false);
                    mCmdScrollView.setScrollbarFadingEnabled(true);
                }
            }
        });

        //btn1
        mBtn1Start = (Button) findViewById(R.id.btn_1_start);
        mBtn1Close = (Button) findViewById(R.id.btn_1_close);
        mBtn1Pwd = (Button) findViewById(R.id.btn_1_pwd);
        mBtn1Ll = (Button) findViewById(R.id.btn_1_ll);
        mBtn1Start.setOnClickListener(this);
        mBtn1Close.setOnClickListener(this);
        mBtn1Pwd.setOnClickListener(this);
        mBtn1Ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //btn1
            case R.id.btn_1_start:
                mSb.setLength(0);
                startTelNet();
                break;
            case R.id.btn_1_close:
                mSb.setLength(0);
                closeTelNet();
                break;
            case R.id.btn_1_pwd:
                mSb.setLength(0);
                sendCmdTest("pwd");
                break;
            case R.id.btn_1_ll:
                mSb.setLength(0);
                sendCmdTest("ll");
                break;

            default:
                break;
        }
    }

    private void initData() {
        //转移到socket service
        /*
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
        */
        Intent intentW = new Intent(MainApp.mContext, SocketService.class);
        startService(intentW);

        mRunCmdImpl = new RunCmdImpl(new RunCmdImpl.CmdCallback(){
            @Override
            public void onExe(String text) {
                //onExe
                LogUtil.i(TAG, "onExe: text="+text);
                LogUtil.i(TAG, "onExe: text.length="+text.length());
                mSb.append(text);

                Message msg_update = mHandler.obtainMessage();
                Bundle b_update = new Bundle();
                b_update.putString("text", mSb.toString());
                msg_update.setData(b_update);
                msg_update.what = MSG_CMD_UI_UPDATE;
//                msg_update.sendToTarget();
                mHandler.removeMessages(MSG_CMD_UI_UPDATE);
                mHandler.sendMessageDelayed(msg_update, 1000);

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

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
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
                case MSG_CMD_UI_UPDATE:
                    LogUtil.i(TAG, "handleMessage: MSG_CMD_UI_UPDATE");
                    Bundle data = msg.getData();
                    String text = data.getString("text");
                    mCmdWindow.setText(text);
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

    @Override
    protected void onResume() {
        super.onResume();
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

    @Deprecated
    private void sendCmdTest(String cmd){
//        mCmdList.clear();
//        mCmdList.add(new CmdBean(cmd,false,false));
//        if(mCmdList==null || mCmdList.isEmpty()) {
//            return;
//        }
        Message msg = mHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("cmd", cmd);
        b.putBoolean("isBack",false);
        b.putBoolean("isClose",false);
        msg.setData(b);
        msg.what = MSG_CMD_EXE;
        msg.sendToTarget();
        //mHandler.sendMessageDelayed(msg, delay);
    }
}
