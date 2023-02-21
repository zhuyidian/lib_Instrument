package com.dunn.instrument.monitor;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.text.TextUtils;

import com.dunn.instrument.bean.PkgClsBean;
import com.dunn.instrument.tools.framework.cpu.CpuManager;
import com.dunn.instrument.tools.framework.fps.FpsInfo;
import com.dunn.instrument.tools.framework.fps.GetFpsUtils;
import com.dunn.instrument.tools.framework.ram.MemManager;
import com.dunn.instrument.tools.thread.ThreadManager;

import java.text.DecimalFormat;

/**
 * @ClassName: PerfMonitor
 * @Author: ZhuYiDian
 * @CreateDate: 2023/1/16 14:40
 * @Description:
 */
public class PerfMonitor {
    private static final String TAG = "PerfMonitor";
    private Context mContext;
    private ActivityManager mActivityManager;
    private PerfMonitorListener mMonitorListener;

    public PerfMonitor(Context context, PerfMonitorListener listener) {
        mContext = context;
        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        mMonitorListener = listener;
        CpuManager.getInstance().init(mContext);
        MemManager.getInstance().init(mContext);
    }

    public void destory() {
        mContext = null;
        mActivityManager = null;
        mMonitorListener = null;
    }

    public String getCurrentPkg() {
        String pkg = SystemProperties.get("sky.current.apk", "");
        String cls = SystemProperties.get("sky.current.actname", "");
        if (TextUtils.isEmpty(pkg)) {
            ComponentName cn = mActivityManager.getRunningTasks(1).get(0).topActivity;
            pkg = cn.getPackageName();
            cls = cn.getClassName();
        }

        PkgClsBean bean = new PkgClsBean(pkg, cls);
        if (mMonitorListener != null) {
            mMonitorListener.onCurrentPkgInfo(bean);
        }

        return pkg;
    }

    public String getSpecifyPkg(String pkg){
        PkgClsBean bean = new PkgClsBean(pkg, null);
        if (mMonitorListener != null) {
            mMonitorListener.onCurrentPkgInfo(bean);
        }
        return pkg;
    }

    public void getCpuInfo(String pkg) {
        CpuManager.CpuInfo mCpuInfo = CpuManager.getInstance().getCpuInfo(pkg);
        if (mMonitorListener != null) {
            mMonitorListener.onCpuInfo(mCpuInfo);
        }
    }

    public void getFps(String pkg) {
        StringBuilder text = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        double[] info = GetFpsUtils.getInfo(pkg);
        double fps1 = info[0];
        double ss = info[2];
        double jumpingFrames = info[1];
        double totalFrame = info[3];
        long maxFrameTime = (long) info[4];
        int stuckSum2 = GetFpsUtils.getStuckSum2();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            text.append("").append(fps1)
                    .append(" 总帧数：").append(totalFrame)   //间隔内获取到的所有帧数
                    .append(" 最大帧时长：").append(maxFrameTime)  //最大桢时长
                    .append(" 掉帧数：").append(jumpingFrames)  //大于16.67的帧数
                    .append(" 卡顿次数：").append(stuckSum2)
                    .append(" 流畅分：").append(decimalFormat.format(ss));
        } else {
            float fps = FpsInfo.fps();
            text.append("").append(fps);
        }
        if (mMonitorListener != null) {
            mMonitorListener.onFps(String.valueOf(text));
        }
    }

    public void getMemInfo(String pkg) {
        ThreadManager.getInstance().ioThread(new MemRunnable(pkg));
    }

    private class MemRunnable implements Runnable {
        String mPkg;

        public MemRunnable(String pkg) {
            mPkg = pkg;
        }

        @Override
        public void run() {
            MemManager.ProcMemInfo mProcMemInfo = MemManager.getInstance().getProcMemInfo(mPkg);
            if (mMonitorListener != null) {
                mMonitorListener.onMemInfo(mProcMemInfo.totalPss + " MB");
            }
        }
    }

    public interface PerfMonitorListener {
        // 通话消息回调
        void onCpuInfo(CpuManager.CpuInfo cpuInfo);

        void onFps(String fps);

        void onMemInfo(String memInfo);

        void onCurrentPkgInfo(PkgClsBean bean);
    }
}
