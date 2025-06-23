package com.dunn.instrument;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.dunn.instrument.service.DeviceInfoService;
import com.dunn.instrument.tools.log.LogUtil;
import com.dunn.instrument.utils.CommonUtil;
import com.dunn.instrument.utils.StorageUtil;


public class MainActivity extends Activity implements View.OnClickListener {
    public static String TAG = "MainActivity-Test";
    //service1
    private Button mServiceDeviceinfo,
            mServiceTest1,
            mServiceTest2;
    //activity1
    private Button mActivityTest1,
            mActivityTest2;
    private boolean mServiceDeviceInfoFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG,"onCreate start");
        setContentView(R.layout.activity_main);

        //service1
        mServiceDeviceinfo = (Button) findViewById(R.id.service_deviceinfo);
        mServiceTest1 = (Button) findViewById(R.id.service_test1);
        mServiceTest2 = (Button) findViewById(R.id.service_test2);
        mServiceDeviceinfo.setOnClickListener(this);

        //activity1
        mActivityTest1 = (Button) findViewById(R.id.activity_test1);
        mActivityTest2 = (Button) findViewById(R.id.activity_test2);

        LogUtil.i(TAG,"onCreate end");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i(TAG,"onResume: ");
        CommonUtil.getBatteryLevel(MainActivity.this);
        String status = CommonUtil.getBatteryStatus(MainActivity.this);
        LogUtil.i(TAG,"onResume: status="+status);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //service1
            case R.id.service_deviceinfo:
                if(mServiceDeviceInfoFlag){
                    stopService(new Intent(MainActivity.this, DeviceInfoService.class));
                }else{
                    startService(new Intent(MainActivity.this, DeviceInfoService.class));
                }
                mServiceDeviceInfoFlag=!mServiceDeviceInfoFlag;
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        LogUtil.i(TAG,"onWindowFocusChanged hasFocus="+hasFocus);
    }
}
