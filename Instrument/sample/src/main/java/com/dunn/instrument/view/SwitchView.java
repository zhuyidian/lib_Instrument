package com.dunn.instrument.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.dunn.instrument.R;

/**
 * Created by CXX on 2016/4/4.
 */
public class SwitchView extends View implements View.OnClickListener {

    private boolean isCheck;  //当前滑动开关状态
    private int slideButton;  //滑动图片资源文件
    private Bitmap bitmapSlide; //滑动图片Bitmap
    private Bitmap bgBitmap;  //背景图片资源文件
    private int MAX_LEFT;     //滑动最大左边距
    private int mSlideLeft;    //当前滑块左边距
    private float startX;
    private float endX;
    private int moveX;
    private boolean isClick = false;  //是否可以点击

    public SwitchView(Context context) {
        this(context, null);
    }

    public SwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        isCheck = attrs.getAttributeBooleanValue(R.styleable.SwitchView_check, false);
        slideButton = attrs.getAttributeResourceValue(R.styleable.SwitchView_beforeBg, R.mipmap.slide_button);
        initView();
    }

    private void initView() {
        Log.e("CXX", "isCheck" + isCheck);
        //将资源图片转换为Bitmap
        bitmapSlide = BitmapFactory.decodeResource(getResources(), slideButton);
        bgBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.switch_background);
        //计算离左边的最大距离
        MAX_LEFT = bgBitmap.getWidth() - bitmapSlide.getWidth();
        if (isCheck) {
            mSlideLeft = MAX_LEFT;
        } else {
            mSlideLeft = 0;
        }
        //刷新界面
        invalidate();
        setOnClickListener(this);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //手指按下时获取x的起点坐标
                startX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                //移动时获取手指移动x的最后坐标
                endX = event.getX();
                float dx = endX - startX;
                moveX = moveX + (int) Math.abs(dx);
                mSlideLeft = mSlideLeft + (int) dx;
                //防止超过边界
                if (mSlideLeft < 0) {
                    mSlideLeft = 0;
                } else if (mSlideLeft > MAX_LEFT) {
                    mSlideLeft = MAX_LEFT;
                }
                invalidate();
                startX = endX;
                break;
            case MotionEvent.ACTION_UP:
                Log.e("CXX", "moveX" + moveX);
                if (moveX > 5) {
                    isClick = false;
                } else {
                    isClick = true;
                }
                moveX = 0;
                if (!isClick) {
                    if (mSlideLeft < MAX_LEFT / 2) {
                        mSlideLeft = 0;
                        isCheck = false;
                    } else {
                        mSlideLeft = MAX_LEFT;
                        isCheck = true;
                    }
                    if (onCheckListener != null) {
                        onCheckListener.onCheck(SwitchView.this, isCheck);
                    }
                }

                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(bgBitmap.getWidth(), bgBitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bgBitmap, 0, 0, null);  //绘制底部背景
        canvas.drawBitmap(bitmapSlide, mSlideLeft, 0, null);  //绘制滑块
    }

    @Override
    public void onClick(View v) {
        if (!isClick) {
            return;
        }
        Log.e("CXX", "click" + isCheck);
        if (isCheck) {
            mSlideLeft = 0;
            isCheck = false;
        } else {
            mSlideLeft = MAX_LEFT;
            isCheck = true;
        }
        Log.e("CXX", "onCheck" + onCheckListener);
        //选中的回调
        if (onCheckListener != null) {
            onCheckListener.onCheck(SwitchView.this, isCheck);
        }
        invalidate();
    }

    private OnCheckListener onCheckListener;

    public void setOnCheckListener(OnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }

    public interface OnCheckListener {
        public void onCheck(View view, boolean isCheck);
    }
}
