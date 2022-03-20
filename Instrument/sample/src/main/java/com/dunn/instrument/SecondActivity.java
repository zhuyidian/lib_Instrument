package com.dunn.instrument;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class SecondActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view) {
        // 执行到了这里，会有一个异常，虚拟机拿到了这个异常，
        Integer.parseInt("0x01");
    }
}
