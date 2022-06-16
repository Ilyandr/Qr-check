package gcu.production.qr_check.Service

actual object BarcodeGenerator
{
    internal actual suspend inline infix fun <reified T> Pair<Int, Int>.
            setCardBarcode(barcodeData: String?): T? = null
}