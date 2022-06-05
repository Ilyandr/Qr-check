package gcu.production.qrcheck.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import gcu.production.qrcheck.RestAPI.AppConfiguration.Configuration
import gcu.production.qrcheck.RestAPI.AppConfiguration.PlatformType
import gcu.production.qrcheck.AppEngine.EngineSDK

internal class BasicActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)

        EngineSDK.init(
            configuration = Configuration(
                platformType = PlatformType.Android(
                    versionPlatform = "1.0", buildNumber = "1"))
        )
    }
}
