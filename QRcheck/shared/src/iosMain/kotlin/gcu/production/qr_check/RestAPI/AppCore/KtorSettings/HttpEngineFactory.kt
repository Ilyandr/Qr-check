package gcu.production.qrcheck.RestAPI.AppCore.KtorSettings

import io.ktor.client.engine.*
import io.ktor.client.engine.ios.*

actual class HttpEngineFactory actual constructor() {
    actual fun createEngine(): HttpClientEngineFactory<HttpClientEngineConfig> = Ios
}