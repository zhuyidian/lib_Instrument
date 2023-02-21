package com.android.server.skymonitor;

import android.content.Context;
import android.skyworth.skymonitor.keepalive.ProcAliveBean;
import android.util.Slog;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: SkyMonitorService
 * @Author: ZhuYiDian
 * @CreateDate: 2023/2/17 17:24
 * @Description: SkyMonitorService
 */
public class SkyMonitorService extends ISkyMonitorManager.Stub {
    private final static String TAG = "SkyMonitor_Service";
    private final Context mContext;

    public SkyMonitorService(Context context) {
        super();
        mContext = context;
        Slog.i(TAG, "SkyMonitorService: ");
    }

    @Override
    public void testMethod() {
        Slog.i(TAG, "testMethod: ");
    }

    @Override
    public void setKeepAliveProc(ProcAliveBean proc) {
        if (proc == null) return;
        Slog.i(TAG, "setKeepAliveProc: proc=" + proc);
    }

    @Override
    public void setKeepAliveProcList(List<ProcAliveBean> procList) {
        if (procList == null) return;
        Slog.i(TAG, "setKeepAliveProcList: procList=" + procList);
    }

    @Override
    public List<ProcAliveBean> getKeepAliveProcList() {
        List<ProcAliveBean> procAliveList = new ArrayList<>();
        Slog.i(TAG, "getKeepAliveProcList: procAliveList=" + procAliveList);
        return procAliveList;
    }

    @Override
    public boolean isKeepAliveProc(ProcAliveBean proc) {
        boolean result = false;
        if (proc == null) return result;

        if("test".equals(proc.getPackageName())){
            result = true;
        }
        Slog.i(TAG, "isKeepAliveProc: result=" + result);
        return result;
    }
}
