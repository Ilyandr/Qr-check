package gcu.production.qr_check.Service

actual class DataStorageService
{
    val x: NSUserDefault
    actual fun actionWithAuth(dataID: String, newValue: String?): String?
    {
        NSUserDefault
    }

    actual companion object
    {
        actual val LOGIN_ID: String = "login"
        actual val PASSWORD_ID: String = "password"
        actual val ROLE_ID: String = "role"
    }
}