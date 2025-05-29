package com.dunn.instrument.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemProperties;

public class CommonUtil {

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
}
