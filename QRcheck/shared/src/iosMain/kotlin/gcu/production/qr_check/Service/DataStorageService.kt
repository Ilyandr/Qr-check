package gcu.production.qr_check.Service

import platform.Foundation.NSUserDefaults
import platform.Foundation.setValue
import platform.Foundation.valueForKey

actual class DataStorageService
{
    private val basicDataController =
        NSUserDefaults.standardUserDefaults()

    actual fun actionWithAuth(dataID: String, newValue: String?): String? =
        newValue?.let {
            this.basicDataController
                .setValue(it, forKey = dataID)
            it
        } ?:
        this.basicDataController
            .valueForKey(dataID) as? String

    actual companion object
    {
        actual val LOGIN_ID: String = "login"
        actual val PASSWORD_ID: String = "password"
        actual val ROLE_ID: String = "role"
    }
}
