package gcu.production.qrcheck.android.Service

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object BarcodeGenerator
{
    @JvmStatic
    internal suspend inline infix fun Pair<Int, Int>
            .setCardBarcode(barcodeData: String?): Bitmap? =
        withContext(Dispatchers.IO)
        {
            BarcodeEncoder()
                .createBitmap(
                    MultiFormatWriter().encode(
                        barcodeData ?: return@withContext null
                        , BarcodeFormat.QR_CODE
                        , this@setCardBarcode.first
                        , this@setCardBarcode.second
                    )
                )
        }
}