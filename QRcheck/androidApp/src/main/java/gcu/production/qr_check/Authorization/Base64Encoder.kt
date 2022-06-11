package gcu.production.qrcheck.android.Authorization

import android.annotation.SuppressLint
import gcu.production.qrcheck.android.Service.SharedPreferencesAuth
import java.nio.charset.StandardCharsets
import java.util.*

internal object Base64Encoder
{
    @SuppressLint("NewApi")
    @JvmStatic
    internal fun SharedPreferencesAuth.encodeAuthDataToBase64Key(): String =
            Base64.getEncoder().encodeToString(
                ("${this.actionWithAuth(
                    SharedPreferencesAuth.LOGIN_ID)}" +
                        ":${this.actionWithAuth(
                            SharedPreferencesAuth.PASSWORD_ID)}")
                    .toByteArray(StandardCharsets.UTF_8))
}