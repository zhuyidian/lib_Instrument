package com.dunn.instrument.function.keepalive;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName: InterfaceKeepalive
 * @Author: ZhuYiDian
 * @CreateDate: 2023/4/23 18:44
 * @Description:
 */
public class InterfaceKeepalive {
    //class
    private Class SkyAppManager = null;

    //method
    private Method getInstance = null;
    private Method getProcessInfo = null;
    private Method setProcessInfo = null;

    //object
    private Object mSkyAppManager = null;

    public InterfaceKeepalive() {
        try {
            SkyAppManager = Class.forName("com.skyworth.android.skyrmapi.SkyRMManager");
            getInstance = SkyAppManager.getMethod("getInstance");
            getProcessInfo = SkyAppManager.getMethod("getProcessInfo", String.class, int.class);
            setProcessInfo = SkyAppManager.getMethod("setProcessInfo", String.class, int.class, int.class);
            mSkyAppManager = getInstance.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getProcessInfo(String packageName, int commandID) {
        try {
            if (getProcessInfo != null && mSkyAppManager != null) {
                return (int) getProcessInfo.invoke(mSkyAppManager,packageName,commandID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int setProcessInfo(String packageName, int commandID, int value) {
        try {
            if (setProcessInfo != null && mSkyAppManager != null) {
                return (int) setProcessInfo.invoke(mSkyAppManager,packageName,commandID,value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
