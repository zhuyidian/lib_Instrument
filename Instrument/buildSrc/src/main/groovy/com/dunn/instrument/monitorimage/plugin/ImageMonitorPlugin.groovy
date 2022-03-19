package com.dunn.instrument.monitorimage.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.dunn.instrument.monitorimage.TinkerPatchParams

class ImageMonitorPlugin implements Plugin<Project> {
    public static final String EXT_NAME = "tinkerPatch"

    @Override
    void apply(Project project) {
        // 传递参数
        project.extensions.create(EXT_NAME, TinkerPatchParams)
        println "ImageMonitorPlugin ---> apply project=" + project +", TinkerPatchParams.oldApk="+TinkerPatchParams

        // 这里怎么写？怎么样去修改 class ，asm，模板，transform
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new ImageMonitorTransform())
    }
}
