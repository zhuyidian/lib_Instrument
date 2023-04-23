package com.dunn.instrument.function.keepalive;

import android.graphics.drawable.Drawable;

import java.util.Objects;

public class AppsBean {
	private String mLabel;
	private String mPackageName;
	private String mProcess;
	private int mPid;
	private Drawable mIcon;
	private AppStateCtl mCtl;

	public AppsBean() {

	}

	public AppsBean(String label, String packageName, String process, int pid) {
		super();
		this.mLabel = label;
		this.mPackageName = packageName;
		this.mProcess = process;
		this.mPid = pid;
	}

	public String getLabel() {
		return mLabel;
	}

	public void setLabel(String label) {
		this.mLabel = label;
	}

	public String getPackageName() {
		return mPackageName;
	}

	public void setPackageName(String packageName) {
		this.mPackageName = packageName;
	}

	public String getProcess() {
		return mProcess;
	}

	public void setProcess(String process) {
		this.mProcess = process;
	}

	public int getPid() {
		return mPid;
	}

	public void setPid(int pid) {
		this.mPid = pid;
	}

	public Drawable getIcon() {
		return mIcon;
	}

	public void setIcon(Drawable icon) {
		this.mIcon = icon;
	}

	public AppStateCtl getCtl() {
		return mCtl;
	}

	public void setCtl(AppStateCtl ctl) {
		this.mCtl = ctl;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AppsBean appsBean = (AppsBean) o;
		return Objects.equals(mPackageName, appsBean.mPackageName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mPackageName);
	}

	@Override
	public String toString() {
		return "AppsBean{" +
				"mLabel='" + mLabel + '\'' +
				", mPackageName='" + mPackageName + '\'' +
				", mProcess='" + mProcess + '\'' +
				", mPid=" + mPid +
				", mIcon=" + mIcon +
				", mCtl=" + mCtl +
				'}';
	}
}
