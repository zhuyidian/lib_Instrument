package com.dunn.instrument.function.execmd;

/**
 * @ClassName: CmdBean
 * @Author: ZhuYiDian
 * @CreateDate: 2024/07/29
 * @Description:
 */
public class CmdBean {
    public String cmd;
    public boolean isBack;
    public boolean isClose;

    public CmdBean(){

    }

    public CmdBean(String cmd, boolean isBack, boolean isClose) {
        this.cmd = cmd;
        this.isBack = isBack;
        this.isClose = isClose;
    }
}
