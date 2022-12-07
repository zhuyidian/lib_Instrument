package com.dunn.instrument.api;

import android.app.Application;
import android.content.Context;




/**
 * @ClassName: ApiCrash
 * @Author: ZhuYiDian
 * @CreateDate: 2022/3/20 3:05
 * @Description:
 */
public class ApiCrash {

    public static void start(Application context){
        //初始化
//        CrashMonitor.getInstance().init(context);
    }

    public static void test(){
        //测试native崩溃
//        CrashMonitor.getInstance().nativeCrashTest();
    }
}
