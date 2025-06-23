package com.dunn.instrument.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.SystemProperties;
import android.util.Log;

public class CommonUtil {
    private static String TAG = "CommonUtil";

    public static String getSkyMid(){
        return SystemProperties.get("ro.build.skymid", "");
    }

    public static String getSkyModel(){
        return SystemProperties.get("ro.build.skymodel", "");
    }

    public static String getSkyType(){
        return SystemProperties.get("ro.build.skytype", "");
    }

    public static String getAndroidVer(){
        return SystemProperties.get("ro.build.version.release", "");
    }

    public static String getBinStatus(){
        return SystemProperties.get("dev.performance.status","0");
    }

    public static void setBinStatus(String status){
        SystemProperties.set("dev.performance.status", status);
    }

    public static void setBinStop(String stop){
        SystemProperties.set("dev.performance.stop", stop);
    }

    public static String getBinStop(){
        return SystemProperties.get("dev.performance.stop","0");
    }

    public static void setBinSocket(String socket){
        SystemProperties.set("dev.performance.socket", socket);
    }

    public static String getBinSocket(){
        return SystemProperties.get("dev.performance.socket","0");
    }

    public static void saveAppVersionToSp(Context context,String spName,String key,String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply(); // apply()为异步操作，commit()为同步操作
    }

    public static String getAppVersionFromSp(Context context,String spName,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, "");
        return value;
    }

    public static int getBatteryLevel(Context context) {
        try {
            BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            if (batteryManager == null) {
                Log.e(TAG,"getBatteryLevel: batteryManager is null battery=50");
                return 50;
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                Log.d(TAG,"getBatteryLevel: battery="+battery);
            }else{
                Log.d(TAG,"getBatteryLevel: battery=50, android.os.Build.VERSION.SDK_INT="+android.os.Build.VERSION.SDK_INT);
                return 50;
            }
        }catch (Exception e){
            Log.e(TAG, "getBatteryLevel: e="+e);
        }
        return 50;
    }

    public static String getBatteryStatus(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, filter);

        if (batteryStatus == null) return "未知";

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return "充电中";
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return "放电中";
            case BatteryManager.BATTERY_STATUS_FULL:
                return "已充满";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return "未充电";
            default:
                return "未知";
        }
    }
}
