package gcu.production.qrcheck.RestAPI.AppConfiguration

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

fun Configuration.configurationModule(): DI.Module =
    DI.Module(
        name = "ConfigurationModule"
        , init = {
            bind<Configuration>() with singleton {
                this@configurationModule }
        })