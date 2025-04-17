package com.appdrivoplus.camera_view

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp

@ReactModule(name = "CustomCameraView")
class CustomCameraManagerView : SimpleViewManager<CustomCameraPreview>() {
    override fun getName(): String = "CustomCameraView"

    override fun createViewInstance(context: ThemedReactContext): CustomCameraPreview {
        return CustomCameraPreview(context)
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @RequiresPermission(Manifest.permission.CAMERA)
    @ReactProp(name = "run")
    fun startCamera(view: CustomCameraPreview, run: Boolean) {
        if (run) {
            view.startCamera()
        }
    }
}
