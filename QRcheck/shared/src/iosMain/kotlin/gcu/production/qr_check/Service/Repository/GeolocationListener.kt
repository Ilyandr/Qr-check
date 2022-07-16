package gcu.production.qr_check.Service.Repository

actual class GeolocationListener
{
    actual inline fun launch(
        additionalDataPoint: Pair<Int, Long?>?
        , additionalDataScan: String?
        , crossinline actionForFault: (() -> Unit)
        , noinline actionForSuccessPoint: actionSuccessCreatePointGeo?
        , noinline actionForSuccessScan: actionSuccessScanQRGeo?
    ) {}
}