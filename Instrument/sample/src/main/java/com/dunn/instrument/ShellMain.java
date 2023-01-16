package com.dunn.instrument;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Looper;
import android.os.Process;
import android.os.SystemProperties;

import com.dunn.instrument.bean.PkgClsBean;
import com.dunn.instrument.monitor.PerfMonitor;
import com.dunn.instrument.tools.framework.cpu.CpuManager;
import com.dunn.instrument.tools.log.LogUtil;
import com.dunn.instrument.tools.reflect.Reflector;

import java.io.File;

public class ShellMain {
    private static final String TAG = "ShellMain";
    private static String mMonitorPkg = null;
    private static PerfMonitor mMonitor;
    private static long cnt = 0;  //时间计数器

    public static void main(String[] args) {
        for (String arg : args) {
            System.out.println("输入的参数:" + arg);
            mMonitorPkg = arg;
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
        System.out.println("我是在 shell 里运行的！！！:" + contextInstance);
        final Context finalContextInstance = contextInstance;

        new Thread(new Runnable() {
            @Override
            public void run() {
                mMonitor = new PerfMonitor(finalContextInstance, new PerfMonitor.PerfMonitorListener() {
                    @Override
                    public void onCpuInfo(CpuManager.CpuInfo cpuInfo) {
                        if (cpuInfo == null) return;
                        System.out.println("onCpuInfo: cpuRate=" + cpuInfo.cpuRate + "procCpuRate=" + cpuInfo.procCpuRate + ", procNum=" + cpuInfo.procProcessNum +
                                ", threadNum=" + cpuInfo.procThreadNum);
                    }

                    @Override
                    public void onFps(String fps) {
                    }

                    @Override
                    public void onMemInfo(String memInfo) {
                        if (memInfo == null) return;
                        System.out.println("onMemInfo: " + memInfo);
                    }

                    @Override
                    public void onCurrentPkgInfo(PkgClsBean bean) {
                        if (bean == null) return;
                        System.out.println("onCurrentPkgInfo: packageName=" + bean.mPackageName + ", className=" + bean.mClassName);
                    }
                });

                //再次检查是否固定监控某个进程
                String property = SystemProperties.get("third.perf.monitor.pkg", "");
                if (property != null && mMonitorPkg == null) {
                    mMonitorPkg = property;
                    System.out.println("系统prop属性:" + property);
                }

                while (true) {
                    long start = System.currentTimeMillis();
                    String pkg = null;
                    if (mMonitorPkg != null) {
                        if (mMonitor != null) pkg = mMonitor.getSpecifyPkg(mMonitorPkg);
                    } else {
                        if (mMonitor != null) pkg = mMonitor.getCurrentPkg();
                    }
                    System.out.println("monitor packageName:" + pkg);
                    if (pkg != null) {
                        if (mMonitor != null) mMonitor.getCpuInfo(pkg);
                        //每5s获取一次内存
                        if (cnt % 5 == 0) {
                            if (mMonitor != null) mMonitor.getMemInfo(pkg);
                        }
                        cnt++;
                    }
                    try {
                        Thread.sleep(Math.max(1000 * 1 - (System.currentTimeMillis() - start), 0));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        Looper.loop();
    }
}
