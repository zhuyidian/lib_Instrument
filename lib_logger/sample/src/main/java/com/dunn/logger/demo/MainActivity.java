package com.dunn.logger.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.dunn.logger.LogJointPoint;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        test1("dunn",29,true);
    }

    public void click(View view){
//        Intent intent = new Intent(this,LoginActivity.class);
//        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @LogJointPoint(type = "MSG",open = true)
    public void test1(String arg1,int arg2,boolean arg3){
        Log.v("logger[", "MainActivity ----test1----arg1="+arg1+", arg2="+arg2+", arg3="+arg3);
    }
}
