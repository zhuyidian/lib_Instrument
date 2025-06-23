package com.dunn.instrument.utils;

import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.UserHandle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内部存储工具类
 */
public class StorageUtil {
    public final static String TAG = "StorageUtil";
    private static volatile StorageUtil mStorageUtil;
    private StorageEntity mStorageEntity;
    private final AtomicBoolean mIsLoading;
    private final List<OnLoadListener> listeners;

    private StorageUtil() {
        mIsLoading = new AtomicBoolean();
        listeners = new ArrayList<>();
    }

    public static StorageUtil getInstance() {
        if (mStorageUtil == null) {
            synchronized (StorageUtil.class) {
                if (mStorageUtil == null) {
                    mStorageUtil = new StorageUtil();
                }
            }
        }
        return mStorageUtil;
    }

    public void clearCache() {
        mStorageEntity = null;
    }

    public void getStorageTotalSizeTest(){
        File data = Environment.getDataDirectory();
        long dataSize = data.getTotalSpace();
        // 1 GB = 1024 * 1024 * 1024 bytes = 1,073,741,824 bytes
        final long BYTES_PER_GB = 1024L * 1024 * 1024;
        double gb = (double) dataSize / BYTES_PER_GB;
        Log.d(TAG,"getStorageTotalSizeTest: dataSize="+dataSize+", gb="+gb);

        //>64GB <128GB = 128GB
        //>128GB <256GB = 256GB
        //>32GB <64GB  = 64GB
        //>256GB < 512GB = 512GB
        if(gb>256d && gb<512d){
            Log.d(TAG,"getStorageTotalSizeTest: 512 GB");
        }else if(gb>128d && gb<256d){
            Log.d(TAG,"getStorageTotalSizeTest: 256 GB");
        }else if(gb>64d && gb<128d){
            Log.d(TAG,"getStorageTotalSizeTest: 128 GB");
        }else if(gb>32d && gb<64d){
            Log.d(TAG,"getStorageTotalSizeTest: 64 GB");
        }else if(gb>16d && gb<32d){
            Log.d(TAG,"getStorageTotalSizeTest: 32 GB");
        }else if(gb>8d && gb<16d){
            Log.d(TAG,"getStorageTotalSizeTest: 16 GB");
        }else{
            Log.d(TAG,"getStorageTotalSizeTest: 8 GB");
        }
    }

    public void queryStorage(Context context, boolean fromCache, OnLoadListener listener) {
        if (mStorageEntity != null && fromCache) {
            if (listener != null) {
                listener.onLoadState(mStorageEntity);
            }
            return;
        }
        if (listener != null) {
            listeners.add(listener);
            listener.onLoadState(null);
        }
        if (mIsLoading.get()) {
            return;
        }
        mIsLoading.set(true);
        queryAppStorageTotalSize(context);
    }

    /**
     * 获取应用统计大小
     */
    private void queryAppStorageTotalSize(Context context) {
        List<ApplicationInfo> applications =
                context.getPackageManager().getInstalledApplications(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            calculateAppStorageGEO(context, applications);
        } else {//8.0以下
            calculateAppStorageLessO(context, applications);
        }
    }

