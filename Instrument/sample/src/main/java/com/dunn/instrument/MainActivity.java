package com.dunn.instrument;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.dunn.instrument.logger.InitJointPoint;
import com.dunn.instrument.logger.LogJointPoint;
import com.dunn.instrument.logger.ReleaseJointPoint;
import com.dunn.instrument.logger.UploadJointPoint;

public class MainActivity extends AppCompatActivity {

    //初始化logger的地方
    @InitJointPoint(mFilePath = "/logger",mFileName = "TTT_logger_cache",isDebug = true)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    public void click(View view){
//        Intent intent = new Intent(this,LoginActivity.class);
//        startActivity(intent);
        logOut("111111111111111111111111111111111111111111");
    }

    //释放logger的地方
    @ReleaseJointPoint
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //收集日志的地方
    @LogJointPoint(type = "MSG",open = true)
    public void logOut(String msg){

    }

    //上传日志的地方
    @UploadJointPoint
    public <T> void uploadLogger(T value,String url,String token,String userId){

    }

}
