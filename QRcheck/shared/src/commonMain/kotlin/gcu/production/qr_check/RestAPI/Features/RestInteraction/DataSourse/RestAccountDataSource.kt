package gcu.production.qr_check.RestAPI.Features.RestInteraction.DataSourse

interface RestAccountDataSource
{
    suspend fun changeAccountSingleSetting(
        accountDataSourceActions: AccountDataSourceActions
        , userLoginKey: String?
        , newParameterData: String): Boolean
}

enum class AccountDataSourceActions
{
    changePhone
    , changeOrganization
    , deleteAccount
    , changePassword
}