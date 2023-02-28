package com.dunn.instrument.activity;

import android.app.Activity;
import android.os.Bundle;
import android.skyworth.skymonitor.SkyMonitorHelper;
import android.skyworth.skymonitor.keepalive.SkyProcAliveBean;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.dunn.instrument.R;
import com.dunn.instrument.tools.log.LogUtil;
import com.dunn.instrument.view.SwitchView;

public class KeepAliveActivity extends Activity {
    public static String TAG = "KeepAliveActivity";
    private TextView mProcessText1,mProcessText2;
    private CheckBox mProcessSwitch1,mProcessSwitch2;
    private SkyMonitorHelper mSkyMonitorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keepalive);

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

}
