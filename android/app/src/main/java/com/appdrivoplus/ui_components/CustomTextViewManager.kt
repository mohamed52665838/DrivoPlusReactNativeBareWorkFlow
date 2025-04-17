package com.appdrivoplus.ui_components

import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp


@ReactModule(name = "CustomTextview")
class CustomTextViewManager: SimpleViewManager<CustomTextView>() {
    override fun getName(): String = "CustomTextView"

    override fun createViewInstance(context: ThemedReactContext): CustomTextView {
        return CustomTextView(context)
    }

    @ReactProp(name="text")
    fun setText(view: CustomTextView, text: String) {
        view.setCustomText(text)
    }

}