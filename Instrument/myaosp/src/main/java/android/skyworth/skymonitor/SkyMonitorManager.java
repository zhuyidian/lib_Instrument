package android.skyworth.skymonitor;

import android.skyworth.skymonitor.keepalive.ProcAliveBean;
import android.util.Slog;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: SkyMonitorManager
 * @Author: ZhuYiDian
 * @CreateDate: 2023/2/17 14:43
 * @Description: xxxManager
 */
public class SkyMonitorManager {
    private final static String TAG = "SkyMonitor_Manager";
    private final ISkyMonitorManager mService;
    private final Context mContext;

    public SkyMonitorManager(Context context,ISkyMonitorManager service) {
        mContext = context;
        mService = service;
        Slog.i(TAG, "SkyMonitorManager:");
    }

    public void testMethod() {
        try {
            Slog.i(TAG, "testMethod:");
            mService.testMethod();
        } catch (RemoteException ex) {
            ex.printStackTrace();
            Slog.e(TAG, "testMethod: ex="+ex);
        }
    }

    public void setKeepAliveProc(ProcAliveBean proc) {
        if (proc == null) return;
        try {
            Slog.i(TAG, "setKeepAliveProc: proc="+proc);
            mService.setKeepAliveProc(proc);
        } catch (RemoteException ex) {
            ex.printStackTrace();
            Slog.e(TAG, "setKeepAliveProc: ex="+ex);
        }
    }

    public void setKeepAliveProcList(List<ProcAliveBean> procList) {
        if (procList == null) return;
        try {
            Slog.i(TAG, "setKeepAliveProcList: procList="+procList);
            mService.setKeepAliveProcList(procList);
        } catch (RemoteException ex) {
            ex.printStackTrace();
            Slog.e(TAG, "setKeepAliveProcList: ex="+ex);
        }
    }

    public List<ProcAliveBean> getKeepAliveProcList() {
        try {
            List<ProcAliveBean> procAliveList = mService.getKeepAliveProcList();
            Slog.i(TAG, "getKeepAliveProcList: procAliveList="+procAliveList);
            return procAliveList;
        } catch (RemoteException ex) {
            ex.printStackTrace();
            Slog.e(TAG, "getKeepAliveProcList: ex="+ex);
        }
        return null;
    }

    public boolean isKeepAliveProc(ProcAliveBean proc) {
        boolean result = false;
        if (proc == null) return result;
        try {
            result = mService.isKeepAliveProc(proc);
            Slog.i(TAG, "isKeepAliveProc: result="+result);
        } catch (RemoteException ex) {
            ex.printStackTrace();
            Slog.e(TAG, "isKeepAliveProc: ex="+ex);
        }
        return result;
    }
}
