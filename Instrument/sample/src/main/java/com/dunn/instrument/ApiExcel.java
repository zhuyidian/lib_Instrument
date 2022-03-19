package com.dunn.instrument;

import android.content.Context;

import com.dunn.instrument.excel.ExcelHelp;

/**
 * @ClassName: ApiExcel
 * @Author: ZhuYiDian
 * @CreateDate: 2022/3/19 23:55
 * @Description:
 */
public class ApiExcel {
    public static void excelInit(Context context){
        ExcelHelp.getInstance().init(context);
    }

    public static void excelInfo(){
        ExcelHelp.getInstance().getInfo().mAppInfoInit = System.currentTimeMillis() + "";
    }

    public static void excelSubmit(){
        ExcelHelp.getInstance().submit();
    }
}
