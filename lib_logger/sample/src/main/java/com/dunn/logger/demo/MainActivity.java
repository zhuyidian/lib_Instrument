package com.dunn.logger.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.dunn.logger.InitJointPoint;
import com.dunn.logger.LogJointPoint;
import com.dunn.logger.ReleaseJointPoint;

public class MainActivity extends AppCompatActivity {

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

    @ReleaseJointPoint
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @LogJointPoint(type = "MSG",open = true)
    public void logOut(String msg){

    }

}
