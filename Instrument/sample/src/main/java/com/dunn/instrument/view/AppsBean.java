package com.dunn.instrument.view;

public class AppsBean {
	private String mPackageName;
	private String nameId;
	/** 名称 */
	private String name;
	/** 身份 */
	private String identity;
	/** 欢迎语 */
	private String welcome;
	private String imagePath;
	private String audioPath;

	public AppsBean() {

	}

	public AppsBean(String packageName, String nameId, String name, String identity, String welcome, String imagePath, String audioPath) {
		super();
		this.mPackageName = packageName;
		this.nameId = nameId;
		this.name = name;
		this.identity = identity;
		this.welcome = welcome;
		this.imagePath = imagePath;
		this.audioPath = audioPath;
	}

	public String getPackageName() {
		return mPackageName;
	}

	public void setPackageName(String packageName) {
		this.mPackageName = packageName;
	}

	public String getNameId() {
		return nameId;
	}

	public void setNameId(String nameId) {
		this.nameId = nameId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getWelcome() {
		return welcome;
	}

	public void setWelcome(String welcome) {
		this.welcome = welcome;
	}

	public String getSamplePath() {
		return imagePath;
	}

	public void setSamplePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getAudioPath() {
		return audioPath;
	}

	public void setAudioPath(String audioPath) {
		this.audioPath = audioPath;
	}

	@Override
	public String toString() {
		return "AppsBean{" +
				"mPackageName='" + mPackageName + '\'' +
				", nameId='" + nameId + '\'' +
				", name='" + name + '\'' +
				", identity='" + identity + '\'' +
				", welcome='" + welcome + '\'' +
				", imagePath='" + imagePath + '\'' +
				", audioPath='" + audioPath + '\'' +
				'}';
	}
}
