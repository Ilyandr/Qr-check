@file:Suppress("PackageName")
package gcu.production.qr_check.Presentation.Main.Common

import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import gcu.production.qr_check.Domain.ViewFactories.CustomLoadingDialogFactory
import gcu.production.qr_check.Presentation.CustomViews.CustomLoadingDialog
import gcu.production.qr_check.Service.Repository.NetworkChangeReceiver
import gcu.production.qr_check.Service.Repository.NetworkConnection
import gcu.production.qr_check.android.R
import gcu.production.qr_check.getModuleAppComponent
import javax.inject.Inject

internal class BasicActivity : AppCompatActivity()
{
    @Inject
    lateinit var customLoadingDialogFactory: CustomLoadingDialogFactory

    private val customLoadingDialog: CustomLoadingDialog by lazy {
        customLoadingDialogFactory.create(this)
    }

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

        getModuleAppComponent().inject(this)
        startReceiver()
    }

    @Suppress("DEPRECATION")
    private fun startReceiver() =
        registerReceiver(
            NetworkChangeReceiver {
                NetworkConnection.checkingAccessWithActions(
                    actionSuccess = { customLoadingDialog.stopLoadingDialog() }
                    , actionFault = {
                        Toast.makeText(
                            this,
                            R.string.toastErrorNetwork,
                            Toast.LENGTH_LONG
                        ).show()
                        customLoadingDialog.startLoadingDialog()
                    }
                )
            }, IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION
            )
        )

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        this.navigationController.popBackStack()
        return super.onOptionsItemSelected(item)
    }

    companion object {
        internal const val DATA_SELECT_KEY = "selectData"
    }
}
