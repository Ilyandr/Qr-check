@file:Suppress("PackageName")
package gcu.production.qr_check.Domain.Models.Admin

import gcu.production.qrcheck.RestAPI.Models.User.UserInputEntity

internal sealed class RecordsModel
{
    internal object DefaultState: RecordsModel()

    internal object FaultState: RecordsModel()

    internal data class LoadingState(
        val selectId: Long
        ): RecordsModel()

    internal data class SuccessfulState(
        val dataList: List<UserInputEntity>
        ): RecordsModel()
}