package com.dunn.instrument.thread;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: ThreadManager
 * @Author: ZhuYiDian
 * @CreateDate: 2022/4/21 13:56
 * @Description:
 */
public class ThreadHelp {
    private static final String TAG = "ThreadManager";
    //根据压力测试获得最好的最大核心数量
    public static int CPUCOUNT = Runtime.getRuntime().availableProcessors();
    //AsyncTask中使用的
    //private int CPUCOUNT = Runtime.getRuntime().availableProcessors();
    //private int CORE_POOL_SIZE = Math.max(2, Math.min(CPUCOUNT-1, 4));
    //借鉴网上根据压力测试获得最好的最大核心数量
    public static int MAXCOUNT = CPUCOUNT * 4 + 1;
    public static int QUEUESIZE = MAXCOUNT;
    private volatile boolean sIsStart = false; //防止重复初始化

    private final HandlerThread handlerExecutor = new HandlerThread("thread-handler");
    private final Handler mHandler;

    private final ThreadPoolExecutor cpuExecutor = new ThreadPoolExecutor(0,
            MAXCOUNT,
            10,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(QUEUESIZE),
            new CustomThreadFactory("thread-cpu-test"),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    private final ThreadPoolExecutor iOExecutor = new ThreadPoolExecutor(0,
            10,
            10,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(10),
            new CustomThreadFactory("thread-io-test"),
            new ThreadPoolExecutor.DiscardPolicy());

    private static class CustomThreadFactory implements ThreadFactory {
        private String mPrefix = "";
        private final AtomicInteger mThreadIndex;

        public CustomThreadFactory(String prefix) {
            this.mPrefix = prefix;
            this.mThreadIndex = new AtomicInteger(1);
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, mPrefix + "_" + mThreadIndex.getAndIncrement());
        }
    }

    private static class ThreadPoolManagerHolder {
        private static volatile ThreadHelp mInstance = new ThreadHelp();
    }

    private ThreadHelp() {
        handlerExecutor.start();
        mHandler = new Handler(handlerExecutor.getLooper());
    }

    public static ThreadHelp getInstance() {
        return ThreadPoolManagerHolder.mInstance;
    }

    public void init() {
        if (sIsStart) {
            return;
        }
        sIsStart = true;
        //cpu线程池提前创建核心线程数
//        cpuExecutor.allowCoreThreadTimeOut(true);
//        iOExecutor.allowCoreThreadTimeOut(true);
    }

    /**
     * 注：计算型代码、Bitmap转换、Gson转换等，和大多数执行CPU运行的任务使用
     * @param r
     */
    public void cpuExecute(Runnable r) {
        cpuExecutor.execute(r);
    }

    /**
     * 网络请求，io读写，DB读写等任务使用
     * @param r
     */
    public void ioExecute(Runnable r) {
        iOExecutor.execute(r);
    }

    public ThreadPoolExecutor getIoExecuter(){
        return iOExecutor;
    }

    @Deprecated
    public void singleExecute(Runnable r) {
        if (mHandler != null) {
            mHandler.post(r);
        }
    }

    /**
     * 定时任务使用
     * @param r
     * @param delay
     */
    public void singleExecuteDelay(Runnable r, long delay) {
        if (mHandler != null) {
            mHandler.postDelayed(r, delay);
        }
    }

    /**
     * 任务取消
     * @param r
     */
    public void singleExecuteRemove(Runnable r) {
        if (mHandler != null) {
            mHandler.removeCallbacks(r);
        }
    }

    /**
     * 任务队列清除
     */
    public void singleExecuteClear() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
