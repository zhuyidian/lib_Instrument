package com.dunn.instrument.function.keepalive;

/**
 * @ClassName: AppStateCtl
 * @Author: ZhuYiDian
 * @CreateDate: 2023/3/1 14:23
 * @Description:
 */
public class AppStateCtl {
    //是否用户控制自启动
    private boolean mIsAutoStartByUserCtl;
    /*
	是否允许自启动:
	true:允许
	false:不允许
	*/
    private boolean mIsAutoStartToAllow;

    //是否用户控制后台运行
    private boolean mIsBackgroundRunByUserCtl;
    /*
    后台运行策略:
    smart: 智能调控
    keep: 保持后台运行
    limit: 限制后台运行
    */
    private String mBackgroundRunToStrategy = "smart";

    //排序使用
    private int mScore = 0;

    public AppStateCtl() {
        culScore();
    }

    public boolean getIsAutoStartByUserCtl(){
        return mIsAutoStartByUserCtl;
    }

    public void setIsAutoStartByUserCtl(boolean isAutoStartByUserCtl){
        this.mIsAutoStartByUserCtl = isAutoStartByUserCtl;
    }

    public boolean getIsAutoStartToAllow() {
        return mIsAutoStartToAllow;
    }

    public void setIsAutoStartToAllow(boolean isAutoStartToAllow) {
        mIsAutoStartToAllow = isAutoStartToAllow;
        culScore();
    }

    public boolean getIsBackgroundRunByUserCtl() {
        return mIsBackgroundRunByUserCtl;
    }

    public void setIsBackgroundRunByUserCtl(boolean isBackgroundRunByUserCtl) {
        mIsBackgroundRunByUserCtl = isBackgroundRunByUserCtl;
    }

    public String getBackgroundRunToStrategy() {
        return mBackgroundRunToStrategy;
    }

    public void setBackgroundRunToStrategy(String backgroundRunToStrategy) {
        mBackgroundRunToStrategy = backgroundRunToStrategy;
        culScore();
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        this.mScore = score;
    }

    private void culScore(){
        int val = 0;
        if(mIsAutoStartToAllow){
            val+=2;
        }else{
            val+=1;
        }
        if("smart".equals(mBackgroundRunToStrategy)){
            val+=5;
        }else if("keep".equals(mBackgroundRunToStrategy)){
            val+=4;
        }else if("limit".equals(mBackgroundRunToStrategy)){
            val+=3;
        }
        mScore = val;
    }

    @Override
    public String toString() {
        return "AppStateCtl{" +
                "mIsAutoStartByUserCtl=" + mIsAutoStartByUserCtl +
                ", mIsAutoStartToAllow=" + mIsAutoStartToAllow +
                ", mIsBackgroundRunByUserCtl=" + mIsBackgroundRunByUserCtl +
                ", mBackgroundRunToStrategy='" + mBackgroundRunToStrategy + '\'' +
                ", mScore=" + mScore +
                '}';
    }
}