    /**
     * Android版本大于o
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculateAppStorageGEO(Context context, List<ApplicationInfo> applications) {
        StorageStatsManager manager = (StorageStatsManager)
                context.getSystemService(Context.STORAGE_STATS_SERVICE);
        long start = System.currentTimeMillis();
        Log.d(TAG, "calculateAppStorageGEO size=" + applications.size());
        StorageStats storageStats;
        long appsTotalSize = 0;
        for (ApplicationInfo info : applications) {
            try {
                storageStats = manager.queryStatsForPackage(
                        info.storageUuid, info.packageName,
                        UserHandle.getUserHandleForUid(info.uid));
                long blamedSize = storageStats.getDataBytes() + storageStats.getAppBytes()
                        + storageStats.getCacheBytes();
                appsTotalSize += blamedSize;
                Log.d(StorageUtil.TAG, "calculateAppStorageGEO pkg="
                        + info.packageName + ",blamedSize=" + blamedSize);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "calculateAppStorageGEO pkg="
                        + info.packageName + ",e=" + e.getMessage());
            }
        }
        queryWithStatFs(context, appsTotalSize);
        Log.d(TAG, "calculateAppStorageGEO time=" + (System.currentTimeMillis() - start));
    }

    private void calculateAppStorageLessO(Context context, List<ApplicationInfo> applications) {
        Method method = null;
        try {
            method = PackageManager.class.getMethod("getPackageSizeInfo",
                    new Class[]{String.class, IPackageStatsObserver.class});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Log.e(TAG, "calculateAppStorageLessO e=" + e.getMessage());
        }
        if (method == null) {
            return;
        }
        Log.d(TAG, "calculateAppStorageLessO size=" + applications.size());
        long time = System.currentTimeMillis();
        IPackageStatsObserver.Stub observer = new IPackageStatsObserver.Stub() {
            final AtomicInteger count = new AtomicInteger(applications.size());
            final AtomicLong appTotalSize = new AtomicLong();

            @Override
            public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
                if (succeeded) {
                    appTotalSize.addAndGet(stats.dataSize + stats.codeSize + stats.cacheSize);
                } else {
                    Log.d(TAG, "onGetStatsCompleted failed pkg=" + stats.packageName);
                }
                int count = this.count.decrementAndGet();
                Log.d(TAG, "onGetStatsCompleted pkg=" + stats.packageName + ",count=" + count);
                if (count <= 0) {
                    queryWithStatFs(context, appTotalSize.get());
                    Log.d(TAG, "calculateAppStorageLessO time=" + (System.currentTimeMillis() - time));
                }
            }
        };
        PackageManager packageManager = context.getPackageManager();
        for (ApplicationInfo info : applications) {
            try {
                method.invoke(packageManager, info.packageName, observer);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "calculateAppStorageLessO pkg=" + info.packageName
                        + ",e" + e.getMessage());
            }
        }
    }

    /**
     * 获取内置总存储
     */
    private static long getStorageTotalSize() {
        File data = Environment.getDataDirectory();
        File root = Environment.getRootDirectory();
        File cache = new File("/cache");
        File skyota = new File("/skyota");
        File factory = new File("/factory");
        File skyworth = new File("/skyworth");

        long dataSize = data.getTotalSpace();
        long rootSize = root.getTotalSpace();
        long cacheSize = 0;
        if (isRealPath(cache)) {
            cacheSize = cache.getTotalSpace();
        }
        long skyotaSize = skyota.getTotalSpace();
        long factorySize = factory.getTotalSpace();
        long skyworthSize = skyworth.getTotalSpace();
        long totalSize = dataSize + rootSize + cacheSize + skyotaSize + factorySize + skyworthSize;
        //当计算少于2000000000时，使其大于2000000000，从而计算出总存储空间为4G NXXT-139632
        if (totalSize <= 2000000000) {
            totalSize = 2000000001;
        }
        long totalStorageSize = roundStorageSize(totalSize);
        long fTotalStorageSize = (totalStorageSize / 1000000000) * 1024 * 1024 * 1024;//以1024为1M的计算单位
        Log.d(TAG, "getStorageTotalSize: "
                + "dataPath=" + data.getAbsolutePath()
                + ",rootPath=" + root.getAbsolutePath()
                + ",dataSize=" + dataSize
                + ",rootSize=" + rootSize
                + ",cacheSize=" + cacheSize
                + ",skyotaSize=" + skyotaSize
                + ",factorySize=" + factorySize
                + ",skyworthSize=" + skyworthSize
                + ",totalStorageSize=" + totalStorageSize
                + ",fTotalStorageSize=" + fTotalStorageSize);
        return fTotalStorageSize;
    }

