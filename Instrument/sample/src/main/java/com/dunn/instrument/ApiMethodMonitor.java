package com.dunn.instrument;

import android.app.Application;

import com.dunn.instrument.methodmonitor.choreographer.ChoreographerMonitor;
import com.dunn.instrument.methodmonitor.slowmethod.SlowMethodMonitor;


/**
 * @ClassName: ApiMethod
 * @Author: ZhuYiDian
 * @CreateDate: 2022/3/20 3:05
 * @Description:
 */
public class ApiMethodMonitor {

    public static void start(){
        methodInit();
        choreographerInit();
    }

    public static void methodInit(){
        SlowMethodMonitor slowMethodMonitor = new SlowMethodMonitor();
        slowMethodMonitor.start();
    }

    public static void choreographerInit(){
        ChoreographerMonitor choreographerMonitor = new ChoreographerMonitor();
        choreographerMonitor.start();
    }
}
