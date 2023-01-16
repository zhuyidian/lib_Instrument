package com.dunn.instrument.shell;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.dunn.instrument.tools.log.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientConnect {
    private static final String TAG = "ClientConnect";
    //private static final String LocalSocketName = "coocaa_guard_ctrl_cmd";
    private static final String LocalSocketNameCMD = "coocaa_guard_ctrl_cmd";
    private static final String LocalSocketNameSOCK = "/data/ccos/guard.sock";
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
    private LocalSocket client = null;  //coocaa_guard回复ok后就会关闭，不会保持长连接
    private Socket socket;
    final Handler mMainThreadHandler = new Handler(Looper.getMainLooper()) {
        final byte[] mReceiveBuffer = new byte[4 * 1024];
        @Override
        public void handleMessage(Message msg) {
            int bytesRead = mProcessToTerminalIOQueue.read(mReceiveBuffer, false);
            //Log.d(TAG, "handleMessage: MSG_NEW_INPUT is true or false: " + (msg.what == MSG_NEW_INPUT));
            if (bytesRead > 0) {
                //mEmulator.append(mReceiveBuffer, bytesRead);
                byte[] array = new byte[bytesRead];
                System.arraycopy(mReceiveBuffer, 0, array, 0, bytesRead);

                //Log.d(TAG, "handleMessage: length: "+ bytesRead + " string: " + new String(array, StandardCharsets.UTF_8));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    LogUtil.i(TAG,"handleMessage: KITKAT notify text=" + (new String(array, StandardCharsets.UTF_8)));
                    notifyScreenUpdate(new String(array, StandardCharsets.UTF_8));
                } else {
                    LogUtil.i(TAG,"handleMessage: notify text=" + (new String(array, StandardCharsets.UTF_8)));
                    notifyScreenUpdate(new String(array, StandardCharsets.UTF_8));
                }
            }

            if (msg.what == MSG_PROCESS_EXITED) {
                int exitCode = (Integer) msg.obj;
                LogUtil.i(TAG,"handleMessage: MSG_PROCESS_EXITED exitCode=" + exitCode);
                cleanupResources(exitCode);
                String exitDescription = "SocketDisconnect" + exitCode;
                if (exitCode == -1) {
                    exitDescription = exitDescription +
                            " \r\ncoocaa_guard or /system(/vendor)/bin/skybusybox/busybox  are not found" +
                            " \r\nnormal shell will be instead of root shell" +
                            " \r\nsome commands may not be permitted\r\n";
                }
                mChangeCallback.onSessionFinished(exitDescription);
                notifyScreenUpdate(exitDescription);
            }
        }
    };

    public ClientConnect(final SessionChangedCallback mChangeCallback) {
        this.mChangeCallback = mChangeCallback;
    }

    public boolean connect() {
        try {
            File file = new File("/data/ccos/guard.sock");
            client = new LocalSocket();
            //client.connect(new LocalSocketAddress(LocalSocketName));
            if (file.exists()) {
                LogUtil.i(TAG,"connect:LocalSocketNameSOCK");
                client.connect(new LocalSocketAddress(LocalSocketNameSOCK,
                        LocalSocketAddress.Namespace.FILESYSTEM));
            } else {
                LogUtil.i(TAG,"connect:LocalSocketNameCMD");
                client.connect(new LocalSocketAddress(LocalSocketNameCMD,
                        LocalSocketAddress.Namespace.ABSTRACT));
            }
            LogUtil.i(TAG,"connect:success");
            //Client.setSoTimeout(timeout);
        } catch (IOException e) {
            LogUtil.e(TAG,"connect: fail e="+e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void send(String data) {
        try (OutputStream outputStream = client.getOutputStream();
             InputStream inputStream = client.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            LogUtil.i(TAG,"send: connect telnet server data=" + data);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                outputStream.write(data.getBytes(StandardCharsets.UTF_8));
            } else {
                outputStream.write(data.getBytes(StandardCharsets.UTF_8));
            }
            outputStream.flush();

            String result = bufferedReader.readLine();
            LogUtil.i(TAG,"send: result: " + result);
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
            LogUtil.i(TAG,"send: connect telnet server e=" + e);
        }
        close();
    }

    private void close() {
        //任意一个流关闭都会导致连接关闭
        try {
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectTelnetServer() {
        try {
            //创建一个无连接的Socket
            socket = new Socket();
            //连接到指定的IP和端口号，并指定1s的超时时间
            socket.connect(new InetSocketAddress(telnetHostName, telnetHostPort), 1000);
            //socket = new Socket("127.0.0.1", 4149);
            LogUtil.i(TAG,"connectTelnetServer: socket isConnected=" + socket.isConnected());
            if (socket.isConnected()) {
                new Thread("TermSessionInputReader") {
                    @Override
                    public void run() {
                        try (InputStream termIn = socket.getInputStream()) {
                            final byte[] buffer = new byte[4096];
                            boolean isFirst = true;
                            while (true) {
                                int read = termIn.read(buffer);
                                LogUtil.i(TAG,"connectTelnetServer: socket read...... len=" + read);
                                //某些设备telnet会找不到可用pty
                                //第一次读返回-1，当做telnet打开失败
                                if (read == -1) {
                                    if (isFirst) {
                                        mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(MSG_PROCESS_EXITED, -1));
                                    } else {  //长时间连接后可能出现的失败
                                        mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(MSG_PROCESS_EXITED, 0));
                                    }
                                    return;
                                }
                                isFirst = false;
                                if (!mProcessToTerminalIOQueue.write(buffer, 0, read)) return;
                                mMainThreadHandler.sendEmptyMessage(MSG_NEW_INPUT);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtil.i(TAG,"connectTelnetServer: read e=" + e);
                        }
                    }
                }.start();

                new Thread("TermSessionOutputWriter") {
                    @Override
                    public void run() {
                        final byte[] buffer = new byte[4096];
                        try (OutputStream termOut = socket.getOutputStream()) {
                            while (true) {
                                int bytesToWrite = mTerminalToProcessIOQueue.read(buffer, true);
                                LogUtil.i(TAG,"connectTelnetServer: socket write...... len=" + bytesToWrite);
                                if (bytesToWrite == -1) return;
                                //Log.d(TAG, "run: TermSessionOutputWriter length: "+bytesToWrite);
                                //Log.d(TAG, "run TermSessionOutputWriter: " + new String(buffer, StandardCharsets.UTF_8));
                                termOut.write(buffer, 0, bytesToWrite);
                                termOut.flush();
                            }
                        } catch (Exception e) {
                            // Ignore, just shutting down.
                            //由读线程监测异常，避免重复发消息
                            LogUtil.i(TAG,"connectTelnetServer: write e=" + e);
                        }
                    }
                }.start();

                mChangeCallback.onSessionStart();
            } else {
                LogUtil.i(TAG,"connectTelnetServer: socket connected is error!!!!!");
                mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(MSG_PROCESS_EXITED, -1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i(TAG,"connectTelnetServer: e=" + e);
            mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(MSG_PROCESS_EXITED, -1));
        }
    }

    /**
     * Write a string using the UTF-8 encoding to the terminal client.
     */
    public final void write(String data) {
        LogUtil.i(TAG,"write: data=" + data);
        byte[] bytes;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            bytes = data.getBytes(StandardCharsets.UTF_8);
        } else {
            bytes = data.getBytes(StandardCharsets.UTF_8);
        }
        write(bytes, 0, bytes.length);
    }

    public void write(byte[] data, int offset, int count) {
        if (socket != null && socket.isConnected()) {
            mTerminalToProcessIOQueue.write(data, offset, count);
        }
    }

    private void notifyScreenUpdate(String text) {
        mChangeCallback.onTextChanged(text);
    }

    private void cleanupResources(int exitStatus) {
        // Stop the reader and writer threads, and close the I/O streams
        mTerminalToProcessIOQueue.close();
        mProcessToTerminalIOQueue.close();
        try {
            socket.close(); //会导致输入和输出流异常
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void finishIfRunning() {
        if (socket != null && socket.isConnected()) {
            try {
                socket.close(); //会导致输入和输出流异常
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(MSG_PROCESS_EXITED, 0));
    }

    public interface SessionChangedCallback {
        void onSessionStart();

        void onSessionFinished(String text);

        void onTextChanged(String text);
    }
}
