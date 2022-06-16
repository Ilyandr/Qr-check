package gcu.production.qr_check.Service

expect object BarcodeGenerator
{
    internal suspend inline infix fun <reified T> Pair<Int, Int>
            .setCardBarcode(barcodeData: String?): T?
}