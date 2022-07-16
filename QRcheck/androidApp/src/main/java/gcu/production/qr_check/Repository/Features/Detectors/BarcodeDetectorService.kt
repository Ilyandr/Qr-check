@file:Suppress("DEPRECATION", "PackageName")
package gcu.production.qr_check.Repository.Features.Detectors

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException

typealias inputBarcode =
            (barcodeData: String) -> Unit

internal class BarcodeDetectorService @AssistedInject constructor(
    @Assisted("actionDetected")
    actionDetected: inputBarcode
) : BarcodeProcessor(actionDetected = actionDetected), SurfaceHolder.Callback
{
    internal lateinit var surfaceView: SurfaceView

    private val barcodeDetector: BarcodeDetector by lazy {
        BarcodeDetector
            .Builder(this.surfaceView.context)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
    }

    private val cameraSource: CameraSource by lazy {
        cameraSourceBuilder()
    }

    internal infix fun launchBarcodeDetector(surfaceView: SurfaceView)
    {
        this.surfaceView = surfaceView
        this.surfaceView.background = null

        this.barcodeDetector.setProcessor(this)
        this.surfaceView.holder.addCallback(this)
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

    companion object
    {
        fun FragmentActivity.checkCameraPermissions(): Boolean
        {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(
                    this
                    , arrayOf(Manifest.permission.CAMERA)
                    , 535
                )
                return false
            }
            return true
        }
    }
}