    /**
     * Round the given size of a storage device to a nice round power-of-two
     * value, such as 256MB or 32GB. This avoids showing weird values like
     * "29.5GB" in UI.
     */
    private static long roundStorageSize(long size) {
        long val = 1;
        long pow = 1;
        while ((val * pow) < size) {
            val <<= 1;
            if (val > 512) {
                val = 1;
                pow *= 1000;
            }
        }
        return val * pow;
    }

    /**
     * 此方法不会把系统空间算进来的
     */
    private void queryWithStatFs(Context context, long appSize) {
        StatFs statFs = null;
        try {
            statFs = new StatFs(Environment.getDataDirectory().getPath());
        } catch (Exception e) {
            Log.e(TAG, "queryWithStatFs e=" + e.getMessage());
        }
        long totalSize = 0;
        long freeSize = 0;
        if (statFs != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                totalSize = statFs.getBlockSizeLong() * statFs.getBlockCountLong();
                freeSize = statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong();
            } else {
                totalSize = (long) statFs.getBlockSize() * statFs.getBlockCount();
                freeSize = (long) statFs.getBlockSize() * statFs.getAvailableBlocks();
            }
        }
        StorageEntity storageEntity = new StorageEntity();
        long storageTotalSize = getStorageTotalSize();//包括系统
        Log.d(TAG, "storageTotalSize:" + storageTotalSize + ",totalSize:" + totalSize
                + ",free:" + freeSize);
        storageEntity.totalSize = storageTotalSize;
        storageEntity.systemSize = storageTotalSize - totalSize;
        storageEntity.usedSize = storageTotalSize - freeSize;
        storageEntity.freeSize = freeSize;
        storageEntity.appSpaceSize = appSize;
        storageEntity.otherSpaceSize = storageEntity.usedSize - storageEntity.systemSize
                - storageEntity.appSpaceSize;
        storageEntity.totalMemorySize = getTotalMemory();
//        storageEntity.emmcLifeStatus = new HWLifeManager(context).getEMMCStatus();
        Log.d(TAG, "queryWithStatFs storageEntity=" + storageEntity);
        storageEntity.isInit = true;
        mStorageEntity = storageEntity;
        if (listeners.isEmpty()) {
            return;
        }
        for (OnLoadListener listener : listeners) {
            listener.onLoadState(storageEntity);
        }
        listeners.clear();
        mIsLoading.set(false);
    }

    public static class StorageEntity {

        public boolean isInit;

        public long totalSize;//存储总大小

        public long usedSize;//已经使用的存储大小

        public long freeSize;//剩余可用的存储大小

        public long systemSize;//系统占用大小

        public long appSpaceSize;//应用占用大小

        public long otherSpaceSize;//其它占用空间大小(并到系统占用里)

        public long totalMemorySize;//内存大小

        public int emmcLifeStatus = -1;

        public String getUsedSize() {
            return Formatter.formatFileSize(usedSize);
        }

        public String getFreeSize() {
            return Formatter.formatFileSize(freeSize);
        }

        private String getOtherSpaceSize() {
            return Formatter.formatFileSize(otherSpaceSize);
        }

        public String getSystemSize() {
            if (systemSize <= 0) {
                return "未知";
            }
            return Formatter.formatFileSize(systemSize + otherSpaceSize);//其它占用并到系统占用里
        }

        public String getAppSpaceSize() {
            return Formatter.formatFileSize(appSpaceSize);
        }

        public String getTotalSize() {
            return Formatter.formatFileSize(totalSize);
        }

        public String getTotalMemorySize() {
            return totalMemorySize + "MB";
        }

        public String getEmmcStatus() {
//            switch (emmcLifeStatus) {
//                case EMMCLifeStatus.ERROR:
//                    return "ERROR";
//                case EMMCLifeStatus.WARNING:
//                    return "WARNING";
//                case EMMCLifeStatus.HEALTHY:
//                    return "HEALTHY";
//                case EMMCLifeStatus.UNKNOWN:
//                    return "UNKNOWN";
//            }
            return "";
        }

        @NonNull
        @Override
        public String toString() {
            return "StorageEntity{" +
                    "totalSize=" + totalSize +
                    ", usedSize=" + usedSize +
                    ", freeSize=" + freeSize +
                    ", systemSize=" + systemSize +
                    ", appSpaceSize=" + appSpaceSize +
                    ", otherSpaceSize=" + otherSpaceSize +
                    ", totalMemorySize=" + totalMemorySize +
                    ", emmcLifeStatus=" + emmcLifeStatus +
                    '}';
        }
    }

    private static class Formatter {
        /**
         * get file format size * * @param context context * @param roundedBytes file size * @return file format size (like 2.12k)
         */
        public static String formatFileSize(long roundedBytes) {
            return formatFileSize(roundedBytes, false, 1, Locale.US);
        }

        public static String formatFileSize(long roundedBytes, int flag) {
            return formatFileSize(roundedBytes, false, flag, Locale.US);
        }

        public static String formatFileSize(long roundedBytes, Locale locale) {
            return formatFileSize(roundedBytes, false, 1, locale);
        }


        private static String formatFileSize(long roundedBytes, boolean shorter, int flags, Locale locale) {
            final int unit = (flags != 0) ? 1024 : 1000;
            float result = roundedBytes;
            String suffix = "B";
            if (result > 900) {
                suffix = "KB";
                result = result / unit;
            }
            if (result > 900) {
                suffix = "MB";
                result = result / unit;
            }
            if (result > 900) {
                suffix = "GB";
                result = result / unit;
            }
            if (result > 900) {
                suffix = "TB";
                result = result / unit;
            }
            if (result > 900) {
                suffix = "PB";
                result = result / unit;
            }
            String value;
            if (result < 1) {
                value = String.format(locale, "%.2f", result);
            } else if (result < 10) {
                if (shorter) {
                    value = String.format(locale, "%.1f", result);
                } else {
                    value = String.format(locale, "%.2f", result);
                }
            } else if (result < 100) {
                if (shorter) {
                    value = String.format(locale, "%.0f", result);
                } else {
                    value = String.format(locale, "%.2f", result);
                }
            } else {
                value = String.format(locale, "%.0f", result);
            }
            return String.format("%s%s", value, suffix);
        }

    }

    public interface OnLoadListener {
        void onLoadState(StorageEntity entity);
    }

    private static boolean isRealPath(File file) {
        boolean result = false;
        try {
            result = file.getPath().equals(file.getCanonicalFile().getPath());
        } catch (Exception e) {
            Log.e(TAG, "isRealPath e=" + e.getMessage());
        }
        Log.d(TAG, "isRealPath result=" + result + ",file=" + file);
        return result;
    }

    public String exeGetScreenLock() {
        String ret = execShell("dumpsys power");
        Log.d(TAG, "exeGetScreenLock ret=" + ret);
        return ret;
    }

    private String execShell(String cmd) {
        StringBuilder s = new StringBuilder();
        InputStream inputStream = null;
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("Lock") || line.contains("lock") || line.contains("LOCK")) {
                    s.append(line).append("\n");
                }
            }
            process.waitFor();
            inputStream.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "execShell e=" + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "execShell2 e=" + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return s.toString();
    }

    private static Long getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

//        try
//        {
//            FileReader localFileReader = new FileReader(str1);
//            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
//            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大�?
//
//            arrayOfString = str2.split("\\s+");
//
//            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() / 1024;// 获得系统总内存，单位是KB，除�?
//            // 1024 单位为M
//            localBufferedReader.close();
//
//        } catch (IOException e)
//        {
//        }
        return initial_memory;
    }
}
