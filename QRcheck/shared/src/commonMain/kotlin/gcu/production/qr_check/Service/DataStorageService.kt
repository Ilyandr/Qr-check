package gcu.production.qr_check.Service

expect class DataStorageService
{
    fun actionWithAuth(
        dataID: String
        , newValue: String?): String?

    companion object
    {
        val LOGIN_ID: String
        val PASSWORD_ID: String
        val ROLE_ID: String
    }
}