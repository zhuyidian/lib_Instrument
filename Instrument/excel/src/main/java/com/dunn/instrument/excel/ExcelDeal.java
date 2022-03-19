package com.dunn.instrument.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * @ClassName: ExcelUtil
 * @Author: ZhuYiDian
 * @CreateDate: 2022/3/8 17:48
 * @Description:
 */
public class ExcelDeal {
    private static final String TAG = ExcelDeal.class.getSimpleName();
    private Context mContext;
    public final static String FILE_NAME = "Ccosservice.xls";
    private String ABSOLUTE_FILE_PATH = "";
    public int count = 0;
    public final int MAX_ROW_SIZE = 5000;

    //sheet 0
    public final static String SHEET_0 = "All";
    public final static int SHEET_ID_0 = 0;
    public static final int COLUMNS_MUN = 13;
    public static final String START_BROADCAST_LABEL = "StartBroadcast";
    public static final String OTHER_BROADCAST_LABEL = "OtherBroadcast";
    public static final String PID_LABEL = "Pid";
    public static final String PROCESS_START_LABEL = "ProcessStart";
    public static final String PROCESS_INIT_LABEL = "ProcessInit";
    public static final String SERVICE_START_LABEL = "ServiceStart";
    public static final String SERVICE_INIT_LABEL = "ServiceInit";
    public static final String SERVICE_COMMAND_LABEL = "ServiceCommand";
    public static final String SERVICE_MODE_LABEL = "ServiceMode";
    public static final String SERVICE_SPECIFICAPP_LABEL = "ServiceSpecificApp";
    public static final String DIALOG_CREATE_LABEL = "DialogCreate";
    public static final String DIALOG_INIT_LABEL = "DialogInit";
    public static final String DIALOG_SHOW_LABEL = "DialogShow(MS)";

    //sheet 1
    public final static String SHEET_1 = "Other";
    public final static int SHEET_ID_1 = 1;
    public static final int COLUMNS_MUN1 = 14;
    public static final String mThemeHook_LABEL = "ThemeHook";
    public static final String mThemeInit_LABEL = "ThemeInit";
    public static final String mDensityInit_LABEL = "DensityInit";
    public static final String mBroadcastInit_LABEL = "BroadcastInit";
    public static final String mQuickInit_LABEL = "QuickInit";
    public static final String mSourceInit_LABEL = "SourceInit";
    public static final String mAppInfoInit_LABEL = "AppInfoInit";
    public static final String mHomeHeaderInit_LABEL = "HomeHeaderInit";
    public static final String mImageLoaderInit_LABEL = "ImageLoaderInit";
    public static final String mSkyInit_LABEL = "SkyInit";
    public static final String mMultiModelInit_LABEL = "MultiModelInit";
    public static final String mHostRemoteInit_LABEL = "HostRemoteInit";
    public static final String mSensorDataInit_LABEL = "SensorDataInit";
    public static final String mTtsInit_LABEL = "TtsInit";

    //sheet 2
    public final static String SHEET_2 = "Widget";
    public final static int SHEET_ID_2 = 2;
    public static final int COLUMNS_MUN2 = 11;
    public static final String mTheHook_LABEL = "TheHook";
    public static final String mThemeRegister_LABEL = "ThemeRegister";
    public static final String mCreateDialog_LABEL = "CreateDialog";
    public static final String mCreateBar_LABEL = "CreateBar";
    public static final String mCreateControlPanel_LABEL = "CreateControlPanel";
    public static final String mCreateInfoPanel_LABEL = "CreateInfoPanel";
    public static final String mCreateScroll_LABEL = "CreateScroll";
    public static final String mCreateDate_LABEL = "CreateDate";
    public static final String mCreateSource_LABEL = "CreateSource";
    public static final String mCreateQuick_LABEL = "CreateQuick";
    public static final String mCreateApps_LABEL = "CreateApps";

    public ExcelDeal(Context context) {
        mContext = context;
        File dir = mContext.getFilesDir();
        File file = new File(dir, FILE_NAME);
        ABSOLUTE_FILE_PATH = file.getPath();
        Log.d(TAG, "ExcelDeal: ABSOLUTE_FILE_PATH=" + ABSOLUTE_FILE_PATH);
    }

