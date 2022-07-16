package gcu.production.qr_check

import android.app.Application
import androidx.fragment.app.FragmentActivity
import gcu.production.qr_check.Repository.DI.Components.DaggerModulesComponent
import gcu.production.qr_check.Repository.DI.Components.ModulesComponent
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.AppConfiguration.Configuration
import gcu.production.qrcheck.RestAPI.AppConfiguration.PlatformType

internal class GeneralApplication: Application()
{
    lateinit var modulesComponent: ModulesComponent

    override fun onCreate()
    {
        super.onCreate()

        EngineSDK.init(
            configuration = Configuration(
                platformType = PlatformType.Android(
                    versionPlatform = "R-1.1.2", buildNumber = "2"
                )
            )
        )

        this.modulesComponent =
            DaggerModulesComponent
                .builder()
                .contextApplication(this)
                .build()
    }
}

internal fun FragmentActivity.getModuleAppComponent() =
    (this.application as GeneralApplication)
        .modulesComponent