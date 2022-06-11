package gcu.production.qrcheck.android.Service.Detectors

import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@DelicateCoroutinesApi
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
                    GlobalScope.launch(Dispatchers.Main)
                    {
                        actionDetected(it.rawValue)
                    }
                }
        } catch (ex: Exception) {}
    }

    override fun release() {}
}