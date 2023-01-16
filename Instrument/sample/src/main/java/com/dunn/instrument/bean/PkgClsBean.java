package com.dunn.instrument.bean;

/**
 * @ClassName: PkgClsBean
 * @Author: ZhuYiDian
 * @CreateDate: 2023/1/16 15:22
 * @Description:
 */
public class PkgClsBean {
    public String mPackageName;
    public String mClassName;

    public PkgClsBean(String packageName, String className) {
        this.mPackageName = packageName;
        this.mClassName = className;
    }
}
