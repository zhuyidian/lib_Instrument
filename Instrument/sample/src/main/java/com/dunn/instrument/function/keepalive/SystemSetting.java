package com.dunn.instrument.function.keepalive;

import com.dunn.instrument.tools.log.LogUtil;

/**
 * @ClassName: SystemSetting
 * @Author: ZhuYiDian
 * @CreateDate: 2023/4/24 14:03
 * @Description:
 */
public class SystemSetting {
    public static final String TAG = "SkyRMManager";

    public SystemSetting() {
    }

    public int getProcessInfo(String packageName,int commandID){
//        LogUtil.i(TAG, "getProcessInfo: packageName="+packageName+", commandID="+commandID);
        return 1;
    }

    public int setProcessInfo(String packageName, int commandID, int value){
//        LogUtil.i(TAG, "setProcessInfo: packageName="+packageName+", commandID="+commandID+", value="+value);
        return 0;
    }
}
