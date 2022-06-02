package gcu.production.qrcheck.AppCore

import gcu.production.qrcheck.AppCore.KtorSettings.ktorModule
import gcu.production.qrcheck.AppCore.SerializationSettings.serializationModule
import org.kodein.di.DI

val coreModule =
    DI.Module(
         init = {
           importAll(
               ktorModule
               , serializationModule)
        })