package gcu.production.qrcheck.Features.RestInteraction

import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.Features.RestInteraction.DataSourse.RestDataSource
import gcu.production.qrcheck.Features.RestInteraction.KtorAPI.KtorRestDataSource
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import kotlin.native.concurrent.ThreadLocal

internal val restModule =
    DI.Module(
        name = "RestModule"
        , init = {
            bind<RestDataSource>() with singleton {
                KtorRestDataSource(
                    httpClient = instance()
                    , sendJsonObject = instance())
            }
            bind<RestRepository>() with singleton {
                RestRepository(
                    restDataSource = instance())
            }
        })

@ThreadLocal
object RestModule
{
    val restRepository: RestRepository
        get() = EngineSDK.di.instance()
}

val EngineSDK.rest: RestModule
    get() = RestModule