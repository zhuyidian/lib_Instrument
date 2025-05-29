package com.dunn.instrument.shell;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.dunn.instrument.tools.log.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName: ClientConnect
 * @Author: ZhuYiDian
 * @CreateDate: 2024/07/29
 * @Description: ClientConnect
 */
public class ClientConnect {
    private static final String TAG = "ClientConnect";
    private static final String LocalSocketNameCMD = "coocaa_guard_ctrl_cmd";
    private static final String LocalSocketNameSOCK = "/data/ccos/guard.sock";
    private volatile boolean isCMDTry = false;
    private LocalSocket mClient = null;  //coocaa_guard回复ok后就会关闭，不会保持长连接

    private Socket mSocket;
    private static final String telnetHostName = "127.0.0.1";
    private static final int telnetHostPort = 4149;
    private static final int MSG_NEW_INPUT = 1;
    private static final int MSG_PROCESS_EXITED = 4;
    /**
     * A queue written to from a separate thread when the process outputs, and read by main thread to process by
     * terminal emulator.
     */
    final ByteQueue mProcessToTerminalIOQueue = new ByteQueue(4096);
    /**
     * A queue written to from the main thread due to user interaction, and read by another thread which forwards by
     * writing to the.
     */
    final ByteQueue mTerminalToProcessIOQueue = new ByteQueue(4096);
    final SessionChangedCallback mChangeCallback;

    public interface SessionChangedCallback {
        void onSessionStart();

        void onSessionFinished(String text);

        void onTextChanged(String text);
    }

    public ClientConnect(final SessionChangedCallback changeCallback) {
        this.mChangeCallback = changeCallback;
    }

