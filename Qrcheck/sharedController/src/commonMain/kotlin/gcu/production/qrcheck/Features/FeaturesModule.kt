package gcu.production.qrcheck.Features

import gcu.production.qrcheck.Features.RestInteraction.restModule
import org.kodein.di.DI

val featuresModule =
    DI.Module {
        importAll(restModule)
    }