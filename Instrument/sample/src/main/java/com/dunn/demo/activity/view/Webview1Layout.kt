package com.dunn.demo.activity.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.coocaa.custom.webview.CustomWebview
import com.coocaa.custom.webview.callback.IWebViewBusiness
import com.coocaa.custom.webview.data.CameraVideoWindowData
import com.coocaa.custom.webview.data.ChildAgentData
import com.coocaa.custom.webview.data.CommonIntentData
import com.coocaa.custom.webview.data.EnterRoomData
import com.coocaa.custom.webview.data.IntentData
import com.coocaa.custom.webview.data.LaunchAppForWeb
import com.coocaa.custom.webview.data.OnClickData
import com.coocaa.custom.webview.data.StartPageData
import com.coocaa.custom.webview.data.WebAccountInfo
import com.dunn.demo.R
import com.dunn.demo.utils.matchParent
import com.dunn.demo.utils.parentId
import com.dunn.demo.utils.updateLayoutParams
import com.dunn.frameworks.camera.Camera2Helper
import java.net.URLEncoder


open class Webview1Layout(context: Context) : FrameLayout(context) {
    val TAG = "Webview1Layout"
    private var isBackPressedIntercept = false

    //------------------------------webview---------------------------------------------------------
    private val customWebview = CustomWebview(context, object : IWebViewBusiness {
        override fun hideLodaingImage() {
            Log.d(TAG, "hideLodaingImage: <*** from web ")
        }

        override fun onProgressChanged(newProgress: Int) {
            if (newProgress > 99) {
                Log.d(TAG, "onProgressChanged: <*** from web newProgress=$newProgress")
            }
        }

        override fun onLoadFinish(url: String) {
            Log.d(TAG, "onLoadFinish: <*** from web ")
        }

        override fun getAccountInfo(): WebAccountInfo? {
            Log.d(TAG, "getAccountInfo: <*** from web ")
            return null
        }

        override fun getSystemSN(): String? {
            Log.d(TAG, "getSystemSN: <*** from web ")
            return ""
        }

        override fun getToken(): String? {
            Log.d(TAG, "getToken: <*** from web ")
            return ""
        }

        override fun getStudentInfo(): String? {
            Log.d(TAG, "getStudentInfo: <*** from web ")
            return ""
        }

        override fun getSystemInfo(): String? {
            Log.d(TAG, "getSystemInfo: <*** from web")
            return ""
        }

        override fun getOcr(): String? {
            Log.d(TAG, "getOcr: <*** from web ")
            return "123456"
        }

        override fun onClosePage() {
            Log.d(TAG, "onClosePage: <*** from web ")
            (context as Activity).finish()
        }

        override fun onClickData(data: OnClickData, paramJson: String) {
            try {
                Log.d(TAG, "onClickData: <*** from web data=$data")
                Log.d(TAG, "onClickData: <*** from web paramJson=$paramJson")
                //val onClickData = CcJSONUtil.parseObject(data, OnClickData::class.java)
                if (data != null) {
                    val intent = data.buildIntent(context)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    Log.d(TAG, "onClickData: <*** from web intent=$intent")
                    var pkg = intent?.component?.packageName.orEmpty()
                    if (pkg.isNullOrEmpty()) {
                        pkg = intent?.`package` ?: ""
                    }
                    intent.putExtra("paramJsonForWeb", paramJson)
                    context.startActivity(intent)
                }
            } catch (e: Exception) {
                Log.e(TAG, "onClickData: <*** from web e=$e")
            }
        }

        override fun onLaunchApp(data: LaunchAppForWeb) {
            try {
                Log.d(TAG, "onLaunchApp: <*** from web data=$data")
                var intent = data.buildIntent()
                Log.d(TAG, "onLaunchApp: <*** from web intent=$intent")
                // 获取 Intent 中的所有参数
                val extras = intent?.extras
                if (extras != null) {
                    for (key in extras.keySet()) {
                        val value = extras[key]
                        when (value) {
                            is String -> {
                                Log.d(TAG, "onLaunchApp: <*** from web is String Key=$key, Value=$value")
                            }

                            is Int -> {
                                Log.d(TAG, "onLaunchApp: <*** from web is Int Key=$key, Value=$value")
                            }

                            is Boolean -> {
                                Log.d(TAG, "onLaunchApp: <*** from web is Boolean Key=$key, Value=$value")
                            }

                            else -> {
                                Log.d(TAG, "onLaunchApp: <*** from web is Unknown Key=$key, Value=$value")
                            }
                        }
                    }
                } else {
                    Log.w(TAG, "onLaunchApp: <*** from web No extras found in the Intent.")
                }

                intent?.let {
                    var pkg = intent?.component?.packageName.orEmpty()
                    if (pkg.isNullOrEmpty()) {
                        pkg = intent?.`package` ?: ""
                    }
                    context.startActivity(it)
                }
            } catch (e: Exception) {
                Log.e(TAG, "onLaunchApp: <*** from web e=$e")
            }
        }

        override fun playVoice(data: String) {
            Log.d(TAG, "playVoice: <*** from web data=$data")
        }

        override fun playVoiceId(data: String, ttsId: String, speaker: String, speed: String) {
            Log.d(TAG, "playVoiceId: <*** from web ttsId=$ttsId, speaker=$speaker, speed=$speed, data=$data")
        }

        override fun stopVoice() {
            Log.d(TAG, "stopVoice: <*** from web ")
        }

        override fun getOriginUrl(): String? {
            Log.d(TAG, "getOriginUrl: <*** from web ")
            return "111111111111111111111111111111111"
        }

        override fun streamStart() {
            Log.d(TAG, "streamStart: <*** from web ")
        }

        override fun streamStartId(ttsId: String, speaker: String, speed: String) {
            Log.d(TAG, "streamStartId: <*** from web ttsId=$ttsId, speaker=$speaker, speed=$speed")
        }

        override fun streamMessage(message: String) {
            Log.d(TAG, "streamMessage: <*** from web message=$message")
        }

        override fun streamEnd() {
            Log.d(TAG, "streamEnd: <*** from web ")
        }

        override fun onWakeOn() {
            Log.d(TAG, "onWakeOn: <*** from web ")
        }

        override fun onWakeOff() {
            Log.d(TAG, "onWakeOff: <*** from web ")
        }

        override fun onStartSpeak() {
            Log.d(TAG, "onStartSpeak: <*** from web ")
        }

        override fun onStopSpeak(cancel: Boolean) {
            Log.d(TAG, "onStopSpeak: <*** from web cancel=$cancel")
        }

        override fun onTakeCamera() {
            Log.d(TAG, "onTakeCamera: <*** from web ")
        }

        override fun onTakePhoto() {
            Log.d(TAG, "onTakePhoto: <*** from web ")
        }

        override fun onStartMusic(data: String) {
            Log.d(TAG, "onStartMusic: <*** from web data=$data")
        }

        override fun onStopMusic() {
            Log.d(TAG, "onStopMusic: <*** from web ")
        }

        override fun onStartVideo(data: LaunchAppForWeb) {
            try {
                Log.d(TAG, "onStartVideo: <*** from web data=$data")
                var intent = data.buildIntent()
                Log.d(TAG, "onStartVideo: <*** from web intent=$intent")
                // 获取 Intent 中的所有参数
                val extras = intent?.extras
                if (extras != null) {
                    for (key in extras.keySet()) {
                        val value = extras[key]
                        when (value) {
                            is String -> {
                                Log.d(TAG, "onStartVideo: <*** from web is String Key=$key, Value=$value")
                            }

                            is Int -> {
                                Log.d(TAG, "onStartVideo: <*** from web is Int Key=$key, Value=$value")
                            }

                            is Boolean -> {
                                Log.d(TAG, "onStartVideo: <*** from web is Boolean Key=$key, Value=$value")
                            }

                            else -> {
                                Log.d(TAG, "onStartVideo: <*** from web is Unknown Key=$key, Value=$value")
                            }
                        }
                    }
                } else {
                    Log.w(TAG, "onStartVideo: <*** from web No extras found in the Intent.")
                }

                intent?.let {
                    context.startActivity(it)
                }
            } catch (e: Exception) {
                Log.e(TAG, "onStartVideo: <*** from web e=$e")
            }
        }

        override fun screenKeep(data: Boolean) {
            Log.d(TAG, "screenKeep: <*** from web data=$data")
        }

        override fun enterFull() {
            Log.d(TAG, "enterFull: <*** from web")
        }

        override fun exitFull() {
            Log.d(TAG, "exitFull: <*** from web")
        }

        override fun stopAsrFull() {
            Log.d(TAG, "stopAsrFull: <*** from web")
        }

        override fun setSpeaker(speaker: String) {
            Log.d(TAG, "setSpeaker: <*** from web speaker=$speaker")
        }

        override fun setSpeed(speed: String) {
            Log.d(TAG, "setSpeed: <*** from web speed=$speed")
        }

        override fun receiveIntent(data: CommonIntentData) {
            Log.d(TAG, "receiveIntent: <*** from web data=$data")
        }

        override fun enterChildAgent(data: ChildAgentData) {
            if (data.parentCode == 31) {
            }
        }

        override fun enterRoom(data: EnterRoomData) {
        }

        override fun exitRoom(isFinish: Boolean) {
        }

        override fun getBrainMachineStatus(): String? {
            return ""
        }

        override fun startCameraVideo(cameraVideoWindowData: CameraVideoWindowData) {
        }

        override fun stopCameraVideo() {
        }

        override fun backPressedIntercept(data: Boolean) {
            isBackPressedIntercept = data
        }
    })

