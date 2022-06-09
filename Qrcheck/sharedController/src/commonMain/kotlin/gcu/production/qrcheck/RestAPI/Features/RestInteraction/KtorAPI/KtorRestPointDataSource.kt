package gcu.production.qrcheck.RestAPI.Features.RestInteraction.KtorAPI

import gcu.production.qrcheck.RestAPI.Features.RestInteraction.DataSourse.RestPointDataSource
import gcu.production.qrcheck.RestAPI.Models.Point.DataPointInputEntity
import gcu.production.qrcheck.RestAPI.Models.Point.DataPointOutputEntity
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ExperimentalSerializationApi
class KtorRestPointDataSource(
    private val httpClient: HttpClient): RestPointDataSource
{
    override suspend fun generatePoint(
        userLoginKey: String?,
        dataPointOutputEntity: DataPointOutputEntity
    ): DataPointInputEntity =
        Json.decodeFromString(this.httpClient.post<HttpStatement>
        {
            url {
                path("generatedPoint")
                body = Json.encodeToString(dataPointOutputEntity)

                headers {
                    header(key = "Content-Type", value = "application/json")
                    header(
                        HttpHeaders.Authorization
                        , value = "Basic $userLoginKey")
                }
            }
        }.execute().readText())

    override suspend fun generateToken(
        userLoginKey: String?, pointID: Long): String? =
        try
        {
            this.httpClient.get<HttpStatement>
            {
                url {
                    path("generateToken")
                    header(
                        HttpHeaders.Authorization
                        , value = "Basic $userLoginKey")
                    parameter("pointId", pointID)
                }
            }.execute().readText()
        } catch (ex: Exception) { null }

    override suspend fun getAllPoint(
        userLoginKey: String?): MutableList<DataPointInputEntity> =
        try
        {
            Json.decodeFromString(
                ListSerializer(
                    DataPointInputEntity.serializer())
                ,  this.httpClient.get<HttpStatement>
                {
                    url {
                        path("getAllPoint")
                        header(
                            HttpHeaders.Authorization
                            , value = "Basic $userLoginKey")
                    }
                }.execute()
                    .readText())
        } catch (ex: Exception) { emptyList() }
                as MutableList<DataPointInputEntity>
}