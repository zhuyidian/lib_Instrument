package com.dunn.instrument.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String FILE_NAME = "Ccosservice.xls";
    private String ABSOLUTE_FILE_PATH = "";

    //sheet 0
    private static final String SHEET_0 = "module";
    private static final int SHEET_ID_0 = 0;
    private static final int COLUMNS_MUN = 7;
    private static final String PID_COL = "pid";
    private static final String APP_START_COL = "app_start";
    private static final String APP_INIT_COL = "app_init";
    private static final String BROADCAST_START_COL = "broadcast_start";
    private static final String SERVICE_START_COL = "service_start";
    private static final String SERVICE_INIT_COL = "service_init";
    private static final String SERVICE_COMMAND_COL = "service_command";

    //sheet 1
    private static final String SHEET_1 = "function";
    private static final int SHEET_ID_1 = 1;
    public static final String FUNCTION1_COL = "function1";
    public static final String FUNCTION2_COL = "function2";
    public static final String FUNCTION3_COL = "function3";
    public static final String FUNCTION4_COL = "function4";
    public static final String FUNCTION5_COL = "function5";
    private Map<String, String> mFunctionMap = new HashMap<>();
    public static final String[] FUNCTION_COL = new String[]{
            FUNCTION1_COL,
            FUNCTION2_COL,
            FUNCTION3_COL,
            FUNCTION4_COL,
            FUNCTION5_COL,
    };

    //sheet 2
    private static final String SHEET_2 = "ui";
    private static final int SHEET_ID_2 = 2;
    public static final String UI1_COL = "ui1";
    public static final String UI2_COL = "ui2";
    public static final String UI3_COL = "ui3";
    public static final String UI4_COL = "ui4";
    public static final String UI5_COL = "ui5";
    private Map<String, String> mUiMap = new HashMap<>();
    public static final String[] UI_COL = new String[]{
            UI1_COL,
            UI2_COL,
            UI3_COL,
            UI4_COL,
            UI5_COL,
    };

    public ExcelDeal(Context context) {
        this(context, null);
    }

    public ExcelDeal(Context context, String excelFileName) {
        mContext = context;
        if (excelFileName != null) {
            FILE_NAME = excelFileName;
        }
        for (int i = 0; i < FUNCTION_COL.length; i++) {
            mFunctionMap.put(FUNCTION_COL[i], FUNCTION_COL[i]);
        }
        for (int i = 0; i < UI_COL.length; i++) {
            mUiMap.put(UI_COL[i], UI_COL[i]);
        }

        try {
            File dir = mContext.getFilesDir();
            File file = new File(dir, FILE_NAME);
            ABSOLUTE_FILE_PATH = file.getPath();
            Log.d(TAG, "ExcelDeal: ABSOLUTE_FILE_PATH=" + ABSOLUTE_FILE_PATH);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "ExcelDeal: e=" + e);
        }
    }

    public ExcelDeal setFunctionRowName(String[] rowName) {
        if (rowName == null || rowName.length <= 0) return this;

        for (int i = 0; i < rowName.length && i < FUNCTION_COL.length; i++) {
            mFunctionMap.put(FUNCTION_COL[i], rowName[i]);
        }

        return this;
    }

    public ExcelDeal setUiRowName(String[] rowName) {
        if (rowName == null || rowName.length <= 0) return this;

        for (int i = 0; i < rowName.length && i < UI_COL.length; i++) {
            mUiMap.put(UI_COL[i], rowName[i]);
        }

        return this;
    }

    /**
     * 删除 Excel
     */
    public ExcelDeal delExcel() {
        File file = new File(ABSOLUTE_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }

        Log.d(TAG, "delExcel:");
        return this;
    }

    public ExcelDeal updateExcel(ExcelInfo info) {
        try {
            if (!isFileExists(ABSOLUTE_FILE_PATH)) {
                createExcel();
            }
            Log.d(TAG, "updateExcel: info=" + info);
            appendExcel(info);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, "updateExcel: e=" + e);
        }finally {
            return this;
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
        // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z, 排版格式
        Label label;
        label = new Label(0, 0, PID_COL, getHeader());
        sheet.addCell(label);
        label = new Label(1, 0, APP_START_COL, getHeader());
        sheet.addCell(label);
        label = new Label(2, 0, APP_INIT_COL, getHeader());
        sheet.addCell(label);
        label = new Label(3, 0, BROADCAST_START_COL, getHeader());
        sheet.addCell(label);
        label = new Label(4, 0, SERVICE_START_COL, getHeader());
        sheet.addCell(label);
        label = new Label(5, 0, SERVICE_INIT_COL, getHeader());
        sheet.addCell(label);
        label = new Label(6, 0, SERVICE_COMMAND_COL, getHeader());
        sheet.addCell(label);

        //sheet 1
        // 添加第一个工作表并设置第一个Sheet的名字
        WritableSheet sheet1 = wwb.createSheet(SHEET_1, SHEET_ID_1);
        // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z, 排版格式
        for (int i = 0; i < FUNCTION_COL.length; i++) {
            Log.d(TAG, "createExcel: label=" + SHEET_1+", col="+i+", row="+0+", name="+mFunctionMap.get(FUNCTION_COL[i]));
            Label label1 = new Label(i, 0, mFunctionMap.get(FUNCTION_COL[i]), getHeader());
            sheet1.addCell(label1);
        }

        //sheet 2
        WritableSheet sheet2 = wwb.createSheet(SHEET_2, SHEET_ID_2);
        // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z, 排版格式
        for (int i = 0; i < UI_COL.length; i++) {
            Log.d(TAG, "createExcel: label=" + SHEET_2+", col="+i+", row="+0+", name="+mUiMap.get(UI_COL[i]));
            Label label2 = new Label(i, 0, mUiMap.get(UI_COL[i]), getHeader());
            sheet2.addCell(label2);
        }

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
        int currentRow1 = sheet1.getRows();
        for (int i = 0; i < FUNCTION_COL.length; i++) {
            Label label1 = new Label(i, currentRow1, info.getFunction1Value(FUNCTION_COL[i]));
            sheet1.addCell(label1);
        }

        //sheet 2
        WritableSheet sheet2 = wwb.getSheet(SHEET_ID_2);
        int currentRow2 = sheet2.getRows();
        for (int i = 0; i < UI_COL.length; i++) {
            Label label2 = new Label(i, currentRow2, info.getUi2Value(UI_COL[i]));
            sheet2.addCell(label2);
        }

        if (null != wwb) {
            wwb.write();
            wwb.close();
        }
    }

    private Label getLabelSheet0(int col, int raw, ExcelInfo info) {
        if (col >= COLUMNS_MUN || info == null) return null;

        switch (col) {
            case 0:
                return new Label(col, raw, info.mModule0.mPid);
            case 1:
                return new Label(col, raw, info.mModule0.mApplicationStart);
            case 2:
                return new Label(col, raw, info.mModule0.mApplicationInit);
            case 3:
                return new Label(col, raw, info.mModule0.mBroadcastStart);
            case 4:
                return new Label(col, raw, info.mModule0.mServiceStart);
            case 5:
                return new Label(col, raw, info.mModule0.mServiceInit);
            case 6:
                return new Label(col, raw, info.mModule0.mServiceCommand);
            default:
                return null;
        }
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
}
