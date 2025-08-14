package com.dunn.demo.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.dunn.demo.activity.view.CameraPreviewLayout
import com.dunn.demo.activity.view.Webview1Layout
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class Webview1Activity: Activity() {
    private val TAG = "Webview1Activity"
    private val layout by lazy {
        Webview1Layout(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        layout.loadWebview(restart = true)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        layout.loadWebview(restart = false)
    }

    override fun onResume() {
        super.onResume()
        layout.onResume()
    }

    override fun onBackPressed() {
        layout.backPressed()
    }

    override fun onPause() {
        super.onPause()
        layout.onPause()
    }

    override fun onStop() {
        super.onStop()
        layout.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        layout.onDestroy()
    }
}