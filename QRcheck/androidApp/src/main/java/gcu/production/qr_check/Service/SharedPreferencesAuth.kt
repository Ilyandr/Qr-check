package gcu.production.qrcheck.android.Service

import android.content.Context
import android.content.SharedPreferences

internal class SharedPreferencesAuth(context: Context)
{
    private val sharedPreferences: SharedPreferences

    init
    {
        this.sharedPreferences =
            context.getSharedPreferences(
                null, Context.MODE_PRIVATE)
    }

    internal fun actionWithAuth(
        dataID: String
        , newValue: String? = null): String? =
        newValue?.let {
            this.sharedPreferences
                .edit()
                .putString(dataID, it)
                .apply()
            return@let it
        } ?: this.sharedPreferences.getString(dataID, null)

    companion object
    {
        internal const val LOGIN_ID = "login"
        internal const val PASSWORD_ID = "password"
        internal const val ROLE_ID = "role"
    }
}