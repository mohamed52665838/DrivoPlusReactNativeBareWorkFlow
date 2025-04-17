package com.appdrivoplus.camera_view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraCaptureSession
import android.media.ImageReader
import android.os.Build
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.bridge.ReactApplicationContext

class CustomCameraPreview(context: Context) : FrameLayout(context) {
    private val textureView = TextureView(context)
    private var isRunning = false
    private var cameraFeature: CameraFeature? = null

    init {
        // must be the first initialization
        addView(
            textureView, LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            @RequiresPermission(Manifest.permission.CAMERA)
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                cameraFeature = CameraFeature.getInstance().setCameraManager(
                    context.getSystemService(
                        Context.CAMERA_SERVICE
                    ) as CameraManager
                ).setPreviewSurface(Surface(textureView.surfaceTexture))
                cameraFeature?.startRecording(1, null)
                isRunning = true
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
//                closeCamera()
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @RequiresPermission(Manifest.permission.CAMERA)
    fun startCamera() {
        if (!isRunning) {
            cameraFeature?.startRecording(1, null)
            isRunning = true
        }
    }

    fun stopCamera() {
        if (isRunning) {
            cameraFeature?.closeCamera()
            isRunning = false
        }
    }
//    }

//    private fun openCamera() {
//        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
//        try {
//            val cameraId = cameraManager.cameraIdList[0] // Use back camera
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                return
//            }
//            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
//                @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//                override fun onOpened(device: CameraDevice) {
//                    cameraDevice = device
//                    startPreview()
//                }
//
//                override fun onDisconnected(device: CameraDevice) {
//                    device.close()
//                    cameraDevice = null
//                }
//
//                override fun onError(device: CameraDevice, error: Int) {
//                    device.close()
//                    cameraDevice = null
//                }
//            }, null)
//        } catch (e: CameraAccessException) {
//            e.printStackTrace()
//        }
//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    private fun startPreview() {
//        val surfaceTexture = textureView.surfaceTexture ?: return
//        surfaceTexture.setDefaultBufferSize(1080, 1920)
//        val imageReader = ImageReader.Builder(480,360).build()
//        val imageReaderSurface = imageReader.surface
//        val surface = Surface(surfaceTexture)
//        try {
//            val captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
//            captureRequestBuilder.addTarget(surface)
//
//            cameraDevice?.createCaptureSession(listOf(surface, imageReaderSurface), object : CameraCaptureSession.StateCallback() {
//                override fun onConfigured(session: CameraCaptureSession) {
//                    cameraCaptureSession = session
//
//                    session.setRepeatingRequest(captureRequestBuilder.build(), null, null)
//                }
//
//                override fun onConfigureFailed(session: CameraCaptureSession) {}
//            }, null)
//        } catch (e: CameraAccessException) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun closeCamera() {
//        cameraCaptureSession?.close()
//        cameraDevice?.close()
//        cameraCaptureSession = null
//        cameraDevice = null
//    }
}