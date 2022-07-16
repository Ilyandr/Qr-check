@file:Suppress("PackageName")
package gcu.production.qr_check.Repository.Features.Detectors

import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import kotlinx.coroutines.*

internal open class BarcodeProcessor(
    private val actionDetected: (barcodeData: String) -> Unit)
    : Detector.Processor<Barcode?>
{
    override fun receiveDetections(detections: Detector.Detections<Barcode?>)
    {
        try
        {
            (detections.detectedItems)
                .valueAt(0)
                ?.let {
                    MainScope().launch(Dispatchers.Main)
                    {
                        actionDetected(it.rawValue)
                    }
                }
        } catch (ex: Exception) {}
    }

    override fun release() {}
}