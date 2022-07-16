package gcu.production.qr_check.Service.Repository

actual class DataStorageService actual constructor()
{
    private val basicDataController =
        NSUserDefaults.standardUserDefaults()

    actual fun actionWithAuth(
        dataID: String
        , newValue: String?): String? =
        newValue?.let {
            if (it.isEmpty())
                this.basicDataController
                    .valueForKey(
                        dataID) as? String
            else
            {
                this.basicDataController
                    .setValue(it, forKey = dataID)
                it
            }
        } ?:
        this.basicDataController
            .valueForKey(
                dataID) as? String

    actual fun anyAction(
        dataID: String
        , newValue: Boolean?): Boolean? =
        newValue?.let {
                this.basicDataController
                    .setValue(it, forKey = dataID)
                it
        } ?:
        this.basicDataController
            .valueForKey(
                dataID) as? Boolean

    actual fun removeAllData(): Boolean = false

    actual companion object
    {
        actual val LOGIN_ID: String = "login"
        actual val PASSWORD_ID: String = "password"
        actual val ROLE_ID: String = "role"
        actual val THEME_APP = "theme"
    }
}