package com.dunn.instrument;

import com.dunn.instrument.iomonitor.FileIOMonitor;
import com.dunn.instrument.thread.ThreadHelp;

/**
 * @ClassName: ApiIO
 * @Author: ZhuYiDian
 * @CreateDate: 2022/3/20 3:05
 * @Description:
 */
public class ApiIO {
    public static void ioStart(){
        FileIOMonitor.start();
    }
}