    /**
     * 删除 Excel
     */
    public void delExcel() {
        File file = new File(ABSOLUTE_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }

        Log.d(TAG, "delExcel:");
    }

    public void updateExcel(ExcelInfo info) {
        try {
            if (!isFileExists(ABSOLUTE_FILE_PATH)) {
                createExcel();
            }
            Log.d(TAG, "updateExcel: info=" + info);
            appendExcel(info);
            count++;

            if (count > MAX_ROW_SIZE) {
                reSetCount();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, "updateExcel: e=" + e);
        }
    }

    private void createExcel() throws Exception {
        WritableWorkbook wwb;
        OutputStream os;

        File dir = mContext.getFilesDir();
        File file = new File(dir, FILE_NAME);
        Log.d(TAG, "createExcel: dir=" + dir.getPath() + ", file=" + file.getName());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (!file.exists()) {
            file.createNewFile();
        }
        Log.d(TAG, "createExcel: filePath=" + file.getPath());
        // 创建Excel工作表
        os = new FileOutputStream(file);
        wwb = Workbook.createWorkbook(os);

        //sheet 0
        // 添加第一个工作表并设置第一个Sheet的名字
        WritableSheet sheet = wwb.createSheet(SHEET_0, SHEET_ID_0);
        Label label;
        // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z, 排版格式
        label = new Label(0, 0, START_BROADCAST_LABEL, getHeader());
        sheet.addCell(label);
        label = new Label(1, 0, OTHER_BROADCAST_LABEL, getHeader());
        sheet.addCell(label);
        label = new Label(2, 0, PID_LABEL, getHeader());
        sheet.addCell(label);
        label = new Label(3, 0, PROCESS_START_LABEL, getHeader());
        sheet.addCell(label);
        label = new Label(4, 0, PROCESS_INIT_LABEL, getHeader());
        sheet.addCell(label);
        label = new Label(5, 0, SERVICE_START_LABEL, getHeader());
        sheet.addCell(label);
        label = new Label(6, 0, SERVICE_INIT_LABEL, getHeader());
        sheet.addCell(label);
        label = new Label(7, 0, SERVICE_COMMAND_LABEL, getHeader());
        sheet.addCell(label);
        label = new Label(8, 0, SERVICE_MODE_LABEL, getHeader());
        sheet.addCell(label);
        label = new Label(9, 0, SERVICE_SPECIFICAPP_LABEL, getHeader());
        sheet.addCell(label);
        label = new Label(10, 0, DIALOG_CREATE_LABEL, getHeader());
        sheet.addCell(label);
        label = new Label(11, 0, DIALOG_INIT_LABEL, getHeader());
        sheet.addCell(label);
        label = new Label(12, 0, DIALOG_SHOW_LABEL, getHeader());
        sheet.addCell(label);

        //sheet 1
        WritableSheet sheet1 = wwb.createSheet(SHEET_1, SHEET_ID_1);
        Label label1;
        // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z, 排版格式
        label1 = new Label(0, 0, mThemeHook_LABEL, getHeader());
        sheet1.addCell(label1);
        label1 = new Label(1, 0, mThemeInit_LABEL, getHeader());
        sheet1.addCell(label1);
        label1 = new Label(2, 0, mDensityInit_LABEL, getHeader());
        sheet1.addCell(label1);
        label1 = new Label(3, 0, mBroadcastInit_LABEL, getHeader());
        sheet1.addCell(label1);
        label1 = new Label(4, 0, mQuickInit_LABEL, getHeader());
        sheet1.addCell(label1);
        label1 = new Label(5, 0, mSourceInit_LABEL, getHeader());
        sheet1.addCell(label1);
        label1 = new Label(6, 0, mAppInfoInit_LABEL, getHeader());
        sheet1.addCell(label1);
        label1 = new Label(7, 0, mHomeHeaderInit_LABEL, getHeader());
        sheet1.addCell(label1);
        label1 = new Label(8, 0, mImageLoaderInit_LABEL, getHeader());
        sheet1.addCell(label1);
        label1 = new Label(9, 0, mSkyInit_LABEL, getHeader());
        sheet1.addCell(label1);
        label1 = new Label(10, 0, mMultiModelInit_LABEL, getHeader());
        sheet1.addCell(label1);
        label1 = new Label(11, 0, mHostRemoteInit_LABEL, getHeader());
        sheet1.addCell(label1);
        label1 = new Label(12, 0, mSensorDataInit_LABEL, getHeader());
        sheet1.addCell(label1);
        label1 = new Label(13, 0, mTtsInit_LABEL, getHeader());
        sheet1.addCell(label1);

        //sheet 2
        WritableSheet sheet2 = wwb.createSheet(SHEET_2, SHEET_ID_2);
        Label label2;
        // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z, 排版格式
        label2 = new Label(0, 0, mTheHook_LABEL, getHeader());
        sheet2.addCell(label2);
        label2 = new Label(1, 0, mThemeRegister_LABEL, getHeader());
        sheet2.addCell(label2);
        label2 = new Label(2, 0, mCreateDialog_LABEL, getHeader());
        sheet2.addCell(label2);
        label2 = new Label(3, 0, mCreateBar_LABEL, getHeader());
        sheet2.addCell(label2);
        label2 = new Label(4, 0, mCreateControlPanel_LABEL, getHeader());
        sheet2.addCell(label2);
        label2 = new Label(5, 0, mCreateInfoPanel_LABEL, getHeader());
        sheet2.addCell(label2);
        label2 = new Label(6, 0, mCreateScroll_LABEL, getHeader());
        sheet2.addCell(label2);
        label2 = new Label(7, 0, mCreateDate_LABEL, getHeader());
        sheet2.addCell(label2);
        label2 = new Label(8, 0, mCreateSource_LABEL, getHeader());
        sheet2.addCell(label2);
        label2 = new Label(9, 0, mCreateQuick_LABEL, getHeader());
        sheet2.addCell(label2);
        label2 = new Label(10, 0, mCreateApps_LABEL, getHeader());
        sheet2.addCell(label2);

        if (null != wwb) {
            // 写入数据
            wwb.write();
            // 关闭文件
            wwb.close();
        }
    }

