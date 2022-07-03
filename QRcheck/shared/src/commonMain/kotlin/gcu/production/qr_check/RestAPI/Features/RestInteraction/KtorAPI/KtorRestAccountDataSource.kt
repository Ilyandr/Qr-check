package gcu.production.qr_check.RestAPI.Features.RestInteraction.KtorAPI

import gcu.production.qr_check.RestAPI.Features.RestInteraction.DataSourse.AccountDataSourceActions
import gcu.production.qr_check.RestAPI.Features.RestInteraction.DataSourse.RestAccountDataSource
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class KtorRestAccountDataSource(
    private val httpClient: HttpClient
): RestAccountDataSource
{
    override suspend fun changeAccountSingleSetting(
        accountDataSourceActions: AccountDataSourceActions,
        userLoginKey: String?,
        newParameterData: String
    ) = try
    {
        if (accountDataSourceActions == AccountDataSourceActions.deleteAccount)
            this.httpClient.delete<HttpStatement>
            {
                url {
                    path(accountDataSourceActions.getCurrentPath())
                    header(
                        HttpHeaders.Authorization
                        , value = "Basic $userLoginKey"
                    )
                    body = newParameterData
                }
            }.execute().readText().toBoolean()
        else
            this.httpClient.post<HttpStatement>
            {
                url {
                    path(accountDataSourceActions.getCurrentPath())
                    header(
                        HttpHeaders.Authorization
                        , value = "Basic $userLoginKey"
                    )

                    if (accountDataSourceActions == AccountDataSourceActions.changePassword)
                        body = newParameterData
                    else if (accountDataSourceActions == AccountDataSourceActions.changePhone
                        || accountDataSourceActions == AccountDataSourceActions.changeOrganization)
                        parameter(
                            accountDataSourceActions.getCurrentParameter()
                            , newParameterData
                        )
                }
        }.execute().readText().toBoolean()
    } catch (ex: Exception) { false }

    private fun AccountDataSourceActions.getCurrentPath() =
        when(this)
        {
            AccountDataSourceActions.deleteAccount -> "deleteAccount"
            AccountDataSourceActions.changePhone -> "changePhone"
            AccountDataSourceActions.changeOrganization -> "changeOrganization"
            AccountDataSourceActions.changePassword -> "changePassword"
        }

    private fun AccountDataSourceActions.getCurrentParameter() =
        when(this)
        {
            AccountDataSourceActions.deleteAccount -> "password"
            AccountDataSourceActions.changePhone -> "phone"
            AccountDataSourceActions.changeOrganization -> "organization"
            AccountDataSourceActions.changePassword -> "password"
        }
}