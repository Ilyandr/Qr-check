@file:Suppress("PackageName")
package gcu.production.qr_check.Domain.Models.Admin

internal sealed class ShowQRModel
{
    internal data class LoadingState(
        val selectKeyId: Long
        ): ShowQRModel()

    internal data class SuccessState(
        val barcodeData: String
        ):ShowQRModel()

    internal object FaultState: ShowQRModel()

    internal object DefaultState: ShowQRModel()
}