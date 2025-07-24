package com.dunn.demo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

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

    public static String getFactoryMacAddresses(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            try {
                Class<?> clazz = WifiManager.class;
                Method method = clazz.getDeclaredMethod("getFactoryMacAddresses");
                String[] macArray = (String[]) method.invoke(wifiManager);
                Log.d(TAG,"getFactoryMacAddresses: macArray="+macArray);
                if (macArray != null && macArray.length > 0) {
                    String mac = macArray[0].replace(":", "");
                    Log.d(TAG,"getFactoryMacAddresses: mac="+mac);
                    return mac.toUpperCase();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getNetworkType(Context context){
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkCapabilities nc = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (nc == null) {
                    Log.d(TAG,"getNetworkType: DISCONNECTED");
                    return "DISCONNECTED";
                }

                if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.d(TAG,"getNetworkType: Wi-Fi");
                    return "Wi-Fi";
                } else if (nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.d(TAG,"getNetworkType: RJ45");
                    return "RJ45";
                } else if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return getCellularNetworkType(context); // 蜂窝网络细分
                }
                Log.d(TAG,"getNetworkType: UNKNOWN");
                return "UNKNOWN";
            }else{
                Log.d(TAG,"getNetworkType: SDK is other UNKNOWN");
                return "UNKNOWN";
            }
        }catch (Exception e){
            Log.e(TAG, "getNetworkType: e="+e);
        }
        return "UNKNOWN";
    }

    @SuppressLint("MissingPermission")
    private static String getCellularNetworkType(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            switch (tm.getNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_NR:
                    Log.d(TAG,"getCellularNetworkType: 5G");
                    return "5G";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    Log.d(TAG,"getCellularNetworkType: 4G");
                    return "4G";
//                case TelephonyManager.NETWORK_TYPE_NB_IOT:
//                    return "NB-IoT";
                default:
                    Log.d(TAG,"getCellularNetworkType: 3G/2G");
                    return "3G/2G";
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "getCellularNetworkType: e="+e);
        }
        return "UNKNOWN";
    }

    private static LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            if(location==null){
                return;
            }
            double lat = location.getLatitude();     //经度
            double lng = location.getLongitude(); //纬度
            double altitude =  location.getAltitude();     //海拔
            Log.d(TAG,"onLocationChanged lat="+lat+",lng="+lng+",altitude="+altitude);
        }
    };
    public static String getDeviceLocation(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(false);

        // 获取最佳定位提供者
        String provider = lm.getBestProvider(criteria, true);
        Log.d(TAG,"getDeviceLocation: provider="+provider);
        if (provider == null) {
            Log.d(TAG,"getDeviceLocation: 0,0,0");
            return "0,0,0";  // 无定位能力:ml-citation{ref="1" data="citationList"}
        }

        try {
            //lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, mLocationListener);
            Location location = lm.getLastKnownLocation(provider);
            //Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.d(TAG,"getDeviceLocation: location="+location);
            if (location == null) {
                Log.d(TAG,"getDeviceLocation: 0,0,0");
                return "0,0,0";
            }

            // 解析经纬度
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            // 获取定位类型
            int locationType = getLocationType(context, lm, location);
            Log.d(TAG,"getDeviceLocation: "+longitude + "," + latitude + "," + locationType);
            return longitude + "," + latitude + "," + locationType;

        } catch (SecurityException e) {
            return "0,0,0";  // 权限不足
        }
    }

    // 定位类型位掩码定义
    private static final int GPS = 1;          // GPS卫星
    private static final int BEIDOU = 2;       // 北斗卫星
    private static final int GALILEO = 4;      // 伽利略卫星
    private static final int GLONASS = 8;      // 格洛纳斯卫星
    private static final int CELL = 16;        // 基站定位
    private static final int WIFI = 32;        // WiFi定位
    @SuppressLint("MissingPermission")
    private static int getLocationType(Context context, LocationManager lm, Location location) {
        int type = 0;

        // 卫星定位检测
        /*
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //GnssStatus status = lm.getGnssStatus(null);
                // 需要动态申请权限（Android 10+）
                GnssStatus.Callback callback = new GnssStatus.Callback() {
                    @Override
                    public void onSatelliteStatusChanged(GnssStatus status) {
                        // 实时获取卫星状态:ml-citation{ref="3,6" data="citationList"}
                        int type_back = 0;
                        for (int i = 0; i < status.getSatelliteCount(); i++) {
                            switch (status.getConstellationType(i)) {
                                case GnssStatus.CONSTELLATION_GPS:
                                    type_back |= GPS;
                                    break;
                                case GnssStatus.CONSTELLATION_BEIDOU:
                                    type_back |= BEIDOU;
                                    break;
                                case GnssStatus.CONSTELLATION_GALILEO:
                                    type_back |= GALILEO;
                                    break;
                                case GnssStatus.CONSTELLATION_GLONASS:
                                    type_back |= GLONASS;
                                    break;
                            }
                        }
                        Log.d(TAG,"getLocationType: onSatelliteStatusChanged type_back="+type_back);
                    }
                };
                lm.registerGnssStatusCallback(callback, new Handler(Looper.getMainLooper()));
            } else {
                type = GPS;  // 默认GPS（旧版本无法区分卫星类型）:ml-citation{ref="1,4" data="citationList"}
            }
        }
        */
        // 网络定位检测
        if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkCapabilities nc = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                    type |= CELL;
                if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    type |= WIFI;
            }
        }
        Log.d(TAG,"getLocationType: type="+type);
        return (type == 0) ? 0 : type;  // 未识别类型返回0
    }

    public static int getWiFiRssi(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int initialRssi = wifiManager.getConnectionInfo().getRssi();

        return initialRssi;
    }
}
