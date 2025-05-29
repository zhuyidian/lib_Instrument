package com.dunn.instrument.shell;

import com.dunn.instrument.tools.log.LogUtil;
import java.util.List;

/**
 * @ClassName: RunCmdImpl
 * @Author: ZhuYiDian
 * @CreateDate: 2024/07/29
 * @Description: RunCmdImpl
 */
public class RunCmdImpl {
    private static final String TAG = "RunCmdImpl";
    private ClientConnect mClientConnect;
    private boolean isTelnetEnable = true;
    private volatile boolean mStartOnce = false;
    private volatile String mExeCmd = "";
    private final Object mCmdMuex = new Object();
    private CmdCallback mCmdCallback;

    public interface CmdCallback {
        void onExe(String text);
    }

    public RunCmdImpl(final CmdCallback changeCallback) {
        mCmdCallback = changeCallback;
    }

    public void createClient() throws Exception{
        if (mClientConnect == null) {
            isTelnetEnable = true;
            mClientConnect = new ClientConnect(new ClientConnect.SessionChangedCallback() {
                @Override
                public void onSessionStart() {
                    //start
                    LogUtil.i(TAG, "createClient: onSessionStart");
                }

                @Override
                public void onSessionFinished(String error) {
                    //no connect telnet
                    if (error.contains("SocketDisconnect-1")) {
                        LogUtil.e(TAG, "createClient: onSessionFinished client connect is error isTelnetEnable=" + isTelnetEnable);
                        try {
                            closeClient();
                        } catch (Exception e) {
                            LogUtil.e(TAG, "createClient: onSessionFinished close client is error e=" + e);
                        }
                    }
                }

                @Override
                public void onTextChanged(String text) {
                    LogUtil.i(TAG, "createClient: onTextChanged text=" + text);
                    // [:/ #] end
                    if(mCmdCallback!=null){
                        mCmdCallback.onExe(text);
                    }
                }
            });
            if (mClientConnect.connect()) {
                LogUtil.i(TAG, "createClient: client connect and send start_telnetd");
                mClientConnect.send("start_telnetd");
            } else {
                closeClient();
                LogUtil.e(TAG, "createClient: client connect is error isTelnetEnable=" + isTelnetEnable);
            }
        }
    }

    public void closeClient() throws Exception{
        if(mClientConnect!=null){
            LogUtil.i(TAG, "closeClient: ");
            mClientConnect.finishIfRunning();
            mClientConnect = null;
            isTelnetEnable = false;
        }
    }

    /*
    public boolean syncSendCmd(List<String> cmdList, boolean backGround){
        if(cmdList==null) {
            Slog.e(TAG, "syncSendCmd: cmdList is null");
            return false;
        }
        synchronized (mCmdMuex) {
            try {
                String cmd = "";
                for (String c : cmdList) {
                    cmd = cmd + c + " ";
                }
                if(backGround){
                    cmd = cmd + "&";
                }
                mExeCmd = cmd.trim();
                Slog.i(TAG, "syncSendCmd: isTelnetEnable=" + isTelnetEnable + ", mClientConnect=" + mClientConnect + ", mExeCmd=" + mExeCmd);
                mStartOnce = true;
                createClient();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Slog.e(TAG, "syncSendCmd: e=" + e);
            }
        }
        return false;
    }
    */

    public boolean asyncSendCmdStr(String cmd, boolean backGround){
        if(cmd==null) {
            LogUtil.i(TAG,"asyncSendCmdStr: cmd is null");
            return false;
        }
        if(mClientConnect==null || !isTelnetEnable){
            LogUtil.i(TAG, "asyncSendCmdStr: client not connect isTelnetEnable=" + isTelnetEnable + ", mClientConnect=" + mClientConnect);
            return false;
        }
        synchronized (mCmdMuex) {
            try {
                if(backGround){
                    cmd = cmd + " &";
                }
                mExeCmd = cmd.trim();
                LogUtil.i(TAG, "asyncSendCmdStr: isTelnetEnable=" + isTelnetEnable + ", mClientConnect=" + mClientConnect + ", mExeCmd=" + mExeCmd);
                if (isTelnetEnable && mClientConnect != null) {
                    mClientConnect.write(mExeCmd + "\r");
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "asyncSendCmdStr: e=" + e);
            }
        }
        return false;
    }

    public boolean asyncSendCmd(List<String> cmdList, boolean backGround){
        if(cmdList==null) {
            LogUtil.i(TAG, "asynSendCmd: cmdList is null");
            return false;
        }
        if(mClientConnect==null || !isTelnetEnable){
            LogUtil.i(TAG, "asynSendCmd: client not connect isTelnetEnable=" + isTelnetEnable + ", mClientConnect=" + mClientConnect);
            return false;
        }
        synchronized (mCmdMuex) {
            try {
                String cmd = "";
                for (String c : cmdList) {
                    cmd = cmd + c + " ";
                }
                if(backGround){
                    cmd = cmd + "&";
                }
                mExeCmd = cmd.trim();
                LogUtil.i(TAG, "asynSendCmd: isTelnetEnable=" + isTelnetEnable + ", mClientConnect=" + mClientConnect + ", mExeCmd=" + mExeCmd);
                if (isTelnetEnable && mClientConnect != null) {
                    mClientConnect.write(mExeCmd + "\r");
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "asynSendCmd: e=" + e);
            }
        }
        return false;
    }
}
