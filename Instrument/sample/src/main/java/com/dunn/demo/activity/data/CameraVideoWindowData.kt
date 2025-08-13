package com.dunn.demo.activity.data

import android.util.Xml
import java.io.Serializable

data class CameraVideoWindowData(
    var x: Int = -1,
    var y: Int = -1,
    var w: Int = -1,
    var h: Int = -1
): Serializable {

    override fun toString(): String {
        return "CameraVideoWindowData(x=$x, y=$y, w=$w, h=$h)"
    }
}