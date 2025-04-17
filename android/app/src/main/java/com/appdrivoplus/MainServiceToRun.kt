package com.appdrivoplus

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.appdrivoplus.camera_view.CameraFeature
import com.facebook.react.ReactApplication
import com.facebook.react.modules.core.DeviceEventManagerModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainServiceToRun: Service() {

    private val CHANNEL_ID = "my_foreground_channel"
    private var isRunning = false
    var counter = 0
    val coroutineScope = CoroutineScope(Dispatchers.Unconfined)
    // this instance should have the camera preview and every thing we need to run recording at this point
    val cameraFeature = CameraFeature.getInstance()
    val imageReader = ImageReader.newInstance(
        480,
        360,
        ImageFormat.YUV_420_888,
        2
    )
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        imageReader.setOnImageAvailableListener(
            { reader ->
                var image: Image? = null
                try {
                    image = reader.acquireLatestImage()
                    if (image != null) {
                        // Process the image here
                        val bytes = yuv420888ToJpegBytes(image)
                        println(bytes.size)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    image?.close()
                }
            }, null)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH // no sound
            )
            serviceChannel.description = "Used for running foreground service silently"

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(serviceChannel)
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when(action) {
            Actions.START.toString() ->  {
                val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Service Active")
                    .setContentText("Running in foreground")
                    .setSmallIcon(R.drawable.rn_edit_text_material)
                    .build()

                startForeground(1, notification)
                isRunning = true
                cameraFeature.startRecordingWithService(1, imageReader.surface)
            }
            Actions.STOP.toString() ->  {
                if(isRunning) {
                    coroutineScope.cancel()
                    cameraFeature.startRecordingWithService(1, null)
                    stopSelf()
                    println("Service Stopped Here")
                    isRunning = false
                }
            }
            else ->  {
                throw Exception("Action Error: Action Not Specified Correctly")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    enum class Actions {
        START, STOP
    }

    fun yuv420888ToJpegBytes(image: Image, quality: Int = 90): ByteArray {

        val width = image.width
        val height = image.height

        fun yuv420888ToNv21(image: Image): ByteArray {

            val ySize = width * height
            val uvSize = width * height / 2

            val nv21 = ByteArray(ySize + uvSize)

            val yBuffer = image.planes[0].buffer
            val uBuffer = image.planes[1].buffer
            val vBuffer = image.planes[2].buffer

            val yRowStride = image.planes[0].rowStride
            val uvRowStride = image.planes[1].rowStride
            val uvPixelStride = image.planes[1].pixelStride

            // Copy Y channel
            var pos = 0
            for (row in 0 until height) {
                yBuffer.position(row * yRowStride)
                yBuffer.get(nv21, pos, width)
                pos += width
            }

            // Copy UV channels
            val uvHeight = height / 2
            for (row in 0 until uvHeight) {
                var col = 0
                while (col < width) {
                    val uvPos = row * uvRowStride + (col / 2) * uvPixelStride
                    vBuffer.position(uvPos)
                    nv21[pos++] = vBuffer.get()
                    uBuffer.position(uvPos)
                    nv21[pos++] = uBuffer.get()
                    col += 2
                }
            }

            return nv21
        }

        val nv21 = yuv420888ToNv21(image)
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        val outStream = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), quality, outStream)

        return outStream.toByteArray()
    }
}