package com.dunn.instrument.function.keepalive;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.os.Parcel;
import android.util.SparseArray;

import com.dunn.instrument.tools.log.LogUtil;

//import android.util.Log;
/**
 * SkyRMManager 管理TV 的接口
  */

public class SkyRMManager {
    public static final String TAG = "SkyRMManager";
    private static SkyRMManager skyRMManager = null;

    public static synchronized SkyRMManager getInstance() {
        if((skyRMManager == null)) {
            skyRMManager = new SkyRMManager();
        }

        return skyRMManager;
    }

    private SkyRMManager() {
        LogUtil.i(TAG, "SkyRMManager: ");
    }

    public Object getSystemSetting(){
        return new SystemSetting();
    }
}
