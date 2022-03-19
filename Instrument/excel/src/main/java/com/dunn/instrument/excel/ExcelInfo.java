package com.dunn.instrument.excel;

/**
 * @ClassName: ExcelInfo
 * @Author: ZhuYiDian
 * @CreateDate: 2022/3/8 17:48
 * @Description:
 */
public class ExcelInfo {
    //sheet 0
    public String mStartBroadcast;
    public String mOtherBroadcast;
    public int mPid;
    public String mProcessStartTime;
    public String mProcessInitTime;
    public String mServiceStartTime;
    public String mServiceInitTime;
    public String mServiceCommand;
    public String mServiceMode;
    public String mServiceSpecificApp;
    public String mDialogCreateTime;
    public String mDialogInitTime;
    public long mDialogShowTime;

    //sheet 1
    public String mThemeHook;
    public String mThemeInit;
    public String mDensityInit;
    public String mBroadcastInit;
    public String mQuickInit;
    public String mSourceInit;
    public String mAppInfoInit;
    public String mHomeHeaderInit;
    public String mImageLoaderInit;
    public String mSkyInit;
    public String mMultiModelInit;
    public String mHostRemoteInit;
    public String mSensorDataInit;
    public String mTtsInit;

    //sheet 2
    public String mTheHook;
    public String mThemeRegister;
    public String mCreateDialog;
    public String mCreateBar;
    public String mCreateControlPanel;
    public String mCreateInfoPanel;
    public String mCreateScroll;
    public String mCreateDate;
    public String mCreateSource;
    public String mCreateQuick;
    public String mCreateApps;

    public ExcelInfo() {

    }

    public ExcelInfo(ExcelInfo info) {
        //sheet 0
        mPid = info.mPid;
        mStartBroadcast = info.mStartBroadcast;
        mOtherBroadcast = info.mOtherBroadcast;
        mProcessStartTime = info.mProcessStartTime;
        mProcessInitTime = info.mProcessInitTime;
        mServiceStartTime = info.mServiceStartTime;
        mServiceInitTime = info.mServiceInitTime;
        mServiceCommand = info.mServiceCommand;
        mServiceMode = info.mServiceMode;
        mServiceSpecificApp = info.mServiceSpecificApp;
        mDialogCreateTime = info.mDialogCreateTime;
        mDialogInitTime = info.mDialogInitTime;
        mDialogShowTime = info.mDialogShowTime;

        //sheet 1
        mThemeHook = info.mThemeHook;
        mThemeInit = info.mThemeInit;
        mDensityInit = info.mDensityInit;
        mBroadcastInit = info.mBroadcastInit;
        mQuickInit = info.mQuickInit;
        mSourceInit = info.mSourceInit;
        mAppInfoInit = info.mAppInfoInit;
        mHomeHeaderInit = info.mHomeHeaderInit;
        mImageLoaderInit = info.mImageLoaderInit;
        mSkyInit = info.mSkyInit;
        mMultiModelInit = info.mMultiModelInit;
        mHostRemoteInit = info.mHostRemoteInit;
        mSensorDataInit = info.mSensorDataInit;
        mTtsInit = info.mTtsInit;

        //sheet 2
        mTheHook = info.mTheHook;
        mThemeRegister = info.mThemeRegister;
        mCreateDialog = info.mCreateDialog;
        mCreateBar = info.mCreateBar;
        mCreateControlPanel = info.mCreateControlPanel;
        mCreateInfoPanel = info.mCreateInfoPanel;
        mCreateScroll = info.mCreateScroll;
        mCreateDate = info.mCreateDate;
        mCreateSource = info.mCreateSource;
        mCreateQuick = info.mCreateQuick;
        mCreateApps = info.mCreateApps;
    }

    @Override
    public String toString() {
        return "ExcelInfo{" +
                "mStartBroadcast='" + mStartBroadcast + '\'' +
                ", mOtherBroadcast='" + mOtherBroadcast + '\'' +
                ", mPid=" + mPid +
                ", mProcessStartTime='" + mProcessStartTime + '\'' +
                ", mProcessInitTime='" + mProcessInitTime + '\'' +
                ", mServiceStartTime='" + mServiceStartTime + '\'' +
                ", mServiceInitTime='" + mServiceInitTime + '\'' +
                ", mServiceCommand='" + mServiceCommand + '\'' +
                ", mServiceMode='" + mServiceMode + '\'' +
                ", mServiceSpecificApp='" + mServiceSpecificApp + '\'' +
                ", mDialogCreateTime='" + mDialogCreateTime + '\'' +
                ", mDialogInitTime='" + mDialogInitTime + '\'' +
                ", mDialogShowTime=" + mDialogShowTime +
                ", mThemeHook='" + mThemeHook + '\'' +
                ", mThemeInit='" + mThemeInit + '\'' +
                ", mDensityInit='" + mDensityInit + '\'' +
                ", mBroadcastInit='" + mBroadcastInit + '\'' +
                ", mQuickInit='" + mQuickInit + '\'' +
                ", mSourceInit='" + mSourceInit + '\'' +
                ", mAppInfoInit='" + mAppInfoInit + '\'' +
                ", mHomeHeaderInit='" + mHomeHeaderInit + '\'' +
                ", mImageLoaderInit='" + mImageLoaderInit + '\'' +
                ", mSkyInit='" + mSkyInit + '\'' +
                ", mMultiModelInit='" + mMultiModelInit + '\'' +
                ", mHostRemoteInit='" + mHostRemoteInit + '\'' +
                ", mSensorDataInit='" + mSensorDataInit + '\'' +
                ", mTtsInit='" + mTtsInit + '\'' +
                ", mTheHook='" + mTheHook + '\'' +
                ", mThemeRegister='" + mThemeRegister + '\'' +
                ", mCreateDialog='" + mCreateDialog + '\'' +
                ", mCreateBar='" + mCreateBar + '\'' +
                ", mCreateControlPanel='" + mCreateControlPanel + '\'' +
                ", mCreateInfoPanel='" + mCreateInfoPanel + '\'' +
                ", mCreateScroll='" + mCreateScroll + '\'' +
                ", mCreateDate='" + mCreateDate + '\'' +
                ", mCreateSource='" + mCreateSource + '\'' +
                ", mCreateQuick='" + mCreateQuick + '\'' +
                ", mCreateApps='" + mCreateApps + '\'' +
                '}';
    }
}
