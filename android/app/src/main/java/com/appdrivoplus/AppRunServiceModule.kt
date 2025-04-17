package com.appdrivoplus

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AppRunServiceModule(
    reactApplicationContext: ReactApplicationContext
): ReactContextBaseJavaModule(reactApplicationContext) {
    private val TAG = "AppRunServiceModule"
    override fun getName(): String = "RunServiceModule"

    @ReactMethod
    fun startService(promise: Promise) {
        Log.d(TAG, "Service Running")
        try {
            Intent(reactApplicationContext, MainServiceToRun::class.java).also {
                it.action = MainServiceToRun.Actions.START.toString()
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    reactApplicationContext.startForegroundService(it)
                }else {
                    reactApplicationContext.startService(it)
                }
                promise.resolve("Service Started")
            }
        }catch (e: Exception) {
            Log.d(TAG, e.stackTraceToString())
            promise.resolve(e.localizedMessage)
        }
    }

    @ReactMethod
    fun stopService(promise: Promise) {
        try {
            Intent(reactApplicationContext, MainServiceToRun::class.java).also {
                it.action = MainServiceToRun.Actions.STOP.toString()
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    reactApplicationContext.startForegroundService(it)
                }else {
                    reactApplicationContext.startService(it)
                }
                promise.resolve("Service Stopped")
            }
        }catch (e: Exception) {
            Log.d(TAG, e.stackTraceToString())
            promise.resolve(e.localizedMessage)
        }
    }
}