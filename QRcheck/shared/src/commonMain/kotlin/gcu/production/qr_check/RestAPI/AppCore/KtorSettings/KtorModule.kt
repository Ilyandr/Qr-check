package gcu.production.qrcheck.RestAPI.AppCore.KtorSettings

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val ktorModule =
    DI.Module(
        name = "KtorModule"
        , init = {
            bind<HttpEngineFactory>() with singleton { HttpEngineFactory() }
            bind<HttpClient>() with singleton {
                HttpClient(
                    instance<HttpEngineFactory>().createEngine())
                {
                    install(Logging)
                    {
                        level = LogLevel.ALL
                        logger = Logger.SIMPLE
                    }

                    defaultRequest {
                        host = "78.40.217.59:9630"
                        url {
                            protocol = URLProtocol.HTTP
                        }
                    }
                }
            }
        })