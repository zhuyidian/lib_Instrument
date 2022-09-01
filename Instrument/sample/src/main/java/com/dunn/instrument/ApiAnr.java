package com.dunn.instrument;

import android.content.Context;

import com.dunn.instrument.anr.watchdog.ANRWatchDog;
import com.dunn.instrument.methodmonitor.choreographer.ChoreographerMonitor;
import com.dunn.instrument.methodmonitor.slowmethod.SlowMethodMonitor;


/**
 * @ClassName: ApiAnr
 * @Author: ZhuYiDian
 * @CreateDate: 2022/3/20 3:05
 * @Description:
 */
public class ApiAnr {

    public static void start(Context context){
        ANRWatchDog anrWatchDog = new ANRWatchDog(context);
        anrWatchDog.start();
    }

}
