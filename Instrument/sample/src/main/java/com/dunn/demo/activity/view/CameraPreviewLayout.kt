package com.dunn.demo.activity.view

import android.content.Context
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.TextureView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.dunn.demo.R
import com.dunn.demo.activity.data.CameraVideoWindowData
import com.dunn.demo.utils.parentId
import com.dunn.demo.utils.updateLayoutParams
import com.dunn.frameworks.camera.Camera2Helper


open class CameraPreviewLayout(context: Context) : FrameLayout(context) {
    val TAG = "CameraPreviewLayout"
    var isStopCamera = false

    private lateinit var cameraHelper: Camera2Helper
    private val recordingImg by lazy {
        ImageView(context).apply {
            id = View.generateViewId()
            visibility = GONE
            setImageResource(R.drawable.ic_recording)
        }
    }
    private val textureViewBg by lazy {
        ImageView(context).apply {
            id = View.generateViewId()
            visibility = GONE
            setImageResource(R.drawable.ic_camera_bg)
            setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startCameraView()
                }
            }
        }
    }
    private val textureView by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RoundedTextureView(context).apply {
                id = generateViewId()
                // 设置圆角半径（单位：像素）
                setCornerRadius(24f)
                visibility = GONE
                setOnClickListener {
                    stopCameraView()
                }
            }
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        }
    }

    init {
        Log.i(TAG,"init:")
        setBackgroundColor(getResources().getColor(R.color.Grey))
        addView(textureView, FrameLayout.LayoutParams(400, 300).apply {
            gravity = Gravity.CENTER
        })
        addView(textureViewBg, FrameLayout.LayoutParams(400, 300).apply {
            gravity = Gravity.CENTER
        })
        addView(recordingImg, FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
            topMargin = 300
        })
        cameraHelper = Camera2Helper(context, textureView)

        textureViewBg.visibility = VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun startCameraView() {
        isStopCamera = false
        Log.i(TAG,"startCameraView: isStopCamera=$isStopCamera")
        cameraHelper.startCamera()
        textureViewBg.visibility = GONE
        textureView.visibility = VISIBLE
        recordingImg.visibility = VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun stopCameraView() {
        isStopCamera = true
        Log.i(TAG,"stopCameraView: isStopCamera=$isStopCamera")
        cameraHelper.stopCamera()
        textureViewBg.visibility = VISIBLE
        textureView.visibility = GONE
        recordingImg.visibility = GONE
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun hideCameraView() {
        textureView.visibility = GONE
        recordingImg.visibility = GONE
        textureViewBg.visibility = GONE
        cameraHelper.stopCamera()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun showCameraView(data: CameraVideoWindowData) {
        Log.d(TAG, "showCameraView---" + data.toString())
        textureView.updateLayoutParams<LayoutParams> {
            width = data.w
            height = data.h
            leftMargin = data.x
            topMargin = data.y
        }
        textureView.postDelayed({
            cameraHelper.startCamera()
        }, 200)
        textureViewBg.updateLayoutParams<LayoutParams> {
            width = data.w
            height = data.h
            leftMargin = data.x
            topMargin = data.y
        }
        recordingImg.visibility = VISIBLE
        textureView.visibility = VISIBLE
        textureViewBg.visibility = GONE

        recordingImg.updateLayoutParams<LayoutParams> {
            leftMargin = (data.x + 24)
            topMargin = (data.y + 20)
        }
    }
}