package com.dunn.instrument.shell;

import android.content.Context;
import android.util.Log;

import com.dunn.instrument.tools.log.LogUtil;

/**
 * @ClassName: Telnet
 * @Author: ZhuYiDian
 * @CreateDate: 2023/1/16 16:54
 * @Description:
 */
public class Telnet {
    private static final String TAG = "Telnet";
    private ClientConnect clientConnect;

    public void startTelnet(final Context context) {
        if (clientConnect == null) {
            clientConnect = new ClientConnect(new ClientConnect.SessionChangedCallback() {
                @Override
                public void onSessionStart() {
                    String pkg = context.getPackageName();
                    LogUtil.i(TAG,"startTelnet onSessionStart pkg name: " + pkg);
                    String cmd = "export CLASSPATH=`pm path " + pkg + "`\n";
                    clientConnect.write(cmd);
                    clientConnect.write("app_process /system/bin com.dunn.instrument.ShellProc\n");
                }

                @Override
                public void onSessionFinished(String error) {
                    //无法连接telnetd
                    if (error.contains("SocketDisconnect-1")) {
                        LogUtil.e(TAG,"startTelnet onSessionFinished: 无法连接telnetd");
                    } else {
                        clientConnect = null; //尝试重连
                    }
                }

                @Override
                public void onTextChanged(String text) {
                    LogUtil.e(TAG,"startTelnet onTextChanged: " + text);
                }
            });
            if (clientConnect.connect()) {
                clientConnect.send("start_telnetd");
            } else {
                LogUtil.e(TAG,"startTelnet: no telnet");
            }
        }
    }

    public void stopTelnet(){
        if (clientConnect != null) {
            clientConnect.finishIfRunning();
            clientConnect = null;
        }
    }
}
