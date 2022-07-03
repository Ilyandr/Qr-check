@file:Suppress("PackageName")
package gcu.production.qr_check.Main.Settings

import androidx.preference.Preference

internal interface SettingsServiceImpl
{
    fun launchSingleAction(
        preference: Preference
        ,  switchData: Boolean? = null
        , returnValue: Boolean = true): Boolean
}