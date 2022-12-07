package com.dunn.instrument.api;

import android.content.Context;



/**
 * @ClassName: ApiExcel
 * @Author: ZhuYiDian
 * @CreateDate: 2022/3/19 23:55
 * @Description:
 */
public class ApiExcel {

    /**
     * 使用前先初始化
     * @param context
     */
    public static void excelInit(Context context){
//        ExcelHelp.getInstance().init(context, "Ccosservice.xls");
    }

    /**
     * 删除excel表
     */
    public static void clearExcel(){
//        ExcelHelp.getInstance().clearExcel();
    }

    /**
     * 创建列标签
     */
    public static void setFunctionRowName(){
//        ExcelHelp.getInstance().setFunctionRowName(new String[]{
//                "wo", "ni", "ta",
//        });
    }

    /**
     * 创建列标签
     */
    public static void setUiRowName(){
//        ExcelHelp.getInstance().setUiRowName(new String[]{
//                "wo", "ni", "ta",
//        });
    }

//    public static ExcelInfo getInfo(){
//        return ExcelHelp.getInstance().getInfo();
//    }

    /**
     * 提交完整一行数据
     */
    public static void excelSubmit(){
//        ExcelHelp.getInstance().submit();
    }
}
