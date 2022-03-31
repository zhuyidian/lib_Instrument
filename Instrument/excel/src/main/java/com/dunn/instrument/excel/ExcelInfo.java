package com.dunn.instrument.excel;

import java.util.HashMap;
import java.util.Map;

import static com.dunn.instrument.excel.ExcelDeal.FUNCTION_COL;
import static com.dunn.instrument.excel.ExcelDeal.UI_COL;

/**
 * @ClassName: ExcelInfo
 * @Author: ZhuYiDian
 * @CreateDate: 2022/3/8 17:48
 * @Description:
 */
public class ExcelInfo {
    public Module0 mModule0 = new Module0();
    private Map<String, String> mFunction1Value = new HashMap<String, String>();
    private Map<String, String> mUi2Value = new HashMap<String, String>();

    public ExcelInfo() {
        for(int i=0;i<FUNCTION_COL.length;i++){
            mFunction1Value.put(FUNCTION_COL[i], "");
        }
        for(int i=0;i<UI_COL.length;i++){
            mUi2Value.put(UI_COL[i], "");
        }
    }

    public ExcelInfo(ExcelInfo info) {
        //sheet 0
        mModule0.mPid = info.mModule0.mPid;
        mModule0.mApplicationStart = info.mModule0.mApplicationStart;
        mModule0.mApplicationInit = info.mModule0.mApplicationInit;
        mModule0.mBroadcastStart = info.mModule0.mBroadcastStart;
        mModule0.mServiceStart = info.mModule0.mServiceStart;
        mModule0.mServiceInit = info.mModule0.mServiceInit;
        mModule0.mServiceCommand = info.mModule0.mServiceCommand;

        //sheet 1
        for(Map.Entry<String,String> entry:info.mFunction1Value.entrySet()){
            mFunction1Value.put(entry.getKey(),entry.getValue());
        }

        //sheet 2
        for(Map.Entry<String,String> entry:info.mUi2Value.entrySet()){
            mUi2Value.put(entry.getKey(),entry.getValue());
        }
    }

    public void setFunction1Value(String key,String value){
        mFunction1Value.put(key,value);
    }

    public String getFunction1Value(String key){
        return mFunction1Value.get(key);
    }

    public void setUi2Value(String key,String value){
        mUi2Value.put(key,value);
    }

    public String getUi2Value(String key){
        return mUi2Value.get(key);
    }

    /**
     * sheet 0 ：总模块数据统计
     */
    public class Module0{
        public String mPid;
        public String mApplicationStart;
        public String mApplicationInit;
        public String mBroadcastStart;
        public String mServiceStart;
        public String mServiceInit;
        public String mServiceCommand;

        @Override
        public String toString() {
            return "Module0{" +
                    "mPid='" + mPid + '\'' +
                    ", mApplicationStart='" + mApplicationStart + '\'' +
                    ", mApplicationInit='" + mApplicationInit + '\'' +
                    ", mBroadcastStart='" + mBroadcastStart + '\'' +
                    ", mServiceStart='" + mServiceStart + '\'' +
                    ", mServiceInit='" + mServiceInit + '\'' +
                    ", mServiceCommand='" + mServiceCommand + '\'' +
                    '}';
        }
    }
}
