@file:Suppress("PackageName")
package gcu.production.qr_check.Domain.Models.Common

import android.content.Context
import androidx.annotation.StringRes

internal sealed class SettingsModel
{
    internal object LoadingState : SettingsModel()

    internal object DefaultState : SettingsModel()

    internal data class ExitState(
        val context: Context?
    ) : SettingsModel()

    internal data class ThemeState(
        val switchData: Boolean
    ) : SettingsModel()

    internal data class LoginState(
        val context: Context
        ) : SettingsModel()

    internal data class PasswordState(
        val context: Context
        ) : SettingsModel()

    internal data class OrganizationState(
        val context: Context
        ) : SettingsModel()

    internal data class RemoveAccountState(
        val context: Context?
        ) : SettingsModel()

    internal data class MessageChangeState(
        @StringRes val faultMessageId: Int
    ) : SettingsModel()
}