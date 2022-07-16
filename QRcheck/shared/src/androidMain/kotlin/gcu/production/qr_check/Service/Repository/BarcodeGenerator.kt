@file:Suppress("PackageName")
package gcu.production.qr_check.Service.Repository

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BarcodeGenerator
{
    @JvmStatic
    suspend inline infix fun <reified T> Pair<Int, Int>
            .setCardBarcode(barcodeData: String?): T? =
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
        } as T?
}