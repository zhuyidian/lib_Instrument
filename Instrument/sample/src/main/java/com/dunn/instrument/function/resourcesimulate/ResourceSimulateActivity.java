package com.dunn.instrument.function.resourcesimulate;

import static com.dunn.instrument.function.keepalive.InterfaceKeepalive.PROCESS_CMD_AUTO_START;
import static com.dunn.instrument.function.keepalive.InterfaceKeepalive.PROCESS_CMD_BACKGROUND;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.SystemProperties;
import android.skyworth.skymonitor.AppCustom;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dunn.instrument.R;
import com.dunn.instrument.function.keepalive.AppStateCtl;
import com.dunn.instrument.function.keepalive.AppsAdapter;
import com.dunn.instrument.function.keepalive.AppsBean;
import com.dunn.instrument.function.keepalive.InterfaceKeepaliveSystem;
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
import java.util.List;

public class ResourceSimulateActivity extends Activity implements View.OnClickListener {
    public static String TAG = "ResourceSimulateActivity";
    private ListView mAppListView;
    private AppsAdapter mAppsAdapter;
    private ArrayList<AppsBean> appsList = new ArrayList<AppsBean>();
    private Button mNativeReadTest, mNativeWriteTest;
    private TextView mOpenTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keepalive);
        initView();
    }

    private void initView() {
        mAppListView = (ListView) findViewById(R.id.apps_list);
        mAppsAdapter = new AppsAdapter(ResourceSimulateActivity.this, appsList);
        mAppListView.setAdapter(mAppsAdapter);
        mAppListView.setFocusable(true);
        mAppListView.setFocusableInTouchMode(true);
        mAppListView.requestFocus();
        mAppListView.setSelection(1);
        mAppListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                DelayShowDialog.init(ResourceSimulateActivity.this).showDialog(appsList.get(i).getCtl(), new TCallBack<String, String>() {
                    @Override
                    public void call(String s, String s2) {
                        LogUtil.i(TAG, "onItemSelected: ctl=" + appsList.get(i).getCtl());
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
        mOpenTest = (TextView) findViewById(R.id.open_test);
        mNativeReadTest.setOnClickListener(this);
        mNativeWriteTest.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.native_read_test:

                break;
            case R.id.native_write_test:

                break;
            default:
                break;
        }
    }
}
