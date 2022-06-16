package gcu.production.qr_check.Service

expect class GeolocationListener
{
    fun launch(actionForFault: (() -> Unit)? = null)
}