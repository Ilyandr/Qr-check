package gcu.production.qrcheck.RestAPI.Features.RestInteraction

import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.DataSourse.RestAuthDataSource
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.DataSourse.RestPointDataSource
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.KtorAPI.KtorRestAuthDataSource
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.KtorAPI.KtorRestPointDataSource
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.KtorAPI.KtorRestRecordDataSource
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.Repository.RestAuthRepository
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.Repository.RestPointRepository
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.Repository.RestRecordRepository
import kotlinx.serialization.ExperimentalSerializationApi
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import kotlin.native.concurrent.ThreadLocal

@ExperimentalSerializationApi
internal val restModule =
    DI.Module(
        name = "RestModule"
        , init = {
            bind<RestAuthDataSource>() with singleton {
                KtorRestAuthDataSource(
                    httpClient = instance())
            }
            bind<RestAuthRepository>() with singleton {
                RestAuthRepository(
                    restAuthDataSource = instance())
            }

            bind<RestPointDataSource>() with singleton {
                KtorRestPointDataSource(
                    httpClient = instance())
            }

            bind<RestPointRepository>() with singleton {
                RestPointRepository(
                    restPointDataSource = instance())
            }


            bind<KtorRestRecordDataSource>() with singleton {
                KtorRestRecordDataSource(
                    httpClient = instance())
            }

            bind<RestRecordRepository>() with singleton {
                RestRecordRepository(
                    restRecordDataSource = instance())
            }
        })

@ThreadLocal
object RestModule
{
    val restAuthRepository: RestAuthRepository
        get() = EngineSDK.di.instance()

    val restPointRepository: RestPointRepository
        get() = EngineSDK.di.instance()

    val restRecordRepository: RestRecordRepository
        get() = EngineSDK.di.instance()
}

val EngineSDK.restAPI: RestModule
    get() = RestModule