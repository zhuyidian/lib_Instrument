package com.dunn.instrument.view.dialog;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dunn.instrument.R;
import com.dunn.instrument.function.keepalive.AppStateCtl;

/**
 * 选择操作Dialog
 */
public class DelayShowDialog extends Dialog implements View.OnClickListener , View.OnFocusChangeListener {
    public static String TAG = "DelayShowDialog";
    private static DelayShowDialog mDelayShowDialog;

    public static DelayShowDialog init(Context context) {
        return new DelayShowDialog(context);
    }

    private Context mContext;
    private AppStateCtl mCtl;
    private LinearLayout tv_delay_item1, tv_delay_item3, tv_delay_item4,tv_delay_item5;
    private TextView tv_delay_hide1,tv_delay_hide2;
    private CheckBox tv_delay_checkbox1,tv_delay_checkbox3,tv_delay_checkbox4,tv_delay_checkbox5;
    private RelativeLayout tv_ok_item;
    private TCallBack<String, String> onFinishListenner;

    /**
     * 初始化构造函数
     */
    public DelayShowDialog(Context mContext) {
        super(mContext, R.style.Theme_Light_FullScreenDialogAct);
        // 解决小米出现 X给截取一半（状态栏遮盖的问题）
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 绑定Layout
        this.setContentView(R.layout.dialog_delay_show);
        // ==------------------------------------------==
        Window window = DelayShowDialog.this.getWindow();
        window.setGravity(Gravity.RIGHT|Gravity.BOTTOM);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
//        lp.alpha = 1.0f;

        // 禁止点击其他地方自动关闭
        this.setCanceledOnTouchOutside(false);
        this.mContext = mContext;
        // ============ 初始化控件 =================
        //holder.numview.setText(String.format(parent.getContext().getString(vstc.device.smart.R.string.smart_find_carema),data.size()));

        initViews();
        initValues();
        initListener();
    }

    private void initViews() {
        tv_delay_item1 = (LinearLayout) findViewById(R.id.tv_delay_item1);
        tv_delay_item3 = (LinearLayout) findViewById(R.id.tv_delay_item3);
        tv_delay_item4 = (LinearLayout) findViewById(R.id.tv_delay_item4);
        tv_delay_item5 = (LinearLayout) findViewById(R.id.tv_delay_item5);
        tv_ok_item = (RelativeLayout) findViewById(R.id.tv_ok_item);

        tv_delay_hide1 = (TextView) findViewById(R.id.tv_delay_hide1);
        tv_delay_hide2 = (TextView) findViewById(R.id.tv_delay_hide2);

        tv_delay_checkbox1 = (CheckBox) findViewById(R.id.tv_delay_checkbox1);
        tv_delay_checkbox3 = (CheckBox) findViewById(R.id.tv_delay_checkbox3);
        tv_delay_checkbox4 = (CheckBox) findViewById(R.id.tv_delay_checkbox4);
        tv_delay_checkbox5 = (CheckBox) findViewById(R.id.tv_delay_checkbox5);
    }

    private void initValues() {
    }

    private void initListener() {
        tv_delay_item1.setOnClickListener(this);
        tv_delay_item3.setOnClickListener(this);
        tv_delay_item4.setOnClickListener(this);
        tv_delay_item5.setOnClickListener(this);
        tv_ok_item.setOnClickListener(this);

        tv_delay_item1.setOnFocusChangeListener(this);
        tv_delay_item3.setOnFocusChangeListener(this);
        tv_delay_item4.setOnFocusChangeListener(this);
        tv_delay_item5.setOnFocusChangeListener(this);
        tv_ok_item.setOnFocusChangeListener(this);
    }

    /**
     * 关闭Dialog
     */
    public void cancelDialog() {
        if (this.isShowing()) {
            this.cancel();
        }
    }

    /**
     * 显示提示框
     */
    public void showDialog(AppStateCtl ctl, TCallBack<String, String> onClickListener) {
        this.mCtl = ctl;
        this.onFinishListenner = onClickListener;
        setChoice();
        this.show();
    }

