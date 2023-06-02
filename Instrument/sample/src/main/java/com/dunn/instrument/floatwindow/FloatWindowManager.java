package com.dunn.instrument.floatwindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dunn.instrument.R;
import com.dunn.instrument.tools.framework.screen.ScreenUtils;
import com.dunn.instrument.tools.log.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class FloatWindowManager implements IFloatWindowManager {
    private static final String TAG = "FloatWindowManager";
    private Context mContext;
    private static final int ROW = 6;  //一行6个
    private static final int COLUM = 4;  //一列4个
    private static final int ROW_SPACE = 20;
    private static final int COLUM_SPACE = 20;
    private int mWindowWidth, mWindowHeight;
    private Map<Integer, List<WindowRecordBean>> mWindowMap = new HashMap<>();  //以行号为key
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private static class FloatWindowManagerHolder {
        private static volatile FloatWindowManager mInstance = new FloatWindowManager();
    }

    private FloatWindowManager() {
    }

    public static FloatWindowManager getInstance() {
        return FloatWindowManagerHolder.mInstance;
    }

    @Override
    public void init(Context context) {
        Context base = context.getApplicationContext();
        if (base != null) {
            this.mContext = base;
        } else {
            this.mContext = context;
        }
        calcScreenForWindow();
    }

    @Override
    public void destory() {
        this.mContext = null;
    }

    @Override
    public WindowRecordBean createAndShowFloatWindow(String name) {
        if (mContext == null) {
            LogUtil.e(TAG, "createAndShowFloatWindow: is no init!!!");
            return null;
        }

        String titleName = name!=null?name:"";
        //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
        windowManager = (WindowManager) mContext.getSystemService(mContext.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.alpha = 0.7f;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        //find window
        WindowRecordBean bean = findWindow();
        if (bean != null) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.float_window_base, null);
            bean.setView(view);
            TextView title = view.findViewById(R.id.window_name);
            title.setText(bean.getTitle()+"["+titleName+"]");
            RelativeLayout contentView = view.findViewById(R.id.window_content);
            bean.setContentView(contentView);
            //显示的位置
            layoutParams.x = bean.getStartX();
            layoutParams.y = bean.getStartY();
            layoutParams.width = bean.getWidth();
            layoutParams.height = bean.getHeight();
            windowManager.addView(view, layoutParams);
            //关闭悬浮窗
            // windowManager.removeView(view);
            return bean;
        }

        return null;
    }

    @Override
    public boolean removeFloatWindow(WindowRecordBean record) {
        if (windowManager == null) {
            LogUtil.e(TAG, "removeFloatWindow: window manager is null!!!");
            return false;
        }
        if (record == null) {
            LogUtil.e(TAG, "removeFloatWindow: revord is null!!!");
            return false;
        }
        if (record.getView() == null) {
            LogUtil.e(TAG, "removeFloatWindow: view is null!!!");
            return false;
        }

        windowManager.removeView(record.getView());

        resetWindow(record);
        return true;
    }

    @Override
    public int getWindowWidth() {
        return mWindowWidth;
    }

    @Override
    public int getWindowHeight() {
        return mWindowHeight;
    }

    private void calcScreenForWindow() {
        DisplayMetrics metrics = ScreenUtils.getDisplayMetrics(mContext);
        int screenWidth = metrics.widthPixels;  //1280
        int screenHeight = metrics.heightPixels;  //720
        LogUtil.i(TAG, "calcScreenForWindow: screenWidth=" + screenWidth + ", screenHeight=" + screenHeight);
        int totalRowSpace = (ROW - 1) * ROW_SPACE;
        mWindowWidth = (screenWidth - totalRowSpace) / ROW;
        int totalColumSpace = (COLUM - 1) * COLUM_SPACE;
        mWindowHeight = (screenHeight - totalColumSpace) / COLUM;

        LogUtil.i(TAG, "calcScreenForWindow: mWindowWidth=" + mWindowWidth + ", mWindowHeight=" + mWindowHeight);

        synchronized (mWindowMap) {
            //init window map data
            for (int i = 0; i < COLUM; i++) {
                ArrayList windowList = new ArrayList<WindowRecordBean>(ROW);
                for (int j = 0; j < ROW; j++) {
                    WindowRecordBean windowBean = new WindowRecordBean(i+"-"+j,j * (mWindowWidth + ROW_SPACE), i * (mWindowHeight + COLUM_SPACE), mWindowWidth, mWindowHeight, WindowRecordBean.STATUS_INVALID);
                    windowList.add(windowBean);
                }
                mWindowMap.put(i, windowList);
            }

            //printf window map
            for (Map.Entry<Integer, List<WindowRecordBean>> entry : mWindowMap.entrySet()) {
                LogUtil.i(TAG, "calcScreenForWindow: key=" + entry.getKey() + ", value=" + entry.getValue());
            }
        }
    }

    private WindowRecordBean findWindow() {
        synchronized (mWindowMap) {
            Iterator<Map.Entry<Integer, List<WindowRecordBean>>> entries = mWindowMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<Integer, List<WindowRecordBean>> entry = entries.next();
                List<WindowRecordBean> list = entry.getValue();
                Iterator<WindowRecordBean> it = list.iterator();
                while (it.hasNext()) {
                    WindowRecordBean bean = it.next(); // next() 返回下一个元素
                    if (bean != null && (bean.getStatus() == WindowRecordBean.STATUS_INVALID||
                            bean.getStatus() == WindowRecordBean.STATUS_HIDE)) {
                        bean.setStatus(WindowRecordBean.STATUS_SHOW);

                        //printf window map
                        for (Map.Entry<Integer, List<WindowRecordBean>> entry1 : mWindowMap.entrySet()) {
                            LogUtil.i(TAG, "findWindow: key=" + entry1.getKey() + ", value=" + entry1.getValue());
                        }
                        return bean;
                    }
                }
            }

            return null;
        }
    }

    private void resetWindow(WindowRecordBean record) {
        synchronized (mWindowMap) {
            Iterator<Map.Entry<Integer, List<WindowRecordBean>>> entries = mWindowMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<Integer, List<WindowRecordBean>> entry = entries.next();
                List<WindowRecordBean> list = entry.getValue();
                Iterator<WindowRecordBean> it = list.iterator();
                while (it.hasNext()) {
                    WindowRecordBean bean = it.next(); // next() 返回下一个元素
                    if (bean != null && record != null && bean.findWindowForStartXY(record.getStartX(),record.getStartY())) {
                        bean.setStatus(WindowRecordBean.STATUS_HIDE);
                        bean.setView(null);
                        bean.setContentView(null);
                        //printf window map
                        for (Map.Entry<Integer, List<WindowRecordBean>> entry1 : mWindowMap.entrySet()) {
                            LogUtil.i(TAG, "resetWindow: key=" + entry1.getKey() + ", value=" + entry1.getValue());
                        }
                        return;
                    }
                }
            }
        }
    }
}