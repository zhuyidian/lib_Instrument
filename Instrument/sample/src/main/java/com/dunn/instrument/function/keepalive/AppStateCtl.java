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
	0(不允许启动)
	1(允许启动)
	*/
    private int mIsAutoStartToAllow;

    //是否用户控制后台运行
    private boolean mIsBackgroundRunByUserCtl;
    /*
    后台运行策略:
    0(智能调控)
    1(限制运行)
    2(保持运行)
    */
    private int mBackgroundRunToStrategy;

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

    public int getIsAutoStartToAllow() {
        return mIsAutoStartToAllow;
    }

    public void setIsAutoStartToAllow(int isAutoStartToAllow) {
        mIsAutoStartToAllow = isAutoStartToAllow;
        culScore();
    }

    public boolean getIsBackgroundRunByUserCtl() {
        return mIsBackgroundRunByUserCtl;
    }

    public void setIsBackgroundRunByUserCtl(boolean isBackgroundRunByUserCtl) {
        mIsBackgroundRunByUserCtl = isBackgroundRunByUserCtl;
    }

    public int getBackgroundRunToStrategy() {
        return mBackgroundRunToStrategy;
    }

    public void setBackgroundRunToStrategy(int backgroundRunToStrategy) {
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
        if(mIsAutoStartToAllow==1){
            val+=2;
        }else{
            val+=1;
        }
        if(mBackgroundRunToStrategy==0){
            val+=5;
        }else if(mBackgroundRunToStrategy==2){
            val+=4;
        }else if(mBackgroundRunToStrategy==1){
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
