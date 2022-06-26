package gcu.production.qr_check.Service

import android.content.Context
import android.content.SharedPreferences

actual class DataStorageService(context: Context)
{
    private val sharedPreferences: SharedPreferences

    init
    {
        this.sharedPreferences =
            context.getSharedPreferences(
                null, Context.MODE_PRIVATE)
    }

    actual fun actionWithAuth(
        dataID: String
        , newValue: String?): String? =
        newValue?.let {
            this.sharedPreferences
                .edit()
                .putString(dataID, it)
                .apply()
            return@let it
        } ?: this.sharedPreferences.getString(dataID, null)

    actual fun anyAction(
        dataID: String
        , newValue: Boolean?): Boolean? =
        newValue?.let {
            this.sharedPreferences
                .edit()
                .putBoolean(dataID, it)
                .apply()
            return@let it
        } ?: this.sharedPreferences.getBoolean(dataID, false)

    actual fun removeAllData(): Boolean =
        this.sharedPreferences
            .edit()
            .clear()
            .commit()

    actual companion object
    {
        actual val LOGIN_ID = "login"
        actual val PASSWORD_ID = "password"
        actual val ROLE_ID = "role"
        actual val THEME_APP = "theme"
    }
}