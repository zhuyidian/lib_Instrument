package com.dunn.instrument.floatwindow;


import android.view.View;
import android.widget.RelativeLayout;

/**
 * @ClassName: WindowRecordBean
 * @Author: ZhuYiDian
 * @CreateDate: 2022/12/9
 * @Description:
 */
public class WindowRecordBean implements Cloneable {
    public static final int STATUS_SHOW = 1;
    public static final int STATUS_HIDE = 2;
    public static final int STATUS_INVALID = 3;
    private String mTitle;
    private int mStartX, mStartY;
    private int mWidth, mHeight;
    private int mStatus;
    private View mView;
    private RelativeLayout mContentView;

    public WindowRecordBean(String mTitle, int mStartX, int mStartY, int mWidth, int mHeight) {
        this.mTitle = mTitle;
        this.mStartX = mStartX;
        this.mStartY = mStartY;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }

    public WindowRecordBean(String mTitle, int mStartX, int mStartY, int mWidth, int mHeight, int mStatus) {
        this.mTitle = mTitle;
        this.mStartX = mStartX;
        this.mStartY = mStartY;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        this.mStatus = mStatus;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getStartX() {
        return mStartX;
    }

    public void setStartX(int mStartX) {
        this.mStartX = mStartX;
    }

    public int getStartY() {
        return mStartY;
    }

    public void setStartY(int mStartY) {
        this.mStartY = mStartY;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    public View getView() {
        return mView;
    }

    public void setView(View mView) {
        this.mView = mView;
    }

    public RelativeLayout getContentView() {
        return mContentView;
    }

    public void setContentView(RelativeLayout mContentView) {
        this.mContentView = mContentView;
    }

    public boolean findWindowForStartXY(int startX, int startY){
        if(startX==mStartX && startY==mStartY){
            return true;
        }

        return false;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "WindowRecordBean{" +
                "mTitle='" + mTitle + '\'' +
                ", mStartX=" + mStartX +
                ", mStartY=" + mStartY +
                ", mWidth=" + mWidth +
                ", mHeight=" + mHeight +
                ", mStatus=" + mStatus +
                '}';
    }
}
