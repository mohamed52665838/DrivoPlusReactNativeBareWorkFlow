package com.appdrivoplus

import com.appdrivoplus.camera_view.CustomCameraManagerView
import com.appdrivoplus.ui_components.CustomTextViewManager
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

class AppRunServicePackage: ReactPackage {
    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        return listOf(
            AppRunServiceModule(reactContext)
        )
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<in Nothing, in Nothing>> {
        return listOf(CustomTextViewManager(), CustomCameraManagerView())
    }
}