package com.dunn.instrument;

import android.app.Application;
import android.content.Context;

import com.dunn.instrument.anr.watchdog.ANRWatchDog;
import com.dunn.instrument.crash.CrashMonitor;

/**
 * @ClassName: ApiAnrWatchDog
 * @Author: ZhuYiDian
 * @CreateDate: 2022/3/20 3:05
 * @Description:
 */
public class ApiAnrWatchDog {
    public static void anrInit(Context context){
        ANRWatchDog anrWatchDog = new ANRWatchDog(context);
        anrWatchDog.start();
    }
}
