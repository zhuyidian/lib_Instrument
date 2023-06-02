package com.dunn.instrument.function.keepalive;

import android.content.Context;
import android.os.Process;
import android.os.RemoteException;
import android.skyworth.skymonitor.AppCustom;
import android.skyworth.skymonitor.ISkyResourceCallback;
import android.skyworth.skymonitor.ResourceMsg;
import android.skyworth.skymonitor.SkyMonitorHelper;

import com.dunn.instrument.service.FrameworkInfoService;
import com.dunn.instrument.tools.log.LogUtil;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @ClassName: InterfaceKeepaliveSystem
 * @Author: ZhuYiDian
 * @CreateDate: 2023/5/10 18:44
 * @Description:
 */
public class InterfaceKeepaliveSystem {
    public static final String TAG = "InterfaceKeepaliveSystem";

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
    public SkyMonitorHelper mSkyMonitorHelper;
    public SkyResourceCallback mSkyResourceCallback;
    public FrameworkInfoService.MsgCallback mMsgCallback;

    public InterfaceKeepaliveSystem(Context context) {
        mSkyMonitorHelper = new SkyMonitorHelper(context);
        mSkyResourceCallback = new SkyResourceCallback();
    }

    public void registerCallback(FrameworkInfoService.MsgCallback bc){
        try{
            mMsgCallback = bc;
            mSkyMonitorHelper.register(mSkyResourceCallback, Process.myPid());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void unRegisterCallback(){
        try{
            mSkyMonitorHelper.unregister(mSkyResourceCallback);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class SkyResourceCallback extends ISkyResourceCallback.Stub{
        @Override
        public void notifyMsg(ResourceMsg resourceMsg) throws RemoteException {
            LogUtil.i(TAG, "notifyMsg: resourceMsg="+resourceMsg);
            if(mMsgCallback!=null){
                mMsgCallback.onMsg(resourceMsg);
            }
        }
    }

    public List<AppCustom> getProcessList(){
        try {
            List<AppCustom> appList = mSkyMonitorHelper.getProcessList();
            if(appList==null){
                LogUtil.e(TAG, "getProcessList: appList is null");
                return null;
            }
            for(AppCustom app : appList){
                LogUtil.i(TAG, "getProcessList: pkgname="+app.pkgname+", autoUser="+app.autoUser+", autoallow="+app.autoallow+
                        ", backuser="+app.backuser+", backstrate="+app.backstrate);
            }
            return appList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
//    public int getProcessInfo(String packageName, int commandID) {
//        try {
////            LogUtil.i(TAG, "getProcessInfo: packageName="+packageName+", commandID="+commandID);
//            if (getProcessInfo != null && mSetting != null) {
//                return (int) getProcessInfo.invoke(mSetting,packageName,commandID);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            LogUtil.e(TAG, "getProcessInfo: e="+e);
//        }
//        return PROCESS_CMD_RESULT_ERROR;
//    }

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
            int result = mSkyMonitorHelper.setProcessInfo(packageName,commandID,value);
            LogUtil.i(TAG, "setProcessInfo: packageName="+packageName+", commandID="+commandID+", value="+value+
                    ", result="+result);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "setProcessInfo: e="+e);
        }
        return PROCESS_CMD_RESULT_ERROR;
    }
}
