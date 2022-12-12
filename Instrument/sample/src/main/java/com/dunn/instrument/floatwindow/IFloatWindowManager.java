package com.dunn.instrument.floatwindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import java.util.List;

/**
 * @ClassName: IFloatWindowManager
 * @Author: ZhuYiDian
 * @CreateDate: 2022/12/8
 * @Description:
 */
public interface IFloatWindowManager {
    void init(Context context);

    void destory();

    WindowRecordBean createAndShowFloatWindow();

    boolean removeFloatWindow(WindowRecordBean record);

    int getWindowWidth();

    int getWindowHeight();

}
