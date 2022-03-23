package com.dunn.instrument;

import android.app.Application;

import com.dunn.instrument.crash.CrashMonitor;
import com.dunn.instrument.thread.ThreadHelp;

/**
 * @ClassName: ApiCrash
 * @Author: ZhuYiDian
 * @CreateDate: 2022/3/20 3:05
 * @Description:
 */
public class ApiThread {
    public static void threadInit(){
        ThreadHelp.monitorThreadInit();
    }

    public static void threadMonitor(){
        ThreadHelp.monitorAllThread();
    }
}