    init {
        Log.i(TAG,"init:")
        setBackgroundColor(getResources().getColor(R.color.Grey))
        addView(
            customWebview?.getParentView(),
            FrameLayout.LayoutParams(matchParent, matchParent).apply {
//                gravity = Gravity.TOP or Gravity.LEFT
            })
    }

    fun onResume() {
        customWebview?.onResume()
    }

    fun onPause() {
        customWebview?.onPause()
        isBackPressedIntercept = false
    }

    fun onStop() {

    }

    fun onDestroy() {
        customWebview?.onDestroy()
    }

    // 使用示例
    @SuppressLint("MethodCallDetector")
    fun loadWebview(
        agentCode: String = "",
        id: Int = 0,
        agentContent: IntentData? = null,
        restart: Boolean = true,
    ) {
        val params = mapOf("code" to agentCode, "id" to if (id > 0) id.toString() else "")
        val data = StartPageData(agentCode = agentCode, id = if (id > 0) id.toString() else "")

        val url = "https://www.csdn.net/"
//        val finalUrl = buildUrl(url, params)
        val finalUrl = url
        Log.d(TAG, "loadWebview: restart=$restart")
        Log.d(TAG, "loadWebview: finalUrl=$finalUrl")
        Log.d(TAG, "loadWebview: data=$data")
        Log.d(TAG, "loadWebview: agentContent=$agentContent")
        customWebview.loadWebView(url = finalUrl, data = data, agentData = agentContent, restart)
    }

    /**
     * 是否能返回上一级
     */
    fun canGoBack(): Boolean {
        return customWebview?.canGoBack() ?: false
    }

    /**
     * 返回上一级
     */
    fun goBack() {
        customWebview?.goBack()
    }

    /**
     * 通知h5
     */
    fun backPressed() {
        Log.d(TAG, "backPressed-----------")
        if (isBackPressedIntercept) {
            customWebview?.getWebView()?.loadJsHolder?.quickCallJs("backPressedFromAndroid")
        } else {
            if (canGoBack()) {
                goBack()
            } else {
                (context as Activity).finish()
            }
        }
    }

    private fun buildUrl(baseUrl: String, params: Map<String, String>): String {
        val validParams = params.filter { !it.value.isNullOrEmpty() }
        return if (validParams.isNotEmpty()) {
            val query = validParams.entries.joinToString("&") {
                "${it.key}=${
                    URLEncoder.encode(
                        it.value,
                        "UTF-8"
                    )
                }"
            }
            "$baseUrl?$query"
        } else {
            baseUrl
        }
    }
}