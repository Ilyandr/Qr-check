package gcu.production.qrcheck.AppCore.KtorSettings

import io.ktor.client.engine.*

expect class HttpEngineFactory constructor()
{
    fun createEngine(): HttpClientEngineFactory<HttpClientEngineConfig>
}