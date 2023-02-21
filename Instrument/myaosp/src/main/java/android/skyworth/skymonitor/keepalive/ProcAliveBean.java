package android.skyworth.skymonitor.keepalive;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author:zhuyidian
 * Date:2023/2/21 17:53
 * Description:ProcAliveBean
 */
public class ProcAliveBean implements Parcelable {
    private String mPackageName;
    private String mProcess;

    public static final Creator<ProcAliveBean> CREATOR = new Creator<ProcAliveBean>() {
        public ProcAliveBean createFromParcel(Parcel in) {
            return new ProcAliveBean(in);
        }

        public ProcAliveBean[] newArray(int size) {
            return new ProcAliveBean[size];
        }
    };

    public ProcAliveBean(){}

    public ProcAliveBean(String packageName, String process) {
        this.mPackageName = packageName;
        this.mProcess = process;
    }

    private ProcAliveBean(Parcel in) {
        this.mPackageName = in.readString();
        this.mProcess = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPackageName);
        dest.writeString(this.mProcess);
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

    @Override
    public String toString() {
        return "ProcAliveBean{" +
                "mPackageName='" + mPackageName + '\'' +
                ", mProcess='" + mProcess + '\'' +
                '}';
    }
}
