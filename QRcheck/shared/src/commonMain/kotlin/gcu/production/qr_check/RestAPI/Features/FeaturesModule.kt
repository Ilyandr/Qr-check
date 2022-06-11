package gcu.production.qrcheck.RestAPI.Features

import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restModule
import org.kodein.di.DI

val featuresModule =
    DI.Module {
        importAll(restModule)
    }