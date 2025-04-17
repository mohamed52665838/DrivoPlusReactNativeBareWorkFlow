package com.appdrivoplus.camera_view

import android.Manifest
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.params.OutputConfiguration
import android.os.Build
import android.view.Surface
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission

// open camera => create session (where define list of buffers) => create capture request

class CameraFeature private constructor() {
    private lateinit var cameraManager: CameraManager // obtained at creation time
    private lateinit var previewSurface: Surface // obtained at creation time

    private lateinit var cameraSession: CameraCaptureSession // configuration inside
    private var cameraDevice: CameraDevice? = null // configuration inside

    @RequiresPermission(Manifest.permission.CAMERA)
    private fun configureSession(cameraId: Int, onCameraOpened: () -> Unit) {
        if (cameraId > cameraManager.cameraIdList.lastIndex) {
            throw IllegalArgumentException("Error: Camera id out of range you got only ${cameraManager.cameraIdList.size} Camera Devices")
        }
        val cameraId = cameraManager.cameraIdList[cameraId] // Camera Id

        val stateCallbacks = object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                // GOT THE CAMERA DEVICE
                cameraDevice = camera
                onCameraOpened()
            }

            override fun onDisconnected(camera: CameraDevice) {
                // CAMERA DEVICE DISCONNECT TODO: HANDLE DISCONNECT
            }

            override fun onError(camera: CameraDevice, error: Int) {
                // ERROR TODO: HANDLE ERROR
            }

        }

        cameraManager.openCamera(cameraId, stateCallbacks, null)
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startRecording(cameraId: Int, imageReaderSurface: Surface?) {
        configureSession(cameraId) {
            try {
                val targets = mutableListOf(previewSurface)
                val captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequestBuilder.addTarget(previewSurface)
                imageReaderSurface?.let {
                    captureRequestBuilder.addTarget(it)
                    targets.add(it)
                }

                cameraDevice?.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        cameraSession = session
                        session.setRepeatingRequest(captureRequestBuilder.build(), null, null)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {}
                }, null)

            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }


    @RequiresPermission(Manifest.permission.CAMERA)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startRecordingWithService(cameraId: Int, imageReaderSurface: Surface?) {
           if(!::previewSurface.isInitialized) {
              return
           }
        if (cameraDevice == null) {
            configureSession(cameraId) {
                startRecordingWithService(cameraId, imageReaderSurface) // try again once opened
            }
            return
        }
        try {
            if (::cameraSession.isInitialized) {
                cameraSession.abortCaptures()
                cameraSession.close()
            }

            val targets = mutableListOf(previewSurface)
            val captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(previewSurface)
            imageReaderSurface?.let {
                captureRequestBuilder.addTarget(it)
                targets.add(it)
            }

            cameraDevice?.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    cameraSession = session
                    session.setRepeatingRequest(captureRequestBuilder.build(), null, null)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {}
            }, null)

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }


    fun closeCamera() {
        cameraSession.close()
        cameraDevice?.close()
    }

    fun setCameraManager(cameraManager: CameraManager): CameraFeature {
        this.cameraManager = cameraManager
        return this
    }

    fun setPreviewSurface(previewSurface: Surface): CameraFeature {
        this.previewSurface = previewSurface
        return this
    }
    companion object {
        @Volatile private var _instance: CameraFeature? = null
        fun getInstance(): CameraFeature {
           return _instance ?: synchronized(this) {
               _instance ?: CameraFeature().also { _instance = it }
           }
        }
    }

}