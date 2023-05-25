package com.dunn.instrument.function.keepalive;

import android.util.Log;

import com.dunn.instrument.tools.log.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName: InterfaceKeepalive
 * @Author: ZhuYiDian
 * @CreateDate: 2023/4/23 18:44
 * @Description:
 */
public class InterfaceKeepalive {
    public static final String TAG = "InterfaceKeepalive";

    //cmd类型
    public static final int PROCESS_CMD_AUTO_START = 0;   //自启动
    public static final int PROCESS_CMD_BACKGROUND = 1;   //后台运行
    //是否允许启动
    public static final int PROCESS_CMD_AUTO_START_NOT_ALLOW = 0;   //不允许自启动
    public static final int PROCESS_CMD_AUTO_START_ALLOW = 1;   //允许自启动
    //后台运行策略
    public static final int PROCESS_CMD_BACKGROUND_SMART = 0; //后台智能调控
    public static final int PROCESS_CMD_BACKGROUND_LIMIT = 1; //限制后台运行
    public static final int PROCESS_CMD_BACKGROUND_KEEP = 2; //保持后台运行
    //返回错误
    public static final int PROCESS_CMD_RESULT_ERROR = -1;

    //class
    private Class SkyAppManager = null;

    //method
    private Method getInstance = null;
    private Method getSystemSetting = null;
    private Method getProcessInfo = null;
    private Method setProcessInfo = null;

    //object
    private Object mSkyAppManager = null;
    private Object mSetting = null;

    public InterfaceKeepalive() {
        try {
            LogUtil.i(TAG, "InterfaceKeepalive: ");
            getInterfaceInfo();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "InterfaceKeepalive: e="+e);
        }
    }

    /**
     * 方法说明: 获取应用的具体选项
     * @param packageName: 应用的包名
     * @param commandID: 获取启动或后台运行选项
     *                 0: 获取启动选项的cmd
     *                 1: 获取后台运行选项的cmd
     * @return: 返回具体的选项值
     *          启动选项的返回值: 0(不允许启动) 1(允许启动)
     *          后台运行选项的返回值: 0(智能调控) 1(限制运行) 2(保持运行)
     */
    public int getProcessInfo(String packageName, int commandID) {
        try {
//            LogUtil.i(TAG, "getProcessInfo: packageName="+packageName+", commandID="+commandID);
            if (getProcessInfo != null && mSetting != null) {
                return (int) getProcessInfo.invoke(mSetting,packageName,commandID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "getProcessInfo: e="+e);
        }
        return PROCESS_CMD_RESULT_ERROR;
    }

    /**
     * 方法说明: 设置应用的具体选项
     * @param packageName: 应用的包名
     * @param commandID: 设置启动或后台运行选项
     *                 0: 设置启动选项的cmd
     *                 1: 设置后台运行选项的cmd
     * @param value: 设置的具体选项值
     *             设置的是启动选项: 0(不允许启动) 1(允许启动)
     *             设置的是后台运行选项: 0(智能调控) 1(限制运行) 2(保持运行)
     * @return: 返回设置结果
     *          0: 设置成功
     *          -1: 设置失败
     */
    public int setProcessInfo(String packageName, int commandID, int value) {
        try {
//            LogUtil.i(TAG, "setProcessInfo: packageName="+packageName+", commandID="+commandID+", value="+value);
            if (setProcessInfo != null && mSetting != null) {
                return (int) setProcessInfo.invoke(mSetting,packageName,commandID,value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "setProcessInfo: e="+e);
        }
        return PROCESS_CMD_RESULT_ERROR;
    }

    private void getInterfaceInfo() throws Exception{
        SkyAppManager = Class.forName("com.skyworth.android.skyrmapi.SkyRMManager");
        getInstance = SkyAppManager.getMethod("getInstance");
        mSkyAppManager = getInstance.invoke(null);

        getSystemSetting = SkyAppManager.getMethod("getSystemSetting");
        mSetting = getSystemSetting.invoke(mSkyAppManager);

        getProcessInfo = mSetting.getClass().getMethod("getProcessInfo", String.class, int.class);
        setProcessInfo = mSetting.getClass().getMethod("setProcessInfo", String.class, int.class, int.class);
        LogUtil.e(TAG, "getInterfaceInfo: SkyAppManager="+SkyAppManager+", getInstance="+getInstance+", getSystemSetting="+getSystemSetting+
                ", mSetting="+mSetting+", getProcessInfo="+getProcessInfo+", setProcessInfo="+setProcessInfo);
    }
}