    private void appendExcel(ExcelInfo info) throws IOException, BiffException, WriteException {
        Workbook rwb = Workbook.getWorkbook(new File(ABSOLUTE_FILE_PATH));
        WritableWorkbook wwb = Workbook.createWorkbook(new File(ABSOLUTE_FILE_PATH), rwb);// copy

        //sheet 0
        WritableSheet sheet = wwb.getSheet(SHEET_ID_0);
        Label label;
        // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
        //int currentColumns = sheet.getColumns();
        int currentRow = sheet.getRows();
        for (int i = 0; i < COLUMNS_MUN; i++) {
            label = getLabelSheet0(i, currentRow, info);
            sheet.addCell(label);
        }

        //sheet 1
        WritableSheet sheet1 = wwb.getSheet(SHEET_ID_1);
        Label label1;
        int currentRow1 = sheet1.getRows();
        for (int i = 0; i < COLUMNS_MUN1; i++) {
            label1 = getLabelSheet1(i, currentRow1, info);
            sheet1.addCell(label1);
        }

        //sheet 2
        WritableSheet sheet2 = wwb.getSheet(SHEET_ID_2);
        Label label2;
        int currentRow2 = sheet2.getRows();
        for (int i = 0; i < COLUMNS_MUN2; i++) {
            label2 = getLabelSheet2(i, currentRow2, info);
            sheet2.addCell(label2);
        }

        if (null != wwb) {
            wwb.write();
            wwb.close();
        }
    }

    private void reSetCount() {
        count = 0;
    }

