// ISkyMonitorManager.aidl
package android.skyworth.skymonitor;
import android.skyworth.skymonitor.keepalive.ProcAliveBean;

interface ISkyMonitorManager {
    //test method
    void testMethod();

    //WhiteList method
    void setKeepAliveProc(in ProcAliveBean proc);
    void setKeepAliveProcList(in List<ProcAliveBean> procList);
    List<ProcAliveBean> getKeepAliveProcList();
    boolean isKeepAliveProc(in ProcAliveBean proc);
}
