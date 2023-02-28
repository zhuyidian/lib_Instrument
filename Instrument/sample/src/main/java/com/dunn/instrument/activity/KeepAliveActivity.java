package com.dunn.instrument.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.skyworth.skymonitor.SkyMonitorHelper;
import android.skyworth.skymonitor.keepalive.SkyProcAliveBean;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.dunn.instrument.R;
import com.dunn.instrument.tools.log.LogUtil;
import com.dunn.instrument.view.AppsAdapter;
import com.dunn.instrument.view.AppsBean;
import com.dunn.instrument.view.SwitchView;

import java.util.ArrayList;
import java.util.List;

public class KeepAliveActivity extends Activity {
    public static String TAG = "KeepAliveActivity";
    private ListView mAppListView;
    private AppsAdapter mAppsAdapter;
    private ArrayList<AppsBean> appsList = new ArrayList<AppsBean>();

    private TextView mProcessText1,mProcessText2;
    private CheckBox mProcessSwitch1,mProcessSwitch2;
    private SkyMonitorHelper mSkyMonitorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keepalive);

        mAppListView = (ListView)findViewById(R.id.apps_list) ;
        testData();
        mAppsAdapter = new AppsAdapter(KeepAliveActivity.this, appsList);
        mAppListView.setAdapter(mAppsAdapter);

        mSkyMonitorHelper = new SkyMonitorHelper(KeepAliveActivity.this);

        mProcessText1 = (TextView) findViewById(R.id.processText1);
        mProcessText2 = (TextView) findViewById(R.id.processText2);
        mProcessSwitch1 = (CheckBox) findViewById(R.id.processSwitch1);
        mProcessSwitch2 = (CheckBox) findViewById(R.id.processSwitch2);
        mProcessSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtil.i(TAG,"onCheckedChanged: compoundButton="+compoundButton+", b="+b);
                setKeepAlive("PackageName1","Process1",b);
            }
        });
        mProcessSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtil.i(TAG,"onCheckedChanged: compoundButton="+compoundButton+", b="+b);
            }
        });
    }

    private void setKeepAlive(String packageName, String process, boolean keepAlive){
        if(keepAlive){
            mSkyMonitorHelper.addKeepAlive(new SkyProcAliveBean(packageName,process));
        }else{
            mSkyMonitorHelper.removeKeepAlive(new SkyProcAliveBean(packageName,process));
        }
    }

    public void testData(){
        for(int i=0;i<5;i++){
            AppsBean bean = new AppsBean();
            bean.setPackageName("packageName"+i);
            appsList.add(bean);
        }
    }

    public void updateListView(){
        mAppsAdapter.notifyDataSetChanged();
    }

}
