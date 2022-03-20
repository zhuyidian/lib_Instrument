package com.dunn.instrument;

import android.app.Application;
import android.content.Context;

import com.dunn.instrument.crash.CrashMonitor;

/**
 * @ClassName: ApiCrash
 * @Author: ZhuYiDian
 * @CreateDate: 2022/3/20 3:05
 * @Description:
 */
public class ApiCrash {
    public static void crashInit(Application application){
        // 在首页的空闲时候触发
        CrashMonitor.getInstance().init(application);
    }
}
