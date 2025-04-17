package com.appdrivoplus.ui_components

import android.content.Context
import android.graphics.Color
import android.widget.FrameLayout
import android.widget.TextView

class CustomTextView(context: Context): FrameLayout(context)  {
    var textView: TextView = TextView(context)

    init {
        addView(textView, LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        ))
    }

    fun setCustomText(text: String) {
        this.textView.text = text
    }
}
