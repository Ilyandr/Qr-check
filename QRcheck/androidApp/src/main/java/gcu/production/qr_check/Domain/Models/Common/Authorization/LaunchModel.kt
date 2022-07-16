@file:Suppress("PackageName")
package gcu.production.qr_check.Domain.Models.Common.Authorization

import androidx.annotation.StringRes
import gcu.production.qr_check.Service.Repository.DataStorageService

internal sealed class LaunchModel
{
    internal object LoadingState: LaunchModel()

    internal object SwitchSuccessState: LaunchModel()

    internal data class SuccessState(
        val dataStorageService: DataStorageService? = null
        , @StringRes val navigationId: Int? = null
        ): LaunchModel()

    internal data class FaultState(
        @StringRes val messageId: Int
        ): LaunchModel()
}