package com.dunn.instrument;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.dunn.instrument.anr.watchdog.ANRWatchDog;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * @ClassName: ApiTools
 * @Author: ZhuYiDian
 * @CreateDate: 2022/3/20 3:05
 * @Description:
 */
public class ApiTools {
    public static StringBuilder screenResultBuilder = new StringBuilder();
//    public static AppDatabase dbUtil;

    /**
     * 数据库写测试
     * @param context
     */
    public static void DbTest(Context context){
//        dbUtil = new AppDatabase(context);
//        //数据库测试
//        AppDataHelper.L().updateAppinfo(dbUtil, "packageName", "className",
//                "label", 1, null);
    }

    /**
     * 数据库读测试
     * @return
     */
    public static Drawable getDbBmp() {
//        //测试数据库
//        List<Map<String, Object>> list = AppDataHelper.L().getAppinfo(dbUtil);
//        for (Map<String, Object> map : list) {
//            for (Map.Entry<String, Object> entry : map.entrySet()) {
//                if (entry.getKey().equals("blob")) {
//                    Drawable able = new BitmapDrawable((Bitmap) entry.getValue());
//                    return able;
//                }
//            }
//        }
        return null;
    }

    /**
     * reflect测试
     */
    public static void reflectExec(){
//        Object[] args = new Object[1];
//        args[0] = 1;
//        ReflectUtil.exec(TimeUtil.class.getName(),"getDayHoursList",args);
    }

    /**
     * reflect测试
     */
//    public static void soFix(Context context) {
//        // 从服务器下载 so ，比对 so 的版本
//        // 现在下好了，在我的手机里面 /so/libmain.so
//
//        // 先调用 sdk 方法动态加载或者修复
//        File mainSoPath = new File(Environment.getExternalStorageDirectory(),"so/libmain.so");
//
//        //必须要拷贝文件到应用自己的目录，否则会报错
//        File libSoPath = new File(context.getDir("lib", Context.MODE_PRIVATE),"so");
//        if(!libSoPath.exists()){
//            libSoPath.mkdirs();
//        }
//        File dst = new File(libSoPath,"libmain.so");
//        try {
//            FileUtil.copyFile(mainSoPath,dst);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            SoHotFix soHotFix = new SoHotFix(context);
//            soHotFix.injectLoadPath(libSoPath.getAbsolutePath());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * screen测试
     * @param context
     */
//    public static void screenTest(Context context){
//        DisplayMetrics dMetrics = ScreenUtils.getDisplayMetrics(context);
//        //  屏幕的绝对宽度（像素）
//        //int screenWidth = dMetrics.widthPixels;  //不包含虚拟导航栏的高度
//        int screenWidth = ScreenUtils.getScreenWidth3(context); //包含虚拟导航栏的高度
//        // 屏幕的绝对高度（像素）
//        //int screenHeight = dMetrics.heightPixels;  //不包含虚拟导航栏的高度
//        int screenHeight = ScreenUtils.getScreenHeight3(context); //包含虚拟导航栏的高度
//        screenResultBuilder.append("\r\n"+"像素:"+screenWidth+","+screenHeight);
//
//        // X轴方向上屏幕每英寸的物理像素数。
//        float xdpi = dMetrics.xdpi;
//        // Y轴方向上屏幕每英寸的物理像素数。
//        float ydpi = dMetrics.ydpi;
//        screenResultBuilder.append("\r\n"+"XY每英寸的像素数(密度PPI):"+xdpi+","+ydpi);
//
//        //获取屏幕尺寸
//        double screenInches = ScreenUtils.getScreenInches(context);
//        screenResultBuilder.append("\r\n"+"屏幕尺寸Inches:"+screenInches);
//
//        // 屏幕的逻辑密度，是密度无关像素（dip）的缩放因子，160dpi是系统屏幕显示的基线，1dip = 1px， 所以，在160dpi的屏幕上，density = 1， 而在一个120dpi屏幕上 density = 0.75。
//        // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
//        float density = dMetrics.density;
//        // 每英寸的像素点数，屏幕密度的另一种表示。densityDpi = density * 160.
//        // 屏幕密度（每寸像素：120/160/240/320）
//        float desityDpi = dMetrics.densityDpi;
//        screenResultBuilder.append("\r\n"+"屏幕密度(像素比例):"+density+",   屏幕密度dpi(每英寸的点数):"+desityDpi);
//
//        String dpi = ScreenUtils.getResourcesDpiMsg(context);
//        screenResultBuilder.append("\r\n"+"res: "+dpi);
//
//        //  屏幕上字体显示的缩放因子，一般与density值相同，除非在程序运行中，用户根据喜好调整了显示字体的大小时，会有微小的增加。
//        float scaledDensity = dMetrics.scaledDensity;
//        screenResultBuilder.append("\r\n"+"字体显示的缩放因子:"+scaledDensity);
//
//        //px-->dp
//        int widthDp = ScreenUtils.pxConvertDip(context,screenWidth);
//        int heightDp = ScreenUtils.pxConvertDip(context,screenHeight);
//        screenResultBuilder.append("\r\n"+"dp转换:"+widthDp+","+heightDp);
//
//        //StatusBar
//        int statusBar = ScreenUtils.getStatusBarHeight(context);
//        int navigationBar = ScreenUtils.getNavigationBarHeight(context);
//        screenResultBuilder.append("\r\n"+"statusBar:"+statusBar+",   navigationBar"+navigationBar);
//
//        LogUtil.v("screen","result="+screenResultBuilder.toString());
//    }
}