    private WritableCellFormat getHeader() {
        WritableFont font = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD);// 定义字体
        try {
            // 字体
            font.setColour(Colour.BLACK);
        } catch (WriteException e1) {
            e1.printStackTrace();
        }
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            // 左右居中
            format.setAlignment(jxl.format.Alignment.CENTRE);
            // 上下居中
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            // 边框
            format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            // 背景
            format.setBackground(Colour.GREEN);
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return format;
    }

    private WritableCellFormat getHeaderMark() {
        WritableFont font = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD);// 定义字体
        try {
            // 字体
            font.setColour(Colour.BLACK);
        } catch (WriteException e1) {
            e1.printStackTrace();
        }
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            // 左右居中
            format.setAlignment(jxl.format.Alignment.CENTRE);
            // 上下居中
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            // 边框
            format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            // 背景
            format.setBackground(Colour.RED);
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return format;
    }

    private boolean isFileExists(String filename) {
        try {
            File f = new File(filename);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    private Label getLabelSheet0(int col, int raw, ExcelInfo info) {
        if (col >= COLUMNS_MUN || info == null) return null;

        switch (col) {
            case 0:
                return new Label(col, raw, info.mStartBroadcast);
            case 1:
                return new Label(col, raw, info.mOtherBroadcast);
            case 2:
                return new Label(col, raw, info.mPid + "");
            case 3:
                return new Label(col, raw, info.mProcessStartTime);
            case 4:
                return new Label(col, raw, info.mProcessInitTime);
            case 5:
                return new Label(col, raw, info.mServiceStartTime);
            case 6:
                return new Label(col, raw, info.mServiceInitTime);
            case 7:
                return new Label(col, raw, info.mServiceCommand);
            case 8:
                return new Label(col, raw, info.mServiceMode);
            case 9:
                return new Label(col, raw, info.mServiceSpecificApp);
            case 10:
                return new Label(col, raw, info.mDialogCreateTime);
            case 11:
                return new Label(col, raw, info.mDialogInitTime);
            case 12:
                Label lable;
                if (info.mDialogShowTime > 300l) {
                    lable = new Label(col, raw, info.mDialogShowTime + "", getHeaderMark());
                } else {
                    lable = new Label(col, raw, info.mDialogShowTime + "");
                }
                return lable;
            default:
                return null;
        }
    }

    private Label getLabelSheet1(int col, int raw, ExcelInfo info) {
        if (col >= COLUMNS_MUN1 || info == null) return null;

        switch (col) {
            case 0:
                return new Label(col, raw, info.mThemeHook);
            case 1:
                return new Label(col, raw, info.mThemeInit);
            case 2:
                return new Label(col, raw, info.mDensityInit);
            case 3:
                return new Label(col, raw, info.mBroadcastInit);
            case 4:
                return new Label(col, raw, info.mQuickInit);
            case 5:
                return new Label(col, raw, info.mSourceInit);
            case 6:
                return new Label(col, raw, info.mAppInfoInit);
            case 7:
                return new Label(col, raw, info.mHomeHeaderInit);
            case 8:
                return new Label(col, raw, info.mImageLoaderInit);
            case 9:
                return new Label(col, raw, info.mSkyInit);
            case 10:
                return new Label(col, raw, info.mMultiModelInit);
            case 11:
                return new Label(col, raw, info.mHostRemoteInit);
            case 12:
                return new Label(col, raw, info.mSensorDataInit);
            case 13:
                return new Label(col, raw, info.mTtsInit);
            default:
                return null;
        }
    }

    private Label getLabelSheet2(int col, int raw, ExcelInfo info) {
        if (col >= COLUMNS_MUN2 || info == null) return null;

        switch (col) {
            case 0:
                return new Label(col, raw, info.mTheHook);
            case 1:
                return new Label(col, raw, info.mThemeRegister);
            case 2:
                return new Label(col, raw, info.mCreateDialog);
            case 3:
                return new Label(col, raw, info.mCreateBar);
            case 4:
                return new Label(col, raw, info.mCreateControlPanel);
            case 5:
                return new Label(col, raw, info.mCreateInfoPanel);
            case 6:
                return new Label(col, raw, info.mCreateScroll);
            case 7:
                return new Label(col, raw, info.mCreateDate);
            case 8:
                return new Label(col, raw, info.mCreateSource);
            case 9:
                return new Label(col, raw, info.mCreateQuick);
            case 10:
                return new Label(col, raw, info.mCreateApps);
            default:
                return null;
        }
    }
}
