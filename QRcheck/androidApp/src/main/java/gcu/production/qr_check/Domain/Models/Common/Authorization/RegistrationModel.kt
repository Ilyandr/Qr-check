@file:Suppress("PackageName")
package gcu.production.qr_check.Domain.Models.Common.Authorization

import androidx.annotation.StringRes
import gcu.production.qrcheck.RestAPI.Models.User.UserOutputEntity

internal sealed class RegistrationModel
{
    internal object DefaultState: RegistrationModel()

    internal data class FaultState(
        @StringRes val messageId: Int
    ): RegistrationModel()

    internal data class LoadingStageFirstState(
        val passwordData: String?
        ): RegistrationModel()

    internal data class LoadingSwitchingState(
        val passwordData: String
    ): RegistrationModel()

    internal data class LoadingStageSecondState(
        val allRegisterData: UserOutputEntity? = null
    ): RegistrationModel()

    internal data class SuccessState(
        @StringRes val navigateId: Int
    ): RegistrationModel()
}