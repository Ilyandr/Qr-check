package gcu.production.qrcheck.RestAPI.Features.RestInteraction.KtorAPI

import gcu.production.qrcheck.RestAPI.Features.RestInteraction.DataSourse.RestAuthDataSource
import gcu.production.qrcheck.RestAPI.Models.User.UserInputEntity
import gcu.production.qrcheck.RestAPI.Models.User.UserOutputEntity
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@ExperimentalSerializationApi
class KtorRestAuthDataSource(private val httpClient: HttpClient): RestAuthDataSource
{
    override suspend fun registration(
        userOutputEntity: UserOutputEntity
    ): UserInputEntity? = try
    {
        Json.decodeFromString(this.httpClient.post<HttpStatement>
        {
            url {
                path("registration")
                body = Json.encodeToString(userOutputEntity)
                header(key = "Content-Type", value = "application/json")
            }
        }.execute().readText())
    } catch (ex: Exception) { null }

    override suspend fun login(
        userLoginKey: String?): UserInputEntity? = try
        {
            this.httpClient.get<HttpStatement>
            {
                url {
                    path("login")
                    header(
                        HttpHeaders.Authorization
                        , value = "Basic $userLoginKey")
                }
            }.execute().let {
                if (it.status == HttpStatusCode.OK)
                    Json.decodeFromString(
                        it.readText()) as UserInputEntity
                else null
            }
        } catch (ex: Exception) { null }


    override suspend fun existUser(userLoginData: String?) = try
    {
        Json.decodeFromString(this.httpClient.get<HttpStatement>
        {
            url {
                parameter("phone", userLoginData)
                path("existUser")
            }
        }.execute().readText()) as Boolean
    } catch (ex: Exception) { null }
}