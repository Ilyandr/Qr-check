package gcu.production.qrcheck.android

import gcu.production.qrcheck.Features.RestInteraction.rest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import gcu.production.qrcheck.AppConfiguration.Configuration
import gcu.production.qrcheck.AppConfiguration.PlatformType
import gcu.production.qrcheck.AppEngine.EngineSDK
import kotlinx.coroutines.*

internal class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        EngineSDK.init(
            configuration = Configuration(
                platformType = PlatformType.Android(
                    versionPlatform = "1.0", buildNumber = "1"))
        )

      val test: Deferred<String> = GlobalScope.async(Dispatchers.IO) {
               EngineSDK.rest.restRepository.fetchNews()
       }

        GlobalScope.launch(Dispatchers.Main) {
            findViewById<TextView>(R.id.text_view).text = test.await()
        }
    }
}
