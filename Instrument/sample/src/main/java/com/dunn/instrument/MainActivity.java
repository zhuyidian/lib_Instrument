package com.dunn.instrument;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.dunn.instrument.excel.ExcelHelp;
import com.dunn.instrument.logger.InitJointPoint;
import com.dunn.instrument.logger.LogJointPoint;
import com.dunn.instrument.logger.ReleaseJointPoint;
import com.dunn.instrument.logger.UploadJointPoint;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    public void click(View view) {
        ApiLogger.logOut("logger日志测试");
        ApiExcel.excelInfo();
        ApiExcel.excelSubmit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApiLogger.loggerRelease();
    }
}
