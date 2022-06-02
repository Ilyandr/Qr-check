package gcu.production.qrcheck.AppEngine

import gcu.production.qrcheck.AppConfiguration.Configuration
import gcu.production.qrcheck.AppConfiguration.configurationModule
import gcu.production.qrcheck.AppCore.KtorSettings.ktorModule
import gcu.production.qrcheck.AppCore.coreModule
import gcu.production.qrcheck.Features.RestInteraction.restModule
import gcu.production.qrcheck.Features.featuresModule
import org.kodein.di.*
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object EngineSDK
{
    internal val di: DirectDI
        get() = requireNotNull(directDI)
    private var directDI: DirectDI? = null

    fun init(configuration: Configuration)
    {
        if (this.directDI != null)
            this.directDI = null

        this.directDI = DI {
            importAll(
                configuration.configurationModule(),
                engineModule
                , featuresModule
                , coreModule
            )
        }.direct
    }
}