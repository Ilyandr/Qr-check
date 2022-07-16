@file:Suppress("PackageName")
package gcu.production.qr_check.Domain.Models.Admin

import gcu.production.qrcheck.RestAPI.Models.Point.DataPointInputEntity

sealed class BaseAdminModel
{
    internal object ActionsSuccessfully: BaseAdminModel()

    internal object DefaultData: BaseAdminModel()

    internal object LoadingDataForList: BaseAdminModel()

    internal object FaultInvalidData : BaseAdminModel()

    internal data class SuccessfulDataForList(
        val data: List<DataPointInputEntity>
    ): BaseAdminModel()

    internal data class LoadingCreatePoint(
        val X: Int
        , val Y: Long?
        ): BaseAdminModel()

    internal object FaultCreatePoint: BaseAdminModel()

    internal object SuccessfulCreatePoint: BaseAdminModel()
}