    final Handler mMainThreadHandler = new Handler(Looper.getMainLooper()) {
        final byte[] mReceiveBuffer = new byte[4 * 1024];

        @Override
        public void handleMessage(Message msg) {
            try {
                int bytesRead = mProcessToTerminalIOQueue.read(mReceiveBuffer, false);
                if (bytesRead > 0) {
                    LogUtil.i(TAG, "handleMessage: [socket] process to terminal data len=" + bytesRead);
                    //mEmulator.append(mReceiveBuffer, bytesRead);
                    byte[] array = new byte[bytesRead];
                    System.arraycopy(mReceiveBuffer, 0, array, 0, bytesRead);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        //call back
                        //notifyScreenUpdate(new String(array, StandardCharsets.UTF_8));
                        String receiveText = new String(array, StandardCharsets.UTF_8);
                        LogUtil.i(TAG, "handleMessage: [socket] receiveText=" + receiveText);
                        if (mChangeCallback != null) mChangeCallback.onTextChanged(receiveText);
                    } else {
                        //call back
                        //notifyScreenUpdate(new String(array, Charset.forName("UTF-8")));
                        String receiveText = new String(array, Charset.forName("UTF-8"));
                        LogUtil.i(TAG, "handleMessage: [socket] receiveText=" + receiveText);
                        if (mChangeCallback != null) mChangeCallback.onTextChanged(receiveText);
                    }
                }

                if (msg.what == MSG_PROCESS_EXITED) {
                    int exitCode = (Integer) msg.obj;
                    LogUtil.i(TAG, "handleMessage: MSG_PROCESS_EXITED [socket] exitCode=" + exitCode);
                    cleanupResources();
                    String exitDescription = "SocketDisconnect" + exitCode;
                    if (exitCode == -1) {
                        exitDescription = exitDescription +
                                " \r\ncoocaa_guard or /system(/vendor)/bin/skybusybox/busybox  are not found" +
                                " \r\nnormal shell will be instead of root shell" +
                                " \r\nsome commands may not be permitted\r\n";
                    }
                    if (mChangeCallback != null) mChangeCallback.onSessionFinished(exitDescription);
                    //notifyScreenUpdate(exitDescription);
                    if (mChangeCallback != null) mChangeCallback.onTextChanged(exitDescription);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "handleMessage: e="+e);
            }
        }
    };

    public boolean connect() {
        isCMDTry = false;
        try {
            File file = new File(LocalSocketNameSOCK);
            mClient = new LocalSocket();
            if (file.exists()) {
                LogUtil.i(TAG, "connect: [client] goto connect /data/ccos/guard.sock");
                mClient.connect(new LocalSocketAddress(LocalSocketNameSOCK,
                        LocalSocketAddress.Namespace.FILESYSTEM));
            } else {
                isCMDTry = true;
                LogUtil.i(TAG, "connect: [client] goto connect coocaa_guard_ctrl_cmd");
                mClient.connect(new LocalSocketAddress(LocalSocketNameCMD,
                        LocalSocketAddress.Namespace.ABSTRACT));
            }
            LogUtil.i(TAG, "connect: [client] connect success");
            //Client.setSoTimeout(timeout);
        } catch (IOException e) {
            if(!isCMDTry) {
                try {
                    LogUtil.i(TAG, "connect: [client] again goto connect coocaa_guard_ctrl_cmd");
                    mClient.connect(new LocalSocketAddress(LocalSocketNameCMD,
                            LocalSocketAddress.Namespace.ABSTRACT));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    LogUtil.e(TAG, "connect: [client] connect coocaa_guard_ctrl_cmd also failed!!! ex="+ex);
                    return false;
                }
                return true;
            } else {
                e.printStackTrace();
                LogUtil.e(TAG, "connect: [client] connect fail!!! e="+e);
                return false;
            }
        }
        return true;
    }

    public void send(String data) {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try{
            outputStream = mClient.getOutputStream();
            inputStream = mClient.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                outputStream.write(data.getBytes(StandardCharsets.UTF_8));
            } else {
                outputStream.write(data.getBytes(Charset.forName("UTF-8")));
            }
            outputStream.flush();
            LogUtil.i(TAG, "send: [client] send over data="+data);

            String result = bufferedReader.readLine();
            LogUtil.i(TAG, "send: [client] read result="+result);
            if (result.contains("ok")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        connectTelnetServer();
                    }
                }).start();
            }
        } catch (IOException e) {
            connectTelnetServer(); //主动触发socket异常，避免导致两个终端都连接不上
            e.printStackTrace();
            LogUtil.e(TAG, "send: [client] send failed e="+e);
        } finally {
            // todo
            try {
                if (bufferedReader != null) bufferedReader.close();
            } catch (IOException e) {
                LogUtil.e(TAG, "send: [client] bufferedReader close failed e="+e);
            }
            try {
                if (inputStreamReader != null) inputStreamReader.close();
            } catch (IOException e) {
                LogUtil.e(TAG, "send: [client] inputStreamReader close failed e="+e);
            }
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                LogUtil.e(TAG, "send: [client] inputStream close failed e="+e);
            }
            try {
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                LogUtil.e(TAG, "send: [client] outputStream close failed e="+e);
            }
        }
        close();
    }

    private void close() {
        //任意一个流关闭都会导致连接关闭
        try {
            mClient.close();
            LogUtil.i(TAG, "close: [client] close is success");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "close: [client] close is failed e="+e);
        }
    }

    private void connectTelnetServer() {
        try {
            //创建一个无连接的Socket
            mSocket = new Socket();
            //连接到指定的IP和端口号，并指定1s的超时时间
            mSocket.connect(new InetSocketAddress(telnetHostName, telnetHostPort), 1000);
            LogUtil.i(TAG, "connectTelnetServer: [socket] isConnected=" + mSocket.isConnected());
            if (mSocket.isConnected()) {
                new Thread("TermSessionInputReader") {
                    @Override
                    public void run() {
                        try {
                            InputStream termIn = mSocket.getInputStream();
                            final byte[] buffer = new byte[4096];
                            boolean isFirst = true;
                            while (true) {
                                int read = termIn.read(buffer);
                                LogUtil.i(TAG, "connectTelnetServer: [socket] read...... len=" + read);
                                //某些设备telnet会找不到可用pty
                                //第一次读返回-1，当做telnet打开失败
                                if (read == -1) {
                                    LogUtil.e(TAG, "connectTelnetServer: [socket] read failed -1");
                                    if (isFirst) {
                                        mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(MSG_PROCESS_EXITED, -1));
                                    } else {  //长时间连接后可能出现的失败
                                        mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(MSG_PROCESS_EXITED, 0));
                                    }
                                    return;
                                }
                                isFirst = false;
                                if (!mProcessToTerminalIOQueue.write(buffer, 0, read)) {
                                    LogUtil.e(TAG, "connectTelnetServer: [socket] process to terminal is error");
                                    return;
                                }
                                mMainThreadHandler.sendEmptyMessage(MSG_NEW_INPUT);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtil.e(TAG, "connectTelnetServer: [socket] read e=" + e);
                        }
                    }
                }.start();

                new Thread("TermSessionOutputWriter") {
                    @Override
                    public void run() {
                        final byte[] buffer = new byte[4096];
                        try {
                            OutputStream termOut = mSocket.getOutputStream();
                            while (true) {
                                int bytesToWrite = mTerminalToProcessIOQueue.read(buffer, true);
                                LogUtil.i(TAG, "connectTelnetServer: [socket] write...... len=" + bytesToWrite);
                                if (bytesToWrite == -1) {
                                    LogUtil.e(TAG, "connectTelnetServer: [socket] write is error len=" + bytesToWrite);
                                    return;
                                }
                                termOut.write(buffer, 0, bytesToWrite);
                                termOut.flush();
                            }
                        } catch (Exception e) {
                            //由读线程监测异常，避免重复发消息
                            LogUtil.e(TAG, "connectTelnetServer: [socket] write e=" + e);
                        }
                    }
                }.start();

                if(mChangeCallback!=null) mChangeCallback.onSessionStart();
            } else {
                LogUtil.e(TAG, "connectTelnetServer: [socket] connected is error!!!!!");
                mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(MSG_PROCESS_EXITED, -1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "connectTelnetServer: [socket] connected is error e=" + e);
            mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(MSG_PROCESS_EXITED, -1));
        }
    }

    /**
     * Write a string using the UTF-8 encoding to the terminal client.
     */
    public final void write(String data) {
        try {
            LogUtil.i(TAG, "write: [socket] terminal to process data=" + data);
            byte[] bytes;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                bytes = data.getBytes(StandardCharsets.UTF_8);
            } else {
                bytes = data.getBytes(Charset.forName("UTF-8"));
            }
            write(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "write: [socket] terminal to process is error data=" + data);
        }
    }

    public void write(byte[] data, int offset, int count) throws Exception{
        if (mSocket != null && mSocket.isConnected()) {
            mTerminalToProcessIOQueue.write(data, offset, count);
        }else{
            LogUtil.e(TAG, "write: [socket] terminal to process is not connect");
        }
    }

    private void cleanupResources() {
        // Stop the reader and writer threads, and close the I/O streams
        try{
            mTerminalToProcessIOQueue.close();
            mProcessToTerminalIOQueue.close();
            LogUtil.i(TAG, "cleanupResources: [socket] IOQueue close");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "cleanupResources: [socket] IOQueue close is error e=" + e);
        }

        try {
            mSocket.close(); //会导致输入和输出流异常
            LogUtil.i(TAG, "cleanupResources: [socket] close");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "cleanupResources: [socket] close is error e=" + e);
        }
    }

    public void finishIfRunning() {
        if (mSocket != null && mSocket.isConnected()) {
            try {
                mSocket.close(); //会导致输入和输出流异常
                LogUtil.i(TAG, "finishIfRunning: [socket] close");
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "finishIfRunning: [socket] socket close is error e=" + e);
            }
        }
        mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(MSG_PROCESS_EXITED, 0));
    }
}
