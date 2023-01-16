package com.dunn.instrument;

import android.content.Context;
import android.os.Looper;

import com.dunn.instrument.bean.PkgClsBean;
import com.dunn.instrument.monitor.PerfMonitor;
import com.dunn.instrument.tools.framework.cpu.CpuManager;
import com.dunn.instrument.tools.reflect.Reflector;

public class ShellProc {
    private static final String TAG = "ShellProc";

    public static void main(String[] args) {
        for (String arg : args) {
            System.out.println("输入的参数:" + arg);
        }
        Looper.prepareMainLooper();
        Class<?> ActivityThread = null;
        try {
            ActivityThread = Class.forName("android.app.ActivityThread");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Context contextInstance = null;
        try {
            Object object = Reflector.on("android.app.ActivityThread").method("systemMain").call();
            contextInstance = Reflector.on("android.app.ContextImpl").method("createSystemContext", ActivityThread).call(object);
        } catch (Reflector.ReflectedException e) {
            e.printStackTrace();
        }
        System.out.println("ShellProc 我是在 shell 里运行的！！！:" + contextInstance);
        final Context finalContextInstance = contextInstance;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("ShellProc running ......");
                }
            }
        }).start();
        Looper.loop();
    }
}
