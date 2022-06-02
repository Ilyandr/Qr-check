package gcu.production.qrcheck.Features.RestInteraction.KtorAPI

import gcu.production.qrcheck.Features.RestInteraction.DataSourse.RestDataSource
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

class KtorRestDataSource(
    private val httpClient: HttpClient
    , val sendJsonObject: Json): RestDataSource
{
    override suspend fun fetchNews() =
        this.httpClient.get<HttpStatement>
        {
            url {
                path("/get")
            }
        }.execute().readText()
}