package com.dunn.demo.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

public class ScreenUtils {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * @param mContext
     * @param dpValue
     */
    public static int dipConvertPx(Context mContext, float dpValue) {
        try {
            float scale = mContext.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        } catch (Exception e) {
        }
        return -1;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     * @param mContext
     * @param pxValue
     */
    public static int pxConvertDip(Context mContext, float pxValue) {
        try {
            float scale = mContext.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        } catch (Exception e) {
        }
        return -1;
    }

    /**
     * 通过上下文获取 WindowManager
     * @param mContext
     * @return
     */
    public static WindowManager getWindowManager(Context mContext) {
        try {
            return (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 通过上下文获取 DisplayMetrics (获取关于显示的通用信息，如显示大小，分辨率和字体)
     * @param mContext
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context mContext) {
        try {
            WindowManager wManager = getWindowManager(mContext);
            if (wManager != null) {
                DisplayMetrics dMetrics = new DisplayMetrics();
                wManager.getDefaultDisplay().getMetrics(dMetrics);
                return dMetrics;
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取屏幕信息
     * @param mContext 上下文
     * @return
     */
    public static int[] getScreen(Context mContext){
        int[] screen = null;
        try {
            WindowManager wManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = wManager.getDefaultDisplay();
            // 获取宽度高度
            int width = display.getWidth();;
            int height = display.getHeight();
            if(width != 0 && height != 0){
                screen = new int[]{width,height};
            }
        } catch (Exception e) {
            screen = null;
        }
        return screen;
    }

    /**
     * 获取屏幕宽度（对外公布）
     * @param mContext 上下文
     * @return
     */
    public static int getWidth(Context mContext){
        int[] screen = getScreen(mContext);
        if(screen != null){
            return screen[0];
        }
        return 0;
    }

    /**
     * 注：这种方式不包含虚拟导航栏的高度
     * 获取屏幕高度（对外公布）
     * @param mContext 上下文
     * @return
     */
    public static int getHeight(Context mContext){
        int[] screen = getScreen(mContext);
        if(screen != null){
            return screen[1];
        }
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getScreenWidth3(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        defaultDisplay.getRealSize(outPoint);
        return outPoint.x;
    }

    //注：包含虚拟导航栏的高度
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getScreenHeight3(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        defaultDisplay.getRealSize(outPoint);
        return outPoint.y;
    }

    // 获取状态栏高度
    public static int getStatusBarHeight(Context mContext){
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return mContext.getResources().getDimensionPixelSize(resourceId);
    }

    // 获取导航栏高度
    public static int getNavigationBarHeight(Context mContext) {
        int rid = mContext.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid!=0){
            int resourceId = mContext.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return mContext.getResources().getDimensionPixelSize(resourceId);
        }else
            return 0;

    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     * @param mContext
     * @param pxValue
     */
    public static int pxConvertSp(Context mContext, float pxValue) {
        try {
            float scale = mContext.getResources().getDisplayMetrics().scaledDensity;
            return (int) (pxValue / scale + 0.5f);
        } catch (Exception e) {
        }
        return -1;
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px
     * @param mContext
     * @param spValue
     */
    public static int spConvertPx(Context mContext, float spValue) {
        try {
            float scale = mContext.getResources().getDisplayMetrics().scaledDensity;
            return (int) (spValue * scale + 0.5f);
        } catch (Exception e) {
        }
        return -1;
    }

    public static void fullscreen(Activity activity,boolean enable) {
        if (enable) { //显示状态栏
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(lp);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else { //隐藏状态栏
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(lp);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * 目标设备res-dpi信息获取方法
     * 一般情况下的普通屏幕密度：ldpi是120，mdpi是160，hdpi是240，xhdpi是320，xxhdpi是480，xxxhdpi是560。
     * 屏幕信息详情{@link DisplayMetrics}
     *
     */
    public static String getResourcesDpiMsg(Context context) {
        String msg;
        int densityDpi = context.getResources().getDisplayMetrics().densityDpi;
        switch (densityDpi) {
            case DisplayMetrics.DENSITY_XXXHIGH:
                msg = "屏幕密度：" + "560像素/英寸, 资源文件夹:xxxhdpi, 分辨率倍数:4";
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                msg = "屏幕密度：" + "480像素/英寸, 资源文件夹:xxhdpi, 分辨率倍数:3";
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                msg = "屏幕密度：" + "320像素/英寸, 资源文件夹:xhdpi, 分辨率倍数:2";
                break;
            case DisplayMetrics.DENSITY_HIGH:
                msg = "屏幕密度：" + "240像素/英寸, 资源文件夹:hdpi, 分辨率倍数:1.5 ";
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                msg = "屏幕密度：" + "160像素/英寸, 资源文件夹:mdpi, 分辨率倍数:1 ";
                break;
            case DisplayMetrics.DENSITY_LOW:
                msg = "屏幕密度：" + "120像素/英寸, 资源文件夹:ldpi, 分辨率倍数:0.75 ";
                break;
            default:
                msg = "暂时没有处理当前分辨率,densityDpi:"+densityDpi;
                break;
        }
        return msg;
    }

    public static double getScreenInches(Context mContext){
        try {
            WindowManager wManager = getWindowManager(mContext);
            if (wManager != null) {
                Point point = new Point();
                wManager.getDefaultDisplay().getRealSize(point);
                DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
                double x = Math.pow(point.x/ dm.xdpi, 2);
                double y = Math.pow(point.y / dm.ydpi, 2);
                double screenInches = Math.sqrt(x + y);
                return screenInches;
            }
        } catch (Exception e) {
        }

        return 0d;
    }

    /**
     *
     * @param activity
     * @param
     */
    public static void setNavigationBar(Activity activity,int visible){
        View decorView = activity.getWindow().getDecorView();
        //显示NavigationBar
        if (View.GONE == visible){
            int option = SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(option);
        }
    }

    /**
     * 导航栏，状态栏隐藏
     * @param activity
     */
    public static void NavigationBarStatusBar(Activity activity,boolean hasFocus){
//        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//        else{
//            View decorView = activity.getWindow().getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
//        }
    }

    public static void hideSystemUI(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY //(修改这个选项，可以设置不同模式)
                        //使用下面三个参数，可以使内容显示在system bar的下面，防止system bar显示或
                        //隐藏时，Activity的大小被resize。
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // 隐藏导航栏和状态栏
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    //显示system bar， 同时还是希望内容显示在system bar的下方。
    public static void showSystemUI(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    //开启/关闭硬件加速
    public static void setLayerType(Activity activity,boolean open){
        View decorView = activity.getWindow().getDecorView();
        if(open){
            decorView.setLayerType(View.LAYER_TYPE_HARDWARE, null);	//开启硬件加速
        }else {
            decorView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);    //关闭硬件加速
        }
    }
}
