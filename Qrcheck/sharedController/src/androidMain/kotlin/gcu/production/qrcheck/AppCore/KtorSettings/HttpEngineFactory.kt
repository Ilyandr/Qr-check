package gcu.production.qrcheck.AppCore.KtorSettings

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*

actual class HttpEngineFactory
{
    actual fun createEngine():
            HttpClientEngineFactory<HttpClientEngineConfig> = OkHttp
}