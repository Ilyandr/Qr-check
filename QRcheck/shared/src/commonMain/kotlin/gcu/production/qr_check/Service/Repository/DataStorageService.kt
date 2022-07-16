package gcu.production.qr_check.Service.Repository

expect class DataStorageService()
{
    fun actionWithAuth(
        dataID: String
        , newValue: String?): String?

    fun anyAction(
        dataID: String
        , newValue: Boolean?): Boolean?

    fun removeAllData(): Boolean

    companion object
    {
        val LOGIN_ID: String
        val PASSWORD_ID: String
        val ROLE_ID: String
        val THEME_APP: String
    }
}