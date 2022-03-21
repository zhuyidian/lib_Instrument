package com.dunn.instrument;

import android.app.Application;

import com.dunn.instrument.crash.CrashMonitor;
import com.dunn.instrument.methodchoreographer.ChoreographerMonitor;
import com.dunn.instrument.methodchoreographer.SlowMethodMonitor;

/**
 * @ClassName: ApiMethod
 * @Author: ZhuYiDian
 * @CreateDate: 2022/3/20 3:05
 * @Description:
 */
public class ApiMethodChoreographer {
    public static void methodInit(){
        SlowMethodMonitor slowMethodMonitor = new SlowMethodMonitor();
        slowMethodMonitor.start();
    }

    public static void choreographerInit(){
        ChoreographerMonitor choreographerMonitor = new ChoreographerMonitor();
        choreographerMonitor.start();
    }
}
