package gcu.production.qrcheck.RestAPI.Features.RestInteraction.KtorAPI

import gcu.production.qrcheck.RestAPI.Features.RestInteraction.DataSourse.RestRecordDataSource
import gcu.production.qrcheck.RestAPI.Models.User.UserInputEntity
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class KtorRestRecordDataSource(
    private val httpClient: HttpClient): RestRecordDataSource
{
    override suspend fun setRecord(
        userAuthKey: String?,
        dataToken: String?,
        userLocation: Pair<Double, Double>): Long? = try
        {
            this.httpClient.post<HttpStatement>
            {
                url {
                    path("setRecord")
                    header(
                        HttpHeaders.Authorization
                        , value = "Basic $userAuthKey")

                    parameter("token", dataToken)
                    parameter("x", userLocation.first)
                    parameter("y", userLocation.second)
                }
            }.execute().readText().toLongOrNull()
        } catch (ex: Exception) { null }

    override suspend fun getAllRecord(
        userAuthKey: String?, pointId: Long): MutableList<UserInputEntity> =
        try
        {
            Json.decodeFromString(
                ListSerializer(
                    UserInputEntity.serializer())
                ,  this.httpClient.get<HttpStatement>
                {
                    url {
                        path("getAllRecord")
                        header(
                            HttpHeaders.Authorization
                            , value = "Basic $userAuthKey")
                        parameter("pointId", pointId)
                    }
                }.execute()
                    .readText())
                .distinctBy { it.id } as MutableList<UserInputEntity>
        } catch (ex: Exception) { mutableListOf() }
}