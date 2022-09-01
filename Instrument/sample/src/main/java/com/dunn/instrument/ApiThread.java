package com.dunn.instrument;

import android.app.Application;


/**
 * @ClassName: ApiCrash
 * @Author: ZhuYiDian
 * @CreateDate: 2022/3/20 3:05
 * @Description:
 */
public class ApiThread {
    public static void threadInit(){
        //开始线程创建销毁监控，建议在APP启动的时候就去监听
//        ThreadHookSrcHelp.monitorThreadCreate();
        //初始化，建议在APP启动的时候就去初始化
//        ThreadHelp.monitorThreadInit();
    }

    public static void threadMonitor(){
        //开始一次监控，建议在用了一会之后，去获取一次。然后用一会儿再去获取一次
//        ThreadHelp.monitorAllThread();
    }
}
