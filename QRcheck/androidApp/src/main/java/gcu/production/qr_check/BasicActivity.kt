package gcu.production.qr_check

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.AppConfiguration.Configuration
import gcu.production.qr_check.android.R
import gcu.production.qrcheck.RestAPI.AppConfiguration.PlatformType

internal class BasicActivity : AppCompatActivity()
{
    private val navigationController: NavController by lazy {
        Navigation
            .findNavController(
                this
                , R.id.fragmentContainerView
            )
    }

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray)
    {
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults)

            Toast.makeText(
                this
                , if (grantResults.all { it == PERMISSION_GRANTED })
                    R.string.successPermissionInfo
                else
                    R.string.faultPermissionInfo
                , Toast.LENGTH_LONG)
                .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        this.navigationController.popBackStack()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
}
