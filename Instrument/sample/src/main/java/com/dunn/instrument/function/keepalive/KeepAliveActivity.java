package com.dunn.instrument.function.keepalive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.skyworth.skymonitor.SkyMonitorHelper;
import android.skyworth.skymonitor.keepalive.SkyProcAliveBean;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.dunn.instrument.R;
import com.dunn.instrument.tools.framework.pkms.PkmsUtil;
import com.dunn.instrument.tools.log.LogUtil;
import com.dunn.instrument.tools.thread.ThreadManager;
import com.dunn.instrument.view.dialog.DelayShowDialog;
import com.dunn.instrument.view.dialog.TCallBack;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class KeepAliveActivity extends Activity implements View.OnClickListener {
    public static String TAG = "KeepAliveActivity";
    private ListView mAppListView;
    private AppsAdapter mAppsAdapter;
    private ArrayList<AppsBean> appsList = new ArrayList<AppsBean>();
    private ArrayList<SkyProcAliveBean> appsAliveList = new ArrayList<>();
    private SkyMonitorHelper mSkyMonitorHelper;
    private Button mNativeReadTest, mNativeWriteTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keepalive);
//        mSkyMonitorHelper = new SkyMonitorHelper(KeepAliveActivity.this);

        initView();
        initData();
    }

    private void initView() {
        mAppListView = (ListView) findViewById(R.id.apps_list);
        mAppsAdapter = new AppsAdapter(KeepAliveActivity.this, appsList);
        mAppListView.setAdapter(mAppsAdapter);
        mAppListView.setFocusable(true);
        mAppListView.setFocusableInTouchMode(true);
        mAppListView.requestFocus();
        mAppListView.setSelection(1);
        mAppListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                DelayShowDialog.init(KeepAliveActivity.this).showDialog(appsList.get(i).getCtl(), new TCallBack<String, String>() {
                    @Override
                    public void call(String s, String s2) {
                        LogUtil.i(TAG, "onItemSelected: ctl=" + appsList.get(i).getCtl());
                        setKeepAlive(appsList.get(i).getPackageName(),
                                appsList.get(i).getCtl().getIsAutoStartByUserCtl(),
                                appsList.get(i).getCtl().getIsAutoStartToAllow(),
                                appsList.get(i).getCtl().getIsBackgroundRunByUserCtl(),
                                appsList.get(i).getCtl().getBackgroundRunToStrategy());
                        updateListView();
                    }
                });
            }
        });
        mAppListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mNativeReadTest = (Button) findViewById(R.id.native_read_test);
        mNativeWriteTest = (Button) findViewById(R.id.native_write_test);
        mNativeReadTest.setOnClickListener(this);
        mNativeWriteTest.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.native_read_test:
//                mSkyMonitorHelper.testGetValNative();
                break;
            case R.id.native_write_test:
