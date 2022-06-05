package gcu.production.qrcheck.RestAPI.AppCore

import gcu.production.qrcheck.RestAPI.AppCore.KtorSettings.ktorModule
import gcu.production.qrcheck.RestAPI.AppCore.SerializationSettings.serializationModule
import org.kodein.di.DI

val coreModule =
    DI.Module(
         init = {
           importAll(
               ktorModule
               , serializationModule)
        })