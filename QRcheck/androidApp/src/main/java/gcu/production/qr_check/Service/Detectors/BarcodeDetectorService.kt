@file:Suppress("DEPRECATION")

package gcu.production.qrcheck.android.Service.Detectors

import android.annotation.SuppressLint
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.coroutines.DelicateCoroutinesApi
import java.io.IOException

@DelicateCoroutinesApi
internal class BarcodeDetectorService(
    private val surfaceView: SurfaceView
    , actionDetected: (barcodeData: String) -> Unit)
    : BarcodeProcessor(actionDetected = actionDetected)
    , SurfaceHolder.Callback
{
    private val barcodeDetector =
        BarcodeDetector
            .Builder(this.surfaceView.context)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
    private lateinit var cameraSource: CameraSource

    @SuppressLint("MissingPermission")
    internal fun launchBarcodeDetector()
    {
        this.cameraSource = cameraSourceBuilder()
        this.barcodeDetector.setProcessor(this)

        this.surfaceView
          .holder
          .addCallback(this)
        surfaceCreated(this.surfaceView.holder)
    }

    @Synchronized
    private fun cameraSourceBuilder() =
        CameraSource.Builder(this.surfaceView.context, this.barcodeDetector)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedPreviewSize(1920, 1080)
            .setRequestedFps(25.0f)
            .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
            .build()

    @SuppressLint("MissingPermission")
    override fun surfaceCreated(p0: SurfaceHolder) {
        try
        {
            this.cameraSource.start(this.surfaceView.holder)
        } catch (ignore: IOException) { }
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        this.cameraSource.stop()
    }

    override fun surfaceChanged(
        p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}
}