package com.dunn.instrument.excel;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: ExcelHelp
 * @Author: ZhuYiDian
 * @CreateDate: 2022/3/8 17:48
 * @Description:
 */
public class ExcelHelp implements Runnable {
    private static final String TAG = "ExcelHelp";
    private static volatile ExcelHelp sInstance;
    private final BlockingQueue<ExcelTask> mQueue;
    private volatile boolean isRunning;
    private ExcelDeal mExcelDeal;
    private ExcelInfo mInfo = new ExcelInfo();

    private ExcelHelp() {
        mQueue = new LinkedBlockingQueue<>();
    }

    public static ExcelHelp getInstance() {
        if (sInstance == null) {
            synchronized (ExcelHelp.class) {
                if (sInstance == null) {
                    sInstance = new ExcelHelp();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        mExcelDeal = new ExcelDeal(context);
//        mExcelDeal.delExcel();
    }

    public ExcelInfo getInfo() {
        return mInfo;
    }

    public void submit() {
        synchronized (mInfo) {
            ExcelInfo infoTask = new ExcelInfo(mInfo);
            submitTask(new ExcelTask(mExcelDeal, infoTask));
        }
    }

    private void submitTask(ExcelTask task) {
        boolean success = false;
        try {
            success = mQueue.add(task);
        } catch (Exception e) {
            Log.e(TAG, "submitTask: e=" + e.getMessage());
        }
        if (success && !isRunning) {
            isRunning = true;
            AsyncTask.SERIAL_EXECUTOR.execute(this);
        }
    }

    @Override
    public void run() {
        ExcelTask task;
        while (isRunning) {
            try {
                task = mQueue.poll(100, TimeUnit.MILLISECONDS);
                if (task != null) {
                    task.submit();
                } else {
                    isRunning = false;
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "run: e=" + e.getMessage());
            }
        }
    }

    private static class ExcelTask {
        private ExcelDeal mDeal;
        private ExcelInfo mInfo;

        ExcelTask(ExcelDeal deal, ExcelInfo info) {
            this.mDeal = deal;
            this.mInfo = info;
        }

        void submit() {
            if (mDeal != null && mInfo != null) {
                mDeal.updateExcel(mInfo);
            }
        }
    }
}
