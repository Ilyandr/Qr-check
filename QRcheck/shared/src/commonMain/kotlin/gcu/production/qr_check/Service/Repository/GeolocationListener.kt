package gcu.production.qr_check.Service.Repository

typealias actionSuccessCreatePointGeo = (
    X: Double
    , Y: Double
    , additionalData: Pair<Int, Long?>
) -> Unit

typealias actionSuccessScanQRGeo = (
    X: Double
    , Y: Double
    , additionalDataScan: String
) -> Unit

expect class GeolocationListener()
{
    inline fun launch(
        additionalDataPoint: Pair<Int, Long?>? = null
        , additionalDataScan: String? = null
        , crossinline actionForFault: (() -> Unit)
        , noinline actionForSuccessPoint: actionSuccessCreatePointGeo? = null
        , noinline actionForSuccessScan: actionSuccessScanQRGeo? = null
    )
}