    private void setChoice(){
        if(mCtl.getIsAutoStartByUserCtl()){
            tv_delay_hide1.setText("允许/不允许");
            tv_delay_hide1.setTextColor(mContext.getResources().getColor(R.color.tc_99));
            if(mCtl.getIsAutoStartToAllow()==1){
                tv_delay_checkbox1.setChecked(true);
            }else if(mCtl.getIsAutoStartToAllow()==0){
                tv_delay_checkbox1.setChecked(false);
            }else{
                tv_delay_checkbox1.setChecked(false);
                tv_delay_hide1.setText("未知");
                tv_delay_hide1.setTextColor(mContext.getResources().getColor(R.color.red));
            }
        }else{
            tv_delay_hide1.setText("错误");
            tv_delay_hide1.setTextColor(mContext.getResources().getColor(R.color.red));
        }

        if(mCtl.getIsBackgroundRunByUserCtl()){
            tv_delay_hide2.setText("智能调控/保持后台运行/限制后台运行");
            tv_delay_hide2.setTextColor(mContext.getResources().getColor(R.color.tc_99));
            if(mCtl.getBackgroundRunToStrategy()==0){
                tv_delay_checkbox3.setChecked(true);
                tv_delay_checkbox4.setChecked(false);
                tv_delay_checkbox5.setChecked(false);
            }else if(mCtl.getBackgroundRunToStrategy()==2){
                tv_delay_checkbox4.setChecked(true);
                tv_delay_checkbox3.setChecked(false);
                tv_delay_checkbox5.setChecked(false);
            }else if(mCtl.getBackgroundRunToStrategy()==1){
                tv_delay_checkbox5.setChecked(true);
                tv_delay_checkbox3.setChecked(false);
                tv_delay_checkbox4.setChecked(false);
            }else{
                tv_delay_checkbox3.setChecked(false);
                tv_delay_checkbox4.setChecked(false);
                tv_delay_checkbox5.setChecked(false);
                tv_delay_hide2.setText("未知");
                tv_delay_hide2.setTextColor(mContext.getResources().getColor(R.color.red));
            }
        }else{
            tv_delay_hide2.setText("错误");
            tv_delay_hide2.setTextColor(mContext.getResources().getColor(R.color.red));
        }
    }

    @Override
    public void show() {
        super.show();
        tv_delay_item1.requestFocus();
    }

    @Override
    public void onClick(View view) {
        if (onFinishListenner == null) return;
        switch (view.getId()) {
            case R.id.tv_delay_item1:
                if(mCtl.getIsAutoStartToAllow()==0) mCtl.setIsAutoStartToAllow(1);
                if(mCtl.getIsAutoStartToAllow()==1) mCtl.setIsAutoStartToAllow(0);
                if(mCtl.getIsAutoStartToAllow()==1){
                    tv_delay_checkbox1.setChecked(true);
                }else{
                    tv_delay_checkbox1.setChecked(false);
                }
                break;
            case R.id.tv_delay_item3:
                tv_delay_checkbox3.setChecked(true);
                tv_delay_checkbox4.setChecked(false);
                tv_delay_checkbox5.setChecked(false);
                mCtl.setBackgroundRunToStrategy(0);
                break;
            case R.id.tv_delay_item4:
                tv_delay_checkbox4.setChecked(true);
                tv_delay_checkbox3.setChecked(false);
                tv_delay_checkbox5.setChecked(false);
                mCtl.setBackgroundRunToStrategy(2);
                break;
            case R.id.tv_delay_item5:
                tv_delay_checkbox5.setChecked(true);
                tv_delay_checkbox3.setChecked(false);
                tv_delay_checkbox4.setChecked(false);
                mCtl.setBackgroundRunToStrategy(1);
                break;
            case R.id.tv_ok_item:
                retrueDelayTime("ok");
                cancelDialog();
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (onFinishListenner == null) return;
        switch (view.getId()) {
            case R.id.tv_delay_item1:
                if(b){
                    tv_delay_item1.setBackgroundColor(mContext.getResources().getColor(R.color.color_black));
                }else{
                    tv_delay_item1.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                }
                break;
            case R.id.tv_delay_item3:
                if(b){
                    tv_delay_item3.setBackgroundColor(mContext.getResources().getColor(R.color.color_black));
                }else{
                    tv_delay_item3.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                }
                break;
            case R.id.tv_delay_item4:
                if(b){
                    tv_delay_item4.setBackgroundColor(mContext.getResources().getColor(R.color.color_black));
                }else{
                    tv_delay_item4.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                }
                break;
            case R.id.tv_delay_item5:
                if(b){
                    tv_delay_item5.setBackgroundColor(mContext.getResources().getColor(R.color.color_black));
                }else{
                    tv_delay_item5.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                }
                break;
            case R.id.tv_ok_item:
                if(b){
                    tv_ok_item.setBackground(mContext.getResources().getDrawable(R.drawable.bg_white_corner_black_side));
                }else{
                    tv_ok_item.setBackground(mContext.getResources().getDrawable(R.drawable.bg_white_corner_white_side));
                }
                break;
        }
    }

    private void retrueDelayTime(String text) {
        onFinishListenner.call("", text);
    }
}
