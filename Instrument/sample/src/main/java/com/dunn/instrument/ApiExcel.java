package com.dunn.instrument;

import android.content.Context;

import com.dunn.instrument.excel.ExcelHelp;
import com.dunn.instrument.excel.ExcelInfo;

/**
 * @ClassName: ApiExcel
 * @Author: ZhuYiDian
 * @CreateDate: 2022/3/19 23:55
 * @Description:
 */
public class ApiExcel {
    public static void excelInit(Context context){
        ExcelHelp.getInstance().init(context, "Ccosservice.xls");
    }

    public static void clearExcel(){
        ExcelHelp.getInstance().clearExcel();
    }

    public static void setFunctionRowName(){
        ExcelHelp.getInstance().setFunctionRowName(new String[]{
                "wo", "ni", "ta",
        });
    }

    public static void setUiRowName(){
        ExcelHelp.getInstance().setUiRowName(new String[]{
                "wo", "ni", "ta",
        });
    }

    public static ExcelInfo getInfo(){
        return ExcelHelp.getInstance().getInfo();
    }

    public static void excelSubmit(){
        ExcelHelp.getInstance().submit();
    }
}
