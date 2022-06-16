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
        newValue.let {
            this.sharedPreferences
                .edit()
                .putString(dataID, it)
                .apply()
            return@let it
        } ?: this.sharedPreferences.getString(dataID, null)

    actual companion object
    {
        actual const val LOGIN_ID = "login"
        actual const val PASSWORD_ID = "password"
        actual const val ROLE_ID = "role"
    }
}