//                mSkyMonitorHelper.testSetValNative(5);
                break;
            default:
                break;
        }
    }

    private void initData() {
        ThreadManager.getInstance().ioThread(new Runnable() {
            @Override
            public void run() {
                //ArrayList<AppsBean> list = getAllAppInfo(KeepAliveActivity.this);
                //ArrayList<SkyPackageInfoBean> installAppList = (ArrayList<SkyPackageInfoBean>) mSkyMonitorHelper.getPackageInfoList();
                //appsAliveList = (ArrayList<SkyProcAliveBean>) mSkyMonitorHelper.getUserCtlList();
                //if(appsAliveList==null){
                //    LogUtil.i(TAG,"getAppsAliveList: appsAliveList is null"+Thread.currentThread());
                //}
                ArrayList<AppsBean> installList = (ArrayList<AppsBean>) getLauncherApps(KeepAliveActivity.this);
                LogUtil.i(TAG, "initData: start installList size=" + installList.size());
                //过滤installList，排除掉一个应用存在多个Launcher属性


                ArrayList<AppsBean> configList = (ArrayList<AppsBean>) readDefaultConfig("Low","/data/system/process_manager_default.xml");
                if(configList!=null) {
                    for (AppsBean bean : configList) {
                        LogUtil.i(TAG, "initData: read config bean=" + bean);
                    }
                    for (AppsBean bean : configList) {
                        boolean findConfigFlag = false;
                        Iterator<AppsBean> it = installList.iterator();
                        while (it.hasNext()) {
                            AppsBean app = it.next();
                            if (bean.getPackageName().equals(app.getPackageName())) {
                                findConfigFlag = true;
                                if (bean.getCtl().getIsAutoStartByUserCtl() || bean.getCtl().getIsBackgroundRunByUserCtl()) {   //配置显示
                                    app.getCtl().setIsAutoStartByUserCtl(bean.getCtl().getIsAutoStartByUserCtl());
                                    app.getCtl().setIsAutoStartToAllow(bean.getCtl().getIsAutoStartToAllow());
                                    app.getCtl().setIsBackgroundRunByUserCtl(bean.getCtl().getIsBackgroundRunByUserCtl());
                                    app.getCtl().setBackgroundRunToStrategy(bean.getCtl().getBackgroundRunToStrategy());
                                } else {  //配置不显示
                                    LogUtil.i(TAG, "initData: ###### remove app=" + app);
                                    it.remove();
                                }
                                break;
                            }
                        }
                        if (!findConfigFlag) {
                            if (bean.getCtl().getIsAutoStartByUserCtl() || bean.getCtl().getIsBackgroundRunByUserCtl()) {   //配置显示
                                if(isAppInstalled(KeepAliveActivity.this,bean.getPackageName())){
                                    LogUtil.i(TAG, "initData: ###### add bean=" + bean);
                                    bean.setLabel(PkmsUtil.getAppNameByPkg(KeepAliveActivity.this,bean.getPackageName()));
                                    bean.setIcon(PkmsUtil.getAppIcon(KeepAliveActivity.this,bean.getPackageName()));
                                    installList.add(bean);
                                }
                            }
                        }
                    }
                }

                if (installList != null) {
                    LogUtil.i(TAG, "initData: end installList size=" + installList.size());
                    appsList.clear();
                    appsList.addAll(installList);
                    ThreadManager.getInstance().uiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateListView();
                        }
                    });
    //            for(int i=0;i<appsAliveList.size();i++){
    //                SkyProcAliveBean bean = appsAliveList.get(i);
    //                AppsBean appBean = new AppsBean();
    //                appBean.setLabel(PkmsUtil.getAppNameByPkg(KeepAliveActivity.this,bean.getPackageName()));
    //                appBean.setPackageName(bean.getPackageName());
    //                appBean.setIcon(PkmsUtil.getAppIcon(KeepAliveActivity.this,bean.getPackageName()));
    //                AppStateCtl ctl = new AppStateCtl();
    //                ctl.setIsAutoStartByUserCtl(bean.getIsAutoStartByUserCtl());
    //                ctl.setIsAutoStartToAllow(bean.getIsAutoStartToAllow());
    //                ctl.setIsBackgroundRunByUserCtl(bean.getIsBackgroundRunByUserCtl());
    //                ctl.setBackgroundRunToStrategy(bean.getBackgroundRunToStrategy());
    //                appBean.setCtl(ctl);
    //                appsList.add(appBean);
    //            }
                }
            }
        });

    }

    private void updateListView() {
        Collections.sort(appsList, new AppsComparatorScore());
        mAppsAdapter.notifyDataSetChanged();
    }

    /**
     * 设置具体应用
     *
     * @param packageName
     * @param isAutoStartByUserCtl
     * @param isAutoStartToAllow
     * @param isBackgroundRunByUserCtl
     * @param backgroundRunToStrategy
     */
    private void setKeepAlive(String packageName, boolean isAutoStartByUserCtl, boolean isAutoStartToAllow, boolean isBackgroundRunByUserCtl, String backgroundRunToStrategy) {
        LogUtil.i(TAG, "setKeepAlive: packageName=" + packageName +
                ", isAutoStartByUserCtl=" + isAutoStartByUserCtl +
                ", isAutoStartToAllow=" + isAutoStartToAllow +
                ", isBackgroundRunByUserCtl=" + isBackgroundRunByUserCtl +
                ", backgroundRunToStrategy=" + backgroundRunToStrategy);
//        mSkyMonitorHelper.updateApp(new SkyProcAliveBean(packageName,isAutoStartByUserCtl,isAutoStartToAllow,isBackgroundRunByUserCtl,backgroundRunToStrategy));
    }




    /**
     * 获取手机已安装应用列表
     *
     * @param ctx
     * @return
     */
    private ArrayList<AppsBean> getAllAppInfo(Context ctx) {
        ArrayList<AppsBean> appBeanList = new ArrayList<>();
        AppsBean bean = null;

        PackageManager packageManager = ctx.getPackageManager();
        List<PackageInfo> list = packageManager.getInstalledPackages(0);
        for (PackageInfo p : list) {
            bean = new AppsBean();
            ApplicationInfo appInfo = p.applicationInfo;
            boolean isSystem = (appInfo.flags & (ApplicationInfo.FLAG_SYSTEM |
                    ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0;
            boolean isPersistent = (appInfo.flags & ApplicationInfo.FLAG_PERSISTENT) != 0;
//            boolean isPrivileged = (appInfo.privateFlags & ApplicationInfo.PRIVATE_FLAG_PRIVILEGED) != 0;
            String packageName = p.packageName;
            bean.setIcon(p.applicationInfo.loadIcon(packageManager));
            bean.setLabel(packageManager.getApplicationLabel(p.applicationInfo).toString());
            bean.setPackageName(p.applicationInfo.packageName);

            AppStateCtl ctl = new AppStateCtl();
            bean.setCtl(ctl);
            appBeanList.add(bean);
        }
        return appBeanList;
    }

    /**
     * 获取带launcher属性的应用列表
     *
     * @param context
     * @return
     */
    private List<AppsBean> getLauncherApps(Context context) {
        if (context == null) {
            LogUtil.e(TAG, "getLauncherApps: context is null");
            return null;
        }
        try {
            long startTime = System.currentTimeMillis();
            AppsBean bean = null;
            ArrayList<AppsBean> appBeanList = new ArrayList<>();

            PackageManager packageManager = context.getPackageManager();
            Intent filterIntent = new Intent(Intent.ACTION_MAIN, null);
            filterIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(filterIntent, 0);
            for (ResolveInfo resolveInfo : apps) {
                bean = new AppsBean();
                ApplicationInfo appInfo = resolveInfo.activityInfo.applicationInfo;
                String pkgName = appInfo.packageName;
                boolean isSystem = (appInfo.flags & (ApplicationInfo.FLAG_SYSTEM |
                        ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0;
                boolean isPersistent = (appInfo.flags & ApplicationInfo.FLAG_PERSISTENT) != 0;
                LogUtil.i(TAG, "getLauncherApps: pkgname=" + pkgName + ", isSystem=" + isSystem + ", isPersistent=" + isPersistent);
                if (!isPersistent) {
                    bean.setIcon(appInfo.loadIcon(packageManager));
                    bean.setLabel(packageManager.getApplicationLabel(appInfo).toString());
                    bean.setPackageName(pkgName);
                    AppStateCtl ctl = new AppStateCtl();
                    ctl.setIsAutoStartByUserCtl(true);
                    ctl.setIsBackgroundRunByUserCtl(true);
                    if (isSystem) {
                        ctl.setIsAutoStartToAllow(true);
                        ctl.setBackgroundRunToStrategy("smart");
                    } else {
                        ctl.setIsAutoStartToAllow(false);
                        ctl.setBackgroundRunToStrategy("smart");
                    }
                    bean.setCtl(ctl);
                    if(!appBeanList.contains(bean)){
                        appBeanList.add(bean);
                    }
                }
            }
            LogUtil.i(TAG, "getLauncherApps: appBeanList size=" + (appBeanList != null ? appBeanList.size() : "null") + ", time=" + (System.currentTimeMillis() - startTime) + "MS");
            return appBeanList;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "getLauncherApps: e=" + e);
        }
        return null;
    }

    /**
     * 判断应用是否安装
     */
    private boolean isAppInstalled(Context context,String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        }catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if(packageInfo ==null){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 给applist排序
     */
    private class AppsComparatorScore implements Comparator<AppsBean> {
        @Override
        public int compare(AppsBean o1, AppsBean o2) {
            if (o1 == null || o2 == null) return 0;

            if (o2.getCtl().getScore() >= o1.getCtl().getScore()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private int mCpuThreshold = -1;
    private int mMemThreshold = -1;
    private List<AppsBean> readDefaultConfig(String platform, String configName) {
        long startTime = System.currentTimeMillis();

        File file = new File(configName);
        if (!file.exists()) {
            LogUtil.e(TAG, "readDefaultConfig: defaule config file is not exists!!!");
            return null;
        }

        FileInputStream fis = null;
        List<AppsBean> resultList = null;
        try {
            LogUtil.i(TAG, "readDefaultConfig: platform=" + platform+", configName="+configName);
            resultList = new ArrayList<>();
            fis = new FileInputStream(file);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, "utf-8");
            int eventType = parser.getEventType();
            AppsBean bean = null;
            AppStateCtl ctl = null;
            boolean isPlatform = false;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
//                LogUtil.i(TAG, "readDefaultConfig: eventType="+eventType+", tagName="+tagName);
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("AppManager".equals(tagName)) {
                            LogUtil.i(TAG, "readDefaultConfig: AppManager label is start............");
                        } else if (platform.equals(tagName)) {
                            LogUtil.i(TAG, "readDefaultConfig: platform="+platform+" label is start");
                            isPlatform = true;
                            String mCpuThresholdStr = parser.getAttributeValue(null, "cpu_threshold");
                            if (null != mCpuThresholdStr) {
                                mCpuThreshold = Integer.parseInt(mCpuThresholdStr);
                            }
                            String mMemThresholdStr = parser.getAttributeValue(null, "mem_threshold");
                            if (null != mMemThresholdStr) {
                                mMemThreshold = Integer.parseInt(mMemThresholdStr);
                            }
                            LogUtil.i(TAG, "readDefaultConfig: mCpuThreshold="+mCpuThreshold+", mMemThreshold="+mMemThreshold);
                        } else if ("AppCustom".equals(tagName)) {
                            if(isPlatform) {
                                LogUtil.i(TAG, "readDefaultConfig: AppCustom label is start");
                                bean = new AppsBean();
                                ctl = new AppStateCtl();
                                bean.setCtl(ctl);

                                String pkgName = parser.getAttributeValue(null, "pkgname");
                                if (pkgName == null) {
                                    LogUtil.e(TAG, "readDefaultConfig: pkgname is null");
                                    break;
                                }
                                bean.setPackageName(pkgName);
                            }
                        } else if ("AutoStart".equals(tagName)) {
                            if(isPlatform) {
                                if (ctl == null) {
                                    break;
                                }
                                LogUtil.i(TAG, "readDefaultConfig: AutoStart label is start");
                                String isUserCtl = parser.getAttributeValue(null, "isUserCtl");
                                String isAllow = parser.getAttributeValue(null, "isAllow");
                                if ("true".equals(isUserCtl)) {
                                    ctl.setIsAutoStartByUserCtl(true);
                                } else {
                                    ctl.setIsAutoStartByUserCtl(false);
                                }
                                if ("true".equals(isAllow)) {
                                    ctl.setIsAutoStartToAllow(true);
                                } else {
                                    ctl.setIsAutoStartToAllow(false);
                                }
                            }
                        } else if ("BackgroundRun".equals(tagName)) {
                            if (isPlatform) {
                                if (ctl == null) {
                                    break;
                                }
                                LogUtil.i(TAG, "readDefaultConfig: BackgroundRun label is start");
                                String isUserCtl = parser.getAttributeValue(null, "isUserCtl");
                                String strategy = parser.getAttributeValue(null, "strategy");
                                if ("true".equals(isUserCtl)) {
                                    ctl.setIsBackgroundRunByUserCtl(true);
                                } else {
                                    ctl.setIsBackgroundRunByUserCtl(false);
                                }
                                ctl.setBackgroundRunToStrategy(strategy);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("AppManager".equals(tagName)) {
                            LogUtil.i(TAG, "readDefaultConfig: AppManager label is end............");
                        } else if (platform.equals(tagName)) {
                            LogUtil.i(TAG, "readDefaultConfig: platform="+platform+" label is end");
                            isPlatform = false;
                        } else if ("AppCustom".equals(tagName)) {
                            if(bean!=null) {
                                resultList.add(bean);
                                bean = null;
                                ctl = null;
                                LogUtil.i(TAG, "readDefaultConfig: AppCustom label is end");
                            }
                        } else if ("AutoStart".equals(tagName)) {
                            if(bean!=null) {
                                LogUtil.i(TAG, "readDefaultConfig: AutoStart label is end");
                            }
                        } else if ("BackgroundRun".equals(tagName)) {
                            if(bean!=null) {
                                LogUtil.i(TAG, "readDefaultConfig: BackgroundRun label is end");
                            }
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "readDefaultConfig: e=" + e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "readDefaultConfig: e1=" + e);
                }
            }
        }

        LogUtil.i(TAG, "readDefaultConfig: resultList size=" + (resultList != null ? resultList.size() : "null") + ", time=" + (System.currentTimeMillis() - startTime) + "MS");
        return resultList;
    }
}
