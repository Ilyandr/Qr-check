@file:Suppress("PackageName")
package gcu.production.qr_check.Domain.Models.Common.Authorization

import android.os.Bundle
import androidx.annotation.StringRes

internal sealed class CommonAuthorizationModel
{
    internal object DefaultState: CommonAuthorizationModel()

    internal data class LoadingStateAuth(
        val loginData: String?
        ): CommonAuthorizationModel()

    internal data class LoadingStateConfirm(
        val loginData: String
        , val passwordData: String?
    ): CommonAuthorizationModel()

    internal data class SuccessState(
        @StringRes val navigateId: Int
        , val bundleData: Bundle? = null
    ): CommonAuthorizationModel()

    internal data class FaultState(
        @StringRes val messageId: Int
    ): CommonAuthorizationModel()
}